package com.adm.supervision.service;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.CodeBarresProduit;
import com.adm.supervision.domain.DepotStock;
import com.adm.supervision.domain.InventaireStock;
import com.adm.supervision.domain.LigneInventaireStock;
import com.adm.supervision.domain.LigneMouvementStock;
import com.adm.supervision.domain.LigneReceptionProduit;
import com.adm.supervision.domain.LigneTransfertStock;
import com.adm.supervision.domain.MouvementStock;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.ReceptionProduit;
import com.adm.supervision.domain.StockProduit;
import com.adm.supervision.domain.TransfertStock;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.StatutInventaire;
import com.adm.supervision.domain.enumeration.StatutMouvementStock;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.domain.enumeration.TypeInventaire;
import com.adm.supervision.domain.enumeration.TypeMouvementStock;
import com.adm.supervision.repository.CodeBarresProduitRepository;
import com.adm.supervision.repository.DepotStockRepository;
import com.adm.supervision.repository.InventaireStockRepository;
import com.adm.supervision.repository.LigneInventaireStockRepository;
import com.adm.supervision.repository.LigneMouvementStockRepository;
import com.adm.supervision.repository.LigneReceptionProduitRepository;
import com.adm.supervision.repository.LigneTransfertStockRepository;
import com.adm.supervision.repository.MouvementStockRepository;
import com.adm.supervision.repository.ProduitRepository;
import com.adm.supervision.repository.ReceptionProduitRepository;
import com.adm.supervision.repository.StockProduitRepository;
import com.adm.supervision.repository.TransfertStockRepository;
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
import com.adm.supervision.service.mapper.InventaireStockMapper;
import com.adm.supervision.service.mapper.LigneInventaireStockMapper;
import com.adm.supervision.service.mapper.LigneReceptionProduitMapper;
import com.adm.supervision.service.mapper.LigneTransfertStockMapper;
import com.adm.supervision.service.mapper.MouvementStockMapper;
import com.adm.supervision.service.mapper.ReceptionProduitMapper;
import com.adm.supervision.service.mapper.TransfertStockMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StockWorkflowService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final ReceptionProduitRepository receptionProduitRepository;
    private final LigneReceptionProduitRepository ligneReceptionProduitRepository;
    private final InventaireStockRepository inventaireStockRepository;
    private final LigneInventaireStockRepository ligneInventaireStockRepository;
    private final TransfertStockRepository transfertStockRepository;
    private final LigneTransfertStockRepository ligneTransfertStockRepository;
    private final DepotStockRepository depotStockRepository;
    private final StockProduitRepository stockProduitRepository;
    private final ProduitRepository produitRepository;
    private final CodeBarresProduitRepository codeBarresProduitRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final LigneMouvementStockRepository ligneMouvementStockRepository;
    private final ModuleSecurityService moduleSecurityService;
    private final JournalAuditService journalAuditService;
    private final ReceptionProduitMapper receptionProduitMapper;
    private final LigneReceptionProduitMapper ligneReceptionProduitMapper;
    private final InventaireStockMapper inventaireStockMapper;
    private final LigneInventaireStockMapper ligneInventaireStockMapper;
    private final LigneTransfertStockMapper ligneTransfertStockMapper;
    private final TransfertStockMapper transfertStockMapper;
    private final MouvementStockMapper mouvementStockMapper;

    public StockWorkflowService(
        ReceptionProduitRepository receptionProduitRepository,
        LigneReceptionProduitRepository ligneReceptionProduitRepository,
        InventaireStockRepository inventaireStockRepository,
        LigneInventaireStockRepository ligneInventaireStockRepository,
        TransfertStockRepository transfertStockRepository,
        LigneTransfertStockRepository ligneTransfertStockRepository,
        DepotStockRepository depotStockRepository,
        StockProduitRepository stockProduitRepository,
        ProduitRepository produitRepository,
        CodeBarresProduitRepository codeBarresProduitRepository,
        MouvementStockRepository mouvementStockRepository,
        LigneMouvementStockRepository ligneMouvementStockRepository,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService,
        ReceptionProduitMapper receptionProduitMapper,
        LigneReceptionProduitMapper ligneReceptionProduitMapper,
        InventaireStockMapper inventaireStockMapper,
        LigneInventaireStockMapper ligneInventaireStockMapper,
        LigneTransfertStockMapper ligneTransfertStockMapper,
        TransfertStockMapper transfertStockMapper,
        MouvementStockMapper mouvementStockMapper
    ) {
        this.receptionProduitRepository = receptionProduitRepository;
        this.ligneReceptionProduitRepository = ligneReceptionProduitRepository;
        this.inventaireStockRepository = inventaireStockRepository;
        this.ligneInventaireStockRepository = ligneInventaireStockRepository;
        this.transfertStockRepository = transfertStockRepository;
        this.ligneTransfertStockRepository = ligneTransfertStockRepository;
        this.depotStockRepository = depotStockRepository;
        this.stockProduitRepository = stockProduitRepository;
        this.produitRepository = produitRepository;
        this.codeBarresProduitRepository = codeBarresProduitRepository;
        this.mouvementStockRepository = mouvementStockRepository;
        this.ligneMouvementStockRepository = ligneMouvementStockRepository;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
        this.receptionProduitMapper = receptionProduitMapper;
        this.ligneReceptionProduitMapper = ligneReceptionProduitMapper;
        this.inventaireStockMapper = inventaireStockMapper;
        this.ligneInventaireStockMapper = ligneInventaireStockMapper;
        this.ligneTransfertStockMapper = ligneTransfertStockMapper;
        this.transfertStockMapper = transfertStockMapper;
        this.mouvementStockMapper = mouvementStockMapper;
    }

    public LigneReceptionProduitDTO scanReception(Long receptionId, ScanReceptionProduitRequest request) {
        ReceptionProduit reception = getReception(receptionId);
        Produit produit = resolveProduct(request.getProduitId(), request.getCodeBarres(), reception.getBoutique().getId());
        ensureSameBoutique(reception.getBoutique(), produit);

        LigneReceptionProduit ligne = ligneReceptionProduitRepository
            .findByReception_IdAndProduit_Id(receptionId, produit.getId())
            .orElseGet(() -> new LigneReceptionProduit().reception(reception).produit(produit).quantiteRecue(ZERO));

        BigDecimal nouvelleQuantite = safe(ligne.getQuantiteRecue()).add(request.getQuantiteRecue());
        ligne.setQuantiteRecue(nouvelleQuantite);
        ligne.setCodeBarresScanne(request.getCodeBarres());
        if (ligne.getQuantiteAttendue() != null) {
            ligne.setEcart(nouvelleQuantite.subtract(ligne.getQuantiteAttendue()));
        }

        ligne = ligneReceptionProduitRepository.save(ligne);
        journalAuditService.logAction(
            TypeActionAudit.MOUVEMENT_STOCK,
            "ReceptionProduit",
            reception.getReference(),
            "Scan reception reference=" + reception.getReference() + ", produit=" + produit.getDesignation(),
            reception.getBoutique(),
            getCurrentUser()
        );
        return ligneReceptionProduitMapper.toDto(ligne);
    }

    public ReceptionProduitDTO validateReception(Long receptionId, ValidateReceptionProduitRequest request) {
        ReceptionProduit reception = getReception(receptionId);
        DepotStock depot = getActiveDepot(request.getDepotId());
        if (!depot.getBoutique().getId().equals(reception.getBoutique().getId())) {
            throw new BusinessValidationException(
                "receptionProduit",
                "invalidDepot",
                "Le depot ne correspond pas a la boutique de la reception"
            );
        }

        String movementReference = "RCP-" + reception.getReference();
        if (mouvementStockRepository.existsByReference(movementReference)) {
            throw new BusinessValidationException("receptionProduit", "alreadyValidated", "La reception a deja ete validee");
        }

        List<LigneReceptionProduit> lignes = ligneReceptionProduitRepository.findAllByReception_Id(receptionId);
        if (lignes.isEmpty()) {
            throw new BusinessValidationException("receptionProduit", "emptyReception", "La reception ne contient aucune ligne");
        }

        User currentUser = getCurrentUser();
        MouvementStock mouvement = mouvementStockRepository.save(
            new MouvementStock()
                .reference(movementReference)
                .typeMouvement(TypeMouvementStock.ENTREE)
                .statut(StatutMouvementStock.VALIDE)
                .dateMouvement(Instant.now())
                .motif("Validation reception " + reception.getReference())
                .boutique(reception.getBoutique())
                .utilisateur(currentUser)
        );

        for (LigneReceptionProduit ligne : lignes) {
            StockProduit stockProduit = getOrCreateStock(ligne.getProduit(), depot);
            BigDecimal stockAvant = safe(stockProduit.getQuantiteTheorique());
            BigDecimal stockApres = stockAvant.add(safe(ligne.getQuantiteRecue()));
            stockProduit.setQuantiteTheorique(stockApres);
            stockProduit.setDateDernierMouvement(Instant.now());
            stockProduitRepository.save(stockProduit);

            ligneMouvementStockRepository.save(
                new LigneMouvementStock()
                    .mouvement(mouvement)
                    .produit(ligne.getProduit())
                    .depot(depot)
                    .quantite(safe(ligne.getQuantiteRecue()))
                    .stockAvant(stockAvant)
                    .stockApres(stockApres)
                    .commentaire("Reception " + reception.getReference())
            );
        }

        journalAuditService.logAction(
            TypeActionAudit.MOUVEMENT_STOCK,
            "ReceptionProduit",
            reception.getReference(),
            "Validation reception reference=" + reception.getReference(),
            reception.getBoutique(),
            currentUser
        );
        return receptionProduitMapper.toDto(reception);
    }

    public InventaireStockDTO startInventory(Long inventaireId) {
        InventaireStock inventaireStock = getInventaire(inventaireId);
        if (inventaireStock.getDepot() == null) {
            throw new BusinessValidationException("inventaireStock", "missingDepot", "Le workflow d'inventaire exige un depot renseigne");
        }
        if (inventaireStock.getStatut() != StatutInventaire.PLANIFIE) {
            throw new BusinessValidationException("inventaireStock", "invalidStatus", "Seul un inventaire planifie peut etre demarre");
        }

        if (inventaireStock.getTypeInventaire() == TypeInventaire.COMPLET) {
            List<StockProduit> stocks = stockProduitRepository.findByDepotIdWithRelationships(inventaireStock.getDepot().getId());
            final InventaireStock inventaireForLines = inventaireStock;
            for (StockProduit stock : stocks) {
                LigneInventaireStock ligne = ligneInventaireStockRepository
                    .findByInventaire_IdAndProduit_Id(inventaireId, stock.getProduit().getId())
                    .orElseGet(() -> new LigneInventaireStock().inventaire(inventaireForLines).produit(stock.getProduit()));
                BigDecimal quantiteTheorique = safe(stock.getQuantiteTheorique());
                ligne.setQuantiteTheorique(quantiteTheorique);
                ligne.setQuantiteComptee(ZERO);
                ligne.setEcart(quantiteTheorique.negate());
                ligneInventaireStockRepository.save(ligne);
            }
        } else {
            for (LigneInventaireStock ligne : ligneInventaireStockRepository.findAllByInventaire_Id(inventaireId)) {
                StockProduit stock = getOrCreateStock(ligne.getProduit(), inventaireStock.getDepot());
                BigDecimal quantiteTheorique = safe(stock.getQuantiteTheorique());
                ligne.setQuantiteTheorique(quantiteTheorique);
                ligne.setQuantiteComptee(ZERO);
                ligne.setEcart(quantiteTheorique.negate());
                ligneInventaireStockRepository.save(ligne);
            }
        }

        inventaireStock.setStatut(StatutInventaire.EN_COURS);
        inventaireStock.setDateDebut(Instant.now());
        inventaireStock = inventaireStockRepository.save(inventaireStock);

        journalAuditService.logAction(
            TypeActionAudit.INVENTAIRE,
            "InventaireStock",
            inventaireStock.getReference(),
            "Demarrage inventaire reference=" + inventaireStock.getReference(),
            inventaireStock.getBoutique(),
            getCurrentUser()
        );
        return inventaireStockMapper.toDto(inventaireStock);
    }

    public LigneInventaireStockDTO scanInventory(Long inventaireId, ScanInventaireStockRequest request) {
        InventaireStock inventaireStock = getInventaire(inventaireId);
        if (inventaireStock.getDepot() == null) {
            throw new BusinessValidationException("inventaireStock", "missingDepot", "Le workflow d'inventaire exige un depot renseigne");
        }
        if (inventaireStock.getStatut() != StatutInventaire.EN_COURS) {
            throw new BusinessValidationException("inventaireStock", "invalidStatus", "L'inventaire doit etre en cours pour etre scanne");
        }

        Produit produit = resolveProduct(request.getProduitId(), request.getCodeBarres(), inventaireStock.getBoutique().getId());
        ensureSameBoutique(inventaireStock.getBoutique(), produit);
        StockProduit stockProduit = getOrCreateStock(produit, inventaireStock.getDepot());
        BigDecimal quantiteTheorique = safe(stockProduit.getQuantiteTheorique());

        LigneInventaireStock ligne = ligneInventaireStockRepository
            .findByInventaire_IdAndProduit_Id(inventaireId, produit.getId())
            .orElseGet(() -> new LigneInventaireStock().inventaire(inventaireStock).produit(produit));
        ligne.setQuantiteTheorique(quantiteTheorique);
        ligne.setQuantiteComptee(request.getQuantiteComptee());
        ligne.setEcart(request.getQuantiteComptee().subtract(quantiteTheorique));
        ligne.setCommentaire(request.getCommentaire());
        ligne = ligneInventaireStockRepository.save(ligne);

        journalAuditService.logAction(
            TypeActionAudit.INVENTAIRE,
            "InventaireStock",
            inventaireStock.getReference(),
            "Scan inventaire reference=" + inventaireStock.getReference() + ", produit=" + produit.getDesignation(),
            inventaireStock.getBoutique(),
            getCurrentUser()
        );
        return ligneInventaireStockMapper.toDto(ligne);
    }

    public InventaireStockDTO closeInventory(Long inventaireId, CloseInventaireStockRequest request) {
        InventaireStock inventaireStock = getInventaire(inventaireId);
        if (inventaireStock.getDepot() == null) {
            throw new BusinessValidationException("inventaireStock", "missingDepot", "Le workflow d'inventaire exige un depot renseigne");
        }
        if (inventaireStock.getStatut() != StatutInventaire.EN_COURS) {
            throw new BusinessValidationException("inventaireStock", "invalidStatus", "Seul un inventaire en cours peut etre cloture");
        }

        List<LigneInventaireStock> lignes = ligneInventaireStockRepository.findAllByInventaire_Id(inventaireId);
        if (lignes.isEmpty()) {
            throw new BusinessValidationException("inventaireStock", "emptyInventory", "Aucune ligne d'inventaire a cloturer");
        }

        User currentUser = getCurrentUser();
        if (request.isApplyAdjustments()) {
            String movementReference = "INV-" + inventaireStock.getReference();
            if (mouvementStockRepository.existsByReference(movementReference)) {
                throw new BusinessValidationException(
                    "inventaireStock",
                    "alreadyClosed",
                    "Les ajustements d'inventaire ont deja ete appliques"
                );
            }

            MouvementStock mouvement = mouvementStockRepository.save(
                new MouvementStock()
                    .reference(movementReference)
                    .typeMouvement(TypeMouvementStock.INVENTAIRE)
                    .statut(StatutMouvementStock.VALIDE)
                    .dateMouvement(Instant.now())
                    .motif("Cloture inventaire " + inventaireStock.getReference())
                    .boutique(inventaireStock.getBoutique())
                    .utilisateur(currentUser)
            );

            for (LigneInventaireStock ligne : lignes) {
                BigDecimal ecart = safe(ligne.getEcart());
                if (ecart.compareTo(ZERO) == 0) {
                    continue;
                }
                StockProduit stockProduit = getOrCreateStock(ligne.getProduit(), inventaireStock.getDepot());
                BigDecimal stockAvant = safe(stockProduit.getQuantiteTheorique());
                BigDecimal stockApres = safe(ligne.getQuantiteComptee());
                stockProduit.setQuantiteTheorique(stockApres);
                stockProduit.setDateDernierMouvement(Instant.now());
                stockProduitRepository.save(stockProduit);

                ligneMouvementStockRepository.save(
                    new LigneMouvementStock()
                        .mouvement(mouvement)
                        .produit(ligne.getProduit())
                        .depot(inventaireStock.getDepot())
                        .quantite(ecart.abs())
                        .stockAvant(stockAvant)
                        .stockApres(stockApres)
                        .commentaire("Ajustement inventaire " + inventaireStock.getReference())
                );
            }
        }

        inventaireStock.setStatut(StatutInventaire.CLOTURE);
        inventaireStock.setDateFin(Instant.now());
        inventaireStock = inventaireStockRepository.save(inventaireStock);

        journalAuditService.logAction(
            TypeActionAudit.INVENTAIRE,
            "InventaireStock",
            inventaireStock.getReference(),
            "Cloture inventaire reference=" + inventaireStock.getReference(),
            inventaireStock.getBoutique(),
            currentUser
        );
        return inventaireStockMapper.toDto(inventaireStock);
    }

    public TransfertStockDTO validateTransfer(Long transfertId, ValidateTransfertStockRequest request) {
        TransfertStock transfertStock = getTransfert(transfertId);
        if (transfertStock.getStatut() != StatutMouvementStock.BROUILLON) {
            throw new BusinessValidationException("transfertStock", "invalidStatus", "Seul un transfert brouillon peut etre valide");
        }

        DepotStock depotOrigine = getActiveDepot(request.getDepotOrigineId());
        DepotStock depotDestination = getActiveDepot(request.getDepotDestinationId());
        if (!depotOrigine.getBoutique().getId().equals(transfertStock.getBoutiqueOrigine().getId())) {
            throw new BusinessValidationException(
                "transfertStock",
                "invalidOriginDepot",
                "Le depot source ne correspond pas a la boutique d'origine"
            );
        }
        if (!depotDestination.getBoutique().getId().equals(transfertStock.getBoutiqueDestination().getId())) {
            throw new BusinessValidationException(
                "transfertStock",
                "invalidDestinationDepot",
                "Le depot destination ne correspond pas a la boutique de destination"
            );
        }
        if (depotOrigine.getId().equals(depotDestination.getId())) {
            throw new BusinessValidationException(
                "transfertStock",
                "sameDepot",
                "Les depots source et destination doivent etre differents"
            );
        }

        List<LigneTransfertStock> lignes = ligneTransfertStockRepository.findAllByTransfert_Id(transfertId);
        if (lignes.isEmpty()) {
            throw new BusinessValidationException("transfertStock", "emptyTransfer", "Le transfert ne contient aucune ligne");
        }

        String outputReference = "TRF-OUT-" + transfertStock.getReference();
        String inputReference = "TRF-IN-" + transfertStock.getReference();
        if (mouvementStockRepository.existsByReference(outputReference) || mouvementStockRepository.existsByReference(inputReference)) {
            throw new BusinessValidationException("transfertStock", "alreadyValidated", "Le transfert a deja ete valide");
        }

        User currentUser = getCurrentUser();
        MouvementStock sortie = mouvementStockRepository.save(
            new MouvementStock()
                .reference(outputReference)
                .typeMouvement(TypeMouvementStock.TRANSFERT)
                .statut(StatutMouvementStock.VALIDE)
                .dateMouvement(Instant.now())
                .motif("Sortie transfert " + transfertStock.getReference())
                .boutique(transfertStock.getBoutiqueOrigine())
                .utilisateur(currentUser)
        );
        MouvementStock entree = mouvementStockRepository.save(
            new MouvementStock()
                .reference(inputReference)
                .typeMouvement(TypeMouvementStock.TRANSFERT)
                .statut(StatutMouvementStock.VALIDE)
                .dateMouvement(Instant.now())
                .motif("Entree transfert " + transfertStock.getReference())
                .boutique(transfertStock.getBoutiqueDestination())
                .utilisateur(currentUser)
        );

        for (LigneTransfertStock ligne : lignes) {
            ensureSameBoutique(transfertStock.getBoutiqueOrigine(), ligne.getProduit());
            StockProduit sourceStock = getExistingUniqueStock(ligne.getProduit(), depotOrigine, "transfertStock", "missingSourceStock");

            BigDecimal quantite = safe(ligne.getQuantite());
            BigDecimal sourceBefore = safe(sourceStock.getQuantiteTheorique());
            if (sourceBefore.compareTo(quantite) < 0) {
                throw new BusinessValidationException(
                    "transfertStock",
                    "insufficientStock",
                    "Stock insuffisant pour transferer le produit " + ligne.getProduit().getDesignation()
                );
            }
            BigDecimal sourceAfter = sourceBefore.subtract(quantite);
            sourceStock.setQuantiteTheorique(sourceAfter);
            sourceStock.setDateDernierMouvement(Instant.now());
            stockProduitRepository.save(sourceStock);

            Produit destinationProduit = resolveDestinationProduct(ligne.getProduit(), transfertStock.getBoutiqueDestination());
            StockProduit destinationStock = getOrCreateStock(destinationProduit, depotDestination);
            BigDecimal destinationBefore = safe(destinationStock.getQuantiteTheorique());
            BigDecimal destinationAfter = destinationBefore.add(quantite);
            destinationStock.setQuantiteTheorique(destinationAfter);
            destinationStock.setDateDernierMouvement(Instant.now());
            stockProduitRepository.save(destinationStock);

            ligneMouvementStockRepository.save(
                new LigneMouvementStock()
                    .mouvement(sortie)
                    .produit(ligne.getProduit())
                    .depot(depotOrigine)
                    .quantite(quantite)
                    .stockAvant(sourceBefore)
                    .stockApres(sourceAfter)
                    .commentaire("Transfert sortant " + transfertStock.getReference())
            );
            ligneMouvementStockRepository.save(
                new LigneMouvementStock()
                    .mouvement(entree)
                    .produit(destinationProduit)
                    .depot(depotDestination)
                    .quantite(quantite)
                    .stockAvant(destinationBefore)
                    .stockApres(destinationAfter)
                    .commentaire("Transfert entrant " + transfertStock.getReference())
            );
        }

        transfertStock.setStatut(StatutMouvementStock.VALIDE);
        transfertStock = transfertStockRepository.save(transfertStock);
        journalAuditService.logAction(
            TypeActionAudit.MOUVEMENT_STOCK,
            "TransfertStock",
            transfertStock.getReference(),
            "Validation transfert reference=" + transfertStock.getReference(),
            transfertStock.getBoutiqueOrigine(),
            currentUser
        );
        return transfertStockMapper.toDto(transfertStock);
    }

    public LigneTransfertStockDTO scanTransfer(Long transfertId, ScanTransfertStockRequest request) {
        TransfertStock transfert = getTransfert(transfertId);
        if (transfert.getStatut() != StatutMouvementStock.BROUILLON) {
            throw new BusinessValidationException("transfertStock", "invalidStatus", "Seul un transfert brouillon peut etre scanne");
        }

        Produit produit = resolveProduct(request.getProduitId(), request.getCodeBarres(), transfert.getBoutiqueOrigine().getId());
        ensureSameBoutique(transfert.getBoutiqueOrigine(), produit);
        LigneTransfertStock ligne = ligneTransfertStockRepository
            .findByTransfert_IdAndProduit_Id(transfertId, produit.getId())
            .orElseGet(() -> new LigneTransfertStock().transfert(transfert).produit(produit).quantite(ZERO));
        ligne.setQuantite(safe(ligne.getQuantite()).add(request.getQuantite()));
        ligne.setCommentaire(request.getCodeBarres() == null ? "Ajout manuel" : "Scan " + request.getCodeBarres().trim());
        return ligneTransfertStockMapper.toDto(ligneTransfertStockRepository.save(ligne));
    }

    public MouvementStockDTO reverseMovement(Long mouvementId, ReverseMouvementStockRequest request) {
        MouvementStock mouvement = getMovement(mouvementId);
        List<MouvementStock> mouvementsToReverse = resolveMovementsToReverse(mouvement);
        User currentUser = getCurrentUser();

        for (MouvementStock original : mouvementsToReverse) {
            ensureMovementCanBeReversed(original);
            if (mouvementStockRepository.existsByReference(buildReverseReference(original.getReference()))) {
                throw new BusinessValidationException(
                    "mouvementStock",
                    "alreadyReversed",
                    "Le mouvement " + original.getReference() + " a deja ete reverse"
                );
            }
        }

        for (MouvementStock original : mouvementsToReverse) {
            for (LigneMouvementStock ligne : ligneMouvementStockRepository.findAllByMouvement_Id(original.getId())) {
                StockProduit stockProduit = getExistingUniqueStock(ligne.getProduit(), ligne.getDepot(), "mouvementStock", "missingStock");
                BigDecimal delta = safe(ligne.getStockApres()).subtract(safe(ligne.getStockAvant()));
                if (delta.signum() > 0 && safe(stockProduit.getQuantiteTheorique()).compareTo(safe(ligne.getStockApres())) != 0) {
                    throw new BusinessValidationException(
                        "mouvementStock",
                        "movementOutdated",
                        "Le mouvement " + original.getReference() + " ne peut plus etre reverse car le stock a evolue"
                    );
                }
            }
        }

        MouvementStock firstReverse = null;
        Instant reversalDate = Instant.now();
        String reversalMotif =
            request == null || request.getMotif() == null || request.getMotif().isBlank() ? "Reverse mouvement" : request.getMotif();

        for (MouvementStock original : mouvementsToReverse) {
            MouvementStock reverse = mouvementStockRepository.save(
                new MouvementStock()
                    .reference(buildReverseReference(original.getReference()))
                    .typeMouvement(original.getTypeMouvement())
                    .statut(StatutMouvementStock.VALIDE)
                    .dateMouvement(reversalDate)
                    .motif(reversalMotif + " " + original.getReference())
                    .boutique(original.getBoutique())
                    .utilisateur(currentUser)
            );
            if (firstReverse == null) {
                firstReverse = reverse;
            }

            for (LigneMouvementStock ligne : ligneMouvementStockRepository.findAllByMouvement_Id(original.getId())) {
                StockProduit stockProduit = getExistingUniqueStock(ligne.getProduit(), ligne.getDepot(), "mouvementStock", "missingStock");
                BigDecimal stockAvant = safe(stockProduit.getQuantiteTheorique());
                BigDecimal delta = safe(ligne.getStockApres()).subtract(safe(ligne.getStockAvant()));
                BigDecimal stockApres = stockAvant.subtract(delta);
                if (stockApres.compareTo(ZERO) < 0) {
                    throw new BusinessValidationException(
                        "mouvementStock",
                        "invalidReverseQuantity",
                        "Le reverse du mouvement " + original.getReference() + " rendrait le stock negatif"
                    );
                }

                stockProduit.setQuantiteTheorique(stockApres);
                stockProduit.setDateDernierMouvement(reversalDate);
                stockProduitRepository.save(stockProduit);

                ligneMouvementStockRepository.save(
                    new LigneMouvementStock()
                        .mouvement(reverse)
                        .produit(ligne.getProduit())
                        .depot(ligne.getDepot())
                        .quantite(delta.abs())
                        .stockAvant(stockAvant)
                        .stockApres(stockApres)
                        .commentaire("Reverse " + original.getReference())
                );
            }

            original.setStatut(StatutMouvementStock.ANNULE);
            mouvementStockRepository.save(original);
            journalAuditService.logAction(
                TypeActionAudit.MOUVEMENT_STOCK,
                "MouvementStock",
                original.getReference(),
                "Reverse mouvement reference=" + original.getReference(),
                original.getBoutique(),
                currentUser
            );
        }

        return mouvementStockMapper.toDto(firstReverse);
    }

    private ReceptionProduit getReception(Long receptionId) {
        ReceptionProduit receptionProduit = receptionProduitRepository
            .findOneWithToOneRelationships(receptionId)
            .orElseThrow(() -> new BusinessValidationException("receptionProduit", "notFound", "Reception introuvable"));
        moduleSecurityService.assertBoutiqueAccess(receptionProduit.getBoutique().getId(), "Acces refuse a la reception produit demandee");
        return receptionProduit;
    }

    private InventaireStock getInventaire(Long inventaireId) {
        InventaireStock inventaireStock = inventaireStockRepository
            .findOneWithToOneRelationships(inventaireId)
            .orElseThrow(() -> new BusinessValidationException("inventaireStock", "notFound", "Inventaire introuvable"));
        moduleSecurityService.assertBoutiqueAccess(inventaireStock.getBoutique().getId(), "Acces refuse a l'inventaire demande");
        return inventaireStock;
    }

    private TransfertStock getTransfert(Long transfertId) {
        TransfertStock transfertStock = transfertStockRepository
            .findOneWithToOneRelationships(transfertId)
            .orElseThrow(() -> new BusinessValidationException("transfertStock", "notFound", "Transfert introuvable"));
        moduleSecurityService.assertAllBoutiquesAccess(
            List.of(transfertStock.getBoutiqueOrigine().getId(), transfertStock.getBoutiqueDestination().getId()),
            "Acces refuse au transfert demande"
        );
        return transfertStock;
    }

    private MouvementStock getMovement(Long mouvementId) {
        MouvementStock mouvementStock = mouvementStockRepository
            .findOneWithToOneRelationships(mouvementId)
            .orElseThrow(() -> new BusinessValidationException("mouvementStock", "notFound", "Mouvement introuvable"));
        moduleSecurityService.assertBoutiqueAccess(mouvementStock.getBoutique().getId(), "Acces refuse au mouvement de stock demande");
        return mouvementStock;
    }

    private Produit resolveProduct(Long produitId, String codeBarres, Long boutiqueId) {
        Produit produit;
        if (produitId != null) {
            produit = produitRepository
                .findById(produitId)
                .orElseThrow(() -> new BusinessValidationException("produit", "notFound", "Produit introuvable"));
            validateProvidedBarcodeForProduct(produitId, codeBarres, boutiqueId);
        } else if (codeBarres != null && !codeBarres.isBlank()) {
            List<CodeBarresProduit> matchingBarcodes =
                boutiqueId == null
                    ? codeBarresProduitRepository.findAllActiveByCode(codeBarres)
                    : codeBarresProduitRepository.findAllActiveByCodeAndBoutiqueId(codeBarres, boutiqueId);
            if (matchingBarcodes.isEmpty()) {
                throw new BusinessValidationException("produit", "barcodeNotFound", "Code-barres actif introuvable");
            }
            if (matchingBarcodes.size() > 1) {
                throw new BusinessValidationException(
                    "produit",
                    "ambiguousBarcode",
                    "Plusieurs produits actifs partagent ce code-barres dans le perimetre courant"
                );
            }
            produit = matchingBarcodes.get(0).getProduit();
        } else {
            throw new BusinessValidationException("produit", "missingReference", "Le produit ou le code-barres doit etre fourni");
        }

        if (produit.getStatut() != StatutGeneral.ACTIF) {
            throw new BusinessValidationException("produit", "inactiveProduct", "Le produit doit etre actif pour ce workflow");
        }
        return produit;
    }

    private void validateProvidedBarcodeForProduct(Long produitId, String codeBarres, Long boutiqueId) {
        if (codeBarres == null || codeBarres.isBlank()) {
            return;
        }

        List<CodeBarresProduit> matchingBarcodes =
            boutiqueId == null
                ? codeBarresProduitRepository.findAllActiveByCode(codeBarres)
                : codeBarresProduitRepository.findAllActiveByCodeAndBoutiqueId(codeBarres, boutiqueId);
        if (matchingBarcodes.size() > 1) {
            throw new BusinessValidationException(
                "produit",
                "ambiguousBarcode",
                "Plusieurs produits actifs partagent ce code-barres dans le perimetre courant"
            );
        }
        if (!matchingBarcodes.isEmpty() && !Objects.equals(matchingBarcodes.get(0).getProduit().getId(), produitId)) {
            throw new BusinessValidationException(
                "produit",
                "barcodeMismatch",
                "Le produit fourni ne correspond pas au code-barres actif selectionne"
            );
        }
    }

    private void ensureSameBoutique(Boutique boutique, Produit produit) {
        if (!produit.getBoutique().getId().equals(boutique.getId())) {
            throw new BusinessValidationException("produit", "invalidBoutique", "Le produit n'appartient pas a la boutique attendue");
        }
    }

    private Produit resolveDestinationProduct(Produit sourceProduit, Boutique destinationBoutique) {
        if (sourceProduit.getBoutique().getId().equals(destinationBoutique.getId())) {
            return sourceProduit;
        }
        return produitRepository
            .findByCodeInterneAndBoutique_Id(sourceProduit.getCodeInterne(), destinationBoutique.getId())
            .filter(produit -> produit.getStatut() == StatutGeneral.ACTIF)
            .orElseThrow(() ->
                new BusinessValidationException(
                    "transfertStock",
                    "missingDestinationProduct",
                    "Aucun produit actif de code interne " + sourceProduit.getCodeInterne() + " n'existe dans la boutique destination"
                )
            );
    }

    private DepotStock getActiveDepot(Long depotId) {
        DepotStock depotStock = depotStockRepository
            .findById(depotId)
            .orElseThrow(() -> new BusinessValidationException("depotStock", "notFound", "Depot introuvable"));
        if (!Boolean.TRUE.equals(depotStock.getActif())) {
            throw new BusinessValidationException("depotStock", "inactiveDepot", "Le depot doit etre actif");
        }
        return depotStock;
    }

    private void ensureMovementCanBeReversed(MouvementStock mouvementStock) {
        if (mouvementStock.getStatut() != StatutMouvementStock.VALIDE) {
            throw new BusinessValidationException("mouvementStock", "invalidStatus", "Seul un mouvement valide peut etre reverse");
        }
        if (mouvementStock.getReference() != null && mouvementStock.getReference().startsWith("REV-")) {
            throw new BusinessValidationException("mouvementStock", "invalidReverse", "Un mouvement de reverse ne peut pas etre reverse");
        }
    }

    private List<MouvementStock> resolveMovementsToReverse(MouvementStock mouvementStock) {
        if (mouvementStock.getTypeMouvement() != TypeMouvementStock.TRANSFERT) {
            return List.of(mouvementStock);
        }
        if (mouvementStock.getReference().startsWith("TRF-OUT-")) {
            String baseReference = mouvementStock.getReference().substring("TRF-OUT-".length());
            return List.of(mouvementStock, findMovementByReference("TRF-IN-" + baseReference));
        }
        if (mouvementStock.getReference().startsWith("TRF-IN-")) {
            String baseReference = mouvementStock.getReference().substring("TRF-IN-".length());
            return List.of(findMovementByReference("TRF-OUT-" + baseReference), mouvementStock);
        }
        return List.of(mouvementStock);
    }

    private MouvementStock findMovementByReference(String reference) {
        return mouvementStockRepository
            .findByReference(reference)
            .orElseThrow(() ->
                new BusinessValidationException("mouvementStock", "notFound", "Mouvement introuvable pour la reference " + reference)
            );
    }

    private String buildReverseReference(String originalReference) {
        return "REV-" + originalReference;
    }

    private StockProduit getExistingUniqueStock(Produit produit, DepotStock depotStock, String entityName, String errorKey) {
        List<StockProduit> stocks = stockProduitRepository.findAllByProduit_IdAndDepot_Id(produit.getId(), depotStock.getId());
        if (stocks.isEmpty()) {
            throw new BusinessValidationException(
                entityName,
                errorKey,
                "Stock introuvable pour le produit " + produit.getDesignation() + " dans le depot " + depotStock.getCode()
            );
        }
        if (stocks.size() > 1) {
            throw new BusinessValidationException(
                "stockProduit",
                "duplicateProductDepot",
                "Plusieurs lignes de stock existent pour ce couple produit/depot"
            );
        }
        return stocks.get(0);
    }

    private StockProduit getOrCreateStock(Produit produit, DepotStock depotStock) {
        ensureSameBoutique(depotStock.getBoutique(), produit);
        List<StockProduit> stocks = stockProduitRepository.findAllByProduit_IdAndDepot_Id(produit.getId(), depotStock.getId());
        if (stocks.size() > 1) {
            throw new BusinessValidationException(
                "stockProduit",
                "duplicateProductDepot",
                "Plusieurs lignes de stock existent pour ce couple produit/depot"
            );
        }
        if (stocks.size() == 1) {
            return stocks.get(0);
        }
        return stockProduitRepository.save(
            new StockProduit()
                .produit(produit)
                .depot(depotStock)
                .quantiteTheorique(ZERO)
                .stockAlerte(null)
                .dateDernierMouvement(Instant.now())
        );
    }

    private User getCurrentUser() {
        return moduleSecurityService.getCurrentUser();
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? ZERO : value;
    }
}
