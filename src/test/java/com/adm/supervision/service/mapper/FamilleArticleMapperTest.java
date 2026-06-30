package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.FamilleArticleAsserts.*;
import static com.adm.supervision.domain.FamilleArticleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FamilleArticleMapperTest {

    private FamilleArticleMapper familleArticleMapper;

    @BeforeEach
    void setUp() {
        familleArticleMapper = new FamilleArticleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFamilleArticleSample1();
        var actual = familleArticleMapper.toEntity(familleArticleMapper.toDto(expected));
        assertFamilleArticleAllPropertiesEquals(expected, actual);
    }
}
