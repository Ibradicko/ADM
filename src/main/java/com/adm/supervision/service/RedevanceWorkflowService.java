package com.adm.supervision.service;

import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.ExploitationBoutique;
import com.adm.supervision.domain.LigneCalculRedevance;
import com.adm.supervision.domain.LigneVente;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.RegleRedevance;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.StatutRedevance;
import com.adm.supervision.domain.enumeration.StatutVente;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.BoutiqueRepository;
import com.adm.supervision.repository.CalculRedevanceRepository;
import com.adm.supervision.repository.ExploitationBoutiqueRepository;
import com.adm.supervision.repository.LigneCalculRedevanceRepository;
import com.adm.supervision.repository.LigneVenteRepository;
import com.adm.supervision.repository.LocataireRepository;
import com.adm.supervision.repository.RegleRedevanceRepository;
import com.adm.supervision.repository.VenteRepository;
import com.adm.supervision.service.dto.CalculRedevanceDTO;
import com.adm.supervision.service.dto.GenerateCalculRedevanceRequest;
import com.adm.supervision.service.mapper.CalculRedevanceMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RedevanceWorkflowService {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private final VenteRepository venteRepository;
    private final LigneVenteRepository ligneVenteRepository;
    private final RegleRedevanceRepository regleRedevanceRepository;
    private final ExploitationBoutiqueRepository exploitationBoutiqueRepository;
    private final CalculRedevanceRepository calculRedevanceRepository;
    private final LigneCalculRedevanceRepository ligneCalculRedevanceRepository;
    private final BoutiqueRepository boutiqueRepository;
    private final LocataireRepository locataireRepository;
    private final CalculRedevanceMapper calculRedevanceMapper;
    private final ModuleSecurityService moduleSecurityService;
    private final JournalAuditService journalAuditService;
    private final RedevanceRateResolver redevanceRateResolver;

    public RedevanceWorkflowService(
        VenteRepository venteRepository,
        LigneVenteRepository ligneVenteRepository,
        RegleRedevanceRepository regleRedevanceRepository,
        ExploitationBoutiqueRepository exploitationBoutiqueRepository,
        CalculRedevanceRepository calculRedevanceRepository,
        LigneCalculRedevanceRepository ligneCalculRedevanceRepository,
        BoutiqueRepository boutiqueRepository,
        LocataireRepository locataireRepository,
        CalculRedevanceMapper calculRedevanceMapper,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService,
        RedevanceRateResolver redevanceRateResolver
    ) {
        this.venteRepository = venteRepository;
        this.ligneVenteRepository = ligneVenteRepository;
        this.regleRedevanceRepository = regleRedevanceRepository;
        this.exploitationBoutiqueRepository = exploitationBoutiqueRepository;
        this.calculRedevanceRepository = calculRedevanceRepository;
        this.ligneCalculRedevanceRepository = ligneCalculRedevanceRepository;
        this.boutiqueRepository = boutiqueRepository;
        this.locataireRepository = locataireRepository;
        this.calculRedevanceMapper = calculRedevanceMapper;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
        this.redevanceRateResolver = redevanceRateResolver;
    }

    public CalculRedevanceDTO generate(GenerateCalculRedevanceRequest request) {
        validateRequest(request);
        moduleSecurityService.assertBoutiqueAccess(request.getBoutiqueId(), "Acces refuse au calcul de redevance demande");

        var boutique = boutiqueRepository
            .findById(request.getBoutiqueId())
            .orElseThrow(() -> new BusinessValidationException("calculRedevance", "boutiqueNotFound", "Boutique introuvable"));
        var locataire = locataireRepository
            .findById(request.getLocataireId())
            .orElseThrow(() -> new BusinessValidationException("calculRedevance", "locataireNotFound", "Locataire introuvable"));

        Instant start = request.getPeriodeDebut().atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = request.getPeriodeFin().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusNanos(1);
        List<Vente> ventes = venteRepository.findAllByDateHeureBetweenAndBoutique_IdAndLocataire_IdAndStatut(
            start,
            end,
            request.getBoutiqueId(),
            request.getLocataireId(),
            StatutVente.VALIDEE
        );
        List<RegleRedevance> rules = regleRedevanceRepository.findAllWithEagerRelationships();
        List<ExploitationBoutique> exploitations = exploitationBoutiqueRepository.findAllWithEagerRelationships();

        cancelPreviousDraftCalculations(request);

        CalculRedevance calcul = new CalculRedevance()
            .reference(buildReference(request))
            .periodeDebut(request.getPeriodeDebut())
            .periodeFin(request.getPeriodeFin())
            .chiffreAffaires(BigDecimal.ZERO)
            .montantRedevance(BigDecimal.ZERO)
            .statut(StatutRedevance.CALCULEE)
            .dateCalcul(Instant.now())
            .boutique(boutique)
            .locataire(locataire);
        calcul = calculRedevanceRepository.save(calcul);

        BigDecimal totalBase = BigDecimal.ZERO;
        BigDecimal totalRoyalty = BigDecimal.ZERO;
        for (Vente vente : ventes) {
            BigDecimal saleBase = BigDecimal.ZERO;
            BigDecimal saleRoyalty = BigDecimal.ZERO;
            LocalDate saleDate = vente.getDateHeure().atZone(ZoneOffset.UTC).toLocalDate();
            for (LigneVente line : ligneVenteRepository.findAllByVente_Id(vente.getId())) {
                BigDecimal base = safe(line.getMontantLigne());
                BigDecimal rate = resolveRate(rules, exploitations, line.getProduit(), vente, saleDate);
                saleBase = saleBase.add(base);
                saleRoyalty = saleRoyalty.add(base.multiply(rate).divide(HUNDRED, 2, RoundingMode.HALF_UP));
            }
            if (saleBase.signum() > 0) {
                BigDecimal effectiveRate = saleRoyalty.multiply(HUNDRED).divide(saleBase, 2, RoundingMode.HALF_UP);
                ligneCalculRedevanceRepository.save(
                    new LigneCalculRedevance()
                        .calcul(calcul)
                        .vente(vente)
                        .baseCalcul(saleBase)
                        .tauxApplique(effectiveRate)
                        .montantRedevance(saleRoyalty)
                );
            }
            totalBase = totalBase.add(saleBase);
            totalRoyalty = totalRoyalty.add(saleRoyalty);
        }

        calcul.setChiffreAffaires(totalBase);
        calcul.setMontantRedevance(totalRoyalty);
        calcul = calculRedevanceRepository.save(calcul);
        journalAuditService.logAction(
            TypeActionAudit.CREATION,
            "CalculRedevance",
            calcul.getReference(),
            "Calcul automatique redevance ventes=" + ventes.size() + ", ca=" + totalBase + ", montant=" + totalRoyalty,
            boutique,
            moduleSecurityService.getCurrentUser()
        );
        return calculRedevanceMapper.toDto(calcul);
    }

    BigDecimal resolveRate(
        List<RegleRedevance> rules,
        List<ExploitationBoutique> exploitations,
        Produit produit,
        Vente vente,
        LocalDate saleDate
    ) {
        return redevanceRateResolver.resolveRate(rules, exploitations, produit, vente, saleDate);
    }

    private void cancelPreviousDraftCalculations(GenerateCalculRedevanceRequest request) {
        calculRedevanceRepository
            .findAllByBoutique_IdAndLocataire_IdAndPeriodeDebutAndPeriodeFin(
                request.getBoutiqueId(),
                request.getLocataireId(),
                request.getPeriodeDebut(),
                request.getPeriodeFin()
            )
            .stream()
            .filter(calcul -> calcul.getStatut() != StatutRedevance.PAYEE && calcul.getStatut() != StatutRedevance.PARTIELLEMENT_PAYEE)
            .forEach(calcul -> calcul.setStatut(StatutRedevance.ANNULEE));
    }

    private void validateRequest(GenerateCalculRedevanceRequest request) {
        if (request.getPeriodeDebut().isAfter(request.getPeriodeFin())) {
            throw new BusinessValidationException("calculRedevance", "invalidPeriod", "La date de debut doit preceder la date de fin");
        }
    }

    private String buildReference(GenerateCalculRedevanceRequest request) {
        return (
            "RED-" +
            request.getBoutiqueId() +
            "-" +
            request.getLocataireId() +
            "-" +
            request.getPeriodeDebut() +
            "-" +
            Instant.now().toEpochMilli()
        );
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
