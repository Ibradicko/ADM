package com.adm.supervision.service;

import com.adm.supervision.domain.LigneReceptionProduit;
import com.adm.supervision.repository.LigneReceptionProduitRepository;
import com.adm.supervision.service.dto.LigneReceptionProduitDTO;
import com.adm.supervision.service.mapper.LigneReceptionProduitMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.LigneReceptionProduit}.
 */
@Service
@Transactional
public class LigneReceptionProduitService {

    private static final Logger LOG = LoggerFactory.getLogger(LigneReceptionProduitService.class);

    private final LigneReceptionProduitRepository ligneReceptionProduitRepository;

    private final LigneReceptionProduitMapper ligneReceptionProduitMapper;

    public LigneReceptionProduitService(
        LigneReceptionProduitRepository ligneReceptionProduitRepository,
        LigneReceptionProduitMapper ligneReceptionProduitMapper
    ) {
        this.ligneReceptionProduitRepository = ligneReceptionProduitRepository;
        this.ligneReceptionProduitMapper = ligneReceptionProduitMapper;
    }

    /**
     * Save a ligneReceptionProduit.
     *
     * @param ligneReceptionProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneReceptionProduitDTO save(LigneReceptionProduitDTO ligneReceptionProduitDTO) {
        LOG.debug("Request to save LigneReceptionProduit : {}", ligneReceptionProduitDTO);
        LigneReceptionProduit ligneReceptionProduit = ligneReceptionProduitMapper.toEntity(ligneReceptionProduitDTO);
        ligneReceptionProduit = ligneReceptionProduitRepository.save(ligneReceptionProduit);
        return ligneReceptionProduitMapper.toDto(ligneReceptionProduit);
    }

    /**
     * Update a ligneReceptionProduit.
     *
     * @param ligneReceptionProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneReceptionProduitDTO update(LigneReceptionProduitDTO ligneReceptionProduitDTO) {
        LOG.debug("Request to update LigneReceptionProduit : {}", ligneReceptionProduitDTO);
        LigneReceptionProduit ligneReceptionProduit = ligneReceptionProduitMapper.toEntity(ligneReceptionProduitDTO);
        ligneReceptionProduit = ligneReceptionProduitRepository.save(ligneReceptionProduit);
        return ligneReceptionProduitMapper.toDto(ligneReceptionProduit);
    }

    /**
     * Partially update a ligneReceptionProduit.
     *
     * @param ligneReceptionProduitDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LigneReceptionProduitDTO> partialUpdate(LigneReceptionProduitDTO ligneReceptionProduitDTO) {
        LOG.debug("Request to partially update LigneReceptionProduit : {}", ligneReceptionProduitDTO);

        return ligneReceptionProduitRepository
            .findById(ligneReceptionProduitDTO.getId())
            .map(existingLigneReceptionProduit -> {
                ligneReceptionProduitMapper.partialUpdate(existingLigneReceptionProduit, ligneReceptionProduitDTO);

                return existingLigneReceptionProduit;
            })
            .map(ligneReceptionProduitRepository::save)
            .map(ligneReceptionProduitMapper::toDto);
    }

    /**
     * Get all the ligneReceptionProduits with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<LigneReceptionProduitDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ligneReceptionProduitRepository.findAllWithEagerRelationships(pageable).map(ligneReceptionProduitMapper::toDto);
    }

    /**
     * Get one ligneReceptionProduit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LigneReceptionProduitDTO> findOne(Long id) {
        LOG.debug("Request to get LigneReceptionProduit : {}", id);
        return ligneReceptionProduitRepository.findOneWithEagerRelationships(id).map(ligneReceptionProduitMapper::toDto);
    }

    /**
     * Delete the ligneReceptionProduit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LigneReceptionProduit : {}", id);
        ligneReceptionProduitRepository.deleteById(id);
    }
}
