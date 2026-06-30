package com.adm.supervision.domain;

import static com.adm.supervision.domain.ParametreGlobalTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParametreGlobalTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParametreGlobal.class);
        ParametreGlobal parametreGlobal1 = getParametreGlobalSample1();
        ParametreGlobal parametreGlobal2 = new ParametreGlobal();
        assertThat(parametreGlobal1).isNotEqualTo(parametreGlobal2);

        parametreGlobal2.setId(parametreGlobal1.getId());
        assertThat(parametreGlobal1).isEqualTo(parametreGlobal2);

        parametreGlobal2 = getParametreGlobalSample2();
        assertThat(parametreGlobal1).isNotEqualTo(parametreGlobal2);
    }
}
