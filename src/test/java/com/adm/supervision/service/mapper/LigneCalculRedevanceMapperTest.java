package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.LigneCalculRedevanceAsserts.*;
import static com.adm.supervision.domain.LigneCalculRedevanceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LigneCalculRedevanceMapperTest {

    private LigneCalculRedevanceMapper ligneCalculRedevanceMapper;

    @BeforeEach
    void setUp() {
        ligneCalculRedevanceMapper = new LigneCalculRedevanceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLigneCalculRedevanceSample1();
        var actual = ligneCalculRedevanceMapper.toEntity(ligneCalculRedevanceMapper.toDto(expected));
        assertLigneCalculRedevanceAllPropertiesEquals(expected, actual);
    }
}
