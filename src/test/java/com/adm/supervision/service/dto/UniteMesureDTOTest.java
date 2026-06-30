package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UniteMesureDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UniteMesureDTO.class);
        UniteMesureDTO uniteMesureDTO1 = new UniteMesureDTO();
        uniteMesureDTO1.setId(1L);
        UniteMesureDTO uniteMesureDTO2 = new UniteMesureDTO();
        assertThat(uniteMesureDTO1).isNotEqualTo(uniteMesureDTO2);
        uniteMesureDTO2.setId(uniteMesureDTO1.getId());
        assertThat(uniteMesureDTO1).isEqualTo(uniteMesureDTO2);
        uniteMesureDTO2.setId(2L);
        assertThat(uniteMesureDTO1).isNotEqualTo(uniteMesureDTO2);
        uniteMesureDTO1.setId(null);
        assertThat(uniteMesureDTO1).isNotEqualTo(uniteMesureDTO2);
    }
}
