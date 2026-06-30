package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.ScanInconnu;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.dto.ProduitDTO;
import com.adm.supervision.service.dto.ScanInconnuDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ScanInconnu} and its DTO {@link ScanInconnuDTO}.
 */
@Mapper(componentModel = "spring")
public interface ScanInconnuMapper extends EntityMapper<ScanInconnuDTO, ScanInconnu> {
    @Mapping(target = "boutique", source = "boutique", qualifiedByName = "boutiqueNom")
    @Mapping(target = "produitAffecte", source = "produitAffecte", qualifiedByName = "produitDesignation")
    ScanInconnuDTO toDto(ScanInconnu s);

    @Named("boutiqueNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    BoutiqueDTO toDtoBoutiqueNom(Boutique boutique);

    @Named("produitDesignation")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "designation", source = "designation")
    ProduitDTO toDtoProduitDesignation(Produit produit);
}
