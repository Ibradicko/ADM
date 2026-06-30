package com.adm.supervision.service;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.InventaireStock;
import com.adm.supervision.domain.LigneVente;
import com.adm.supervision.domain.MouvementStock;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.RapportExport;
import com.adm.supervision.domain.ReceptionProduit;
import com.adm.supervision.domain.RegleRedevance;
import com.adm.supervision.domain.StockProduit;
import com.adm.supervision.domain.TransfertStock;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.repository.CalculRedevanceRepository;
import com.adm.supervision.repository.InventaireStockRepository;
import com.adm.supervision.repository.LigneVenteRepository;
import com.adm.supervision.repository.MouvementStockRepository;
import com.adm.supervision.repository.ProduitRepository;
import com.adm.supervision.repository.RapportExportRepository;
import com.adm.supervision.repository.ReceptionProduitRepository;
import com.adm.supervision.repository.RegleRedevanceRepository;
import com.adm.supervision.repository.StockProduitRepository;
import com.adm.supervision.repository.TransfertStockRepository;
import com.adm.supervision.repository.VenteRepository;
import com.adm.supervision.service.dto.BoutiqueDTO;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ResourceBoutiqueSecurityService {

    private static final String ACCESS_DENIED = "Acces refuse a une ressource hors perimetre boutique";

    private final ModuleSecurityService moduleSecurityService;
    private final VenteRepository venteRepository;
    private final StockProduitRepository stockProduitRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final ProduitRepository produitRepository;
    private final ReceptionProduitRepository receptionProduitRepository;
    private final InventaireStockRepository inventaireStockRepository;
    private final TransfertStockRepository transfertStockRepository;
    private final RapportExportRepository rapportExportRepository;
    private final CalculRedevanceRepository calculRedevanceRepository;
    private final RegleRedevanceRepository regleRedevanceRepository;
    private final LigneVenteRepository ligneVenteRepository;

    public ResourceBoutiqueSecurityService(
        ModuleSecurityService moduleSecurityService,
        VenteRepository venteRepository,
        StockProduitRepository stockProduitRepository,
        MouvementStockRepository mouvementStockRepository,
        ProduitRepository produitRepository,
        ReceptionProduitRepository receptionProduitRepository,
        InventaireStockRepository inventaireStockRepository,
        TransfertStockRepository transfertStockRepository,
        RapportExportRepository rapportExportRepository,
        CalculRedevanceRepository calculRedevanceRepository,
        RegleRedevanceRepository regleRedevanceRepository,
        LigneVenteRepository ligneVenteRepository
    ) {
        this.moduleSecurityService = moduleSecurityService;
        this.venteRepository = venteRepository;
        this.stockProduitRepository = stockProduitRepository;
        this.mouvementStockRepository = mouvementStockRepository;
        this.produitRepository = produitRepository;
        this.receptionProduitRepository = receptionProduitRepository;
        this.inventaireStockRepository = inventaireStockRepository;
        this.transfertStockRepository = transfertStockRepository;
        this.rapportExportRepository = rapportExportRepository;
        this.calculRedevanceRepository = calculRedevanceRepository;
        this.regleRedevanceRepository = regleRedevanceRepository;
        this.ligneVenteRepository = ligneVenteRepository;
    }

    public void assertBoutique(BoutiqueDTO boutique) {
        moduleSecurityService.assertBoutiqueAccess(boutique == null ? null : boutique.getId(), ACCESS_DENIED);
    }

    public void assertBoutiques(BoutiqueDTO... boutiques) {
        moduleSecurityService.assertAllBoutiquesAccess(
            Arrays.stream(boutiques).filter(Objects::nonNull).map(BoutiqueDTO::getId).toList(),
            ACCESS_DENIED
        );
    }

    public boolean canAccessVente(Long id) {
        return isAllowed(() -> assertVente(id));
    }

    public boolean canAccessLigneVente(Long id) {
        return isAllowed(() -> assertLigneVente(id));
    }

    public boolean canAccessStockProduit(Long id) {
        return isAllowed(() -> assertStockProduit(id));
    }

    public boolean canAccessMouvementStock(Long id) {
        return isAllowed(() -> assertMouvementStock(id));
    }

    public boolean canAccessProduit(Long id) {
        return isAllowed(() -> assertProduit(id));
    }

    public boolean canAccessReceptionProduit(Long id) {
        return isAllowed(() -> assertReceptionProduit(id));
    }

    public boolean canAccessInventaireStock(Long id) {
        return isAllowed(() -> assertInventaireStock(id));
    }

    public boolean canAccessTransfertStock(Long id) {
        return isAllowed(() -> assertTransfertStock(id));
    }

    public boolean canAccessRapportExport(Long id) {
        return isAllowed(() -> assertRapportExport(id));
    }

    public boolean canAccessCalculRedevance(Long id) {
        return isAllowed(() -> assertCalculRedevance(id));
    }

    public boolean canAccessRegleRedevance(Long id) {
        return isAllowed(() -> assertRegleRedevance(id));
    }

    public void assertVente(Long id) {
        assertEntity(venteRepository.findById(id), Vente::getBoutique);
    }

    public void assertLigneVente(Long id) {
        LigneVente ligneVente = ligneVenteRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new AccessDeniedException(ACCESS_DENIED));
        moduleSecurityService.assertBoutiqueAccess(ligneVente.getVente().getBoutique().getId(), ACCESS_DENIED);
    }

    public void assertStockProduit(Long id) {
        StockProduit stock = stockProduitRepository.findById(id).orElseThrow(() -> new AccessDeniedException(ACCESS_DENIED));
        assertBoutiqueEntities(List.of(stock.getProduit().getBoutique(), stock.getDepot().getBoutique()));
    }

    public void assertMouvementStock(Long id) {
        assertEntity(mouvementStockRepository.findById(id), MouvementStock::getBoutique);
    }

    public void assertProduit(Long id) {
        assertEntity(produitRepository.findById(id), Produit::getBoutique);
    }

    public void assertReceptionProduit(Long id) {
        assertEntity(receptionProduitRepository.findById(id), ReceptionProduit::getBoutique);
    }

    public void assertInventaireStock(Long id) {
        assertEntity(inventaireStockRepository.findById(id), InventaireStock::getBoutique);
    }

    public void assertTransfertStock(Long id) {
        TransfertStock transfert = transfertStockRepository.findById(id).orElseThrow(() -> new AccessDeniedException(ACCESS_DENIED));
        assertBoutiqueEntities(List.of(transfert.getBoutiqueOrigine(), transfert.getBoutiqueDestination()));
    }

    public void assertRapportExport(Long id) {
        assertEntity(rapportExportRepository.findById(id), RapportExport::getBoutique);
    }

    public void assertCalculRedevance(Long id) {
        assertEntity(calculRedevanceRepository.findById(id), CalculRedevance::getBoutique);
    }

    public void assertRegleRedevance(Long id) {
        assertEntity(regleRedevanceRepository.findById(id), RegleRedevance::getBoutique);
    }

    private <T> void assertEntity(java.util.Optional<T> entity, Function<T, Boutique> boutiqueExtractor) {
        T value = entity.orElseThrow(() -> new AccessDeniedException(ACCESS_DENIED));
        Boutique boutique = boutiqueExtractor.apply(value);
        moduleSecurityService.assertBoutiqueAccess(boutique == null ? null : boutique.getId(), ACCESS_DENIED);
    }

    private void assertBoutiqueEntities(Collection<Boutique> boutiques) {
        moduleSecurityService.assertAllBoutiquesAccess(
            boutiques.stream().filter(Objects::nonNull).map(Boutique::getId).toList(),
            ACCESS_DENIED
        );
    }

    private boolean isAllowed(Runnable assertion) {
        try {
            assertion.run();
            return true;
        } catch (AccessDeniedException ignored) {
            return false;
        }
    }
}
