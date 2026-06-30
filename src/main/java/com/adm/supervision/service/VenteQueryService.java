package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.Vente;
import com.adm.supervision.repository.VenteRepository;
import com.adm.supervision.service.criteria.VenteCriteria;
import com.adm.supervision.service.dto.VenteDTO;
import com.adm.supervision.service.mapper.VenteMapper;
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
 * Service for executing complex queries for {@link Vente} entities in the database.
 * The main input is a {@link VenteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link VenteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class VenteQueryService extends QueryService<Vente> {

    private static final Logger LOG = LoggerFactory.getLogger(VenteQueryService.class);

    private final VenteRepository venteRepository;

    private final VenteMapper venteMapper;

    public VenteQueryService(VenteRepository venteRepository, VenteMapper venteMapper) {
        this.venteRepository = venteRepository;
        this.venteMapper = venteMapper;
    }

    /**
     * Return a {@link Page} of {@link VenteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<VenteDTO> findByCriteria(VenteCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Vente> specification = createSpecification(criteria);
        return venteRepository.findAll(specification, page).map(venteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(VenteCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Vente> specification = createSpecification(criteria);
        return venteRepository.count(specification);
    }

    /**
     * Function to convert {@link VenteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Vente> createSpecification(VenteCriteria criteria) {
        Specification<Vente> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), Vente_.id),
                buildStringSpecification(criteria.getNumeroTicket(), Vente_.numeroTicket),
                buildRangeSpecification(criteria.getDateHeure(), Vente_.dateHeure),
                buildSpecification(criteria.getStatut(), Vente_.statut),
                buildStringSpecification(criteria.getReferencePassager(), Vente_.referencePassager),
                buildStringSpecification(criteria.getReferenceCarteEmbarquement(), Vente_.referenceCarteEmbarquement),
                buildRangeSpecification(criteria.getMontantBrut(), Vente_.montantBrut),
                buildRangeSpecification(criteria.getMontantRemise(), Vente_.montantRemise),
                buildRangeSpecification(criteria.getMontantNet(), Vente_.montantNet),
                buildStringSpecification(criteria.getCommentaire(), Vente_.commentaire),
                buildSpecification(criteria.getBoutiqueId(), root -> root.join(Vente_.boutique, JoinType.LEFT).get(Boutique_.id)),
                buildSpecification(criteria.getLocataireId(), root -> root.join(Vente_.locataire, JoinType.LEFT).get(Locataire_.id)),
                buildSpecification(criteria.getVendeurId(), root -> root.join(Vente_.vendeur, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
