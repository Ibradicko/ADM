package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.ModePaiementRefAsserts.*;
import static com.adm.supervision.domain.ModePaiementRefTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ModePaiementRefMapperTest {

    private ModePaiementRefMapper modePaiementRefMapper;

    @BeforeEach
    void setUp() {
        modePaiementRefMapper = new ModePaiementRefMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getModePaiementRefSample1();
        var actual = modePaiementRefMapper.toEntity(modePaiementRefMapper.toDto(expected));
        assertModePaiementRefAllPropertiesEquals(expected, actual);
    }
}
