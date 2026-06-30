package com.adm.supervision.service;

import com.adm.supervision.domain.ModePaiementRef;
import com.adm.supervision.repository.ModePaiementRefRepository;
import com.adm.supervision.service.dto.ModePaiementRefDTO;
import com.adm.supervision.service.mapper.ModePaiementRefMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.ModePaiementRef}.
 */
@Service
@Transactional
public class ModePaiementRefService {

    private static final Logger LOG = LoggerFactory.getLogger(ModePaiementRefService.class);

    private final ModePaiementRefRepository modePaiementRefRepository;

    private final ModePaiementRefMapper modePaiementRefMapper;

    public ModePaiementRefService(ModePaiementRefRepository modePaiementRefRepository, ModePaiementRefMapper modePaiementRefMapper) {
        this.modePaiementRefRepository = modePaiementRefRepository;
        this.modePaiementRefMapper = modePaiementRefMapper;
    }

    /**
     * Save a modePaiementRef.
     *
     * @param modePaiementRefDTO the entity to save.
     * @return the persisted entity.
     */
    public ModePaiementRefDTO save(ModePaiementRefDTO modePaiementRefDTO) {
        LOG.debug("Request to save ModePaiementRef : {}", modePaiementRefDTO);
        ModePaiementRef modePaiementRef = modePaiementRefMapper.toEntity(modePaiementRefDTO);
        modePaiementRef = modePaiementRefRepository.save(modePaiementRef);
        return modePaiementRefMapper.toDto(modePaiementRef);
    }

    /**
     * Update a modePaiementRef.
     *
     * @param modePaiementRefDTO the entity to save.
     * @return the persisted entity.
     */
    public ModePaiementRefDTO update(ModePaiementRefDTO modePaiementRefDTO) {
        LOG.debug("Request to update ModePaiementRef : {}", modePaiementRefDTO);
        ModePaiementRef modePaiementRef = modePaiementRefMapper.toEntity(modePaiementRefDTO);
        modePaiementRef = modePaiementRefRepository.save(modePaiementRef);
        return modePaiementRefMapper.toDto(modePaiementRef);
    }

    /**
     * Partially update a modePaiementRef.
     *
     * @param modePaiementRefDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ModePaiementRefDTO> partialUpdate(ModePaiementRefDTO modePaiementRefDTO) {
        LOG.debug("Request to partially update ModePaiementRef : {}", modePaiementRefDTO);

        return modePaiementRefRepository
            .findById(modePaiementRefDTO.getId())
            .map(existingModePaiementRef -> {
                modePaiementRefMapper.partialUpdate(existingModePaiementRef, modePaiementRefDTO);

                return existingModePaiementRef;
            })
            .map(modePaiementRefRepository::save)
            .map(modePaiementRefMapper::toDto);
    }

    /**
     * Get one modePaiementRef by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ModePaiementRefDTO> findOne(Long id) {
        LOG.debug("Request to get ModePaiementRef : {}", id);
        return modePaiementRefRepository.findById(id).map(modePaiementRefMapper::toDto);
    }

    /**
     * Delete the modePaiementRef by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ModePaiementRef : {}", id);
        modePaiementRefRepository.deleteById(id);
    }
}
