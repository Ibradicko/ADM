package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.BoutiqueAsserts.*;
import static com.adm.supervision.domain.BoutiqueTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoutiqueMapperTest {

    private BoutiqueMapper boutiqueMapper;

    @BeforeEach
    void setUp() {
        boutiqueMapper = new BoutiqueMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBoutiqueSample1();
        var actual = boutiqueMapper.toEntity(boutiqueMapper.toDto(expected));
        assertBoutiqueAllPropertiesEquals(expected, actual);
    }
}
