package otus.ms.app.model.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class OrderDto {
    private UUID orderUuid;
    private UUID userUuid;
    private List<OrderItemDto> orderItems = new ArrayList<>();
    private boolean confirmed;
}
