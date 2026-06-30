package com.adm.supervision.service;

import com.adm.supervision.domain.RegularisationRedevance;
import com.adm.supervision.repository.RegularisationRedevanceRepository;
import com.adm.supervision.service.dto.RegularisationRedevanceDTO;
import com.adm.supervision.service.mapper.RegularisationRedevanceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.RegularisationRedevance}.
 */
@Service
@Transactional
public class RegularisationRedevanceService {

    private static final Logger LOG = LoggerFactory.getLogger(RegularisationRedevanceService.class);

    private final RegularisationRedevanceRepository regularisationRedevanceRepository;

    private final RegularisationRedevanceMapper regularisationRedevanceMapper;

    public RegularisationRedevanceService(
        RegularisationRedevanceRepository regularisationRedevanceRepository,
        RegularisationRedevanceMapper regularisationRedevanceMapper
    ) {
        this.regularisationRedevanceRepository = regularisationRedevanceRepository;
        this.regularisationRedevanceMapper = regularisationRedevanceMapper;
    }

    /**
     * Save a regularisationRedevance.
     *
     * @param regularisationRedevanceDTO the entity to save.
     * @return the persisted entity.
     */
    public RegularisationRedevanceDTO save(RegularisationRedevanceDTO regularisationRedevanceDTO) {
        LOG.debug("Request to save RegularisationRedevance : {}", regularisationRedevanceDTO);
        RegularisationRedevance regularisationRedevance = regularisationRedevanceMapper.toEntity(regularisationRedevanceDTO);
        regularisationRedevance = regularisationRedevanceRepository.save(regularisationRedevance);
        return regularisationRedevanceMapper.toDto(regularisationRedevance);
    }

    /**
     * Update a regularisationRedevance.
     *
     * @param regularisationRedevanceDTO the entity to save.
     * @return the persisted entity.
     */
    public RegularisationRedevanceDTO update(RegularisationRedevanceDTO regularisationRedevanceDTO) {
        LOG.debug("Request to update RegularisationRedevance : {}", regularisationRedevanceDTO);
        RegularisationRedevance regularisationRedevance = regularisationRedevanceMapper.toEntity(regularisationRedevanceDTO);
        regularisationRedevance = regularisationRedevanceRepository.save(regularisationRedevance);
        return regularisationRedevanceMapper.toDto(regularisationRedevance);
    }

    /**
     * Partially update a regularisationRedevance.
     *
     * @param regularisationRedevanceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RegularisationRedevanceDTO> partialUpdate(RegularisationRedevanceDTO regularisationRedevanceDTO) {
        LOG.debug("Request to partially update RegularisationRedevance : {}", regularisationRedevanceDTO);

        return regularisationRedevanceRepository
            .findById(regularisationRedevanceDTO.getId())
            .map(existingRegularisationRedevance -> {
                regularisationRedevanceMapper.partialUpdate(existingRegularisationRedevance, regularisationRedevanceDTO);

                return existingRegularisationRedevance;
            })
            .map(regularisationRedevanceRepository::save)
            .map(regularisationRedevanceMapper::toDto);
    }

    /**
     * Get all the regularisationRedevances with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<RegularisationRedevanceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return regularisationRedevanceRepository.findAllWithEagerRelationships(pageable).map(regularisationRedevanceMapper::toDto);
    }

    /**
     * Get one regularisationRedevance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RegularisationRedevanceDTO> findOne(Long id) {
        LOG.debug("Request to get RegularisationRedevance : {}", id);
        return regularisationRedevanceRepository.findOneWithEagerRelationships(id).map(regularisationRedevanceMapper::toDto);
    }

    /**
     * Delete the regularisationRedevance by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete RegularisationRedevance : {}", id);
        regularisationRedevanceRepository.deleteById(id);
    }
}
