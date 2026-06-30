package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.UniteMesure;
import com.adm.supervision.repository.UniteMesureRepository;
import com.adm.supervision.service.criteria.UniteMesureCriteria;
import com.adm.supervision.service.dto.UniteMesureDTO;
import com.adm.supervision.service.mapper.UniteMesureMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link UniteMesure} entities in the database.
 * The main input is a {@link UniteMesureCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UniteMesureDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UniteMesureQueryService extends QueryService<UniteMesure> {

    private static final Logger LOG = LoggerFactory.getLogger(UniteMesureQueryService.class);

    private final UniteMesureRepository uniteMesureRepository;

    private final UniteMesureMapper uniteMesureMapper;

    public UniteMesureQueryService(UniteMesureRepository uniteMesureRepository, UniteMesureMapper uniteMesureMapper) {
        this.uniteMesureRepository = uniteMesureRepository;
        this.uniteMesureMapper = uniteMesureMapper;
    }

    /**
     * Return a {@link List} of {@link UniteMesureDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UniteMesureDTO> findByCriteria(UniteMesureCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<UniteMesure> specification = createSpecification(criteria);
        return uniteMesureMapper.toDto(uniteMesureRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UniteMesureCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<UniteMesure> specification = createSpecification(criteria);
        return uniteMesureRepository.count(specification);
    }

    /**
     * Function to convert {@link UniteMesureCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UniteMesure> createSpecification(UniteMesureCriteria criteria) {
        Specification<UniteMesure> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), UniteMesure_.id),
                buildStringSpecification(criteria.getCode(), UniteMesure_.code),
                buildStringSpecification(criteria.getLibelle(), UniteMesure_.libelle),
                buildStringSpecification(criteria.getSymbole(), UniteMesure_.symbole)
            );
        }
        return specification;
    }
}
