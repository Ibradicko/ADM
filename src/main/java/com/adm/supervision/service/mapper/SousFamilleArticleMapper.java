package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.FamilleArticle;
import com.adm.supervision.domain.SousFamilleArticle;
import com.adm.supervision.service.dto.FamilleArticleDTO;
import com.adm.supervision.service.dto.SousFamilleArticleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SousFamilleArticle} and its DTO {@link SousFamilleArticleDTO}.
 */
@Mapper(componentModel = "spring")
public interface SousFamilleArticleMapper extends EntityMapper<SousFamilleArticleDTO, SousFamilleArticle> {
    @Mapping(target = "familleArticle", source = "familleArticle", qualifiedByName = "familleArticleLibelle")
    SousFamilleArticleDTO toDto(SousFamilleArticle s);

    @Named("familleArticleLibelle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "libelle", source = "libelle")
    FamilleArticleDTO toDtoFamilleArticleLibelle(FamilleArticle familleArticle);
}
