package com.adm.supervision.service;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.ExploitationBoutique;
import com.adm.supervision.domain.LigneMouvementStock;
import com.adm.supervision.domain.LigneVente;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.domain.ModePaiementRef;
import com.adm.supervision.domain.MouvementStock;
import com.adm.supervision.domain.PaiementVente;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.StockProduit;
import com.adm.supervision.domain.TicketCaisse;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.StatutMouvementStock;
import com.adm.supervision.domain.enumeration.StatutPaiement;
import com.adm.supervision.domain.enumeration.StatutVente;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.domain.enumeration.TypeMouvementStock;
import com.adm.supervision.repository.BoutiqueRepository;
import com.adm.supervision.repository.ExploitationBoutiqueRepository;
import com.adm.supervision.repository.LigneMouvementStockRepository;
import com.adm.supervision.repository.LigneVenteRepository;
import com.adm.supervision.repository.LocataireRepository;
import com.adm.supervision.repository.ModePaiementRefRepository;
import com.adm.supervision.repository.MouvementStockRepository;
import com.adm.supervision.repository.PaiementVenteRepository;
import com.adm.supervision.repository.ProduitRepository;
import com.adm.supervision.repository.StockProduitRepository;
import com.adm.supervision.repository.TicketCaisseRepository;
import com.adm.supervision.repository.VenteRepository;
import com.adm.supervision.service.dto.CaissePosteArticleDTO;
import com.adm.supervision.service.dto.CaissePosteContexteDTO;
import com.adm.supervision.service.dto.CaisseVenteLigneRequest;
import com.adm.supervision.service.dto.CaisseVentePaiementRequest;
import com.adm.supervision.service.dto.CaisseVenteRequest;
import com.adm.supervision.service.dto.CaisseVenteResultDTO;
import com.adm.supervision.service.dto.VenteDTO;
import com.adm.supervision.service.mapper.BoutiqueMapper;
import com.adm.supervision.service.mapper.LigneVenteMapper;
import com.adm.supervision.service.mapper.LocataireMapper;
import com.adm.supervision.service.mapper.ModePaiementRefMapper;
import com.adm.supervision.service.mapper.PaiementVenteMapper;
import com.adm.supervision.service.mapper.TicketCaisseMapper;
import com.adm.supervision.service.mapper.VenteMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.Vente}.
 */
@Service
@Transactional
public class VenteService {

