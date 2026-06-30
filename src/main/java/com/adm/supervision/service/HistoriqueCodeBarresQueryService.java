package com.adm.supervision.service;

import com.adm.supervision.domain.*; // for static metamodels
import com.adm.supervision.domain.HistoriqueCodeBarres;
import com.adm.supervision.repository.HistoriqueCodeBarresRepository;
import com.adm.supervision.service.criteria.HistoriqueCodeBarresCriteria;
import com.adm.supervision.service.dto.HistoriqueCodeBarresDTO;
import com.adm.supervision.service.mapper.HistoriqueCodeBarresMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link HistoriqueCodeBarres} entities in the database.
 * The main input is a {@link HistoriqueCodeBarresCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link HistoriqueCodeBarresDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class HistoriqueCodeBarresQueryService extends QueryService<HistoriqueCodeBarres> {

    private static final Logger LOG = LoggerFactory.getLogger(HistoriqueCodeBarresQueryService.class);

    private final HistoriqueCodeBarresRepository historiqueCodeBarresRepository;

    private final HistoriqueCodeBarresMapper historiqueCodeBarresMapper;

    public HistoriqueCodeBarresQueryService(
        HistoriqueCodeBarresRepository historiqueCodeBarresRepository,
        HistoriqueCodeBarresMapper historiqueCodeBarresMapper
    ) {
        this.historiqueCodeBarresRepository = historiqueCodeBarresRepository;
        this.historiqueCodeBarresMapper = historiqueCodeBarresMapper;
    }

    /**
     * Return a {@link List} of {@link HistoriqueCodeBarresDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<HistoriqueCodeBarresDTO> findByCriteria(HistoriqueCodeBarresCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<HistoriqueCodeBarres> specification = createSpecification(criteria);
        return historiqueCodeBarresMapper.toDto(historiqueCodeBarresRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(HistoriqueCodeBarresCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<HistoriqueCodeBarres> specification = createSpecification(criteria);
        return historiqueCodeBarresRepository.count(specification);
    }

    /**
     * Function to convert {@link HistoriqueCodeBarresCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<HistoriqueCodeBarres> createSpecification(HistoriqueCodeBarresCriteria criteria) {
        Specification<HistoriqueCodeBarres> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                buildRangeSpecification(criteria.getId(), HistoriqueCodeBarres_.id),
                buildStringSpecification(criteria.getAncienCode(), HistoriqueCodeBarres_.ancienCode),
                buildStringSpecification(criteria.getNouveauCode(), HistoriqueCodeBarres_.nouveauCode),
                buildStringSpecification(criteria.getMotif(), HistoriqueCodeBarres_.motif),
                buildRangeSpecification(criteria.getDateChangement(), HistoriqueCodeBarres_.dateChangement),
                buildSpecification(criteria.getProduitId(), root ->
                    root.join(HistoriqueCodeBarres_.produit, JoinType.LEFT).get(Produit_.id)
                ),
                buildSpecification(criteria.getUtilisateurId(), root ->
                    root.join(HistoriqueCodeBarres_.utilisateur, JoinType.LEFT).get(User_.id)
                )
            );
        }
        return specification;
    }
}
