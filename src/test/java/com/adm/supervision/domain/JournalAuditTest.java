package com.adm.supervision.domain;

import static com.adm.supervision.domain.BoutiqueTestSamples.*;
import static com.adm.supervision.domain.JournalAuditTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class JournalAuditTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JournalAudit.class);
        JournalAudit journalAudit1 = getJournalAuditSample1();
        JournalAudit journalAudit2 = new JournalAudit();
        assertThat(journalAudit1).isNotEqualTo(journalAudit2);

        journalAudit2.setId(journalAudit1.getId());
        assertThat(journalAudit1).isEqualTo(journalAudit2);

        journalAudit2 = getJournalAuditSample2();
        assertThat(journalAudit1).isNotEqualTo(journalAudit2);
    }

    @Test
    void boutiqueTest() {
        JournalAudit journalAudit = getJournalAuditRandomSampleGenerator();
        Boutique boutiqueBack = getBoutiqueRandomSampleGenerator();

        journalAudit.setBoutique(boutiqueBack);
        assertThat(journalAudit.getBoutique()).isEqualTo(boutiqueBack);

        journalAudit.boutique(null);
        assertThat(journalAudit.getBoutique()).isNull();
    }
}
