package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.ModePaiementRef;
import com.adm.supervision.service.dto.ModePaiementRefDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ModePaiementRef} and its DTO {@link ModePaiementRefDTO}.
 */
@Mapper(componentModel = "spring")
public interface ModePaiementRefMapper extends EntityMapper<ModePaiementRefDTO, ModePaiementRef> {}
