package com.adm.supervision.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.GroupeArticle;
import com.adm.supervision.domain.LigneCalculRedevance;
import com.adm.supervision.domain.LigneVente;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.RegleRedevance;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.StatutRedevance;
import com.adm.supervision.domain.enumeration.StatutVente;
import com.adm.supervision.domain.enumeration.TypeRegleRedevance;
import com.adm.supervision.repository.BoutiqueRepository;
import com.adm.supervision.repository.CalculRedevanceRepository;
import com.adm.supervision.repository.ExploitationBoutiqueRepository;
import com.adm.supervision.repository.LigneCalculRedevanceRepository;
import com.adm.supervision.repository.LigneVenteRepository;
import com.adm.supervision.repository.LocataireRepository;
import com.adm.supervision.repository.RegleRedevanceRepository;
import com.adm.supervision.repository.VenteRepository;
import com.adm.supervision.service.dto.GenerateCalculRedevanceRequest;
import com.adm.supervision.service.mapper.CalculRedevanceMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RedevanceWorkflowServiceTest {

    @Mock
    private VenteRepository venteRepository;

    @Mock
    private LigneVenteRepository ligneVenteRepository;

    @Mock
    private RegleRedevanceRepository regleRedevanceRepository;

    @Mock
    private ExploitationBoutiqueRepository exploitationBoutiqueRepository;

    @Mock
    private CalculRedevanceRepository calculRedevanceRepository;

    @Mock
    private LigneCalculRedevanceRepository ligneCalculRedevanceRepository;

    @Mock
    private BoutiqueRepository boutiqueRepository;

    @Mock
    private LocataireRepository locataireRepository;

    @Mock
    private CalculRedevanceMapper calculRedevanceMapper;

    @Mock
    private ModuleSecurityService moduleSecurityService;

    @Mock
    private JournalAuditService journalAuditService;

    private RedevanceWorkflowService service;

    @BeforeEach
    void setUp() {
        service = new RedevanceWorkflowService(
            venteRepository,
            ligneVenteRepository,
            regleRedevanceRepository,
            exploitationBoutiqueRepository,
            calculRedevanceRepository,
            ligneCalculRedevanceRepository,
            boutiqueRepository,
            locataireRepository,
            calculRedevanceMapper,
            moduleSecurityService,
            journalAuditService,
            new RedevanceRateResolver()
        );
    }

    @Test
    void lowerPriorityNumberWinsBeforeSpecificity() {
        Boutique shop = new Boutique().id(1L);
        Locataire tenant = new Locataire().id(2L);
        GroupeArticle group = new GroupeArticle().id(3L);
        Produit product = new Produit().id(4L).groupeArticle(group);
        Vente sale = new Vente().boutique(shop).locataire(tenant);
        LocalDate date = LocalDate.of(2026, 5, 15);

        RegleRedevance shopRule = rule(10L, TypeRegleRedevance.BOUTIQUE, 1, "5").boutique(shop);
        RegleRedevance productRule = rule(11L, TypeRegleRedevance.PRODUIT, 2, "12").produit(product);

        BigDecimal rate = service.resolveRate(List.of(productRule, shopRule), List.of(), product, sale, date);

        assertThat(rate).isEqualByComparingTo("5");
    }

    @Test
    void specificityBreaksEqualPriorityAndPeriodIsRespected() {
        Boutique shop = new Boutique().id(1L);
        Locataire tenant = new Locataire().id(2L);
        GroupeArticle group = new GroupeArticle().id(3L);
        Produit product = new Produit().id(4L).groupeArticle(group);
        Vente sale = new Vente().boutique(shop).locataire(tenant);
        LocalDate date = LocalDate.of(2026, 5, 15);

        RegleRedevance groupRule = rule(10L, TypeRegleRedevance.GROUPE_ARTICLE, 1, "7").groupeArticle(group);
        RegleRedevance productRule = rule(11L, TypeRegleRedevance.PRODUIT, 1, "12").produit(product);
        RegleRedevance expiredProductRule = rule(12L, TypeRegleRedevance.PRODUIT, 0, "20")
            .produit(product)
            .dateFin(LocalDate.of(2026, 4, 30));

        BigDecimal rate = service.resolveRate(List.of(groupRule, productRule, expiredProductRule), List.of(), product, sale, date);

        assertThat(rate).isEqualByComparingTo("12");
    }

    @Test
    void ignoresRulesBelongingToAnotherShopOrTenant() {
        Boutique requestedShop = new Boutique().id(1L);
        Boutique otherShop = new Boutique().id(9L);
        Locataire requestedTenant = new Locataire().id(2L);
        Locataire otherTenant = new Locataire().id(8L);
        Produit product = new Produit().id(4L).tauxRedevanceApplicable(new BigDecimal("3"));
        Vente sale = new Vente().boutique(requestedShop).locataire(requestedTenant);
        LocalDate date = LocalDate.of(2026, 5, 15);

        RegleRedevance otherShopRule = rule(10L, TypeRegleRedevance.BOUTIQUE, 1, "20").boutique(otherShop);
        RegleRedevance otherTenantRule = rule(11L, TypeRegleRedevance.LOCATAIRE, 1, "15").locataire(otherTenant);

        BigDecimal rate = service.resolveRate(List.of(otherShopRule, otherTenantRule), List.of(), product, sale, date);

        assertThat(rate).isEqualByComparingTo("3");
    }

    @Test
    void generatesOnlyForRequestedShopAndTenantAndCancelsPreviousDraft() {
        Boutique shop = new Boutique().id(1L).nom("Shop A");
        Locataire tenant = new Locataire().id(2L).nom("Tenant A");
        Produit product = new Produit().id(4L).tauxRedevanceApplicable(new BigDecimal("10"));
        Vente sale = new Vente()
            .id(5L)
            .dateHeure(Instant.parse("2026-05-10T10:00:00Z"))
            .statut(StatutVente.VALIDEE)
            .boutique(shop)
            .locataire(tenant);
        LigneVente line = new LigneVente().vente(sale).produit(product).montantLigne(new BigDecimal("1000"));
        CalculRedevance previous = new CalculRedevance().id(6L).statut(StatutRedevance.CALCULEE);
        GenerateCalculRedevanceRequest request = request();

        when(boutiqueRepository.findById(1L)).thenReturn(Optional.of(shop));
        when(locataireRepository.findById(2L)).thenReturn(Optional.of(tenant));
        when(venteRepository.findAllByDateHeureBetweenAndBoutique_IdAndLocataire_IdAndStatut(any(), any(), any(), any(), any())).thenReturn(
            List.of(sale)
        );
        when(regleRedevanceRepository.findAllWithEagerRelationships()).thenReturn(List.of());
        when(exploitationBoutiqueRepository.findAllWithEagerRelationships()).thenReturn(List.of());
        when(
            calculRedevanceRepository.findAllByBoutique_IdAndLocataire_IdAndPeriodeDebutAndPeriodeFin(
                1L,
                2L,
                request.getPeriodeDebut(),
                request.getPeriodeFin()
            )
        ).thenReturn(List.of(previous));
        when(calculRedevanceRepository.save(any(CalculRedevance.class))).thenAnswer(invocation -> {
            CalculRedevance calculation = invocation.getArgument(0);
            calculation.setId(20L);
            return calculation;
        });
        when(ligneVenteRepository.findAllByVente_Id(5L)).thenReturn(List.of(line));
        when(ligneCalculRedevanceRepository.save(any(LigneCalculRedevance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.generate(request);

        assertThat(previous.getStatut()).isEqualTo(StatutRedevance.ANNULEE);
        verify(venteRepository).findAllByDateHeureBetweenAndBoutique_IdAndLocataire_IdAndStatut(
            any(),
            any(),
            org.mockito.ArgumentMatchers.eq(1L),
            org.mockito.ArgumentMatchers.eq(2L),
            org.mockito.ArgumentMatchers.eq(StatutVente.VALIDEE)
        );
        ArgumentCaptor<CalculRedevance> calculationCaptor = ArgumentCaptor.forClass(CalculRedevance.class);
        verify(calculRedevanceRepository, org.mockito.Mockito.atLeastOnce()).save(calculationCaptor.capture());
        CalculRedevance result = calculationCaptor.getValue();
        assertThat(result.getChiffreAffaires()).isEqualByComparingTo("1000");
        assertThat(result.getMontantRedevance()).isEqualByComparingTo("100");
        assertThat(result.getBoutique().getId()).isEqualTo(1L);
        assertThat(result.getLocataire().getId()).isEqualTo(2L);
    }

    private RegleRedevance rule(Long id, TypeRegleRedevance type, int priority, String rate) {
        return new RegleRedevance()
            .id(id)
            .typeRegle(type)
            .priorite(priority)
            .taux(new BigDecimal(rate))
            .dateDebut(LocalDate.of(2026, 1, 1))
            .actif(true);
    }

    private GenerateCalculRedevanceRequest request() {
        GenerateCalculRedevanceRequest request = new GenerateCalculRedevanceRequest();
        request.setBoutiqueId(1L);
        request.setLocataireId(2L);
        request.setPeriodeDebut(LocalDate.of(2026, 5, 1));
        request.setPeriodeFin(LocalDate.of(2026, 5, 31));
        return request;
    }
}
