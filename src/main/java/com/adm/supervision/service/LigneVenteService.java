package com.adm.supervision.service;

import com.adm.supervision.domain.LigneVente;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.StatutVente;
import com.adm.supervision.repository.LigneVenteRepository;
import com.adm.supervision.repository.VenteRepository;
import com.adm.supervision.service.dto.LigneVenteDTO;
import com.adm.supervision.service.mapper.LigneVenteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.LigneVente}.
 */
@Service
@Transactional
public class LigneVenteService {

    private static final Logger LOG = LoggerFactory.getLogger(LigneVenteService.class);

    private final LigneVenteRepository ligneVenteRepository;

    private final LigneVenteMapper ligneVenteMapper;

    private final VenteRepository venteRepository;

    private final ModuleSecurityService moduleSecurityService;

    public LigneVenteService(
        LigneVenteRepository ligneVenteRepository,
        LigneVenteMapper ligneVenteMapper,
        VenteRepository venteRepository,
        ModuleSecurityService moduleSecurityService
    ) {
        this.ligneVenteRepository = ligneVenteRepository;
        this.ligneVenteMapper = ligneVenteMapper;
        this.venteRepository = venteRepository;
        this.moduleSecurityService = moduleSecurityService;
    }

    /**
     * Save a ligneVente.
     *
     * @param ligneVenteDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneVenteDTO save(LigneVenteDTO ligneVenteDTO) {
        LOG.debug("Request to save LigneVente : {}", ligneVenteDTO);
        LigneVente ligneVente = ligneVenteMapper.toEntity(ligneVenteDTO);
        ligneVente.setVente(loadEditableSale(ligneVente.getVente()));
        ligneVente = ligneVenteRepository.save(ligneVente);
        return ligneVenteMapper.toDto(ligneVente);
    }

    /**
     * Update a ligneVente.
     *
     * @param ligneVenteDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneVenteDTO update(LigneVenteDTO ligneVenteDTO) {
        LOG.debug("Request to update LigneVente : {}", ligneVenteDTO);
        LigneVente existing = getExisting(ligneVenteDTO.getId());
        loadEditableSale(existing.getVente());
        LigneVente ligneVente = ligneVenteMapper.toEntity(ligneVenteDTO);
        ligneVente.setVente(loadEditableSale(ligneVente.getVente()));
        ligneVente = ligneVenteRepository.save(ligneVente);
        return ligneVenteMapper.toDto(ligneVente);
    }

    /**
     * Partially update a ligneVente.
     *
     * @param ligneVenteDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LigneVenteDTO> partialUpdate(LigneVenteDTO ligneVenteDTO) {
        LOG.debug("Request to partially update LigneVente : {}", ligneVenteDTO);

        return ligneVenteRepository
            .findById(ligneVenteDTO.getId())
            .map(existingLigneVente -> {
                loadEditableSale(existingLigneVente.getVente());
                ligneVenteMapper.partialUpdate(existingLigneVente, ligneVenteDTO);
                existingLigneVente.setVente(loadEditableSale(existingLigneVente.getVente()));

                return existingLigneVente;
            })
            .map(ligneVenteRepository::save)
            .map(ligneVenteMapper::toDto);
    }

    /**
     * Get all the ligneVentes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<LigneVenteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ligneVenteRepository.findAllWithEagerRelationships(pageable).map(ligneVenteMapper::toDto);
    }

    /**
     * Get one ligneVente by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LigneVenteDTO> findOne(Long id) {
        LOG.debug("Request to get LigneVente : {}", id);
        return ligneVenteRepository.findOneWithEagerRelationships(id).map(ligneVenteMapper::toDto);
    }

    /**
     * Delete the ligneVente by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LigneVente : {}", id);
        loadEditableSale(getExisting(id).getVente());
        ligneVenteRepository.deleteById(id);
    }

    private LigneVente getExisting(Long id) {
        return ligneVenteRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("ligneVente", "notFound", "Ligne de vente introuvable"));
    }

    private Vente loadEditableSale(Vente saleReference) {
        if (saleReference == null || saleReference.getId() == null) {
            throw new BusinessValidationException("ligneVente", "missingSale", "La vente est obligatoire");
        }
        Vente sale = venteRepository
            .findOneWithEagerRelationships(saleReference.getId())
            .orElseThrow(() -> new BusinessValidationException("ligneVente", "saleNotFound", "Vente introuvable"));
        moduleSecurityService.assertBoutiqueAccess(sale.getBoutique().getId(), "Acces refuse a la ligne de vente");
        if (sale.getStatut() != StatutVente.BROUILLON) {
            throw new BusinessValidationException(
                "ligneVente",
                "validatedSaleImmutable",
                "Les lignes d'une vente validee ne peuvent pas etre modifiees"
            );
        }
        return sale;
    }
}
