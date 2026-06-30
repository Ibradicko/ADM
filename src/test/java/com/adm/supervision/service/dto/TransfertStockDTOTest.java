package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransfertStockDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransfertStockDTO.class);
        TransfertStockDTO transfertStockDTO1 = new TransfertStockDTO();
        transfertStockDTO1.setId(1L);
        TransfertStockDTO transfertStockDTO2 = new TransfertStockDTO();
        assertThat(transfertStockDTO1).isNotEqualTo(transfertStockDTO2);
        transfertStockDTO2.setId(transfertStockDTO1.getId());
        assertThat(transfertStockDTO1).isEqualTo(transfertStockDTO2);
        transfertStockDTO2.setId(2L);
        assertThat(transfertStockDTO1).isNotEqualTo(transfertStockDTO2);
        transfertStockDTO1.setId(null);
        assertThat(transfertStockDTO1).isNotEqualTo(transfertStockDTO2);
    }
}
