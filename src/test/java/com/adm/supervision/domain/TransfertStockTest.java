package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.TransfertStockTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransfertStockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransfertStock.class);
        TransfertStock transfertStock1 = getTransfertStockSample1();
        TransfertStock transfertStock2 = new TransfertStock();
        assertThat(transfertStock1).isNotEqualTo(transfertStock2);

        transfertStock2.setId(transfertStock1.getId());
        assertThat(transfertStock1).isEqualTo(transfertStock2);

        transfertStock2 = getTransfertStockSample2();
        assertThat(transfertStock1).isNotEqualTo(transfertStock2);
    }

    @Test
    void boutiqueOrigineTest() {
        TransfertStock transfertStock = getTransfertStockRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        transfertStock.setBoutiqueOrigine(boutiqueBack);
        assertThat(transfertStock.getBoutiqueOrigine()).isEqualTo(boutiqueBack);

        transfertStock.boutiqueOrigine(null);
        assertThat(transfertStock.getBoutiqueOrigine()).isNull();
    }

    @Test
    void boutiqueDestinationTest() {
        TransfertStock transfertStock = getTransfertStockRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        transfertStock.setBoutiqueDestination(boutiqueBack);
        assertThat(transfertStock.getBoutiqueDestination()).isEqualTo(boutiqueBack);

        transfertStock.boutiqueDestination(null);
        assertThat(transfertStock.getBoutiqueDestination()).isNull();
    }
}
