package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.RegleRedevanceAsserts.*;
import static com.adm.supervision.domain.RegleRedevanceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegleRedevanceMapperTest {

    private RegleRedevanceMapper regleRedevanceMapper;

    @BeforeEach
    void setUp() {
        regleRedevanceMapper = new RegleRedevanceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRegleRedevanceSample1();
        var actual = regleRedevanceMapper.toEntity(regleRedevanceMapper.toDto(expected));
        assertRegleRedevanceAllPropertiesEquals(expected, actual);
    }
}
