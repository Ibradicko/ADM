package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LigneInventaireStockDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneInventaireStockDTO.class);
        LigneInventaireStockDTO ligneInventaireStockDTO1 = new LigneInventaireStockDTO();
        ligneInventaireStockDTO1.setId(1L);
        LigneInventaireStockDTO ligneInventaireStockDTO2 = new LigneInventaireStockDTO();
        assertThat(ligneInventaireStockDTO1).isNotEqualTo(ligneInventaireStockDTO2);
        ligneInventaireStockDTO2.setId(ligneInventaireStockDTO1.getId());
        assertThat(ligneInventaireStockDTO1).isEqualTo(ligneInventaireStockDTO2);
        ligneInventaireStockDTO2.setId(2L);
        assertThat(ligneInventaireStockDTO1).isNotEqualTo(ligneInventaireStockDTO2);
        ligneInventaireStockDTO1.setId(null);
        assertThat(ligneInventaireStockDTO1).isNotEqualTo(ligneInventaireStockDTO2);
    }
}
