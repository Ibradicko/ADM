package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParametreGlobalDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParametreGlobalDTO.class);
        ParametreGlobalDTO parametreGlobalDTO1 = new ParametreGlobalDTO();
        parametreGlobalDTO1.setId(1L);
        ParametreGlobalDTO parametreGlobalDTO2 = new ParametreGlobalDTO();
        assertThat(parametreGlobalDTO1).isNotEqualTo(parametreGlobalDTO2);
        parametreGlobalDTO2.setId(parametreGlobalDTO1.getId());
        assertThat(parametreGlobalDTO1).isEqualTo(parametreGlobalDTO2);
        parametreGlobalDTO2.setId(2L);
        assertThat(parametreGlobalDTO1).isNotEqualTo(parametreGlobalDTO2);
        parametreGlobalDTO1.setId(null);
        assertThat(parametreGlobalDTO1).isNotEqualTo(parametreGlobalDTO2);
    }
}
