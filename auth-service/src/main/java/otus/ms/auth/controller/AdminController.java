package otus.ms.auth.controller;

import otus.ms.auth.model.entity.User;
import otus.ms.auth.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "AdminController")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminController {

    private final UserRepository userRepository;


    @GetMapping("/user/uuid/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить данные пользователя")
    public User getUserByEmail(@PathVariable("uuid") UUID userId) {
        return userRepository
                .findByUuid(userId)
                .orElseThrow(() -> new BadCredentialsException("Cannod find user by " + userId));
    }

    @GetMapping("/user/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить данные пользователя")
    public User getUserByEmail(@PathVariable("email") String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Cannod find user by " + email));
    }
}
