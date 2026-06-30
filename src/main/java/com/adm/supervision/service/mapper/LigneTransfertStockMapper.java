package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.LigneTransfertStock;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.TransfertStock;
import com.adm.supervision.service.dto.LigneTransfertStockDTO;
import com.adm.supervision.service.dto.ProduitDTO;
import com.adm.supervision.service.dto.TransfertStockDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LigneTransfertStock} and its DTO {@link LigneTransfertStockDTO}.
 */
@Mapper(componentModel = "spring")
public interface LigneTransfertStockMapper extends EntityMapper<LigneTransfertStockDTO, LigneTransfertStock> {
    @Mapping(target = "transfert", source = "transfert", qualifiedByName = "transfertStockReference")
    @Mapping(target = "produit", source = "produit", qualifiedByName = "produitDesignation")
    LigneTransfertStockDTO toDto(LigneTransfertStock s);

    @Named("transfertStockReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    TransfertStockDTO toDtoTransfertStockReference(TransfertStock transfertStock);

    @Named("produitDesignation")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "designation", source = "designation")
    ProduitDTO toDtoProduitDesignation(Produit produit);
}
