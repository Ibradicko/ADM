package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.LocataireTestSamples.*;
import static com.adm.supervision.domain.RapportExportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RapportExportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RapportExport.class);
        RapportExport rapportExport1 = getRapportExportSample1();
        RapportExport rapportExport2 = new RapportExport();
        assertThat(rapportExport1).isNotEqualTo(rapportExport2);

        rapportExport2.setId(rapportExport1.getId());
        assertThat(rapportExport1).isEqualTo(rapportExport2);

        rapportExport2 = getRapportExportSample2();
        assertThat(rapportExport1).isNotEqualTo(rapportExport2);
    }

    @Test
    void boutiqueTest() {
        RapportExport rapportExport = getRapportExportRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        rapportExport.setBoutique(boutiqueBack);
        assertThat(rapportExport.getBoutique()).isEqualTo(boutiqueBack);

        rapportExport.boutique(null);
        assertThat(rapportExport.getBoutique()).isNull();
    }

    @Test
    void locataireTest() {
        RapportExport rapportExport = getRapportExportRandomSampleGenerator();
        Locataire locataireBack = getLocataireRandomSampleGenerator();

        rapportExport.setLocataire(locataireBack);
        assertThat(rapportExport.getLocataire()).isEqualTo(locataireBack);

        rapportExport.locataire(null);
        assertThat(rapportExport.getLocataire()).isNull();
    }
}
