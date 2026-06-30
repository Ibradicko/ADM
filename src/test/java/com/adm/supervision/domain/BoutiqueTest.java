package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BoutiqueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Boutique.class);
        Boutique boutique1 = getBoutiqueSample1();
        Boutique boutique2 = new Boutique();
        assertThat(boutique1).isNotEqualTo(boutique2);

        boutique2.setId(boutique1.getId());
        assertThat(boutique1).isEqualTo(boutique2);

        boutique2 = getBoutiqueSample2();
        assertThat(boutique1).isNotEqualTo(boutique2);
    }
}
