package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.LigneInventaireStock;
import com.adm.supervision.repository.LigneInventaireStockRepository;
import com.adm.supervision.service.criteria.LigneInventaireStockCriteria;
import com.adm.supervision.service.dto.LigneInventaireStockDTO;
import com.adm.supervision.service.mapper.LigneInventaireStockMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link LigneInventaireStock} entities in the database.
 * The main input is a {@link LigneInventaireStockCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LigneInventaireStockDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LigneInventaireStockQueryService extends QueryService<LigneInventaireStock> {

    private static final Logger LOG = LoggerFactory.getLogger(LigneInventaireStockQueryService.class);

    private final LigneInventaireStockRepository ligneInventaireStockRepository;

    private final LigneInventaireStockMapper ligneInventaireStockMapper;

    public LigneInventaireStockQueryService(
        LigneInventaireStockRepository ligneInventaireStockRepository,
        LigneInventaireStockMapper ligneInventaireStockMapper
    ) {
        this.ligneInventaireStockRepository = ligneInventaireStockRepository;
        this.ligneInventaireStockMapper = ligneInventaireStockMapper;
    }

    /**
     * Return a {@link List} of {@link LigneInventaireStockDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LigneInventaireStockDTO> findByCriteria(LigneInventaireStockCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<LigneInventaireStock> specification = createSpecification(criteria);
        return ligneInventaireStockMapper.toDto(ligneInventaireStockRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LigneInventaireStockCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<LigneInventaireStock> specification = createSpecification(criteria);
        return ligneInventaireStockRepository.count(specification);
    }

    /**
     * Function to convert {@link LigneInventaireStockCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<LigneInventaireStock> createSpecification(LigneInventaireStockCriteria criteria) {
        Specification<LigneInventaireStock> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), LigneInventaireStock_.id),
                buildRangeSpecification(criteria.getQuantiteTheorique(), LigneInventaireStock_.quantiteTheorique),
                buildRangeSpecification(criteria.getQuantiteComptee(), LigneInventaireStock_.quantiteComptee),
                buildRangeSpecification(criteria.getEcart(), LigneInventaireStock_.ecart),
                buildStringSpecification(criteria.getCommentaire(), LigneInventaireStock_.commentaire),
                buildSpecification(criteria.getInventaireId(), root ->
                    root.join(LigneInventaireStock_.inventaire, JoinType.LEFT).get(InventaireStock_.id)
                ),
                buildSpecification(criteria.getProduitId(), root ->
                    root.join(LigneInventaireStock_.produit, JoinType.LEFT).get(Produit_.id)
                )
            );
        }
        return specification;
    }
}
