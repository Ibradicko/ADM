package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.LigneTransfertStock;
import com.adm.supervision.repository.LigneTransfertStockRepository;
import com.adm.supervision.service.criteria.LigneTransfertStockCriteria;
import com.adm.supervision.service.dto.LigneTransfertStockDTO;
import com.adm.supervision.service.mapper.LigneTransfertStockMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link LigneTransfertStock} entities in the database.
 * The main input is a {@link LigneTransfertStockCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LigneTransfertStockDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LigneTransfertStockQueryService extends QueryService<LigneTransfertStock> {

    private static final Logger LOG = LoggerFactory.getLogger(LigneTransfertStockQueryService.class);

    private final LigneTransfertStockRepository ligneTransfertStockRepository;

    private final LigneTransfertStockMapper ligneTransfertStockMapper;

    public LigneTransfertStockQueryService(
        LigneTransfertStockRepository ligneTransfertStockRepository,
        LigneTransfertStockMapper ligneTransfertStockMapper
    ) {
        this.ligneTransfertStockRepository = ligneTransfertStockRepository;
        this.ligneTransfertStockMapper = ligneTransfertStockMapper;
    }

    /**
     * Return a {@link List} of {@link LigneTransfertStockDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LigneTransfertStockDTO> findByCriteria(LigneTransfertStockCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<LigneTransfertStock> specification = createSpecification(criteria);
        return ligneTransfertStockMapper.toDto(ligneTransfertStockRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LigneTransfertStockCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<LigneTransfertStock> specification = createSpecification(criteria);
        return ligneTransfertStockRepository.count(specification);
    }

    /**
     * Function to convert {@link LigneTransfertStockCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LigneTransfertStock> createSpecification(LigneTransfertStockCriteria criteria) {
        Specification<LigneTransfertStock> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), LigneTransfertStock_.id),
                buildRangeSpecification(criteria.getQuantite(), LigneTransfertStock_.quantite),
                buildStringSpecification(criteria.getCommentaire(), LigneTransfertStock_.commentaire),
                buildSpecification(criteria.getTransfertId(), root ->
                    root.join(LigneTransfertStock_.transfert, JoinType.LEFT).get(TransfertStock_.id)
                ),
                buildSpecification(criteria.getProduitId(), root -> root.join(LigneTransfertStock_.produit, JoinType.LEFT).get(Produit_.id))
            );
        }
        return specification;
    }
}
