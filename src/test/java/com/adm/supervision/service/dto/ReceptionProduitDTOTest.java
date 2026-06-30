package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReceptionProduitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReceptionProduitDTO.class);
        ReceptionProduitDTO receptionProduitDTO1 = new ReceptionProduitDTO();
        receptionProduitDTO1.setId(1L);
        ReceptionProduitDTO receptionProduitDTO2 = new ReceptionProduitDTO();
        assertThat(receptionProduitDTO1).isNotEqualTo(receptionProduitDTO2);
        receptionProduitDTO2.setId(receptionProduitDTO1.getId());
        assertThat(receptionProduitDTO1).isEqualTo(receptionProduitDTO2);
        receptionProduitDTO2.setId(2L);
        assertThat(receptionProduitDTO1).isNotEqualTo(receptionProduitDTO2);
        receptionProduitDTO1.setId(null);
        assertThat(receptionProduitDTO1).isNotEqualTo(receptionProduitDTO2);
    }
}
