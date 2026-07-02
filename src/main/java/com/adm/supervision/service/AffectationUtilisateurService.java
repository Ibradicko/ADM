package com.adm.supervision.service;

import com.adm.supervision.domain.AffectationUtilisateur;
import com.adm.supervision.repository.AffectationUtilisateurRepository;
import com.adm.supervision.service.dto.AffectationUtilisateurDTO;
import com.adm.supervision.service.mapper.AffectationUtilisateurMapper;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.AffectationUtilisateur}.
 */
@Service
@Transactional
public class AffectationUtilisateurService {

    private static final Logger LOG = LoggerFactory.getLogger(AffectationUtilisateurService.class);
    private static final List<String> SINGLE_BOUTIQUE_PROFILE_CODES = List.of("VENDEUR", "MANAGER_BOUTIQUE");

    private final AffectationUtilisateurRepository affectationUtilisateurRepository;

    private final AffectationUtilisateurMapper affectationUtilisateurMapper;

    public AffectationUtilisateurService(
        AffectationUtilisateurRepository affectationUtilisateurRepository,
        AffectationUtilisateurMapper affectationUtilisateurMapper
    ) {
        this.affectationUtilisateurRepository = affectationUtilisateurRepository;
        this.affectationUtilisateurMapper = affectationUtilisateurMapper;
    }

    /**
     * Save a affectationUtilisateur.
     *
     * @param affectationUtilisateurDTO the entity to save.
     * @return the persisted entity.
     */
    public AffectationUtilisateurDTO save(AffectationUtilisateurDTO affectationUtilisateurDTO) {
        LOG.debug("Request to save AffectationUtilisateur : {}", affectationUtilisateurDTO);
        AffectationUtilisateur affectationUtilisateur = affectationUtilisateurMapper.toEntity(affectationUtilisateurDTO);
        validateSingleBoutiqueAssignment(affectationUtilisateur);
        affectationUtilisateur = affectationUtilisateurRepository.save(affectationUtilisateur);
        return affectationUtilisateurMapper.toDto(affectationUtilisateur);
    }

    /**
     * Update a affectationUtilisateur.
     *
     * @param affectationUtilisateurDTO the entity to save.
     * @return the persisted entity.
     */
    public AffectationUtilisateurDTO update(AffectationUtilisateurDTO affectationUtilisateurDTO) {
        LOG.debug("Request to update AffectationUtilisateur : {}", affectationUtilisateurDTO);
        AffectationUtilisateur affectationUtilisateur = affectationUtilisateurMapper.toEntity(affectationUtilisateurDTO);
        validateSingleBoutiqueAssignment(affectationUtilisateur);
        affectationUtilisateur = affectationUtilisateurRepository.save(affectationUtilisateur);
        return affectationUtilisateurMapper.toDto(affectationUtilisateur);
    }

    /**
     * Partially update a affectationUtilisateur.
     *
     * @param affectationUtilisateurDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AffectationUtilisateurDTO> partialUpdate(AffectationUtilisateurDTO affectationUtilisateurDTO) {
        LOG.debug("Request to partially update AffectationUtilisateur : {}", affectationUtilisateurDTO);

        return affectationUtilisateurRepository
            .findById(affectationUtilisateurDTO.getId())
            .map(existingAffectationUtilisateur -> {
                affectationUtilisateurMapper.partialUpdate(existingAffectationUtilisateur, affectationUtilisateurDTO);
                validateSingleBoutiqueAssignment(existingAffectationUtilisateur);

                return existingAffectationUtilisateur;
            })
            .map(affectationUtilisateurRepository::save)
            .map(affectationUtilisateurMapper::toDto);
    }

    /**
     * Get all the affectationUtilisateurs with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AffectationUtilisateurDTO> findAllWithEagerRelationships(Pageable pageable) {
        return affectationUtilisateurRepository.findAllWithEagerRelationships(pageable).map(affectationUtilisateurMapper::toDto);
    }

    /**
     * Get one affectationUtilisateur by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AffectationUtilisateurDTO> findOne(Long id) {
        LOG.debug("Request to get AffectationUtilisateur : {}", id);
        return affectationUtilisateurRepository.findOneWithEagerRelationships(id).map(affectationUtilisateurMapper::toDto);
    }

    /**
     * Delete the affectationUtilisateur by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AffectationUtilisateur : {}", id);
        affectationUtilisateurRepository.deleteById(id);
    }

    public void validateSingleBoutiqueAssignment(AffectationUtilisateur affectationUtilisateur) {
        if (
            affectationUtilisateur == null ||
            !Boolean.TRUE.equals(affectationUtilisateur.getActif()) ||
            affectationUtilisateur.getUser() == null ||
            affectationUtilisateur.getUser().getId() == null ||
            affectationUtilisateur.getProfil() == null ||
            affectationUtilisateur.getProfil().getCode() == null
        ) {
            return;
        }

        String profilCode = affectationUtilisateur.getProfil().getCode().toUpperCase();
        if (!SINGLE_BOUTIQUE_PROFILE_CODES.contains(profilCode)) {
            return;
        }

        long activeAssignments = affectationUtilisateurRepository.countActiveAssignmentsForSingleBoutiqueProfiles(
            affectationUtilisateur.getUser().getId(),
            SINGLE_BOUTIQUE_PROFILE_CODES,
            affectationUtilisateur.getId()
        );
        if (activeAssignments > 0) {
            throw new BusinessValidationException(
                "affectationUtilisateur",
                "singleBoutiqueAssignment",
                "Un vendeur ou manager boutique ne peut etre rattache qu'a une seule boutique active"
            );
        }
    }
}
