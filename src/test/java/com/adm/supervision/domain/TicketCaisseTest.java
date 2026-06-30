package com.adm.supervision.domain;

import static com.adm.supervision.domain.TicketCaisseTestSamples.*;
import static com.adm.supervision.domain.VenteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.adm.supervision.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TicketCaisseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TicketCaisse.class);
        TicketCaisse ticketCaisse1 = getTicketCaisseSample1();
        TicketCaisse ticketCaisse2 = new TicketCaisse();
        assertThat(ticketCaisse1).isNotEqualTo(ticketCaisse2);

        ticketCaisse2.setId(ticketCaisse1.getId());
        assertThat(ticketCaisse1).isEqualTo(ticketCaisse2);

        ticketCaisse2 = getTicketCaisseSample2();
        assertThat(ticketCaisse1).isNotEqualTo(ticketCaisse2);
    }

    @Test
    void venteTest() {
        TicketCaisse ticketCaisse = getTicketCaisseRandomSampleGenerator();
        Vente venteBack = getVenteRandomSampleGenerator();

        ticketCaisse.setVente(venteBack);
        assertThat(ticketCaisse.getVente()).isEqualTo(venteBack);

        ticketCaisse.vente(null);
        assertThat(ticketCaisse.getVente()).isNull();
    }
}
