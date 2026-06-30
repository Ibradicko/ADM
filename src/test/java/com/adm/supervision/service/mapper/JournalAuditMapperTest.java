package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.JournalAuditAsserts.*;
import static com.adm.supervision.domain.JournalAuditTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JournalAuditMapperTest {

    private JournalAuditMapper journalAuditMapper;

    @BeforeEach
    void setUp() {
        journalAuditMapper = new JournalAuditMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getJournalAuditSample1();
        var actual = journalAuditMapper.toEntity(journalAuditMapper.toDto(expected));
        assertJournalAuditAllPropertiesEquals(expected, actual);
    }
}
