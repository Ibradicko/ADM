package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.FamilleArticleTestSamples.*;
import static com.adm.supervision.domain.GroupeArticleTestSamples.*;
import static com.adm.supervision.domain.ProduitTestSamples.*;
import static com.adm.supervision.domain.SousFamilleArticleTestSamples.*;
import static com.adm.supervision.domain.UniteMesureTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProduitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Produit.class);
        Produit produit1 = getProduitSample1();
        Produit produit2 = new Produit();
        assertThat(produit1).isNotEqualTo(produit2);

        produit2.setId(produit1.getId());
        assertThat(produit1).isEqualTo(produit2);

        produit2 = getProduitSample2();
        assertThat(produit1).isNotEqualTo(produit2);
    }

    @Test
    void boutiqueTest() {
        Produit produit = getProduitRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        produit.setBoutique(boutiqueBack);
        assertThat(produit.getBoutique()).isEqualTo(boutiqueBack);

        produit.boutique(null);
        assertThat(produit.getBoutique()).isNull();
    }

    @Test
    void groupeArticleTest() {
        Produit produit = getProduitRandomSampleGenerator();
        GroupeArticle groupeArticleBack = getGroupeArticleRandomSampleGenerator();

        produit.setGroupeArticle(groupeArticleBack);
        assertThat(produit.getGroupeArticle()).isEqualTo(groupeArticleBack);

        produit.groupeArticle(null);
        assertThat(produit.getGroupeArticle()).isNull();
    }

    @Test
    void familleArticleTest() {
        Produit produit = getProduitRandomSampleGenerator();
        FamilleArticle familleArticleBack = getFamilleArticleRandomSampleGenerator();

        produit.setFamilleArticle(familleArticleBack);
        assertThat(produit.getFamilleArticle()).isEqualTo(familleArticleBack);

        produit.familleArticle(null);
        assertThat(produit.getFamilleArticle()).isNull();
    }

    @Test
    void sousFamilleArticleTest() {
        Produit produit = getProduitRandomSampleGenerator();
        SousFamilleArticle sousFamilleArticleBack = getSousFamilleArticleRandomSampleGenerator();

        produit.setSousFamilleArticle(sousFamilleArticleBack);
        assertThat(produit.getSousFamilleArticle()).isEqualTo(sousFamilleArticleBack);

        produit.sousFamilleArticle(null);
        assertThat(produit.getSousFamilleArticle()).isNull();
    }

    @Test
    void uniteMesureTest() {
        Produit produit = getProduitRandomSampleGenerator();
        UniteMesure uniteMesureBack = getUniteMesureRandomSampleGenerator();

        produit.setUniteMesure(uniteMesureBack);
        assertThat(produit.getUniteMesure()).isEqualTo(uniteMesureBack);

        produit.uniteMesure(null);
        assertThat(produit.getUniteMesure()).isNull();
    }
}
