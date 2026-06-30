package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.CalculRedevanceAsserts.*;
import static com.adm.supervision.domain.CalculRedevanceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CalculRedevanceMapperTest {

    private CalculRedevanceMapper calculRedevanceMapper;

    @BeforeEach
    void setUp() {
        calculRedevanceMapper = new CalculRedevanceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCalculRedevanceSample1();
        var actual = calculRedevanceMapper.toEntity(calculRedevanceMapper.toDto(expected));
        assertCalculRedevanceAllPropertiesEquals(expected, actual);
    }
}
