package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.service.dto.BoutiqueDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Boutique} and its DTO {@link BoutiqueDTO}.
 */
@Mapper(componentModel = "spring")
public interface BoutiqueMapper extends EntityMapper<BoutiqueDTO, Boutique> {}
