package otus.ms.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import otus.ms.app.model.dto.CreateOrderDto;
import otus.ms.app.model.dto.OrderDto;
import otus.ms.app.model.entity.AuthUser;
import otus.ms.app.model.entity.Order;
import otus.ms.app.model.exception.AccessForbiddenException;
import otus.ms.app.model.exception.BadRequestException;
import otus.ms.app.model.exception.ETagException;
import otus.ms.app.model.mapper.OrderItemMapper;
import otus.ms.app.model.mapper.OrderMapper;
import otus.ms.app.repository.WarehouseRepository;
import otus.ms.app.security.UserSessionUtil;
import otus.ms.app.service.OrderService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Tag(name = "OrderController")
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Slf4j
@Validated
public class OrderController {


    private final WarehouseRepository warehouseRepository;

    private final OrderMapper orderMapper;

    private final OrderItemMapper orderItemMapper;

    private final OrderService orderService;

    private final UserSessionUtil userSessionUtil;

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('users:read')")
    @Operation(summary = "Get Order")
    public OrderDto getOrder(@PathVariable("uuid") UUID orderUuid) {
        Order order = orderService.getOrder(orderUuid);
        getAuthorizedUserAndCheckUuid(order.getUserUuid());
        OrderDto result = orderMapper.toOrderDto(order);
        result.setOrderItems(orderItemMapper.toDtoList(orderService.getOrderItems(order.getOrderId())));
        return result;
    }

    @PutMapping("/")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Create order")
    public OrderDto createOrder(@RequestHeader(HttpHeaders.IF_MATCH) String warehouseHash, @RequestHeader Map<String, String> headers, @RequestBody @Valid CreateOrderDto createOrderDto, HttpServletResponse response) {
        AuthUser authUser = getAuthorizedUserAndCheckUuid(null);
        Order order = orderService.findExistingOrder(warehouseHash, createOrderDto, authUser);
        if (order == null || !order.isConfirmed()) {
            AtomicReference<Supplier<CompletableFuture<Void>>> rollbackActionHolder = new AtomicReference<>();
            try {
                order = orderService.createOrder(createOrderDto, warehouseHash, authUser, headers, rollbackActionHolder);
            } catch (Exception e) {
                if (e instanceof ETagException || e instanceof BadRequestException) {
                    throw e;
                }
                log.error("Order creation Error", e);
                if (order != null) {
                    order.setConfirmed(false);
                }
                Supplier<CompletableFuture<Void>> rollbackActionSupplier = rollbackActionHolder.get();
                if (rollbackActionSupplier != null) {
                    try {
                        rollbackActionSupplier.get().join();
                    } catch (Exception ex) {
                        log.error("Order creation rollback Error", e);
                    }
                }
            }
        }
        OrderDto orderDto = orderMapper.toOrderDto(order);
        orderDto.setOrderItems(createOrderDto.getItems());
        return orderDto;
    }

    private AuthUser getAuthorizedUserAndCheckUuid(UUID userUuid) {
        AuthUser authUser = null;
        try {
            authUser = userSessionUtil.getAuthorizedUser();
        } catch (Exception e) {
            log.error("User " + authUser.getUuid() + " cannot update foreign profile.");
            throw new AuthenticationCredentialsNotFoundException("User not authorized.", e);
        }
        if (userUuid != null && !userUuid.equals(authUser.getUuid())) {
            log.error("User " + authUser.getUuid() + " cannot update foreign profile.");
            throw new AccessForbiddenException("User " + authUser.getUuid() + " cannot update foreign profile.");
        }
        return authUser;
    }
}
