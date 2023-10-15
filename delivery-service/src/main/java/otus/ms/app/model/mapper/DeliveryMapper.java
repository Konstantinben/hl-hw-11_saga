package otus.ms.app.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import otus.ms.app.model.dto.DeliveryDto;
import otus.ms.app.model.dto.DeliveryStatus;
import otus.ms.app.model.entity.Delivery;

import java.util.List;

@Mapper(componentModel =  "spring")
public interface DeliveryMapper {

    @Mapping(target = "deliveryStatus", expression = "java(getStatus(delivery.isConfirmed()))")
    DeliveryDto toDto(Delivery delivery);

    List<DeliveryDto> toDtoList(List<Delivery> deliveries);

    Delivery toEntity(DeliveryDto deliveryDto);

    default DeliveryStatus getStatus(boolean confirmed) {
        return confirmed ? DeliveryStatus.CONFIRMED : DeliveryStatus.REJECTED;
    }
}
