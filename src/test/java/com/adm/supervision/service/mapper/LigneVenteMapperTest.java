package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.LigneVenteAsserts.*;
import static com.adm.supervision.domain.LigneVenteTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LigneVenteMapperTest {

    private LigneVenteMapper ligneVenteMapper;

    @BeforeEach
    void setUp() {
        ligneVenteMapper = new LigneVenteMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLigneVenteSample1();
        var actual = ligneVenteMapper.toEntity(ligneVenteMapper.toDto(expected));
        assertLigneVenteAllPropertiesEquals(expected, actual);
    }
}
