package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParametreCodeBarresDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParametreCodeBarresDTO.class);
        ParametreCodeBarresDTO parametreCodeBarresDTO1 = new ParametreCodeBarresDTO();
        parametreCodeBarresDTO1.setId(1L);
        ParametreCodeBarresDTO parametreCodeBarresDTO2 = new ParametreCodeBarresDTO();
        assertThat(parametreCodeBarresDTO1).isNotEqualTo(parametreCodeBarresDTO2);
        parametreCodeBarresDTO2.setId(parametreCodeBarresDTO1.getId());
        assertThat(parametreCodeBarresDTO1).isEqualTo(parametreCodeBarresDTO2);
        parametreCodeBarresDTO2.setId(2L);
        assertThat(parametreCodeBarresDTO1).isNotEqualTo(parametreCodeBarresDTO2);
        parametreCodeBarresDTO1.setId(null);
        assertThat(parametreCodeBarresDTO1).isNotEqualTo(parametreCodeBarresDTO2);
    }
}
