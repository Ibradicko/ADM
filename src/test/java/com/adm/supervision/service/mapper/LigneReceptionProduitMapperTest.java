package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.LigneReceptionProduitAsserts.*;
import static com.adm.supervision.domain.LigneReceptionProduitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LigneReceptionProduitMapperTest {

    private LigneReceptionProduitMapper ligneReceptionProduitMapper;

    @BeforeEach
    void setUp() {
        ligneReceptionProduitMapper = new LigneReceptionProduitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLigneReceptionProduitSample1();
        var actual = ligneReceptionProduitMapper.toEntity(ligneReceptionProduitMapper.toDto(expected));
        assertLigneReceptionProduitAllPropertiesEquals(expected, actual);
    }
}
