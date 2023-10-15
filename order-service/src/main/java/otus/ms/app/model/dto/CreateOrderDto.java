package otus.ms.app.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreateOrderDto {
    @NotEmpty
    private List<OrderItemDto> items = new ArrayList<>();

    @NotNull
    DeliveryDto delivery;
}
