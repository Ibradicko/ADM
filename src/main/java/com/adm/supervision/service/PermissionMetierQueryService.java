package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.PermissionMetier;
import com.adm.supervision.repository.PermissionMetierRepository;
import com.adm.supervision.service.criteria.PermissionMetierCriteria;
import com.adm.supervision.service.dto.PermissionMetierDTO;
import com.adm.supervision.service.mapper.PermissionMetierMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PermissionMetier} entities in the database.
 * The main input is a {@link PermissionMetierCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PermissionMetierDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PermissionMetierQueryService extends QueryService<PermissionMetier> {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionMetierQueryService.class);

    private final PermissionMetierRepository permissionMetierRepository;

    private final PermissionMetierMapper permissionMetierMapper;

    public PermissionMetierQueryService(
        PermissionMetierRepository permissionMetierRepository,
        PermissionMetierMapper permissionMetierMapper
    ) {
        this.permissionMetierRepository = permissionMetierRepository;
        this.permissionMetierMapper = permissionMetierMapper;
    }

    /**
     * Return a {@link List} of {@link PermissionMetierDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PermissionMetierDTO> findByCriteria(PermissionMetierCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<PermissionMetier> specification = createSpecification(criteria);
        return permissionMetierMapper.toDto(permissionMetierRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PermissionMetierCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PermissionMetier> specification = createSpecification(criteria);
        return permissionMetierRepository.count(specification);
    }

    /**
     * Function to convert {@link PermissionMetierCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PermissionMetier> createSpecification(PermissionMetierCriteria criteria) {
        Specification<PermissionMetier> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), PermissionMetier_.id),
                buildStringSpecification(criteria.getCode(), PermissionMetier_.code),
                buildStringSpecification(criteria.getLibelle(), PermissionMetier_.libelle),
                buildStringSpecification(criteria.getModule(), PermissionMetier_.module),
                buildSpecification(criteria.getProfilsId(), root ->
                    root.join(PermissionMetier_.profilses, JoinType.LEFT).get(ProfilMetier_.id)
                )
            );
        }
        return specification;
    }
}
