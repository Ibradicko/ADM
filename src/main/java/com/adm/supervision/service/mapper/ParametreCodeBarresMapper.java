package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.ParametreCodeBarres;
import com.adm.supervision.service.dto.ParametreCodeBarresDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ParametreCodeBarres} and its DTO {@link ParametreCodeBarresDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParametreCodeBarresMapper extends EntityMapper<ParametreCodeBarresDTO, ParametreCodeBarres> {}
