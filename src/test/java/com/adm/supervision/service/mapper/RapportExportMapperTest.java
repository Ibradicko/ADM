package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.RapportExportAsserts.*;
import static com.adm.supervision.domain.RapportExportTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RapportExportMapperTest {

    private RapportExportMapper rapportExportMapper;

    @BeforeEach
    void setUp() {
        rapportExportMapper = new RapportExportMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRapportExportSample1();
        var actual = rapportExportMapper.toEntity(rapportExportMapper.toDto(expected));
        assertRapportExportAllPropertiesEquals(expected, actual);
    }
}
