package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.AffectationUtilisateur;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.ProfilMetier;
import com.adm.supervision.domain.User;
import com.adm.supervision.service.dto.AffectationUtilisateurDTO;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.dto.ProfilMetierDTO;
import com.adm.supervision.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AffectationUtilisateur} and its DTO {@link AffectationUtilisateurDTO}.
 */
@Mapper(componentModel = "spring")
public interface AffectationUtilisateurMapper extends EntityMapper<AffectationUtilisateurDTO, AffectationUtilisateur> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "boutique", source = "boutique", qualifiedByName = "boutiqueNom")
    @Mapping(target = "profil", source = "profil", qualifiedByName = "profilMetierCode")
    AffectationUtilisateurDTO toDto(AffectationUtilisateur s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "email", source = "email")
    UserDTO toDtoUserLogin(User user);

    @Named("boutiqueNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    BoutiqueDTO toDtoBoutiqueNom(Boutique boutique);

    @Named("profilMetierCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    ProfilMetierDTO toDtoProfilMetierCode(ProfilMetier profilMetier);
}
