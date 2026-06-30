package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TarifProduitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TarifProduitDTO.class);
        TarifProduitDTO tarifProduitDTO1 = new TarifProduitDTO();
        tarifProduitDTO1.setId(1L);
        TarifProduitDTO tarifProduitDTO2 = new TarifProduitDTO();
        assertThat(tarifProduitDTO1).isNotEqualTo(tarifProduitDTO2);
        tarifProduitDTO2.setId(tarifProduitDTO1.getId());
        assertThat(tarifProduitDTO1).isEqualTo(tarifProduitDTO2);
        tarifProduitDTO2.setId(2L);
        assertThat(tarifProduitDTO1).isNotEqualTo(tarifProduitDTO2);
        tarifProduitDTO1.setId(null);
        assertThat(tarifProduitDTO1).isNotEqualTo(tarifProduitDTO2);
    }
}
