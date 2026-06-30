package com.adm.supervision.service;

import com.adm.supervision.domain.SousFamilleArticle;
import com.adm.supervision.repository.SousFamilleArticleRepository;
import com.adm.supervision.service.dto.SousFamilleArticleDTO;
import com.adm.supervision.service.mapper.SousFamilleArticleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.SousFamilleArticle}.
 */
@Service
@Transactional
public class SousFamilleArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(SousFamilleArticleService.class);

    private final SousFamilleArticleRepository sousFamilleArticleRepository;

    private final SousFamilleArticleMapper sousFamilleArticleMapper;

    public SousFamilleArticleService(
        SousFamilleArticleRepository sousFamilleArticleRepository,
        SousFamilleArticleMapper sousFamilleArticleMapper
    ) {
        this.sousFamilleArticleRepository = sousFamilleArticleRepository;
        this.sousFamilleArticleMapper = sousFamilleArticleMapper;
    }

    /**
     * Save a sousFamilleArticle.
     *
     * @param sousFamilleArticleDTO the entity to save.
     * @return the persisted entity.
     */
    public SousFamilleArticleDTO save(SousFamilleArticleDTO sousFamilleArticleDTO) {
        LOG.debug("Request to save SousFamilleArticle : {}", sousFamilleArticleDTO);
        SousFamilleArticle sousFamilleArticle = sousFamilleArticleMapper.toEntity(sousFamilleArticleDTO);
        sousFamilleArticle = sousFamilleArticleRepository.save(sousFamilleArticle);
        return sousFamilleArticleMapper.toDto(sousFamilleArticle);
    }

    /**
     * Update a sousFamilleArticle.
     *
     * @param sousFamilleArticleDTO the entity to save.
     * @return the persisted entity.
     */
    public SousFamilleArticleDTO update(SousFamilleArticleDTO sousFamilleArticleDTO) {
        LOG.debug("Request to update SousFamilleArticle : {}", sousFamilleArticleDTO);
        SousFamilleArticle sousFamilleArticle = sousFamilleArticleMapper.toEntity(sousFamilleArticleDTO);
        sousFamilleArticle = sousFamilleArticleRepository.save(sousFamilleArticle);
        return sousFamilleArticleMapper.toDto(sousFamilleArticle);
    }

    /**
     * Partially update a sousFamilleArticle.
     *
     * @param sousFamilleArticleDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SousFamilleArticleDTO> partialUpdate(SousFamilleArticleDTO sousFamilleArticleDTO) {
        LOG.debug("Request to partially update SousFamilleArticle : {}", sousFamilleArticleDTO);

        return sousFamilleArticleRepository
            .findById(sousFamilleArticleDTO.getId())
            .map(existingSousFamilleArticle -> {
                sousFamilleArticleMapper.partialUpdate(existingSousFamilleArticle, sousFamilleArticleDTO);

                return existingSousFamilleArticle;
            })
            .map(sousFamilleArticleRepository::save)
            .map(sousFamilleArticleMapper::toDto);
    }

    /**
     * Get all the sousFamilleArticles with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<SousFamilleArticleDTO> findAllWithEagerRelationships(Pageable pageable) {
        return sousFamilleArticleRepository.findAllWithEagerRelationships(pageable).map(sousFamilleArticleMapper::toDto);
    }

    /**
     * Get one sousFamilleArticle by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SousFamilleArticleDTO> findOne(Long id) {
        LOG.debug("Request to get SousFamilleArticle : {}", id);
        return sousFamilleArticleRepository.findOneWithEagerRelationships(id).map(sousFamilleArticleMapper::toDto);
    }

    /**
     * Delete the sousFamilleArticle by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SousFamilleArticle : {}", id);
        sousFamilleArticleRepository.deleteById(id);
    }
}
