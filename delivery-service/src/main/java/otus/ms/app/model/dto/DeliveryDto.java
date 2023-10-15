package otus.ms.app.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class DeliveryDto {
    @NotNull
    private UUID orderUuid;
    private UUID userUuid;

    @NotNull
    private LocalDate deliveryDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;
    private DeliveryStatus deliveryStatus;
}
