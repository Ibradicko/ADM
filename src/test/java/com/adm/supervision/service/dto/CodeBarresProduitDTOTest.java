package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CodeBarresProduitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CodeBarresProduitDTO.class);
        CodeBarresProduitDTO codeBarresProduitDTO1 = new CodeBarresProduitDTO();
        codeBarresProduitDTO1.setId(1L);
        CodeBarresProduitDTO codeBarresProduitDTO2 = new CodeBarresProduitDTO();
        assertThat(codeBarresProduitDTO1).isNotEqualTo(codeBarresProduitDTO2);
        codeBarresProduitDTO2.setId(codeBarresProduitDTO1.getId());
        assertThat(codeBarresProduitDTO1).isEqualTo(codeBarresProduitDTO2);
        codeBarresProduitDTO2.setId(2L);
        assertThat(codeBarresProduitDTO1).isNotEqualTo(codeBarresProduitDTO2);
        codeBarresProduitDTO1.setId(null);
        assertThat(codeBarresProduitDTO1).isNotEqualTo(codeBarresProduitDTO2);
    }
}
