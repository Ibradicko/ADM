package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.repository.BoutiqueRepository;
import com.adm.supervision.service.criteria.BoutiqueCriteria;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.mapper.BoutiqueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Boutique} entities in the database.
 * The main input is a {@link BoutiqueCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BoutiqueDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BoutiqueQueryService extends QueryService<Boutique> {

    private static final Logger LOG = LoggerFactory.getLogger(BoutiqueQueryService.class);

    private final BoutiqueRepository boutiqueRepository;

    private final BoutiqueMapper boutiqueMapper;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public BoutiqueQueryService(
        BoutiqueRepository boutiqueRepository,
        BoutiqueMapper boutiqueMapper,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.boutiqueRepository = boutiqueRepository;
        this.boutiqueMapper = boutiqueMapper;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * Return a {@link Page} of {@link BoutiqueDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BoutiqueDTO> findByCriteria(BoutiqueCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Boutique> specification = createSpecification(criteria);
        return boutiqueRepository.findAll(specification, page).map(boutiqueMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BoutiqueCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Boutique> specification = createSpecification(criteria);
        return boutiqueRepository.count(specification);
    }

    /**
     * Function to convert {@link BoutiqueCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Boutique> createSpecification(BoutiqueCriteria criteria) {
        Specification<Boutique> specification = Specification.unrestricted();
        BoutiqueCriteria effectiveCriteria = criteria == null ? new BoutiqueCriteria() : criteria.copy();
        effectiveCriteria.setId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(
                effectiveCriteria.getId(),
                "Vous n'etes pas autorise a consulter ces boutiques."
            )
        );
        if (effectiveCriteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(effectiveCriteria.getDistinct())
                    ? distinct(effectiveCriteria.getDistinct())
                    : Specification.unrestricted(),
                buildRangeSpecification(effectiveCriteria.getId(), Boutique_.id),
                buildStringSpecification(effectiveCriteria.getCode(), Boutique_.code),
                buildStringSpecification(effectiveCriteria.getNom(), Boutique_.nom),
                buildSpecification(effectiveCriteria.getType(), Boutique_.type),
                buildStringSpecification(effectiveCriteria.getEmplacement(), Boutique_.emplacement),
                buildStringSpecification(effectiveCriteria.getTelephone(), Boutique_.telephone),
                buildSpecification(effectiveCriteria.getStatut(), Boutique_.statut),
                buildRangeSpecification(effectiveCriteria.getDateCreation(), Boutique_.dateCreation)
            );
        }
        return specification;
    }
}
