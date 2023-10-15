package otus.ms.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserUuidDto {

    @NotNull
    private UUID accountId;
}
