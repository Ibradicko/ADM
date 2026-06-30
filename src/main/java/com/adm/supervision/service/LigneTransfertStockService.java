package com.adm.supervision.service;

import com.adm.supervision.domain.LigneTransfertStock;
import com.adm.supervision.repository.LigneTransfertStockRepository;
import com.adm.supervision.service.dto.LigneTransfertStockDTO;
import com.adm.supervision.service.mapper.LigneTransfertStockMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.LigneTransfertStock}.
 */
@Service
@Transactional
public class LigneTransfertStockService {

    private static final Logger LOG = LoggerFactory.getLogger(LigneTransfertStockService.class);

    private final LigneTransfertStockRepository ligneTransfertStockRepository;

    private final LigneTransfertStockMapper ligneTransfertStockMapper;

    public LigneTransfertStockService(
        LigneTransfertStockRepository ligneTransfertStockRepository,
        LigneTransfertStockMapper ligneTransfertStockMapper
    ) {
        this.ligneTransfertStockRepository = ligneTransfertStockRepository;
        this.ligneTransfertStockMapper = ligneTransfertStockMapper;
    }

    /**
     * Save a ligneTransfertStock.
     *
     * @param ligneTransfertStockDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneTransfertStockDTO save(LigneTransfertStockDTO ligneTransfertStockDTO) {
        LOG.debug("Request to save LigneTransfertStock : {}", ligneTransfertStockDTO);
        LigneTransfertStock ligneTransfertStock = ligneTransfertStockMapper.toEntity(ligneTransfertStockDTO);
        ligneTransfertStock = ligneTransfertStockRepository.save(ligneTransfertStock);
        return ligneTransfertStockMapper.toDto(ligneTransfertStock);
    }

    /**
     * Update a ligneTransfertStock.
     *
     * @param ligneTransfertStockDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneTransfertStockDTO update(LigneTransfertStockDTO ligneTransfertStockDTO) {
        LOG.debug("Request to update LigneTransfertStock : {}", ligneTransfertStockDTO);
        LigneTransfertStock ligneTransfertStock = ligneTransfertStockMapper.toEntity(ligneTransfertStockDTO);
        ligneTransfertStock = ligneTransfertStockRepository.save(ligneTransfertStock);
        return ligneTransfertStockMapper.toDto(ligneTransfertStock);
    }

    /**
     * Partially update a ligneTransfertStock.
     *
     * @param ligneTransfertStockDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LigneTransfertStockDTO> partialUpdate(LigneTransfertStockDTO ligneTransfertStockDTO) {
        LOG.debug("Request to partially update LigneTransfertStock : {}", ligneTransfertStockDTO);

        return ligneTransfertStockRepository
            .findById(ligneTransfertStockDTO.getId())
            .map(existingLigneTransfertStock -> {
                ligneTransfertStockMapper.partialUpdate(existingLigneTransfertStock, ligneTransfertStockDTO);

                return existingLigneTransfertStock;
            })
            .map(ligneTransfertStockRepository::save)
            .map(ligneTransfertStockMapper::toDto);
    }

    /**
     * Get all the ligneTransfertStocks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<LigneTransfertStockDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ligneTransfertStockRepository.findAllWithEagerRelationships(pageable).map(ligneTransfertStockMapper::toDto);
    }

    /**
     * Get one ligneTransfertStock by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LigneTransfertStockDTO> findOne(Long id) {
        LOG.debug("Request to get LigneTransfertStock : {}", id);
        return ligneTransfertStockRepository.findOneWithEagerRelationships(id).map(ligneTransfertStockMapper::toDto);
    }

    /**
     * Delete the ligneTransfertStock by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LigneTransfertStock : {}", id);
        ligneTransfertStockRepository.deleteById(id);
    }
}
