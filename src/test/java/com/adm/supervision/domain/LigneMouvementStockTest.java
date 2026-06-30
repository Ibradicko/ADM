package com.adm.supervision.domain;

import static com.adm.supervision.domain.DepotStockTestSamples.*;
import static com.adm.supervision.domain.LigneMouvementStockTestSamples.*;
import static com.adm.supervision.domain.MouvementStockTestSamples.*;
import static com.adm.supervision.domain.ProduitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LigneMouvementStockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneMouvementStock.class);
        LigneMouvementStock ligneMouvementStock1 = getLigneMouvementStockSample1();
        LigneMouvementStock ligneMouvementStock2 = new LigneMouvementStock();
        assertThat(ligneMouvementStock1).isNotEqualTo(ligneMouvementStock2);

        ligneMouvementStock2.setId(ligneMouvementStock1.getId());
        assertThat(ligneMouvementStock1).isEqualTo(ligneMouvementStock2);

        ligneMouvementStock2 = getLigneMouvementStockSample2();
        assertThat(ligneMouvementStock1).isNotEqualTo(ligneMouvementStock2);
    }

    @Test
    void mouvementTest() {
        LigneMouvementStock ligneMouvementStock = getLigneMouvementStockRandomSampleGenerator();
        MouvementStock mouvementStockBack = getMouvementStockRandomSampleGenerator();

        ligneMouvementStock.setMouvement(mouvementStockBack);
        assertThat(ligneMouvementStock.getMouvement()).isEqualTo(mouvementStockBack);

        ligneMouvementStock.mouvement(null);
        assertThat(ligneMouvementStock.getMouvement()).isNull();
    }

    @Test
    void produitTest() {
        LigneMouvementStock ligneMouvementStock = getLigneMouvementStockRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        ligneMouvementStock.setProduit(produitBack);
        assertThat(ligneMouvementStock.getProduit()).isEqualTo(produitBack);

        ligneMouvementStock.produit(null);
        assertThat(ligneMouvementStock.getProduit()).isNull();
    }

    @Test
    void depotTest() {
        LigneMouvementStock ligneMouvementStock = getLigneMouvementStockRandomSampleGenerator();
        DepotStock depotStockBack = getDepotStockRandomSampleGenerator();

        ligneMouvementStock.setDepot(depotStockBack);
        assertThat(ligneMouvementStock.getDepot()).isEqualTo(depotStockBack);

        ligneMouvementStock.depot(null);
        assertThat(ligneMouvementStock.getDepot()).isNull();
    }
}
