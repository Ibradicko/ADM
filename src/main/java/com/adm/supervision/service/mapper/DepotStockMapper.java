package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.DepotStock;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.dto.DepotStockDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DepotStock} and its DTO {@link DepotStockDTO}.
 */
@Mapper(componentModel = "spring")
public interface DepotStockMapper extends EntityMapper<DepotStockDTO, DepotStock> {
    @Mapping(target = "boutique", source = "boutique", qualifiedByName = "boutiqueNom")
    DepotStockDTO toDto(DepotStock s);

    @Named("boutiqueNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    BoutiqueDTO toDtoBoutiqueNom(Boutique boutique);
}
