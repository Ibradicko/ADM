package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.PaiementRedevance;
import com.adm.supervision.repository.PaiementRedevanceRepository;
import com.adm.supervision.service.criteria.PaiementRedevanceCriteria;
import com.adm.supervision.service.dto.PaiementRedevanceDTO;
import com.adm.supervision.service.mapper.PaiementRedevanceMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PaiementRedevance} entities in the database.
 * The main input is a {@link PaiementRedevanceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PaiementRedevanceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PaiementRedevanceQueryService extends QueryService<PaiementRedevance> {

    private static final Logger LOG = LoggerFactory.getLogger(PaiementRedevanceQueryService.class);

    private final PaiementRedevanceRepository paiementRedevanceRepository;

    private final PaiementRedevanceMapper paiementRedevanceMapper;

    public PaiementRedevanceQueryService(
        PaiementRedevanceRepository paiementRedevanceRepository,
        PaiementRedevanceMapper paiementRedevanceMapper
    ) {
        this.paiementRedevanceRepository = paiementRedevanceRepository;
        this.paiementRedevanceMapper = paiementRedevanceMapper;
    }

    /**
     * Return a {@link List} of {@link PaiementRedevanceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PaiementRedevanceDTO> findByCriteria(PaiementRedevanceCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<PaiementRedevance> specification = createSpecification(criteria);
        return paiementRedevanceMapper.toDto(paiementRedevanceRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PaiementRedevanceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PaiementRedevance> specification = createSpecification(criteria);
        return paiementRedevanceRepository.count(specification);
    }

    /**
     * Function to convert {@link PaiementRedevanceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PaiementRedevance> createSpecification(PaiementRedevanceCriteria criteria) {
        Specification<PaiementRedevance> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), PaiementRedevance_.id),
                buildStringSpecification(criteria.getReference(), PaiementRedevance_.reference),
                buildRangeSpecification(criteria.getMontant(), PaiementRedevance_.montant),
                buildRangeSpecification(criteria.getDatePaiement(), PaiementRedevance_.datePaiement),
                buildStringSpecification(criteria.getModePaiement(), PaiementRedevance_.modePaiement),
                buildStringSpecification(criteria.getCommentaire(), PaiementRedevance_.commentaire),
                buildSpecification(criteria.getCalculId(), root ->
                    root.join(PaiementRedevance_.calcul, JoinType.LEFT).get(CalculRedevance_.id)
                ),
                buildSpecification(criteria.getBoutiqueId(), root ->
                    root.join(PaiementRedevance_.calcul, JoinType.LEFT).join(CalculRedevance_.boutique, JoinType.LEFT).get(Boutique_.id)
                )
            );
        }
        return specification;
    }
}
