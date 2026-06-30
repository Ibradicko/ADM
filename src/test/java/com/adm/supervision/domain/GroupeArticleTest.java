package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.GroupeArticleTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GroupeArticleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GroupeArticle.class);
        GroupeArticle groupeArticle1 = getGroupeArticleSample1();
        GroupeArticle groupeArticle2 = new GroupeArticle();
        assertThat(groupeArticle1).isNotEqualTo(groupeArticle2);

        groupeArticle2.setId(groupeArticle1.getId());
        assertThat(groupeArticle1).isEqualTo(groupeArticle2);

        groupeArticle2 = getGroupeArticleSample2();
        assertThat(groupeArticle1).isNotEqualTo(groupeArticle2);
    }

    @Test
    void boutiqueTest() {
        GroupeArticle groupeArticle = getGroupeArticleRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        groupeArticle.setBoutique(boutiqueBack);
        assertThat(groupeArticle.getBoutique()).isEqualTo(boutiqueBack);

        groupeArticle.boutique(null);
        assertThat(groupeArticle.getBoutique()).isNull();
    }
}
