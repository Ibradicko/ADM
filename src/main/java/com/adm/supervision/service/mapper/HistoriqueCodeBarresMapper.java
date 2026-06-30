package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.HistoriqueCodeBarres;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.User;
import com.adm.supervision.service.dto.HistoriqueCodeBarresDTO;
import com.adm.supervision.service.dto.ProduitDTO;
import com.adm.supervision.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link HistoriqueCodeBarres} and its DTO {@link HistoriqueCodeBarresDTO}.
 */
@Mapper(componentModel = "spring")
public interface HistoriqueCodeBarresMapper extends EntityMapper<HistoriqueCodeBarresDTO, HistoriqueCodeBarres> {
    @Mapping(target = "produit", source = "produit", qualifiedByName = "produitDesignation")
    @Mapping(target = "utilisateur", source = "utilisateur", qualifiedByName = "userLogin")
    HistoriqueCodeBarresDTO toDto(HistoriqueCodeBarres s);

    @Named("produitDesignation")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "designation", source = "designation")
    ProduitDTO toDtoProduitDesignation(Produit produit);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
