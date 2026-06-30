package com.adm.supervision.domain;

import static com.adm.supervision.domain.ParametreCodeBarresTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParametreCodeBarresTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParametreCodeBarres.class);
        ParametreCodeBarres parametreCodeBarres1 = getParametreCodeBarresSample1();
        ParametreCodeBarres parametreCodeBarres2 = new ParametreCodeBarres();
        assertThat(parametreCodeBarres1).isNotEqualTo(parametreCodeBarres2);

        parametreCodeBarres2.setId(parametreCodeBarres1.getId());
        assertThat(parametreCodeBarres1).isEqualTo(parametreCodeBarres2);

        parametreCodeBarres2 = getParametreCodeBarresSample2();
        assertThat(parametreCodeBarres1).isNotEqualTo(parametreCodeBarres2);
    }
}
