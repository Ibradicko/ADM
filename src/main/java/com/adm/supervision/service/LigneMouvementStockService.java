package com.adm.supervision.service;

import com.adm.supervision.domain.LigneMouvementStock;
import com.adm.supervision.repository.LigneMouvementStockRepository;
import com.adm.supervision.service.dto.LigneMouvementStockDTO;
import com.adm.supervision.service.mapper.LigneMouvementStockMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.LigneMouvementStock}.
 */
@Service
@Transactional
public class LigneMouvementStockService {

    private static final Logger LOG = LoggerFactory.getLogger(LigneMouvementStockService.class);

    private final LigneMouvementStockRepository ligneMouvementStockRepository;

    private final LigneMouvementStockMapper ligneMouvementStockMapper;

    public LigneMouvementStockService(
        LigneMouvementStockRepository ligneMouvementStockRepository,
        LigneMouvementStockMapper ligneMouvementStockMapper
    ) {
        this.ligneMouvementStockRepository = ligneMouvementStockRepository;
        this.ligneMouvementStockMapper = ligneMouvementStockMapper;
    }

    /**
     * Save a ligneMouvementStock.
     *
     * @param ligneMouvementStockDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneMouvementStockDTO save(LigneMouvementStockDTO ligneMouvementStockDTO) {
        LOG.debug("Request to save LigneMouvementStock : {}", ligneMouvementStockDTO);
        LigneMouvementStock ligneMouvementStock = ligneMouvementStockMapper.toEntity(ligneMouvementStockDTO);
        ligneMouvementStock = ligneMouvementStockRepository.save(ligneMouvementStock);
        return ligneMouvementStockMapper.toDto(ligneMouvementStock);
    }

    /**
     * Update a ligneMouvementStock.
     *
     * @param ligneMouvementStockDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneMouvementStockDTO update(LigneMouvementStockDTO ligneMouvementStockDTO) {
        LOG.debug("Request to update LigneMouvementStock : {}", ligneMouvementStockDTO);
        LigneMouvementStock ligneMouvementStock = ligneMouvementStockMapper.toEntity(ligneMouvementStockDTO);
        ligneMouvementStock = ligneMouvementStockRepository.save(ligneMouvementStock);
        return ligneMouvementStockMapper.toDto(ligneMouvementStock);
    }

    /**
     * Partially update a ligneMouvementStock.
     *
     * @param ligneMouvementStockDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LigneMouvementStockDTO> partialUpdate(LigneMouvementStockDTO ligneMouvementStockDTO) {
        LOG.debug("Request to partially update LigneMouvementStock : {}", ligneMouvementStockDTO);

        return ligneMouvementStockRepository
            .findById(ligneMouvementStockDTO.getId())
            .map(existingLigneMouvementStock -> {
                ligneMouvementStockMapper.partialUpdate(existingLigneMouvementStock, ligneMouvementStockDTO);

                return existingLigneMouvementStock;
            })
            .map(ligneMouvementStockRepository::save)
            .map(ligneMouvementStockMapper::toDto);
    }

    /**
     * Get all the ligneMouvementStocks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<LigneMouvementStockDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ligneMouvementStockRepository.findAllWithEagerRelationships(pageable).map(ligneMouvementStockMapper::toDto);
    }

    /**
     * Get one ligneMouvementStock by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LigneMouvementStockDTO> findOne(Long id) {
        LOG.debug("Request to get LigneMouvementStock : {}", id);
        return ligneMouvementStockRepository.findOneWithEagerRelationships(id).map(ligneMouvementStockMapper::toDto);
    }

    /**
     * Delete the ligneMouvementStock by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LigneMouvementStock : {}", id);
        ligneMouvementStockRepository.deleteById(id);
    }
}
