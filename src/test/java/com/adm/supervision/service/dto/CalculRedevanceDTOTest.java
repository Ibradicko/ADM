package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CalculRedevanceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CalculRedevanceDTO.class);
        CalculRedevanceDTO calculRedevanceDTO1 = new CalculRedevanceDTO();
        calculRedevanceDTO1.setId(1L);
        CalculRedevanceDTO calculRedevanceDTO2 = new CalculRedevanceDTO();
        assertThat(calculRedevanceDTO1).isNotEqualTo(calculRedevanceDTO2);
        calculRedevanceDTO2.setId(calculRedevanceDTO1.getId());
        assertThat(calculRedevanceDTO1).isEqualTo(calculRedevanceDTO2);
        calculRedevanceDTO2.setId(2L);
        assertThat(calculRedevanceDTO1).isNotEqualTo(calculRedevanceDTO2);
        calculRedevanceDTO1.setId(null);
        assertThat(calculRedevanceDTO1).isNotEqualTo(calculRedevanceDTO2);
    }
}
