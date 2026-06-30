package com.adm.supervision.domain;

import static com.adm.supervision.domain.LotEtiquettesTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LotEtiquettesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LotEtiquettes.class);
        LotEtiquettes lotEtiquettes1 = getLotEtiquettesSample1();
        LotEtiquettes lotEtiquettes2 = new LotEtiquettes();
        assertThat(lotEtiquettes1).isNotEqualTo(lotEtiquettes2);

        lotEtiquettes2.setId(lotEtiquettes1.getId());
        assertThat(lotEtiquettes1).isEqualTo(lotEtiquettes2);

        lotEtiquettes2 = getLotEtiquettesSample2();
        assertThat(lotEtiquettes1).isNotEqualTo(lotEtiquettes2);
    }
}
