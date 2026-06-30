package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.DepotStock;
import com.adm.supervision.domain.InventaireStock;
import com.adm.supervision.domain.User;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.dto.DepotStockDTO;
import com.adm.supervision.service.dto.InventaireStockDTO;
import com.adm.supervision.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InventaireStock} and its DTO {@link InventaireStockDTO}.
 */
@Mapper(componentModel = "spring")
public interface InventaireStockMapper extends EntityMapper<InventaireStockDTO, InventaireStock> {
    @Mapping(target = "boutique", source = "boutique", qualifiedByName = "boutiqueNom")
    @Mapping(target = "depot", source = "depot", qualifiedByName = "depotStockCode")
    @Mapping(target = "utilisateur", source = "utilisateur", qualifiedByName = "userLogin")
    InventaireStockDTO toDto(InventaireStock s);

    @Named("boutiqueNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    BoutiqueDTO toDtoBoutiqueNom(Boutique boutique);

    @Named("depotStockCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    DepotStockDTO toDtoDepotStockCode(DepotStock depotStock);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
