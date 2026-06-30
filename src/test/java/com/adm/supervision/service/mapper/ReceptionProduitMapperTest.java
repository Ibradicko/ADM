package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.ReceptionProduitAsserts.*;
import static com.adm.supervision.domain.ReceptionProduitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReceptionProduitMapperTest {

    private ReceptionProduitMapper receptionProduitMapper;

    @BeforeEach
    void setUp() {
        receptionProduitMapper = new ReceptionProduitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReceptionProduitSample1();
        var actual = receptionProduitMapper.toEntity(receptionProduitMapper.toDto(expected));
        assertReceptionProduitAllPropertiesEquals(expected, actual);
    }
}
