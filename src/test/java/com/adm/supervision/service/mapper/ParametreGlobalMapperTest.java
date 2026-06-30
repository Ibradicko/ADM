package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.ParametreGlobalAsserts.*;
import static com.adm.supervision.domain.ParametreGlobalTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParametreGlobalMapperTest {

    private ParametreGlobalMapper parametreGlobalMapper;

    @BeforeEach
    void setUp() {
        parametreGlobalMapper = new ParametreGlobalMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getParametreGlobalSample1();
        var actual = parametreGlobalMapper.toEntity(parametreGlobalMapper.toDto(expected));
        assertParametreGlobalAllPropertiesEquals(expected, actual);
    }
}
