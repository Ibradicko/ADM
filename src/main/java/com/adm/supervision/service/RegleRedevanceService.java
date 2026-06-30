package com.adm.supervision.service;

import com.adm.supervision.domain.RegleRedevance;
import com.adm.supervision.repository.RegleRedevanceRepository;
import com.adm.supervision.service.dto.RegleRedevanceDTO;
import com.adm.supervision.service.mapper.RegleRedevanceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.RegleRedevance}.
 */
@Service
@Transactional
public class RegleRedevanceService {

    private static final Logger LOG = LoggerFactory.getLogger(RegleRedevanceService.class);

    private final RegleRedevanceRepository regleRedevanceRepository;

    private final RegleRedevanceMapper regleRedevanceMapper;

    public RegleRedevanceService(RegleRedevanceRepository regleRedevanceRepository, RegleRedevanceMapper regleRedevanceMapper) {
        this.regleRedevanceRepository = regleRedevanceRepository;
        this.regleRedevanceMapper = regleRedevanceMapper;
    }

    /**
     * Save a regleRedevance.
     *
     * @param regleRedevanceDTO the entity to save.
     * @return the persisted entity.
     */
    public RegleRedevanceDTO save(RegleRedevanceDTO regleRedevanceDTO) {
        LOG.debug("Request to save RegleRedevance : {}", regleRedevanceDTO);
        RegleRedevance regleRedevance = regleRedevanceMapper.toEntity(regleRedevanceDTO);
        validate(regleRedevance);
        regleRedevance = regleRedevanceRepository.save(regleRedevance);
        return regleRedevanceMapper.toDto(regleRedevance);
    }

    /**
     * Update a regleRedevance.
     *
     * @param regleRedevanceDTO the entity to save.
     * @return the persisted entity.
     */
    public RegleRedevanceDTO update(RegleRedevanceDTO regleRedevanceDTO) {
        LOG.debug("Request to update RegleRedevance : {}", regleRedevanceDTO);
        RegleRedevance regleRedevance = regleRedevanceMapper.toEntity(regleRedevanceDTO);
        validate(regleRedevance);
        regleRedevance = regleRedevanceRepository.save(regleRedevance);
        return regleRedevanceMapper.toDto(regleRedevance);
    }

    /**
     * Partially update a regleRedevance.
     *
     * @param regleRedevanceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RegleRedevanceDTO> partialUpdate(RegleRedevanceDTO regleRedevanceDTO) {
        LOG.debug("Request to partially update RegleRedevance : {}", regleRedevanceDTO);

        return regleRedevanceRepository
            .findById(regleRedevanceDTO.getId())
            .map(existingRegleRedevance -> {
                regleRedevanceMapper.partialUpdate(existingRegleRedevance, regleRedevanceDTO);
                validate(existingRegleRedevance);

                return existingRegleRedevance;
            })
            .map(regleRedevanceRepository::save)
            .map(regleRedevanceMapper::toDto);
    }

    /**
     * Get all the regleRedevances with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<RegleRedevanceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return regleRedevanceRepository.findAllWithEagerRelationships(pageable).map(regleRedevanceMapper::toDto);
    }

    /**
     * Get one regleRedevance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RegleRedevanceDTO> findOne(Long id) {
        LOG.debug("Request to get RegleRedevance : {}", id);
        return regleRedevanceRepository.findOneWithEagerRelationships(id).map(regleRedevanceMapper::toDto);
    }

    /**
     * Delete the regleRedevance by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete RegleRedevance : {}", id);
        regleRedevanceRepository.deleteById(id);
    }

    private void validate(RegleRedevance rule) {
        if (rule.getDateFin() != null && rule.getDateFin().isBefore(rule.getDateDebut())) {
            throw new BusinessValidationException("regleRedevance", "invalidPeriod", "La date de fin doit suivre la date de debut");
        }
        boolean targetPresent = switch (rule.getTypeRegle()) {
            case BOUTIQUE -> rule.getBoutique() != null;
            case LOCATAIRE -> rule.getLocataire() != null;
            case GROUPE_ARTICLE -> rule.getGroupeArticle() != null;
            case PRODUIT -> rule.getProduit() != null;
        };
        if (!targetPresent) {
            throw new BusinessValidationException(
                "regleRedevance",
                "missingTarget",
                "La cible correspondant au type de regle est obligatoire"
            );
        }
    }
}
