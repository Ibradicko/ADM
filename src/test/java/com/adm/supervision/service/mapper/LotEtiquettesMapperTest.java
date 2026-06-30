package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.LotEtiquettesAsserts.*;
import static com.adm.supervision.domain.LotEtiquettesTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LotEtiquettesMapperTest {

    private LotEtiquettesMapper lotEtiquettesMapper;

    @BeforeEach
    void setUp() {
        lotEtiquettesMapper = new LotEtiquettesMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLotEtiquettesSample1();
        var actual = lotEtiquettesMapper.toEntity(lotEtiquettesMapper.toDto(expected));
        assertLotEtiquettesAllPropertiesEquals(expected, actual);
    }
}
