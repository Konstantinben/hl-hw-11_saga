package otus.ms.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import otus.ms.app.model.dto.BalanceDto;
import otus.ms.app.model.dto.OrderDto;
import otus.ms.app.model.dto.OrderStatus;
import otus.ms.app.model.entity.AuthUser;
import otus.ms.app.model.entity.Balance;
import otus.ms.app.model.entity.Order;
import otus.ms.app.model.exception.AccessForbiddenException;
import otus.ms.app.model.exception.BadRequestException;
import otus.ms.app.model.mapper.OrderMapper;
import otus.ms.app.repository.BalanceRepository;
import otus.ms.app.repository.OrderRepository;
import otus.ms.app.security.UserSessionUtil;
import otus.ms.app.service.BillingService;

import javax.validation.Valid;
import java.util.UUID;

@Tag(name = "BillingController")
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BillingController {

    private final BalanceRepository balanceRepository;

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final BillingService billingService;

    private final UserSessionUtil userSessionUtil;

    @GetMapping("/balance")
    @PreAuthorize("hasAuthority('users:read')")
    @Operation(summary = "Get Balance")
    public Balance getOrder() {
        var authUser = getAuthorizedUserAndCheckUuid(null);
        return balanceRepository.findBalanceByUserUuid(authUser.getUuid()).orElse(null);
    }

    @PutMapping("/charge")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Charge Balance")
    public Balance charge(@RequestBody @Valid BalanceDto balanceDto) {
        AuthUser authUser = getAuthorizedUserAndCheckUuid(null);
        return billingService.charge(authUser.getUuid(), balanceDto);
    }



    @PutMapping("/order")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Pay order")
    public OrderDto payOrder(@RequestBody @Valid OrderDto orderDto) {
        AuthUser authUser = getAuthorizedUserAndCheckUuid(orderDto.getUserUuid() != null ? orderDto.getUserUuid() : null);
        orderDto.setUserUuid(authUser.getUuid());
        return billingService.payOrder(orderDto);
    }

    @GetMapping("/order/{orderUuid}")
    @PreAuthorize("hasAuthority('users:read')")
    @Operation(summary = "Get Order")
    public OrderDto getOrder(@PathVariable("orderUuid") UUID orderUuid) {
        Order order = orderRepository.getOrderByOrderUuid(orderUuid).orElse(null);
        if (order == null) {
            return null;
        }
        getAuthorizedUserAndCheckUuid(order.getUserUuid());
        var result =  orderMapper.toOrderDto(order);
        result.setOrderStatus(order.isPaid() ? OrderStatus.PAID : OrderStatus.REJECTED);
        return result;
    }

    @DeleteMapping("/order/{orderUuid}")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Rollback Order")
    public OrderDto rollbackOrder(@PathVariable("orderUuid") UUID orderUuid) {
        Order order = orderRepository.getOrderByOrderUuid(orderUuid).orElse(null);
        if (order == null) {
            throw new BadRequestException("Order does not exist");
        }
        getAuthorizedUserAndCheckUuid(order.getUserUuid());
        billingService.rollbackOrder(order);
        OrderDto orderDto = orderMapper.toOrderDto(order);
        orderDto.setOrderStatus(OrderStatus.REJECTED);
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
