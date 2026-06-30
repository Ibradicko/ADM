package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.HistoriqueCodeBarresAsserts.*;
import static com.adm.supervision.domain.HistoriqueCodeBarresTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HistoriqueCodeBarresMapperTest {

    private HistoriqueCodeBarresMapper historiqueCodeBarresMapper;

    @BeforeEach
    void setUp() {
        historiqueCodeBarresMapper = new HistoriqueCodeBarresMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getHistoriqueCodeBarresSample1();
        var actual = historiqueCodeBarresMapper.toEntity(historiqueCodeBarresMapper.toDto(expected));
        assertHistoriqueCodeBarresAllPropertiesEquals(expected, actual);
    }
}
