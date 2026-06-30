package com.adm.supervision.web.rest;

import com.adm.supervision.domain.enumeration.StatutVente;
import com.adm.supervision.security.BusinessAuthorizationService;
import com.adm.supervision.service.DashboardReportingService;
import com.adm.supervision.service.dto.DashboardOverviewDTO;
import com.adm.supervision.service.dto.DashboardRoyaltyByGroupeArticleDTO;
import com.adm.supervision.service.dto.DashboardSalesByDayPointDTO;
import com.adm.supervision.service.dto.DashboardStockAlertDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardResource {

    private final DashboardReportingService dashboardReportingService;
    private final BusinessAuthorizationService businessAuthorizationService;

    public DashboardResource(
        DashboardReportingService dashboardReportingService,
        BusinessAuthorizationService businessAuthorizationService
    ) {
        this.dashboardReportingService = dashboardReportingService;
        this.businessAuthorizationService = businessAuthorizationService;
    }

    @GetMapping("/overview")
    @PreAuthorize("@businessAuthorizationService.canReadReporting()")
    public ResponseEntity<DashboardOverviewDTO> overview(
        @RequestParam(required = false) LocalDate from,
        @RequestParam(required = false) LocalDate to,
        @RequestParam(required = false) Long boutiqueId,
        @RequestParam(required = false) Long locataireId
    ) {
        Long scopedBoutiqueId = boutiqueId;
        Long scopedLocataireId = locataireId;
        if (businessAuthorizationService.isCurrentUserLocataire()) {
            assertBoutiqueAccessibleForLocataire(boutiqueId);
            scopedLocataireId = businessAuthorizationService
                .getCurrentLocataireId()
                .orElseThrow(() -> new AccessDeniedException("Locataire introuvable pour l'utilisateur courant"));
        }
        return ResponseEntity.ok(dashboardReportingService.getOverview(from, to, scopedBoutiqueId, scopedLocataireId));
    }

    @GetMapping("/sales-by-day")
    @PreAuthorize("@businessAuthorizationService.canReadReporting()")
    public ResponseEntity<List<DashboardSalesByDayPointDTO>> salesByDay(
        @RequestParam(required = false) LocalDate from,
        @RequestParam(required = false) LocalDate to,
        @RequestParam(required = false) Long boutiqueId,
        @RequestParam(required = false) Long locataireId,
        @RequestParam(required = false) StatutVente statutVente,
        @RequestParam(required = false) BigDecimal minMontantNet
    ) {
        Long scopedBoutiqueId = boutiqueId;
        Long scopedLocataireId = locataireId;
        if (businessAuthorizationService.isCurrentUserLocataire()) {
            assertBoutiqueAccessibleForLocataire(boutiqueId);
            scopedLocataireId = businessAuthorizationService
                .getCurrentLocataireId()
                .orElseThrow(() -> new AccessDeniedException("Locataire introuvable pour l'utilisateur courant"));
        }
        return ResponseEntity.ok(
            dashboardReportingService.getSalesByDay(from, to, scopedBoutiqueId, scopedLocataireId, statutVente, minMontantNet)
        );
    }

    @GetMapping("/redevances-par-groupe-article")
    @PreAuthorize("@businessAuthorizationService.canReadRoyalties() or @businessAuthorizationService.isCurrentUserLocataire()")
    public ResponseEntity<List<DashboardRoyaltyByGroupeArticleDTO>> redevancesParGroupeArticle(
        @RequestParam(required = false) LocalDate from,
        @RequestParam(required = false) LocalDate to,
        @RequestParam(required = false) Long boutiqueId,
        @RequestParam(required = false) Long locataireId
    ) {
        Long scopedBoutiqueId = boutiqueId;
        Long scopedLocataireId = locataireId;
        if (businessAuthorizationService.isCurrentUserLocataire()) {
            assertBoutiqueAccessibleForLocataire(boutiqueId);
            scopedLocataireId = businessAuthorizationService
                .getCurrentLocataireId()
                .orElseThrow(() -> new AccessDeniedException("Locataire introuvable pour l'utilisateur courant"));
        }
        return ResponseEntity.ok(dashboardReportingService.getRoyaltyByGroupeArticle(from, to, scopedBoutiqueId, scopedLocataireId));
    }

    private void assertBoutiqueAccessibleForLocataire(Long boutiqueId) {
        if (boutiqueId != null && !businessAuthorizationService.getAccessibleBoutiqueIds().contains(boutiqueId)) {
            throw new AccessDeniedException("Acces refuse a cette boutique");
        }
    }

    @GetMapping("/stock-alerts")
    @PreAuthorize("@businessAuthorizationService.canReadStock()")
    public ResponseEntity<List<DashboardStockAlertDTO>> stockAlerts(
        @RequestParam(required = false) Long boutiqueId,
        @RequestParam(required = false) Long depotId,
        @RequestParam(required = false) Long produitId
    ) {
        return ResponseEntity.ok(dashboardReportingService.getStockAlerts(boutiqueId, depotId, produitId));
    }
}
