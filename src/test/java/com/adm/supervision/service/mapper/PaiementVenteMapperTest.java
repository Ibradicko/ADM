package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.PaiementVenteAsserts.*;
import static com.adm.supervision.domain.PaiementVenteTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaiementVenteMapperTest {

    private PaiementVenteMapper paiementVenteMapper;

    @BeforeEach
    void setUp() {
        paiementVenteMapper = new PaiementVenteMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPaiementVenteSample1();
        var actual = paiementVenteMapper.toEntity(paiementVenteMapper.toDto(expected));
        assertPaiementVenteAllPropertiesEquals(expected, actual);
    }
}
