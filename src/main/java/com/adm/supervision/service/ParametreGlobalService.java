package com.adm.supervision.service;

import com.adm.supervision.domain.ParametreGlobal;
import com.adm.supervision.repository.ParametreGlobalRepository;
import com.adm.supervision.service.dto.ParametreGlobalDTO;
import com.adm.supervision.service.mapper.ParametreGlobalMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.ParametreGlobal}.
 */
@Service
@Transactional
public class ParametreGlobalService {

    private static final Logger LOG = LoggerFactory.getLogger(ParametreGlobalService.class);

    private final ParametreGlobalRepository parametreGlobalRepository;

    private final ParametreGlobalMapper parametreGlobalMapper;

    public ParametreGlobalService(ParametreGlobalRepository parametreGlobalRepository, ParametreGlobalMapper parametreGlobalMapper) {
        this.parametreGlobalRepository = parametreGlobalRepository;
        this.parametreGlobalMapper = parametreGlobalMapper;
    }

    /**
     * Save a parametreGlobal.
     *
     * @param parametreGlobalDTO the entity to save.
     * @return the persisted entity.
     */
    public ParametreGlobalDTO save(ParametreGlobalDTO parametreGlobalDTO) {
        LOG.debug("Request to save ParametreGlobal : {}", parametreGlobalDTO);
        ParametreGlobal parametreGlobal = parametreGlobalMapper.toEntity(parametreGlobalDTO);
        parametreGlobal = parametreGlobalRepository.save(parametreGlobal);
        return parametreGlobalMapper.toDto(parametreGlobal);
    }

    /**
     * Update a parametreGlobal.
     *
     * @param parametreGlobalDTO the entity to save.
     * @return the persisted entity.
     */
    public ParametreGlobalDTO update(ParametreGlobalDTO parametreGlobalDTO) {
        LOG.debug("Request to update ParametreGlobal : {}", parametreGlobalDTO);
        ParametreGlobal parametreGlobal = parametreGlobalMapper.toEntity(parametreGlobalDTO);
        parametreGlobal = parametreGlobalRepository.save(parametreGlobal);
        return parametreGlobalMapper.toDto(parametreGlobal);
    }

    /**
     * Partially update a parametreGlobal.
     *
     * @param parametreGlobalDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ParametreGlobalDTO> partialUpdate(ParametreGlobalDTO parametreGlobalDTO) {
        LOG.debug("Request to partially update ParametreGlobal : {}", parametreGlobalDTO);

        return parametreGlobalRepository
            .findById(parametreGlobalDTO.getId())
            .map(existingParametreGlobal -> {
                parametreGlobalMapper.partialUpdate(existingParametreGlobal, parametreGlobalDTO);

                return existingParametreGlobal;
            })
            .map(parametreGlobalRepository::save)
            .map(parametreGlobalMapper::toDto);
    }

    /**
     * Get one parametreGlobal by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ParametreGlobalDTO> findOne(Long id) {
        LOG.debug("Request to get ParametreGlobal : {}", id);
        return parametreGlobalRepository.findById(id).map(parametreGlobalMapper::toDto);
    }

    /**
     * Delete the parametreGlobal by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ParametreGlobal : {}", id);
        parametreGlobalRepository.deleteById(id);
    }
}
