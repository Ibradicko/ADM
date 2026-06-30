package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RapportExportDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RapportExportDTO.class);
        RapportExportDTO rapportExportDTO1 = new RapportExportDTO();
        rapportExportDTO1.setId(1L);
        RapportExportDTO rapportExportDTO2 = new RapportExportDTO();
        assertThat(rapportExportDTO1).isNotEqualTo(rapportExportDTO2);
        rapportExportDTO2.setId(rapportExportDTO1.getId());
        assertThat(rapportExportDTO1).isEqualTo(rapportExportDTO2);
        rapportExportDTO2.setId(2L);
        assertThat(rapportExportDTO1).isNotEqualTo(rapportExportDTO2);
        rapportExportDTO1.setId(null);
        assertThat(rapportExportDTO1).isNotEqualTo(rapportExportDTO2);
    }
}
