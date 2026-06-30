package com.adm.supervision.domain;

import static com.adm.supervision.domain.CalculRedevanceTestSamples.*;
import static com.adm.supervision.domain.LigneCalculRedevanceTestSamples.*;
import static com.adm.supervision.domain.VenteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LigneCalculRedevanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneCalculRedevance.class);
        LigneCalculRedevance ligneCalculRedevance1 = getLigneCalculRedevanceSample1();
        LigneCalculRedevance ligneCalculRedevance2 = new LigneCalculRedevance();
        assertThat(ligneCalculRedevance1).isNotEqualTo(ligneCalculRedevance2);

        ligneCalculRedevance2.setId(ligneCalculRedevance1.getId());
        assertThat(ligneCalculRedevance1).isEqualTo(ligneCalculRedevance2);

        ligneCalculRedevance2 = getLigneCalculRedevanceSample2();
        assertThat(ligneCalculRedevance1).isNotEqualTo(ligneCalculRedevance2);
    }

    @Test
    void calculTest() {
        LigneCalculRedevance ligneCalculRedevance = getLigneCalculRedevanceRandomSampleGenerator();
        CalculRedevance calculRedevanceBack = getCalculRedevanceRandomSampleGenerator();

        ligneCalculRedevance.setCalcul(calculRedevanceBack);
        assertThat(ligneCalculRedevance.getCalcul()).isEqualTo(calculRedevanceBack);

        ligneCalculRedevance.calcul(null);
        assertThat(ligneCalculRedevance.getCalcul()).isNull();
    }

    @Test
    void venteTest() {
        LigneCalculRedevance ligneCalculRedevance = getLigneCalculRedevanceRandomSampleGenerator();
        Vente venteBack = getVenteRandomSampleGenerator();

        ligneCalculRedevance.setVente(venteBack);
        assertThat(ligneCalculRedevance.getVente()).isEqualTo(venteBack);

        ligneCalculRedevance.vente(null);
        assertThat(ligneCalculRedevance.getVente()).isNull();
    }
}
