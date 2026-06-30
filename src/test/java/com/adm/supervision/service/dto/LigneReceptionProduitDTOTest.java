package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LigneReceptionProduitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneReceptionProduitDTO.class);
        LigneReceptionProduitDTO ligneReceptionProduitDTO1 = new LigneReceptionProduitDTO();
        ligneReceptionProduitDTO1.setId(1L);
        LigneReceptionProduitDTO ligneReceptionProduitDTO2 = new LigneReceptionProduitDTO();
        assertThat(ligneReceptionProduitDTO1).isNotEqualTo(ligneReceptionProduitDTO2);
        ligneReceptionProduitDTO2.setId(ligneReceptionProduitDTO1.getId());
        assertThat(ligneReceptionProduitDTO1).isEqualTo(ligneReceptionProduitDTO2);
        ligneReceptionProduitDTO2.setId(2L);
        assertThat(ligneReceptionProduitDTO1).isNotEqualTo(ligneReceptionProduitDTO2);
        ligneReceptionProduitDTO1.setId(null);
        assertThat(ligneReceptionProduitDTO1).isNotEqualTo(ligneReceptionProduitDTO2);
    }
}
