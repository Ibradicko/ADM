package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.TarifProduitAsserts.*;
import static com.adm.supervision.domain.TarifProduitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TarifProduitMapperTest {

    private TarifProduitMapper tarifProduitMapper;

    @BeforeEach
    void setUp() {
        tarifProduitMapper = new TarifProduitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTarifProduitSample1();
        var actual = tarifProduitMapper.toEntity(tarifProduitMapper.toDto(expected));
        assertTarifProduitAllPropertiesEquals(expected, actual);
    }
}
