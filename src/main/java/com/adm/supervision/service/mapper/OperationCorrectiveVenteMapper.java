package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.OperationCorrectiveVente;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.service.dto.OperationCorrectiveVenteDTO;
import com.adm.supervision.service.dto.UserDTO;
import com.adm.supervision.service.dto.VenteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OperationCorrectiveVente} and its DTO {@link OperationCorrectiveVenteDTO}.
 */
@Mapper(componentModel = "spring")
public interface OperationCorrectiveVenteMapper extends EntityMapper<OperationCorrectiveVenteDTO, OperationCorrectiveVente> {
    @Mapping(target = "vente", source = "vente", qualifiedByName = "venteNumeroTicket")
    @Mapping(target = "utilisateur", source = "utilisateur", qualifiedByName = "userLogin")
    OperationCorrectiveVenteDTO toDto(OperationCorrectiveVente s);

    @Named("venteNumeroTicket")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "numeroTicket", source = "numeroTicket")
    VenteDTO toDtoVenteNumeroTicket(Vente vente);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
