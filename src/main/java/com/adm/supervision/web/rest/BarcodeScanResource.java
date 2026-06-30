package com.adm.supervision.web.rest;

import com.adm.supervision.service.BarcodeScanService;
import com.adm.supervision.service.dto.BarcodeScanRequest;
import com.adm.supervision.service.dto.BarcodeScanResultDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/barcodes")
public class BarcodeScanResource {

    private final BarcodeScanService barcodeScanService;

    public BarcodeScanResource(BarcodeScanService barcodeScanService) {
        this.barcodeScanService = barcodeScanService;
    }

    @PostMapping("/scan")
    @PreAuthorize("@businessAuthorizationService.canManageSales() or @businessAuthorizationService.canManageStock()")
    public ResponseEntity<BarcodeScanResultDTO> scan(@Valid @RequestBody BarcodeScanRequest request) {
        return ResponseEntity.ok(barcodeScanService.scan(request));
    }
}
