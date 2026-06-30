package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaiementRedevanceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaiementRedevanceDTO.class);
        PaiementRedevanceDTO paiementRedevanceDTO1 = new PaiementRedevanceDTO();
        paiementRedevanceDTO1.setId(1L);
        PaiementRedevanceDTO paiementRedevanceDTO2 = new PaiementRedevanceDTO();
        assertThat(paiementRedevanceDTO1).isNotEqualTo(paiementRedevanceDTO2);
        paiementRedevanceDTO2.setId(paiementRedevanceDTO1.getId());
        assertThat(paiementRedevanceDTO1).isEqualTo(paiementRedevanceDTO2);
        paiementRedevanceDTO2.setId(2L);
        assertThat(paiementRedevanceDTO1).isNotEqualTo(paiementRedevanceDTO2);
        paiementRedevanceDTO1.setId(null);
        assertThat(paiementRedevanceDTO1).isNotEqualTo(paiementRedevanceDTO2);
    }
}
