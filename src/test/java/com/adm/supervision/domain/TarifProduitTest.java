package com.adm.supervision.domain;

import static com.adm.supervision.domain.ProduitTestSamples.*;
import static com.adm.supervision.domain.TarifProduitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TarifProduitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TarifProduit.class);
        TarifProduit tarifProduit1 = getTarifProduitSample1();
        TarifProduit tarifProduit2 = new TarifProduit();
        assertThat(tarifProduit1).isNotEqualTo(tarifProduit2);

        tarifProduit2.setId(tarifProduit1.getId());
        assertThat(tarifProduit1).isEqualTo(tarifProduit2);

        tarifProduit2 = getTarifProduitSample2();
        assertThat(tarifProduit1).isNotEqualTo(tarifProduit2);
    }

    @Test
    void produitTest() {
        TarifProduit tarifProduit = getTarifProduitRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        tarifProduit.setProduit(produitBack);
        assertThat(tarifProduit.getProduit()).isEqualTo(produitBack);

        tarifProduit.produit(null);
        assertThat(tarifProduit.getProduit()).isNull();
    }
}
