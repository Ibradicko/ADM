package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.LigneVente;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.service.dto.LigneVenteDTO;
import com.adm.supervision.service.dto.ProduitDTO;
import com.adm.supervision.service.dto.VenteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LigneVente} and its DTO {@link LigneVenteDTO}.
 */
@Mapper(componentModel = "spring")
public interface LigneVenteMapper extends EntityMapper<LigneVenteDTO, LigneVente> {
    @Mapping(target = "vente", source = "vente", qualifiedByName = "venteNumeroTicket")
    @Mapping(target = "produit", source = "produit", qualifiedByName = "produitDesignation")
    LigneVenteDTO toDto(LigneVente s);

    @Named("venteNumeroTicket")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "numeroTicket", source = "numeroTicket")
    VenteDTO toDtoVenteNumeroTicket(Vente vente);

    @Named("produitDesignation")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "designation", source = "designation")
    ProduitDTO toDtoProduitDesignation(Produit produit);
}
