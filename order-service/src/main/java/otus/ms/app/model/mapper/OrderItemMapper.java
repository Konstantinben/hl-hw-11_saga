package otus.ms.app.model.mapper;

import org.mapstruct.Mapper;
import otus.ms.app.model.dto.OrderItemDto;
import otus.ms.app.model.entity.OrderItem;

import java.util.List;

@Mapper(componentModel =  "spring")
public interface OrderItemMapper {

    OrderItem toEntity(OrderItemDto orderItemDto);

    OrderItemDto toDto(OrderItem orderItem);

    List<OrderItemDto> toDtoList(List<OrderItem> orderItemList);
}
