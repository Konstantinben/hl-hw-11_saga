package otus.ms.auth.security;

import otus.ms.auth.model.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public class ResponseUtil {
    public static ResponseEntity<ErrorResponseDto> error(String message, String info,
                                                         HttpStatus status) {
        return ResponseEntity.status(status).body(
                ErrorResponseDto.builder()
                        .message(message)
                        .error(info)
                        .status(status.value())
                        .timestamp(new Date())
                        .build());
    }
}