    private static final Logger LOG = LoggerFactory.getLogger(VenteService.class);
    private static final DateTimeFormatter TICKET_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss", Locale.ROOT).withZone(
        ZoneOffset.UTC
    );

    private final VenteRepository venteRepository;

    private final VenteMapper venteMapper;
    private final LigneVenteMapper ligneVenteMapper;
    private final PaiementVenteMapper paiementVenteMapper;
    private final TicketCaisseMapper ticketCaisseMapper;

    private final ModuleSecurityService moduleSecurityService;

    private final JournalAuditService journalAuditService;
    private final LigneVenteRepository ligneVenteRepository;
    private final StockProduitRepository stockProduitRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final LigneMouvementStockRepository ligneMouvementStockRepository;
    private final BoutiqueRepository boutiqueRepository;
    private final LocataireRepository locataireRepository;
    private final ProduitRepository produitRepository;
    private final ModePaiementRefRepository modePaiementRefRepository;
    private final PaiementVenteRepository paiementVenteRepository;
    private final TicketCaisseRepository ticketCaisseRepository;
    private final ExploitationBoutiqueRepository exploitationBoutiqueRepository;
    private final RedevanceWorkflowService redevanceWorkflowService;
    private final BoutiqueMapper boutiqueMapper;
    private final LocataireMapper locataireMapper;
    private final ModePaiementRefMapper modePaiementRefMapper;

    public VenteService(
        VenteRepository venteRepository,
        VenteMapper venteMapper,
        LigneVenteMapper ligneVenteMapper,
        PaiementVenteMapper paiementVenteMapper,
        TicketCaisseMapper ticketCaisseMapper,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService,
        LigneVenteRepository ligneVenteRepository,
        StockProduitRepository stockProduitRepository,
        MouvementStockRepository mouvementStockRepository,
        LigneMouvementStockRepository ligneMouvementStockRepository,
        BoutiqueRepository boutiqueRepository,
        LocataireRepository locataireRepository,
        ProduitRepository produitRepository,
        ModePaiementRefRepository modePaiementRefRepository,
        PaiementVenteRepository paiementVenteRepository,
        TicketCaisseRepository ticketCaisseRepository,
        ExploitationBoutiqueRepository exploitationBoutiqueRepository,
        RedevanceWorkflowService redevanceWorkflowService,
        BoutiqueMapper boutiqueMapper,
        LocataireMapper locataireMapper,
        ModePaiementRefMapper modePaiementRefMapper
    ) {
        this.venteRepository = venteRepository;
        this.venteMapper = venteMapper;
        this.ligneVenteMapper = ligneVenteMapper;
        this.paiementVenteMapper = paiementVenteMapper;
        this.ticketCaisseMapper = ticketCaisseMapper;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
        this.ligneVenteRepository = ligneVenteRepository;
        this.stockProduitRepository = stockProduitRepository;
        this.mouvementStockRepository = mouvementStockRepository;
        this.ligneMouvementStockRepository = ligneMouvementStockRepository;
        this.boutiqueRepository = boutiqueRepository;
        this.locataireRepository = locataireRepository;
        this.produitRepository = produitRepository;
        this.modePaiementRefRepository = modePaiementRefRepository;
        this.paiementVenteRepository = paiementVenteRepository;
        this.ticketCaisseRepository = ticketCaisseRepository;
        this.exploitationBoutiqueRepository = exploitationBoutiqueRepository;
        this.redevanceWorkflowService = redevanceWorkflowService;
        this.boutiqueMapper = boutiqueMapper;
        this.locataireMapper = locataireMapper;
        this.modePaiementRefMapper = modePaiementRefMapper;
    }

    /**
     * Save a vente.
     *
     * @param venteDTO the entity to save.
     * @return the persisted entity.
     */
    public VenteDTO save(VenteDTO venteDTO) {
        LOG.debug("Request to save Vente : {}", venteDTO);
        Vente vente = venteMapper.toEntity(venteDTO);
        if (vente.getStatut() != StatutVente.BROUILLON) {
            throw immutableSale("Une vente doit etre creee au statut brouillon");
        }
        assertAccessible(vente, "Acces refuse a la vente a creer");
        vente = venteRepository.save(vente);
        audit(TypeActionAudit.CREATION, vente, "Creation vente numero=" + vente.getNumeroTicket());
        return venteMapper.toDto(vente);
    }

    /**
     * Create a complete cash desk sale in one transaction.
     *
     * @param request cash desk sale payload.
     * @return persisted sale, lines, payments and generated ticket.
     */
    public CaisseVenteResultDTO checkout(CaisseVenteRequest request) {
        LOG.debug("Request to checkout Vente : {}", request);
        Boutique boutique = boutiqueRepository
            .findById(request.getBoutiqueId())
            .orElseThrow(() -> new BusinessValidationException("boutique", "notFound", "Boutique introuvable"));
        moduleSecurityService.assertBoutiqueAccess(boutique.getId(), "Acces refuse a la boutique de vente");

        Locataire locataire = locataireRepository
            .findById(request.getLocataireId())
            .orElseThrow(() -> new BusinessValidationException("locataire", "notFound", "Locataire introuvable"));
        if (
            !exploitationBoutiqueRepository.existsByBoutique_IdAndLocataire_IdAndStatut(
                boutique.getId(),
                locataire.getId(),
                StatutGeneral.ACTIF
            )
        ) {
            throw new BusinessValidationException(
                "exploitationBoutique",
                "inactiveTenant",
                "Ce locataire n exploite pas activement cette boutique"
            );
        }

        User vendeur = moduleSecurityService.getCurrentUser();
        List<PreparedSaleLine> lignesPreparees = prepareLines(request.getLignes(), boutique);
        List<PreparedSalePayment> paiementsPrepares = preparePayments(request.getPaiements());

        BigDecimal montantBrut = scaleMoney(
            lignesPreparees.stream().map(PreparedSaleLine::grossAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        BigDecimal montantRemise = scaleMoney(
            lignesPreparees.stream().map(PreparedSaleLine::discountAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
        );
        BigDecimal montantNet = scaleMoney(montantBrut.subtract(montantRemise));
        BigDecimal totalPaiements = scaleMoney(
            paiementsPrepares.stream().map(PreparedSalePayment::amount).reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        if (montantNet.signum() <= 0) {
            throw new BusinessValidationException("vente", "invalidTotal", "Le total net de la vente doit etre positif");
        }
        if (totalPaiements.compareTo(montantNet) != 0) {
            throw new BusinessValidationException("paiementVente", "paymentMismatch", "Le total des paiements doit solder la vente");
        }

        Vente vente = venteRepository.save(
            new Vente()
                .numeroTicket(generateSaleNumber())
                .dateHeure(Instant.now())
                .statut(StatutVente.BROUILLON)
                .referencePassager(blankToNull(request.getReferencePassager()))
                .referenceCarteEmbarquement(blankToNull(request.getReferenceCarteEmbarquement()))
                .montantBrut(montantBrut)
                .montantRemise(montantRemise)
                .montantNet(montantNet)
                .commentaire(blankToNull(request.getCommentaire()))
                .boutique(boutique)
                .locataire(locataire)
                .vendeur(vendeur)
        );
        audit(TypeActionAudit.CREATION, vente, "Creation vente caisse numero=" + vente.getNumeroTicket());

        List<LigneVente> lignes = new ArrayList<>();
        for (PreparedSaleLine lignePreparee : lignesPreparees) {
            lignes.add(
                ligneVenteRepository.save(
                    new LigneVente()
                        .vente(vente)
                        .produit(lignePreparee.product())
                        .quantite(lignePreparee.quantity())
                        .prixUnitaire(lignePreparee.unitPrice())
                        .remise(lignePreparee.discountAmount())
                        .montantLigne(lignePreparee.netAmount())
                        .codeBarresScanne(blankToNull(lignePreparee.scannedBarcode()))
                )
            );
        }

        List<PaiementVente> paiements = new ArrayList<>();
        for (PreparedSalePayment paiementPrepare : paiementsPrepares) {
            PaiementVente paiement = paiementVenteRepository.save(
                new PaiementVente()
                    .vente(vente)
                    .modePaiement(paiementPrepare.paymentMode())
                    .montant(paiementPrepare.amount())
                    .statut(StatutPaiement.PAYE)
                    .referencePaiement(blankToNull(paiementPrepare.reference()))
                    .datePaiement(Instant.now())
            );
            paiements.add(paiement);
            journalAuditService.logAction(
                TypeActionAudit.CREATION,
                "PaiementVente",
                paiement.getReferencePaiement(),
                "Creation paiement vente caisse numero=" + vente.getNumeroTicket(),
                boutique,
                vendeur
            );
        }

        applySaleStockMovement(vente);
        vente.setStatut(StatutVente.VALIDEE);
        vente = venteRepository.save(vente);
        audit(TypeActionAudit.VENTE_VALIDEE, vente, "Validation vente caisse numero=" + vente.getNumeroTicket());

        TicketCaisse ticket = ticketCaisseRepository.save(
            new TicketCaisse()
                .numero("TC-" + vente.getNumeroTicket())
                .dateEmission(Instant.now())
                .nombreImpressions(1)
                .contenu(buildTicketContent(vente, lignes, paiements))
                .vente(vente)
        );
        journalAuditService.logAction(
            TypeActionAudit.CREATION,
            "TicketCaisse",
            ticket.getNumero(),
            "Creation ticket caisse numero=" + ticket.getNumero(),
            boutique,
            vendeur
        );
        redevanceWorkflowService.generateForValidatedSale(vente);

        CaisseVenteResultDTO result = new CaisseVenteResultDTO();
        result.setVente(venteMapper.toDto(vente));
        result.setLignes(lignes.stream().map(ligneVenteMapper::toDto).toList());
        result.setPaiements(paiements.stream().map(paiementVenteMapper::toDto).toList());
        result.setTicket(ticketCaisseMapper.toDto(ticket));
        return result;
    }

    /**
     * Resolve the full server-side context needed by the cash desk screen: the active shop (and
     * every shop the current user may switch to), its currently exploiting tenant, every active
     * product of that shop merged with its aggregated stock, and the active payment modes.
     * <p>
     * This is the single source of truth for "which shop/articles can this user sell" : it reuses
     * exactly the same accessible-shop resolution as every other write operation
     * ({@link ModuleSecurityService#getAccessibleBoutiqueIds()}), so the frontend can no longer
     * drift out of sync with the backend security scope (a stale/looser client-side computation
     * was the root cause of articles being invisible at checkout while still visible elsewhere).
     *
     * @param boutiqueIdDemande optional shop requested by the client ; ignored if not accessible.
     * @return the resolved cash desk context.
     */
    @Transactional(readOnly = true)
    public CaissePosteContexteDTO getContextePoste(Long boutiqueIdDemande) {
        List<Boutique> boutiquesAccessibles = resoudreBoutiquesAccessibles();
        if (boutiquesAccessibles.isEmpty()) {
            throw new BusinessValidationException("caisse", "noShopAccess", "Aucune boutique accessible pour ce compte");
        }

        Boutique boutique = boutiquesAccessibles
            .stream()
            .filter(candidate -> boutiqueIdDemande != null && candidate.getId().equals(boutiqueIdDemande))
            .findFirst()
            .orElseGet(() -> choisirBoutiqueParDefaut(boutiquesAccessibles));

        Locataire locataire = exploitationBoutiqueRepository
            .findByBoutique_IdAndStatut(boutique.getId(), StatutGeneral.ACTIF)
            .stream()
            .findFirst()
            .map(ExploitationBoutique::getLocataire)
            .orElse(null);

        List<Produit> produits = produitRepository.findByBoutique_IdAndStatut(
            boutique.getId(),
            StatutGeneral.ACTIF,
            Sort.by("designation")
        );
        Map<Long, BigDecimal> stockParProduit = new HashMap<>();
        for (StockProduit stock : stockProduitRepository.findByBoutiqueId(boutique.getId())) {
            if (stock.getProduit() == null || stock.getProduit().getId() == null) {
                continue;
            }
            BigDecimal quantite = stock.getQuantiteTheorique() == null ? BigDecimal.ZERO : stock.getQuantiteTheorique();
            stockParProduit.merge(stock.getProduit().getId(), quantite, BigDecimal::add);
        }

        List<CaissePosteArticleDTO> articles = produits
            .stream()
            .map(produit -> toArticleDTO(produit, stockParProduit.getOrDefault(produit.getId(), BigDecimal.ZERO)))
            .toList();

        CaissePosteContexteDTO contexte = new CaissePosteContexteDTO();
        contexte.setBoutique(boutiqueMapper.toDto(boutique));
        contexte.setBoutiquesAccessibles(boutiquesAccessibles.stream().map(boutiqueMapper::toDto).toList());
        contexte.setLocataire(locataire == null ? null : locataireMapper.toDto(locataire));
        contexte.setArticles(articles);
        contexte.setModesPaiement(
            modePaiementRefRepository
                .findAll()
                .stream()
                .filter(mode -> Boolean.TRUE.equals(mode.getActif()))
                .map(modePaiementRefMapper::toDto)
                .toList()
        );
        return contexte;
    }

    private List<Boutique> resoudreBoutiquesAccessibles() {
        if (moduleSecurityService.hasGlobalBoutiqueAccess()) {
            return boutiqueRepository.findAll(Sort.by("nom"));
        }

        Set<Long> ids = moduleSecurityService.getAccessibleBoutiqueIds();
        if (ids.isEmpty()) {
            return List.of();
        }
        return boutiqueRepository
            .findAllById(ids)
            .stream()
            .sorted(Comparator.comparing(Boutique::getNom, Comparator.nullsLast(String::compareToIgnoreCase)))
            .toList();
    }

    private Boutique choisirBoutiqueParDefaut(List<Boutique> boutiquesAccessibles) {
        return boutiquesAccessibles
            .stream()
            .filter(candidate -> produitRepository.existsByBoutique_IdAndStatut(candidate.getId(), StatutGeneral.ACTIF))
            .findFirst()
            .orElse(boutiquesAccessibles.get(0));
    }

    private CaissePosteArticleDTO toArticleDTO(Produit produit, BigDecimal stockDisponible) {
        CaissePosteArticleDTO dto = new CaissePosteArticleDTO();
        dto.setProduitId(produit.getId());
        dto.setCodeInterne(produit.getCodeInterne());
        dto.setDesignation(produit.getDesignation());
        dto.setDescription(produit.getDescription());
        dto.setPrixVente(produit.getPrixVente());
        if (produit.getGroupeArticle() != null) {
            dto.setGroupeArticleId(produit.getGroupeArticle().getId());
            dto.setGroupeArticleLibelle(produit.getGroupeArticle().getLibelle());
        }
        dto.setStockDisponible(stockDisponible);
        return dto;
    }

    /**
     * Update a vente.
     *
     * @param venteDTO the entity to save.
     * @return the persisted entity.
     */
    public VenteDTO update(VenteDTO venteDTO) {
        LOG.debug("Request to update Vente : {}", venteDTO);
        Vente existingVente = getExistingVente(venteDTO.getId());
        assertDirectUpdateAllowed(existingVente, venteDTO.getStatut());
        Vente vente = venteMapper.toEntity(venteDTO);
        assertAccessible(vente, "Acces refuse a la vente a modifier");
        if (existingVente.getStatut() == StatutVente.BROUILLON && vente.getStatut() == StatutVente.VALIDEE) {
            applySaleStockMovement(existingVente);
        }
        vente = venteRepository.save(vente);
        auditUpdate(existingVente, vente, "Modification vente numero=" + vente.getNumeroTicket());
        return venteMapper.toDto(vente);
    }

    /**
     * Partially update a vente.
     *
     * @param venteDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VenteDTO> partialUpdate(VenteDTO venteDTO) {
        LOG.debug("Request to partially update Vente : {}", venteDTO);

        return venteRepository
            .findById(venteDTO.getId())
            .map(existingVente -> {
                StatutVente targetStatus = venteDTO.getStatut() == null ? existingVente.getStatut() : venteDTO.getStatut();
                assertDirectUpdateAllowed(existingVente, targetStatus);
                if (existingVente.getStatut() == StatutVente.BROUILLON && targetStatus == StatutVente.VALIDEE) {
                    applySaleStockMovement(existingVente);
                }
                venteMapper.partialUpdate(existingVente, venteDTO);
                assertAccessible(existingVente, "Acces refuse a la vente a modifier");

                return existingVente;
            })
            .map(venteRepository::save)
            .map(vente -> {
                TypeActionAudit action =
                    vente.getStatut() == StatutVente.VALIDEE ? TypeActionAudit.VENTE_VALIDEE : TypeActionAudit.MODIFICATION;
                audit(action, vente, "Modification partielle vente numero=" + vente.getNumeroTicket());
                return vente;
            })
            .map(venteMapper::toDto);
    }

    /**
     * Get all the ventes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<VenteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return venteRepository.findAllWithEagerRelationships(pageable).map(venteMapper::toDto);
    }

    /**
     * Get one vente by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VenteDTO> findOne(Long id) {
        LOG.debug("Request to get Vente : {}", id);
        return venteRepository
            .findOneWithEagerRelationships(id)
            .map(vente -> {
                assertAccessible(vente, "Acces refuse a la vente demandee");
                return vente;
            })
            .map(venteMapper::toDto);
    }

    /**
     * Delete the vente by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Vente : {}", id);
        Vente vente = venteRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("vente", "notFound", "Vente introuvable"));
        assertAccessible(vente, "Acces refuse a la vente a supprimer");
        if (vente.getStatut() != StatutVente.BROUILLON) {
            throw immutableSale("Une vente validee ou corrigee ne peut pas etre supprimee");
        }
        audit(TypeActionAudit.DESACTIVATION, vente, "Suppression vente numero=" + vente.getNumeroTicket());
        venteRepository.deleteById(id);
    }

    private Vente getExistingVente(Long id) {
        return venteRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("vente", "notFound", "Vente introuvable"));
    }

    private void assertDirectUpdateAllowed(Vente existingVente, StatutVente targetStatus) {
        assertAccessible(existingVente, "Acces refuse a la vente a modifier");
        if (existingVente.getStatut() != StatutVente.BROUILLON) {
            throw immutableSale("Une vente validee ne peut etre modifiee directement; utilisez une operation corrective");
        }
        if (targetStatus != StatutVente.BROUILLON && targetStatus != StatutVente.VALIDEE) {
            throw immutableSale("Une annulation, un retour ou un ajustement doit passer par une operation corrective");
        }
    }

    private void auditUpdate(Vente previous, Vente updated, String description) {
        TypeActionAudit action =
            previous.getStatut() == StatutVente.BROUILLON && updated.getStatut() == StatutVente.VALIDEE
                ? TypeActionAudit.VENTE_VALIDEE
                : TypeActionAudit.MODIFICATION;
        audit(action, updated, description);
    }

    private BusinessValidationException immutableSale(String message) {
        return new BusinessValidationException("vente", "validatedSaleImmutable", message);
    }

    private void applySaleStockMovement(Vente vente) {
        String reference = "VTE-" + vente.getNumeroTicket();
        if (mouvementStockRepository.existsByReference(reference)) {
            throw new BusinessValidationException("vente", "stockAlreadyUpdated", "Le stock de cette vente a deja ete mis a jour");
        }

        List<LigneVente> lignes = ligneVenteRepository.findAllByVente_Id(vente.getId());
        if (lignes.isEmpty()) {
            throw new BusinessValidationException("vente", "emptySale", "Une vente sans ligne ne peut pas etre validee");
        }

        MouvementStock mouvement = mouvementStockRepository.save(
            new MouvementStock()
                .reference(reference)
                .typeMouvement(TypeMouvementStock.VENTE)
                .statut(StatutMouvementStock.VALIDE)
                .dateMouvement(Instant.now())
                .motif("Validation vente " + vente.getNumeroTicket())
                .boutique(vente.getBoutique())
                .utilisateur(moduleSecurityService.getCurrentUser())
        );

        for (LigneVente ligne : lignes) {
            if (!ligne.getProduit().getBoutique().getId().equals(vente.getBoutique().getId())) {
                throw new BusinessValidationException(
                    "vente",
                    "invalidProductBoutique",
                    "Un produit de la vente appartient a une autre boutique"
                );
            }
            BigDecimal restante = ligne.getQuantite();
            List<StockProduit> stocks = stockProduitRepository.findByProduitIdAndBoutiqueId(
                ligne.getProduit().getId(),
                vente.getBoutique().getId()
            );
            BigDecimal disponible = stocks
                .stream()
                .map(StockProduit::getQuantiteTheorique)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (disponible.compareTo(restante) < 0) {
                throw new BusinessValidationException(
                    "vente",
                    "insufficientStock",
                    "Stock insuffisant pour le produit " + ligne.getProduit().getDesignation()
                );
            }

            for (StockProduit stock : stocks) {
                if (restante.signum() == 0) {
                    break;
                }
                BigDecimal avant = stock.getQuantiteTheorique();
                BigDecimal prelevee = avant.min(restante);
                if (prelevee.signum() == 0) {
                    continue;
                }
                BigDecimal apres = avant.subtract(prelevee);
                stock.setQuantiteTheorique(apres);
                stock.setDateDernierMouvement(Instant.now());
                stockProduitRepository.save(stock);
                ligneMouvementStockRepository.save(
                    new LigneMouvementStock()
                        .mouvement(mouvement)
                        .produit(ligne.getProduit())
                        .depot(stock.getDepot())
                        .quantite(prelevee)
                        .stockAvant(avant)
                        .stockApres(apres)
                        .commentaire("Vente " + vente.getNumeroTicket())
                );
                restante = restante.subtract(prelevee);
            }
        }
    }

    private List<PreparedSaleLine> prepareLines(List<CaisseVenteLigneRequest> lineRequests, Boutique boutique) {
        if (lineRequests == null || lineRequests.isEmpty()) {
            throw new BusinessValidationException("ligneVente", "emptySale", "Une vente doit contenir au moins une ligne");
        }

        List<PreparedSaleLine> lines = new ArrayList<>();
        for (CaisseVenteLigneRequest lineRequest : lineRequests) {
            Produit produit = produitRepository
                .findOneWithEagerRelationships(lineRequest.getProduitId())
                .orElseThrow(() -> new BusinessValidationException("produit", "notFound", "Produit introuvable"));
            if (produit.getBoutique() == null || !produit.getBoutique().getId().equals(boutique.getId())) {
                throw new BusinessValidationException(
                    "produit",
                    "invalidBoutique",
                    "Un produit de la vente appartient a une autre boutique"
                );
            }
            if (produit.getStatut() != StatutGeneral.ACTIF) {
                throw new BusinessValidationException(
                    "produit",
                    "inactiveProduct",
                    "Le produit " + produit.getDesignation() + " est inactif"
                );
            }

            BigDecimal quantity = positiveMoney(
                lineRequest.getQuantite(),
                "ligneVente",
                "invalidQuantity",
                "La quantite doit etre positive"
            );
            BigDecimal discount = nonNegativeMoney(
                lineRequest.getRemise(),
                "ligneVente",
                "invalidDiscount",
                "La remise ne peut pas etre negative"
            );
            BigDecimal unitPrice = nonNegativeMoney(
                produit.getPrixVente(),
                "produit",
                "invalidSalePrice",
                "Le prix de vente du produit doit etre renseigne"
            );
            BigDecimal grossAmount = scaleMoney(unitPrice.multiply(quantity));
            if (discount.compareTo(grossAmount) > 0) {
                throw new BusinessValidationException(
                    "ligneVente",
                    "discountTooHigh",
                    "La remise ne peut pas depasser le montant de la ligne"
                );
            }

            lines.add(
                new PreparedSaleLine(
                    produit,
                    quantity,
                    unitPrice,
                    discount,
                    grossAmount,
                    scaleMoney(grossAmount.subtract(discount)),
                    lineRequest.getCodeBarresScanne()
                )
            );
        }
        return lines;
    }

    private List<PreparedSalePayment> preparePayments(List<CaisseVentePaiementRequest> paymentRequests) {
        if (paymentRequests == null || paymentRequests.isEmpty()) {
            throw new BusinessValidationException("paiementVente", "missingPayment", "Une vente doit contenir au moins un paiement");
        }

        List<PreparedSalePayment> payments = new ArrayList<>();
        for (CaisseVentePaiementRequest paymentRequest : paymentRequests) {
            ModePaiementRef modePaiement = modePaiementRefRepository
                .findById(paymentRequest.getModePaiementId())
                .orElseThrow(() -> new BusinessValidationException("modePaiementRef", "notFound", "Mode de paiement introuvable"));
            if (Boolean.FALSE.equals(modePaiement.getActif())) {
                throw new BusinessValidationException("modePaiementRef", "inactivePaymentMode", "Ce mode de paiement est inactif");
            }
            payments.add(
                new PreparedSalePayment(
                    modePaiement,
                    positiveMoney(
                        paymentRequest.getMontant(),
                        "paiementVente",
                        "invalidAmount",
                        "Le montant du paiement doit etre positif"
                    ),
                    paymentRequest.getReferencePaiement()
                )
            );
        }
        return payments;
    }

    private BigDecimal positiveMoney(BigDecimal value, String entity, String key, String message) {
        BigDecimal amount = nonNegativeMoney(value, entity, key, message);
        if (amount.signum() <= 0) {
            throw new BusinessValidationException(entity, key, message);
        }
        return amount;
    }

    private BigDecimal nonNegativeMoney(BigDecimal value, String entity, String key, String message) {
        if (value == null || value.signum() < 0) {
            throw new BusinessValidationException(entity, key, message);
        }
        return scaleMoney(value);
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP);
    }

    private String generateSaleNumber() {
        return "VT-" + TICKET_DATE_FORMAT.format(Instant.now()) + "-" + ThreadLocalRandom.current().nextInt(100, 1000);
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String buildTicketContent(Vente vente, List<LigneVente> lignes, List<PaiementVente> paiements) {
        StringBuilder builder = new StringBuilder();
        builder.append("Ticket ADM\n");
        builder.append("Numero: ").append(vente.getNumeroTicket()).append('\n');
        builder.append("Date: ").append(vente.getDateHeure()).append('\n');
        builder.append("Boutique: ").append(vente.getBoutique().getNom()).append('\n');
        builder.append("Locataire: ").append(vente.getLocataire().getNom()).append('\n');
        builder.append("Vendeur: ").append(vente.getVendeur().getLogin()).append("\n\n");
        builder.append("Articles\n");
        for (LigneVente ligne : lignes) {
            builder
                .append("- ")
                .append(ligne.getProduit().getDesignation())
                .append(" x")
                .append(ligne.getQuantite())
                .append(" : ")
                .append(ligne.getMontantLigne())
                .append('\n');
        }
        builder.append("\nPaiements\n");
        for (PaiementVente paiement : paiements) {
            builder.append("- ").append(paiement.getModePaiement().getLibelle()).append(" : ").append(paiement.getMontant()).append('\n');
        }
        builder.append("\nTotal net: ").append(vente.getMontantNet()).append('\n');
        return builder.toString();
    }

    private record PreparedSaleLine(
        Produit product,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal grossAmount,
        BigDecimal netAmount,
        String scannedBarcode
    ) {}

    private record PreparedSalePayment(ModePaiementRef paymentMode, BigDecimal amount, String reference) {}

    private void assertAccessible(Vente vente, String message) {
        Boutique boutique = vente.getBoutique();
        if (boutique == null || boutique.getId() == null) {
            throw new BusinessValidationException("vente", "missingBoutique", "La vente doit etre rattachee a une boutique");
        }
        moduleSecurityService.assertBoutiqueAccess(boutique.getId(), message);
    }

    private void audit(TypeActionAudit action, Vente vente, String description) {
        journalAuditService.logAction(
            action,
            "Vente",
            vente.getNumeroTicket(),
            description,
            vente.getBoutique(),
            moduleSecurityService.getCurrentUser()
        );
    }
}
