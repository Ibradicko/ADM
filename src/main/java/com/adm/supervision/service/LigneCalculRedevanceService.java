package com.adm.supervision.service;

import com.adm.supervision.domain.LigneCalculRedevance;
import com.adm.supervision.repository.LigneCalculRedevanceRepository;
import com.adm.supervision.service.dto.LigneCalculRedevanceDTO;
import com.adm.supervision.service.mapper.LigneCalculRedevanceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.LigneCalculRedevance}.
 */
@Service
@Transactional
public class LigneCalculRedevanceService {

    private static final Logger LOG = LoggerFactory.getLogger(LigneCalculRedevanceService.class);

    private final LigneCalculRedevanceRepository ligneCalculRedevanceRepository;

    private final LigneCalculRedevanceMapper ligneCalculRedevanceMapper;

    public LigneCalculRedevanceService(
        LigneCalculRedevanceRepository ligneCalculRedevanceRepository,
        LigneCalculRedevanceMapper ligneCalculRedevanceMapper
    ) {
        this.ligneCalculRedevanceRepository = ligneCalculRedevanceRepository;
        this.ligneCalculRedevanceMapper = ligneCalculRedevanceMapper;
    }

    /**
     * Save a ligneCalculRedevance.
     *
     * @param ligneCalculRedevanceDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneCalculRedevanceDTO save(LigneCalculRedevanceDTO ligneCalculRedevanceDTO) {
        LOG.debug("Request to save LigneCalculRedevance : {}", ligneCalculRedevanceDTO);
        LigneCalculRedevance ligneCalculRedevance = ligneCalculRedevanceMapper.toEntity(ligneCalculRedevanceDTO);
        ligneCalculRedevance = ligneCalculRedevanceRepository.save(ligneCalculRedevance);
        return ligneCalculRedevanceMapper.toDto(ligneCalculRedevance);
    }

    /**
     * Update a ligneCalculRedevance.
     *
     * @param ligneCalculRedevanceDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneCalculRedevanceDTO update(LigneCalculRedevanceDTO ligneCalculRedevanceDTO) {
        LOG.debug("Request to update LigneCalculRedevance : {}", ligneCalculRedevanceDTO);
        LigneCalculRedevance ligneCalculRedevance = ligneCalculRedevanceMapper.toEntity(ligneCalculRedevanceDTO);
        ligneCalculRedevance = ligneCalculRedevanceRepository.save(ligneCalculRedevance);
        return ligneCalculRedevanceMapper.toDto(ligneCalculRedevance);
    }

    /**
     * Partially update a ligneCalculRedevance.
     *
     * @param ligneCalculRedevanceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LigneCalculRedevanceDTO> partialUpdate(LigneCalculRedevanceDTO ligneCalculRedevanceDTO) {
        LOG.debug("Request to partially update LigneCalculRedevance : {}", ligneCalculRedevanceDTO);

        return ligneCalculRedevanceRepository
            .findById(ligneCalculRedevanceDTO.getId())
            .map(existingLigneCalculRedevance -> {
                ligneCalculRedevanceMapper.partialUpdate(existingLigneCalculRedevance, ligneCalculRedevanceDTO);

                return existingLigneCalculRedevance;
            })
            .map(ligneCalculRedevanceRepository::save)
            .map(ligneCalculRedevanceMapper::toDto);
    }

    /**
     * Get all the ligneCalculRedevances with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<LigneCalculRedevanceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ligneCalculRedevanceRepository.findAllWithEagerRelationships(pageable).map(ligneCalculRedevanceMapper::toDto);
    }

    /**
     * Get one ligneCalculRedevance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LigneCalculRedevanceDTO> findOne(Long id) {
        LOG.debug("Request to get LigneCalculRedevance : {}", id);
        return ligneCalculRedevanceRepository.findOneWithEagerRelationships(id).map(ligneCalculRedevanceMapper::toDto);
    }

    /**
     * Delete the ligneCalculRedevance by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LigneCalculRedevance : {}", id);
        ligneCalculRedevanceRepository.deleteById(id);
    }
}
