package otus.ms.app.client;

import otus.ms.app.model.entity.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
import java.util.UUID;

@Service
@FeignClient(value = "AuthServiceBillingClient", url = "${app.auth-service-uri}")
public interface AuthServiceClient {

    @GetMapping("/admin/user/uuid/{uuid}")
    AuthUser getUserById(@PathVariable("uuid") UUID userId, @RequestHeader Map<String, String> headerMap);


    @GetMapping("/admin/user/email/{email}")
    @Operation(summary = "Посмотреть профиль")
    AuthUser getUserByEmail(@PathVariable("email") String email, @RequestHeader Map<String, String> headerMap);
}
