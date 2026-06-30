package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.LigneReceptionProduit;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.ReceptionProduit;
import com.adm.supervision.service.dto.LigneReceptionProduitDTO;
import com.adm.supervision.service.dto.ProduitDTO;
import com.adm.supervision.service.dto.ReceptionProduitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LigneReceptionProduit} and its DTO {@link LigneReceptionProduitDTO}.
 */
@Mapper(componentModel = "spring")
public interface LigneReceptionProduitMapper extends EntityMapper<LigneReceptionProduitDTO, LigneReceptionProduit> {
    @Mapping(target = "reception", source = "reception", qualifiedByName = "receptionProduitReference")
    @Mapping(target = "produit", source = "produit", qualifiedByName = "produitDesignation")
    LigneReceptionProduitDTO toDto(LigneReceptionProduit s);

    @Named("receptionProduitReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    ReceptionProduitDTO toDtoReceptionProduitReference(ReceptionProduit receptionProduit);

    @Named("produitDesignation")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "designation", source = "designation")
    ProduitDTO toDtoProduitDesignation(Produit produit);
}
