package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.GroupeArticleTestSamples.*;
import static com.adm.supervision.domain.LocataireTestSamples.*;
import static com.adm.supervision.domain.ProduitTestSamples.*;
import static com.adm.supervision.domain.RegleRedevanceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RegleRedevanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RegleRedevance.class);
        RegleRedevance regleRedevance1 = getRegleRedevanceSample1();
        RegleRedevance regleRedevance2 = new RegleRedevance();
        assertThat(regleRedevance1).isNotEqualTo(regleRedevance2);

        regleRedevance2.setId(regleRedevance1.getId());
        assertThat(regleRedevance1).isEqualTo(regleRedevance2);

        regleRedevance2 = getRegleRedevanceSample2();
        assertThat(regleRedevance1).isNotEqualTo(regleRedevance2);
    }

    @Test
    void boutiqueTest() {
        RegleRedevance regleRedevance = getRegleRedevanceRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        regleRedevance.setBoutique(boutiqueBack);
        assertThat(regleRedevance.getBoutique()).isEqualTo(boutiqueBack);

        regleRedevance.boutique(null);
        assertThat(regleRedevance.getBoutique()).isNull();
    }

    @Test
    void locataireTest() {
        RegleRedevance regleRedevance = getRegleRedevanceRandomSampleGenerator();
        Locataire locataireBack = getLocataireRandomSampleGenerator();

        regleRedevance.setLocataire(locataireBack);
        assertThat(regleRedevance.getLocataire()).isEqualTo(locataireBack);

        regleRedevance.locataire(null);
        assertThat(regleRedevance.getLocataire()).isNull();
    }

    @Test
    void groupeArticleTest() {
        RegleRedevance regleRedevance = getRegleRedevanceRandomSampleGenerator();
        GroupeArticle groupeArticleBack = getGroupeArticleRandomSampleGenerator();

        regleRedevance.setGroupeArticle(groupeArticleBack);
        assertThat(regleRedevance.getGroupeArticle()).isEqualTo(groupeArticleBack);

        regleRedevance.groupeArticle(null);
        assertThat(regleRedevance.getGroupeArticle()).isNull();
    }

    @Test
    void produitTest() {
        RegleRedevance regleRedevance = getRegleRedevanceRandomSampleGenerator();
        Produit produitBack = getProduitRandomSampleGenerator();

        regleRedevance.setProduit(produitBack);
        assertThat(regleRedevance.getProduit()).isEqualTo(produitBack);

        regleRedevance.produit(null);
        assertThat(regleRedevance.getProduit()).isNull();
    }
}
