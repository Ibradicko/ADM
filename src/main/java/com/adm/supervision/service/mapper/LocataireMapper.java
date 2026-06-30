package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.Locataire;
import com.adm.supervision.service.dto.LocataireDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Locataire} and its DTO {@link LocataireDTO}.
 */
@Mapper(componentModel = "spring")
public interface LocataireMapper extends EntityMapper<LocataireDTO, Locataire> {
    @Mapping(target = "loginGenere", source = "user.login")
    LocataireDTO toDto(Locataire locataire);

    @Mapping(target = "user", ignore = true)
    Locataire toEntity(LocataireDTO locataireDTO);
}
