package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HistoriqueCodeBarresDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HistoriqueCodeBarresDTO.class);
        HistoriqueCodeBarresDTO historiqueCodeBarresDTO1 = new HistoriqueCodeBarresDTO();
        historiqueCodeBarresDTO1.setId(1L);
        HistoriqueCodeBarresDTO historiqueCodeBarresDTO2 = new HistoriqueCodeBarresDTO();
        assertThat(historiqueCodeBarresDTO1).isNotEqualTo(historiqueCodeBarresDTO2);
        historiqueCodeBarresDTO2.setId(historiqueCodeBarresDTO1.getId());
        assertThat(historiqueCodeBarresDTO1).isEqualTo(historiqueCodeBarresDTO2);
        historiqueCodeBarresDTO2.setId(2L);
        assertThat(historiqueCodeBarresDTO1).isNotEqualTo(historiqueCodeBarresDTO2);
        historiqueCodeBarresDTO1.setId(null);
        assertThat(historiqueCodeBarresDTO1).isNotEqualTo(historiqueCodeBarresDTO2);
    }
}
