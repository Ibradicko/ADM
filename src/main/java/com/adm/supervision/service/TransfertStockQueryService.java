package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.TransfertStock;
import com.adm.supervision.repository.TransfertStockRepository;
import com.adm.supervision.service.criteria.TransfertStockCriteria;
import com.adm.supervision.service.dto.TransfertStockDTO;
import com.adm.supervision.service.mapper.TransfertStockMapper;
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
 * Service for executing complex queries for {@link TransfertStock} entities in the database.
 * The main input is a {@link TransfertStockCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TransfertStockDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TransfertStockQueryService extends QueryService<TransfertStock> {

    private static final Logger LOG = LoggerFactory.getLogger(TransfertStockQueryService.class);

    private final TransfertStockRepository transfertStockRepository;

    private final TransfertStockMapper transfertStockMapper;

    public TransfertStockQueryService(TransfertStockRepository transfertStockRepository, TransfertStockMapper transfertStockMapper) {
        this.transfertStockRepository = transfertStockRepository;
        this.transfertStockMapper = transfertStockMapper;
    }

    /**
     * Return a {@link Page} of {@link TransfertStockDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TransfertStockDTO> findByCriteria(TransfertStockCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TransfertStock> specification = createSpecification(criteria);
        return transfertStockRepository.findAll(specification, page).map(transfertStockMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TransfertStockCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TransfertStock> specification = createSpecification(criteria);
        return transfertStockRepository.count(specification);
    }

    /**
     * Function to convert {@link TransfertStockCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TransfertStock> createSpecification(TransfertStockCriteria criteria) {
        Specification<TransfertStock> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), TransfertStock_.id),
                buildStringSpecification(criteria.getReference(), TransfertStock_.reference),
                buildRangeSpecification(criteria.getDateTransfert(), TransfertStock_.dateTransfert),
                buildSpecification(criteria.getStatut(), TransfertStock_.statut),
                buildStringSpecification(criteria.getMotif(), TransfertStock_.motif),
                buildSpecification(criteria.getBoutiqueOrigineId(), root ->
                    root.join(TransfertStock_.boutiqueOrigine, JoinType.LEFT).get(Boutique_.id)
                ),
                buildSpecification(criteria.getBoutiqueDestinationId(), root ->
                    root.join(TransfertStock_.boutiqueDestination, JoinType.LEFT).get(Boutique_.id)
                ),
                buildSpecification(criteria.getUtilisateurId(), root -> root.join(TransfertStock_.utilisateur, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
