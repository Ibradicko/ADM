package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.EtiquetteProduit;
import com.adm.supervision.domain.LotEtiquettes;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.service.dto.EtiquetteProduitDTO;
import com.adm.supervision.service.dto.LotEtiquettesDTO;
import com.adm.supervision.service.dto.ProduitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EtiquetteProduit} and its DTO {@link EtiquetteProduitDTO}.
 */
@Mapper(componentModel = "spring")
public interface EtiquetteProduitMapper extends EntityMapper<EtiquetteProduitDTO, EtiquetteProduit> {
    @Mapping(target = "produit", source = "produit", qualifiedByName = "produitDesignation")
    @Mapping(target = "lot", source = "lot", qualifiedByName = "lotEtiquettesReference")
    EtiquetteProduitDTO toDto(EtiquetteProduit s);

    @Named("produitDesignation")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "designation", source = "designation")
    ProduitDTO toDtoProduitDesignation(Produit produit);

    @Named("lotEtiquettesReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    LotEtiquettesDTO toDtoLotEtiquettesReference(LotEtiquettes lotEtiquettes);
}
