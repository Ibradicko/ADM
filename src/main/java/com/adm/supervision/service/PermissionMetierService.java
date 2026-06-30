package com.adm.supervision.service;

import com.adm.supervision.domain.PermissionMetier;
import com.adm.supervision.repository.PermissionMetierRepository;
import com.adm.supervision.service.dto.PermissionMetierDTO;
import com.adm.supervision.service.mapper.PermissionMetierMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.PermissionMetier}.
 */
@Service
@Transactional
public class PermissionMetierService {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionMetierService.class);

    private final PermissionMetierRepository permissionMetierRepository;

    private final PermissionMetierMapper permissionMetierMapper;

    public PermissionMetierService(PermissionMetierRepository permissionMetierRepository, PermissionMetierMapper permissionMetierMapper) {
        this.permissionMetierRepository = permissionMetierRepository;
        this.permissionMetierMapper = permissionMetierMapper;
    }

    /**
     * Save a permissionMetier.
     *
     * @param permissionMetierDTO the entity to save.
     * @return the persisted entity.
     */
    public PermissionMetierDTO save(PermissionMetierDTO permissionMetierDTO) {
        LOG.debug("Request to save PermissionMetier : {}", permissionMetierDTO);
        PermissionMetier permissionMetier = permissionMetierMapper.toEntity(permissionMetierDTO);
        permissionMetier = permissionMetierRepository.save(permissionMetier);
        return permissionMetierMapper.toDto(permissionMetier);
    }

    /**
     * Update a permissionMetier.
     *
     * @param permissionMetierDTO the entity to save.
     * @return the persisted entity.
     */
    public PermissionMetierDTO update(PermissionMetierDTO permissionMetierDTO) {
        LOG.debug("Request to update PermissionMetier : {}", permissionMetierDTO);
        PermissionMetier permissionMetier = permissionMetierMapper.toEntity(permissionMetierDTO);
        permissionMetier = permissionMetierRepository.save(permissionMetier);
        return permissionMetierMapper.toDto(permissionMetier);
    }

    /**
     * Partially update a permissionMetier.
     *
     * @param permissionMetierDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PermissionMetierDTO> partialUpdate(PermissionMetierDTO permissionMetierDTO) {
        LOG.debug("Request to partially update PermissionMetier : {}", permissionMetierDTO);

        return permissionMetierRepository
            .findById(permissionMetierDTO.getId())
            .map(existingPermissionMetier -> {
                permissionMetierMapper.partialUpdate(existingPermissionMetier, permissionMetierDTO);

                return existingPermissionMetier;
            })
            .map(permissionMetierRepository::save)
            .map(permissionMetierMapper::toDto);
    }

    /**
     * Get one permissionMetier by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PermissionMetierDTO> findOne(Long id) {
        LOG.debug("Request to get PermissionMetier : {}", id);
        return permissionMetierRepository.findById(id).map(permissionMetierMapper::toDto);
    }

    /**
     * Delete the permissionMetier by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PermissionMetier : {}", id);
        permissionMetierRepository.deleteById(id);
    }
}
