package com.adm.supervision.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.DepotStock;
import com.adm.supervision.domain.LigneVente;
import com.adm.supervision.domain.MouvementStock;
import com.adm.supervision.domain.OperationCorrectiveVente;
import com.adm.supervision.domain.PaiementVente;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.StockProduit;
import com.adm.supervision.domain.TicketCaisse;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.StatutPaiement;
import com.adm.supervision.domain.enumeration.StatutVente;
import com.adm.supervision.domain.enumeration.TypeOperationCorrective;
import com.adm.supervision.repository.BoutiqueRepository;
import com.adm.supervision.repository.ExploitationBoutiqueRepository;
import com.adm.supervision.repository.LigneMouvementStockRepository;
import com.adm.supervision.repository.LigneVenteRepository;
import com.adm.supervision.repository.LocataireRepository;
import com.adm.supervision.repository.ModePaiementRefRepository;
import com.adm.supervision.repository.MouvementStockRepository;
import com.adm.supervision.repository.OperationCorrectiveVenteRepository;
import com.adm.supervision.repository.PaiementVenteRepository;
import com.adm.supervision.repository.ProduitRepository;
import com.adm.supervision.repository.StockProduitRepository;
import com.adm.supervision.repository.TicketCaisseRepository;
import com.adm.supervision.repository.VenteRepository;
import com.adm.supervision.service.dto.OperationCorrectiveVenteDTO;
import com.adm.supervision.service.dto.PaiementVenteDTO;
import com.adm.supervision.service.dto.TicketCaisseDTO;
import com.adm.supervision.service.dto.VenteDTO;
import com.adm.supervision.service.mapper.LigneVenteMapper;
import com.adm.supervision.service.mapper.OperationCorrectiveVenteMapper;
import com.adm.supervision.service.mapper.PaiementVenteMapper;
import com.adm.supervision.service.mapper.TicketCaisseMapper;
import com.adm.supervision.service.mapper.VenteMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VenteWorkflowServiceTest {

    @Mock
    private VenteRepository venteRepository;

    @Mock
    private VenteMapper venteMapper;

    @Mock
    private ModuleSecurityService moduleSecurityService;

    @Mock
    private JournalAuditService journalAuditService;

    @Mock
    private OperationCorrectiveVenteRepository operationRepository;

    @Mock
    private OperationCorrectiveVenteMapper operationMapper;

    @Mock
    private PaiementVenteRepository paiementRepository;

    @Mock
    private LigneVenteRepository ligneVenteRepository;

    @Mock
    private StockProduitRepository stockProduitRepository;

    @Mock
    private MouvementStockRepository mouvementStockRepository;

    @Mock
    private LigneMouvementStockRepository ligneMouvementStockRepository;

    @Mock
    private PaiementVenteMapper paiementVenteMapper;

    @Mock
    private LigneVenteMapper ligneVenteMapper;

    @Mock
    private TicketCaisseRepository ticketCaisseRepository;

    @Mock
    private TicketCaisseMapper ticketCaisseMapper;

    @Mock
    private BoutiqueRepository boutiqueRepository;

    @Mock
    private LocataireRepository locataireRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ModePaiementRefRepository modePaiementRefRepository;

    @Mock
    private ExploitationBoutiqueRepository exploitationBoutiqueRepository;

    private VenteService venteService;
    private OperationCorrectiveVenteService correctiveService;
    private PaiementVenteService paiementVenteService;
    private TicketCaisseService ticketCaisseService;
    private Vente sale;
    private User currentUser;

    @BeforeEach
    void setUp() {
        venteService = new VenteService(
            venteRepository,
            venteMapper,
            ligneVenteMapper,
            paiementVenteMapper,
            ticketCaisseMapper,
            moduleSecurityService,
            journalAuditService,
            ligneVenteRepository,
            stockProduitRepository,
            mouvementStockRepository,
            ligneMouvementStockRepository,
            boutiqueRepository,
            locataireRepository,
            produitRepository,
            modePaiementRefRepository,
            paiementRepository,
            ticketCaisseRepository,
            exploitationBoutiqueRepository
        );
        correctiveService = new OperationCorrectiveVenteService(
            operationRepository,
            operationMapper,
            venteRepository,
            paiementRepository,
            moduleSecurityService,
            journalAuditService
        );
        paiementVenteService = new PaiementVenteService(
            paiementRepository,
            paiementVenteMapper,
            moduleSecurityService,
            journalAuditService,
            venteRepository
        );
        ticketCaisseService = new TicketCaisseService(
            ticketCaisseRepository,
            ticketCaisseMapper,
            moduleSecurityService,
            journalAuditService,
            venteRepository
        );

        Boutique boutique = new Boutique();
        boutique.setId(10L);
        sale = new Vente().id(20L).numeroTicket("V-20").statut(StatutVente.BROUILLON).boutique(boutique);
        currentUser = new User();
        currentUser.setId(30L);
        currentUser.setLogin("cashier");
        lenient().when(moduleSecurityService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void createsDraftSale() {
        VenteDTO dto = new VenteDTO();
        when(venteMapper.toEntity(dto)).thenReturn(sale);
        when(venteRepository.save(sale)).thenReturn(sale);

        venteService.save(dto);

        verify(venteRepository).save(sale);
        assertThat(sale.getStatut()).isEqualTo(StatutVente.BROUILLON);
    }

    @Test
    void validatesDraftSale() {
        Produit produit = new Produit().id(40L).designation("Produit").boutique(sale.getBoutique());
        DepotStock depot = new DepotStock().id(50L).boutique(sale.getBoutique());
        StockProduit stock = new StockProduit().produit(produit).depot(depot).quantiteTheorique(new BigDecimal("5"));
        LigneVente ligne = new LigneVente().vente(sale).produit(produit).quantite(new BigDecimal("2"));
        VenteDTO dto = new VenteDTO();
        dto.setId(sale.getId());
        dto.setStatut(StatutVente.VALIDEE);
        when(venteRepository.findById(sale.getId())).thenReturn(Optional.of(sale));
        when(ligneVenteRepository.findAllByVente_Id(sale.getId())).thenReturn(List.of(ligne));
        when(stockProduitRepository.findByProduitIdAndBoutiqueId(produit.getId(), sale.getBoutique().getId())).thenReturn(List.of(stock));
        when(mouvementStockRepository.save(any(MouvementStock.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doAnswer(invocation -> {
            ((Vente) invocation.getArgument(0)).setStatut(StatutVente.VALIDEE);
            return null;
        })
            .when(venteMapper)
            .partialUpdate(sale, dto);
        when(venteRepository.save(sale)).thenReturn(sale);

        venteService.partialUpdate(dto);

        assertThat(sale.getStatut()).isEqualTo(StatutVente.VALIDEE);
        assertThat(stock.getQuantiteTheorique()).isEqualByComparingTo("3");
        verify(journalAuditService).logAction(any(), any(), any(), any(), any(), any());
    }

    @Test
    void refusesDirectModificationOfValidatedSale() {
        sale.setStatut(StatutVente.VALIDEE);
        VenteDTO dto = new VenteDTO();
        dto.setId(sale.getId());
        when(venteRepository.findById(sale.getId())).thenReturn(Optional.of(sale));

        assertThatThrownBy(() -> venteService.partialUpdate(dto))
            .isInstanceOf(BusinessValidationException.class)
            .hasMessageContaining("operation corrective");
    }

    @Test
    void refusesDeletionOfValidatedSale() {
        sale.setStatut(StatutVente.VALIDEE);
        when(venteRepository.findOneWithEagerRelationships(sale.getId())).thenReturn(Optional.of(sale));

        assertThatThrownBy(() -> venteService.delete(sale.getId()))
            .isInstanceOf(BusinessValidationException.class)
            .hasMessageContaining("ne peut pas etre supprimee");
    }

    @Test
    void createsPaymentFromShortSaleReference() {
        PaiementVente paiement = new PaiementVente()
            .referencePaiement("PAY-20")
            .statut(StatutPaiement.PAYE)
            .vente(new Vente().id(sale.getId()));
        PaiementVenteDTO dto = new PaiementVenteDTO();

        when(paiementVenteMapper.toEntity(dto)).thenReturn(paiement);
        when(venteRepository.findOneWithEagerRelationships(sale.getId())).thenReturn(Optional.of(sale));
        when(paiementRepository.save(paiement)).thenReturn(paiement);

        paiementVenteService.save(dto);

        assertThat(paiement.getVente()).isSameAs(sale);
        verify(moduleSecurityService).assertBoutiqueAccess(eq(10L), any());
        verify(journalAuditService).logAction(any(), any(), any(), any(), any(), any());
    }

    @Test
    void createsTicketFromShortSaleReference() {
        TicketCaisse ticket = new TicketCaisse().numero("TC-20").vente(new Vente().id(sale.getId()));
        TicketCaisseDTO dto = new TicketCaisseDTO();

        when(ticketCaisseMapper.toEntity(dto)).thenReturn(ticket);
        when(venteRepository.findOneWithEagerRelationships(sale.getId())).thenReturn(Optional.of(sale));
        when(ticketCaisseRepository.save(ticket)).thenReturn(ticket);

        ticketCaisseService.save(dto);

        assertThat(ticket.getVente()).isSameAs(sale);
        verify(moduleSecurityService).assertBoutiqueAccess(eq(10L), any());
        verify(journalAuditService).logAction(any(), any(), any(), any(), any(), any());
    }

    @Test
    void appliesAuthorizedCorrectiveOperationAndCancelsPayments() {
        sale.setStatut(StatutVente.VALIDEE);
        PaiementVente paiement = new PaiementVente().statut(StatutPaiement.PAYE).vente(sale);
        OperationCorrectiveVente operation = new OperationCorrectiveVente()
            .typeOperation(TypeOperationCorrective.ANNULATION)
            .motif("Erreur de saisie")
            .vente(new Vente().id(sale.getId()));
        OperationCorrectiveVenteDTO dto = new OperationCorrectiveVenteDTO();

        when(operationMapper.toEntity(dto)).thenReturn(operation);
        when(venteRepository.findOneWithEagerRelationships(sale.getId())).thenReturn(Optional.of(sale));
        when(operationRepository.save(operation)).thenReturn(operation);
        when(paiementRepository.findAllByVenteId(sale.getId())).thenReturn(List.of(paiement));

        correctiveService.save(dto);

        assertThat(sale.getStatut()).isEqualTo(StatutVente.ANNULEE);
        assertThat(paiement.getStatut()).isEqualTo(StatutPaiement.ANNULE);
        assertThat(operation.getUtilisateur()).isEqualTo(currentUser);
        verify(journalAuditService).logAction(any(), any(), any(), any(), any(), any());
    }
}
