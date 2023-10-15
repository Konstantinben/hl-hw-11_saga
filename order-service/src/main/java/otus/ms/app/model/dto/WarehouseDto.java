package otus.ms.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class WarehouseDto {
    List<WarehouseItem> items = new ArrayList<>();
}
