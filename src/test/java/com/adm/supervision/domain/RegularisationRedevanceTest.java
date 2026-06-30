package com.adm.supervision.domain;

import static com.adm.supervision.domain.CalculRedevanceTestSamples.*;
import static com.adm.supervision.domain.RegularisationRedevanceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RegularisationRedevanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RegularisationRedevance.class);
        RegularisationRedevance regularisationRedevance1 = getRegularisationRedevanceSample1();
        RegularisationRedevance regularisationRedevance2 = new RegularisationRedevance();
        assertThat(regularisationRedevance1).isNotEqualTo(regularisationRedevance2);

        regularisationRedevance2.setId(regularisationRedevance1.getId());
        assertThat(regularisationRedevance1).isEqualTo(regularisationRedevance2);

        regularisationRedevance2 = getRegularisationRedevanceSample2();
        assertThat(regularisationRedevance1).isNotEqualTo(regularisationRedevance2);
    }

    @Test
    void calculTest() {
        RegularisationRedevance regularisationRedevance = getRegularisationRedevanceRandomSampleGenerator();
        CalculRedevance calculRedevanceBack = getCalculRedevanceRandomSampleGenerator();

        regularisationRedevance.setCalcul(calculRedevanceBack);
        assertThat(regularisationRedevance.getCalcul()).isEqualTo(calculRedevanceBack);

        regularisationRedevance.calcul(null);
        assertThat(regularisationRedevance.getCalcul()).isNull();
    }
}
