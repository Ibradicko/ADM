package com.adm.supervision.domain;

import static com.adm.supervision.domain.CodeBarresProduitTestSamples.*;
import static com.adm.supervision.domain.ProduitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CodeBarresProduitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CodeBarresProduit.class);
        CodeBarresProduit codeBarresProduit1 = getCodeBarresProduitSample1();
        CodeBarresProduit codeBarresProduit2 = new CodeBarresProduit();
        assertThat(codeBarresProduit1).isNotEqualTo(codeBarresProduit2);

        codeBarresProduit2.setId(codeBarresProduit1.getId());
        assertThat(codeBarresProduit1).isEqualTo(codeBarresProduit2);

        codeBarresProduit2 = getCodeBarresProduitSample2();
        assertThat(codeBarresProduit1).isNotEqualTo(codeBarresProduit2);
    }

    @Test
    void produitTest() {
        CodeBarresProduit codeBarresProduit = getCodeBarresProduitRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        codeBarresProduit.setProduit(produitBack);
        assertThat(codeBarresProduit.getProduit()).isEqualTo(produitBack);

        codeBarresProduit.produit(null);
        assertThat(codeBarresProduit.getProduit()).isNull();
    }
}
