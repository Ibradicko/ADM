package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LigneVenteDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneVenteDTO.class);
        LigneVenteDTO ligneVenteDTO1 = new LigneVenteDTO();
        ligneVenteDTO1.setId(1L);
        LigneVenteDTO ligneVenteDTO2 = new LigneVenteDTO();
        assertThat(ligneVenteDTO1).isNotEqualTo(ligneVenteDTO2);
        ligneVenteDTO2.setId(ligneVenteDTO1.getId());
        assertThat(ligneVenteDTO1).isEqualTo(ligneVenteDTO2);
        ligneVenteDTO2.setId(2L);
        assertThat(ligneVenteDTO1).isNotEqualTo(ligneVenteDTO2);
        ligneVenteDTO1.setId(null);
        assertThat(ligneVenteDTO1).isNotEqualTo(ligneVenteDTO2);
    }
}
