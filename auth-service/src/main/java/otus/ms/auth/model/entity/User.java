package otus.ms.auth.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import otus.ms.auth.model.Role;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.UUID;

@Table("auth_user")
@Data
public class User {

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
