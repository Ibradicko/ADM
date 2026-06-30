package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.TicketCaisse;
import com.adm.supervision.repository.TicketCaisseRepository;
import com.adm.supervision.service.criteria.TicketCaisseCriteria;
import com.adm.supervision.service.dto.TicketCaisseDTO;
import com.adm.supervision.service.mapper.TicketCaisseMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TicketCaisse} entities in the database.
 * The main input is a {@link TicketCaisseCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TicketCaisseDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TicketCaisseQueryService extends QueryService<TicketCaisse> {

    private static final Logger LOG = LoggerFactory.getLogger(TicketCaisseQueryService.class);

    private final TicketCaisseRepository ticketCaisseRepository;

    private final TicketCaisseMapper ticketCaisseMapper;

    public TicketCaisseQueryService(TicketCaisseRepository ticketCaisseRepository, TicketCaisseMapper ticketCaisseMapper) {
        this.ticketCaisseRepository = ticketCaisseRepository;
        this.ticketCaisseMapper = ticketCaisseMapper;
    }

    /**
     * Return a {@link List} of {@link TicketCaisseDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TicketCaisseDTO> findByCriteria(TicketCaisseCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<TicketCaisse> specification = createSpecification(criteria);
        return ticketCaisseMapper.toDto(ticketCaisseRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TicketCaisseCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TicketCaisse> specification = createSpecification(criteria);
        return ticketCaisseRepository.count(specification);
    }

    /**
     * Function to convert {@link TicketCaisseCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TicketCaisse> createSpecification(TicketCaisseCriteria criteria) {
        Specification<TicketCaisse> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), TicketCaisse_.id),
                buildStringSpecification(criteria.getNumero(), TicketCaisse_.numero),
                buildRangeSpecification(criteria.getDateEmission(), TicketCaisse_.dateEmission),
                buildRangeSpecification(criteria.getNombreImpressions(), TicketCaisse_.nombreImpressions),
                buildSpecification(criteria.getVenteId(), root -> root.join(TicketCaisse_.vente, JoinType.LEFT).get(Vente_.id)),
                buildSpecification(criteria.getBoutiqueId(), root ->
                    root.join(TicketCaisse_.vente, JoinType.LEFT).join(Vente_.boutique, JoinType.LEFT).get(Boutique_.id)
                )
            );
        }
        return specification;
    }
}
