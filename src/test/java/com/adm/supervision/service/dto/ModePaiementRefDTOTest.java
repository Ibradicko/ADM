package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ModePaiementRefDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ModePaiementRefDTO.class);
        ModePaiementRefDTO modePaiementRefDTO1 = new ModePaiementRefDTO();
        modePaiementRefDTO1.setId(1L);
        ModePaiementRefDTO modePaiementRefDTO2 = new ModePaiementRefDTO();
        assertThat(modePaiementRefDTO1).isNotEqualTo(modePaiementRefDTO2);
        modePaiementRefDTO2.setId(modePaiementRefDTO1.getId());
        assertThat(modePaiementRefDTO1).isEqualTo(modePaiementRefDTO2);
        modePaiementRefDTO2.setId(2L);
        assertThat(modePaiementRefDTO1).isNotEqualTo(modePaiementRefDTO2);
        modePaiementRefDTO1.setId(null);
        assertThat(modePaiementRefDTO1).isNotEqualTo(modePaiementRefDTO2);
    }
}
