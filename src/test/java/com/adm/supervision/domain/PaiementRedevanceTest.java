package com.adm.supervision.domain;

import static com.adm.supervision.domain.CalculRedevanceTestSamples.*;
import static com.adm.supervision.domain.PaiementRedevanceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaiementRedevanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaiementRedevance.class);
        PaiementRedevance paiementRedevance1 = getPaiementRedevanceSample1();
        PaiementRedevance paiementRedevance2 = new PaiementRedevance();
        assertThat(paiementRedevance1).isNotEqualTo(paiementRedevance2);

        paiementRedevance2.setId(paiementRedevance1.getId());
        assertThat(paiementRedevance1).isEqualTo(paiementRedevance2);

        paiementRedevance2 = getPaiementRedevanceSample2();
        assertThat(paiementRedevance1).isNotEqualTo(paiementRedevance2);
    }

    @Test
    void calculTest() {
        PaiementRedevance paiementRedevance = getPaiementRedevanceRandomSampleGenerator();
        CalculRedevance calculRedevanceBack = getCalculRedevanceRandomSampleGenerator();

        paiementRedevance.setCalcul(calculRedevanceBack);
        assertThat(paiementRedevance.getCalcul()).isEqualTo(calculRedevanceBack);

        paiementRedevance.calcul(null);
        assertThat(paiementRedevance.getCalcul()).isNull();
    }
}
