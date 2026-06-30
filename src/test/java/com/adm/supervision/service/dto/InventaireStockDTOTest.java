package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InventaireStockDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InventaireStockDTO.class);
        InventaireStockDTO inventaireStockDTO1 = new InventaireStockDTO();
        inventaireStockDTO1.setId(1L);
        InventaireStockDTO inventaireStockDTO2 = new InventaireStockDTO();
        assertThat(inventaireStockDTO1).isNotEqualTo(inventaireStockDTO2);
        inventaireStockDTO2.setId(inventaireStockDTO1.getId());
        assertThat(inventaireStockDTO1).isEqualTo(inventaireStockDTO2);
        inventaireStockDTO2.setId(2L);
        assertThat(inventaireStockDTO1).isNotEqualTo(inventaireStockDTO2);
        inventaireStockDTO1.setId(null);
        assertThat(inventaireStockDTO1).isNotEqualTo(inventaireStockDTO2);
    }
}
