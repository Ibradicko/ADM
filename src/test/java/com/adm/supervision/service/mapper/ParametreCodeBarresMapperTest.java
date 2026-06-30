package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.ParametreCodeBarresAsserts.*;
import static com.adm.supervision.domain.ParametreCodeBarresTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParametreCodeBarresMapperTest {

    private ParametreCodeBarresMapper parametreCodeBarresMapper;

    @BeforeEach
    void setUp() {
        parametreCodeBarresMapper = new ParametreCodeBarresMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getParametreCodeBarresSample1();
        var actual = parametreCodeBarresMapper.toEntity(parametreCodeBarresMapper.toDto(expected));
        assertParametreCodeBarresAllPropertiesEquals(expected, actual);
    }
}
