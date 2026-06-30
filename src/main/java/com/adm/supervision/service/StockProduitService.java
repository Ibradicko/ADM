package com.adm.supervision.service;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.StockProduit;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.DepotStockRepository;
import com.adm.supervision.repository.ProduitRepository;
import com.adm.supervision.repository.StockProduitRepository;
import com.adm.supervision.service.dto.StockProduitDTO;
import com.adm.supervision.service.mapper.StockProduitMapper;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.StockProduit}.
 */
@Service
@Transactional
public class StockProduitService {

    private static final Logger LOG = LoggerFactory.getLogger(StockProduitService.class);

    private final StockProduitRepository stockProduitRepository;

    private final StockProduitMapper stockProduitMapper;

    private final ModuleSecurityService moduleSecurityService;

    private final JournalAuditService journalAuditService;
    private final ProduitRepository produitRepository;
    private final DepotStockRepository depotStockRepository;

    public StockProduitService(
        StockProduitRepository stockProduitRepository,
        StockProduitMapper stockProduitMapper,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService,
        ProduitRepository produitRepository,
        DepotStockRepository depotStockRepository
    ) {
        this.stockProduitRepository = stockProduitRepository;
        this.stockProduitMapper = stockProduitMapper;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
        this.produitRepository = produitRepository;
        this.depotStockRepository = depotStockRepository;
    }

