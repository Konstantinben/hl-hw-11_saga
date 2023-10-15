package otus.ms.app.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import otus.ms.app.model.dto.DeliveryDto;
import otus.ms.app.model.entity.Delivery;
import otus.ms.app.model.exception.BadRequestException;
import otus.ms.app.model.mapper.DeliveryMapper;
import otus.ms.app.repository.DeliveryRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    private final JdbcAggregateTemplate template;

    private final DeliveryMapper deliveryMapper;

    @Transactional
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        AtomicBoolean deliveryExists = new AtomicBoolean(true);
        Delivery delivery = deliveryRepository.getDeliveryByOrderUuidAndConfirmedIsTrue(deliveryDto.getOrderUuid()).orElseGet(() -> {
            deliveryExists.set(false);
            return deliveryMapper.toEntity(deliveryDto);
        });
        if (delivery.isConfirmed()) {
            throw new BadRequestException("Delivery already confirmed");
        }
        List<Delivery> deliveries = deliveryRepository
                .findDeliveriesByDeliveryDateAndStartTimeIsGreaterThanEqualAndEndTimeIsLessThanEqualAndConfirmedIsTrue(
                        deliveryDto.getDeliveryDate(), deliveryDto.getStartTime(), deliveryDto.getEndTime());
        if (CollectionUtils.isNotEmpty(deliveries)) {
            throw new BadRequestException("Delivery time slot is busy");
        }
        delivery.setConfirmed(true);
        if (deliveryExists.get()) {
            deliveryRepository.save(delivery);
        } else {
            template.insert(delivery);
        }

        return deliveryMapper.toDto(delivery);
    }

    public Delivery getDelivery(UUID orderUuid) {
        return deliveryRepository.getDeliveryByOrderUuidAndConfirmedIsTrue(orderUuid).orElse(null);
    }


    public List<Delivery> getDeliveries(UUID orderUuid) {
        return deliveryRepository.getDeliveriesByOrderUuid(orderUuid);
    }

    public DeliveryDto rejectDelivery(Delivery delivery) {
        if (delivery.isConfirmed()) {
            delivery.setConfirmed(false);
            deliveryRepository.save(delivery);
        }
        return deliveryMapper.toDto(delivery);
    }
}
