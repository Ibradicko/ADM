package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.InventaireStock;
import com.adm.supervision.domain.LigneInventaireStock;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.service.dto.InventaireStockDTO;
import com.adm.supervision.service.dto.LigneInventaireStockDTO;
import com.adm.supervision.service.dto.ProduitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LigneInventaireStock} and its DTO {@link LigneInventaireStockDTO}.
 */
@Mapper(componentModel = "spring")
public interface LigneInventaireStockMapper extends EntityMapper<LigneInventaireStockDTO, LigneInventaireStock> {
    @Mapping(target = "inventaire", source = "inventaire", qualifiedByName = "inventaireStockReference")
    @Mapping(target = "produit", source = "produit", qualifiedByName = "produitDesignation")
    LigneInventaireStockDTO toDto(LigneInventaireStock s);

    @Named("inventaireStockReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    InventaireStockDTO toDtoInventaireStockReference(InventaireStock inventaireStock);

    @Named("produitDesignation")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "designation", source = "designation")
    ProduitDTO toDtoProduitDesignation(Produit produit);
}
