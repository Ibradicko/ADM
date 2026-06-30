package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.ProfilMetier;
import com.adm.supervision.repository.ProfilMetierRepository;
import com.adm.supervision.service.criteria.ProfilMetierCriteria;
import com.adm.supervision.service.dto.ProfilMetierDTO;
import com.adm.supervision.service.mapper.ProfilMetierMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link ProfilMetier} entities in the database.
 * The main input is a {@link ProfilMetierCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ProfilMetierDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ProfilMetierQueryService extends QueryService<ProfilMetier> {

    private static final Logger LOG = LoggerFactory.getLogger(ProfilMetierQueryService.class);

    private final ProfilMetierRepository profilMetierRepository;

    private final ProfilMetierMapper profilMetierMapper;

    public ProfilMetierQueryService(ProfilMetierRepository profilMetierRepository, ProfilMetierMapper profilMetierMapper) {
        this.profilMetierRepository = profilMetierRepository;
        this.profilMetierMapper = profilMetierMapper;
    }

    /**
     * Return a {@link List} of {@link ProfilMetierDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ProfilMetierDTO> findByCriteria(ProfilMetierCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<ProfilMetier> specification = createSpecification(criteria);
        return profilMetierMapper.toDto(profilMetierRepository.fetchBagRelationships(profilMetierRepository.findAll(specification)));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ProfilMetierCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<ProfilMetier> specification = createSpecification(criteria);
        return profilMetierRepository.count(specification);
    }

    /**
     * Function to convert {@link ProfilMetierCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ProfilMetier> createSpecification(ProfilMetierCriteria criteria) {
        Specification<ProfilMetier> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), ProfilMetier_.id),
                buildStringSpecification(criteria.getCode(), ProfilMetier_.code),
                buildStringSpecification(criteria.getLibelle(), ProfilMetier_.libelle),
                buildSpecification(criteria.getStatut(), ProfilMetier_.statut),
                buildSpecification(criteria.getPermissionsId(), root ->
                    root.join(ProfilMetier_.permissionses, JoinType.LEFT).get(PermissionMetier_.id)
                )
            );
        }
        return specification;
    }
}
