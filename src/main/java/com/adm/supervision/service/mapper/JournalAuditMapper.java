package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.JournalAudit;
import com.adm.supervision.domain.User;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.dto.JournalAuditDTO;
import com.adm.supervision.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link JournalAudit} and its DTO {@link JournalAuditDTO}.
 */
@Mapper(componentModel = "spring")
public interface JournalAuditMapper extends EntityMapper<JournalAuditDTO, JournalAudit> {
    @Mapping(target = "boutique", source = "boutique", qualifiedByName = "boutiqueNom")
    @Mapping(target = "utilisateur", source = "utilisateur", qualifiedByName = "userLogin")
    JournalAuditDTO toDto(JournalAudit s);

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
