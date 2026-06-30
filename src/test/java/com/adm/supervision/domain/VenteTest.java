package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.LocataireTestSamples.*;
import static com.adm.supervision.domain.VenteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VenteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vente.class);
        Vente vente1 = getVenteSample1();
        Vente vente2 = new Vente();
        assertThat(vente1).isNotEqualTo(vente2);

        vente2.setId(vente1.getId());
        assertThat(vente1).isEqualTo(vente2);

        vente2 = getVenteSample2();
        assertThat(vente1).isNotEqualTo(vente2);
    }

    @Test
    void boutiqueTest() {
        Vente vente = getVenteRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        vente.setBoutique(boutiqueBack);
        assertThat(vente.getBoutique()).isEqualTo(boutiqueBack);

        vente.boutique(null);
        assertThat(vente.getBoutique()).isNull();
    }

    @Test
    void locataireTest() {
        Vente vente = getVenteRandomSampleGenerator();
        Locataire locataireBack = getLocataireRandomSampleGenerator();

        vente.setLocataire(locataireBack);
        assertThat(vente.getLocataire()).isEqualTo(locataireBack);

        vente.locataire(null);
        assertThat(vente.getLocataire()).isNull();
    }
}
