package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FamilleArticleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FamilleArticleDTO.class);
        FamilleArticleDTO familleArticleDTO1 = new FamilleArticleDTO();
        familleArticleDTO1.setId(1L);
        FamilleArticleDTO familleArticleDTO2 = new FamilleArticleDTO();
        assertThat(familleArticleDTO1).isNotEqualTo(familleArticleDTO2);
        familleArticleDTO2.setId(familleArticleDTO1.getId());
        assertThat(familleArticleDTO1).isEqualTo(familleArticleDTO2);
        familleArticleDTO2.setId(2L);
        assertThat(familleArticleDTO1).isNotEqualTo(familleArticleDTO2);
        familleArticleDTO1.setId(null);
        assertThat(familleArticleDTO1).isNotEqualTo(familleArticleDTO2);
    }
}
