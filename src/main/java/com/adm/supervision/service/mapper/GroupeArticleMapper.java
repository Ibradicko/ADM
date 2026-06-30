package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.GroupeArticle;
import com.adm.supervision.service.dto.GroupeArticleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link GroupeArticle} and its DTO {@link GroupeArticleDTO}.
 */
@Mapper(componentModel = "spring")
public interface GroupeArticleMapper extends EntityMapper<GroupeArticleDTO, GroupeArticle> {}
