package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.JournalAudit;
import com.adm.supervision.repository.JournalAuditRepository;
import com.adm.supervision.service.criteria.JournalAuditCriteria;
import com.adm.supervision.service.dto.JournalAuditDTO;
import com.adm.supervision.service.mapper.JournalAuditMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link JournalAudit} entities in the database.
 * The main input is a {@link JournalAuditCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link JournalAuditDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class JournalAuditQueryService extends QueryService<JournalAudit> {

    private static final Logger LOG = LoggerFactory.getLogger(JournalAuditQueryService.class);

    private final JournalAuditRepository journalAuditRepository;

    private final JournalAuditMapper journalAuditMapper;

    public JournalAuditQueryService(JournalAuditRepository journalAuditRepository, JournalAuditMapper journalAuditMapper) {
        this.journalAuditRepository = journalAuditRepository;
        this.journalAuditMapper = journalAuditMapper;
    }

    /**
     * Return a {@link Page} of {@link JournalAuditDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<JournalAuditDTO> findByCriteria(JournalAuditCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<JournalAudit> specification = createSpecification(criteria);
        return journalAuditRepository.findAll(specification, page).map(journalAuditMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(JournalAuditCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<JournalAudit> specification = createSpecification(criteria);
        return journalAuditRepository.count(specification);
    }

    /**
     * Function to convert {@link JournalAuditCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<JournalAudit> createSpecification(JournalAuditCriteria criteria) {
        Specification<JournalAudit> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), JournalAudit_.id),
                buildSpecification(criteria.getTypeAction(), JournalAudit_.typeAction),
                buildStringSpecification(criteria.getEntiteConcernee(), JournalAudit_.entiteConcernee),
                buildStringSpecification(criteria.getIdentifiantEntite(), JournalAudit_.identifiantEntite),
                buildStringSpecification(criteria.getAdresseIp(), JournalAudit_.adresseIp),
                buildRangeSpecification(criteria.getDateAction(), JournalAudit_.dateAction),
                buildSpecification(criteria.getBoutiqueId(), root -> root.join(JournalAudit_.boutique, JoinType.LEFT).get(Boutique_.id)),
                buildSpecification(criteria.getUtilisateurId(), root -> root.join(JournalAudit_.utilisateur, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
