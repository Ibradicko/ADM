package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.DepotStock;
import com.adm.supervision.repository.DepotStockRepository;
import com.adm.supervision.service.criteria.DepotStockCriteria;
import com.adm.supervision.service.dto.DepotStockDTO;
import com.adm.supervision.service.mapper.DepotStockMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link DepotStock} entities in the database.
 * The main input is a {@link DepotStockCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DepotStockDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DepotStockQueryService extends QueryService<DepotStock> {

    private static final Logger LOG = LoggerFactory.getLogger(DepotStockQueryService.class);

    private final DepotStockRepository depotStockRepository;

    private final DepotStockMapper depotStockMapper;

    public DepotStockQueryService(DepotStockRepository depotStockRepository, DepotStockMapper depotStockMapper) {
        this.depotStockRepository = depotStockRepository;
        this.depotStockMapper = depotStockMapper;
    }

    /**
     * Return a {@link List} of {@link DepotStockDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DepotStockDTO> findByCriteria(DepotStockCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<DepotStock> specification = createSpecification(criteria);
        return depotStockMapper.toDto(depotStockRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DepotStockCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<DepotStock> specification = createSpecification(criteria);
        return depotStockRepository.count(specification);
    }

    /**
     * Function to convert {@link DepotStockCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DepotStock> createSpecification(DepotStockCriteria criteria) {
        Specification<DepotStock> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), DepotStock_.id),
                buildStringSpecification(criteria.getCode(), DepotStock_.code),
                buildStringSpecification(criteria.getLibelle(), DepotStock_.libelle),
                buildStringSpecification(criteria.getEmplacement(), DepotStock_.emplacement),
                buildSpecification(criteria.getActif(), DepotStock_.actif),
                buildSpecification(criteria.getBoutiqueId(), root -> root.join(DepotStock_.boutique, JoinType.LEFT).get(Boutique_.id))
            );
        }
        return specification;
    }
}
