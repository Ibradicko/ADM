package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.LigneCalculRedevance;
import com.adm.supervision.repository.LigneCalculRedevanceRepository;
import com.adm.supervision.service.criteria.LigneCalculRedevanceCriteria;
import com.adm.supervision.service.dto.LigneCalculRedevanceDTO;
import com.adm.supervision.service.mapper.LigneCalculRedevanceMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link LigneCalculRedevance} entities in the database.
 * The main input is a {@link LigneCalculRedevanceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LigneCalculRedevanceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LigneCalculRedevanceQueryService extends QueryService<LigneCalculRedevance> {

    private static final Logger LOG = LoggerFactory.getLogger(LigneCalculRedevanceQueryService.class);

    private final LigneCalculRedevanceRepository ligneCalculRedevanceRepository;

    private final LigneCalculRedevanceMapper ligneCalculRedevanceMapper;

    public LigneCalculRedevanceQueryService(
        LigneCalculRedevanceRepository ligneCalculRedevanceRepository,
        LigneCalculRedevanceMapper ligneCalculRedevanceMapper
    ) {
        this.ligneCalculRedevanceRepository = ligneCalculRedevanceRepository;
        this.ligneCalculRedevanceMapper = ligneCalculRedevanceMapper;
    }

    /**
     * Return a {@link List} of {@link LigneCalculRedevanceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LigneCalculRedevanceDTO> findByCriteria(LigneCalculRedevanceCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<LigneCalculRedevance> specification = createSpecification(criteria);
        return ligneCalculRedevanceMapper.toDto(ligneCalculRedevanceRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LigneCalculRedevanceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<LigneCalculRedevance> specification = createSpecification(criteria);
        return ligneCalculRedevanceRepository.count(specification);
    }

    /**
     * Function to convert {@link LigneCalculRedevanceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LigneCalculRedevance> createSpecification(LigneCalculRedevanceCriteria criteria) {
        Specification<LigneCalculRedevance> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), LigneCalculRedevance_.id),
                buildRangeSpecification(criteria.getBaseCalcul(), LigneCalculRedevance_.baseCalcul),
                buildRangeSpecification(criteria.getTauxApplique(), LigneCalculRedevance_.tauxApplique),
                buildRangeSpecification(criteria.getMontantRedevance(), LigneCalculRedevance_.montantRedevance),
                buildSpecification(criteria.getCalculId(), root ->
                    root.join(LigneCalculRedevance_.calcul, JoinType.LEFT).get(CalculRedevance_.id)
                ),
                buildSpecification(criteria.getVenteId(), root -> root.join(LigneCalculRedevance_.vente, JoinType.LEFT).get(Vente_.id))
            );
        }
        return specification;
    }
}
