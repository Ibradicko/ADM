package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.LocataireAsserts.*;
import static com.adm.supervision.domain.LocataireTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LocataireMapperTest {

    private LocataireMapper locataireMapper;

    @BeforeEach
    void setUp() {
        locataireMapper = new LocataireMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLocataireSample1();
        var actual = locataireMapper.toEntity(locataireMapper.toDto(expected));
        assertLocataireAllPropertiesEquals(expected, actual);
    }
}
