package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.InventaireStockAsserts.*;
import static com.adm.supervision.domain.InventaireStockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventaireStockMapperTest {

    private InventaireStockMapper inventaireStockMapper;

    @BeforeEach
    void setUp() {
        inventaireStockMapper = new InventaireStockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInventaireStockSample1();
        var actual = inventaireStockMapper.toEntity(inventaireStockMapper.toDto(expected));
        assertInventaireStockAllPropertiesEquals(expected, actual);
    }
}
