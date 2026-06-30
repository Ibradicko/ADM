package com.adm.supervision.domain;

import static com.adm.supervision.domain.FamilleArticleTestSamples.*;
import static com.adm.supervision.domain.GroupeArticleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FamilleArticleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FamilleArticle.class);
        FamilleArticle familleArticle1 = getFamilleArticleSample1();
        FamilleArticle familleArticle2 = new FamilleArticle();
        assertThat(familleArticle1).isNotEqualTo(familleArticle2);

        familleArticle2.setId(familleArticle1.getId());
        assertThat(familleArticle1).isEqualTo(familleArticle2);

        familleArticle2 = getFamilleArticleSample2();
        assertThat(familleArticle1).isNotEqualTo(familleArticle2);
    }

    @Test
    void groupeArticleTest() {
        FamilleArticle familleArticle = getFamilleArticleRandomSampleGenerator();
        GroupeArticle groupeArticleBack = getGroupeArticleRandomSampleGenerator();

        familleArticle.setGroupeArticle(groupeArticleBack);
        assertThat(familleArticle.getGroupeArticle()).isEqualTo(groupeArticleBack);

        familleArticle.groupeArticle(null);
        assertThat(familleArticle.getGroupeArticle()).isNull();
    }
}
