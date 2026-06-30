package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.LigneInventaireStockAsserts.*;
import static com.adm.supervision.domain.LigneInventaireStockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LigneInventaireStockMapperTest {

    private LigneInventaireStockMapper ligneInventaireStockMapper;

    @BeforeEach
    void setUp() {
        ligneInventaireStockMapper = new LigneInventaireStockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLigneInventaireStockSample1();
        var actual = ligneInventaireStockMapper.toEntity(ligneInventaireStockMapper.toDto(expected));
        assertLigneInventaireStockAllPropertiesEquals(expected, actual);
    }
}
