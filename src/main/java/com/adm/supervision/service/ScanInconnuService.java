package com.adm.supervision.service;

import com.adm.supervision.domain.ScanInconnu;
import com.adm.supervision.repository.ScanInconnuRepository;
import com.adm.supervision.service.dto.ScanInconnuDTO;
import com.adm.supervision.service.mapper.ScanInconnuMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.ScanInconnu}.
 */
@Service
@Transactional
public class ScanInconnuService {

    private static final Logger LOG = LoggerFactory.getLogger(ScanInconnuService.class);

    private final ScanInconnuRepository scanInconnuRepository;

    private final ScanInconnuMapper scanInconnuMapper;

    public ScanInconnuService(ScanInconnuRepository scanInconnuRepository, ScanInconnuMapper scanInconnuMapper) {
        this.scanInconnuRepository = scanInconnuRepository;
        this.scanInconnuMapper = scanInconnuMapper;
    }

    /**
     * Save a scanInconnu.
     *
     * @param scanInconnuDTO the entity to save.
     * @return the persisted entity.
     */
    public ScanInconnuDTO save(ScanInconnuDTO scanInconnuDTO) {
        LOG.debug("Request to save ScanInconnu : {}", scanInconnuDTO);
        ScanInconnu scanInconnu = scanInconnuMapper.toEntity(scanInconnuDTO);
        scanInconnu = scanInconnuRepository.save(scanInconnu);
        return scanInconnuMapper.toDto(scanInconnu);
    }

    /**
     * Update a scanInconnu.
     *
     * @param scanInconnuDTO the entity to save.
     * @return the persisted entity.
     */
    public ScanInconnuDTO update(ScanInconnuDTO scanInconnuDTO) {
        LOG.debug("Request to update ScanInconnu : {}", scanInconnuDTO);
        ScanInconnu scanInconnu = scanInconnuMapper.toEntity(scanInconnuDTO);
        scanInconnu = scanInconnuRepository.save(scanInconnu);
        return scanInconnuMapper.toDto(scanInconnu);
    }

    /**
     * Partially update a scanInconnu.
     *
     * @param scanInconnuDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ScanInconnuDTO> partialUpdate(ScanInconnuDTO scanInconnuDTO) {
        LOG.debug("Request to partially update ScanInconnu : {}", scanInconnuDTO);

        return scanInconnuRepository
            .findById(scanInconnuDTO.getId())
            .map(existingScanInconnu -> {
                scanInconnuMapper.partialUpdate(existingScanInconnu, scanInconnuDTO);

                return existingScanInconnu;
            })
            .map(scanInconnuRepository::save)
            .map(scanInconnuMapper::toDto);
    }

    /**
     * Get all the scanInconnus with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ScanInconnuDTO> findAllWithEagerRelationships(Pageable pageable) {
        return scanInconnuRepository.findAllWithEagerRelationships(pageable).map(scanInconnuMapper::toDto);
    }

    /**
     * Get one scanInconnu by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ScanInconnuDTO> findOne(Long id) {
        LOG.debug("Request to get ScanInconnu : {}", id);
        return scanInconnuRepository.findOneWithEagerRelationships(id).map(scanInconnuMapper::toDto);
    }

    /**
     * Delete the scanInconnu by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ScanInconnu : {}", id);
        scanInconnuRepository.deleteById(id);
    }
}
