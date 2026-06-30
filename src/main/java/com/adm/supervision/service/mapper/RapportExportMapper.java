package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.domain.RapportExport;
import com.adm.supervision.domain.User;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.dto.LocataireDTO;
import com.adm.supervision.service.dto.RapportExportDTO;
import com.adm.supervision.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RapportExport} and its DTO {@link RapportExportDTO}.
 */
@Mapper(componentModel = "spring")
public interface RapportExportMapper extends EntityMapper<RapportExportDTO, RapportExport> {
    @Mapping(target = "boutique", source = "boutique", qualifiedByName = "boutiqueNom")
    @Mapping(target = "locataire", source = "locataire", qualifiedByName = "locataireNom")
    @Mapping(target = "utilisateur", source = "utilisateur", qualifiedByName = "userLogin")
    RapportExportDTO toDto(RapportExport s);

    @Named("boutiqueNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    BoutiqueDTO toDtoBoutiqueNom(Boutique boutique);

    @Named("locataireNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    LocataireDTO toDtoLocataireNom(Locataire locataire);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
