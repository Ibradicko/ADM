package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.dto.CalculRedevanceDTO;
import com.adm.supervision.service.dto.LocataireDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CalculRedevance} and its DTO {@link CalculRedevanceDTO}.
 */
@Mapper(componentModel = "spring")
public interface CalculRedevanceMapper extends EntityMapper<CalculRedevanceDTO, CalculRedevance> {
    @Mapping(target = "boutique", source = "boutique", qualifiedByName = "boutiqueNom")
    @Mapping(target = "locataire", source = "locataire", qualifiedByName = "locataireNom")
    CalculRedevanceDTO toDto(CalculRedevance s);

    @Named("boutiqueNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    BoutiqueDTO toDtoBoutiqueNom(Boutique boutique);

    @Named("locataireNom")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nom", source = "nom")
    LocataireDTO toDtoLocataireNom(Locataire locataire);
}
