package otus.ms.app.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUser {

    @Id
    private Long id;

    @NotBlank
    @Email
    private String email;

    private UUID uuid;

    private Role role;

    @NotBlank
    private String password;
}
