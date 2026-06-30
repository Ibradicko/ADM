package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EtiquetteProduitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EtiquetteProduitDTO.class);
        EtiquetteProduitDTO etiquetteProduitDTO1 = new EtiquetteProduitDTO();
        etiquetteProduitDTO1.setId(1L);
        EtiquetteProduitDTO etiquetteProduitDTO2 = new EtiquetteProduitDTO();
        assertThat(etiquetteProduitDTO1).isNotEqualTo(etiquetteProduitDTO2);
        etiquetteProduitDTO2.setId(etiquetteProduitDTO1.getId());
        assertThat(etiquetteProduitDTO1).isEqualTo(etiquetteProduitDTO2);
        etiquetteProduitDTO2.setId(2L);
        assertThat(etiquetteProduitDTO1).isNotEqualTo(etiquetteProduitDTO2);
        etiquetteProduitDTO1.setId(null);
        assertThat(etiquetteProduitDTO1).isNotEqualTo(etiquetteProduitDTO2);
    }
}
