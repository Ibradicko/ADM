package com.adm.supervision.service;

import com.adm.supervision.domain.TransfertStock;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.TransfertStockRepository;
import com.adm.supervision.service.dto.TransfertStockDTO;
import com.adm.supervision.service.mapper.TransfertStockMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.TransfertStock}.
 */
@Service
@Transactional
public class TransfertStockService {

    private static final Logger LOG = LoggerFactory.getLogger(TransfertStockService.class);

    private final TransfertStockRepository transfertStockRepository;

    private final TransfertStockMapper transfertStockMapper;

    private final ModuleSecurityService moduleSecurityService;

    private final JournalAuditService journalAuditService;

    public TransfertStockService(
        TransfertStockRepository transfertStockRepository,
        TransfertStockMapper transfertStockMapper,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService
    ) {
        this.transfertStockRepository = transfertStockRepository;
        this.transfertStockMapper = transfertStockMapper;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
    }

    /**
     * Save a transfertStock.
     *
     * @param transfertStockDTO the entity to save.
     * @return the persisted entity.
     */
    public TransfertStockDTO save(TransfertStockDTO transfertStockDTO) {
        LOG.debug("Request to save TransfertStock : {}", transfertStockDTO);
        TransfertStock transfertStock = transfertStockMapper.toEntity(transfertStockDTO);
        assertAccessible(transfertStock, "Acces refuse au transfert a creer");
        transfertStock = transfertStockRepository.save(transfertStock);
        audit(TypeActionAudit.CREATION, transfertStock, "Creation transfert reference=" + transfertStock.getReference());
        return transfertStockMapper.toDto(transfertStock);
    }

    /**
     * Update a transfertStock.
     *
     * @param transfertStockDTO the entity to save.
     * @return the persisted entity.
     */
    public TransfertStockDTO update(TransfertStockDTO transfertStockDTO) {
        LOG.debug("Request to update TransfertStock : {}", transfertStockDTO);
        TransfertStock transfertStock = transfertStockMapper.toEntity(transfertStockDTO);
        assertAccessible(transfertStock, "Acces refuse au transfert a modifier");
        transfertStock = transfertStockRepository.save(transfertStock);
        audit(TypeActionAudit.MODIFICATION, transfertStock, "Modification transfert reference=" + transfertStock.getReference());
        return transfertStockMapper.toDto(transfertStock);
    }

    /**
     * Partially update a transfertStock.
     *
     * @param transfertStockDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TransfertStockDTO> partialUpdate(TransfertStockDTO transfertStockDTO) {
        LOG.debug("Request to partially update TransfertStock : {}", transfertStockDTO);

        return transfertStockRepository
            .findById(transfertStockDTO.getId())
            .map(existingTransfertStock -> {
                transfertStockMapper.partialUpdate(existingTransfertStock, transfertStockDTO);
                assertAccessible(existingTransfertStock, "Acces refuse au transfert a modifier");

                return existingTransfertStock;
            })
            .map(transfertStockRepository::save)
            .map(transfertStock -> {
                audit(
                    TypeActionAudit.MODIFICATION,
                    transfertStock,
                    "Modification partielle transfert reference=" + transfertStock.getReference()
                );
                return transfertStock;
            })
            .map(transfertStockMapper::toDto);
    }

    /**
     * Get all the transfertStocks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TransfertStockDTO> findAllWithEagerRelationships(Pageable pageable) {
        return transfertStockRepository.findAllWithEagerRelationships(pageable).map(transfertStockMapper::toDto);
    }

    /**
     * Get one transfertStock by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TransfertStockDTO> findOne(Long id) {
        LOG.debug("Request to get TransfertStock : {}", id);
        return transfertStockRepository
            .findOneWithEagerRelationships(id)
            .map(transfertStock -> {
                assertAccessible(transfertStock, "Acces refuse au transfert demande");
                return transfertStock;
            })
            .map(transfertStockMapper::toDto);
    }

    /**
     * Delete the transfertStock by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TransfertStock : {}", id);
        TransfertStock transfertStock = transfertStockRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("transfertStock", "notFound", "Transfert introuvable"));
        assertAccessible(transfertStock, "Acces refuse au transfert a supprimer");
        audit(TypeActionAudit.DESACTIVATION, transfertStock, "Suppression transfert reference=" + transfertStock.getReference());
        transfertStockRepository.deleteById(id);
    }

    private void assertAccessible(TransfertStock transfertStock, String message) {
        moduleSecurityService.assertAllBoutiquesAccess(
            java.util.List.of(transfertStock.getBoutiqueOrigine().getId(), transfertStock.getBoutiqueDestination().getId()),
            message
        );
    }

    private void audit(TypeActionAudit action, TransfertStock transfertStock, String description) {
        journalAuditService.logAction(
            action,
            "TransfertStock",
            transfertStock.getReference(),
            description,
            transfertStock.getBoutiqueOrigine(),
            moduleSecurityService.getCurrentUser()
        );
    }
}
