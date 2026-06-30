package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.ScanInconnuAsserts.*;
import static com.adm.supervision.domain.ScanInconnuTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScanInconnuMapperTest {

    private ScanInconnuMapper scanInconnuMapper;

    @BeforeEach
    void setUp() {
        scanInconnuMapper = new ScanInconnuMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getScanInconnuSample1();
        var actual = scanInconnuMapper.toEntity(scanInconnuMapper.toDto(expected));
        assertScanInconnuAllPropertiesEquals(expected, actual);
    }
}
