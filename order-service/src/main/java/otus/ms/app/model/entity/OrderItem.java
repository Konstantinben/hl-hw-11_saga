package otus.ms.app.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;
import java.util.UUID;

@Table("order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    private Long orderId;

    private Long itemId;

    @NonNull
    private UUID itemUuid;

    private int quantity;

    private int price;
}
