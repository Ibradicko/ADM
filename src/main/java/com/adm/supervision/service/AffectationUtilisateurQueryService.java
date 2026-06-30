package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.AffectationUtilisateur;
import com.adm.supervision.repository.AffectationUtilisateurRepository;
import com.adm.supervision.service.criteria.AffectationUtilisateurCriteria;
import com.adm.supervision.service.dto.AffectationUtilisateurDTO;
import com.adm.supervision.service.mapper.AffectationUtilisateurMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link AffectationUtilisateur} entities in the database.
 * The main input is a {@link AffectationUtilisateurCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AffectationUtilisateurDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AffectationUtilisateurQueryService extends QueryService<AffectationUtilisateur> {

    private static final Logger LOG = LoggerFactory.getLogger(AffectationUtilisateurQueryService.class);

    private final AffectationUtilisateurRepository affectationUtilisateurRepository;

    private final AffectationUtilisateurMapper affectationUtilisateurMapper;

    public AffectationUtilisateurQueryService(
        AffectationUtilisateurRepository affectationUtilisateurRepository,
        AffectationUtilisateurMapper affectationUtilisateurMapper
    ) {
        this.affectationUtilisateurRepository = affectationUtilisateurRepository;
        this.affectationUtilisateurMapper = affectationUtilisateurMapper;
    }

    /**
     * Return a {@link List} of {@link AffectationUtilisateurDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AffectationUtilisateurDTO> findByCriteria(AffectationUtilisateurCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<AffectationUtilisateur> specification = createSpecification(criteria);
        return affectationUtilisateurMapper.toDto(affectationUtilisateurRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AffectationUtilisateurCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AffectationUtilisateur> specification = createSpecification(criteria);
        return affectationUtilisateurRepository.count(specification);
    }

    /**
     * Function to convert {@link AffectationUtilisateurCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AffectationUtilisateur> createSpecification(AffectationUtilisateurCriteria criteria) {
        Specification<AffectationUtilisateur> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), AffectationUtilisateur_.id),
                buildRangeSpecification(criteria.getDateDebut(), AffectationUtilisateur_.dateDebut),
                buildRangeSpecification(criteria.getDateFin(), AffectationUtilisateur_.dateFin),
                buildSpecification(criteria.getActif(), AffectationUtilisateur_.actif),
                buildSpecification(criteria.getUserId(), root -> root.join(AffectationUtilisateur_.user, JoinType.LEFT).get(User_.id)),
                buildSpecification(criteria.getBoutiqueId(), root ->
                    root.join(AffectationUtilisateur_.boutique, JoinType.LEFT).get(Boutique_.id)
                ),
                buildSpecification(criteria.getProfilId(), root ->
                    root.join(AffectationUtilisateur_.profil, JoinType.LEFT).get(ProfilMetier_.id)
                )
            );
        }
        return specification;
    }
}
