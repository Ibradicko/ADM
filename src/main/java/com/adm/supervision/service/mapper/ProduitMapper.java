package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.FamilleArticle;
import com.adm.supervision.domain.GroupeArticle;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.SousFamilleArticle;
import com.adm.supervision.domain.UniteMesure;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.dto.FamilleArticleDTO;
import com.adm.supervision.service.dto.GroupeArticleDTO;
import com.adm.supervision.service.dto.ProduitDTO;
import com.adm.supervision.service.dto.SousFamilleArticleDTO;
import com.adm.supervision.service.dto.UniteMesureDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Produit} and its DTO {@link ProduitDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProduitMapper extends EntityMapper<ProduitDTO, Produit> {
    @Mapping(target = "boutique", source = "boutique", qualifiedByName = "boutiqueNom")
    @Mapping(target = "groupeArticle", source = "groupeArticle", qualifiedByName = "groupeArticleLibelle")
    @Mapping(target = "familleArticle", source = "familleArticle", qualifiedByName = "familleArticleLibelle")
    @Mapping(target = "sousFamilleArticle", source = "sousFamilleArticle", qualifiedByName = "sousFamilleArticleLibelle")
    @Mapping(target = "uniteMesure", source = "uniteMesure", qualifiedByName = "uniteMesureCode")
    ProduitDTO toDto(Produit s);

    @Named("boutiqueNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    BoutiqueDTO toDtoBoutiqueNom(Boutique boutique);

    @Named("groupeArticleLibelle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "libelle", source = "libelle")
    GroupeArticleDTO toDtoGroupeArticleLibelle(GroupeArticle groupeArticle);

    @Named("familleArticleLibelle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "libelle", source = "libelle")
    FamilleArticleDTO toDtoFamilleArticleLibelle(FamilleArticle familleArticle);

    @Named("sousFamilleArticleLibelle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "libelle", source = "libelle")
    SousFamilleArticleDTO toDtoSousFamilleArticleLibelle(SousFamilleArticle sousFamilleArticle);

    @Named("uniteMesureCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    UniteMesureDTO toDtoUniteMesureCode(UniteMesure uniteMesure);
}
