package com.adm.supervision.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.CodeBarresProduit;
import com.adm.supervision.domain.DepotStock;
import com.adm.supervision.domain.InventaireStock;
import com.adm.supervision.domain.LigneInventaireStock;
import com.adm.supervision.domain.LigneReceptionProduit;
import com.adm.supervision.domain.LigneTransfertStock;
import com.adm.supervision.domain.MouvementStock;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.ReceptionProduit;
import com.adm.supervision.domain.StockProduit;
import com.adm.supervision.domain.TransfertStock;
import com.adm.supervision.domain.UniteMesure;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.StatutInventaire;
import com.adm.supervision.domain.enumeration.StatutMouvementStock;
import com.adm.supervision.domain.enumeration.TypeBoutique;
import com.adm.supervision.domain.enumeration.TypeCodeBarres;
import com.adm.supervision.domain.enumeration.TypeInventaire;
import com.adm.supervision.domain.enumeration.TypePrix;
import com.adm.supervision.repository.CodeBarresProduitRepository;
import com.adm.supervision.repository.DepotStockRepository;
import com.adm.supervision.repository.InventaireStockRepository;
import com.adm.supervision.repository.LigneInventaireStockRepository;
import com.adm.supervision.repository.LigneMouvementStockRepository;
import com.adm.supervision.repository.LigneReceptionProduitRepository;
import com.adm.supervision.repository.LigneTransfertStockRepository;
import com.adm.supervision.repository.MouvementStockRepository;
import com.adm.supervision.repository.ReceptionProduitRepository;
import com.adm.supervision.repository.ScanInconnuRepository;
import com.adm.supervision.repository.StockProduitRepository;
import com.adm.supervision.repository.TransfertStockRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.service.dto.CloseInventaireStockRequest;
import com.adm.supervision.service.dto.DepotStockDTO;
import com.adm.supervision.service.dto.InventaireStockDTO;
import com.adm.supervision.service.dto.LigneReceptionProduitDTO;
import com.adm.supervision.service.dto.ProduitDTO;
import com.adm.supervision.service.dto.ReverseMouvementStockRequest;
import com.adm.supervision.service.dto.ScanInventaireStockRequest;
import com.adm.supervision.service.dto.ScanReceptionProduitRequest;
import com.adm.supervision.service.dto.StockProduitDTO;
import com.adm.supervision.service.dto.ValidateReceptionProduitRequest;
import com.adm.supervision.service.dto.ValidateTransfertStockRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
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
class StockWorkflowResourceIT {

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MockMvc restMockMvc;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReceptionProduitRepository receptionProduitRepository;

    @Autowired
    private LigneReceptionProduitRepository ligneReceptionProduitRepository;

    @Autowired
    private InventaireStockRepository inventaireStockRepository;

    @Autowired
    private LigneInventaireStockRepository ligneInventaireStockRepository;

    @Autowired
    private TransfertStockRepository transfertStockRepository;

    @Autowired
    private LigneTransfertStockRepository ligneTransfertStockRepository;

    @Autowired
    private StockProduitRepository stockProduitRepository;

    @Autowired
    private DepotStockRepository depotStockRepository;

    @Autowired
    private MouvementStockRepository mouvementStockRepository;

    @Autowired
    private LigneMouvementStockRepository ligneMouvementStockRepository;

    @Autowired
    private CodeBarresProduitRepository codeBarresProduitRepository;

    @Autowired
    private ScanInconnuRepository scanInconnuRepository;

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private User adminUser;

    @BeforeEach
    void init() {
        adminUser = userRepository.findOneByLogin("admin").orElseThrow();
    }

