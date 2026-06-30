package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.ProfilMetierAsserts.*;
import static com.adm.supervision.domain.ProfilMetierTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfilMetierMapperTest {

    private ProfilMetierMapper profilMetierMapper;

    @BeforeEach
    void setUp() {
        profilMetierMapper = new ProfilMetierMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProfilMetierSample1();
        var actual = profilMetierMapper.toEntity(profilMetierMapper.toDto(expected));
        assertProfilMetierAllPropertiesEquals(expected, actual);
    }
}
