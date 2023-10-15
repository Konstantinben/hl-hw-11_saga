package otus.ms.app.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Table("warehouse_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseItem {
    @Id
    private Long itemId;

    @NonNull
    private UUID itemUuid;

    @NotBlank
    private String itemName;

    private int itemQuantity;

    private int price;
}
