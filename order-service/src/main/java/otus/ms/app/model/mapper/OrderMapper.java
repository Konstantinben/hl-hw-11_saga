package otus.ms.app.model.mapper;

import org.mapstruct.Mapper;
import otus.ms.app.model.dto.CreateOrderDto;
import otus.ms.app.model.dto.DeliveryStatus;
import otus.ms.app.model.dto.OrderDto;
import otus.ms.app.model.entity.Order;

@Mapper(componentModel =  "spring")
public interface OrderMapper {

    OrderDto toOrderDto(Order order);

    Order toEntity(CreateOrderDto orderDto);
}
