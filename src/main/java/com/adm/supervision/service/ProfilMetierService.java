package com.adm.supervision.service;

import com.adm.supervision.domain.ProfilMetier;
import com.adm.supervision.repository.ProfilMetierRepository;
import com.adm.supervision.service.dto.ProfilMetierDTO;
import com.adm.supervision.service.mapper.ProfilMetierMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.ProfilMetier}.
 */
@Service
@Transactional
public class ProfilMetierService {

    private static final Logger LOG = LoggerFactory.getLogger(ProfilMetierService.class);

    private final ProfilMetierRepository profilMetierRepository;

    private final ProfilMetierMapper profilMetierMapper;

    public ProfilMetierService(ProfilMetierRepository profilMetierRepository, ProfilMetierMapper profilMetierMapper) {
        this.profilMetierRepository = profilMetierRepository;
        this.profilMetierMapper = profilMetierMapper;
    }

    /**
     * Save a profilMetier.
     *
     * @param profilMetierDTO the entity to save.
     * @return the persisted entity.
     */
    public ProfilMetierDTO save(ProfilMetierDTO profilMetierDTO) {
        LOG.debug("Request to save ProfilMetier : {}", profilMetierDTO);
        ProfilMetier profilMetier = profilMetierMapper.toEntity(profilMetierDTO);
        profilMetier = profilMetierRepository.save(profilMetier);
        return profilMetierMapper.toDto(profilMetier);
    }

    /**
     * Update a profilMetier.
     *
     * @param profilMetierDTO the entity to save.
     * @return the persisted entity.
     */
    public ProfilMetierDTO update(ProfilMetierDTO profilMetierDTO) {
        LOG.debug("Request to update ProfilMetier : {}", profilMetierDTO);
        ProfilMetier profilMetier = profilMetierMapper.toEntity(profilMetierDTO);
        profilMetier = profilMetierRepository.save(profilMetier);
        return profilMetierMapper.toDto(profilMetier);
    }

    /**
     * Partially update a profilMetier.
     *
     * @param profilMetierDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProfilMetierDTO> partialUpdate(ProfilMetierDTO profilMetierDTO) {
        LOG.debug("Request to partially update ProfilMetier : {}", profilMetierDTO);

        return profilMetierRepository
            .findById(profilMetierDTO.getId())
            .map(existingProfilMetier -> {
                profilMetierMapper.partialUpdate(existingProfilMetier, profilMetierDTO);

                return existingProfilMetier;
            })
            .map(profilMetierRepository::save)
            .map(profilMetierMapper::toDto);
    }

    /**
     * Get all the profilMetiers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ProfilMetierDTO> findAllWithEagerRelationships(Pageable pageable) {
        return profilMetierRepository.findAllWithEagerRelationships(pageable).map(profilMetierMapper::toDto);
    }

    /**
     * Get one profilMetier by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProfilMetierDTO> findOne(Long id) {
        LOG.debug("Request to get ProfilMetier : {}", id);
        return profilMetierRepository.findOneWithEagerRelationships(id).map(profilMetierMapper::toDto);
    }

    /**
     * Delete the profilMetier by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ProfilMetier : {}", id);
        profilMetierRepository.deleteById(id);
    }
}
