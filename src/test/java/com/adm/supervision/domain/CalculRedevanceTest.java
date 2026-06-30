package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.CalculRedevanceTestSamples.*;
import static com.adm.supervision.domain.LocataireTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CalculRedevanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CalculRedevance.class);
        CalculRedevance calculRedevance1 = getCalculRedevanceSample1();
        CalculRedevance calculRedevance2 = new CalculRedevance();
        assertThat(calculRedevance1).isNotEqualTo(calculRedevance2);

        calculRedevance2.setId(calculRedevance1.getId());
        assertThat(calculRedevance1).isEqualTo(calculRedevance2);

        calculRedevance2 = getCalculRedevanceSample2();
        assertThat(calculRedevance1).isNotEqualTo(calculRedevance2);
    }

    @Test
    void boutiqueTest() {
        CalculRedevance calculRedevance = getCalculRedevanceRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        calculRedevance.setBoutique(boutiqueBack);
        assertThat(calculRedevance.getBoutique()).isEqualTo(boutiqueBack);

        calculRedevance.boutique(null);
        assertThat(calculRedevance.getBoutique()).isNull();
    }

    @Test
    void locataireTest() {
        CalculRedevance calculRedevance = getCalculRedevanceRandomSampleGenerator();
        Locataire locataireBack = getLocataireRandomSampleGenerator();

        calculRedevance.setLocataire(locataireBack);
        assertThat(calculRedevance.getLocataire()).isEqualTo(locataireBack);

        calculRedevance.locataire(null);
        assertThat(calculRedevance.getLocataire()).isNull();
    }
}
