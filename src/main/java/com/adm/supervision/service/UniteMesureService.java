package com.adm.supervision.service;

import com.adm.supervision.domain.UniteMesure;
import com.adm.supervision.repository.UniteMesureRepository;
import com.adm.supervision.service.dto.UniteMesureDTO;
import com.adm.supervision.service.mapper.UniteMesureMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.UniteMesure}.
 */
@Service
@Transactional
public class UniteMesureService {

    private static final Logger LOG = LoggerFactory.getLogger(UniteMesureService.class);

    private final UniteMesureRepository uniteMesureRepository;

    private final UniteMesureMapper uniteMesureMapper;

    public UniteMesureService(UniteMesureRepository uniteMesureRepository, UniteMesureMapper uniteMesureMapper) {
        this.uniteMesureRepository = uniteMesureRepository;
        this.uniteMesureMapper = uniteMesureMapper;
    }

    /**
     * Save a uniteMesure.
     *
     * @param uniteMesureDTO the entity to save.
     * @return the persisted entity.
     */
    public UniteMesureDTO save(UniteMesureDTO uniteMesureDTO) {
        LOG.debug("Request to save UniteMesure : {}", uniteMesureDTO);
        UniteMesure uniteMesure = uniteMesureMapper.toEntity(uniteMesureDTO);
        uniteMesure = uniteMesureRepository.save(uniteMesure);
        return uniteMesureMapper.toDto(uniteMesure);
    }

    /**
     * Update a uniteMesure.
     *
     * @param uniteMesureDTO the entity to save.
     * @return the persisted entity.
     */
    public UniteMesureDTO update(UniteMesureDTO uniteMesureDTO) {
        LOG.debug("Request to update UniteMesure : {}", uniteMesureDTO);
        UniteMesure uniteMesure = uniteMesureMapper.toEntity(uniteMesureDTO);
        uniteMesure = uniteMesureRepository.save(uniteMesure);
        return uniteMesureMapper.toDto(uniteMesure);
    }

    /**
     * Partially update a uniteMesure.
     *
     * @param uniteMesureDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<UniteMesureDTO> partialUpdate(UniteMesureDTO uniteMesureDTO) {
        LOG.debug("Request to partially update UniteMesure : {}", uniteMesureDTO);

        return uniteMesureRepository
            .findById(uniteMesureDTO.getId())
            .map(existingUniteMesure -> {
                uniteMesureMapper.partialUpdate(existingUniteMesure, uniteMesureDTO);

                return existingUniteMesure;
            })
            .map(uniteMesureRepository::save)
            .map(uniteMesureMapper::toDto);
    }

    /**
     * Get one uniteMesure by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UniteMesureDTO> findOne(Long id) {
        LOG.debug("Request to get UniteMesure : {}", id);
        return uniteMesureRepository.findById(id).map(uniteMesureMapper::toDto);
    }

    /**
     * Delete the uniteMesure by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete UniteMesure : {}", id);
        uniteMesureRepository.deleteById(id);
    }
}
