package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LotEtiquettesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LotEtiquettesDTO.class);
        LotEtiquettesDTO lotEtiquettesDTO1 = new LotEtiquettesDTO();
        lotEtiquettesDTO1.setId(1L);
        LotEtiquettesDTO lotEtiquettesDTO2 = new LotEtiquettesDTO();
        assertThat(lotEtiquettesDTO1).isNotEqualTo(lotEtiquettesDTO2);
        lotEtiquettesDTO2.setId(lotEtiquettesDTO1.getId());
        assertThat(lotEtiquettesDTO1).isEqualTo(lotEtiquettesDTO2);
        lotEtiquettesDTO2.setId(2L);
        assertThat(lotEtiquettesDTO1).isNotEqualTo(lotEtiquettesDTO2);
        lotEtiquettesDTO1.setId(null);
        assertThat(lotEtiquettesDTO1).isNotEqualTo(lotEtiquettesDTO2);
    }
}
