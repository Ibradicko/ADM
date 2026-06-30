package com.adm.supervision.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketCaisseDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketCaisseDTO.class);
        TicketCaisseDTO ticketCaisseDTO1 = new TicketCaisseDTO();
        ticketCaisseDTO1.setId(1L);
        TicketCaisseDTO ticketCaisseDTO2 = new TicketCaisseDTO();
        assertThat(ticketCaisseDTO1).isNotEqualTo(ticketCaisseDTO2);
        ticketCaisseDTO2.setId(ticketCaisseDTO1.getId());
        assertThat(ticketCaisseDTO1).isEqualTo(ticketCaisseDTO2);
        ticketCaisseDTO2.setId(2L);
        assertThat(ticketCaisseDTO1).isNotEqualTo(ticketCaisseDTO2);
        ticketCaisseDTO1.setId(null);
        assertThat(ticketCaisseDTO1).isNotEqualTo(ticketCaisseDTO2);
    }
}
