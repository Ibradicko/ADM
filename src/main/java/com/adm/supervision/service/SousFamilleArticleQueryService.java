package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.SousFamilleArticle;
import com.adm.supervision.repository.SousFamilleArticleRepository;
import com.adm.supervision.service.criteria.SousFamilleArticleCriteria;
import com.adm.supervision.service.dto.SousFamilleArticleDTO;
import com.adm.supervision.service.mapper.SousFamilleArticleMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link SousFamilleArticle} entities in the database.
 * The main input is a {@link SousFamilleArticleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SousFamilleArticleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SousFamilleArticleQueryService extends QueryService<SousFamilleArticle> {

    private static final Logger LOG = LoggerFactory.getLogger(SousFamilleArticleQueryService.class);

    private final SousFamilleArticleRepository sousFamilleArticleRepository;

    private final SousFamilleArticleMapper sousFamilleArticleMapper;

    public SousFamilleArticleQueryService(
        SousFamilleArticleRepository sousFamilleArticleRepository,
        SousFamilleArticleMapper sousFamilleArticleMapper
    ) {
        this.sousFamilleArticleRepository = sousFamilleArticleRepository;
        this.sousFamilleArticleMapper = sousFamilleArticleMapper;
    }

    /**
     * Return a {@link List} of {@link SousFamilleArticleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SousFamilleArticleDTO> findByCriteria(SousFamilleArticleCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<SousFamilleArticle> specification = createSpecification(criteria);
        return sousFamilleArticleMapper.toDto(sousFamilleArticleRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SousFamilleArticleCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<SousFamilleArticle> specification = createSpecification(criteria);
        return sousFamilleArticleRepository.count(specification);
    }

    /**
     * Function to convert {@link SousFamilleArticleCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SousFamilleArticle> createSpecification(SousFamilleArticleCriteria criteria) {
        Specification<SousFamilleArticle> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), SousFamilleArticle_.id),
                buildStringSpecification(criteria.getCode(), SousFamilleArticle_.code),
                buildStringSpecification(criteria.getLibelle(), SousFamilleArticle_.libelle),
                buildSpecification(criteria.getStatut(), SousFamilleArticle_.statut),
                buildSpecification(criteria.getFamilleArticleId(), root ->
                    root.join(SousFamilleArticle_.familleArticle, JoinType.LEFT).get(FamilleArticle_.id)
                )
            );
        }
        return specification;
    }
}
