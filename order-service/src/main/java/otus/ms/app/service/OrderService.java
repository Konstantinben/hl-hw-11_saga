package otus.ms.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import otus.ms.app.client.BillingServiceClient;
import otus.ms.app.client.DeliveryServiceClient;
import otus.ms.app.model.dto.*;
import otus.ms.app.model.entity.AuthUser;
import otus.ms.app.model.entity.Order;
import otus.ms.app.model.entity.OrderItem;
import otus.ms.app.model.entity.WarehouseItem;
import otus.ms.app.model.exception.BadRequestException;
import otus.ms.app.model.exception.ETagException;
import otus.ms.app.model.mapper.OrderItemMapper;
import otus.ms.app.model.mapper.OrderMapper;
import otus.ms.app.repository.OrderItemRepository;
import otus.ms.app.repository.OrderRepository;
import otus.ms.app.repository.WarehouseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;

    private final WarehouseRepository warehouseRepository;

    private final OrderItemMapper orderItemMapper;

    private final OrderMapper orderMapper;

    private final DeliveryServiceClient deliveryServiceClient;

    private final BillingServiceClient billingServiceClient;

    @Transactional
    public Order createOrder(CreateOrderDto orderDto, String warehouseHash, AuthUser authUser, Map<String, String> headers, AtomicReference<Supplier<CompletableFuture<Void>>> rollbackActionHolder) {
        removeContentLength(headers);
        List<WarehouseItem> warehouseItems = warehouseRepository.findAllByOrderByItemUuid();
        if (StringUtils.isBlank(warehouseHash) || warehouseItems.hashCode() != Integer.valueOf(warehouseHash).intValue()) {
            throw new ETagException("Cannot create an order. Warehouse state changed. Please renew");
        }
        Map<UUID, WarehouseItem> itemsByUuid = warehouseItems.stream().collect(
                Collectors.toMap(WarehouseItem::getItemUuid, Function.identity()));
        List<WarehouseItem> warehouseItemsUpdateList = new ArrayList<>();
        List<OrderItem> orderItemList = new ArrayList<>();

        Order order = orderMapper.toEntity(orderDto);
        order.setUserUuid(authUser.getUuid());
        order.setOrderUuid(UUID.randomUUID());
        order.setEtag(Integer.valueOf(warehouseHash));
        order.setConfirmed(true);
        orderRepository.save(order); // insert and get orderId

        for (OrderItemDto orderItemDto : orderDto.getItems()) {
            WarehouseItem warehouseItem = itemsByUuid.get(orderItemDto.getItemUuid());
            if (warehouseItem.getItemQuantity() < orderItemDto.getQuantity()) {
                throw new BadRequestException("Ordered quantity is more than exists for item [" + warehouseItem.getItemName() + "]; UUID: " + orderItemDto.getItemUuid());
            }
            if (orderItemDto.getQuantity() <= 0) {
                throw new BadRequestException("Ordered quantity must be more than zero for item [" + warehouseItem.getItemName() + "]; UUID: " + orderItemDto.getItemUuid());
            }
            warehouseItem.setItemQuantity(warehouseItem.getItemQuantity() - orderItemDto.getQuantity());
            warehouseItemsUpdateList.add(warehouseItem);

            OrderItem orderItem = orderItemMapper.toEntity(orderItemDto);
            orderItem.setItemId(warehouseItem.getItemId());
            orderItem.setOrderId(order.getOrderId());
            orderItem.setPrice(warehouseItem.getPrice());
            orderItemList.add(orderItem);
            orderItemRepository.saveOrderItem(orderItem.getOrderId(), orderItem.getItemId(), orderItem.getItemUuid(), orderItem.getQuantity(), orderItem.getPrice());
            order.setTotalPrice(order.getTotalPrice() + orderItem.getQuantity()*orderItem.getPrice());
        }
        orderRepository.save(order); // update with calculated price

        Supplier<CompletableFuture<PayOrderDto>> rollbackpaymentFuture = rollbackPayment(order.getOrderUuid(), headers);
        Supplier<CompletableFuture<DeliveryDto>> rollbackDeliveryFuture = rejectDelivery(order.getOrderUuid(), headers);

        Supplier<CompletableFuture<Void>> rollback = () -> CompletableFuture.allOf(rollbackpaymentFuture.get(), rollbackDeliveryFuture.get());
        rollbackActionHolder.set(rollback);

        CompletableFuture<PayOrderDto> payOrderFuture = payOrder(order, headers);
        CompletableFuture<DeliveryDto> deliveryFuture = requestDelivery(order, orderDto.getDelivery(), headers);

        CompletableFuture saga = CompletableFuture.allOf(payOrderFuture, deliveryFuture)
                .thenAccept(__ -> {
                    PayOrderStatus payOrderStatus = payOrderFuture.join().getOrderStatus();
                    DeliveryStatus deliveryStatus = deliveryFuture.join().getDeliveryStatus();
                    if (PayOrderStatus.PAID == payOrderStatus && DeliveryStatus.CONFIRMED == deliveryStatus) {
                        warehouseRepository.saveAll(warehouseItemsUpdateList);
                    } else {
                        log.error("Order {} rejected. Inappropriate status. Payment Status {}. Delivery Status {}.",
                                order.getOrderUuid(), payOrderStatus, deliveryStatus);
                        order.setConfirmed(false);
                        rollback.get().join();
                    }
                })
                .exceptionally(error -> {
                    log.error("Order" + order.getOrderUuid() + "rejected due to error.", error);
                    order.setConfirmed(false);
                    return rollback.get().join();
                });

        saga.join();

        orderRepository.save(order);

        return order;
    }

    private CompletableFuture<PayOrderDto> payOrder(Order order, Map<String, String> headers) {
        PayOrderDto payOrderDto = PayOrderDto.builder()
                .orderUuid(order.getOrderUuid())
                .userUuid(order.getUserUuid())
                .totalPrice(order.getTotalPrice())
                .build();
        return CompletableFuture
                .supplyAsync(() -> billingServiceClient.payOrder(payOrderDto, headers))
                .exceptionally(ex -> {
                    log.error("Billing service payment error for {}", order, ex);
                    payOrderDto.setOrderStatus(PayOrderStatus.REJECTED);
                    return payOrderDto;
                });
    }

    private CompletableFuture<DeliveryDto> requestDelivery(Order order, DeliveryDto deliveryDto, Map<String, String> headers) {
        deliveryDto.setOrderUuid(order.getOrderUuid());
        deliveryDto.setUserUuid(order.getUserUuid());
        return CompletableFuture
                .supplyAsync(() -> deliveryServiceClient.createDelivery(deliveryDto, headers))
                .exceptionally(ex -> {
                    log.error("Delivery request error for {}", order, ex);
                    deliveryDto.setDeliveryStatus(DeliveryStatus.REJECTED);
                    return deliveryDto;
                });
    }

    private Supplier<CompletableFuture<DeliveryDto>> rejectDelivery(UUID orderUuid, Map<String, String> headers) {
        return () -> CompletableFuture.supplyAsync(() -> {
            try {
                return deliveryServiceClient.rejectDelivery(orderUuid, headers);
            } catch (Exception e) {
                log.error("Delivery rollback error for {}", orderUuid, e);
            }
            return null;
        });
    }

    private Supplier<CompletableFuture<PayOrderDto>> rollbackPayment(UUID orderUuid, Map<String, String> headers) {
        return () -> CompletableFuture.supplyAsync(() -> {
            try {
                return billingServiceClient.rollbackOrder(orderUuid, headers);
            } catch (Exception e) {
                log.error("Payment rollback error for {}", orderUuid, e);
            }
            return null;
        });
    }
