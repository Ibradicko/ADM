package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.OperationCorrectiveVenteAsserts.*;
import static com.adm.supervision.domain.OperationCorrectiveVenteTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OperationCorrectiveVenteMapperTest {

    private OperationCorrectiveVenteMapper operationCorrectiveVenteMapper;

    @BeforeEach
    void setUp() {
        operationCorrectiveVenteMapper = new OperationCorrectiveVenteMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getOperationCorrectiveVenteSample1();
        var actual = operationCorrectiveVenteMapper.toEntity(operationCorrectiveVenteMapper.toDto(expected));
        assertOperationCorrectiveVenteAllPropertiesEquals(expected, actual);
    }
}
