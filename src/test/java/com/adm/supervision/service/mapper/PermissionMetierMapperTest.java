package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.PermissionMetierAsserts.*;
import static com.adm.supervision.domain.PermissionMetierTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermissionMetierMapperTest {

    private PermissionMetierMapper permissionMetierMapper;

    @BeforeEach
    void setUp() {
        permissionMetierMapper = new PermissionMetierMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPermissionMetierSample1();
        var actual = permissionMetierMapper.toEntity(permissionMetierMapper.toDto(expected));
        assertPermissionMetierAllPropertiesEquals(expected, actual);
    }
}
