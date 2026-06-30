package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.DepotStock;
import com.adm.supervision.domain.LigneMouvementStock;
import com.adm.supervision.domain.MouvementStock;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.service.dto.DepotStockDTO;
import com.adm.supervision.service.dto.LigneMouvementStockDTO;
import com.adm.supervision.service.dto.MouvementStockDTO;
import com.adm.supervision.service.dto.ProduitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LigneMouvementStock} and its DTO {@link LigneMouvementStockDTO}.
 */
@Mapper(componentModel = "spring")
public interface LigneMouvementStockMapper extends EntityMapper<LigneMouvementStockDTO, LigneMouvementStock> {
    @Mapping(target = "mouvement", source = "mouvement", qualifiedByName = "mouvementStockReference")
    @Mapping(target = "produit", source = "produit", qualifiedByName = "produitDesignation")
    @Mapping(target = "depot", source = "depot", qualifiedByName = "depotStockCode")
    LigneMouvementStockDTO toDto(LigneMouvementStock s);

    @Named("mouvementStockReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    MouvementStockDTO toDtoMouvementStockReference(MouvementStock mouvementStock);

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
