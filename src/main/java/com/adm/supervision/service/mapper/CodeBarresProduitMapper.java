package com.adm.supervision.service.mapper;

import com.adm.supervision.domain.CodeBarresProduit;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.service.dto.CodeBarresProduitDTO;
import com.adm.supervision.service.dto.ProduitDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CodeBarresProduit} and its DTO {@link CodeBarresProduitDTO}.
 */
@Mapper(componentModel = "spring")
public interface CodeBarresProduitMapper extends EntityMapper<CodeBarresProduitDTO, CodeBarresProduit> {
    @Mapping(target = "produit", source = "produit", qualifiedByName = "produitDesignation")
    CodeBarresProduitDTO toDto(CodeBarresProduit s);

    @Named("produitDesignation")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "designation", source = "designation")
    ProduitDTO toDtoProduitDesignation(Produit produit);
}
