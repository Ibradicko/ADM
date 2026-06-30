package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.LigneReceptionProduit;
import com.adm.supervision.repository.LigneReceptionProduitRepository;
import com.adm.supervision.service.criteria.LigneReceptionProduitCriteria;
import com.adm.supervision.service.dto.LigneReceptionProduitDTO;
import com.adm.supervision.service.mapper.LigneReceptionProduitMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link LigneReceptionProduit} entities in the database.
 * The main input is a {@link LigneReceptionProduitCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LigneReceptionProduitDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LigneReceptionProduitQueryService extends QueryService<LigneReceptionProduit> {

    private static final Logger LOG = LoggerFactory.getLogger(LigneReceptionProduitQueryService.class);

    private final LigneReceptionProduitRepository ligneReceptionProduitRepository;

    private final LigneReceptionProduitMapper ligneReceptionProduitMapper;

    public LigneReceptionProduitQueryService(
        LigneReceptionProduitRepository ligneReceptionProduitRepository,
        LigneReceptionProduitMapper ligneReceptionProduitMapper
    ) {
        this.ligneReceptionProduitRepository = ligneReceptionProduitRepository;
        this.ligneReceptionProduitMapper = ligneReceptionProduitMapper;
    }

    /**
     * Return a {@link List} of {@link LigneReceptionProduitDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LigneReceptionProduitDTO> findByCriteria(LigneReceptionProduitCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<LigneReceptionProduit> specification = createSpecification(criteria);
        return ligneReceptionProduitMapper.toDto(ligneReceptionProduitRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LigneReceptionProduitCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<LigneReceptionProduit> specification = createSpecification(criteria);
        return ligneReceptionProduitRepository.count(specification);
    }

    /**
     * Function to convert {@link LigneReceptionProduitCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LigneReceptionProduit> createSpecification(LigneReceptionProduitCriteria criteria) {
        Specification<LigneReceptionProduit> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), LigneReceptionProduit_.id),
                buildRangeSpecification(criteria.getQuantiteAttendue(), LigneReceptionProduit_.quantiteAttendue),
                buildRangeSpecification(criteria.getQuantiteRecue(), LigneReceptionProduit_.quantiteRecue),
                buildRangeSpecification(criteria.getEcart(), LigneReceptionProduit_.ecart),
                buildStringSpecification(criteria.getCodeBarresScanne(), LigneReceptionProduit_.codeBarresScanne),
                buildSpecification(criteria.getReceptionId(), root ->
                    root.join(LigneReceptionProduit_.reception, JoinType.LEFT).get(ReceptionProduit_.id)
                ),
                buildSpecification(criteria.getProduitId(), root ->
                    root.join(LigneReceptionProduit_.produit, JoinType.LEFT).get(Produit_.id)
                )
            );
        }
        return specification;
    }
}
