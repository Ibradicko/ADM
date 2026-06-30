package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.RegularisationRedevance;
import com.adm.supervision.service.dto.CalculRedevanceDTO;
import com.adm.supervision.service.dto.RegularisationRedevanceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RegularisationRedevance} and its DTO {@link RegularisationRedevanceDTO}.
 */
@Mapper(componentModel = "spring")
public interface RegularisationRedevanceMapper extends EntityMapper<RegularisationRedevanceDTO, RegularisationRedevance> {
    @Mapping(target = "calcul", source = "calcul", qualifiedByName = "calculRedevanceReference")
    RegularisationRedevanceDTO toDto(RegularisationRedevance s);

    @Named("calculRedevanceReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    CalculRedevanceDTO toDtoCalculRedevanceReference(CalculRedevance calculRedevance);
}
