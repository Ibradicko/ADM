package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.FamilleArticle;
import com.adm.supervision.domain.GroupeArticle;
import com.adm.supervision.service.dto.FamilleArticleDTO;
import com.adm.supervision.service.dto.GroupeArticleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FamilleArticle} and its DTO {@link FamilleArticleDTO}.
 */
@Mapper(componentModel = "spring")
public interface FamilleArticleMapper extends EntityMapper<FamilleArticleDTO, FamilleArticle> {
    @Mapping(target = "groupeArticle", source = "groupeArticle", qualifiedByName = "groupeArticleLibelle")
    FamilleArticleDTO toDto(FamilleArticle s);

    @Named("groupeArticleLibelle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "libelle", source = "libelle")
    GroupeArticleDTO toDtoGroupeArticleLibelle(GroupeArticle groupeArticle);
}
