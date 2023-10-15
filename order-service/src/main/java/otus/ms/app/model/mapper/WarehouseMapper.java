package otus.ms.app.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import otus.ms.app.model.entity.WarehouseItem;

import java.util.List;

@Mapper(componentModel =  "spring")
public interface WarehouseMapper {

    List<otus.ms.app.model.dto.WarehouseItem> toWarehouseItems(List<WarehouseItem> items);

    @Mapping(target = "quantity", source = "itemQuantity")
    otus.ms.app.model.dto.WarehouseItem toDto(WarehouseItem warehouseItem);
}
