package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.PaiementRedevanceAsserts.*;
import static com.adm.supervision.domain.PaiementRedevanceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaiementRedevanceMapperTest {

    private PaiementRedevanceMapper paiementRedevanceMapper;

    @BeforeEach
    void setUp() {
        paiementRedevanceMapper = new PaiementRedevanceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPaiementRedevanceSample1();
        var actual = paiementRedevanceMapper.toEntity(paiementRedevanceMapper.toDto(expected));
        assertPaiementRedevanceAllPropertiesEquals(expected, actual);
    }
}
