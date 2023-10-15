package otus.ms.auth.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserSignupDto extends UserDto {

    @NotBlank
    private String password;
}
