package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GroupeArticleDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GroupeArticleDTO.class);
        GroupeArticleDTO groupeArticleDTO1 = new GroupeArticleDTO();
        groupeArticleDTO1.setId(1L);
        GroupeArticleDTO groupeArticleDTO2 = new GroupeArticleDTO();
        assertThat(groupeArticleDTO1).isNotEqualTo(groupeArticleDTO2);
        groupeArticleDTO2.setId(groupeArticleDTO1.getId());
        assertThat(groupeArticleDTO1).isEqualTo(groupeArticleDTO2);
        groupeArticleDTO2.setId(2L);
        assertThat(groupeArticleDTO1).isNotEqualTo(groupeArticleDTO2);
        groupeArticleDTO1.setId(null);
        assertThat(groupeArticleDTO1).isNotEqualTo(groupeArticleDTO2);
    }
}
