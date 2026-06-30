package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.StockProduit;
import com.adm.supervision.repository.StockProduitRepository;
import com.adm.supervision.service.criteria.StockProduitCriteria;
import com.adm.supervision.service.dto.StockProduitDTO;
import com.adm.supervision.service.mapper.StockProduitMapper;
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
 * Service for executing complex queries for {@link StockProduit} entities in the database.
 * The main input is a {@link StockProduitCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link StockProduitDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StockProduitQueryService extends QueryService<StockProduit> {

    private static final Logger LOG = LoggerFactory.getLogger(StockProduitQueryService.class);

    private final StockProduitRepository stockProduitRepository;

    private final StockProduitMapper stockProduitMapper;

    public StockProduitQueryService(StockProduitRepository stockProduitRepository, StockProduitMapper stockProduitMapper) {
        this.stockProduitRepository = stockProduitRepository;
        this.stockProduitMapper = stockProduitMapper;
    }

    /**
     * Return a {@link Page} of {@link StockProduitDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StockProduitDTO> findByCriteria(StockProduitCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StockProduit> specification = createSpecification(criteria);
        return stockProduitRepository.findAll(specification, page).map(stockProduitMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StockProduitCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<StockProduit> specification = createSpecification(criteria);
        return stockProduitRepository.count(specification);
    }

    /**
     * Function to convert {@link StockProduitCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StockProduit> createSpecification(StockProduitCriteria criteria) {
        Specification<StockProduit> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), StockProduit_.id),
                buildRangeSpecification(criteria.getQuantiteTheorique(), StockProduit_.quantiteTheorique),
                buildRangeSpecification(criteria.getStockAlerte(), StockProduit_.stockAlerte),
                buildRangeSpecification(criteria.getDateDernierMouvement(), StockProduit_.dateDernierMouvement),
                buildSpecification(criteria.getProduitId(), root -> root.join(StockProduit_.produit, JoinType.LEFT).get(Produit_.id)),
                buildSpecification(criteria.getDepotId(), root -> root.join(StockProduit_.depot, JoinType.LEFT).get(DepotStock_.id)),
                buildSpecification(criteria.getBoutiqueId(), root ->
                    root.join(StockProduit_.depot, JoinType.LEFT).join(DepotStock_.boutique, JoinType.LEFT).get(Boutique_.id)
                )
            );
        }
        return specification;
    }
}
