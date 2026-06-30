package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.LigneMouvementStockAsserts.*;
import static com.adm.supervision.domain.LigneMouvementStockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LigneMouvementStockMapperTest {

    private LigneMouvementStockMapper ligneMouvementStockMapper;

    @BeforeEach
    void setUp() {
        ligneMouvementStockMapper = new LigneMouvementStockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLigneMouvementStockSample1();
        var actual = ligneMouvementStockMapper.toEntity(ligneMouvementStockMapper.toDto(expected));
        assertLigneMouvementStockAllPropertiesEquals(expected, actual);
    }
}
