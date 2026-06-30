package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.ProduitTestSamples.*;
import static com.adm.supervision.domain.ScanInconnuTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScanInconnuTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScanInconnu.class);
        ScanInconnu scanInconnu1 = getScanInconnuSample1();
        ScanInconnu scanInconnu2 = new ScanInconnu();
        assertThat(scanInconnu1).isNotEqualTo(scanInconnu2);

        scanInconnu2.setId(scanInconnu1.getId());
        assertThat(scanInconnu1).isEqualTo(scanInconnu2);

        scanInconnu2 = getScanInconnuSample2();
        assertThat(scanInconnu1).isNotEqualTo(scanInconnu2);
    }

    @Test
    void boutiqueTest() {
        ScanInconnu scanInconnu = getScanInconnuRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        scanInconnu.setBoutique(boutiqueBack);
        assertThat(scanInconnu.getBoutique()).isEqualTo(boutiqueBack);

        scanInconnu.boutique(null);
        assertThat(scanInconnu.getBoutique()).isNull();
    }

    @Test
    void produitAffecteTest() {
        ScanInconnu scanInconnu = getScanInconnuRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        scanInconnu.setProduitAffecte(produitBack);
        assertThat(scanInconnu.getProduitAffecte()).isEqualTo(produitBack);

        scanInconnu.produitAffecte(null);
        assertThat(scanInconnu.getProduitAffecte()).isNull();
    }
}
