package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LigneMouvementStockDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneMouvementStockDTO.class);
        LigneMouvementStockDTO ligneMouvementStockDTO1 = new LigneMouvementStockDTO();
        ligneMouvementStockDTO1.setId(1L);
        LigneMouvementStockDTO ligneMouvementStockDTO2 = new LigneMouvementStockDTO();
        assertThat(ligneMouvementStockDTO1).isNotEqualTo(ligneMouvementStockDTO2);
        ligneMouvementStockDTO2.setId(ligneMouvementStockDTO1.getId());
        assertThat(ligneMouvementStockDTO1).isEqualTo(ligneMouvementStockDTO2);
        ligneMouvementStockDTO2.setId(2L);
        assertThat(ligneMouvementStockDTO1).isNotEqualTo(ligneMouvementStockDTO2);
        ligneMouvementStockDTO1.setId(null);
        assertThat(ligneMouvementStockDTO1).isNotEqualTo(ligneMouvementStockDTO2);
    }
}
