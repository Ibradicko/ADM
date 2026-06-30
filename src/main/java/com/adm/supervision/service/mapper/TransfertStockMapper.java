package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.TransfertStock;
import com.adm.supervision.domain.User;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.dto.TransfertStockDTO;
import com.adm.supervision.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TransfertStock} and its DTO {@link TransfertStockDTO}.
 */
@Mapper(componentModel = "spring")
public interface TransfertStockMapper extends EntityMapper<TransfertStockDTO, TransfertStock> {
    @Mapping(target = "boutiqueOrigine", source = "boutiqueOrigine", qualifiedByName = "boutiqueNom")
    @Mapping(target = "boutiqueDestination", source = "boutiqueDestination", qualifiedByName = "boutiqueNom")
    @Mapping(target = "utilisateur", source = "utilisateur", qualifiedByName = "userLogin")
    TransfertStockDTO toDto(TransfertStock s);

    @Named("boutiqueNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    BoutiqueDTO toDtoBoutiqueNom(Boutique boutique);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
