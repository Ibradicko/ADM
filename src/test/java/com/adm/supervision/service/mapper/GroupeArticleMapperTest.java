package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.GroupeArticleAsserts.*;
import static com.adm.supervision.domain.GroupeArticleTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GroupeArticleMapperTest {

    private GroupeArticleMapper groupeArticleMapper;

    @BeforeEach
    void setUp() {
        groupeArticleMapper = new GroupeArticleMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getGroupeArticleSample1();
        var actual = groupeArticleMapper.toEntity(groupeArticleMapper.toDto(expected));
        assertGroupeArticleAllPropertiesEquals(expected, actual);
    }
}
