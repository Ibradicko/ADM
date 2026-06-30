package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.MouvementStockAsserts.*;
import static com.adm.supervision.domain.MouvementStockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MouvementStockMapperTest {

    private MouvementStockMapper mouvementStockMapper;

    @BeforeEach
    void setUp() {
        mouvementStockMapper = new MouvementStockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMouvementStockSample1();
        var actual = mouvementStockMapper.toEntity(mouvementStockMapper.toDto(expected));
        assertMouvementStockAllPropertiesEquals(expected, actual);
    }
}
