package otus.ms.app.model.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class BalanceDto {

    @NotNull(message = "balance cannot be null")
    @Min(value = 1, message = "balance must be greater than zero")
    private Integer balance;
}
