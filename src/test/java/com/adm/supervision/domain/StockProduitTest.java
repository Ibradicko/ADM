package com.adm.supervision.domain;

import static com.adm.supervision.domain.DepotStockTestSamples.*;
import static com.adm.supervision.domain.ProduitTestSamples.*;
import static com.adm.supervision.domain.StockProduitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockProduitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockProduit.class);
        StockProduit stockProduit1 = getStockProduitSample1();
        StockProduit stockProduit2 = new StockProduit();
        assertThat(stockProduit1).isNotEqualTo(stockProduit2);

        stockProduit2.setId(stockProduit1.getId());
        assertThat(stockProduit1).isEqualTo(stockProduit2);

        stockProduit2 = getStockProduitSample2();
        assertThat(stockProduit1).isNotEqualTo(stockProduit2);
    }

    @Test
    void produitTest() {
        StockProduit stockProduit = getStockProduitRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        stockProduit.setProduit(produitBack);
        assertThat(stockProduit.getProduit()).isEqualTo(produitBack);

        stockProduit.produit(null);
        assertThat(stockProduit.getProduit()).isNull();
    }

    @Test
    void depotTest() {
        StockProduit stockProduit = getStockProduitRandomSampleGenerator();
        DepotStock depotStockBack = getDepotStockRandomSampleGenerator();

        stockProduit.setDepot(depotStockBack);
        assertThat(stockProduit.getDepot()).isEqualTo(depotStockBack);

        stockProduit.depot(null);
        assertThat(stockProduit.getDepot()).isNull();
    }
}
