package com.adm.supervision.service;

import com.adm.supervision.domain.ReceptionProduit;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.ReceptionProduitRepository;
import com.adm.supervision.service.dto.ReceptionProduitDTO;
import com.adm.supervision.service.mapper.ReceptionProduitMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.ReceptionProduit}.
 */
@Service
@Transactional
public class ReceptionProduitService {

    private static final Logger LOG = LoggerFactory.getLogger(ReceptionProduitService.class);

    private final ReceptionProduitRepository receptionProduitRepository;

    private final ReceptionProduitMapper receptionProduitMapper;

    private final ModuleSecurityService moduleSecurityService;

    private final JournalAuditService journalAuditService;

    public ReceptionProduitService(
        ReceptionProduitRepository receptionProduitRepository,
        ReceptionProduitMapper receptionProduitMapper,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService
    ) {
        this.receptionProduitRepository = receptionProduitRepository;
        this.receptionProduitMapper = receptionProduitMapper;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
    }

    /**
     * Save a receptionProduit.
     *
     * @param receptionProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public ReceptionProduitDTO save(ReceptionProduitDTO receptionProduitDTO) {
        LOG.debug("Request to save ReceptionProduit : {}", receptionProduitDTO);
        ReceptionProduit receptionProduit = receptionProduitMapper.toEntity(receptionProduitDTO);
        assertAccessible(receptionProduit, "Acces refuse a la reception a creer");
        receptionProduit = receptionProduitRepository.save(receptionProduit);
        audit(TypeActionAudit.CREATION, receptionProduit, "Creation reception reference=" + receptionProduit.getReference());
        return receptionProduitMapper.toDto(receptionProduit);
    }

    /**
     * Update a receptionProduit.
     *
     * @param receptionProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public ReceptionProduitDTO update(ReceptionProduitDTO receptionProduitDTO) {
        LOG.debug("Request to update ReceptionProduit : {}", receptionProduitDTO);
        ReceptionProduit receptionProduit = receptionProduitMapper.toEntity(receptionProduitDTO);
        assertAccessible(receptionProduit, "Acces refuse a la reception a modifier");
        receptionProduit = receptionProduitRepository.save(receptionProduit);
        audit(TypeActionAudit.MODIFICATION, receptionProduit, "Modification reception reference=" + receptionProduit.getReference());
        return receptionProduitMapper.toDto(receptionProduit);
    }

    /**
     * Partially update a receptionProduit.
     *
     * @param receptionProduitDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ReceptionProduitDTO> partialUpdate(ReceptionProduitDTO receptionProduitDTO) {
        LOG.debug("Request to partially update ReceptionProduit : {}", receptionProduitDTO);

        return receptionProduitRepository
            .findById(receptionProduitDTO.getId())
            .map(existingReceptionProduit -> {
                receptionProduitMapper.partialUpdate(existingReceptionProduit, receptionProduitDTO);
                assertAccessible(existingReceptionProduit, "Acces refuse a la reception a modifier");

                return existingReceptionProduit;
            })
            .map(receptionProduitRepository::save)
            .map(receptionProduit -> {
                audit(
                    TypeActionAudit.MODIFICATION,
                    receptionProduit,
                    "Modification partielle reception reference=" + receptionProduit.getReference()
                );
                return receptionProduit;
            })
            .map(receptionProduitMapper::toDto);
    }

    /**
     * Get all the receptionProduits with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ReceptionProduitDTO> findAllWithEagerRelationships(Pageable pageable) {
        return receptionProduitRepository.findAllWithEagerRelationships(pageable).map(receptionProduitMapper::toDto);
    }

    /**
     * Get one receptionProduit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ReceptionProduitDTO> findOne(Long id) {
        LOG.debug("Request to get ReceptionProduit : {}", id);
        return receptionProduitRepository
            .findOneWithEagerRelationships(id)
            .map(receptionProduit -> {
                assertAccessible(receptionProduit, "Acces refuse a la reception demandee");
                return receptionProduit;
            })
            .map(receptionProduitMapper::toDto);
    }

    /**
     * Delete the receptionProduit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ReceptionProduit : {}", id);
        ReceptionProduit receptionProduit = receptionProduitRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("receptionProduit", "notFound", "Reception introuvable"));
        assertAccessible(receptionProduit, "Acces refuse a la reception a supprimer");
        audit(TypeActionAudit.DESACTIVATION, receptionProduit, "Suppression reception reference=" + receptionProduit.getReference());
        receptionProduitRepository.deleteById(id);
    }

    private void assertAccessible(ReceptionProduit receptionProduit, String message) {
        moduleSecurityService.assertBoutiqueAccess(receptionProduit.getBoutique().getId(), message);
    }

    private void audit(TypeActionAudit action, ReceptionProduit receptionProduit, String description) {
        journalAuditService.logAction(
            action,
            "ReceptionProduit",
            receptionProduit.getReference(),
            description,
            receptionProduit.getBoutique(),
            moduleSecurityService.getCurrentUser()
        );
    }
}
