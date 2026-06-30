package com.adm.supervision.web.rest;

import com.adm.supervision.service.StockWorkflowService;
import com.adm.supervision.service.dto.CloseInventaireStockRequest;
import com.adm.supervision.service.dto.InventaireStockDTO;
import com.adm.supervision.service.dto.LigneInventaireStockDTO;
import com.adm.supervision.service.dto.LigneReceptionProduitDTO;
import com.adm.supervision.service.dto.LigneTransfertStockDTO;
import com.adm.supervision.service.dto.MouvementStockDTO;
import com.adm.supervision.service.dto.ReceptionProduitDTO;
import com.adm.supervision.service.dto.ReverseMouvementStockRequest;
import com.adm.supervision.service.dto.ScanInventaireStockRequest;
import com.adm.supervision.service.dto.ScanReceptionProduitRequest;
import com.adm.supervision.service.dto.ScanTransfertStockRequest;
import com.adm.supervision.service.dto.TransfertStockDTO;
import com.adm.supervision.service.dto.ValidateReceptionProduitRequest;
import com.adm.supervision.service.dto.ValidateTransfertStockRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StockWorkflowResource {

    private final StockWorkflowService stockWorkflowService;

    public StockWorkflowResource(StockWorkflowService stockWorkflowService) {
        this.stockWorkflowService = stockWorkflowService;
    }

    @PostMapping("/reception-produits/{receptionId}/scan")
    @PreAuthorize("@businessAuthorizationService.canManageStock()")
    public ResponseEntity<LigneReceptionProduitDTO> scanReception(
        @PathVariable Long receptionId,
        @Valid @RequestBody ScanReceptionProduitRequest request
    ) {
        return ResponseEntity.ok(stockWorkflowService.scanReception(receptionId, request));
    }

    @PostMapping("/reception-produits/{receptionId}/validate")
    @PreAuthorize("@businessAuthorizationService.canManageStock()")
    public ResponseEntity<ReceptionProduitDTO> validateReception(
        @PathVariable Long receptionId,
        @Valid @RequestBody ValidateReceptionProduitRequest request
    ) {
        return ResponseEntity.ok(stockWorkflowService.validateReception(receptionId, request));
    }

    @PostMapping("/inventaire-stocks/{inventaireId}/start")
    @PreAuthorize("@businessAuthorizationService.canManageStock()")
    public ResponseEntity<InventaireStockDTO> startInventory(@PathVariable Long inventaireId) {
        return ResponseEntity.ok(stockWorkflowService.startInventory(inventaireId));
    }

    @PostMapping("/inventaire-stocks/{inventaireId}/scan")
    @PreAuthorize("@businessAuthorizationService.canManageStock()")
    public ResponseEntity<LigneInventaireStockDTO> scanInventory(
        @PathVariable Long inventaireId,
        @Valid @RequestBody ScanInventaireStockRequest request
    ) {
        return ResponseEntity.ok(stockWorkflowService.scanInventory(inventaireId, request));
    }

    @PostMapping("/inventaire-stocks/{inventaireId}/close")
    @PreAuthorize("@businessAuthorizationService.canManageStock()")
    public ResponseEntity<InventaireStockDTO> closeInventory(
        @PathVariable Long inventaireId,
        @Valid @RequestBody CloseInventaireStockRequest request
    ) {
        return ResponseEntity.ok(stockWorkflowService.closeInventory(inventaireId, request));
    }

    @PostMapping("/transfert-stocks/{transfertId}/validate")
    @PreAuthorize("@businessAuthorizationService.canManageStock()")
    public ResponseEntity<TransfertStockDTO> validateTransfer(
        @PathVariable Long transfertId,
        @Valid @RequestBody ValidateTransfertStockRequest request
    ) {
        return ResponseEntity.ok(stockWorkflowService.validateTransfer(transfertId, request));
    }

    @PostMapping("/transfert-stocks/{transfertId}/scan")
    @PreAuthorize("@businessAuthorizationService.canManageStock()")
    public ResponseEntity<LigneTransfertStockDTO> scanTransfer(
        @PathVariable Long transfertId,
        @Valid @RequestBody ScanTransfertStockRequest request
    ) {
        return ResponseEntity.ok(stockWorkflowService.scanTransfer(transfertId, request));
    }

    @PostMapping("/mouvement-stocks/{mouvementId}/reverse")
    @PreAuthorize("@businessAuthorizationService.canManageStock()")
    public ResponseEntity<MouvementStockDTO> reverseMovement(
        @PathVariable Long mouvementId,
        @Valid @RequestBody(required = false) ReverseMouvementStockRequest request
    ) {
        return ResponseEntity.ok(stockWorkflowService.reverseMovement(mouvementId, request));
    }
}
