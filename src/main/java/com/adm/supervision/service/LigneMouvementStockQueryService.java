package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.LigneMouvementStock;
import com.adm.supervision.repository.LigneMouvementStockRepository;
import com.adm.supervision.service.criteria.LigneMouvementStockCriteria;
import com.adm.supervision.service.dto.LigneMouvementStockDTO;
import com.adm.supervision.service.mapper.LigneMouvementStockMapper;
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
 * Service for executing complex queries for {@link LigneMouvementStock} entities in the database.
 * The main input is a {@link LigneMouvementStockCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link LigneMouvementStockDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LigneMouvementStockQueryService extends QueryService<LigneMouvementStock> {

    private static final Logger LOG = LoggerFactory.getLogger(LigneMouvementStockQueryService.class);

    private final LigneMouvementStockRepository ligneMouvementStockRepository;

    private final LigneMouvementStockMapper ligneMouvementStockMapper;

    public LigneMouvementStockQueryService(
        LigneMouvementStockRepository ligneMouvementStockRepository,
        LigneMouvementStockMapper ligneMouvementStockMapper
    ) {
        this.ligneMouvementStockRepository = ligneMouvementStockRepository;
        this.ligneMouvementStockMapper = ligneMouvementStockMapper;
    }

    /**
     * Return a {@link Page} of {@link LigneMouvementStockDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LigneMouvementStockDTO> findByCriteria(LigneMouvementStockCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LigneMouvementStock> specification = createSpecification(criteria);
        return ligneMouvementStockRepository.findAll(specification, page).map(ligneMouvementStockMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LigneMouvementStockCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<LigneMouvementStock> specification = createSpecification(criteria);
        return ligneMouvementStockRepository.count(specification);
    }

    /**
     * Function to convert {@link LigneMouvementStockCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LigneMouvementStock> createSpecification(LigneMouvementStockCriteria criteria) {
        Specification<LigneMouvementStock> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), LigneMouvementStock_.id),
                buildRangeSpecification(criteria.getQuantite(), LigneMouvementStock_.quantite),
                buildRangeSpecification(criteria.getStockAvant(), LigneMouvementStock_.stockAvant),
                buildRangeSpecification(criteria.getStockApres(), LigneMouvementStock_.stockApres),
                buildStringSpecification(criteria.getCommentaire(), LigneMouvementStock_.commentaire),
                buildSpecification(criteria.getMouvementId(), root ->
                    root.join(LigneMouvementStock_.mouvement, JoinType.LEFT).get(MouvementStock_.id)
                ),
                buildSpecification(criteria.getProduitId(), root ->
                    root.join(LigneMouvementStock_.produit, JoinType.LEFT).get(Produit_.id)
                ),
                buildSpecification(criteria.getDepotId(), root -> root.join(LigneMouvementStock_.depot, JoinType.LEFT).get(DepotStock_.id))
            );
        }
        return specification;
    }
}
