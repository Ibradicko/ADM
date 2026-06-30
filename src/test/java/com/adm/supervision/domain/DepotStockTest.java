package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.DepotStockTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DepotStockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DepotStock.class);
        DepotStock depotStock1 = getDepotStockSample1();
        DepotStock depotStock2 = new DepotStock();
        assertThat(depotStock1).isNotEqualTo(depotStock2);

        depotStock2.setId(depotStock1.getId());
        assertThat(depotStock1).isEqualTo(depotStock2);

        depotStock2 = getDepotStockSample2();
        assertThat(depotStock1).isNotEqualTo(depotStock2);
    }

    @Test
    void boutiqueTest() {
        DepotStock depotStock = getDepotStockRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        depotStock.setBoutique(boutiqueBack);
        assertThat(depotStock.getBoutique()).isEqualTo(boutiqueBack);

        depotStock.boutique(null);
        assertThat(depotStock.getBoutique()).isNull();
    }
}
