package com.adm.supervision.domain;

import static com.adm.supervision.domain.LigneReceptionProduitTestSamples.*;
import static com.adm.supervision.domain.ProduitTestSamples.*;
import static com.adm.supervision.domain.ReceptionProduitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LigneReceptionProduitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneReceptionProduit.class);
        LigneReceptionProduit ligneReceptionProduit1 = getLigneReceptionProduitSample1();
        LigneReceptionProduit ligneReceptionProduit2 = new LigneReceptionProduit();
        assertThat(ligneReceptionProduit1).isNotEqualTo(ligneReceptionProduit2);

        ligneReceptionProduit2.setId(ligneReceptionProduit1.getId());
        assertThat(ligneReceptionProduit1).isEqualTo(ligneReceptionProduit2);

        ligneReceptionProduit2 = getLigneReceptionProduitSample2();
        assertThat(ligneReceptionProduit1).isNotEqualTo(ligneReceptionProduit2);
    }

    @Test
    void receptionTest() {
        LigneReceptionProduit ligneReceptionProduit = getLigneReceptionProduitRandomSampleGenerator();
        ReceptionProduit receptionProduitBack = getReceptionProduitRandomSampleGenerator();

        ligneReceptionProduit.setReception(receptionProduitBack);
        assertThat(ligneReceptionProduit.getReception()).isEqualTo(receptionProduitBack);

        ligneReceptionProduit.reception(null);
        assertThat(ligneReceptionProduit.getReception()).isNull();
    }

    @Test
    void produitTest() {
        LigneReceptionProduit ligneReceptionProduit = getLigneReceptionProduitRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        ligneReceptionProduit.setProduit(produitBack);
        assertThat(ligneReceptionProduit.getProduit()).isEqualTo(produitBack);

        ligneReceptionProduit.produit(null);
        assertThat(ligneReceptionProduit.getProduit()).isNull();
    }
}
