package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.LigneTransfertStockAsserts.*;
import static com.adm.supervision.domain.LigneTransfertStockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LigneTransfertStockMapperTest {

    private LigneTransfertStockMapper ligneTransfertStockMapper;

    @BeforeEach
    void setUp() {
        ligneTransfertStockMapper = new LigneTransfertStockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLigneTransfertStockSample1();
        var actual = ligneTransfertStockMapper.toEntity(ligneTransfertStockMapper.toDto(expected));
        assertLigneTransfertStockAllPropertiesEquals(expected, actual);
    }
}
