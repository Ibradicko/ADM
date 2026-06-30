package com.adm.supervision.domain;

import static com.adm.supervision.domain.AffectationUtilisateurTestSamples.*;
import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.ProfilMetierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AffectationUtilisateurTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AffectationUtilisateur.class);
        AffectationUtilisateur affectationUtilisateur1 = getAffectationUtilisateurSample1();
        AffectationUtilisateur affectationUtilisateur2 = new AffectationUtilisateur();
        assertThat(affectationUtilisateur1).isNotEqualTo(affectationUtilisateur2);

        affectationUtilisateur2.setId(affectationUtilisateur1.getId());
        assertThat(affectationUtilisateur1).isEqualTo(affectationUtilisateur2);

        affectationUtilisateur2 = getAffectationUtilisateurSample2();
        assertThat(affectationUtilisateur1).isNotEqualTo(affectationUtilisateur2);
    }

    @Test
    void boutiqueTest() {
        AffectationUtilisateur affectationUtilisateur = getAffectationUtilisateurRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        affectationUtilisateur.setBoutique(boutiqueBack);
        assertThat(affectationUtilisateur.getBoutique()).isEqualTo(boutiqueBack);

        affectationUtilisateur.boutique(null);
        assertThat(affectationUtilisateur.getBoutique()).isNull();
    }

    @Test
    void profilTest() {
        AffectationUtilisateur affectationUtilisateur = getAffectationUtilisateurRandomSampleGenerator();
        ProfilMetier profilMetierBack = getProfilMetierRandomSampleGenerator();

        affectationUtilisateur.setProfil(profilMetierBack);
        assertThat(affectationUtilisateur.getProfil()).isEqualTo(profilMetierBack);

        affectationUtilisateur.profil(null);
        assertThat(affectationUtilisateur.getProfil()).isNull();
    }
}
