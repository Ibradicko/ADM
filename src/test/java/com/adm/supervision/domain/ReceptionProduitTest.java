package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.ReceptionProduitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReceptionProduitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReceptionProduit.class);
        ReceptionProduit receptionProduit1 = getReceptionProduitSample1();
        ReceptionProduit receptionProduit2 = new ReceptionProduit();
        assertThat(receptionProduit1).isNotEqualTo(receptionProduit2);

        receptionProduit2.setId(receptionProduit1.getId());
        assertThat(receptionProduit1).isEqualTo(receptionProduit2);

        receptionProduit2 = getReceptionProduitSample2();
        assertThat(receptionProduit1).isNotEqualTo(receptionProduit2);
    }

    @Test
    void boutiqueTest() {
        ReceptionProduit receptionProduit = getReceptionProduitRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        receptionProduit.setBoutique(boutiqueBack);
        assertThat(receptionProduit.getBoutique()).isEqualTo(boutiqueBack);

        receptionProduit.boutique(null);
        assertThat(receptionProduit.getBoutique()).isNull();
    }
}
