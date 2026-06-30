package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.DepotStock;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.StockProduit;
import com.adm.supervision.service.dto.DepotStockDTO;
import com.adm.supervision.service.dto.ProduitDTO;
import com.adm.supervision.service.dto.StockProduitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockProduit} and its DTO {@link StockProduitDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockProduitMapper extends EntityMapper<StockProduitDTO, StockProduit> {
    @Mapping(target = "produit", source = "produit", qualifiedByName = "produitDesignation")
    @Mapping(target = "depot", source = "depot", qualifiedByName = "depotStockCode")
    StockProduitDTO toDto(StockProduit s);

    @Named("produitDesignation")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "designation", source = "designation")
    ProduitDTO toDtoProduitDesignation(Produit produit);

    @Named("depotStockCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    DepotStockDTO toDtoDepotStockCode(DepotStock depotStock);
}
