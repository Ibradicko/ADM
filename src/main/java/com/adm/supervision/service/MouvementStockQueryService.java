package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.MouvementStock;
import com.adm.supervision.repository.MouvementStockRepository;
import com.adm.supervision.service.criteria.MouvementStockCriteria;
import com.adm.supervision.service.dto.MouvementStockDTO;
import com.adm.supervision.service.mapper.MouvementStockMapper;
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
 * Service for executing complex queries for {@link MouvementStock} entities in the database.
 * The main input is a {@link MouvementStockCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link MouvementStockDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class MouvementStockQueryService extends QueryService<MouvementStock> {

    private static final Logger LOG = LoggerFactory.getLogger(MouvementStockQueryService.class);

    private final MouvementStockRepository mouvementStockRepository;

    private final MouvementStockMapper mouvementStockMapper;

    public MouvementStockQueryService(MouvementStockRepository mouvementStockRepository, MouvementStockMapper mouvementStockMapper) {
        this.mouvementStockRepository = mouvementStockRepository;
        this.mouvementStockMapper = mouvementStockMapper;
    }

    /**
     * Return a {@link Page} of {@link MouvementStockDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<MouvementStockDTO> findByCriteria(MouvementStockCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<MouvementStock> specification = createSpecification(criteria);
        return mouvementStockRepository.findAll(specification, page).map(mouvementStockMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(MouvementStockCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<MouvementStock> specification = createSpecification(criteria);
        return mouvementStockRepository.count(specification);
    }

    /**
     * Function to convert {@link MouvementStockCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<MouvementStock> createSpecification(MouvementStockCriteria criteria) {
        Specification<MouvementStock> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), MouvementStock_.id),
                buildStringSpecification(criteria.getReference(), MouvementStock_.reference),
                buildSpecification(criteria.getTypeMouvement(), MouvementStock_.typeMouvement),
                buildSpecification(criteria.getStatut(), MouvementStock_.statut),
                buildRangeSpecification(criteria.getDateMouvement(), MouvementStock_.dateMouvement),
                buildStringSpecification(criteria.getMotif(), MouvementStock_.motif),
                buildSpecification(criteria.getBoutiqueId(), root -> root.join(MouvementStock_.boutique, JoinType.LEFT).get(Boutique_.id)),
                buildSpecification(criteria.getUtilisateurId(), root -> root.join(MouvementStock_.utilisateur, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
