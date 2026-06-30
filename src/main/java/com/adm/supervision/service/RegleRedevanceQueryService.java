package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.RegleRedevance;
import com.adm.supervision.repository.RegleRedevanceRepository;
import com.adm.supervision.service.criteria.RegleRedevanceCriteria;
import com.adm.supervision.service.dto.RegleRedevanceDTO;
import com.adm.supervision.service.mapper.RegleRedevanceMapper;
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
 * Service for executing complex queries for {@link RegleRedevance} entities in the database.
 * The main input is a {@link RegleRedevanceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RegleRedevanceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RegleRedevanceQueryService extends QueryService<RegleRedevance> {

    private static final Logger LOG = LoggerFactory.getLogger(RegleRedevanceQueryService.class);

    private final RegleRedevanceRepository regleRedevanceRepository;

    private final RegleRedevanceMapper regleRedevanceMapper;

    public RegleRedevanceQueryService(RegleRedevanceRepository regleRedevanceRepository, RegleRedevanceMapper regleRedevanceMapper) {
        this.regleRedevanceRepository = regleRedevanceRepository;
        this.regleRedevanceMapper = regleRedevanceMapper;
    }

    /**
     * Return a {@link Page} of {@link RegleRedevanceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RegleRedevanceDTO> findByCriteria(RegleRedevanceCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RegleRedevance> specification = createSpecification(criteria);
        return regleRedevanceRepository.findAll(specification, page).map(regleRedevanceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RegleRedevanceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<RegleRedevance> specification = createSpecification(criteria);
        return regleRedevanceRepository.count(specification);
    }

    /**
     * Function to convert {@link RegleRedevanceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RegleRedevance> createSpecification(RegleRedevanceCriteria criteria) {
        Specification<RegleRedevance> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), RegleRedevance_.id),
                buildStringSpecification(criteria.getCode(), RegleRedevance_.code),
                buildSpecification(criteria.getTypeRegle(), RegleRedevance_.typeRegle),
                buildRangeSpecification(criteria.getTaux(), RegleRedevance_.taux),
                buildRangeSpecification(criteria.getDateDebut(), RegleRedevance_.dateDebut),
                buildRangeSpecification(criteria.getDateFin(), RegleRedevance_.dateFin),
                buildRangeSpecification(criteria.getPriorite(), RegleRedevance_.priorite),
                buildSpecification(criteria.getActif(), RegleRedevance_.actif),
                buildSpecification(criteria.getBoutiqueId(), root -> root.join(RegleRedevance_.boutique, JoinType.LEFT).get(Boutique_.id)),
                buildSpecification(criteria.getLocataireId(), root ->
                    root.join(RegleRedevance_.locataire, JoinType.LEFT).get(Locataire_.id)
                ),
                buildSpecification(criteria.getGroupeArticleId(), root ->
                    root.join(RegleRedevance_.groupeArticle, JoinType.LEFT).get(GroupeArticle_.id)
                ),
                buildSpecification(criteria.getProduitId(), root -> root.join(RegleRedevance_.produit, JoinType.LEFT).get(Produit_.id))
            );
        }
        return specification;
    }
}
