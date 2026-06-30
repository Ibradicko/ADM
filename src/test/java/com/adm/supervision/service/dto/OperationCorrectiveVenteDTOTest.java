package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OperationCorrectiveVenteDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OperationCorrectiveVenteDTO.class);
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO1 = new OperationCorrectiveVenteDTO();
        operationCorrectiveVenteDTO1.setId(1L);
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO2 = new OperationCorrectiveVenteDTO();
        assertThat(operationCorrectiveVenteDTO1).isNotEqualTo(operationCorrectiveVenteDTO2);
        operationCorrectiveVenteDTO2.setId(operationCorrectiveVenteDTO1.getId());
        assertThat(operationCorrectiveVenteDTO1).isEqualTo(operationCorrectiveVenteDTO2);
        operationCorrectiveVenteDTO2.setId(2L);
        assertThat(operationCorrectiveVenteDTO1).isNotEqualTo(operationCorrectiveVenteDTO2);
        operationCorrectiveVenteDTO1.setId(null);
        assertThat(operationCorrectiveVenteDTO1).isNotEqualTo(operationCorrectiveVenteDTO2);
    }
}
