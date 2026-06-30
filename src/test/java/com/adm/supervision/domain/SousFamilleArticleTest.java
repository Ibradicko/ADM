package com.adm.supervision.domain;

import static com.adm.supervision.domain.FamilleArticleTestSamples.*;
import static com.adm.supervision.domain.SousFamilleArticleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SousFamilleArticleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SousFamilleArticle.class);
        SousFamilleArticle sousFamilleArticle1 = getSousFamilleArticleSample1();
        SousFamilleArticle sousFamilleArticle2 = new SousFamilleArticle();
        assertThat(sousFamilleArticle1).isNotEqualTo(sousFamilleArticle2);

        sousFamilleArticle2.setId(sousFamilleArticle1.getId());
        assertThat(sousFamilleArticle1).isEqualTo(sousFamilleArticle2);

        sousFamilleArticle2 = getSousFamilleArticleSample2();
        assertThat(sousFamilleArticle1).isNotEqualTo(sousFamilleArticle2);
    }

    @Test
    void familleArticleTest() {
        SousFamilleArticle sousFamilleArticle = getSousFamilleArticleRandomSampleGenerator();
        FamilleArticle familleArticleBack = getFamilleArticleRandomSampleGenerator();

        sousFamilleArticle.setFamilleArticle(familleArticleBack);
        assertThat(sousFamilleArticle.getFamilleArticle()).isEqualTo(familleArticleBack);

        sousFamilleArticle.familleArticle(null);
        assertThat(sousFamilleArticle.getFamilleArticle()).isNull();
    }
}
