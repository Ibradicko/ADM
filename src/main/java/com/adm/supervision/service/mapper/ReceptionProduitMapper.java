package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.ReceptionProduit;
import com.adm.supervision.domain.User;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.dto.ReceptionProduitDTO;
import com.adm.supervision.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ReceptionProduit} and its DTO {@link ReceptionProduitDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReceptionProduitMapper extends EntityMapper<ReceptionProduitDTO, ReceptionProduit> {
    @Mapping(target = "boutique", source = "boutique", qualifiedByName = "boutiqueNom")
    @Mapping(target = "utilisateur", source = "utilisateur", qualifiedByName = "userLogin")
    ReceptionProduitDTO toDto(ReceptionProduit s);

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
