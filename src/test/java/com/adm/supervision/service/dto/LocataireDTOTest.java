package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LocataireDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LocataireDTO.class);
        LocataireDTO locataireDTO1 = new LocataireDTO();
        locataireDTO1.setId(1L);
        LocataireDTO locataireDTO2 = new LocataireDTO();
        assertThat(locataireDTO1).isNotEqualTo(locataireDTO2);
        locataireDTO2.setId(locataireDTO1.getId());
        assertThat(locataireDTO1).isEqualTo(locataireDTO2);
        locataireDTO2.setId(2L);
        assertThat(locataireDTO1).isNotEqualTo(locataireDTO2);
        locataireDTO1.setId(null);
        assertThat(locataireDTO1).isNotEqualTo(locataireDTO2);
    }
}
