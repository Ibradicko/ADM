package com.adm.supervision.service;

import com.adm.supervision.domain.MouvementStock;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.MouvementStockRepository;
import com.adm.supervision.service.dto.MouvementStockDTO;
import com.adm.supervision.service.mapper.MouvementStockMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.MouvementStock}.
 */
@Service
@Transactional
public class MouvementStockService {

    private static final Logger LOG = LoggerFactory.getLogger(MouvementStockService.class);

    private final MouvementStockRepository mouvementStockRepository;

    private final MouvementStockMapper mouvementStockMapper;

    private final ModuleSecurityService moduleSecurityService;

    private final JournalAuditService journalAuditService;

    public MouvementStockService(
        MouvementStockRepository mouvementStockRepository,
        MouvementStockMapper mouvementStockMapper,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService
    ) {
        this.mouvementStockRepository = mouvementStockRepository;
        this.mouvementStockMapper = mouvementStockMapper;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
    }

    /**
     * Save a mouvementStock.
     *
     * @param mouvementStockDTO the entity to save.
     * @return the persisted entity.
     */
    public MouvementStockDTO save(MouvementStockDTO mouvementStockDTO) {
        LOG.debug("Request to save MouvementStock : {}", mouvementStockDTO);
        MouvementStock mouvementStock = mouvementStockMapper.toEntity(mouvementStockDTO);
        assertAccessible(mouvementStock, "Acces refuse au mouvement de stock a creer");
        mouvementStock = mouvementStockRepository.save(mouvementStock);
        audit(TypeActionAudit.CREATION, mouvementStock, "Creation mouvement stock reference=" + mouvementStock.getReference());
        return mouvementStockMapper.toDto(mouvementStock);
    }

    /**
     * Update a mouvementStock.
     *
     * @param mouvementStockDTO the entity to save.
     * @return the persisted entity.
     */
    public MouvementStockDTO update(MouvementStockDTO mouvementStockDTO) {
        LOG.debug("Request to update MouvementStock : {}", mouvementStockDTO);
        MouvementStock mouvementStock = mouvementStockMapper.toEntity(mouvementStockDTO);
        assertAccessible(mouvementStock, "Acces refuse au mouvement de stock a modifier");
        mouvementStock = mouvementStockRepository.save(mouvementStock);
        audit(TypeActionAudit.MODIFICATION, mouvementStock, "Modification mouvement stock reference=" + mouvementStock.getReference());
        return mouvementStockMapper.toDto(mouvementStock);
    }

    /**
     * Partially update a mouvementStock.
     *
     * @param mouvementStockDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MouvementStockDTO> partialUpdate(MouvementStockDTO mouvementStockDTO) {
        LOG.debug("Request to partially update MouvementStock : {}", mouvementStockDTO);

        return mouvementStockRepository
            .findById(mouvementStockDTO.getId())
            .map(existingMouvementStock -> {
                mouvementStockMapper.partialUpdate(existingMouvementStock, mouvementStockDTO);
                assertAccessible(existingMouvementStock, "Acces refuse au mouvement de stock a modifier");

                return existingMouvementStock;
            })
            .map(mouvementStockRepository::save)
            .map(mouvementStock -> {
                audit(
                    TypeActionAudit.MODIFICATION,
                    mouvementStock,
                    "Modification partielle mouvement stock reference=" + mouvementStock.getReference()
                );
                return mouvementStock;
            })
            .map(mouvementStockMapper::toDto);
    }

    /**
     * Get all the mouvementStocks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MouvementStockDTO> findAllWithEagerRelationships(Pageable pageable) {
        return mouvementStockRepository.findAllWithEagerRelationships(pageable).map(mouvementStockMapper::toDto);
    }

    /**
     * Get one mouvementStock by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MouvementStockDTO> findOne(Long id) {
        LOG.debug("Request to get MouvementStock : {}", id);
        return mouvementStockRepository
            .findOneWithEagerRelationships(id)
            .map(mouvementStock -> {
                assertAccessible(mouvementStock, "Acces refuse au mouvement de stock demande");
                return mouvementStock;
            })
            .map(mouvementStockMapper::toDto);
    }

    /**
     * Delete the mouvementStock by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MouvementStock : {}", id);
        MouvementStock mouvementStock = mouvementStockRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("mouvementStock", "notFound", "Mouvement stock introuvable"));
        assertAccessible(mouvementStock, "Acces refuse au mouvement de stock a supprimer");
        audit(TypeActionAudit.DESACTIVATION, mouvementStock, "Suppression mouvement stock reference=" + mouvementStock.getReference());
        mouvementStockRepository.deleteById(id);
    }

    private void assertAccessible(MouvementStock mouvementStock, String message) {
        moduleSecurityService.assertBoutiqueAccess(mouvementStock.getBoutique().getId(), message);
    }

    private void audit(TypeActionAudit action, MouvementStock mouvementStock, String description) {
        journalAuditService.logAction(
            action,
            "MouvementStock",
            mouvementStock.getReference(),
            description,
            mouvementStock.getBoutique(),
            moduleSecurityService.getCurrentUser()
        );
    }
}
