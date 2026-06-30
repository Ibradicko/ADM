package com.adm.supervision.service;

import com.adm.supervision.domain.LigneInventaireStock;
import com.adm.supervision.repository.LigneInventaireStockRepository;
import com.adm.supervision.service.dto.LigneInventaireStockDTO;
import com.adm.supervision.service.mapper.LigneInventaireStockMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.LigneInventaireStock}.
 */
@Service
@Transactional
public class LigneInventaireStockService {

    private static final Logger LOG = LoggerFactory.getLogger(LigneInventaireStockService.class);

    private final LigneInventaireStockRepository ligneInventaireStockRepository;

    private final LigneInventaireStockMapper ligneInventaireStockMapper;

    public LigneInventaireStockService(
        LigneInventaireStockRepository ligneInventaireStockRepository,
        LigneInventaireStockMapper ligneInventaireStockMapper
    ) {
        this.ligneInventaireStockRepository = ligneInventaireStockRepository;
        this.ligneInventaireStockMapper = ligneInventaireStockMapper;
    }

    /**
     * Save a ligneInventaireStock.
     *
     * @param ligneInventaireStockDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneInventaireStockDTO save(LigneInventaireStockDTO ligneInventaireStockDTO) {
        LOG.debug("Request to save LigneInventaireStock : {}", ligneInventaireStockDTO);
        LigneInventaireStock ligneInventaireStock = ligneInventaireStockMapper.toEntity(ligneInventaireStockDTO);
        ligneInventaireStock = ligneInventaireStockRepository.save(ligneInventaireStock);
        return ligneInventaireStockMapper.toDto(ligneInventaireStock);
    }

    /**
     * Update a ligneInventaireStock.
     *
     * @param ligneInventaireStockDTO the entity to save.
     * @return the persisted entity.
     */
    public LigneInventaireStockDTO update(LigneInventaireStockDTO ligneInventaireStockDTO) {
        LOG.debug("Request to update LigneInventaireStock : {}", ligneInventaireStockDTO);
        LigneInventaireStock ligneInventaireStock = ligneInventaireStockMapper.toEntity(ligneInventaireStockDTO);
        ligneInventaireStock = ligneInventaireStockRepository.save(ligneInventaireStock);
        return ligneInventaireStockMapper.toDto(ligneInventaireStock);
    }

    /**
     * Partially update a ligneInventaireStock.
     *
     * @param ligneInventaireStockDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LigneInventaireStockDTO> partialUpdate(LigneInventaireStockDTO ligneInventaireStockDTO) {
        LOG.debug("Request to partially update LigneInventaireStock : {}", ligneInventaireStockDTO);

        return ligneInventaireStockRepository
            .findById(ligneInventaireStockDTO.getId())
            .map(existingLigneInventaireStock -> {
                ligneInventaireStockMapper.partialUpdate(existingLigneInventaireStock, ligneInventaireStockDTO);

                return existingLigneInventaireStock;
            })
            .map(ligneInventaireStockRepository::save)
            .map(ligneInventaireStockMapper::toDto);
    }

    /**
     * Get all the ligneInventaireStocks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<LigneInventaireStockDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ligneInventaireStockRepository.findAllWithEagerRelationships(pageable).map(ligneInventaireStockMapper::toDto);
    }

    /**
     * Get one ligneInventaireStock by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LigneInventaireStockDTO> findOne(Long id) {
        LOG.debug("Request to get LigneInventaireStock : {}", id);
        return ligneInventaireStockRepository.findOneWithEagerRelationships(id).map(ligneInventaireStockMapper::toDto);
    }

    /**
     * Delete the ligneInventaireStock by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LigneInventaireStock : {}", id);
        ligneInventaireStockRepository.deleteById(id);
    }
}