/*

    private Supplier<CompletableFuture<Order>> rollbackOrder(Order order) {
        return () -> new CompletableFuture().thenApply(__ -> {
            order.setConfirmed(false);
            try {
                orderRepository.save(order);
            } catch (Exception e) {
                log.error("Order rollback error. {}", order, e);
            }
            return order;
        });
    }*/


    public Order getOrder(UUID orderUuid) {
        return orderRepository.getOrderByOrderUuid(orderUuid).orElse(null);
    }

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.getByOrderId(orderId);
    }

    public Order findExistingOrder(String warehouseHash, CreateOrderDto createOrderDto, AuthUser authUser) {
        Order order = orderRepository.getOrderByUserUuidAndEtagAndConfirmedIsTrue(authUser.getUuid(), Integer.valueOf(warehouseHash)).orElse(null);
        if (order != null) {
            List<OrderItem> orderItemList = orderItemRepository.getByOrderId(order.getOrderId());
            List<OrderItemDto> orderItemDtoList = orderItemMapper.toDtoList(orderItemList);
            if (CollectionUtils.isEqualCollection(orderItemDtoList, createOrderDto.getItems())) {
                return order;
            }
        }
        return null;
    }

    private static void removeContentLength(Map<String, String> headers) {
        String contentLengthKey = headers.keySet()
                .stream()
                .filter(key -> key.equalsIgnoreCase("content-length"))
                .findFirst().orElse(null);
        if (contentLengthKey != null) {
            headers.remove(contentLengthKey);
        }
    }
}
