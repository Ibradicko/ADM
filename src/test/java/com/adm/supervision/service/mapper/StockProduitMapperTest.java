package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.StockProduitAsserts.*;
import static com.adm.supervision.domain.StockProduitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockProduitMapperTest {

    private StockProduitMapper stockProduitMapper;

    @BeforeEach
    void setUp() {
        stockProduitMapper = new StockProduitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStockProduitSample1();
        var actual = stockProduitMapper.toEntity(stockProduitMapper.toDto(expected));
        assertStockProduitAllPropertiesEquals(expected, actual);
    }
}
