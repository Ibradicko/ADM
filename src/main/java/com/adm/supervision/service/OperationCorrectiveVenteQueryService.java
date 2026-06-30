package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.OperationCorrectiveVente;
import com.adm.supervision.repository.OperationCorrectiveVenteRepository;
import com.adm.supervision.service.criteria.OperationCorrectiveVenteCriteria;
import com.adm.supervision.service.dto.OperationCorrectiveVenteDTO;
import com.adm.supervision.service.mapper.OperationCorrectiveVenteMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link OperationCorrectiveVente} entities in the database.
 * The main input is a {@link OperationCorrectiveVenteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OperationCorrectiveVenteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OperationCorrectiveVenteQueryService extends QueryService<OperationCorrectiveVente> {

    private static final Logger LOG = LoggerFactory.getLogger(OperationCorrectiveVenteQueryService.class);

    private final OperationCorrectiveVenteRepository operationCorrectiveVenteRepository;

    private final OperationCorrectiveVenteMapper operationCorrectiveVenteMapper;

    private final ModuleSecurityService moduleSecurityService;

    public OperationCorrectiveVenteQueryService(
        OperationCorrectiveVenteRepository operationCorrectiveVenteRepository,
        OperationCorrectiveVenteMapper operationCorrectiveVenteMapper,
        ModuleSecurityService moduleSecurityService
    ) {
        this.operationCorrectiveVenteRepository = operationCorrectiveVenteRepository;
        this.operationCorrectiveVenteMapper = operationCorrectiveVenteMapper;
        this.moduleSecurityService = moduleSecurityService;
    }

    /**
     * Return a {@link List} of {@link OperationCorrectiveVenteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OperationCorrectiveVenteDTO> findByCriteria(OperationCorrectiveVenteCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<OperationCorrectiveVente> specification = createSpecification(criteria);
        return operationCorrectiveVenteMapper.toDto(operationCorrectiveVenteRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OperationCorrectiveVenteCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<OperationCorrectiveVente> specification = createSpecification(criteria);
        return operationCorrectiveVenteRepository.count(specification);
    }

    /**
     * Function to convert {@link OperationCorrectiveVenteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OperationCorrectiveVente> createSpecification(OperationCorrectiveVenteCriteria criteria) {
        Specification<OperationCorrectiveVente> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), OperationCorrectiveVente_.id),
                buildSpecification(criteria.getTypeOperation(), OperationCorrectiveVente_.typeOperation),
                buildStringSpecification(criteria.getMotif(), OperationCorrectiveVente_.motif),
                buildRangeSpecification(criteria.getMontantImpact(), OperationCorrectiveVente_.montantImpact),
                buildRangeSpecification(criteria.getDateOperation(), OperationCorrectiveVente_.dateOperation),
                buildSpecification(criteria.getVenteId(), root -> root.join(OperationCorrectiveVente_.vente, JoinType.LEFT).get(Vente_.id)),
                buildSpecification(criteria.getUtilisateurId(), root ->
                    root.join(OperationCorrectiveVente_.utilisateur, JoinType.LEFT).get(User_.id)
                )
            );
        }
        if (!moduleSecurityService.hasGlobalBoutiqueAccess()) {
            Set<Long> boutiqueIds = moduleSecurityService.getAccessibleBoutiqueIds();
            specification = specification.and((root, query, builder) ->
                boutiqueIds.isEmpty()
                    ? builder.disjunction()
                    : root
                          .join(OperationCorrectiveVente_.vente, JoinType.INNER)
                          .join(Vente_.boutique, JoinType.INNER)
                          .get(Boutique_.id)
                          .in(boutiqueIds)
            );
        }
        return specification;
    }
}
