package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SousFamilleArticleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SousFamilleArticleDTO.class);
        SousFamilleArticleDTO sousFamilleArticleDTO1 = new SousFamilleArticleDTO();
        sousFamilleArticleDTO1.setId(1L);
        SousFamilleArticleDTO sousFamilleArticleDTO2 = new SousFamilleArticleDTO();
        assertThat(sousFamilleArticleDTO1).isNotEqualTo(sousFamilleArticleDTO2);
        sousFamilleArticleDTO2.setId(sousFamilleArticleDTO1.getId());
        assertThat(sousFamilleArticleDTO1).isEqualTo(sousFamilleArticleDTO2);
        sousFamilleArticleDTO2.setId(2L);
        assertThat(sousFamilleArticleDTO1).isNotEqualTo(sousFamilleArticleDTO2);
        sousFamilleArticleDTO1.setId(null);
        assertThat(sousFamilleArticleDTO1).isNotEqualTo(sousFamilleArticleDTO2);
    }
}
