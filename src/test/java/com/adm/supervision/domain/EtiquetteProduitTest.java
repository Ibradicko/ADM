package com.adm.supervision.domain;

import static com.adm.supervision.domain.EtiquetteProduitTestSamples.*;
import static com.adm.supervision.domain.LotEtiquettesTestSamples.*;
import static com.adm.supervision.domain.ProduitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EtiquetteProduitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EtiquetteProduit.class);
        EtiquetteProduit etiquetteProduit1 = getEtiquetteProduitSample1();
        EtiquetteProduit etiquetteProduit2 = new EtiquetteProduit();
        assertThat(etiquetteProduit1).isNotEqualTo(etiquetteProduit2);

        etiquetteProduit2.setId(etiquetteProduit1.getId());
        assertThat(etiquetteProduit1).isEqualTo(etiquetteProduit2);

        etiquetteProduit2 = getEtiquetteProduitSample2();
        assertThat(etiquetteProduit1).isNotEqualTo(etiquetteProduit2);
    }

    @Test
    void produitTest() {
        EtiquetteProduit etiquetteProduit = getEtiquetteProduitRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        etiquetteProduit.setProduit(produitBack);
        assertThat(etiquetteProduit.getProduit()).isEqualTo(produitBack);

        etiquetteProduit.produit(null);
        assertThat(etiquetteProduit.getProduit()).isNull();
    }

    @Test
    void lotTest() {
        EtiquetteProduit etiquetteProduit = getEtiquetteProduitRandomSampleGenerator();
        LotEtiquettes lotEtiquettesBack = getLotEtiquettesRandomSampleGenerator();

        etiquetteProduit.setLot(lotEtiquettesBack);
        assertThat(etiquetteProduit.getLot()).isEqualTo(lotEtiquettesBack);

        etiquetteProduit.lot(null);
        assertThat(etiquetteProduit.getLot()).isNull();
    }
}
