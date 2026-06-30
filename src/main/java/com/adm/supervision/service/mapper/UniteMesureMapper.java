package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.UniteMesure;
import com.adm.supervision.service.dto.UniteMesureDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UniteMesure} and its DTO {@link UniteMesureDTO}.
 */
@Mapper(componentModel = "spring")
public interface UniteMesureMapper extends EntityMapper<UniteMesureDTO, UniteMesure> {}
