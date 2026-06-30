package com.adm.supervision.domain;

import static com.adm.supervision.domain.OperationCorrectiveVenteTestSamples.*;
import static com.adm.supervision.domain.VenteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OperationCorrectiveVenteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OperationCorrectiveVente.class);
        OperationCorrectiveVente operationCorrectiveVente1 = getOperationCorrectiveVenteSample1();
        OperationCorrectiveVente operationCorrectiveVente2 = new OperationCorrectiveVente();
        assertThat(operationCorrectiveVente1).isNotEqualTo(operationCorrectiveVente2);

        operationCorrectiveVente2.setId(operationCorrectiveVente1.getId());
        assertThat(operationCorrectiveVente1).isEqualTo(operationCorrectiveVente2);

        operationCorrectiveVente2 = getOperationCorrectiveVenteSample2();
        assertThat(operationCorrectiveVente1).isNotEqualTo(operationCorrectiveVente2);
    }

    @Test
    void venteTest() {
        OperationCorrectiveVente operationCorrectiveVente = getOperationCorrectiveVenteRandomSampleGenerator();
        Vente venteBack = getVenteRandomSampleGenerator();

        operationCorrectiveVente.setVente(venteBack);
        assertThat(operationCorrectiveVente.getVente()).isEqualTo(venteBack);

        operationCorrectiveVente.vente(null);
        assertThat(operationCorrectiveVente.getVente()).isNull();
    }
}
