package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.FamilleArticle;
import com.adm.supervision.repository.FamilleArticleRepository;
import com.adm.supervision.service.criteria.FamilleArticleCriteria;
import com.adm.supervision.service.dto.FamilleArticleDTO;
import com.adm.supervision.service.mapper.FamilleArticleMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link FamilleArticle} entities in the database.
 * The main input is a {@link FamilleArticleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link FamilleArticleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FamilleArticleQueryService extends QueryService<FamilleArticle> {

    private static final Logger LOG = LoggerFactory.getLogger(FamilleArticleQueryService.class);

    private final FamilleArticleRepository familleArticleRepository;

    private final FamilleArticleMapper familleArticleMapper;

    public FamilleArticleQueryService(FamilleArticleRepository familleArticleRepository, FamilleArticleMapper familleArticleMapper) {
        this.familleArticleRepository = familleArticleRepository;
        this.familleArticleMapper = familleArticleMapper;
    }

    /**
     * Return a {@link List} of {@link FamilleArticleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<FamilleArticleDTO> findByCriteria(FamilleArticleCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<FamilleArticle> specification = createSpecification(criteria);
        return familleArticleMapper.toDto(familleArticleRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FamilleArticleCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<FamilleArticle> specification = createSpecification(criteria);
        return familleArticleRepository.count(specification);
    }

    /**
     * Function to convert {@link FamilleArticleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<FamilleArticle> createSpecification(FamilleArticleCriteria criteria) {
        Specification<FamilleArticle> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), FamilleArticle_.id),
                buildStringSpecification(criteria.getCode(), FamilleArticle_.code),
                buildStringSpecification(criteria.getLibelle(), FamilleArticle_.libelle),
                buildSpecification(criteria.getStatut(), FamilleArticle_.statut),
                buildSpecification(criteria.getGroupeArticleId(), root ->
                    root.join(FamilleArticle_.groupeArticle, JoinType.LEFT).get(GroupeArticle_.id)
                )
            );
        }
        return specification;
    }
}
