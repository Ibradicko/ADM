package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.PermissionMetier;
import com.adm.supervision.domain.ProfilMetier;
import com.adm.supervision.service.dto.PermissionMetierDTO;
import com.adm.supervision.service.dto.ProfilMetierDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PermissionMetier} and its DTO {@link PermissionMetierDTO}.
 */
@Mapper(componentModel = "spring")
public interface PermissionMetierMapper extends EntityMapper<PermissionMetierDTO, PermissionMetier> {
    @Mapping(target = "profilses", source = "profilses", qualifiedByName = "profilMetierCodeSet")
    PermissionMetierDTO toDto(PermissionMetier s);

    @Mapping(target = "profilses", ignore = true)
    @Mapping(target = "removeProfils", ignore = true)
    PermissionMetier toEntity(PermissionMetierDTO permissionMetierDTO);

    @Named("profilMetierCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    ProfilMetierDTO toDtoProfilMetierCode(ProfilMetier profilMetier);

    @Named("profilMetierCodeSet")
    default Set<ProfilMetierDTO> toDtoProfilMetierCodeSet(Set<ProfilMetier> profilMetier) {
        return profilMetier.stream().map(this::toDtoProfilMetierCode).collect(Collectors.toSet());
    }
}
