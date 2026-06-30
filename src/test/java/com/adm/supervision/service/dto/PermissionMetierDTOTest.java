package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PermissionMetierDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PermissionMetierDTO.class);
        PermissionMetierDTO permissionMetierDTO1 = new PermissionMetierDTO();
        permissionMetierDTO1.setId(1L);
        PermissionMetierDTO permissionMetierDTO2 = new PermissionMetierDTO();
        assertThat(permissionMetierDTO1).isNotEqualTo(permissionMetierDTO2);
        permissionMetierDTO2.setId(permissionMetierDTO1.getId());
        assertThat(permissionMetierDTO1).isEqualTo(permissionMetierDTO2);
        permissionMetierDTO2.setId(2L);
        assertThat(permissionMetierDTO1).isNotEqualTo(permissionMetierDTO2);
        permissionMetierDTO1.setId(null);
        assertThat(permissionMetierDTO1).isNotEqualTo(permissionMetierDTO2);
    }
}
