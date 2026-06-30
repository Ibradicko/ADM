package com.adm.supervision.service;

import com.adm.supervision.domain.FamilleArticle;
import com.adm.supervision.repository.FamilleArticleRepository;
import com.adm.supervision.service.dto.FamilleArticleDTO;
import com.adm.supervision.service.mapper.FamilleArticleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.FamilleArticle}.
 */
@Service
@Transactional
public class FamilleArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(FamilleArticleService.class);

    private final FamilleArticleRepository familleArticleRepository;

    private final FamilleArticleMapper familleArticleMapper;

    public FamilleArticleService(FamilleArticleRepository familleArticleRepository, FamilleArticleMapper familleArticleMapper) {
        this.familleArticleRepository = familleArticleRepository;
        this.familleArticleMapper = familleArticleMapper;
    }

    /**
     * Save a familleArticle.
     *
     * @param familleArticleDTO the entity to save.
     * @return the persisted entity.
     */
    public FamilleArticleDTO save(FamilleArticleDTO familleArticleDTO) {
        LOG.debug("Request to save FamilleArticle : {}", familleArticleDTO);
        FamilleArticle familleArticle = familleArticleMapper.toEntity(familleArticleDTO);
        familleArticle = familleArticleRepository.save(familleArticle);
        return familleArticleMapper.toDto(familleArticle);
    }

    /**
     * Update a familleArticle.
     *
     * @param familleArticleDTO the entity to save.
     * @return the persisted entity.
     */
    public FamilleArticleDTO update(FamilleArticleDTO familleArticleDTO) {
        LOG.debug("Request to update FamilleArticle : {}", familleArticleDTO);
        FamilleArticle familleArticle = familleArticleMapper.toEntity(familleArticleDTO);
        familleArticle = familleArticleRepository.save(familleArticle);
        return familleArticleMapper.toDto(familleArticle);
    }

    /**
     * Partially update a familleArticle.
     *
     * @param familleArticleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FamilleArticleDTO> partialUpdate(FamilleArticleDTO familleArticleDTO) {
        LOG.debug("Request to partially update FamilleArticle : {}", familleArticleDTO);

        return familleArticleRepository
            .findById(familleArticleDTO.getId())
            .map(existingFamilleArticle -> {
                familleArticleMapper.partialUpdate(existingFamilleArticle, familleArticleDTO);

                return existingFamilleArticle;
            })
            .map(familleArticleRepository::save)
            .map(familleArticleMapper::toDto);
    }

    /**
     * Get all the familleArticles with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<FamilleArticleDTO> findAllWithEagerRelationships(Pageable pageable) {
        return familleArticleRepository.findAllWithEagerRelationships(pageable).map(familleArticleMapper::toDto);
    }

    /**
     * Get one familleArticle by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FamilleArticleDTO> findOne(Long id) {
        LOG.debug("Request to get FamilleArticle : {}", id);
        return familleArticleRepository.findOneWithEagerRelationships(id).map(familleArticleMapper::toDto);
    }

    /**
     * Delete the familleArticle by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete FamilleArticle : {}", id);
        familleArticleRepository.deleteById(id);
    }
}
