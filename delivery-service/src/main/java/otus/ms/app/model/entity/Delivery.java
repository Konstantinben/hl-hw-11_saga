package otus.ms.app.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Table("delivery")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    private UUID orderUuid;
    private UUID userUuid;
    private LocalDate deliveryDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean confirmed;
}
