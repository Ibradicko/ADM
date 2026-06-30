package com.adm.supervision.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.GroupeArticle;
import com.adm.supervision.domain.LigneVente;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.domain.PaiementRedevance;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.RapportExport;
import com.adm.supervision.domain.ScanInconnu;
import com.adm.supervision.domain.StockProduit;
import com.adm.supervision.domain.UniteMesure;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.FormatExport;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.StatutRedevance;
import com.adm.supervision.domain.enumeration.StatutVente;
import com.adm.supervision.domain.enumeration.TypeBoutique;
import com.adm.supervision.domain.enumeration.TypeLocataire;
import com.adm.supervision.domain.enumeration.TypePrix;
import com.adm.supervision.repository.RapportExportRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.service.dto.GenerateRapportExportRequest;
import com.adm.supervision.service.dto.RapportExportPreviewDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
class DashboardReportingResourceIT {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RapportExportRepository rapportExportRepository;

    private User adminUser;

    @BeforeEach
    void init() {
        adminUser = userRepository.findOneByLogin("admin").orElseThrow();
    }

    @Test
    @Transactional
    void overviewShouldReturnAggregatedMetrics() throws Exception {
        Boutique boutique = persistBoutique("DBO");
        Locataire locataire = persistLocataire("DBO");
        Produit produit = persistProduit(boutique, "DBO");
        persistStock(produit, boutique, "2", "5");
        persistValidatedSale(boutique, locataire, new BigDecimal("100"), new BigDecimal("90"), LocalDate.now());
        persistDraftSale(boutique, locataire, new BigDecimal("40"), new BigDecimal("40"), LocalDate.now());
        persistUnknownScan(boutique, produit);
        CalculRedevance calcul = persistCalcul(boutique, locataire, "30");
        persistPaiement(calcul, "10");

        restMockMvc
            .perform(get("/api/dashboard/overview").param("boutiqueId", boutique.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.validatedSalesCount").value(1))
            .andExpect(jsonPath("$.pendingSalesCount").value(1))
            .andExpect(jsonPath("$.grossSales").value(100))
            .andExpect(jsonPath("$.netSales").value(90))
            .andExpect(jsonPath("$.stockAlertCount").value(1))
            .andExpect(jsonPath("$.unresolvedUnknownScans").value(1))
            .andExpect(jsonPath("$.royaltyOutstandingAmount").value(20));
    }

    @Test
    @Transactional
    void salesByDayShouldReturnTimeline() throws Exception {
        Boutique boutique = persistBoutique("DBS");
        Locataire locataire = persistLocataire("DBS");
        persistValidatedSale(boutique, locataire, new BigDecimal("20"), new BigDecimal("18"), LocalDate.now().minusDays(1));
        persistValidatedSale(boutique, locataire, new BigDecimal("30"), new BigDecimal("27"), LocalDate.now());

        restMockMvc
            .perform(
                get("/api/dashboard/sales-by-day")
                    .param("boutiqueId", boutique.getId().toString())
                    .param("from", LocalDate.now().minusDays(1).toString())
                    .param("to", LocalDate.now().toString())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].validatedSalesCount").value(1))
            .andExpect(jsonPath("$[1].validatedSalesCount").value(1));
    }

    @Test
    @Transactional
    void redevancesParGroupeArticleShouldAggregateByGroupUsingProductRate() throws Exception {
        Boutique boutique = persistBoutique("DBG");
        Locataire locataire = persistLocataire("DBG");
        GroupeArticle groupe = persistGroupeArticle("DBG");
        Produit produit = persistProduit(boutique, "DBG");
        produit.setGroupeArticle(groupe);
        produit.setTauxRedevanceApplicable(new BigDecimal("10"));
        em.persist(produit);
        Vente vente = persistValidatedSale(boutique, locataire, new BigDecimal("100"), new BigDecimal("100"), LocalDate.now());
        persistLigneVente(vente, produit, new BigDecimal("100"));

        restMockMvc
            .perform(get("/api/dashboard/redevances-par-groupe-article").param("boutiqueId", boutique.getId().toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].groupeArticleId").value(groupe.getId()))
            .andExpect(jsonPath("$[0].chiffreAffaires").value(100))
            .andExpect(jsonPath("$[0].montantRedevance").value(10))
            .andExpect(jsonPath("$[0].tauxEffectif").value(10));
    }

    @Test
    @Transactional
    void stockAlertsShouldReturnCriticalRows() throws Exception {
        Boutique boutique = persistBoutique("DBA");
        Produit produit = persistProduit(boutique, "DBA");
        var matchingStock = persistStock(produit, boutique, "1", "3");
        Boutique otherBoutique = persistBoutique("DBA-OTHER");
        Produit otherProduit = persistProduit(otherBoutique, "DBA-OTHER");
        persistStock(otherProduit, otherBoutique, "1", "3");

        restMockMvc
            .perform(
                get("/api/dashboard/stock-alerts")
                    .param("boutiqueId", boutique.getId().toString())
                    .param("depotId", matchingStock.getDepot().getId().toString())
                    .param("produitId", produit.getId().toString())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].produitDesignation").value(produit.getDesignation()));
    }

    @Test
    @Transactional
    void generatePreviewAndDownloadPdfExportShouldPersistPhysicalFile() throws Exception {
        Boutique boutique = persistBoutique("RPT");
        Produit produit = persistProduit(boutique, "RPT");
        persistStock(produit, boutique, "1", "4");

        GenerateRapportExportRequest request = new GenerateRapportExportRequest();
        request.setTypeRapport("stock_alerts");
        request.setFormat(FormatExport.PDF);
        request.setBoutiqueId(boutique.getId());
        request.setPeriodeDebut(LocalDate.now().minusDays(3));
        request.setPeriodeFin(LocalDate.now());

        RapportExportPreviewDTO preview = om.readValue(
            restMockMvc
                .perform(
                    post("/api/reporting/exports/generate").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RapportExportPreviewDTO.class
        );

        assertThat(preview.getExport()).isNotNull();
        assertThat(preview.getPreview()).contains("Alertes stock: 1");

        RapportExport export = rapportExportRepository.findById(preview.getExport().getId()).orElseThrow();
        restMockMvc.perform(get("/api/reporting/exports/{rapportExportId}/preview", export.getId())).andExpect(status().isOk());

        byte[] pdfContent = restMockMvc
            .perform(get("/api/reporting/exports/{rapportExportId}/download", export.getId()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();
        assertThat(new String(pdfContent, 0, 4, java.nio.charset.StandardCharsets.US_ASCII)).isEqualTo("%PDF");
    }

    @Test
    @Transactional
    void generateExcelExportShouldSupportLocataireAndAmountFilters() throws Exception {
        Boutique boutique = persistBoutique("XL");
        Locataire locataireA = persistLocataire("XL-A");
        Locataire locataireB = persistLocataire("XL-B");
        persistValidatedSale(boutique, locataireA, new BigDecimal("100"), new BigDecimal("90"), LocalDate.now());
        persistValidatedSale(boutique, locataireB, new BigDecimal("20"), new BigDecimal("18"), LocalDate.now());

        GenerateRapportExportRequest request = new GenerateRapportExportRequest();
        request.setTypeRapport("sales_by_day");
        request.setFormat(FormatExport.EXCEL);
        request.setBoutiqueId(boutique.getId());
        request.setLocataireId(locataireA.getId());
        request.setPeriodeDebut(LocalDate.now().minusDays(1));
        request.setPeriodeFin(LocalDate.now());
        request.setMinMontantNet(new BigDecimal("50"));

        RapportExportPreviewDTO preview = om.readValue(
            restMockMvc
                .perform(
                    post("/api/reporting/exports/generate").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RapportExportPreviewDTO.class
        );

        assertThat(preview.getPreview()).contains("Locataire: " + locataireA.getNom());
        assertThat(preview.getPreview()).contains("90");

        byte[] excelContent = restMockMvc
            .perform(get("/api/reporting/exports/{rapportExportId}/download", preview.getExport().getId()))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();
        assertThat(excelContent[0]).isEqualTo((byte) 'P');
        assertThat(excelContent[1]).isEqualTo((byte) 'K');
    }

    private Boutique persistBoutique(String prefix) {
        Boutique boutique = new Boutique()
            .code(unique(prefix + "-B"))
            .nom(unique(prefix + "-Boutique"))
            .type(TypeBoutique.COMMERCE)
            .statut(StatutGeneral.ACTIF)
            .dateCreation(Instant.now());
        em.persist(boutique);
        return boutique;
    }

    private Locataire persistLocataire(String prefix) {
        Locataire locataire = new Locataire()
            .code(unique(prefix + "-L"))
            .nom(unique(prefix + "-Locataire"))
            .typeLocataire(TypeLocataire.PERSONNE_MORALE)
            .statut(StatutGeneral.ACTIF)
            .dateCreation(Instant.now());
        em.persist(locataire);
        return locataire;
    }

    private Produit persistProduit(Boutique boutique, String prefix) {
        UniteMesure uniteMesure = new UniteMesure().code(unique(prefix + "-UM")).libelle("Piece " + unique(prefix + "-LIB"));
        em.persist(uniteMesure);
        Produit produit = new Produit()
            .codeInterne(unique(prefix + "-CODE"))
            .designation(unique(prefix + "-Produit"))
            .typePrix(TypePrix.STANDARD)
            .prixVente(new BigDecimal("10"))
            .statut(StatutGeneral.ACTIF)
            .dateCreation(Instant.now())
            .boutique(boutique)
            .uniteMesure(uniteMesure);
        em.persist(produit);
        return produit;
    }

    private GroupeArticle persistGroupeArticle(String prefix) {
        GroupeArticle groupeArticle = new GroupeArticle()
            .code(unique(prefix + "-G"))
            .libelle(unique(prefix + "-Groupe"))
            .statut(StatutGeneral.ACTIF);
        em.persist(groupeArticle);
        return groupeArticle;
    }

    private LigneVente persistLigneVente(Vente vente, Produit produit, BigDecimal montantLigne) {
        LigneVente ligneVente = new LigneVente()
            .quantite(BigDecimal.ONE)
            .prixUnitaire(montantLigne)
            .montantLigne(montantLigne)
            .vente(vente)
            .produit(produit);
        em.persist(ligneVente);
        return ligneVente;
    }

    private StockProduit persistStock(Produit produit, Boutique boutique, String quantity, String alert) {
        var depot = new com.adm.supervision.domain.DepotStock()
            .code(unique("DEP"))
            .libelle(unique("DEP-LIB"))
            .actif(true)
            .boutique(boutique);
        em.persist(depot);
        StockProduit stockProduit = new StockProduit()
            .produit(produit)
            .depot(depot)
            .quantiteTheorique(new BigDecimal(quantity))
            .stockAlerte(new BigDecimal(alert))
            .dateDernierMouvement(Instant.now());
        em.persist(stockProduit);
        return stockProduit;
    }

    private Vente persistValidatedSale(Boutique boutique, Locataire locataire, BigDecimal brut, BigDecimal net, LocalDate day) {
        Vente vente = baseSale(boutique, locataire, brut, net, day);
        vente.setStatut(StatutVente.VALIDEE);
        em.persist(vente);
        return vente;
    }

    private Vente persistDraftSale(Boutique boutique, Locataire locataire, BigDecimal brut, BigDecimal net, LocalDate day) {
        Vente vente = baseSale(boutique, locataire, brut, net, day);
        vente.setStatut(StatutVente.BROUILLON);
        em.persist(vente);
        return vente;
    }

    private Vente baseSale(Boutique boutique, Locataire locataire, BigDecimal brut, BigDecimal net, LocalDate day) {
        return new Vente()
            .numeroTicket(unique("VTE"))
            .dateHeure(day.atStartOfDay().toInstant(java.time.ZoneOffset.UTC))
            .statut(StatutVente.BROUILLON)
            .montantBrut(brut)
            .montantRemise(brut.subtract(net))
            .montantNet(net)
            .boutique(boutique)
            .locataire(locataire)
            .vendeur(adminUser);
    }

    private ScanInconnu persistUnknownScan(Boutique boutique, Produit produit) {
        ScanInconnu scanInconnu = new ScanInconnu()
            .codeScanne(unique("UNK"))
            .dateScan(Instant.now())
            .resolu(false)
            .boutique(boutique)
            .produitAffecte(produit);
        em.persist(scanInconnu);
        return scanInconnu;
    }

    private CalculRedevance persistCalcul(Boutique boutique, Locataire locataire, String amount) {
        CalculRedevance calculRedevance = new CalculRedevance()
            .reference(unique("CAL"))
            .periodeDebut(LocalDate.now().minusDays(30))
            .periodeFin(LocalDate.now())
            .chiffreAffaires(new BigDecimal("100"))
            .montantRedevance(new BigDecimal(amount))
            .statut(StatutRedevance.CALCULEE)
            .dateCalcul(Instant.now())
            .boutique(boutique)
            .locataire(locataire);
        em.persist(calculRedevance);
        return calculRedevance;
    }

    private PaiementRedevance persistPaiement(CalculRedevance calculRedevance, String amount) {
        PaiementRedevance paiementRedevance = new PaiementRedevance()
            .reference(unique("PAY"))
            .montant(new BigDecimal(amount))
            .datePaiement(LocalDate.now())
            .calcul(calculRedevance);
        em.persist(paiementRedevance);
        return paiementRedevance;
    }

    private String unique(String prefix) {
        return prefix + "-" + COUNTER.incrementAndGet();
    }
}