    /**
     * Save a stockProduit.
     *
     * @param stockProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public StockProduitDTO save(StockProduitDTO stockProduitDTO) {
        LOG.debug("Request to save StockProduit : {}", stockProduitDTO);
        StockProduit stockProduit = stockProduitMapper.toEntity(stockProduitDTO);
        hydrateRelationships(stockProduit);
        assertAccessible(stockProduit, "Acces refuse a la ligne de stock a creer");
        validateUniqueProductDepot(stockProduit);
        stockProduit = stockProduitRepository.save(stockProduit);
        audit(TypeActionAudit.CREATION, stockProduit, "Creation ligne stock produit=" + stockProduit.getProduit().getDesignation());
        return stockProduitMapper.toDto(stockProduit);
    }

    /**
     * Update a stockProduit.
     *
     * @param stockProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public StockProduitDTO update(StockProduitDTO stockProduitDTO) {
        LOG.debug("Request to update StockProduit : {}", stockProduitDTO);
        StockProduit stockProduit = stockProduitMapper.toEntity(stockProduitDTO);
        hydrateRelationships(stockProduit);
        assertAccessible(stockProduit, "Acces refuse a la ligne de stock a modifier");
        validateUniqueProductDepot(stockProduit);
        stockProduit = stockProduitRepository.save(stockProduit);
        audit(TypeActionAudit.MODIFICATION, stockProduit, "Modification ligne stock produit=" + stockProduit.getProduit().getDesignation());
        return stockProduitMapper.toDto(stockProduit);
    }

    /**
     * Partially update a stockProduit.
     *
     * @param stockProduitDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockProduitDTO> partialUpdate(StockProduitDTO stockProduitDTO) {
        LOG.debug("Request to partially update StockProduit : {}", stockProduitDTO);

        return stockProduitRepository
            .findById(stockProduitDTO.getId())
            .map(existingStockProduit -> {
                stockProduitMapper.partialUpdate(existingStockProduit, stockProduitDTO);
                assertAccessible(existingStockProduit, "Acces refuse a la ligne de stock a modifier");
                validateUniqueProductDepot(existingStockProduit);
                return existingStockProduit;
            })
            .map(stockProduitRepository::save)
            .map(stockProduit -> {
                audit(
                    TypeActionAudit.MODIFICATION,
                    stockProduit,
                    "Modification partielle ligne stock produit=" + stockProduit.getProduit().getDesignation()
                );
                return stockProduit;
            })
            .map(stockProduitMapper::toDto);
    }

    /**
     * Get all the stockProduits with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<StockProduitDTO> findAllWithEagerRelationships(Pageable pageable) {
        return stockProduitRepository.findAllWithEagerRelationships(pageable).map(stockProduitMapper::toDto);
    }

    /**
     * Get one stockProduit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockProduitDTO> findOne(Long id) {
        LOG.debug("Request to get StockProduit : {}", id);
        return stockProduitRepository
            .findOneWithEagerRelationships(id)
            .map(stockProduit -> {
                assertAccessible(stockProduit, "Acces refuse a la ligne de stock demandee");
                return stockProduit;
            })
            .map(stockProduitMapper::toDto);
    }

    /**
     * Delete the stockProduit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete StockProduit : {}", id);
        StockProduit stockProduit = stockProduitRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("stockProduit", "notFound", "Ligne de stock introuvable"));
        assertAccessible(stockProduit, "Acces refuse a la ligne de stock a supprimer");
        audit(TypeActionAudit.DESACTIVATION, stockProduit, "Suppression ligne stock produit=" + stockProduit.getProduit().getDesignation());
        stockProduitRepository.deleteById(id);
    }

    private void validateUniqueProductDepot(StockProduit stockProduit) {
        if (
            stockProduit.getProduit() == null ||
            stockProduit.getProduit().getId() == null ||
            stockProduit.getDepot() == null ||
            stockProduit.getDepot().getId() == null
        ) {
            return;
        }
        Long produitBoutiqueId = produitRepository
            .findById(stockProduit.getProduit().getId())
            .map(produit -> produit.getBoutique().getId())
            .orElseThrow(() -> new BusinessValidationException("stockProduit", "productNotFound", "Produit introuvable"));
        Long depotBoutiqueId = depotStockRepository
            .findById(stockProduit.getDepot().getId())
            .map(depot -> depot.getBoutique().getId())
            .orElseThrow(() -> new BusinessValidationException("stockProduit", "depotNotFound", "Depot introuvable"));
        if (!Objects.equals(produitBoutiqueId, depotBoutiqueId)) {
            throw new BusinessValidationException(
                "stockProduit",
                "invalidBoutique",
                "Le produit et le depot doivent appartenir a la meme boutique"
            );
        }

        List<StockProduit> existingStocks = stockProduitRepository.findAllByProduit_IdAndDepot_Id(
            stockProduit.getProduit().getId(),
            stockProduit.getDepot().getId()
        );
        boolean duplicateExists = existingStocks.stream().anyMatch(existing -> !Objects.equals(existing.getId(), stockProduit.getId()));
        if (duplicateExists) {
            throw new BusinessValidationException(
                "stockProduit",
                "duplicateProductDepot",
                "Une ligne de stock existe deja pour ce couple produit/depot"
            );
        }
    }

    private void hydrateRelationships(StockProduit stockProduit) {
        if (stockProduit.getProduit() != null && stockProduit.getProduit().getId() != null) {
            stockProduit.setProduit(
                produitRepository
                    .findById(stockProduit.getProduit().getId())
                    .orElseThrow(() -> new BusinessValidationException("stockProduit", "productNotFound", "Produit introuvable"))
            );
        }
        if (stockProduit.getDepot() != null && stockProduit.getDepot().getId() != null) {
            stockProduit.setDepot(
                depotStockRepository
                    .findById(stockProduit.getDepot().getId())
                    .orElseThrow(() -> new BusinessValidationException("stockProduit", "depotNotFound", "Depot introuvable"))
            );
        }
    }

    private void assertAccessible(StockProduit stockProduit, String message) {
        Boutique boutique = stockProduit.getDepot() == null ? null : stockProduit.getDepot().getBoutique();
        if (boutique == null || boutique.getId() == null) {
            throw new BusinessValidationException(
                "stockProduit",
                "missingBoutique",
                "La ligne de stock doit etre rattachee a une boutique"
            );
        }
        moduleSecurityService.assertBoutiqueAccess(boutique.getId(), message);
    }

    private void audit(TypeActionAudit action, StockProduit stockProduit, String description) {
        journalAuditService.logAction(
            action,
            "StockProduit",
            String.valueOf(stockProduit.getId()),
            description,
            stockProduit.getDepot().getBoutique(),
            moduleSecurityService.getCurrentUser()
        );
    }
}
