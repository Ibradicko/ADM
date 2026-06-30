package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DepotStockDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DepotStockDTO.class);
        DepotStockDTO depotStockDTO1 = new DepotStockDTO();
        depotStockDTO1.setId(1L);
        DepotStockDTO depotStockDTO2 = new DepotStockDTO();
        assertThat(depotStockDTO1).isNotEqualTo(depotStockDTO2);
        depotStockDTO2.setId(depotStockDTO1.getId());
        assertThat(depotStockDTO1).isEqualTo(depotStockDTO2);
        depotStockDTO2.setId(2L);
        assertThat(depotStockDTO1).isNotEqualTo(depotStockDTO2);
        depotStockDTO1.setId(null);
        assertThat(depotStockDTO1).isNotEqualTo(depotStockDTO2);
    }
}
