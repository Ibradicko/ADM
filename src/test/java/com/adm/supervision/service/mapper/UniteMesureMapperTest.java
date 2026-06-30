package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.UniteMesureAsserts.*;
import static com.adm.supervision.domain.UniteMesureTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UniteMesureMapperTest {

    private UniteMesureMapper uniteMesureMapper;

    @BeforeEach
    void setUp() {
        uniteMesureMapper = new UniteMesureMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUniteMesureSample1();
        var actual = uniteMesureMapper.toEntity(uniteMesureMapper.toDto(expected));
        assertUniteMesureAllPropertiesEquals(expected, actual);
    }
}
