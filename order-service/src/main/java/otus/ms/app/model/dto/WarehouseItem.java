package otus.ms.app.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class WarehouseItem {
    private UUID itemUuid;
    private String itemName;
    private int quantity;
}
