package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.LigneCalculRedevance;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.service.dto.CalculRedevanceDTO;
import com.adm.supervision.service.dto.LigneCalculRedevanceDTO;
import com.adm.supervision.service.dto.VenteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LigneCalculRedevance} and its DTO {@link LigneCalculRedevanceDTO}.
 */
@Mapper(componentModel = "spring")
public interface LigneCalculRedevanceMapper extends EntityMapper<LigneCalculRedevanceDTO, LigneCalculRedevance> {
    @Mapping(target = "calcul", source = "calcul", qualifiedByName = "calculRedevanceReference")
    @Mapping(target = "vente", source = "vente", qualifiedByName = "venteNumeroTicket")
    LigneCalculRedevanceDTO toDto(LigneCalculRedevance s);

    @Named("calculRedevanceReference")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "reference", source = "reference")
    CalculRedevanceDTO toDtoCalculRedevanceReference(CalculRedevance calculRedevance);

    @Named("venteNumeroTicket")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "numeroTicket", source = "numeroTicket")
    VenteDTO toDtoVenteNumeroTicket(Vente vente);
}
