package com.adm.supervision.service;

import com.adm.supervision.domain.ParametreCodeBarres;
import com.adm.supervision.repository.ParametreCodeBarresRepository;
import com.adm.supervision.service.dto.ParametreCodeBarresDTO;
import com.adm.supervision.service.mapper.ParametreCodeBarresMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.ParametreCodeBarres}.
 */
@Service
@Transactional
public class ParametreCodeBarresService {

    private static final Logger LOG = LoggerFactory.getLogger(ParametreCodeBarresService.class);

    private final ParametreCodeBarresRepository parametreCodeBarresRepository;

    private final ParametreCodeBarresMapper parametreCodeBarresMapper;

    public ParametreCodeBarresService(
        ParametreCodeBarresRepository parametreCodeBarresRepository,
        ParametreCodeBarresMapper parametreCodeBarresMapper
    ) {
        this.parametreCodeBarresRepository = parametreCodeBarresRepository;
        this.parametreCodeBarresMapper = parametreCodeBarresMapper;
    }

    /**
     * Save a parametreCodeBarres.
     *
     * @param parametreCodeBarresDTO the entity to save.
     * @return the persisted entity.
     */
    public ParametreCodeBarresDTO save(ParametreCodeBarresDTO parametreCodeBarresDTO) {
        LOG.debug("Request to save ParametreCodeBarres : {}", parametreCodeBarresDTO);
        ParametreCodeBarres parametreCodeBarres = parametreCodeBarresMapper.toEntity(parametreCodeBarresDTO);
        parametreCodeBarres = parametreCodeBarresRepository.save(parametreCodeBarres);
        return parametreCodeBarresMapper.toDto(parametreCodeBarres);
    }

    /**
     * Update a parametreCodeBarres.
     *
     * @param parametreCodeBarresDTO the entity to save.
     * @return the persisted entity.
     */
    public ParametreCodeBarresDTO update(ParametreCodeBarresDTO parametreCodeBarresDTO) {
        LOG.debug("Request to update ParametreCodeBarres : {}", parametreCodeBarresDTO);
        ParametreCodeBarres parametreCodeBarres = parametreCodeBarresMapper.toEntity(parametreCodeBarresDTO);
        parametreCodeBarres = parametreCodeBarresRepository.save(parametreCodeBarres);
        return parametreCodeBarresMapper.toDto(parametreCodeBarres);
    }

    /**
     * Partially update a parametreCodeBarres.
     *
     * @param parametreCodeBarresDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ParametreCodeBarresDTO> partialUpdate(ParametreCodeBarresDTO parametreCodeBarresDTO) {
        LOG.debug("Request to partially update ParametreCodeBarres : {}", parametreCodeBarresDTO);

        return parametreCodeBarresRepository
            .findById(parametreCodeBarresDTO.getId())
            .map(existingParametreCodeBarres -> {
                parametreCodeBarresMapper.partialUpdate(existingParametreCodeBarres, parametreCodeBarresDTO);

                return existingParametreCodeBarres;
            })
            .map(parametreCodeBarresRepository::save)
            .map(parametreCodeBarresMapper::toDto);
    }

    /**
     * Get one parametreCodeBarres by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ParametreCodeBarresDTO> findOne(Long id) {
        LOG.debug("Request to get ParametreCodeBarres : {}", id);
        return parametreCodeBarresRepository.findById(id).map(parametreCodeBarresMapper::toDto);
    }

    /**
     * Delete the parametreCodeBarres by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ParametreCodeBarres : {}", id);
        parametreCodeBarresRepository.deleteById(id);
    }
}
