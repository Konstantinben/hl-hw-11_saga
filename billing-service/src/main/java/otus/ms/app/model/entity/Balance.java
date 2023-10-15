package otus.ms.app.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("balance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Balance {

    @Id
    private UUID userUuid;
    private Integer balance;
}
