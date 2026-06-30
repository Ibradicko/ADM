package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.MouvementStockTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MouvementStockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MouvementStock.class);
        MouvementStock mouvementStock1 = getMouvementStockSample1();
        MouvementStock mouvementStock2 = new MouvementStock();
        assertThat(mouvementStock1).isNotEqualTo(mouvementStock2);

        mouvementStock2.setId(mouvementStock1.getId());
        assertThat(mouvementStock1).isEqualTo(mouvementStock2);

        mouvementStock2 = getMouvementStockSample2();
        assertThat(mouvementStock1).isNotEqualTo(mouvementStock2);
    }

    @Test
    void boutiqueTest() {
        MouvementStock mouvementStock = getMouvementStockRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        mouvementStock.setBoutique(boutiqueBack);
        assertThat(mouvementStock.getBoutique()).isEqualTo(boutiqueBack);

        mouvementStock.boutique(null);
        assertThat(mouvementStock.getBoutique()).isNull();
    }
}
