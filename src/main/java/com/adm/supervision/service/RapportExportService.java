package com.adm.supervision.service;

import com.adm.supervision.domain.RapportExport;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.RapportExportRepository;
import com.adm.supervision.service.dto.RapportExportDTO;
import com.adm.supervision.service.mapper.RapportExportMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.RapportExport}.
 */
@Service
@Transactional
public class RapportExportService {

    private static final Logger LOG = LoggerFactory.getLogger(RapportExportService.class);

    private final RapportExportRepository rapportExportRepository;

    private final RapportExportMapper rapportExportMapper;

    private final ModuleSecurityService moduleSecurityService;

    private final JournalAuditService journalAuditService;

    public RapportExportService(
        RapportExportRepository rapportExportRepository,
        RapportExportMapper rapportExportMapper,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService
    ) {
        this.rapportExportRepository = rapportExportRepository;
        this.rapportExportMapper = rapportExportMapper;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
    }

    /**
     * Save a rapportExport.
     *
     * @param rapportExportDTO the entity to save.
     * @return the persisted entity.
     */
    public RapportExportDTO save(RapportExportDTO rapportExportDTO) {
        LOG.debug("Request to save RapportExport : {}", rapportExportDTO);
        RapportExport rapportExport = rapportExportMapper.toEntity(rapportExportDTO);
        assertAccessible(rapportExport, "Acces refuse au rapport export a creer");
        rapportExport = rapportExportRepository.save(rapportExport);
        audit(TypeActionAudit.EXPORT, rapportExport, "Creation rapport export reference=" + rapportExport.getReference());
        return rapportExportMapper.toDto(rapportExport);
    }

    /**
     * Update a rapportExport.
     *
     * @param rapportExportDTO the entity to save.
     * @return the persisted entity.
     */
    public RapportExportDTO update(RapportExportDTO rapportExportDTO) {
        LOG.debug("Request to update RapportExport : {}", rapportExportDTO);
        RapportExport rapportExport = rapportExportMapper.toEntity(rapportExportDTO);
        assertAccessible(rapportExport, "Acces refuse au rapport export a modifier");
        rapportExport = rapportExportRepository.save(rapportExport);
        audit(TypeActionAudit.EXPORT, rapportExport, "Modification rapport export reference=" + rapportExport.getReference());
        return rapportExportMapper.toDto(rapportExport);
    }

    /**
     * Partially update a rapportExport.
     *
     * @param rapportExportDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RapportExportDTO> partialUpdate(RapportExportDTO rapportExportDTO) {
        LOG.debug("Request to partially update RapportExport : {}", rapportExportDTO);

        return rapportExportRepository
            .findById(rapportExportDTO.getId())
            .map(existingRapportExport -> {
                rapportExportMapper.partialUpdate(existingRapportExport, rapportExportDTO);
                assertAccessible(existingRapportExport, "Acces refuse au rapport export a modifier");

                return existingRapportExport;
            })
            .map(rapportExportRepository::save)
            .map(rapportExport -> {
                audit(
                    TypeActionAudit.EXPORT,
                    rapportExport,
                    "Modification partielle rapport export reference=" + rapportExport.getReference()
                );
                return rapportExport;
            })
            .map(rapportExportMapper::toDto);
    }

    /**
     * Get all the rapportExports with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<RapportExportDTO> findAllWithEagerRelationships(Pageable pageable) {
        return rapportExportRepository.findAllWithEagerRelationships(pageable).map(rapportExportMapper::toDto);
    }

    /**
     * Get one rapportExport by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RapportExportDTO> findOne(Long id) {
        LOG.debug("Request to get RapportExport : {}", id);
        return rapportExportRepository
            .findOneWithEagerRelationships(id)
            .map(rapportExport -> {
                assertAccessible(rapportExport, "Acces refuse au rapport export demande");
                return rapportExport;
            })
            .map(rapportExportMapper::toDto);
    }

    /**
     * Delete the rapportExport by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete RapportExport : {}", id);
        RapportExport rapportExport = rapportExportRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("rapportExport", "notFound", "Rapport export introuvable"));
        assertAccessible(rapportExport, "Acces refuse au rapport export a supprimer");
        audit(TypeActionAudit.EXPORT, rapportExport, "Suppression rapport export reference=" + rapportExport.getReference());
        rapportExportRepository.deleteById(id);
    }

    private void assertAccessible(RapportExport rapportExport, String message) {
        if (rapportExport.getBoutique() != null && rapportExport.getBoutique().getId() != null) {
            moduleSecurityService.assertBoutiqueAccess(rapportExport.getBoutique().getId(), message);
            return;
        }
        if (
            !moduleSecurityService.isAdmin() &&
            (rapportExport.getUtilisateur() == null ||
                !rapportExport.getUtilisateur().getId().equals(moduleSecurityService.getCurrentUser().getId()))
        ) {
            throw new org.springframework.security.access.AccessDeniedException(message);
        }
    }

    private void audit(TypeActionAudit action, RapportExport rapportExport, String description) {
        journalAuditService.logAction(
            action,
            "RapportExport",
            rapportExport.getReference(),
            description,
            rapportExport.getBoutique(),
            moduleSecurityService.getCurrentUser()
        );
    }
}
