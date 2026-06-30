package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfilMetierDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfilMetierDTO.class);
        ProfilMetierDTO profilMetierDTO1 = new ProfilMetierDTO();
        profilMetierDTO1.setId(1L);
        ProfilMetierDTO profilMetierDTO2 = new ProfilMetierDTO();
        assertThat(profilMetierDTO1).isNotEqualTo(profilMetierDTO2);
        profilMetierDTO2.setId(profilMetierDTO1.getId());
        assertThat(profilMetierDTO1).isEqualTo(profilMetierDTO2);
        profilMetierDTO2.setId(2L);
        assertThat(profilMetierDTO1).isNotEqualTo(profilMetierDTO2);
        profilMetierDTO1.setId(null);
        assertThat(profilMetierDTO1).isNotEqualTo(profilMetierDTO2);
    }
}
