package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.DepotStockAsserts.*;
import static com.adm.supervision.domain.DepotStockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DepotStockMapperTest {

    private DepotStockMapper depotStockMapper;

    @BeforeEach
    void setUp() {
        depotStockMapper = new DepotStockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDepotStockSample1();
        var actual = depotStockMapper.toEntity(depotStockMapper.toDto(expected));
        assertDepotStockAllPropertiesEquals(expected, actual);
    }
}
