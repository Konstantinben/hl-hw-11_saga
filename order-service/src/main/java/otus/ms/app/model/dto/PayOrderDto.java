package otus.ms.app.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class PayOrderDto {
    @NotNull
    private UUID orderUuid;

    private UUID userUuid;

    @NotNull(message = "balance cannot be null")
    @Min(value = 1, message = "balance must be greater than zero")
    private Integer totalPrice;

    private PayOrderStatus orderStatus;
}
