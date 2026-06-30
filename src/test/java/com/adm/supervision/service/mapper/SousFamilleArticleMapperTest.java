package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.SousFamilleArticleAsserts.*;
import static com.adm.supervision.domain.SousFamilleArticleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SousFamilleArticleMapperTest {

    private SousFamilleArticleMapper sousFamilleArticleMapper;

    @BeforeEach
    void setUp() {
        sousFamilleArticleMapper = new SousFamilleArticleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSousFamilleArticleSample1();
        var actual = sousFamilleArticleMapper.toEntity(sousFamilleArticleMapper.toDto(expected));
        assertSousFamilleArticleAllPropertiesEquals(expected, actual);
    }
}
