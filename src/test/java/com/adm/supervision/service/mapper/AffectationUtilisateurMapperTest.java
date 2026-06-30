package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.AffectationUtilisateurAsserts.*;
import static com.adm.supervision.domain.AffectationUtilisateurTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AffectationUtilisateurMapperTest {

    private AffectationUtilisateurMapper affectationUtilisateurMapper;

    @BeforeEach
    void setUp() {
        affectationUtilisateurMapper = new AffectationUtilisateurMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAffectationUtilisateurSample1();
        var actual = affectationUtilisateurMapper.toEntity(affectationUtilisateurMapper.toDto(expected));
        assertAffectationUtilisateurAllPropertiesEquals(expected, actual);
    }
}
