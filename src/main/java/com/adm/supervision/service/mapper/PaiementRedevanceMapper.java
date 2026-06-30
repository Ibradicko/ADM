package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.PaiementRedevance;
import com.adm.supervision.service.dto.CalculRedevanceDTO;
import com.adm.supervision.service.dto.PaiementRedevanceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PaiementRedevance} and its DTO {@link PaiementRedevanceDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaiementRedevanceMapper extends EntityMapper<PaiementRedevanceDTO, PaiementRedevance> {
    @Mapping(target = "calcul", source = "calcul", qualifiedByName = "calculRedevanceReference")
    PaiementRedevanceDTO toDto(PaiementRedevance s);

    @Named("calculRedevanceReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    CalculRedevanceDTO toDtoCalculRedevanceReference(CalculRedevance calculRedevance);
}
