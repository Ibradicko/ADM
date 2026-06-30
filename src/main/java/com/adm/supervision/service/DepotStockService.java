package com.adm.supervision.service;

import com.adm.supervision.domain.DepotStock;
import com.adm.supervision.repository.DepotStockRepository;
import com.adm.supervision.service.dto.DepotStockDTO;
import com.adm.supervision.service.mapper.DepotStockMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.DepotStock}.
 */
@Service
@Transactional
public class DepotStockService {

    private static final Logger LOG = LoggerFactory.getLogger(DepotStockService.class);

    private final DepotStockRepository depotStockRepository;

    private final DepotStockMapper depotStockMapper;

    public DepotStockService(DepotStockRepository depotStockRepository, DepotStockMapper depotStockMapper) {
        this.depotStockRepository = depotStockRepository;
        this.depotStockMapper = depotStockMapper;
    }

    /**
     * Save a depotStock.
     *
     * @param depotStockDTO the entity to save.
     * @return the persisted entity.
     */
    public DepotStockDTO save(DepotStockDTO depotStockDTO) {
        LOG.debug("Request to save DepotStock : {}", depotStockDTO);
        DepotStock depotStock = depotStockMapper.toEntity(depotStockDTO);
        depotStock = depotStockRepository.save(depotStock);
        return depotStockMapper.toDto(depotStock);
    }

    /**
     * Update a depotStock.
     *
     * @param depotStockDTO the entity to save.
     * @return the persisted entity.
     */
    public DepotStockDTO update(DepotStockDTO depotStockDTO) {
        LOG.debug("Request to update DepotStock : {}", depotStockDTO);
        DepotStock depotStock = depotStockMapper.toEntity(depotStockDTO);
        depotStock = depotStockRepository.save(depotStock);
        return depotStockMapper.toDto(depotStock);
    }

    /**
     * Partially update a depotStock.
     *
     * @param depotStockDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<DepotStockDTO> partialUpdate(DepotStockDTO depotStockDTO) {
        LOG.debug("Request to partially update DepotStock : {}", depotStockDTO);

        return depotStockRepository
            .findById(depotStockDTO.getId())
            .map(existingDepotStock -> {
                depotStockMapper.partialUpdate(existingDepotStock, depotStockDTO);

                return existingDepotStock;
            })
            .map(depotStockRepository::save)
            .map(depotStockMapper::toDto);
    }

    /**
     * Get all the depotStocks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<DepotStockDTO> findAllWithEagerRelationships(Pageable pageable) {
        return depotStockRepository.findAllWithEagerRelationships(pageable).map(depotStockMapper::toDto);
    }

    /**
     * Get one depotStock by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<DepotStockDTO> findOne(Long id) {
        LOG.debug("Request to get DepotStock : {}", id);
        return depotStockRepository.findOneWithEagerRelationships(id).map(depotStockMapper::toDto);
    }

    /**
     * Delete the depotStock by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete DepotStock : {}", id);
        depotStockRepository.deleteById(id);
    }
}
