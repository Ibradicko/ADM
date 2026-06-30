package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockProduitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockProduitDTO.class);
        StockProduitDTO stockProduitDTO1 = new StockProduitDTO();
        stockProduitDTO1.setId(1L);
        StockProduitDTO stockProduitDTO2 = new StockProduitDTO();
        assertThat(stockProduitDTO1).isNotEqualTo(stockProduitDTO2);
        stockProduitDTO2.setId(stockProduitDTO1.getId());
        assertThat(stockProduitDTO1).isEqualTo(stockProduitDTO2);
        stockProduitDTO2.setId(2L);
        assertThat(stockProduitDTO1).isNotEqualTo(stockProduitDTO2);
        stockProduitDTO1.setId(null);
        assertThat(stockProduitDTO1).isNotEqualTo(stockProduitDTO2);
    }
}
