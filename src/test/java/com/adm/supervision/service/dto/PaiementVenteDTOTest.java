package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaiementVenteDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaiementVenteDTO.class);
        PaiementVenteDTO paiementVenteDTO1 = new PaiementVenteDTO();
        paiementVenteDTO1.setId(1L);
        PaiementVenteDTO paiementVenteDTO2 = new PaiementVenteDTO();
        assertThat(paiementVenteDTO1).isNotEqualTo(paiementVenteDTO2);
        paiementVenteDTO2.setId(paiementVenteDTO1.getId());
        assertThat(paiementVenteDTO1).isEqualTo(paiementVenteDTO2);
        paiementVenteDTO2.setId(2L);
        assertThat(paiementVenteDTO1).isNotEqualTo(paiementVenteDTO2);
        paiementVenteDTO1.setId(null);
        assertThat(paiementVenteDTO1).isNotEqualTo(paiementVenteDTO2);
    }
}
