package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.RegularisationRedevanceAsserts.*;
import static com.adm.supervision.domain.RegularisationRedevanceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegularisationRedevanceMapperTest {

    private RegularisationRedevanceMapper regularisationRedevanceMapper;

    @BeforeEach
    void setUp() {
        regularisationRedevanceMapper = new RegularisationRedevanceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRegularisationRedevanceSample1();
        var actual = regularisationRedevanceMapper.toEntity(regularisationRedevanceMapper.toDto(expected));
        assertRegularisationRedevanceAllPropertiesEquals(expected, actual);
    }
}
