package otus.ms.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import otus.ms.app.model.dto.WarehouseDto;
import otus.ms.app.model.mapper.WarehouseMapper;
import otus.ms.app.repository.WarehouseRepository;

import javax.servlet.http.HttpServletResponse;

@Tag(name = "WarehouseController")
@RestController
@RequestMapping("/warehouse")
@RequiredArgsConstructor
@Slf4j
@Validated
public class WarehouseController {

    private final WarehouseRepository warehouseRepository;

    private final WarehouseMapper warehouseMapper;

    @GetMapping("/items")
    @PreAuthorize("hasAuthority('users:read')")
    @Operation(summary = "Get Items")
    public WarehouseDto warehouse(HttpServletResponse response) {
        var warehouseItems = warehouseRepository.findAllByOrderByItemUuid();
        response.addHeader(HttpHeaders.ETAG, String.valueOf(warehouseItems.hashCode()));
        return new WarehouseDto(warehouseMapper.toWarehouseItems(warehouseItems));
    }
}
