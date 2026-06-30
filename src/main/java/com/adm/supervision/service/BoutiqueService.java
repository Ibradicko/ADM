package com.adm.supervision.service;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.repository.BoutiqueRepository;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.mapper.BoutiqueMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.Boutique}.
 */
@Service
@Transactional
public class BoutiqueService {

    private static final Logger LOG = LoggerFactory.getLogger(BoutiqueService.class);

    private final BoutiqueRepository boutiqueRepository;

    private final BoutiqueMapper boutiqueMapper;

    public BoutiqueService(BoutiqueRepository boutiqueRepository, BoutiqueMapper boutiqueMapper) {
        this.boutiqueRepository = boutiqueRepository;
        this.boutiqueMapper = boutiqueMapper;
    }

    /**
     * Save a boutique.
     *
     * @param boutiqueDTO the entity to save.
     * @return the persisted entity.
     */
    public BoutiqueDTO save(BoutiqueDTO boutiqueDTO) {
        LOG.debug("Request to save Boutique : {}", boutiqueDTO);
        assertCodeAvailable(boutiqueDTO.getCode(), null);
        Boutique boutique = boutiqueMapper.toEntity(boutiqueDTO);
        boutique = boutiqueRepository.save(boutique);
        return boutiqueMapper.toDto(boutique);
    }

    /**
     * Update a boutique.
     *
     * @param boutiqueDTO the entity to save.
     * @return the persisted entity.
     */
    public BoutiqueDTO update(BoutiqueDTO boutiqueDTO) {
        LOG.debug("Request to update Boutique : {}", boutiqueDTO);
        assertCodeAvailable(boutiqueDTO.getCode(), boutiqueDTO.getId());
        Boutique boutique = boutiqueMapper.toEntity(boutiqueDTO);
        boutique = boutiqueRepository.save(boutique);
        return boutiqueMapper.toDto(boutique);
    }

    /**
     * Partially update a boutique.
     *
     * @param boutiqueDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BoutiqueDTO> partialUpdate(BoutiqueDTO boutiqueDTO) {
        LOG.debug("Request to partially update Boutique : {}", boutiqueDTO);

        return boutiqueRepository
            .findById(boutiqueDTO.getId())
            .map(existingBoutique -> {
                boutiqueMapper.partialUpdate(existingBoutique, boutiqueDTO);

                return existingBoutique;
            })
            .map(boutiqueRepository::save)
            .map(boutiqueMapper::toDto);
    }

    /**
     * Get one boutique by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BoutiqueDTO> findOne(Long id) {
        LOG.debug("Request to get Boutique : {}", id);
        return boutiqueRepository.findById(id).map(boutiqueMapper::toDto);
    }

    /**
     * Delete the boutique by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Boutique : {}", id);
        if (!boutiqueRepository.existsById(id)) {
            throw new BusinessValidationException("boutique", "notFound", "Boutique introuvable");
        }
        try {
            boutiqueRepository.deleteById(id);
            boutiqueRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new BusinessValidationException(
                "boutique",
                "deleteBlocked",
                "La boutique ne peut pas etre supprimee car elle est deja utilisee par des operations, des stocks ou des affectations"
            );
        }
    }

    private void assertCodeAvailable(String code, Long currentBoutiqueId) {
        String normalizedCode = trimToNull(code);
        if (normalizedCode == null) {
            return;
        }
        boutiqueRepository
            .findOneByCodeIgnoreCase(normalizedCode)
            .filter(existing -> currentBoutiqueId == null || !existing.getId().equals(currentBoutiqueId))
            .ifPresent(existing -> {
                throw new BusinessValidationException(
                    "boutique",
                    "codeAlreadyUsed",
                    "Le code boutique '" + normalizedCode + "' est deja utilise par " + existing.getNom()
                );
            });
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
