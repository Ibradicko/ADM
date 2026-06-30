package com.adm.supervision.service;

import com.adm.supervision.domain.HistoriqueCodeBarres;
import com.adm.supervision.repository.HistoriqueCodeBarresRepository;
import com.adm.supervision.service.dto.HistoriqueCodeBarresDTO;
import com.adm.supervision.service.mapper.HistoriqueCodeBarresMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.HistoriqueCodeBarres}.
 */
@Service
@Transactional
public class HistoriqueCodeBarresService {

    private static final Logger LOG = LoggerFactory.getLogger(HistoriqueCodeBarresService.class);

    private final HistoriqueCodeBarresRepository historiqueCodeBarresRepository;

    private final HistoriqueCodeBarresMapper historiqueCodeBarresMapper;

    public HistoriqueCodeBarresService(
        HistoriqueCodeBarresRepository historiqueCodeBarresRepository,
        HistoriqueCodeBarresMapper historiqueCodeBarresMapper
    ) {
        this.historiqueCodeBarresRepository = historiqueCodeBarresRepository;
        this.historiqueCodeBarresMapper = historiqueCodeBarresMapper;
    }

    /**
     * Save a historiqueCodeBarres.
     *
     * @param historiqueCodeBarresDTO the entity to save.
     * @return the persisted entity.
     */
    public HistoriqueCodeBarresDTO save(HistoriqueCodeBarresDTO historiqueCodeBarresDTO) {
        LOG.debug("Request to save HistoriqueCodeBarres : {}", historiqueCodeBarresDTO);
        HistoriqueCodeBarres historiqueCodeBarres = historiqueCodeBarresMapper.toEntity(historiqueCodeBarresDTO);
        historiqueCodeBarres = historiqueCodeBarresRepository.save(historiqueCodeBarres);
        return historiqueCodeBarresMapper.toDto(historiqueCodeBarres);
    }

    /**
     * Update a historiqueCodeBarres.
     *
     * @param historiqueCodeBarresDTO the entity to save.
     * @return the persisted entity.
     */
    public HistoriqueCodeBarresDTO update(HistoriqueCodeBarresDTO historiqueCodeBarresDTO) {
        LOG.debug("Request to update HistoriqueCodeBarres : {}", historiqueCodeBarresDTO);
        HistoriqueCodeBarres historiqueCodeBarres = historiqueCodeBarresMapper.toEntity(historiqueCodeBarresDTO);
        historiqueCodeBarres = historiqueCodeBarresRepository.save(historiqueCodeBarres);
        return historiqueCodeBarresMapper.toDto(historiqueCodeBarres);
    }

    /**
     * Partially update a historiqueCodeBarres.
     *
     * @param historiqueCodeBarresDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<HistoriqueCodeBarresDTO> partialUpdate(HistoriqueCodeBarresDTO historiqueCodeBarresDTO) {
        LOG.debug("Request to partially update HistoriqueCodeBarres : {}", historiqueCodeBarresDTO);

        return historiqueCodeBarresRepository
            .findById(historiqueCodeBarresDTO.getId())
            .map(existingHistoriqueCodeBarres -> {
                historiqueCodeBarresMapper.partialUpdate(existingHistoriqueCodeBarres, historiqueCodeBarresDTO);

                return existingHistoriqueCodeBarres;
            })
            .map(historiqueCodeBarresRepository::save)
            .map(historiqueCodeBarresMapper::toDto);
    }

    /**
     * Get all the historiqueCodeBarreses with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<HistoriqueCodeBarresDTO> findAllWithEagerRelationships(Pageable pageable) {
        return historiqueCodeBarresRepository.findAllWithEagerRelationships(pageable).map(historiqueCodeBarresMapper::toDto);
    }

    /**
     * Get one historiqueCodeBarres by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<HistoriqueCodeBarresDTO> findOne(Long id) {
        LOG.debug("Request to get HistoriqueCodeBarres : {}", id);
        return historiqueCodeBarresRepository.findOneWithEagerRelationships(id).map(historiqueCodeBarresMapper::toDto);
    }

    /**
     * Delete the historiqueCodeBarres by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete HistoriqueCodeBarres : {}", id);
        historiqueCodeBarresRepository.deleteById(id);
    }
}
