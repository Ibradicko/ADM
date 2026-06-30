package com.adm.supervision.service;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.JournalAudit;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.JournalAuditRepository;
import com.adm.supervision.service.dto.JournalAuditDTO;
import com.adm.supervision.service.mapper.JournalAuditMapper;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.JournalAudit}.
 */
@Service
@Transactional
public class JournalAuditService {

    private static final Logger LOG = LoggerFactory.getLogger(JournalAuditService.class);

    private final JournalAuditRepository journalAuditRepository;

    private final JournalAuditMapper journalAuditMapper;

    public JournalAuditService(JournalAuditRepository journalAuditRepository, JournalAuditMapper journalAuditMapper) {
        this.journalAuditRepository = journalAuditRepository;
        this.journalAuditMapper = journalAuditMapper;
    }

    /**
     * Save a journalAudit.
     *
     * @param journalAuditDTO the entity to save.
     * @return the persisted entity.
     */
    public JournalAuditDTO save(JournalAuditDTO journalAuditDTO) {
        LOG.debug("Request to save JournalAudit : {}", journalAuditDTO);
        JournalAudit journalAudit = journalAuditMapper.toEntity(journalAuditDTO);
        journalAudit = journalAuditRepository.save(journalAudit);
        return journalAuditMapper.toDto(journalAudit);
    }

    /**
     * Update a journalAudit.
     *
     * @param journalAuditDTO the entity to save.
     * @return the persisted entity.
     */
    public JournalAuditDTO update(JournalAuditDTO journalAuditDTO) {
        LOG.debug("Request to update JournalAudit : {}", journalAuditDTO);
        JournalAudit journalAudit = journalAuditMapper.toEntity(journalAuditDTO);
        journalAudit = journalAuditRepository.save(journalAudit);
        return journalAuditMapper.toDto(journalAudit);
    }

    /**
     * Partially update a journalAudit.
     *
     * @param journalAuditDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<JournalAuditDTO> partialUpdate(JournalAuditDTO journalAuditDTO) {
        LOG.debug("Request to partially update JournalAudit : {}", journalAuditDTO);

        return journalAuditRepository
            .findById(journalAuditDTO.getId())
            .map(existingJournalAudit -> {
                journalAuditMapper.partialUpdate(existingJournalAudit, journalAuditDTO);

                return existingJournalAudit;
            })
            .map(journalAuditRepository::save)
            .map(journalAuditMapper::toDto);
    }

    /**
     * Get all the journalAudits with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<JournalAuditDTO> findAllWithEagerRelationships(Pageable pageable) {
        return journalAuditRepository.findAllWithEagerRelationships(pageable).map(journalAuditMapper::toDto);
    }

    /**
     * Get one journalAudit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<JournalAuditDTO> findOne(Long id) {
        LOG.debug("Request to get JournalAudit : {}", id);
        return journalAuditRepository.findOneWithEagerRelationships(id).map(journalAuditMapper::toDto);
    }

    /**
     * Delete the journalAudit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete JournalAudit : {}", id);
        journalAuditRepository.deleteById(id);
    }

    public JournalAudit logAction(
        TypeActionAudit typeAction,
        String entiteConcernee,
        String identifiantEntite,
        String description,
        Boutique boutique,
        User utilisateur
    ) {
        JournalAudit journalAudit = new JournalAudit()
            .typeAction(typeAction)
            .entiteConcernee(entiteConcernee)
            .identifiantEntite(identifiantEntite)
            .description(description)
            .dateAction(Instant.now())
            .boutique(boutique)
            .utilisateur(utilisateur);
        return journalAuditRepository.save(journalAudit);
    }
}
