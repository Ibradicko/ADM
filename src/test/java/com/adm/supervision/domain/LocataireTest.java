package com.adm.supervision.domain;

import static com.adm.supervision.domain.LocataireTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LocataireTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Locataire.class);
        Locataire locataire1 = getLocataireSample1();
        Locataire locataire2 = new Locataire();
        assertThat(locataire1).isNotEqualTo(locataire2);

        locataire2.setId(locataire1.getId());
        assertThat(locataire1).isEqualTo(locataire2);

        locataire2 = getLocataireSample2();
        assertThat(locataire1).isNotEqualTo(locataire2);
    }
}
