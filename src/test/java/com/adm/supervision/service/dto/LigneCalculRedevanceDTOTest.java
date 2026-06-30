package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LigneCalculRedevanceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneCalculRedevanceDTO.class);
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO1 = new LigneCalculRedevanceDTO();
        ligneCalculRedevanceDTO1.setId(1L);
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO2 = new LigneCalculRedevanceDTO();
        assertThat(ligneCalculRedevanceDTO1).isNotEqualTo(ligneCalculRedevanceDTO2);
        ligneCalculRedevanceDTO2.setId(ligneCalculRedevanceDTO1.getId());
        assertThat(ligneCalculRedevanceDTO1).isEqualTo(ligneCalculRedevanceDTO2);
        ligneCalculRedevanceDTO2.setId(2L);
        assertThat(ligneCalculRedevanceDTO1).isNotEqualTo(ligneCalculRedevanceDTO2);
        ligneCalculRedevanceDTO1.setId(null);
        assertThat(ligneCalculRedevanceDTO1).isNotEqualTo(ligneCalculRedevanceDTO2);
    }
}
