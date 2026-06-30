package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.RapportExport;
import com.adm.supervision.repository.RapportExportRepository;
import com.adm.supervision.service.criteria.RapportExportCriteria;
import com.adm.supervision.service.dto.RapportExportDTO;
import com.adm.supervision.service.mapper.RapportExportMapper;
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
 * Service for executing complex queries for {@link RapportExport} entities in the database.
 * The main input is a {@link RapportExportCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RapportExportDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RapportExportQueryService extends QueryService<RapportExport> {

    private static final Logger LOG = LoggerFactory.getLogger(RapportExportQueryService.class);

    private final RapportExportRepository rapportExportRepository;

    private final RapportExportMapper rapportExportMapper;

    public RapportExportQueryService(RapportExportRepository rapportExportRepository, RapportExportMapper rapportExportMapper) {
        this.rapportExportRepository = rapportExportRepository;
        this.rapportExportMapper = rapportExportMapper;
    }

    /**
     * Return a {@link Page} of {@link RapportExportDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RapportExportDTO> findByCriteria(RapportExportCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RapportExport> specification = createSpecification(criteria);
        return rapportExportRepository.findAll(specification, page).map(rapportExportMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RapportExportCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<RapportExport> specification = createSpecification(criteria);
        return rapportExportRepository.count(specification);
    }

    /**
     * Function to convert {@link RapportExportCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RapportExport> createSpecification(RapportExportCriteria criteria) {
        Specification<RapportExport> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), RapportExport_.id),
                buildStringSpecification(criteria.getReference(), RapportExport_.reference),
                buildStringSpecification(criteria.getTypeRapport(), RapportExport_.typeRapport),
                buildSpecification(criteria.getFormat(), RapportExport_.format),
                buildRangeSpecification(criteria.getPeriodeDebut(), RapportExport_.periodeDebut),
                buildRangeSpecification(criteria.getPeriodeFin(), RapportExport_.periodeFin),
                buildStringSpecification(criteria.getCheminFichier(), RapportExport_.cheminFichier),
                buildRangeSpecification(criteria.getDateGeneration(), RapportExport_.dateGeneration),
                buildSpecification(criteria.getBoutiqueId(), root -> root.join(RapportExport_.boutique, JoinType.LEFT).get(Boutique_.id)),
                buildSpecification(criteria.getLocataireId(), root ->
                    root.join(RapportExport_.locataire, JoinType.LEFT).get(Locataire_.id)
                ),
                buildSpecification(criteria.getUtilisateurId(), root -> root.join(RapportExport_.utilisateur, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
