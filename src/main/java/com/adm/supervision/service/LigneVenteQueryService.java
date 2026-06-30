package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.LigneVente;
import com.adm.supervision.repository.LigneVenteRepository;
import com.adm.supervision.service.criteria.LigneVenteCriteria;
import com.adm.supervision.service.dto.LigneVenteDTO;
import com.adm.supervision.service.mapper.LigneVenteMapper;
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
 * Service for executing complex queries for {@link LigneVente} entities in the database.
 * The main input is a {@link LigneVenteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link LigneVenteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LigneVenteQueryService extends QueryService<LigneVente> {

    private static final Logger LOG = LoggerFactory.getLogger(LigneVenteQueryService.class);

    private final LigneVenteRepository ligneVenteRepository;

    private final LigneVenteMapper ligneVenteMapper;

    public LigneVenteQueryService(LigneVenteRepository ligneVenteRepository, LigneVenteMapper ligneVenteMapper) {
        this.ligneVenteRepository = ligneVenteRepository;
        this.ligneVenteMapper = ligneVenteMapper;
    }

    /**
     * Return a {@link Page} of {@link LigneVenteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<LigneVenteDTO> findByCriteria(LigneVenteCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<LigneVente> specification = createSpecification(criteria);
        return ligneVenteRepository.findAll(specification, page).map(ligneVenteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LigneVenteCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<LigneVente> specification = createSpecification(criteria);
        return ligneVenteRepository.count(specification);
    }

    /**
     * Function to convert {@link LigneVenteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LigneVente> createSpecification(LigneVenteCriteria criteria) {
        Specification<LigneVente> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), LigneVente_.id),
                buildRangeSpecification(criteria.getQuantite(), LigneVente_.quantite),
                buildRangeSpecification(criteria.getPrixUnitaire(), LigneVente_.prixUnitaire),
                buildRangeSpecification(criteria.getRemise(), LigneVente_.remise),
                buildRangeSpecification(criteria.getMontantLigne(), LigneVente_.montantLigne),
                buildStringSpecification(criteria.getCodeBarresScanne(), LigneVente_.codeBarresScanne),
                buildSpecification(criteria.getVenteId(), root -> root.join(LigneVente_.vente, JoinType.LEFT).get(Vente_.id)),
                buildSpecification(criteria.getProduitId(), root -> root.join(LigneVente_.produit, JoinType.LEFT).get(Produit_.id))
            );
        }
        return specification;
    }
}
