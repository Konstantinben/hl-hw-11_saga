package otus.ms.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import otus.ms.app.model.dto.BalanceDto;
import otus.ms.app.model.dto.OrderDto;
import otus.ms.app.model.dto.OrderStatus;
import otus.ms.app.model.entity.Balance;
import otus.ms.app.model.entity.Order;
import otus.ms.app.model.exception.BadRequestException;
import otus.ms.app.model.exception.NoEnoughBalanceException;
import otus.ms.app.model.mapper.OrderMapper;
import otus.ms.app.repository.BalanceRepository;
import otus.ms.app.repository.OrderRepository;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final OrderRepository orderRepository;

    private final BalanceRepository balanceRepository;

    private final JdbcAggregateTemplate template;

    private final OrderMapper orderMapper;

    @Transactional
    public OrderDto payOrder(OrderDto orderDto) {
        AtomicBoolean orderExists = new AtomicBoolean(true);
        Order order = orderRepository.getOrderByOrderUuid(orderDto.getOrderUuid()).orElseGet(() -> {
            orderExists.set(false);
            return orderMapper.toEntity(orderDto);
        });
        if (order.isPaid()) {
            throw new BadRequestException("Order already paid");
        }
        Balance balance = balanceRepository.findBalanceByUserUuid(orderDto.getUserUuid())
                .orElseThrow(() -> new NoEnoughBalanceException("No balance."));
        balance.setBalance(balance.getBalance() - orderDto.getTotalPrice());
        if (balance.getBalance() < 0) {
            throw new NoEnoughBalanceException("No balance.");
        }
        balanceRepository.save(balance);

        order.setPaid(true);
        if (orderExists.get()) {
            orderRepository.save(order);
        } else {
            template.insert(order);
        }

        orderDto.setOrderStatus(OrderStatus.PAID);
        return orderDto;
    }

    public Order rollbackOrder(Order order) {
        if (order.isPaid()) {
            Balance balance = balanceRepository.findBalanceByUserUuid(order.getUserUuid())
                    .orElseThrow(() -> new NoEnoughBalanceException("No balance."));

            balance.setBalance(balance.getBalance() + order.getTotalPrice());
            balanceRepository.save(balance);
            order.setPaid(false);
            orderRepository.save(order);
        }
        return order;
    }

    public Balance charge(UUID userUuid, BalanceDto balanceDto) {
        var balance = balanceRepository.findBalanceByUserUuid(userUuid).orElse(null);
        if (balance == null) {
            balance = new Balance(userUuid, balanceDto.getBalance());
            template.insert(balance);
        } else {
            balance.setBalance(balance.getBalance() + balanceDto.getBalance());
            balanceRepository.save(balance);
        }
        return balance;
    }
}
