package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.CodeBarresProduitAsserts.*;
import static com.adm.supervision.domain.CodeBarresProduitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CodeBarresProduitMapperTest {

    private CodeBarresProduitMapper codeBarresProduitMapper;

    @BeforeEach
    void setUp() {
        codeBarresProduitMapper = new CodeBarresProduitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCodeBarresProduitSample1();
        var actual = codeBarresProduitMapper.toEntity(codeBarresProduitMapper.toDto(expected));
        assertCodeBarresProduitAllPropertiesEquals(expected, actual);
    }
}
