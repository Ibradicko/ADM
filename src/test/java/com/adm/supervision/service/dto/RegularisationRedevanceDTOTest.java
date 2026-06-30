package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RegularisationRedevanceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RegularisationRedevanceDTO.class);
        RegularisationRedevanceDTO regularisationRedevanceDTO1 = new RegularisationRedevanceDTO();
        regularisationRedevanceDTO1.setId(1L);
        RegularisationRedevanceDTO regularisationRedevanceDTO2 = new RegularisationRedevanceDTO();
        assertThat(regularisationRedevanceDTO1).isNotEqualTo(regularisationRedevanceDTO2);
        regularisationRedevanceDTO2.setId(regularisationRedevanceDTO1.getId());
        assertThat(regularisationRedevanceDTO1).isEqualTo(regularisationRedevanceDTO2);
        regularisationRedevanceDTO2.setId(2L);
        assertThat(regularisationRedevanceDTO1).isNotEqualTo(regularisationRedevanceDTO2);
        regularisationRedevanceDTO1.setId(null);
        assertThat(regularisationRedevanceDTO1).isNotEqualTo(regularisationRedevanceDTO2);
    }
}
