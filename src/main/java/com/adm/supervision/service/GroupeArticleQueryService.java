package com.adm.supervision.service;

import com.adm.supervision.domain.*;
import com.adm.supervision.domain.GroupeArticle;
import com.adm.supervision.repository.GroupeArticleRepository;
import com.adm.supervision.service.criteria.GroupeArticleCriteria;
import com.adm.supervision.service.dto.GroupeArticleDTO;
import com.adm.supervision.service.mapper.GroupeArticleMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link GroupeArticle} entities in the database.
 */
@Service
@Transactional(readOnly = true)
public class GroupeArticleQueryService extends QueryService<GroupeArticle> {

    private static final Logger LOG = LoggerFactory.getLogger(GroupeArticleQueryService.class);

    private final GroupeArticleRepository groupeArticleRepository;

    private final GroupeArticleMapper groupeArticleMapper;

    public GroupeArticleQueryService(GroupeArticleRepository groupeArticleRepository, GroupeArticleMapper groupeArticleMapper) {
        this.groupeArticleRepository = groupeArticleRepository;
        this.groupeArticleMapper = groupeArticleMapper;
    }

    @Transactional(readOnly = true)
    public List<GroupeArticleDTO> findByCriteria(GroupeArticleCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<GroupeArticle> specification = createSpecification(criteria);
        return groupeArticleMapper.toDto(groupeArticleRepository.findAll(specification));
    }

    @Transactional(readOnly = true)
    public long countByCriteria(GroupeArticleCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<GroupeArticle> specification = createSpecification(criteria);
        return groupeArticleRepository.count(specification);
    }

    protected Specification<GroupeArticle> createSpecification(GroupeArticleCriteria criteria) {
        Specification<GroupeArticle> specification = Specification.unrestricted();
        if (criteria != null) {
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), GroupeArticle_.id),
                buildStringSpecification(criteria.getCode(), GroupeArticle_.code),
                buildStringSpecification(criteria.getLibelle(), GroupeArticle_.libelle),
                buildSpecification(criteria.getStatut(), GroupeArticle_.statut)
            );
        }
        return specification;
    }
}
