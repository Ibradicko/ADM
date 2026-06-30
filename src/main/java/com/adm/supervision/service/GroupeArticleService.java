package com.adm.supervision.service;

import com.adm.supervision.domain.GroupeArticle;
import com.adm.supervision.repository.GroupeArticleRepository;
import com.adm.supervision.service.dto.GroupeArticleDTO;
import com.adm.supervision.service.mapper.GroupeArticleMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.GroupeArticle}.
 */
@Service
@Transactional
public class GroupeArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(GroupeArticleService.class);

    private final GroupeArticleRepository groupeArticleRepository;

    private final GroupeArticleMapper groupeArticleMapper;

    public GroupeArticleService(GroupeArticleRepository groupeArticleRepository, GroupeArticleMapper groupeArticleMapper) {
        this.groupeArticleRepository = groupeArticleRepository;
        this.groupeArticleMapper = groupeArticleMapper;
    }

    public GroupeArticleDTO save(GroupeArticleDTO groupeArticleDTO) {
        LOG.debug("Request to save GroupeArticle : {}", groupeArticleDTO);
        GroupeArticle groupeArticle = groupeArticleMapper.toEntity(groupeArticleDTO);
        groupeArticle = groupeArticleRepository.save(groupeArticle);
        return groupeArticleMapper.toDto(groupeArticle);
    }

    public GroupeArticleDTO update(GroupeArticleDTO groupeArticleDTO) {
        LOG.debug("Request to update GroupeArticle : {}", groupeArticleDTO);
        GroupeArticle groupeArticle = groupeArticleMapper.toEntity(groupeArticleDTO);
        groupeArticle = groupeArticleRepository.save(groupeArticle);
        return groupeArticleMapper.toDto(groupeArticle);
    }

    public Optional<GroupeArticleDTO> partialUpdate(GroupeArticleDTO groupeArticleDTO) {
        LOG.debug("Request to partially update GroupeArticle : {}", groupeArticleDTO);
        return groupeArticleRepository
            .findById(groupeArticleDTO.getId())
            .map(existingGroupeArticle -> {
                groupeArticleMapper.partialUpdate(existingGroupeArticle, groupeArticleDTO);
                return existingGroupeArticle;
            })
            .map(groupeArticleRepository::save)
            .map(groupeArticleMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<GroupeArticleDTO> findOne(Long id) {
        LOG.debug("Request to get GroupeArticle : {}", id);
        return groupeArticleRepository.findById(id).map(groupeArticleMapper::toDto);
    }

    public void delete(Long id) {
        LOG.debug("Request to delete GroupeArticle : {}", id);
        groupeArticleRepository.deleteById(id);
    }
}
