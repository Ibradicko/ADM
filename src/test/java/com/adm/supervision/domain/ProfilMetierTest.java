package com.adm.supervision.domain;

import static com.adm.supervision.domain.PermissionMetierTestSamples.*;
import static com.adm.supervision.domain.ProfilMetierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProfilMetierTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProfilMetier.class);
        ProfilMetier profilMetier1 = getProfilMetierSample1();
        ProfilMetier profilMetier2 = new ProfilMetier();
        assertThat(profilMetier1).isNotEqualTo(profilMetier2);

        profilMetier2.setId(profilMetier1.getId());
        assertThat(profilMetier1).isEqualTo(profilMetier2);

        profilMetier2 = getProfilMetierSample2();
        assertThat(profilMetier1).isNotEqualTo(profilMetier2);
    }

    @Test
    void permissionsTest() {
        ProfilMetier profilMetier = getProfilMetierRandomSampleGenerator();
        PermissionMetier permissionMetierBack = getPermissionMetierRandomSampleGenerator();

        profilMetier.addPermissions(permissionMetierBack);
        assertThat(profilMetier.getPermissionses()).containsOnly(permissionMetierBack);

        profilMetier.removePermissions(permissionMetierBack);
        assertThat(profilMetier.getPermissionses()).doesNotContain(permissionMetierBack);

        profilMetier.permissionses(new HashSet<>(Set.of(permissionMetierBack)));
        assertThat(profilMetier.getPermissionses()).containsOnly(permissionMetierBack);

        profilMetier.setPermissionses(new HashSet<>());
        assertThat(profilMetier.getPermissionses()).doesNotContain(permissionMetierBack);
    }
}
