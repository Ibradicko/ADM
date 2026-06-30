package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.GroupeArticle;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.RegleRedevance;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.dto.GroupeArticleDTO;
import com.adm.supervision.service.dto.LocataireDTO;
import com.adm.supervision.service.dto.ProduitDTO;
import com.adm.supervision.service.dto.RegleRedevanceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RegleRedevance} and its DTO {@link RegleRedevanceDTO}.
 */
@Mapper(componentModel = "spring")
public interface RegleRedevanceMapper extends EntityMapper<RegleRedevanceDTO, RegleRedevance> {
    @Mapping(target = "boutique", source = "boutique", qualifiedByName = "boutiqueNom")
    @Mapping(target = "locataire", source = "locataire", qualifiedByName = "locataireNom")
    @Mapping(target = "groupeArticle", source = "groupeArticle", qualifiedByName = "groupeArticleLibelle")
    @Mapping(target = "produit", source = "produit", qualifiedByName = "produitDesignation")
    RegleRedevanceDTO toDto(RegleRedevance s);

    @Named("boutiqueNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    BoutiqueDTO toDtoBoutiqueNom(Boutique boutique);

    @Named("locataireNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    LocataireDTO toDtoLocataireNom(Locataire locataire);

    @Named("groupeArticleLibelle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "libelle", source = "libelle")
    GroupeArticleDTO toDtoGroupeArticleLibelle(GroupeArticle groupeArticle);

    @Named("produitDesignation")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "designation", source = "designation")
    ProduitDTO toDtoProduitDesignation(Produit produit);
}
