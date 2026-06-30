package com.adm.supervision.service;

import com.adm.supervision.domain.TarifProduit;
import com.adm.supervision.repository.TarifProduitRepository;
import com.adm.supervision.service.dto.TarifProduitDTO;
import com.adm.supervision.service.mapper.TarifProduitMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.TarifProduit}.
 */
@Service
@Transactional
public class TarifProduitService {

    private static final Logger LOG = LoggerFactory.getLogger(TarifProduitService.class);

    private final TarifProduitRepository tarifProduitRepository;

    private final TarifProduitMapper tarifProduitMapper;

    public TarifProduitService(TarifProduitRepository tarifProduitRepository, TarifProduitMapper tarifProduitMapper) {
        this.tarifProduitRepository = tarifProduitRepository;
        this.tarifProduitMapper = tarifProduitMapper;
    }

    /**
     * Save a tarifProduit.
     *
     * @param tarifProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public TarifProduitDTO save(TarifProduitDTO tarifProduitDTO) {
        LOG.debug("Request to save TarifProduit : {}", tarifProduitDTO);
        TarifProduit tarifProduit = tarifProduitMapper.toEntity(tarifProduitDTO);
        tarifProduit = tarifProduitRepository.save(tarifProduit);
        return tarifProduitMapper.toDto(tarifProduit);
    }

    /**
     * Update a tarifProduit.
     *
     * @param tarifProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public TarifProduitDTO update(TarifProduitDTO tarifProduitDTO) {
        LOG.debug("Request to update TarifProduit : {}", tarifProduitDTO);
        TarifProduit tarifProduit = tarifProduitMapper.toEntity(tarifProduitDTO);
        tarifProduit = tarifProduitRepository.save(tarifProduit);
        return tarifProduitMapper.toDto(tarifProduit);
    }

    /**
     * Partially update a tarifProduit.
     *
     * @param tarifProduitDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TarifProduitDTO> partialUpdate(TarifProduitDTO tarifProduitDTO) {
        LOG.debug("Request to partially update TarifProduit : {}", tarifProduitDTO);

        return tarifProduitRepository
            .findById(tarifProduitDTO.getId())
            .map(existingTarifProduit -> {
                tarifProduitMapper.partialUpdate(existingTarifProduit, tarifProduitDTO);

                return existingTarifProduit;
            })
            .map(tarifProduitRepository::save)
            .map(tarifProduitMapper::toDto);
    }

    /**
     * Get all the tarifProduits with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TarifProduitDTO> findAllWithEagerRelationships(Pageable pageable) {
        return tarifProduitRepository.findAllWithEagerRelationships(pageable).map(tarifProduitMapper::toDto);
    }

    /**
     * Get one tarifProduit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TarifProduitDTO> findOne(Long id) {
        LOG.debug("Request to get TarifProduit : {}", id);
        return tarifProduitRepository.findOneWithEagerRelationships(id).map(tarifProduitMapper::toDto);
    }

    /**
     * Delete the tarifProduit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TarifProduit : {}", id);
        tarifProduitRepository.deleteById(id);
    }
}
