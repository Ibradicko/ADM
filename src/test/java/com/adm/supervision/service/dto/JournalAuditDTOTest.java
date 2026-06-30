package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class JournalAuditDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(JournalAuditDTO.class);
        JournalAuditDTO journalAuditDTO1 = new JournalAuditDTO();
        journalAuditDTO1.setId(1L);
        JournalAuditDTO journalAuditDTO2 = new JournalAuditDTO();
        assertThat(journalAuditDTO1).isNotEqualTo(journalAuditDTO2);
        journalAuditDTO2.setId(journalAuditDTO1.getId());
        assertThat(journalAuditDTO1).isEqualTo(journalAuditDTO2);
        journalAuditDTO2.setId(2L);
        assertThat(journalAuditDTO1).isNotEqualTo(journalAuditDTO2);
        journalAuditDTO1.setId(null);
        assertThat(journalAuditDTO1).isNotEqualTo(journalAuditDTO2);
    }
}
