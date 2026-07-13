package com.adm.supervision.service;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.ExploitationBoutique;
import com.adm.supervision.domain.GroupeArticle;
import com.adm.supervision.domain.LigneVente;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.domain.PaiementRedevance;
import com.adm.supervision.domain.RapportExport;
import com.adm.supervision.domain.RegleRedevance;
import com.adm.supervision.domain.ScanInconnu;
import com.adm.supervision.domain.StockProduit;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.StatutVente;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.BoutiqueRepository;
import com.adm.supervision.repository.CalculRedevanceRepository;
import com.adm.supervision.repository.ExploitationBoutiqueRepository;
import com.adm.supervision.repository.LigneVenteRepository;
import com.adm.supervision.repository.LocataireRepository;
import com.adm.supervision.repository.PaiementRedevanceRepository;
import com.adm.supervision.repository.RapportExportRepository;
import com.adm.supervision.repository.RegleRedevanceRepository;
import com.adm.supervision.repository.ScanInconnuRepository;
import com.adm.supervision.repository.StockProduitRepository;
import com.adm.supervision.repository.VenteRepository;
import com.adm.supervision.service.dto.DashboardOverviewDTO;
import com.adm.supervision.service.dto.DashboardRoyaltyByGroupeArticleDTO;
import com.adm.supervision.service.dto.DashboardSalesByDayPointDTO;
import com.adm.supervision.service.dto.DashboardStockAlertDTO;
import com.adm.supervision.service.dto.GenerateRapportExportRequest;
import com.adm.supervision.service.dto.RapportExportPreviewDTO;
import com.adm.supervision.service.mapper.RapportExportMapper;
import com.adm.supervision.service.reporting.ExportDownload;
import com.adm.supervision.service.reporting.ReportDocument;
import com.adm.supervision.service.reporting.ReportFileService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DashboardReportingService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final VenteRepository venteRepository;
    private final LigneVenteRepository ligneVenteRepository;
    private final RegleRedevanceRepository regleRedevanceRepository;
    private final ExploitationBoutiqueRepository exploitationBoutiqueRepository;
    private final RedevanceRateResolver redevanceRateResolver;
    private final StockProduitRepository stockProduitRepository;
    private final ScanInconnuRepository scanInconnuRepository;
    private final CalculRedevanceRepository calculRedevanceRepository;
    private final PaiementRedevanceRepository paiementRedevanceRepository;
    private final RapportExportRepository rapportExportRepository;
    private final RapportExportMapper rapportExportMapper;
    private final BoutiqueRepository boutiqueRepository;
    private final LocataireRepository locataireRepository;
    private final ModuleSecurityService moduleSecurityService;
    private final JournalAuditService journalAuditService;
    private final ReportFileService reportFileService;

    public DashboardReportingService(
        VenteRepository venteRepository,
        LigneVenteRepository ligneVenteRepository,
        RegleRedevanceRepository regleRedevanceRepository,
        ExploitationBoutiqueRepository exploitationBoutiqueRepository,
        RedevanceRateResolver redevanceRateResolver,
        StockProduitRepository stockProduitRepository,
        ScanInconnuRepository scanInconnuRepository,
        CalculRedevanceRepository calculRedevanceRepository,
        PaiementRedevanceRepository paiementRedevanceRepository,
        RapportExportRepository rapportExportRepository,
        RapportExportMapper rapportExportMapper,
        BoutiqueRepository boutiqueRepository,
        LocataireRepository locataireRepository,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService,
        ReportFileService reportFileService
    ) {
        this.venteRepository = venteRepository;
        this.ligneVenteRepository = ligneVenteRepository;
        this.regleRedevanceRepository = regleRedevanceRepository;
        this.exploitationBoutiqueRepository = exploitationBoutiqueRepository;
        this.redevanceRateResolver = redevanceRateResolver;
        this.stockProduitRepository = stockProduitRepository;
        this.scanInconnuRepository = scanInconnuRepository;
        this.calculRedevanceRepository = calculRedevanceRepository;
        this.paiementRedevanceRepository = paiementRedevanceRepository;
        this.rapportExportRepository = rapportExportRepository;
        this.rapportExportMapper = rapportExportMapper;
        this.boutiqueRepository = boutiqueRepository;
        this.locataireRepository = locataireRepository;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
        this.reportFileService = reportFileService;
    }

    @Transactional(readOnly = true)
    public DashboardOverviewDTO getOverview(LocalDate from, LocalDate to, Long boutiqueId, Long locataireId) {
        assertBoutiqueAccess(boutiqueId, "Acces refuse au tableau de bord demande");
        LocalDate effectiveFrom = from == null ? LocalDate.now().minusDays(29) : from;
        LocalDate effectiveTo = to == null ? LocalDate.now() : to;
        validatePeriodRange(effectiveFrom, effectiveTo);

        boolean royaltyScopedDashboard = moduleSecurityService.hasGlobalBoutiqueAccess() && moduleSecurityService.canReadRoyalties();
        List<Vente> ventes = royaltyScopedDashboard
            ? List.of()
            : loadSales(effectiveFrom, effectiveTo, boutiqueId, locataireId, null, null);
        BigDecimal royaltyTurnover = royaltyScopedDashboard
            ? calculateRoyaltyTurnover(effectiveFrom, effectiveTo, boutiqueId, locataireId)
            : ZERO;
        DashboardOverviewDTO overview = new DashboardOverviewDTO();
        overview.setGrossSales(
            royaltyScopedDashboard
                ? royaltyTurnover
                : sum(
                      ventes
                          .stream()
                          .filter(vente -> vente.getStatut() == StatutVente.VALIDEE)
                          .map(Vente::getMontantBrut)
                          .toList()
                  )
        );
        overview.setNetSales(
            royaltyScopedDashboard
                ? royaltyTurnover
                : sum(
                      ventes
                          .stream()
                          .filter(vente -> vente.getStatut() == StatutVente.VALIDEE)
                          .map(Vente::getMontantNet)
                          .toList()
                  )
        );
        overview.setValidatedSalesCount(
            ventes
                .stream()
                .filter(vente -> vente.getStatut() == StatutVente.VALIDEE)
                .count()
        );
        overview.setPendingSalesCount(
            ventes
                .stream()
                .filter(vente -> vente.getStatut() == StatutVente.BROUILLON)
                .count()
        );
        overview.setStockAlertCount(getStockAlerts(boutiqueId, null, null).size());
        overview.setUnresolvedUnknownScans(
            boutiqueId == null
                ? scanInconnuRepository.countByResoluFalse()
                : scanInconnuRepository.countByBoutique_IdAndResoluFalse(boutiqueId)
        );
        overview.setRoyaltyOutstandingAmount(calculateOutstandingRoyalty(boutiqueId, locataireId));
        return overview;
    }

    @Transactional(readOnly = true)
    public List<DashboardSalesByDayPointDTO> getSalesByDay(
        LocalDate from,
        LocalDate to,
        Long boutiqueId,
        Long locataireId,
        StatutVente statutVente,
        BigDecimal minMontantNet
    ) {
        assertBoutiqueAccess(boutiqueId, "Acces refuse a la synthese ventes demandee");
        LocalDate effectiveFrom = from == null ? LocalDate.now().minusDays(6) : from;
        LocalDate effectiveTo = to == null ? LocalDate.now() : to;
        validatePeriodRange(effectiveFrom, effectiveTo);
        StatutVente effectiveStatut = statutVente == null ? StatutVente.VALIDEE : statutVente;

        Map<LocalDate, DashboardSalesByDayPointDTO> byDay = new LinkedHashMap<>();
        LocalDate cursor = effectiveFrom;
        while (!cursor.isAfter(effectiveTo)) {
            DashboardSalesByDayPointDTO point = new DashboardSalesByDayPointDTO();
            point.setDay(cursor);
            point.setValidatedSalesCount(0);
            point.setGrossAmount(ZERO);
            point.setNetAmount(ZERO);
            byDay.put(cursor, point);
            cursor = cursor.plusDays(1);
        }

        for (Vente vente : loadSales(effectiveFrom, effectiveTo, boutiqueId, locataireId, effectiveStatut, minMontantNet)) {
            LocalDate day = vente.getDateHeure().atZone(ZoneOffset.UTC).toLocalDate();
            DashboardSalesByDayPointDTO point = byDay.get(day);
            if (point == null) {
                continue;
            }
            point.setValidatedSalesCount(point.getValidatedSalesCount() + 1);
            point.setGrossAmount(safe(point.getGrossAmount()).add(safe(vente.getMontantBrut())));
            point.setNetAmount(safe(point.getNetAmount()).add(safe(vente.getMontantNet())));
        }
        return new ArrayList<>(byDay.values());
    }

    @Transactional(readOnly = true)
    public List<DashboardRoyaltyByGroupeArticleDTO> getRoyaltyByGroupeArticle(
        LocalDate from,
        LocalDate to,
        Long boutiqueId,
        Long locataireId
    ) {
        assertBoutiqueAccess(boutiqueId, "Acces refuse a la ventilation de redevance demandee");
        LocalDate effectiveFrom = from == null ? LocalDate.now().minusDays(29) : from;
        LocalDate effectiveTo = to == null ? LocalDate.now() : to;
        validatePeriodRange(effectiveFrom, effectiveTo);

        List<Vente> ventes = loadSales(effectiveFrom, effectiveTo, boutiqueId, locataireId, StatutVente.VALIDEE, null);
        List<RegleRedevance> rules = regleRedevanceRepository.findAllWithEagerRelationships();
        List<ExploitationBoutique> exploitations = exploitationBoutiqueRepository.findAllWithEagerRelationships();

        Map<Long, DashboardRoyaltyByGroupeArticleDTO> byGroup = new LinkedHashMap<>();
        for (Vente vente : ventes) {
            LocalDate saleDate = vente.getDateHeure().atZone(ZoneOffset.UTC).toLocalDate();
            for (LigneVente ligne : ligneVenteRepository.findAllByVente_Id(vente.getId())) {
                GroupeArticle groupe = ligne.getProduit().getGroupeArticle();
                Long groupKey = groupe == null ? 0L : groupe.getId();
                BigDecimal base = safe(ligne.getMontantLigne());
                BigDecimal taux = redevanceRateResolver.resolveRate(rules, exploitations, ligne.getProduit(), vente, saleDate);
                BigDecimal royalty = base.multiply(taux).divide(HUNDRED, 2, RoundingMode.HALF_UP);

                DashboardRoyaltyByGroupeArticleDTO dto = byGroup.computeIfAbsent(groupKey, key -> {
                    DashboardRoyaltyByGroupeArticleDTO created = new DashboardRoyaltyByGroupeArticleDTO();
                    created.setGroupeArticleId(groupe == null ? null : groupe.getId());
                    created.setGroupeArticleCode(groupe == null ? null : groupe.getCode());
                    created.setGroupeArticleLibelle(groupe == null ? "Hors groupe" : groupe.getLibelle());
                    created.setChiffreAffaires(ZERO);
                    created.setMontantRedevance(ZERO);
                    created.setTauxEffectif(ZERO);
                    created.setNombreArticlesVendus(0);
                    return created;
                });

                dto.setChiffreAffaires(safe(dto.getChiffreAffaires()).add(base));
                dto.setMontantRedevance(safe(dto.getMontantRedevance()).add(royalty));
                dto.setNombreArticlesVendus(dto.getNombreArticlesVendus() + safe(ligne.getQuantite()).longValue());
            }
        }

        for (DashboardRoyaltyByGroupeArticleDTO dto : byGroup.values()) {
            if (dto.getChiffreAffaires().signum() > 0) {
                dto.setTauxEffectif(dto.getMontantRedevance().multiply(HUNDRED).divide(dto.getChiffreAffaires(), 2, RoundingMode.HALF_UP));
            }
        }

        return byGroup
            .values()
            .stream()
            .sorted((left, right) -> right.getMontantRedevance().compareTo(left.getMontantRedevance()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<DashboardStockAlertDTO> getStockAlerts(Long boutiqueId, Long depotId, Long produitId) {
        assertBoutiqueAccess(boutiqueId, "Acces refuse aux alertes stock demandees");
        List<StockProduit> alerts =
            boutiqueId == null ? stockProduitRepository.findStockAlerts() : stockProduitRepository.findStockAlertsByBoutiqueId(boutiqueId);
        return alerts
            .stream()
            .filter(stockProduit -> isBoutiqueVisible(stockProduit.getDepot().getBoutique().getId()))
            .filter(stockProduit -> depotId == null || Objects.equals(stockProduit.getDepot().getId(), depotId))
            .filter(stockProduit -> produitId == null || Objects.equals(stockProduit.getProduit().getId(), produitId))
            .map(this::toAlertDto)
            .toList();
    }

    public RapportExportPreviewDTO generateExport(GenerateRapportExportRequest request) {
        applyExportScope(request);
        assertReportTypeAllowed(request);
        assertBoutiqueAccess(request.getBoutiqueId(), "Acces refuse a l'export demande");
        validatePeriodRange(request.getPeriodeDebut(), request.getPeriodeFin());
        Boutique boutique = resolveBoutique(request.getBoutiqueId());
        Locataire locataire = resolveLocataire(request.getLocataireId());
        User currentUser = moduleSecurityService.getCurrentUser();

        ReportDocument document = buildReportDocument(request, boutique, locataire);
        String fileName = buildExportFileName(request.getTypeRapport(), request.getFormat().name().toLowerCase(Locale.ROOT));
        reportFileService.writeReport(fileName, request.getFormat(), document);
        reportFileService.writePreview(fileName, document.preview());

        RapportExport export = new RapportExport()
            .reference(buildExportReference(request.getTypeRapport()))
            .typeRapport(request.getTypeRapport())
            .format(request.getFormat())
            .periodeDebut(request.getPeriodeDebut())
            .periodeFin(request.getPeriodeFin())
            .cheminFichier(fileName)
            .dateGeneration(Instant.now())
            .boutique(boutique)
            .locataire(locataire)
            .utilisateur(currentUser);
        export = rapportExportRepository.save(export);

        journalAuditService.logAction(
            TypeActionAudit.EXPORT,
            "RapportExport",
            export.getReference(),
            "Generation rapport type=" + export.getTypeRapport() + ", format=" + export.getFormat(),
            boutique,
            currentUser
        );

        RapportExportPreviewDTO previewDTO = new RapportExportPreviewDTO();
        previewDTO.setExport(rapportExportMapper.toDto(export));
        previewDTO.setPreview(document.preview());
        return previewDTO;
    }

    @Transactional(readOnly = true)
    public RapportExportPreviewDTO previewExport(Long rapportExportId) {
        RapportExport export = getExport(rapportExportId);
        auditExportAccess("Apercu export reference=" + export.getReference(), export);
        RapportExportPreviewDTO previewDTO = new RapportExportPreviewDTO();
        previewDTO.setExport(rapportExportMapper.toDto(export));
        previewDTO.setPreview(reportFileService.loadPreview(export.getCheminFichier()));
        return previewDTO;
    }

    @Transactional(readOnly = true)
    public ExportDownload downloadExport(Long rapportExportId) {
        RapportExport export = getExport(rapportExportId);
        auditExportAccess("Telechargement export reference=" + export.getReference(), export);
        return reportFileService.download(export.getCheminFichier(), export.getFormat());
    }

    private RapportExport getExport(Long rapportExportId) {
        RapportExport export = rapportExportRepository
            .findOneWithEagerRelationships(rapportExportId)
            .orElseThrow(() -> new BusinessValidationException("rapportExport", "notFound", "Rapport export introuvable"));
        if (export.getBoutique() != null) {
            moduleSecurityService.assertBoutiqueAccess(export.getBoutique().getId(), "Acces refuse au rapport export demande");
        } else if (
            !moduleSecurityService.hasGlobalBoutiqueAccess() &&
            !Objects.equals(moduleSecurityService.getCurrentUser().getId(), export.getUtilisateur().getId())
        ) {
            throw new org.springframework.security.access.AccessDeniedException("Acces refuse au rapport export demande");
        }
        return export;
    }

    private Boutique resolveBoutique(Long boutiqueId) {
        if (boutiqueId == null) {
            return null;
        }
        return boutiqueRepository
            .findById(boutiqueId)
            .orElseThrow(() -> new BusinessValidationException("boutique", "notFound", "Boutique introuvable"));
    }

    private Locataire resolveLocataire(Long locataireId) {
        if (locataireId == null) {
            return null;
        }
        return locataireRepository
            .findById(locataireId)
            .orElseThrow(() -> new BusinessValidationException("locataire", "notFound", "Locataire introuvable"));
    }

    private List<Vente> loadSales(
        LocalDate from,
        LocalDate to,
        Long boutiqueId,
        Long locataireId,
        StatutVente statutVente,
        BigDecimal minMontantNet
    ) {
        Instant start = from.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = to.plusDays(1).atStartOfDay().minusNanos(1).toInstant(ZoneOffset.UTC);
        List<Vente> ventes =
            boutiqueId == null
                ? venteRepository.findAllByDateHeureBetween(start, end)
                : venteRepository.findAllByDateHeureBetweenAndBoutique_Id(start, end, boutiqueId);
        return ventes
            .stream()
            .filter(vente -> isBoutiqueVisible(vente.getBoutique().getId()))
            .filter(this::isSaleVisibleForCurrentSeller)
            .filter(vente -> locataireId == null || Objects.equals(vente.getLocataire().getId(), locataireId))
            .filter(vente -> statutVente == null || vente.getStatut() == statutVente)
            .filter(vente -> minMontantNet == null || safe(vente.getMontantNet()).compareTo(minMontantNet) >= 0)
            .sorted((left, right) -> left.getDateHeure().compareTo(right.getDateHeure()))
            .toList();
    }

    private DashboardStockAlertDTO toAlertDto(StockProduit stockProduit) {
        DashboardStockAlertDTO dto = new DashboardStockAlertDTO();
        dto.setStockProduitId(stockProduit.getId());
        dto.setProduitId(stockProduit.getProduit().getId());
        dto.setProduitDesignation(stockProduit.getProduit().getDesignation());
        dto.setDepotId(stockProduit.getDepot().getId());
        dto.setDepotCode(stockProduit.getDepot().getCode());
        dto.setBoutiqueId(stockProduit.getDepot().getBoutique().getId());
        dto.setBoutiqueNom(stockProduit.getDepot().getBoutique().getNom());
        dto.setQuantiteTheorique(stockProduit.getQuantiteTheorique());
        dto.setStockAlerte(stockProduit.getStockAlerte());
        return dto;
    }

    private BigDecimal calculateOutstandingRoyalty(Long boutiqueId, Long locataireId) {
        List<CalculRedevance> calculs = calculRedevanceRepository
            .findAllWithEagerRelationships()
            .stream()
            .filter(calcul -> calcul.getStatut() != com.adm.supervision.domain.enumeration.StatutRedevance.ANNULEE)
            .filter(calcul -> isBoutiqueVisible(calcul.getBoutique().getId()))
            .filter(calcul -> boutiqueId == null || Objects.equals(calcul.getBoutique().getId(), boutiqueId))
            .filter(calcul -> locataireId == null || Objects.equals(calcul.getLocataire().getId(), locataireId))
            .toList();
        BigDecimal totalDue = sum(calculs.stream().map(CalculRedevance::getMontantRedevance).toList());
        List<Long> calculIds = calculs.stream().map(CalculRedevance::getId).toList();
        BigDecimal totalPaid = sum(
            paiementRedevanceRepository
                .findAllWithEagerRelationships()
                .stream()
                .filter(paiement -> calculIds.contains(paiement.getCalcul().getId()))
                .map(PaiementRedevance::getMontant)
                .toList()
        );
        BigDecimal outstanding = totalDue.subtract(totalPaid);
        return outstanding.compareTo(ZERO) < 0 ? ZERO : outstanding;
    }

    private BigDecimal calculateRoyaltyTurnover(LocalDate from, LocalDate to, Long boutiqueId, Long locataireId) {
        List<BigDecimal> turnovers = calculRedevanceRepository
            .findAllWithEagerRelationships()
            .stream()
            .filter(calcul -> calcul.getStatut() != com.adm.supervision.domain.enumeration.StatutRedevance.ANNULEE)
            .filter(calcul -> isBoutiqueVisible(calcul.getBoutique().getId()))
            .filter(calcul -> boutiqueId == null || Objects.equals(calcul.getBoutique().getId(), boutiqueId))
            .filter(calcul -> locataireId == null || Objects.equals(calcul.getLocataire().getId(), locataireId))
            .filter(calcul -> !calcul.getPeriodeDebut().isBefore(from) && !calcul.getPeriodeFin().isAfter(to))
            .map(CalculRedevance::getChiffreAffaires)
            .toList();
        return sum(turnovers);
    }

    private ReportDocument buildReportDocument(GenerateRapportExportRequest request, Boutique boutique, Locataire locataire) {
        String normalizedType = normalizeReportType(request.getTypeRapport());
        if (normalizedType.contains("stock")) {
            return buildStockAlertDocument(request);
        }
        if (normalizedType.contains("redevance") || normalizedType.contains("royalty")) {
            return buildRoyaltyDocument(request, boutique, locataire);
        }
        if (normalizedType.contains("scan")) {
            return buildUnknownScanDocument(request);
        }
        return buildSalesDocument(request, locataire);
    }

    private ReportDocument buildStockAlertDocument(GenerateRapportExportRequest request) {
        List<DashboardStockAlertDTO> alerts = getStockAlerts(request.getBoutiqueId(), request.getDepotId(), request.getProduitId());
        BigDecimal totalEcart = sum(
            alerts
                .stream()
                .map(alert -> maxZero(safe(alert.getStockAlerte()).subtract(safe(alert.getQuantiteTheorique()))))
                .toList()
        );
        long critiques = alerts
            .stream()
            .filter(alert -> safe(alert.getQuantiteTheorique()).compareTo(ZERO) <= 0)
            .count();
        List<String> summary = List.of(
            "Perimetre: " + scopeLabel(request),
            "Alertes stock: " + alerts.size(),
            "Ruptures ou stock nul: " + critiques,
            "Ecart total sous seuil: " + decimalString(totalEcart)
        );
        List<List<String>> rows = alerts
            .stream()
            .map(alert ->
                List.of(
                    nullSafe(alert.getBoutiqueNom()),
                    nullSafe(alert.getDepotCode()),
                    String.valueOf(alert.getProduitId()),
                    nullSafe(alert.getProduitDesignation()),
                    decimalString(alert.getQuantiteTheorique()),
                    decimalString(alert.getStockAlerte()),
                    decimalString(maxZero(safe(alert.getStockAlerte()).subtract(safe(alert.getQuantiteTheorique()))))
                )
            )
            .toList();
        List<String> headers = List.of("Boutique", "Depot", "Produit ID", "Produit", "Stock theorique", "Seuil alerte", "Ecart");
        return new ReportDocument(
            "Rapport alertes stock",
            summary,
            headers,
            rows,
            buildPreview("Rapport alertes stock", summary, headers, rows)
        );
    }

    private ReportDocument buildSalesDocument(GenerateRapportExportRequest request, Locataire locataire) {
        LocalDate from = request.getPeriodeDebut() == null ? LocalDate.now().minusDays(6) : request.getPeriodeDebut();
        LocalDate to = request.getPeriodeFin() == null ? LocalDate.now() : request.getPeriodeFin();
        List<Vente> ventes = loadSales(
            from,
            to,
            request.getBoutiqueId(),
            request.getLocataireId(),
            request.getStatutVente(),
            request.getMinMontantNet()
        );
        Map<LocalDate, List<Vente>> ventesParJour = new LinkedHashMap<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            ventesParJour.put(cursor, new ArrayList<>());
            cursor = cursor.plusDays(1);
        }
        ventes.forEach(vente -> {
            LocalDate day = vente.getDateHeure().atZone(ZoneOffset.UTC).toLocalDate();
            ventesParJour.computeIfAbsent(day, ignored -> new ArrayList<>()).add(vente);
        });
        BigDecimal totalBrut = sum(ventes.stream().map(Vente::getMontantBrut).toList());
        BigDecimal totalNet = sum(ventes.stream().map(Vente::getMontantNet).toList());
        BigDecimal totalRemise = totalBrut.subtract(totalNet);
        BigDecimal ticketMoyen = ventes.isEmpty() ? ZERO : totalNet.divide(BigDecimal.valueOf(ventes.size()), 2, RoundingMode.HALF_UP);
        List<String> summary = List.of(
            "Periode: " + from + " -> " + to,
            "Perimetre: " + scopeLabel(request),
            locataire == null ? "Locataire: tous" : "Locataire: " + locataire.getNom(),
            "Statut vente: " + (request.getStatutVente() == null ? "tous" : request.getStatutVente().name()),
            "Montant net minimum: " + (request.getMinMontantNet() == null ? "aucun" : decimalString(request.getMinMontantNet())),
            "Nombre de ventes: " + ventes.size(),
            "Total brut: " + decimalString(totalBrut),
            "Total remise: " + decimalString(totalRemise),
            "Total net: " + decimalString(totalNet),
            "Ticket moyen: " + decimalString(ticketMoyen)
        );
        List<List<String>> rows = ventesParJour
            .entrySet()
            .stream()
            .map(entry -> {
                List<Vente> ventesDuJour = entry.getValue();
                BigDecimal brut = sum(ventesDuJour.stream().map(Vente::getMontantBrut).toList());
                BigDecimal net = sum(ventesDuJour.stream().map(Vente::getMontantNet).toList());
                BigDecimal remise = brut.subtract(net);
                BigDecimal moyen = ventesDuJour.isEmpty()
                    ? ZERO
                    : net.divide(BigDecimal.valueOf(ventesDuJour.size()), 2, RoundingMode.HALF_UP);
                return List.of(
                    entry.getKey().toString(),
                    String.valueOf(ventesDuJour.size()),
                    decimalString(brut),
                    decimalString(remise),
                    decimalString(net),
                    decimalString(moyen)
                );
            })
            .toList();
        List<String> headers = List.of("Jour", "Nb ventes", "Montant brut", "Remise", "Montant net", "Ticket moyen");
        return new ReportDocument(
            "Rapport ventes par jour",
            summary,
            headers,
            rows,
            buildPreview("Rapport ventes par jour", summary, headers, rows)
        );
    }

    private ReportDocument buildRoyaltyDocument(GenerateRapportExportRequest request, Boutique boutique, Locataire locataire) {
        LocalDate from = request.getPeriodeDebut() == null ? LocalDate.now().minusMonths(1) : request.getPeriodeDebut();
        LocalDate to = request.getPeriodeFin() == null ? LocalDate.now() : request.getPeriodeFin();
        Map<Long, BigDecimal> paidByCalcul = new LinkedHashMap<>();
        for (PaiementRedevance paiement : paiementRedevanceRepository.findAllWithEagerRelationships()) {
            paidByCalcul.merge(paiement.getCalcul().getId(), safe(paiement.getMontant()), BigDecimal::add);
        }

        List<CalculRedevance> calculs = calculRedevanceRepository
            .findAllWithEagerRelationships()
            .stream()
            .filter(calcul -> calcul.getStatut() != com.adm.supervision.domain.enumeration.StatutRedevance.ANNULEE)
            .filter(calcul -> isBoutiqueVisible(calcul.getBoutique().getId()))
            .filter(calcul -> boutique == null || Objects.equals(calcul.getBoutique().getId(), boutique.getId()))
            .filter(calcul -> locataire == null || Objects.equals(calcul.getLocataire().getId(), locataire.getId()))
            .filter(calcul -> !calcul.getPeriodeFin().isBefore(from) && !calcul.getPeriodeDebut().isAfter(to))
            .sorted((left, right) -> left.getPeriodeDebut().compareTo(right.getPeriodeDebut()))
            .toList();

        BigDecimal totalCa = ZERO;
        BigDecimal totalDue = ZERO;
        BigDecimal totalPaid = ZERO;
        long soldes = 0;
        long partiels = 0;
        List<List<String>> rows = new ArrayList<>();
        for (CalculRedevance calcul : calculs) {
            BigDecimal paid = safe(paidByCalcul.get(calcul.getId()));
            BigDecimal due = safe(calcul.getMontantRedevance());
            BigDecimal reste = maxZero(due.subtract(paid));
            totalCa = totalCa.add(safe(calcul.getChiffreAffaires()));
            totalDue = totalDue.add(due);
            totalPaid = totalPaid.add(paid);
            if (reste.compareTo(ZERO) == 0 && due.compareTo(ZERO) > 0) {
                soldes++;
            } else if (paid.compareTo(ZERO) > 0) {
                partiels++;
            }
            rows.add(
                List.of(
                    calcul.getReference(),
                    calcul.getPeriodeDebut() + " -> " + calcul.getPeriodeFin(),
                    calcul.getBoutique().getNom(),
                    calcul.getLocataire().getNom(),
                    decimalString(calcul.getChiffreAffaires()),
                    decimalString(due),
                    decimalString(paid),
                    decimalString(reste),
                    calcul.getStatut().name()
                )
            );
        }
        List<String> summary = List.of(
            "Periode: " + from + " -> " + to,
            "Perimetre: " + scopeLabel(request),
            "Calculs: " + calculs.size(),
            "Total CA: " + decimalString(totalCa),
            "Total redevance: " + decimalString(totalDue),
            "Total paye: " + decimalString(totalPaid),
            "Reste: " + decimalString(maxZero(totalDue.subtract(totalPaid))),
            "Soldes: " + soldes,
            "Paiements partiels: " + partiels
        );
        List<String> headers = List.of(
            "Reference",
            "Periode calcul",
            "Boutique",
            "Locataire",
            "CA",
            "Redevance",
            "Paye",
            "Reste",
            "Statut"
        );
        return new ReportDocument("Rapport redevances", summary, headers, rows, buildPreview("Rapport redevances", summary, headers, rows));
    }

    private ReportDocument buildUnknownScanDocument(GenerateRapportExportRequest request) {
        List<ScanInconnu> scans = scanInconnuRepository
            .findAllWithEagerRelationships()
            .stream()
            .filter(scan -> isBoutiqueVisible(scan.getBoutique().getId()))
            .filter(scan -> request.getBoutiqueId() == null || Objects.equals(scan.getBoutique().getId(), request.getBoutiqueId()))
            .filter(
                scan ->
                    request.getProduitId() == null ||
                    (scan.getProduitAffecte() != null && Objects.equals(scan.getProduitAffecte().getId(), request.getProduitId()))
            )
            .filter(
                scan ->
                    request.getPeriodeDebut() == null ||
                    !scan.getDateScan().atZone(ZoneOffset.UTC).toLocalDate().isBefore(request.getPeriodeDebut())
            )
            .filter(
                scan ->
                    request.getPeriodeFin() == null ||
                    !scan.getDateScan().atZone(ZoneOffset.UTC).toLocalDate().isAfter(request.getPeriodeFin())
            )
            .sorted((left, right) -> left.getDateScan().compareTo(right.getDateScan()))
            .toList();
        long nonResolus = scans
            .stream()
            .filter(scan -> !Boolean.TRUE.equals(scan.getResolu()))
            .count();
        List<String> summary = List.of(
            "Perimetre: " + scopeLabel(request),
            "Scans inconnus: " + scans.size(),
            "Non resolus: " + nonResolus,
            "Resolus: " + (scans.size() - nonResolus)
        );
        List<List<String>> rows = scans
            .stream()
            .map(scan ->
                List.of(
                    scan.getDateScan().atZone(ZoneOffset.UTC).toString(),
                    scan.getCodeScanne(),
                    scan.getBoutique().getNom(),
                    scan.getProduitAffecte() == null ? "" : scan.getProduitAffecte().getDesignation(),
                    Boolean.TRUE.equals(scan.getResolu()) ? "RESOLU" : "NON RESOLU"
                )
            )
            .toList();
        List<String> headers = List.of("Date scan", "Code", "Boutique", "Produit affecte", "Statut");
        return new ReportDocument(
            "Rapport scans inconnus",
            summary,
            headers,
            rows,
            buildPreview("Rapport scans inconnus", summary, headers, rows)
        );
    }

    private String buildExportReference(String typeRapport) {
        return "RPT-" + normalizeReportType(typeRapport).toUpperCase(Locale.ROOT) + "-" + Instant.now().toEpochMilli();
    }

    private String buildExportFileName(String typeRapport, String extensionKey) {
        String extension = "excel".equals(extensionKey) ? "xlsx" : "pdf";
        return normalizeReportType(typeRapport) + "-" + Instant.now().toEpochMilli() + "." + extension;
    }

    private String normalizeReportType(String typeRapport) {
        String normalized = typeRapport == null ? "rapport" : typeRapport.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        normalized = normalized.replaceAll("(^-+|-+$)", "");
        return normalized.isBlank() ? "rapport" : normalized;
    }

    private String buildPreview(String title, List<String> summary, List<String> headers, List<List<String>> rows) {
        List<String> lines = new ArrayList<>();
        lines.add(title);
        lines.addAll(summary);
        lines.add(String.join(" | ", headers));
        int previewRowCount = Math.min(rows.size(), 10);
        for (int i = 0; i < previewRowCount; i++) {
            lines.add(String.join(" | ", rows.get(i)));
        }
        if (rows.size() > previewRowCount) {
            lines.add("... " + (rows.size() - previewRowCount) + " lignes supplementaires");
        }
        return String.join("\n", lines);
    }

    private void validatePeriodRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new BusinessValidationException("rapportExport", "invalidPeriod", "La periode de debut doit preceder la periode de fin");
        }
    }

    private void assertBoutiqueAccess(Long boutiqueId, String message) {
        if (boutiqueId != null) {
            moduleSecurityService.assertBoutiqueAccess(boutiqueId, message);
        }
    }

    private boolean isBoutiqueVisible(Long boutiqueId) {
        return moduleSecurityService.hasGlobalBoutiqueAccess() || moduleSecurityService.getAccessibleBoutiqueIds().contains(boutiqueId);
    }

    private void applyExportScope(GenerateRapportExportRequest request) {
        if (moduleSecurityService.isCurrentUserLocataire()) {
            Long locataireId = moduleSecurityService.getCurrentLocataireIdOrNull();
            if (locataireId == null) {
                throw new org.springframework.security.access.AccessDeniedException("Acces refuse a l'export demande");
            }
            request.setLocataireId(locataireId);
        }
        if (!moduleSecurityService.hasGlobalBoutiqueAccess() && request.getBoutiqueId() == null) {
            List<Long> accessibleBoutiqueIds = new ArrayList<>(moduleSecurityService.getAccessibleBoutiqueIds());
            if (accessibleBoutiqueIds.size() == 1) {
                request.setBoutiqueId(accessibleBoutiqueIds.get(0));
            }
        }
    }

    private void assertReportTypeAllowed(GenerateRapportExportRequest request) {
        String normalizedType = normalizeReportType(request.getTypeRapport());
        boolean allowed =
            normalizedType.contains("vente") ||
            ((normalizedType.contains("redevance") || normalizedType.contains("royalty")) && moduleSecurityService.canReadRoyalties()) ||
            (normalizedType.contains("stock") && moduleSecurityService.canReadStock()) ||
            (normalizedType.contains("scan") && moduleSecurityService.canReadAudit());
        if (!allowed || !moduleSecurityService.canAccessReportingExports()) {
            throw new org.springframework.security.access.AccessDeniedException("Acces refuse au type de rapport demande");
        }
    }

    private String scopeLabel(GenerateRapportExportRequest request) {
        List<String> parts = new ArrayList<>();
        if (request.getBoutiqueId() != null) {
            parts.add("boutique #" + request.getBoutiqueId());
        } else if (!moduleSecurityService.hasGlobalBoutiqueAccess()) {
            parts.add("boutiques accessibles");
        } else {
            parts.add("toutes boutiques");
        }
        if (request.getLocataireId() != null) {
            parts.add("locataire #" + request.getLocataireId());
        }
        if (request.getDepotId() != null) {
            parts.add("depot #" + request.getDepotId());
        }
        if (request.getProduitId() != null) {
            parts.add("produit #" + request.getProduitId());
        }
        return String.join(", ", parts);
    }

    private boolean isSaleVisibleForCurrentSeller(Vente vente) {
        if (moduleSecurityService.hasGlobalBoutiqueAccess()) {
            return true;
        }
        User currentUser = moduleSecurityService.getCurrentUser();
        boolean currentUserIsSeller = currentUser
            .getAuthorities()
            .stream()
            .anyMatch(authority -> "ROLE_VENDEUR".equals(authority.getName()));
        if (!currentUserIsSeller) {
            return true;
        }
        return vente.getVendeur() != null && Objects.equals(vente.getVendeur().getId(), currentUser.getId());
    }

    private void auditExportAccess(String description, RapportExport export) {
        journalAuditService.logAction(
            TypeActionAudit.EXPORT,
            "RapportExport",
            export.getReference(),
            description,
            export.getBoutique(),
            moduleSecurityService.getCurrentUser()
        );
    }

    private BigDecimal sum(List<BigDecimal> values) {
        return values.stream().filter(Objects::nonNull).reduce(ZERO, BigDecimal::add);
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? ZERO : value;
    }

    private BigDecimal maxZero(BigDecimal value) {
        return value.compareTo(ZERO) < 0 ? ZERO : value;
    }

    private String decimalString(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
