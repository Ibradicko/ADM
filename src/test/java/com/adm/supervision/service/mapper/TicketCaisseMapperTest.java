package com.adm.supervision.service.mapper;

import static com.adm.supervision.domain.TicketCaisseAsserts.*;
import static com.adm.supervision.domain.TicketCaisseTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TicketCaisseMapperTest {

    private TicketCaisseMapper ticketCaisseMapper;

    @BeforeEach
    void setUp() {
        ticketCaisseMapper = new TicketCaisseMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTicketCaisseSample1();
        var actual = ticketCaisseMapper.toEntity(ticketCaisseMapper.toDto(expected));
        assertTicketCaisseAllPropertiesEquals(expected, actual);
    }
}
