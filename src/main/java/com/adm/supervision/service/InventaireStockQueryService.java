package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.InventaireStock;
import com.adm.supervision.repository.InventaireStockRepository;
import com.adm.supervision.service.criteria.InventaireStockCriteria;
import com.adm.supervision.service.dto.InventaireStockDTO;
import com.adm.supervision.service.mapper.InventaireStockMapper;
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
 * Service for executing complex queries for {@link InventaireStock} entities in the database.
 * The main input is a {@link InventaireStockCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link InventaireStockDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InventaireStockQueryService extends QueryService<InventaireStock> {

    private static final Logger LOG = LoggerFactory.getLogger(InventaireStockQueryService.class);

    private final InventaireStockRepository inventaireStockRepository;

    private final InventaireStockMapper inventaireStockMapper;

    public InventaireStockQueryService(InventaireStockRepository inventaireStockRepository, InventaireStockMapper inventaireStockMapper) {
        this.inventaireStockRepository = inventaireStockRepository;
        this.inventaireStockMapper = inventaireStockMapper;
    }

    /**
     * Return a {@link Page} of {@link InventaireStockDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InventaireStockDTO> findByCriteria(InventaireStockCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<InventaireStock> specification = createSpecification(criteria);
        return inventaireStockRepository.findAll(specification, page).map(inventaireStockMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InventaireStockCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<InventaireStock> specification = createSpecification(criteria);
        return inventaireStockRepository.count(specification);
    }

    /**
     * Function to convert {@link InventaireStockCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<InventaireStock> createSpecification(InventaireStockCriteria criteria) {
        Specification<InventaireStock> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), InventaireStock_.id),
                buildStringSpecification(criteria.getReference(), InventaireStock_.reference),
                buildSpecification(criteria.getTypeInventaire(), InventaireStock_.typeInventaire),
                buildSpecification(criteria.getStatut(), InventaireStock_.statut),
                buildRangeSpecification(criteria.getDateDebut(), InventaireStock_.dateDebut),
                buildRangeSpecification(criteria.getDateFin(), InventaireStock_.dateFin),
                buildSpecification(criteria.getBoutiqueId(), root -> root.join(InventaireStock_.boutique, JoinType.LEFT).get(Boutique_.id)),
                buildSpecification(criteria.getDepotId(), root -> root.join(InventaireStock_.depot, JoinType.LEFT).get(DepotStock_.id)),
                buildSpecification(criteria.getUtilisateurId(), root ->
                    root.join(InventaireStock_.utilisateur, JoinType.LEFT).get(User_.id)
                )
            );
        }
        return specification;
    }
}
