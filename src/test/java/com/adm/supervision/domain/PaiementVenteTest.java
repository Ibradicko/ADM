package com.adm.supervision.domain;

import static com.adm.supervision.domain.ModePaiementRefTestSamples.*;
import static com.adm.supervision.domain.PaiementVenteTestSamples.*;
import static com.adm.supervision.domain.VenteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaiementVenteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaiementVente.class);
        PaiementVente paiementVente1 = getPaiementVenteSample1();
        PaiementVente paiementVente2 = new PaiementVente();
        assertThat(paiementVente1).isNotEqualTo(paiementVente2);

        paiementVente2.setId(paiementVente1.getId());
        assertThat(paiementVente1).isEqualTo(paiementVente2);

        paiementVente2 = getPaiementVenteSample2();
        assertThat(paiementVente1).isNotEqualTo(paiementVente2);
    }

    @Test
    void venteTest() {
        PaiementVente paiementVente = getPaiementVenteRandomSampleGenerator();
        Vente venteBack = getVenteRandomSampleGenerator();

        paiementVente.setVente(venteBack);
        assertThat(paiementVente.getVente()).isEqualTo(venteBack);

        paiementVente.vente(null);
        assertThat(paiementVente.getVente()).isNull();
    }

    @Test
    void modePaiementTest() {
        PaiementVente paiementVente = getPaiementVenteRandomSampleGenerator();
        ModePaiementRef modePaiementRefBack = getModePaiementRefRandomSampleGenerator();

        paiementVente.setModePaiement(modePaiementRefBack);
        assertThat(paiementVente.getModePaiement()).isEqualTo(modePaiementRefBack);

        paiementVente.modePaiement(null);
        assertThat(paiementVente.getModePaiement()).isNull();
    }
}
