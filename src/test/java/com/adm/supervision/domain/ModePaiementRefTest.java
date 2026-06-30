package com.adm.supervision.domain;

import static com.adm.supervision.domain.ModePaiementRefTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ModePaiementRefTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ModePaiementRef.class);
        ModePaiementRef modePaiementRef1 = getModePaiementRefSample1();
        ModePaiementRef modePaiementRef2 = new ModePaiementRef();
        assertThat(modePaiementRef1).isNotEqualTo(modePaiementRef2);

        modePaiementRef2.setId(modePaiementRef1.getId());
        assertThat(modePaiementRef1).isEqualTo(modePaiementRef2);

        modePaiementRef2 = getModePaiementRefSample2();
        assertThat(modePaiementRef1).isNotEqualTo(modePaiementRef2);
    }
}
