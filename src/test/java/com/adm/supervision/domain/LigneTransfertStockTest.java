package com.adm.supervision.domain;

import static com.adm.supervision.domain.LigneTransfertStockTestSamples.*;
import static com.adm.supervision.domain.ProduitTestSamples.*;
import static com.adm.supervision.domain.TransfertStockTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LigneTransfertStockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneTransfertStock.class);
        LigneTransfertStock ligneTransfertStock1 = getLigneTransfertStockSample1();
        LigneTransfertStock ligneTransfertStock2 = new LigneTransfertStock();
        assertThat(ligneTransfertStock1).isNotEqualTo(ligneTransfertStock2);

        ligneTransfertStock2.setId(ligneTransfertStock1.getId());
        assertThat(ligneTransfertStock1).isEqualTo(ligneTransfertStock2);

        ligneTransfertStock2 = getLigneTransfertStockSample2();
        assertThat(ligneTransfertStock1).isNotEqualTo(ligneTransfertStock2);
    }

    @Test
    void transfertTest() {
        LigneTransfertStock ligneTransfertStock = getLigneTransfertStockRandomSampleGenerator();
        TransfertStock transfertStockBack = getTransfertStockRandomSampleGenerator();

        ligneTransfertStock.setTransfert(transfertStockBack);
        assertThat(ligneTransfertStock.getTransfert()).isEqualTo(transfertStockBack);

        ligneTransfertStock.transfert(null);
        assertThat(ligneTransfertStock.getTransfert()).isNull();
    }

    @Test
    void produitTest() {
        LigneTransfertStock ligneTransfertStock = getLigneTransfertStockRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        ligneTransfertStock.setProduit(produitBack);
        assertThat(ligneTransfertStock.getProduit()).isEqualTo(produitBack);

        ligneTransfertStock.produit(null);
        assertThat(ligneTransfertStock.getProduit()).isNull();
    }
}
