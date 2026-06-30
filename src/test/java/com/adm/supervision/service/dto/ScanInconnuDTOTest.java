package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScanInconnuDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScanInconnuDTO.class);
        ScanInconnuDTO scanInconnuDTO1 = new ScanInconnuDTO();
        scanInconnuDTO1.setId(1L);
        ScanInconnuDTO scanInconnuDTO2 = new ScanInconnuDTO();
        assertThat(scanInconnuDTO1).isNotEqualTo(scanInconnuDTO2);
        scanInconnuDTO2.setId(scanInconnuDTO1.getId());
        assertThat(scanInconnuDTO1).isEqualTo(scanInconnuDTO2);
        scanInconnuDTO2.setId(2L);
        assertThat(scanInconnuDTO1).isNotEqualTo(scanInconnuDTO2);
        scanInconnuDTO1.setId(null);
        assertThat(scanInconnuDTO1).isNotEqualTo(scanInconnuDTO2);
    }
}
