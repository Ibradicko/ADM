package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.PermissionMetier;
import com.adm.supervision.domain.ProfilMetier;
import com.adm.supervision.service.dto.PermissionMetierDTO;
import com.adm.supervision.service.dto.ProfilMetierDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProfilMetier} and its DTO {@link ProfilMetierDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProfilMetierMapper extends EntityMapper<ProfilMetierDTO, ProfilMetier> {
    @Mapping(target = "permissionses", source = "permissionses", qualifiedByName = "permissionMetierCodeSet")
    ProfilMetierDTO toDto(ProfilMetier s);

    @Mapping(target = "removePermissions", ignore = true)
    ProfilMetier toEntity(ProfilMetierDTO profilMetierDTO);

    @Named("permissionMetierCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    PermissionMetierDTO toDtoPermissionMetierCode(PermissionMetier permissionMetier);

    @Named("permissionMetierCodeSet")
    default Set<PermissionMetierDTO> toDtoPermissionMetierCodeSet(Set<PermissionMetier> permissionMetier) {
        return permissionMetier.stream().map(this::toDtoPermissionMetierCode).collect(Collectors.toSet());
    }
}
