package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.CodeBarresProduit;
import com.adm.supervision.repository.CodeBarresProduitRepository;
import com.adm.supervision.service.criteria.CodeBarresProduitCriteria;
import com.adm.supervision.service.dto.CodeBarresProduitDTO;
import com.adm.supervision.service.mapper.CodeBarresProduitMapper;
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
 * Service for executing complex queries for {@link CodeBarresProduit} entities in the database.
 * The main input is a {@link CodeBarresProduitCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CodeBarresProduitDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CodeBarresProduitQueryService extends QueryService<CodeBarresProduit> {

    private static final Logger LOG = LoggerFactory.getLogger(CodeBarresProduitQueryService.class);

    private final CodeBarresProduitRepository codeBarresProduitRepository;

    private final CodeBarresProduitMapper codeBarresProduitMapper;

    public CodeBarresProduitQueryService(
        CodeBarresProduitRepository codeBarresProduitRepository,
        CodeBarresProduitMapper codeBarresProduitMapper
    ) {
        this.codeBarresProduitRepository = codeBarresProduitRepository;
        this.codeBarresProduitMapper = codeBarresProduitMapper;
    }

    /**
     * Return a {@link Page} of {@link CodeBarresProduitDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CodeBarresProduitDTO> findByCriteria(CodeBarresProduitCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CodeBarresProduit> specification = createSpecification(criteria);
        return codeBarresProduitRepository.findAll(specification, page).map(codeBarresProduitMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CodeBarresProduitCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CodeBarresProduit> specification = createSpecification(criteria);
        return codeBarresProduitRepository.count(specification);
    }

    /**
     * Function to convert {@link CodeBarresProduitCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CodeBarresProduit> createSpecification(CodeBarresProduitCriteria criteria) {
        Specification<CodeBarresProduit> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), CodeBarresProduit_.id),
                buildStringSpecification(criteria.getCode(), CodeBarresProduit_.code),
                buildSpecification(criteria.getType(), CodeBarresProduit_.type),
                buildSpecification(criteria.getPrincipal(), CodeBarresProduit_.principal),
                buildSpecification(criteria.getGenereParSysteme(), CodeBarresProduit_.genereParSysteme),
                buildSpecification(criteria.getActif(), CodeBarresProduit_.actif),
                buildRangeSpecification(criteria.getDateAffectation(), CodeBarresProduit_.dateAffectation),
                buildSpecification(criteria.getProduitId(), root -> root.join(CodeBarresProduit_.produit, JoinType.LEFT).get(Produit_.id))
            );
        }
        return specification;
    }
}
