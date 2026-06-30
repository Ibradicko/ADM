package com.adm.supervision.domain;

import static com.adm.supervision.domain.PermissionMetierTestSamples.*;
import static com.adm.supervision.domain.ProfilMetierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PermissionMetierTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PermissionMetier.class);
        PermissionMetier permissionMetier1 = getPermissionMetierSample1();
        PermissionMetier permissionMetier2 = new PermissionMetier();
        assertThat(permissionMetier1).isNotEqualTo(permissionMetier2);

        permissionMetier2.setId(permissionMetier1.getId());
        assertThat(permissionMetier1).isEqualTo(permissionMetier2);

        permissionMetier2 = getPermissionMetierSample2();
        assertThat(permissionMetier1).isNotEqualTo(permissionMetier2);
    }

    @Test
    void profilsTest() {
        PermissionMetier permissionMetier = getPermissionMetierRandomSampleGenerator();
        ProfilMetier profilMetierBack = getProfilMetierRandomSampleGenerator();

        permissionMetier.addProfils(profilMetierBack);
        assertThat(permissionMetier.getProfilses()).containsOnly(profilMetierBack);
        assertThat(profilMetierBack.getPermissionses()).containsOnly(permissionMetier);

        permissionMetier.removeProfils(profilMetierBack);
        assertThat(permissionMetier.getProfilses()).doesNotContain(profilMetierBack);
        assertThat(profilMetierBack.getPermissionses()).doesNotContain(permissionMetier);

        permissionMetier.profilses(new HashSet<>(Set.of(profilMetierBack)));
        assertThat(permissionMetier.getProfilses()).containsOnly(profilMetierBack);
        assertThat(profilMetierBack.getPermissionses()).containsOnly(permissionMetier);

        permissionMetier.setProfilses(new HashSet<>());
        assertThat(permissionMetier.getProfilses()).doesNotContain(profilMetierBack);
        assertThat(profilMetierBack.getPermissionses()).doesNotContain(permissionMetier);
    }
}
