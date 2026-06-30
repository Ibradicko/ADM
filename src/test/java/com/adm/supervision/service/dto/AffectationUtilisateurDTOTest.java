package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AffectationUtilisateurDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AffectationUtilisateurDTO.class);
        AffectationUtilisateurDTO affectationUtilisateurDTO1 = new AffectationUtilisateurDTO();
        affectationUtilisateurDTO1.setId(1L);
        AffectationUtilisateurDTO affectationUtilisateurDTO2 = new AffectationUtilisateurDTO();
        assertThat(affectationUtilisateurDTO1).isNotEqualTo(affectationUtilisateurDTO2);
        affectationUtilisateurDTO2.setId(affectationUtilisateurDTO1.getId());
        assertThat(affectationUtilisateurDTO1).isEqualTo(affectationUtilisateurDTO2);
        affectationUtilisateurDTO2.setId(2L);
        assertThat(affectationUtilisateurDTO1).isNotEqualTo(affectationUtilisateurDTO2);
        affectationUtilisateurDTO1.setId(null);
        assertThat(affectationUtilisateurDTO1).isNotEqualTo(affectationUtilisateurDTO2);
    }
}
