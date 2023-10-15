package otus.ms.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import otus.ms.auth.model.dto.AuthenticationRequestDto;
import otus.ms.auth.model.dto.UserSignupDto;
import otus.ms.auth.model.dto.UserUuidDto;
import otus.ms.auth.model.entity.User;
import otus.ms.auth.model.exception.UserExistsException;
import otus.ms.auth.model.mapper.UserMapper;
import otus.ms.auth.repository.UserRepository;
import otus.ms.auth.security.JwtTokenProvider;
import otus.ms.auth.security.ResponseUtil;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@Tag(name = "UserController", description = "Контроллер для работы с пользователями")
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/signup")
    @Operation(summary = "Signup user")
    public UserUuidDto signup(@RequestBody @Valid UserSignupDto userDto, HttpServletResponse response) {
        userRepository.findByEmail(userDto.getEmail()).ifPresent(
                existentUser -> {
                    log.warn("User " + userDto.getEmail() + " already registered.");
                    throw new UserExistsException("User " + userDto.getEmail() + " already registered.");
                }
        );
        User user = userMapper.toUser(userDto, passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
        return new UserUuidDto(user.getUuid());
    }

    @PostMapping("/login")
    @Operation(summary = "Получить JWT token")
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationRequestDto authRequest, HttpServletResponse response) {

        String email = authRequest.getEmail();
        String password = authRequest.getPassword();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User does not exists"));
            String token = jwtTokenProvider.createToken(email, user.getRole(), user.getUuid());

            return ResponseEntity.ok(Map.of("token", token));

        } catch (AuthenticationException e) {
            return ResponseUtil.error("HttpStatus.FORBIDDEN", "Invalid Email/password combination", HttpStatus.FORBIDDEN);
        }
    }
}


