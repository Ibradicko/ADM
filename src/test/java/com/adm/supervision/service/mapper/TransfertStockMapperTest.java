package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.TransfertStockAsserts.*;
import static com.adm.supervision.domain.TransfertStockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransfertStockMapperTest {

    private TransfertStockMapper transfertStockMapper;

    @BeforeEach
    void setUp() {
        transfertStockMapper = new TransfertStockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTransfertStockSample1();
        var actual = transfertStockMapper.toEntity(transfertStockMapper.toDto(expected));
        assertTransfertStockAllPropertiesEquals(expected, actual);
    }
}
