package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LigneTransfertStockDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneTransfertStockDTO.class);
        LigneTransfertStockDTO ligneTransfertStockDTO1 = new LigneTransfertStockDTO();
        ligneTransfertStockDTO1.setId(1L);
        LigneTransfertStockDTO ligneTransfertStockDTO2 = new LigneTransfertStockDTO();
        assertThat(ligneTransfertStockDTO1).isNotEqualTo(ligneTransfertStockDTO2);
        ligneTransfertStockDTO2.setId(ligneTransfertStockDTO1.getId());
        assertThat(ligneTransfertStockDTO1).isEqualTo(ligneTransfertStockDTO2);
        ligneTransfertStockDTO2.setId(2L);
        assertThat(ligneTransfertStockDTO1).isNotEqualTo(ligneTransfertStockDTO2);
        ligneTransfertStockDTO1.setId(null);
        assertThat(ligneTransfertStockDTO1).isNotEqualTo(ligneTransfertStockDTO2);
    }
}
