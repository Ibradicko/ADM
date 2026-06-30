package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.ScanInconnu;
import com.adm.supervision.repository.ScanInconnuRepository;
import com.adm.supervision.service.criteria.ScanInconnuCriteria;
import com.adm.supervision.service.dto.ScanInconnuDTO;
import com.adm.supervision.service.mapper.ScanInconnuMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ScanInconnu} entities in the database.
 * The main input is a {@link ScanInconnuCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ScanInconnuDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ScanInconnuQueryService extends QueryService<ScanInconnu> {

    private static final Logger LOG = LoggerFactory.getLogger(ScanInconnuQueryService.class);

    private final ScanInconnuRepository scanInconnuRepository;

    private final ScanInconnuMapper scanInconnuMapper;

    public ScanInconnuQueryService(ScanInconnuRepository scanInconnuRepository, ScanInconnuMapper scanInconnuMapper) {
        this.scanInconnuRepository = scanInconnuRepository;
        this.scanInconnuMapper = scanInconnuMapper;
    }

    /**
     * Return a {@link List} of {@link ScanInconnuDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ScanInconnuDTO> findByCriteria(ScanInconnuCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<ScanInconnu> specification = createSpecification(criteria);
        return scanInconnuMapper.toDto(scanInconnuRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ScanInconnuCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ScanInconnu> specification = createSpecification(criteria);
        return scanInconnuRepository.count(specification);
    }

    /**
     * Function to convert {@link ScanInconnuCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ScanInconnu> createSpecification(ScanInconnuCriteria criteria) {
        Specification<ScanInconnu> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), ScanInconnu_.id),
                buildStringSpecification(criteria.getCodeScanne(), ScanInconnu_.codeScanne),
                buildStringSpecification(criteria.getEcranOrigine(), ScanInconnu_.ecranOrigine),
                buildRangeSpecification(criteria.getDateScan(), ScanInconnu_.dateScan),
                buildStringSpecification(criteria.getCommentaire(), ScanInconnu_.commentaire),
                buildSpecification(criteria.getResolu(), ScanInconnu_.resolu),
                buildSpecification(criteria.getBoutiqueId(), root -> root.join(ScanInconnu_.boutique, JoinType.LEFT).get(Boutique_.id)),
                buildSpecification(criteria.getProduitAffecteId(), root ->
                    root.join(ScanInconnu_.produitAffecte, JoinType.LEFT).get(Produit_.id)
                )
            );
        }
        return specification;
    }
}
