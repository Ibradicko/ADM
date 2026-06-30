package com.adm.supervision.domain;

import static com.adm.supervision.domain.LigneVenteTestSamples.*;
import static com.adm.supervision.domain.ProduitTestSamples.*;
import static com.adm.supervision.domain.VenteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LigneVenteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneVente.class);
        LigneVente ligneVente1 = getLigneVenteSample1();
        LigneVente ligneVente2 = new LigneVente();
        assertThat(ligneVente1).isNotEqualTo(ligneVente2);

        ligneVente2.setId(ligneVente1.getId());
        assertThat(ligneVente1).isEqualTo(ligneVente2);

        ligneVente2 = getLigneVenteSample2();
        assertThat(ligneVente1).isNotEqualTo(ligneVente2);
    }

    @Test
    void venteTest() {
        LigneVente ligneVente = getLigneVenteRandomSampleGenerator();
        Vente venteBack = getVenteRandomSampleGenerator();

        ligneVente.setVente(venteBack);
        assertThat(ligneVente.getVente()).isEqualTo(venteBack);

        ligneVente.vente(null);
        assertThat(ligneVente.getVente()).isNull();
    }

    @Test
    void produitTest() {
        LigneVente ligneVente = getLigneVenteRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        ligneVente.setProduit(produitBack);
        assertThat(ligneVente.getProduit()).isEqualTo(produitBack);

        ligneVente.produit(null);
        assertThat(ligneVente.getProduit()).isNull();
    }
}
