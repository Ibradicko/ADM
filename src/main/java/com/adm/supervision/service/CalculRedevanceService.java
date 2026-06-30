package com.adm.supervision.service;

import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.CalculRedevanceRepository;
import com.adm.supervision.service.dto.CalculRedevanceDTO;
import com.adm.supervision.service.mapper.CalculRedevanceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.CalculRedevance}.
 */
@Service
@Transactional
public class CalculRedevanceService {

    private static final Logger LOG = LoggerFactory.getLogger(CalculRedevanceService.class);

    private final CalculRedevanceRepository calculRedevanceRepository;

    private final CalculRedevanceMapper calculRedevanceMapper;

    private final ModuleSecurityService moduleSecurityService;

    private final JournalAuditService journalAuditService;

    public CalculRedevanceService(
        CalculRedevanceRepository calculRedevanceRepository,
        CalculRedevanceMapper calculRedevanceMapper,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService
    ) {
        this.calculRedevanceRepository = calculRedevanceRepository;
        this.calculRedevanceMapper = calculRedevanceMapper;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
    }

    /**
     * Save a calculRedevance.
     *
     * @param calculRedevanceDTO the entity to save.
     * @return the persisted entity.
     */
    public CalculRedevanceDTO save(CalculRedevanceDTO calculRedevanceDTO) {
        LOG.debug("Request to save CalculRedevance : {}", calculRedevanceDTO);
        CalculRedevance calculRedevance = calculRedevanceMapper.toEntity(calculRedevanceDTO);
        assertAccessible(calculRedevance, "Acces refuse au calcul de redevance a creer");
        calculRedevance = calculRedevanceRepository.save(calculRedevance);
        audit(TypeActionAudit.CREATION, calculRedevance, "Creation calcul redevance reference=" + calculRedevance.getReference());
        return calculRedevanceMapper.toDto(calculRedevance);
    }

    /**
     * Update a calculRedevance.
     *
     * @param calculRedevanceDTO the entity to save.
     * @return the persisted entity.
     */
    public CalculRedevanceDTO update(CalculRedevanceDTO calculRedevanceDTO) {
        LOG.debug("Request to update CalculRedevance : {}", calculRedevanceDTO);
        CalculRedevance calculRedevance = calculRedevanceMapper.toEntity(calculRedevanceDTO);
        assertAccessible(calculRedevance, "Acces refuse au calcul de redevance a modifier");
        calculRedevance = calculRedevanceRepository.save(calculRedevance);
        audit(TypeActionAudit.MODIFICATION, calculRedevance, "Modification calcul redevance reference=" + calculRedevance.getReference());
        return calculRedevanceMapper.toDto(calculRedevance);
    }

    /**
     * Partially update a calculRedevance.
     *
     * @param calculRedevanceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CalculRedevanceDTO> partialUpdate(CalculRedevanceDTO calculRedevanceDTO) {
        LOG.debug("Request to partially update CalculRedevance : {}", calculRedevanceDTO);

        return calculRedevanceRepository
            .findById(calculRedevanceDTO.getId())
            .map(existingCalculRedevance -> {
                calculRedevanceMapper.partialUpdate(existingCalculRedevance, calculRedevanceDTO);
                assertAccessible(existingCalculRedevance, "Acces refuse au calcul de redevance a modifier");

                return existingCalculRedevance;
            })
            .map(calculRedevanceRepository::save)
            .map(calculRedevance -> {
                audit(
                    TypeActionAudit.MODIFICATION,
                    calculRedevance,
                    "Modification partielle calcul redevance reference=" + calculRedevance.getReference()
                );
                return calculRedevance;
            })
            .map(calculRedevanceMapper::toDto);
    }

    /**
     * Get all the calculRedevances with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<CalculRedevanceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return calculRedevanceRepository.findAllWithEagerRelationships(pageable).map(calculRedevanceMapper::toDto);
    }

    /**
     * Get one calculRedevance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CalculRedevanceDTO> findOne(Long id) {
        LOG.debug("Request to get CalculRedevance : {}", id);
        return calculRedevanceRepository
            .findOneWithEagerRelationships(id)
            .map(calculRedevance -> {
                assertAccessible(calculRedevance, "Acces refuse au calcul de redevance demande");
                return calculRedevance;
            })
            .map(calculRedevanceMapper::toDto);
    }

    /**
     * Delete the calculRedevance by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CalculRedevance : {}", id);
        CalculRedevance calculRedevance = calculRedevanceRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("calculRedevance", "notFound", "Calcul redevance introuvable"));
        assertAccessible(calculRedevance, "Acces refuse au calcul de redevance a supprimer");
        audit(TypeActionAudit.DESACTIVATION, calculRedevance, "Suppression calcul redevance reference=" + calculRedevance.getReference());
        calculRedevanceRepository.deleteById(id);
    }

    private void assertAccessible(CalculRedevance calculRedevance, String message) {
        moduleSecurityService.assertBoutiqueAccess(calculRedevance.getBoutique().getId(), message);
    }

    private void audit(TypeActionAudit action, CalculRedevance calculRedevance, String description) {
        journalAuditService.logAction(
            action,
            "CalculRedevance",
            calculRedevance.getReference(),
            description,
            calculRedevance.getBoutique(),
            moduleSecurityService.getCurrentUser()
        );
    }
}
