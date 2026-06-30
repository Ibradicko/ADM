package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.ModePaiementRef;
import com.adm.supervision.domain.PaiementVente;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.service.dto.ModePaiementRefDTO;
import com.adm.supervision.service.dto.PaiementVenteDTO;
import com.adm.supervision.service.dto.VenteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PaiementVente} and its DTO {@link PaiementVenteDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaiementVenteMapper extends EntityMapper<PaiementVenteDTO, PaiementVente> {
    @Mapping(target = "vente", source = "vente", qualifiedByName = "venteNumeroTicket")
    @Mapping(target = "modePaiement", source = "modePaiement", qualifiedByName = "modePaiementRefLibelle")
    PaiementVenteDTO toDto(PaiementVente s);

    @Named("venteNumeroTicket")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "numeroTicket", source = "numeroTicket")
    VenteDTO toDtoVenteNumeroTicket(Vente vente);

    @Named("modePaiementRefLibelle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "libelle", source = "libelle")
    ModePaiementRefDTO toDtoModePaiementRefLibelle(ModePaiementRef modePaiementRef);
}
