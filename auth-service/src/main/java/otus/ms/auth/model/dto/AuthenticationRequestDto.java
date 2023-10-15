package otus.ms.auth.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthenticationRequestDto {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
