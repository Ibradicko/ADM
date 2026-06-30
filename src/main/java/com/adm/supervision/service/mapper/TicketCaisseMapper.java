package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.TicketCaisse;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.service.dto.TicketCaisseDTO;
import com.adm.supervision.service.dto.VenteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TicketCaisse} and its DTO {@link TicketCaisseDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketCaisseMapper extends EntityMapper<TicketCaisseDTO, TicketCaisse> {
    @Mapping(target = "vente", source = "vente", qualifiedByName = "venteNumeroTicket")
    TicketCaisseDTO toDto(TicketCaisse s);

    @Named("venteNumeroTicket")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "numeroTicket", source = "numeroTicket")
    VenteDTO toDtoVenteNumeroTicket(Vente vente);
}
