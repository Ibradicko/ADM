package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TicketCaisseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TicketCaisse getTicketCaisseSample1() {
        return new TicketCaisse().id(1L).numero("numero1").nombreImpressions(1);
    }

    public static TicketCaisse getTicketCaisseSample2() {
        return new TicketCaisse().id(2L).numero("numero2").nombreImpressions(2);
    }

    public static TicketCaisse getTicketCaisseRandomSampleGenerator() {
        return new TicketCaisse()
            .id(longCount.incrementAndGet())
            .numero(UUID.randomUUID().toString())
            .nombreImpressions(intCount.incrementAndGet());
    }
}
