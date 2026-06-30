package com.adm.supervision.domain;

import static com.adm.supervision.domain.HistoriqueCodeBarresTestSamples.*;
import static com.adm.supervision.domain.ProduitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HistoriqueCodeBarresTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HistoriqueCodeBarres.class);
        HistoriqueCodeBarres historiqueCodeBarres1 = getHistoriqueCodeBarresSample1();
        HistoriqueCodeBarres historiqueCodeBarres2 = new HistoriqueCodeBarres();
        assertThat(historiqueCodeBarres1).isNotEqualTo(historiqueCodeBarres2);

        historiqueCodeBarres2.setId(historiqueCodeBarres1.getId());
        assertThat(historiqueCodeBarres1).isEqualTo(historiqueCodeBarres2);

        historiqueCodeBarres2 = getHistoriqueCodeBarresSample2();
        assertThat(historiqueCodeBarres1).isNotEqualTo(historiqueCodeBarres2);
    }

    @Test
    void produitTest() {
        HistoriqueCodeBarres historiqueCodeBarres = getHistoriqueCodeBarresRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        historiqueCodeBarres.setProduit(produitBack);
        assertThat(historiqueCodeBarres.getProduit()).isEqualTo(produitBack);

        historiqueCodeBarres.produit(null);
        assertThat(historiqueCodeBarres.getProduit()).isNull();
    }
}
