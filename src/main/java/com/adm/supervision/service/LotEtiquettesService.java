package com.adm.supervision.service;

import com.adm.supervision.domain.LotEtiquettes;
import com.adm.supervision.repository.LotEtiquettesRepository;
import com.adm.supervision.service.dto.LotEtiquettesDTO;
import com.adm.supervision.service.mapper.LotEtiquettesMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.LotEtiquettes}.
 */
@Service
@Transactional
public class LotEtiquettesService {

    private static final Logger LOG = LoggerFactory.getLogger(LotEtiquettesService.class);

    private final LotEtiquettesRepository lotEtiquettesRepository;

    private final LotEtiquettesMapper lotEtiquettesMapper;

    public LotEtiquettesService(LotEtiquettesRepository lotEtiquettesRepository, LotEtiquettesMapper lotEtiquettesMapper) {
        this.lotEtiquettesRepository = lotEtiquettesRepository;
        this.lotEtiquettesMapper = lotEtiquettesMapper;
    }

    /**
     * Save a lotEtiquettes.
     *
     * @param lotEtiquettesDTO the entity to save.
     * @return the persisted entity.
     */
    public LotEtiquettesDTO save(LotEtiquettesDTO lotEtiquettesDTO) {
        LOG.debug("Request to save LotEtiquettes : {}", lotEtiquettesDTO);
        LotEtiquettes lotEtiquettes = lotEtiquettesMapper.toEntity(lotEtiquettesDTO);
        lotEtiquettes = lotEtiquettesRepository.save(lotEtiquettes);
        return lotEtiquettesMapper.toDto(lotEtiquettes);
    }

    /**
     * Update a lotEtiquettes.
     *
     * @param lotEtiquettesDTO the entity to save.
     * @return the persisted entity.
     */
    public LotEtiquettesDTO update(LotEtiquettesDTO lotEtiquettesDTO) {
        LOG.debug("Request to update LotEtiquettes : {}", lotEtiquettesDTO);
        LotEtiquettes lotEtiquettes = lotEtiquettesMapper.toEntity(lotEtiquettesDTO);
        lotEtiquettes = lotEtiquettesRepository.save(lotEtiquettes);
        return lotEtiquettesMapper.toDto(lotEtiquettes);
    }

    /**
     * Partially update a lotEtiquettes.
     *
     * @param lotEtiquettesDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LotEtiquettesDTO> partialUpdate(LotEtiquettesDTO lotEtiquettesDTO) {
        LOG.debug("Request to partially update LotEtiquettes : {}", lotEtiquettesDTO);

        return lotEtiquettesRepository
            .findById(lotEtiquettesDTO.getId())
            .map(existingLotEtiquettes -> {
                lotEtiquettesMapper.partialUpdate(existingLotEtiquettes, lotEtiquettesDTO);

                return existingLotEtiquettes;
            })
            .map(lotEtiquettesRepository::save)
            .map(lotEtiquettesMapper::toDto);
    }

    /**
     * Get one lotEtiquettes by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LotEtiquettesDTO> findOne(Long id) {
        LOG.debug("Request to get LotEtiquettes : {}", id);
        return lotEtiquettesRepository.findById(id).map(lotEtiquettesMapper::toDto);
    }

    /**
     * Delete the lotEtiquettes by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LotEtiquettes : {}", id);
        lotEtiquettesRepository.deleteById(id);
    }
}
