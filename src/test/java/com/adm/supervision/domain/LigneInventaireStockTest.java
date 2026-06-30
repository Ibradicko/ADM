package com.adm.supervision.domain;

import static com.adm.supervision.domain.InventaireStockTestSamples.*;
import static com.adm.supervision.domain.LigneInventaireStockTestSamples.*;
import static com.adm.supervision.domain.ProduitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LigneInventaireStockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LigneInventaireStock.class);
        LigneInventaireStock ligneInventaireStock1 = getLigneInventaireStockSample1();
        LigneInventaireStock ligneInventaireStock2 = new LigneInventaireStock();
        assertThat(ligneInventaireStock1).isNotEqualTo(ligneInventaireStock2);

        ligneInventaireStock2.setId(ligneInventaireStock1.getId());
        assertThat(ligneInventaireStock1).isEqualTo(ligneInventaireStock2);

        ligneInventaireStock2 = getLigneInventaireStockSample2();
        assertThat(ligneInventaireStock1).isNotEqualTo(ligneInventaireStock2);
    }

    @Test
    void inventaireTest() {
        LigneInventaireStock ligneInventaireStock = getLigneInventaireStockRandomSampleGenerator();
        InventaireStock inventaireStockBack = getInventaireStockRandomSampleGenerator();

        ligneInventaireStock.setInventaire(inventaireStockBack);
        assertThat(ligneInventaireStock.getInventaire()).isEqualTo(inventaireStockBack);

        ligneInventaireStock.inventaire(null);
        assertThat(ligneInventaireStock.getInventaire()).isNull();
    }

    @Test
    void produitTest() {
        LigneInventaireStock ligneInventaireStock = getLigneInventaireStockRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        ligneInventaireStock.setProduit(produitBack);
        assertThat(ligneInventaireStock.getProduit()).isEqualTo(produitBack);

        ligneInventaireStock.produit(null);
        assertThat(ligneInventaireStock.getProduit()).isNull();
    }
}
