package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.LotEtiquettes;
import com.adm.supervision.repository.LotEtiquettesRepository;
import com.adm.supervision.service.criteria.LotEtiquettesCriteria;
import com.adm.supervision.service.dto.LotEtiquettesDTO;
import com.adm.supervision.service.mapper.LotEtiquettesMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link LotEtiquettes} entities in the database.
 * The main input is a {@link LotEtiquettesCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LotEtiquettesDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LotEtiquettesQueryService extends QueryService<LotEtiquettes> {

    private static final Logger LOG = LoggerFactory.getLogger(LotEtiquettesQueryService.class);

    private final LotEtiquettesRepository lotEtiquettesRepository;

    private final LotEtiquettesMapper lotEtiquettesMapper;

    public LotEtiquettesQueryService(LotEtiquettesRepository lotEtiquettesRepository, LotEtiquettesMapper lotEtiquettesMapper) {
        this.lotEtiquettesRepository = lotEtiquettesRepository;
        this.lotEtiquettesMapper = lotEtiquettesMapper;
    }

    /**
     * Return a {@link List} of {@link LotEtiquettesDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LotEtiquettesDTO> findByCriteria(LotEtiquettesCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<LotEtiquettes> specification = createSpecification(criteria);
        return lotEtiquettesMapper.toDto(lotEtiquettesRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LotEtiquettesCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<LotEtiquettes> specification = createSpecification(criteria);
        return lotEtiquettesRepository.count(specification);
    }

    /**
     * Function to convert {@link LotEtiquettesCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LotEtiquettes> createSpecification(LotEtiquettesCriteria criteria) {
        Specification<LotEtiquettes> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), LotEtiquettes_.id),
                buildStringSpecification(criteria.getReference(), LotEtiquettes_.reference),
                buildRangeSpecification(criteria.getDateGeneration(), LotEtiquettes_.dateGeneration),
                buildStringSpecification(criteria.getFormatImpression(), LotEtiquettes_.formatImpression),
                buildRangeSpecification(criteria.getNombreEtiquettes(), LotEtiquettes_.nombreEtiquettes)
            );
        }
        return specification;
    }
}
