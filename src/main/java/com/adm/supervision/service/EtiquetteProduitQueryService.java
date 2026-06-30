package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.EtiquetteProduit;
import com.adm.supervision.repository.EtiquetteProduitRepository;
import com.adm.supervision.service.criteria.EtiquetteProduitCriteria;
import com.adm.supervision.service.dto.EtiquetteProduitDTO;
import com.adm.supervision.service.mapper.EtiquetteProduitMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link EtiquetteProduit} entities in the database.
 * The main input is a {@link EtiquetteProduitCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link EtiquetteProduitDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EtiquetteProduitQueryService extends QueryService<EtiquetteProduit> {

    private static final Logger LOG = LoggerFactory.getLogger(EtiquetteProduitQueryService.class);

    private final EtiquetteProduitRepository etiquetteProduitRepository;

    private final EtiquetteProduitMapper etiquetteProduitMapper;

    public EtiquetteProduitQueryService(
        EtiquetteProduitRepository etiquetteProduitRepository,
        EtiquetteProduitMapper etiquetteProduitMapper
    ) {
        this.etiquetteProduitRepository = etiquetteProduitRepository;
        this.etiquetteProduitMapper = etiquetteProduitMapper;
    }

    /**
     * Return a {@link List} of {@link EtiquetteProduitDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<EtiquetteProduitDTO> findByCriteria(EtiquetteProduitCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<EtiquetteProduit> specification = createSpecification(criteria);
        return etiquetteProduitMapper.toDto(etiquetteProduitRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EtiquetteProduitCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<EtiquetteProduit> specification = createSpecification(criteria);
        return etiquetteProduitRepository.count(specification);
    }

    /**
     * Function to convert {@link EtiquetteProduitCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<EtiquetteProduit> createSpecification(EtiquetteProduitCriteria criteria) {
        Specification<EtiquetteProduit> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), EtiquetteProduit_.id),
                buildRangeSpecification(criteria.getQuantite(), EtiquetteProduit_.quantite),
                buildSpecification(criteria.getImprimee(), EtiquetteProduit_.imprimee),
                buildRangeSpecification(criteria.getDateImpression(), EtiquetteProduit_.dateImpression),
                buildSpecification(criteria.getProduitId(), root -> root.join(EtiquetteProduit_.produit, JoinType.LEFT).get(Produit_.id)),
                buildSpecification(criteria.getLotId(), root -> root.join(EtiquetteProduit_.lot, JoinType.LEFT).get(LotEtiquettes_.id))
            );
        }
        return specification;
    }
}
