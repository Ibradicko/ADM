package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.ReceptionProduit;
import com.adm.supervision.repository.ReceptionProduitRepository;
import com.adm.supervision.service.criteria.ReceptionProduitCriteria;
import com.adm.supervision.service.dto.ReceptionProduitDTO;
import com.adm.supervision.service.mapper.ReceptionProduitMapper;
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
 * Service for executing complex queries for {@link ReceptionProduit} entities in the database.
 * The main input is a {@link ReceptionProduitCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ReceptionProduitDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReceptionProduitQueryService extends QueryService<ReceptionProduit> {

    private static final Logger LOG = LoggerFactory.getLogger(ReceptionProduitQueryService.class);

    private final ReceptionProduitRepository receptionProduitRepository;

    private final ReceptionProduitMapper receptionProduitMapper;

    public ReceptionProduitQueryService(
        ReceptionProduitRepository receptionProduitRepository,
        ReceptionProduitMapper receptionProduitMapper
    ) {
        this.receptionProduitRepository = receptionProduitRepository;
        this.receptionProduitMapper = receptionProduitMapper;
    }

    /**
     * Return a {@link Page} of {@link ReceptionProduitDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReceptionProduitDTO> findByCriteria(ReceptionProduitCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<ReceptionProduit> specification = createSpecification(criteria);
        return receptionProduitRepository.findAll(specification, page).map(receptionProduitMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReceptionProduitCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ReceptionProduit> specification = createSpecification(criteria);
        return receptionProduitRepository.count(specification);
    }

    /**
     * Function to convert {@link ReceptionProduitCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ReceptionProduit> createSpecification(ReceptionProduitCriteria criteria) {
        Specification<ReceptionProduit> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), ReceptionProduit_.id),
                buildStringSpecification(criteria.getReference(), ReceptionProduit_.reference),
                buildRangeSpecification(criteria.getDateReception(), ReceptionProduit_.dateReception),
                buildStringSpecification(criteria.getFournisseur(), ReceptionProduit_.fournisseur),
                buildStringSpecification(criteria.getCommentaire(), ReceptionProduit_.commentaire),
                buildSpecification(criteria.getBoutiqueId(), root ->
                    root.join(ReceptionProduit_.boutique, JoinType.LEFT).get(Boutique_.id)
                ),
                buildSpecification(criteria.getUtilisateurId(), root ->
                    root.join(ReceptionProduit_.utilisateur, JoinType.LEFT).get(User_.id)
                )
            );
        }
        return specification;
    }
}
