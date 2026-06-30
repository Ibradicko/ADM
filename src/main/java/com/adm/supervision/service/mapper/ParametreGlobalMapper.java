package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.ParametreGlobal;
import com.adm.supervision.service.dto.ParametreGlobalDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ParametreGlobal} and its DTO {@link ParametreGlobalDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParametreGlobalMapper extends EntityMapper<ParametreGlobalDTO, ParametreGlobal> {}
