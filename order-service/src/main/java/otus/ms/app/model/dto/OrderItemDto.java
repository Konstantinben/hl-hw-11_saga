package otus.ms.app.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class OrderItemDto {

    @NotNull
    private UUID itemUuid;
    private int quantity;
}
