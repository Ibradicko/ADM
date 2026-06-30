package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.Produit;
import com.adm.supervision.repository.ProduitRepository;
import com.adm.supervision.service.criteria.ProduitCriteria;
import com.adm.supervision.service.dto.ProduitDTO;
import com.adm.supervision.service.mapper.ProduitMapper;
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
 * Service for executing complex queries for {@link Produit} entities in the database.
 * The main input is a {@link ProduitCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ProduitDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProduitQueryService extends QueryService<Produit> {

    private static final Logger LOG = LoggerFactory.getLogger(ProduitQueryService.class);

    private final ProduitRepository produitRepository;

    private final ProduitMapper produitMapper;

    public ProduitQueryService(ProduitRepository produitRepository, ProduitMapper produitMapper) {
        this.produitRepository = produitRepository;
        this.produitMapper = produitMapper;
    }

    /**
     * Return a {@link Page} of {@link ProduitDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ProduitDTO> findByCriteria(ProduitCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Produit> specification = createSpecification(criteria);
        return produitRepository.findAll(specification, page).map(produitMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProduitCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Produit> specification = createSpecification(criteria);
        return produitRepository.count(specification);
    }

    /**
     * Function to convert {@link ProduitCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Produit> createSpecification(ProduitCriteria criteria) {
        Specification<Produit> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Produit_.id),
                buildStringSpecification(criteria.getCodeInterne(), Produit_.codeInterne),
                buildStringSpecification(criteria.getDesignation(), Produit_.designation),
                buildSpecification(criteria.getTypePrix(), Produit_.typePrix),
                buildRangeSpecification(criteria.getPrixVente(), Produit_.prixVente),
                buildRangeSpecification(criteria.getTauxRedevanceApplicable(), Produit_.tauxRedevanceApplicable),
                buildSpecification(criteria.getStatut(), Produit_.statut),
                buildRangeSpecification(criteria.getDateCreation(), Produit_.dateCreation),
                buildSpecification(criteria.getBoutiqueId(), root -> root.join(Produit_.boutique, JoinType.LEFT).get(Boutique_.id)),
                buildSpecification(criteria.getGroupeArticleId(), root ->
                    root.join(Produit_.groupeArticle, JoinType.LEFT).get(GroupeArticle_.id)
                ),
                buildSpecification(criteria.getFamilleArticleId(), root ->
                    root.join(Produit_.familleArticle, JoinType.LEFT).get(FamilleArticle_.id)
                ),
                buildSpecification(criteria.getSousFamilleArticleId(), root ->
                    root.join(Produit_.sousFamilleArticle, JoinType.LEFT).get(SousFamilleArticle_.id)
                ),
                buildSpecification(criteria.getUniteMesureId(), root -> root.join(Produit_.uniteMesure, JoinType.LEFT).get(UniteMesure_.id))
            );
        }
        return specification;
    }
}
