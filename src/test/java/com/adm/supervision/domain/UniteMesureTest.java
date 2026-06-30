package com.adm.supervision.domain;

import static com.adm.supervision.domain.UniteMesureTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UniteMesureTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UniteMesure.class);
        UniteMesure uniteMesure1 = getUniteMesureSample1();
        UniteMesure uniteMesure2 = new UniteMesure();
        assertThat(uniteMesure1).isNotEqualTo(uniteMesure2);

        uniteMesure2.setId(uniteMesure1.getId());
        assertThat(uniteMesure1).isEqualTo(uniteMesure2);

        uniteMesure2 = getUniteMesureSample2();
        assertThat(uniteMesure1).isNotEqualTo(uniteMesure2);
    }
}
