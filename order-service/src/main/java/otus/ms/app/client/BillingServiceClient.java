package otus.ms.app.client;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import otus.ms.app.model.dto.PayOrderDto;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@Service
@FeignClient(value = "BillingServiceClient", url = "${app.billing-service-uri}")
public interface BillingServiceClient {

    @PutMapping("/order")
    @Operation(summary = "Pay order")
    PayOrderDto payOrder(@RequestBody @Valid PayOrderDto orderDto, @RequestHeader Map<String, String> headerMap);

    @DeleteMapping("/order/{orderUuid}")
    @Operation(summary = "Rollback Order")
    public PayOrderDto rollbackOrder(@PathVariable("orderUuid") UUID orderUuid, @RequestHeader Map<String, String> headerMap);
}
