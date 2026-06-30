package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.LotEtiquettes;
import com.adm.supervision.service.dto.LotEtiquettesDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LotEtiquettes} and its DTO {@link LotEtiquettesDTO}.
 */
@Mapper(componentModel = "spring")
public interface LotEtiquettesMapper extends EntityMapper<LotEtiquettesDTO, LotEtiquettes> {}
