package com.adm.supervision.service;

import com.adm.supervision.domain.InventaireStock;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.InventaireStockRepository;
import com.adm.supervision.service.dto.InventaireStockDTO;
import com.adm.supervision.service.mapper.InventaireStockMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.InventaireStock}.
 */
@Service
@Transactional
public class InventaireStockService {

    private static final Logger LOG = LoggerFactory.getLogger(InventaireStockService.class);

    private final InventaireStockRepository inventaireStockRepository;

    private final InventaireStockMapper inventaireStockMapper;

    private final ModuleSecurityService moduleSecurityService;

    private final JournalAuditService journalAuditService;

    public InventaireStockService(
        InventaireStockRepository inventaireStockRepository,
        InventaireStockMapper inventaireStockMapper,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService
    ) {
        this.inventaireStockRepository = inventaireStockRepository;
        this.inventaireStockMapper = inventaireStockMapper;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
    }

    /**
     * Save a inventaireStock.
     *
     * @param inventaireStockDTO the entity to save.
     * @return the persisted entity.
     */
    public InventaireStockDTO save(InventaireStockDTO inventaireStockDTO) {
        LOG.debug("Request to save InventaireStock : {}", inventaireStockDTO);
        InventaireStock inventaireStock = inventaireStockMapper.toEntity(inventaireStockDTO);
        assertAccessible(inventaireStock, "Acces refuse a l'inventaire a creer");
        inventaireStock = inventaireStockRepository.save(inventaireStock);
        audit(TypeActionAudit.CREATION, inventaireStock, "Creation inventaire reference=" + inventaireStock.getReference());
        return inventaireStockMapper.toDto(inventaireStock);
    }

    /**
     * Update a inventaireStock.
     *
     * @param inventaireStockDTO the entity to save.
     * @return the persisted entity.
     */
    public InventaireStockDTO update(InventaireStockDTO inventaireStockDTO) {
        LOG.debug("Request to update InventaireStock : {}", inventaireStockDTO);
        InventaireStock inventaireStock = inventaireStockMapper.toEntity(inventaireStockDTO);
        assertAccessible(inventaireStock, "Acces refuse a l'inventaire a modifier");
        inventaireStock = inventaireStockRepository.save(inventaireStock);
        audit(TypeActionAudit.MODIFICATION, inventaireStock, "Modification inventaire reference=" + inventaireStock.getReference());
        return inventaireStockMapper.toDto(inventaireStock);
    }

    /**
     * Partially update a inventaireStock.
     *
     * @param inventaireStockDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InventaireStockDTO> partialUpdate(InventaireStockDTO inventaireStockDTO) {
        LOG.debug("Request to partially update InventaireStock : {}", inventaireStockDTO);

        return inventaireStockRepository
            .findById(inventaireStockDTO.getId())
            .map(existingInventaireStock -> {
                inventaireStockMapper.partialUpdate(existingInventaireStock, inventaireStockDTO);
                assertAccessible(existingInventaireStock, "Acces refuse a l'inventaire a modifier");

                return existingInventaireStock;
            })
            .map(inventaireStockRepository::save)
            .map(inventaireStock -> {
                audit(
                    TypeActionAudit.MODIFICATION,
                    inventaireStock,
                    "Modification partielle inventaire reference=" + inventaireStock.getReference()
                );
                return inventaireStock;
            })
            .map(inventaireStockMapper::toDto);
    }

    /**
     * Get all the inventaireStocks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<InventaireStockDTO> findAllWithEagerRelationships(Pageable pageable) {
        return inventaireStockRepository.findAllWithEagerRelationships(pageable).map(inventaireStockMapper::toDto);
    }

    /**
     * Get one inventaireStock by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InventaireStockDTO> findOne(Long id) {
        LOG.debug("Request to get InventaireStock : {}", id);
        return inventaireStockRepository
            .findOneWithEagerRelationships(id)
            .map(inventaireStock -> {
                assertAccessible(inventaireStock, "Acces refuse a l'inventaire demande");
                return inventaireStock;
            })
            .map(inventaireStockMapper::toDto);
    }

    /**
     * Delete the inventaireStock by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete InventaireStock : {}", id);
        InventaireStock inventaireStock = inventaireStockRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("inventaireStock", "notFound", "Inventaire introuvable"));
        assertAccessible(inventaireStock, "Acces refuse a l'inventaire a supprimer");
        audit(TypeActionAudit.DESACTIVATION, inventaireStock, "Suppression inventaire reference=" + inventaireStock.getReference());
        inventaireStockRepository.deleteById(id);
    }

    private void assertAccessible(InventaireStock inventaireStock, String message) {
        moduleSecurityService.assertBoutiqueAccess(inventaireStock.getBoutique().getId(), message);
    }

    private void audit(TypeActionAudit action, InventaireStock inventaireStock, String description) {
        journalAuditService.logAction(
            action,
            "InventaireStock",
            inventaireStock.getReference(),
            description,
            inventaireStock.getBoutique(),
            moduleSecurityService.getCurrentUser()
        );
    }
}