    @Test
    @Transactional
    void scanReceptionAndValidateShouldIncreaseStock() throws Exception {
        Boutique boutique = persistBoutique("RCP");
        DepotStock depot = persistDepot(boutique, "DEP-RCP");
        Produit produit = persistProduit(boutique, "PRD-RCP");
        CodeBarresProduit barcode = persistBarcode(produit, "BAR-RCP");
        ReceptionProduit reception = persistReception(boutique, "RCP-REF");

        ScanReceptionProduitRequest scanRequest = new ScanReceptionProduitRequest();
        scanRequest.setCodeBarres(barcode.getCode());
        scanRequest.setQuantiteRecue(new BigDecimal("5"));

        LigneReceptionProduitDTO ligne = om.readValue(
            restMockMvc
                .perform(
                    post("/api/reception-produits/{receptionId}/scan", reception.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(scanRequest))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LigneReceptionProduitDTO.class
        );

        assertThat(ligne.getQuantiteRecue()).isEqualByComparingTo("5");
        assertThat(ligneReceptionProduitRepository.findAllByReception_Id(reception.getId())).hasSize(1);

        ValidateReceptionProduitRequest validateRequest = new ValidateReceptionProduitRequest();
        validateRequest.setDepotId(depot.getId());

        restMockMvc
            .perform(
                post("/api/reception-produits/{receptionId}/validate", reception.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(validateRequest))
            )
            .andExpect(status().isOk());

        StockProduit stockProduit = stockProduitRepository.findByProduit_IdAndDepot_Id(produit.getId(), depot.getId()).orElseThrow();
        assertThat(stockProduit.getQuantiteTheorique()).isEqualByComparingTo("5");
        assertThat(mouvementStockRepository.existsByReference("RCP-" + reception.getReference())).isTrue();
        assertThat(ligneMouvementStockRepository.findAll()).hasSize(1);
    }

    @Test
    @Transactional
    void unknownBarcodeScanShouldBeExplicitAndPersisted() throws Exception {
        Boutique boutique = persistBoutique("UNKNOWN");
        long countBefore = scanInconnuRepository.count();
        String unknownCode = unique("BAR-UNKNOWN");

        restMockMvc
            .perform(
                post("/api/barcodes/scan")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "code": "%s",
                          "boutiqueId": %d,
                          "ecranOrigine": "POSTE_CAISSE"
                        }
                        """.formatted(unknownCode, boutique.getId())
                    )
            )
            .andExpect(status().isOk())
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.trouve").value(false))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.affectationAutorisee").value(true))
            .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.scanInconnuId").isNumber())
            .andExpect(
                org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.message").value(
                    org.hamcrest.Matchers.containsString("inconnu")
                )
            );

        assertThat(scanInconnuRepository.count()).isEqualTo(countBefore + 1);
        assertThat(scanInconnuRepository.findAll()).anySatisfy(scan -> {
            assertThat(scan.getCodeScanne()).isEqualTo(unknownCode);
            assertThat(scan.getEcranOrigine()).isEqualTo("POSTE_CAISSE");
            assertThat(scan.getResolu()).isFalse();
            assertThat(scan.getBoutique().getId()).isEqualTo(boutique.getId());
        });
    }

    @Test
    @Transactional
    void inventoryWorkflowShouldAdjustStockOnClose() throws Exception {
        Boutique boutique = persistBoutique("INV");
        DepotStock depot = persistDepot(boutique, "DEP-INV");
        Produit produit = persistProduit(boutique, "PRD-INV");
        persistStock(produit, depot, "10", "2");
        InventaireStock inventaire = persistInventaire(boutique, depot, "INV-REF");

        restMockMvc.perform(post("/api/inventaire-stocks/{inventaireId}/start", inventaire.getId())).andExpect(status().isOk());

        ScanInventaireStockRequest scanRequest = new ScanInventaireStockRequest();
        scanRequest.setProduitId(produit.getId());
        scanRequest.setQuantiteComptee(new BigDecimal("7"));
        scanRequest.setCommentaire("Comptage manuel");

        restMockMvc
            .perform(
                post("/api/inventaire-stocks/{inventaireId}/scan", inventaire.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scanRequest))
            )
            .andExpect(status().isOk());

        CloseInventaireStockRequest closeRequest = new CloseInventaireStockRequest();
        closeRequest.setApplyAdjustments(true);

        InventaireStockDTO closedInventory = om.readValue(
            restMockMvc
                .perform(
                    post("/api/inventaire-stocks/{inventaireId}/close", inventaire.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(closeRequest))
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InventaireStockDTO.class
        );

        assertThat(closedInventory.getStatut()).isEqualTo(StatutInventaire.CLOTURE);
        assertThat(
            stockProduitRepository.findByProduit_IdAndDepot_Id(produit.getId(), depot.getId()).orElseThrow().getQuantiteTheorique()
        ).isEqualByComparingTo("7");
        assertThat(mouvementStockRepository.existsByReference("INV-" + inventaire.getReference())).isTrue();
    }

    @Test
    @Transactional
    void completeInventoryShouldSetUnscannedProductsToZero() throws Exception {
        Boutique boutique = persistBoutique("INV-COMPLETE");
        DepotStock depot = persistDepot(boutique, "DEP-INV-COMPLETE");
        Produit produit = persistProduit(boutique, "PRD-INV-COMPLETE");
        persistStock(produit, depot, "6", "1");
        InventaireStock inventaire = persistInventaire(boutique, depot, "INV-COMPLETE-REF");
        inventaire.setTypeInventaire(TypeInventaire.COMPLET);

        restMockMvc.perform(post("/api/inventaire-stocks/{inventaireId}/start", inventaire.getId())).andExpect(status().isOk());

        LigneInventaireStock ligne = ligneInventaireStockRepository
            .findByInventaire_IdAndProduit_Id(inventaire.getId(), produit.getId())
            .orElseThrow();
        assertThat(ligne.getQuantiteTheorique()).isEqualByComparingTo("6");
        assertThat(ligne.getQuantiteComptee()).isEqualByComparingTo("0");
        assertThat(ligne.getEcart()).isEqualByComparingTo("-6");

        CloseInventaireStockRequest closeRequest = new CloseInventaireStockRequest();
        closeRequest.setApplyAdjustments(true);
        restMockMvc
            .perform(
                post("/api/inventaire-stocks/{inventaireId}/close", inventaire.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(closeRequest))
            )
            .andExpect(status().isOk());

        assertThat(
            stockProduitRepository.findByProduit_IdAndDepot_Id(produit.getId(), depot.getId()).orElseThrow().getQuantiteTheorique()
        ).isEqualByComparingTo("0");
    }

    @Test
    @Transactional
    void inventoryWithoutDepotShouldBeRejected() throws Exception {
        Boutique boutique = persistBoutique("INV-ND");
        InventaireStock inventaire = persistInventaire(boutique, null, "INV-ND-REF");

        restMockMvc.perform(post("/api/inventaire-stocks/{inventaireId}/start", inventaire.getId())).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void validateTransferShouldMoveStockBetweenDepots() throws Exception {
        Boutique boutique = persistBoutique("TRF");
        DepotStock depotOrigine = persistDepot(boutique, "DEP-TRF-OUT");
        DepotStock depotDestination = persistDepot(boutique, "DEP-TRF-IN");
        Produit produit = persistProduit(boutique, "PRD-TRF");
        persistStock(produit, depotOrigine, "9", "1");
        TransfertStock transfert = persistTransfer(boutique, boutique, "TRF-REF");
        persistTransferLine(transfert, produit, "4");

        ValidateTransfertStockRequest request = new ValidateTransfertStockRequest();
        request.setDepotOrigineId(depotOrigine.getId());
        request.setDepotDestinationId(depotDestination.getId());

        restMockMvc
            .perform(
                post("/api/transfert-stocks/{transfertId}/validate", transfert.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(request))
            )
            .andExpect(status().isOk());

        assertThat(transfertStockRepository.findById(transfert.getId()).orElseThrow().getStatut()).isEqualTo(StatutMouvementStock.VALIDE);
        assertThat(
            stockProduitRepository.findByProduit_IdAndDepot_Id(produit.getId(), depotOrigine.getId()).orElseThrow().getQuantiteTheorique()
        ).isEqualByComparingTo("5");
        assertThat(
            stockProduitRepository
                .findByProduit_IdAndDepot_Id(produit.getId(), depotDestination.getId())
                .orElseThrow()
                .getQuantiteTheorique()
        ).isEqualByComparingTo("4");
        assertThat(mouvementStockRepository.existsByReference("TRF-OUT-" + transfert.getReference())).isTrue();
        assertThat(mouvementStockRepository.existsByReference("TRF-IN-" + transfert.getReference())).isTrue();
    }

    @Test
    @Transactional
    void interShopTransferShouldCreditDestinationShopProduct() throws Exception {
        Boutique origine = persistBoutique("TRF-INTER-OUT");
        Boutique destination = persistBoutique("TRF-INTER-IN");
        DepotStock depotOrigine = persistDepot(origine, "DEP-INTER-OUT");
        DepotStock depotDestination = persistDepot(destination, "DEP-INTER-IN");
        Produit produitOrigine = persistProduit(origine, "PRD-INTER-OUT");
        Produit produitDestination = persistProduit(destination, "PRD-INTER-IN");
        produitDestination.setCodeInterne(produitOrigine.getCodeInterne());
        persistStock(produitOrigine, depotOrigine, "8", "1");
        TransfertStock transfert = persistTransfer(origine, destination, "TRF-INTER");
        persistTransferLine(transfert, produitOrigine, "3");

        ValidateTransfertStockRequest request = new ValidateTransfertStockRequest();
        request.setDepotOrigineId(depotOrigine.getId());
        request.setDepotDestinationId(depotDestination.getId());

        restMockMvc
            .perform(
                post("/api/transfert-stocks/{transfertId}/validate", transfert.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(request))
            )
            .andExpect(status().isOk());

        assertThat(
            stockProduitRepository
                .findByProduit_IdAndDepot_Id(produitOrigine.getId(), depotOrigine.getId())
                .orElseThrow()
                .getQuantiteTheorique()
        ).isEqualByComparingTo("5");
        assertThat(
            stockProduitRepository
                .findByProduit_IdAndDepot_Id(produitDestination.getId(), depotDestination.getId())
                .orElseThrow()
                .getQuantiteTheorique()
        ).isEqualByComparingTo("3");
        assertThat(stockProduitRepository.findByProduit_IdAndDepot_Id(produitOrigine.getId(), depotDestination.getId())).isEmpty();
    }

    @Test
    @Transactional
    void createStockAcrossShopsShouldBeRejected() throws Exception {
        Boutique produitBoutique = persistBoutique("STOCK-CROSS-P");
        Boutique depotBoutique = persistBoutique("STOCK-CROSS-D");
        Produit produit = persistProduit(produitBoutique, "PRD-CROSS");
        DepotStock depot = persistDepot(depotBoutique, "DEP-CROSS");

        ProduitDTO produitDTO = new ProduitDTO();
        produitDTO.setId(produit.getId());
        DepotStockDTO depotDTO = new DepotStockDTO();
        depotDTO.setId(depot.getId());
        StockProduitDTO stockDTO = new StockProduitDTO();
        stockDTO.setQuantiteTheorique(BigDecimal.ONE);
        stockDTO.setProduit(produitDTO);
        stockDTO.setDepot(depotDTO);

        restMockMvc
            .perform(post("/api/stock-produits").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void createDuplicateStockShouldBeRejected() throws Exception {
        Boutique boutique = persistBoutique("DUP");
        DepotStock depot = persistDepot(boutique, "DEP-DUP");
        Produit produit = persistProduit(boutique, "PRD-DUP");
        persistStock(produit, depot, "2", "1");

        ProduitDTO produitDTO = new ProduitDTO();
        produitDTO.setId(produit.getId());

        DepotStockDTO depotStockDTO = new DepotStockDTO();
        depotStockDTO.setId(depot.getId());

        StockProduitDTO stockProduitDTO = new StockProduitDTO();
        stockProduitDTO.setQuantiteTheorique(new BigDecimal("3"));
        stockProduitDTO.setStockAlerte(new BigDecimal("1"));
        stockProduitDTO.setProduit(produitDTO);
        stockProduitDTO.setDepot(depotStockDTO);

        restMockMvc
            .perform(post("/api/stock-produits").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockProduitDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void scanReceptionShouldRejectAmbiguousBarcodeWithinBoutique() throws Exception {
        Boutique boutique = persistBoutique("AMB");
        Produit produitA = persistProduit(boutique, "PRD-AMB-A");
        Produit produitB = persistProduit(boutique, "PRD-AMB-B");
        persistBarcode(produitA, "BAR-AMB-COMMON");
        persistBarcode(produitB, "BAR-AMB-COMMON");
        ReceptionProduit reception = persistReception(boutique, "RCP-AMB");

        ScanReceptionProduitRequest scanRequest = new ScanReceptionProduitRequest();
        scanRequest.setCodeBarres("BAR-AMB-COMMON");
        scanRequest.setQuantiteRecue(new BigDecimal("1"));

        restMockMvc
            .perform(
                post("/api/reception-produits/{receptionId}/scan", reception.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scanRequest))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void reverseTransferShouldRestoreBothStocks() throws Exception {
        Boutique boutique = persistBoutique("REV-TRF");
        DepotStock depotOrigine = persistDepot(boutique, "DEP-REV-OUT");
        DepotStock depotDestination = persistDepot(boutique, "DEP-REV-IN");
        Produit produit = persistProduit(boutique, "PRD-REV-TRF");
        persistStock(produit, depotOrigine, "9", "1");
        TransfertStock transfert = persistTransfer(boutique, boutique, "TRF-REV-REF");
        persistTransferLine(transfert, produit, "4");

        ValidateTransfertStockRequest validateRequest = new ValidateTransfertStockRequest();
        validateRequest.setDepotOrigineId(depotOrigine.getId());
        validateRequest.setDepotDestinationId(depotDestination.getId());

        restMockMvc
            .perform(
                post("/api/transfert-stocks/{transfertId}/validate", transfert.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(validateRequest))
            )
            .andExpect(status().isOk());

        MouvementStock sortie = mouvementStockRepository.findByReference("TRF-OUT-" + transfert.getReference()).orElseThrow();
        ReverseMouvementStockRequest reverseRequest = new ReverseMouvementStockRequest();
        reverseRequest.setMotif("Correction transfert");

        restMockMvc
            .perform(
                post("/api/mouvement-stocks/{mouvementId}/reverse", sortie.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(reverseRequest))
            )
            .andExpect(status().isOk());

        assertThat(
            stockProduitRepository.findByProduit_IdAndDepot_Id(produit.getId(), depotOrigine.getId()).orElseThrow().getQuantiteTheorique()
        ).isEqualByComparingTo("9");
        assertThat(
            stockProduitRepository
                .findByProduit_IdAndDepot_Id(produit.getId(), depotDestination.getId())
                .orElseThrow()
                .getQuantiteTheorique()
        ).isEqualByComparingTo("0");
        assertThat(mouvementStockRepository.findByReference("TRF-OUT-" + transfert.getReference()).orElseThrow().getStatut()).isEqualTo(
            StatutMouvementStock.ANNULE
        );
        assertThat(mouvementStockRepository.findByReference("TRF-IN-" + transfert.getReference()).orElseThrow().getStatut()).isEqualTo(
            StatutMouvementStock.ANNULE
        );
        assertThat(mouvementStockRepository.existsByReference("REV-TRF-OUT-" + transfert.getReference())).isTrue();
        assertThat(mouvementStockRepository.existsByReference("REV-TRF-IN-" + transfert.getReference())).isTrue();
    }

    @Test
    @Transactional
    void reverseMovementShouldBeRejectedWhenStockChangedAfterValidation() throws Exception {
        Boutique boutique = persistBoutique("REV-RCP");
        DepotStock depot = persistDepot(boutique, "DEP-REV-RCP");
        Produit produit = persistProduit(boutique, "PRD-REV-RCP");
        ReceptionProduit reception = persistReception(boutique, "RCP-REV");

        ScanReceptionProduitRequest scanRequest = new ScanReceptionProduitRequest();
        scanRequest.setProduitId(produit.getId());
        scanRequest.setQuantiteRecue(new BigDecimal("5"));

        restMockMvc
            .perform(
                post("/api/reception-produits/{receptionId}/scan", reception.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scanRequest))
            )
            .andExpect(status().isOk());

        ValidateReceptionProduitRequest validateRequest = new ValidateReceptionProduitRequest();
        validateRequest.setDepotId(depot.getId());

        restMockMvc
            .perform(
                post("/api/reception-produits/{receptionId}/validate", reception.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(validateRequest))
            )
            .andExpect(status().isOk());

        StockProduit stockProduit = stockProduitRepository.findByProduit_IdAndDepot_Id(produit.getId(), depot.getId()).orElseThrow();
        stockProduit.setQuantiteTheorique(new BigDecimal("6"));
        stockProduitRepository.saveAndFlush(stockProduit);

        MouvementStock mouvement = mouvementStockRepository.findByReference("RCP-" + reception.getReference()).orElseThrow();

        restMockMvc
            .perform(
                post("/api/mouvement-stocks/{mouvementId}/reverse", mouvement.getId()).contentType(MediaType.APPLICATION_JSON).content("{}")
            )
            .andExpect(status().isBadRequest());
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

    private DepotStock persistDepot(Boutique boutique, String prefix) {
        DepotStock depotStock = new DepotStock().code(unique(prefix)).libelle(unique(prefix + "-LIB")).actif(true).boutique(boutique);
        em.persist(depotStock);
        return depotStock;
    }

    private Produit persistProduit(Boutique boutique, String prefix) {
        UniteMesure uniteMesure = new UniteMesure().code(unique(prefix + "-UM")).libelle("Piece " + unique(prefix + "-L"));
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

    private CodeBarresProduit persistBarcode(Produit produit, String prefix) {
        CodeBarresProduit barcode = new CodeBarresProduit()
            .code(unique(prefix))
            .type(TypeCodeBarres.CODE128)
            .principal(true)
            .genereParSysteme(false)
            .actif(true)
            .dateAffectation(Instant.now())
            .produit(produit);
        em.persist(barcode);
        return barcode;
    }

    private ReceptionProduit persistReception(Boutique boutique, String prefix) {
        ReceptionProduit receptionProduit = new ReceptionProduit()
            .reference(unique(prefix))
            .dateReception(Instant.now())
            .boutique(boutique)
            .utilisateur(adminUser);
        em.persist(receptionProduit);
        return receptionProduit;
    }

    private StockProduit persistStock(Produit produit, DepotStock depot, String quantity, String alert) {
        StockProduit stockProduit = new StockProduit()
            .produit(produit)
            .depot(depot)
            .quantiteTheorique(new BigDecimal(quantity))
            .stockAlerte(alert == null ? null : new BigDecimal(alert))
            .dateDernierMouvement(Instant.now());
        em.persist(stockProduit);
        return stockProduit;
    }

    private InventaireStock persistInventaire(Boutique boutique, DepotStock depot, String prefix) {
        InventaireStock inventaireStock = new InventaireStock()
            .reference(unique(prefix))
            .typeInventaire(TypeInventaire.TOURNANT)
            .statut(StatutInventaire.PLANIFIE)
            .dateDebut(Instant.now())
            .boutique(boutique)
            .depot(depot)
            .utilisateur(adminUser);
        em.persist(inventaireStock);
        return inventaireStock;
    }

    private TransfertStock persistTransfer(Boutique boutiqueOrigine, Boutique boutiqueDestination, String prefix) {
        TransfertStock transfertStock = new TransfertStock()
            .reference(unique(prefix))
            .dateTransfert(Instant.now())
            .statut(StatutMouvementStock.BROUILLON)
            .boutiqueOrigine(boutiqueOrigine)
            .boutiqueDestination(boutiqueDestination)
            .utilisateur(adminUser);
        em.persist(transfertStock);
        return transfertStock;
    }

    private LigneTransfertStock persistTransferLine(TransfertStock transfertStock, Produit produit, String quantity) {
        LigneTransfertStock ligneTransfertStock = new LigneTransfertStock()
            .transfert(transfertStock)
            .produit(produit)
            .quantite(new BigDecimal(quantity));
        em.persist(ligneTransfertStock);
        return ligneTransfertStock;
    }

    private String unique(String prefix) {
        return prefix + "-" + COUNTER.incrementAndGet();
    }
}
