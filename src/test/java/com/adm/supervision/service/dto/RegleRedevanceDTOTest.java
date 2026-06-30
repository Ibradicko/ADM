package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RegleRedevanceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RegleRedevanceDTO.class);
        RegleRedevanceDTO regleRedevanceDTO1 = new RegleRedevanceDTO();
        regleRedevanceDTO1.setId(1L);
        RegleRedevanceDTO regleRedevanceDTO2 = new RegleRedevanceDTO();
        assertThat(regleRedevanceDTO1).isNotEqualTo(regleRedevanceDTO2);
        regleRedevanceDTO2.setId(regleRedevanceDTO1.getId());
        assertThat(regleRedevanceDTO1).isEqualTo(regleRedevanceDTO2);
        regleRedevanceDTO2.setId(2L);
        assertThat(regleRedevanceDTO1).isNotEqualTo(regleRedevanceDTO2);
        regleRedevanceDTO1.setId(null);
        assertThat(regleRedevanceDTO1).isNotEqualTo(regleRedevanceDTO2);
    }
}
