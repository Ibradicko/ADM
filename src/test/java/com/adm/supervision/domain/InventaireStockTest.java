package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.DepotStockTestSamples.*;
import static com.adm.supervision.domain.InventaireStockTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InventaireStockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InventaireStock.class);
        InventaireStock inventaireStock1 = getInventaireStockSample1();
        InventaireStock inventaireStock2 = new InventaireStock();
        assertThat(inventaireStock1).isNotEqualTo(inventaireStock2);

        inventaireStock2.setId(inventaireStock1.getId());
        assertThat(inventaireStock1).isEqualTo(inventaireStock2);

        inventaireStock2 = getInventaireStockSample2();
        assertThat(inventaireStock1).isNotEqualTo(inventaireStock2);
    }

    @Test
    void boutiqueTest() {
        InventaireStock inventaireStock = getInventaireStockRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        inventaireStock.setBoutique(boutiqueBack);
        assertThat(inventaireStock.getBoutique()).isEqualTo(boutiqueBack);

        inventaireStock.boutique(null);
        assertThat(inventaireStock.getBoutique()).isNull();
    }

    @Test
    void depotTest() {
        InventaireStock inventaireStock = getInventaireStockRandomSampleGenerator();
        DepotStock depotStockBack = getDepotStockRandomSampleGenerator();

        inventaireStock.setDepot(depotStockBack);
        assertThat(inventaireStock.getDepot()).isEqualTo(depotStockBack);

        inventaireStock.depot(null);
        assertThat(inventaireStock.getDepot()).isNull();
    }
}
