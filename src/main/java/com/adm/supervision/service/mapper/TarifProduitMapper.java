package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.TarifProduit;
import com.adm.supervision.service.dto.ProduitDTO;
import com.adm.supervision.service.dto.TarifProduitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TarifProduit} and its DTO {@link TarifProduitDTO}.
 */
@Mapper(componentModel = "spring")
public interface TarifProduitMapper extends EntityMapper<TarifProduitDTO, TarifProduit> {
    @Mapping(target = "produit", source = "produit", qualifiedByName = "produitDesignation")
    TarifProduitDTO toDto(TarifProduit s);

    @Named("produitDesignation")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "designation", source = "designation")
    ProduitDTO toDtoProduitDesignation(Produit produit);
}
