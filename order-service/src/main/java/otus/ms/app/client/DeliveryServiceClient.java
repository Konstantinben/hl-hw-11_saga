package otus.ms.app.client;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import otus.ms.app.model.dto.DeliveryDto;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@Service
@FeignClient(value = "DeliveryServiceClient", url = "${app.delivery-service-uri}")
public interface DeliveryServiceClient {

    @PutMapping("/")
    @Operation(summary = "Order Delivery")
    public DeliveryDto createDelivery(@RequestBody @Valid DeliveryDto deliveryDto, @RequestHeader Map<String, String> headerMap);

    @DeleteMapping("/{orderUuid}")
    @Operation(summary = "Rollback Order")
    public DeliveryDto rejectDelivery(@PathVariable("orderUuid") UUID orderUuid, @RequestHeader Map<String, String> headerMap);
}
