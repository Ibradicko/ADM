package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BoutiqueDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BoutiqueDTO.class);
        BoutiqueDTO boutiqueDTO1 = new BoutiqueDTO();
        boutiqueDTO1.setId(1L);
        BoutiqueDTO boutiqueDTO2 = new BoutiqueDTO();
        assertThat(boutiqueDTO1).isNotEqualTo(boutiqueDTO2);
        boutiqueDTO2.setId(boutiqueDTO1.getId());
        assertThat(boutiqueDTO1).isEqualTo(boutiqueDTO2);
        boutiqueDTO2.setId(2L);
        assertThat(boutiqueDTO1).isNotEqualTo(boutiqueDTO2);
        boutiqueDTO1.setId(null);
        assertThat(boutiqueDTO1).isNotEqualTo(boutiqueDTO2);
    }
}
