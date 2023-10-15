package otus.ms.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import otus.ms.app.model.dto.DeliveryDto;
import otus.ms.app.model.entity.AuthUser;
import otus.ms.app.model.exception.AccessForbiddenException;
import otus.ms.app.model.exception.BadRequestException;
import otus.ms.app.model.mapper.DeliveryMapper;
import otus.ms.app.security.UserSessionUtil;
import otus.ms.app.service.DeliveryService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Tag(name = "DeliveryController")
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
@Validated
public class DeliveryController {

    private final DeliveryService deliveryService;

    private final UserSessionUtil userSessionUtil;

    private final DeliveryMapper deliveryMapper;

    @GetMapping("/{orderUuid}")
    @PreAuthorize("hasAuthority('users:read')")
    @Operation(summary = "Get Delivery")
    public List<DeliveryDto> getDelivery(@PathVariable("orderUuid") UUID orderUuid) {
        var deliveries = deliveryService.getDeliveries(orderUuid);
        return deliveryMapper.toDtoList(deliveries);
    }

    @PutMapping("/")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Order Delivery")
    public DeliveryDto createDelivery( @RequestBody @Valid DeliveryDto deliveryDto, HttpServletResponse response) {
        AuthUser authUser = getAuthorizedUserAndCheckUuid(deliveryDto.getUserUuid() != null ? deliveryDto.getUserUuid() : null);
        deliveryDto.setUserUuid(authUser.getUuid());
        return deliveryService.createDelivery(deliveryDto);
    }

    @DeleteMapping("/{orderUuid}")
    @PreAuthorize("hasAuthority('users:write')")
    @Operation(summary = "Rollback Order")
    public DeliveryDto rejectDelivery(@PathVariable("orderUuid") UUID orderUuid) {
        var delivery = deliveryService.getDelivery(orderUuid);
        if (delivery == null) {
            return null;
        }
        getAuthorizedUserAndCheckUuid(delivery.getUserUuid());
        return deliveryService.rejectDelivery(delivery);
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
