package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.repository.CalculRedevanceRepository;
import com.adm.supervision.service.criteria.CalculRedevanceCriteria;
import com.adm.supervision.service.dto.CalculRedevanceDTO;
import com.adm.supervision.service.mapper.CalculRedevanceMapper;
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
 * Service for executing complex queries for {@link CalculRedevance} entities in the database.
 * The main input is a {@link CalculRedevanceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CalculRedevanceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CalculRedevanceQueryService extends QueryService<CalculRedevance> {

    private static final Logger LOG = LoggerFactory.getLogger(CalculRedevanceQueryService.class);

    private final CalculRedevanceRepository calculRedevanceRepository;

    private final CalculRedevanceMapper calculRedevanceMapper;

    public CalculRedevanceQueryService(CalculRedevanceRepository calculRedevanceRepository, CalculRedevanceMapper calculRedevanceMapper) {
        this.calculRedevanceRepository = calculRedevanceRepository;
        this.calculRedevanceMapper = calculRedevanceMapper;
    }

    /**
     * Return a {@link Page} of {@link CalculRedevanceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CalculRedevanceDTO> findByCriteria(CalculRedevanceCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CalculRedevance> specification = createSpecification(criteria);
        return calculRedevanceRepository.findAll(specification, page).map(calculRedevanceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CalculRedevanceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CalculRedevance> specification = createSpecification(criteria);
        return calculRedevanceRepository.count(specification);
    }

    /**
     * Function to convert {@link CalculRedevanceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CalculRedevance> createSpecification(CalculRedevanceCriteria criteria) {
        Specification<CalculRedevance> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), CalculRedevance_.id),
                buildStringSpecification(criteria.getReference(), CalculRedevance_.reference),
                buildRangeSpecification(criteria.getPeriodeDebut(), CalculRedevance_.periodeDebut),
                buildRangeSpecification(criteria.getPeriodeFin(), CalculRedevance_.periodeFin),
                buildRangeSpecification(criteria.getChiffreAffaires(), CalculRedevance_.chiffreAffaires),
                buildRangeSpecification(criteria.getMontantRedevance(), CalculRedevance_.montantRedevance),
                buildSpecification(criteria.getStatut(), CalculRedevance_.statut),
                buildRangeSpecification(criteria.getDateCalcul(), CalculRedevance_.dateCalcul),
                buildSpecification(criteria.getBoutiqueId(), root -> root.join(CalculRedevance_.boutique, JoinType.LEFT).get(Boutique_.id)),
                buildSpecification(criteria.getLocataireId(), root ->
                    root.join(CalculRedevance_.locataire, JoinType.LEFT).get(Locataire_.id)
                )
            );
        }
        return specification;
    }
}
