package com.adm.supervision.service;

import com.adm.supervision.domain.EtiquetteProduit;
import com.adm.supervision.repository.EtiquetteProduitRepository;
import com.adm.supervision.service.dto.EtiquetteProduitDTO;
import com.adm.supervision.service.mapper.EtiquetteProduitMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.EtiquetteProduit}.
 */
@Service
@Transactional
public class EtiquetteProduitService {

    private static final Logger LOG = LoggerFactory.getLogger(EtiquetteProduitService.class);

    private final EtiquetteProduitRepository etiquetteProduitRepository;

    private final EtiquetteProduitMapper etiquetteProduitMapper;

    public EtiquetteProduitService(EtiquetteProduitRepository etiquetteProduitRepository, EtiquetteProduitMapper etiquetteProduitMapper) {
        this.etiquetteProduitRepository = etiquetteProduitRepository;
        this.etiquetteProduitMapper = etiquetteProduitMapper;
    }

    /**
     * Save a etiquetteProduit.
     *
     * @param etiquetteProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public EtiquetteProduitDTO save(EtiquetteProduitDTO etiquetteProduitDTO) {
        LOG.debug("Request to save EtiquetteProduit : {}", etiquetteProduitDTO);
        EtiquetteProduit etiquetteProduit = etiquetteProduitMapper.toEntity(etiquetteProduitDTO);
        etiquetteProduit = etiquetteProduitRepository.save(etiquetteProduit);
        return etiquetteProduitMapper.toDto(etiquetteProduit);
    }

    /**
     * Update a etiquetteProduit.
     *
     * @param etiquetteProduitDTO the entity to save.
     * @return the persisted entity.
     */
    public EtiquetteProduitDTO update(EtiquetteProduitDTO etiquetteProduitDTO) {
        LOG.debug("Request to update EtiquetteProduit : {}", etiquetteProduitDTO);
        EtiquetteProduit etiquetteProduit = etiquetteProduitMapper.toEntity(etiquetteProduitDTO);
        etiquetteProduit = etiquetteProduitRepository.save(etiquetteProduit);
        return etiquetteProduitMapper.toDto(etiquetteProduit);
    }

    /**
     * Partially update a etiquetteProduit.
     *
     * @param etiquetteProduitDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EtiquetteProduitDTO> partialUpdate(EtiquetteProduitDTO etiquetteProduitDTO) {
        LOG.debug("Request to partially update EtiquetteProduit : {}", etiquetteProduitDTO);

        return etiquetteProduitRepository
            .findById(etiquetteProduitDTO.getId())
            .map(existingEtiquetteProduit -> {
                etiquetteProduitMapper.partialUpdate(existingEtiquetteProduit, etiquetteProduitDTO);

                return existingEtiquetteProduit;
            })
            .map(etiquetteProduitRepository::save)
            .map(etiquetteProduitMapper::toDto);
    }

    /**
     * Get all the etiquetteProduits with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EtiquetteProduitDTO> findAllWithEagerRelationships(Pageable pageable) {
        return etiquetteProduitRepository.findAllWithEagerRelationships(pageable).map(etiquetteProduitMapper::toDto);
    }

    /**
     * Get one etiquetteProduit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EtiquetteProduitDTO> findOne(Long id) {
        LOG.debug("Request to get EtiquetteProduit : {}", id);
        return etiquetteProduitRepository.findOneWithEagerRelationships(id).map(etiquetteProduitMapper::toDto);
    }

    /**
     * Delete the etiquetteProduit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete EtiquetteProduit : {}", id);
        etiquetteProduitRepository.deleteById(id);
    }
}
