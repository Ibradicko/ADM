package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.EtiquetteProduitAsserts.*;
import static com.adm.supervision.domain.EtiquetteProduitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EtiquetteProduitMapperTest {

    private EtiquetteProduitMapper etiquetteProduitMapper;

    @BeforeEach
    void setUp() {
        etiquetteProduitMapper = new EtiquetteProduitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEtiquetteProduitSample1();
        var actual = etiquetteProduitMapper.toEntity(etiquetteProduitMapper.toDto(expected));
        assertEtiquetteProduitAllPropertiesEquals(expected, actual);
    }
}
