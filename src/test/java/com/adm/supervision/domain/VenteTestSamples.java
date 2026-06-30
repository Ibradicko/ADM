package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class VenteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Vente getVenteSample1() {
        return new Vente()
            .id(1L)
            .numeroTicket("numeroTicket1")
            .referencePassager("referencePassager1")
            .referenceCarteEmbarquement("referenceCarteEmbarquement1")
            .commentaire("commentaire1");
    }

    public static Vente getVenteSample2() {
        return new Vente()
            .id(2L)
            .numeroTicket("numeroTicket2")
            .referencePassager("referencePassager2")
            .referenceCarteEmbarquement("referenceCarteEmbarquement2")
            .commentaire("commentaire2");
    }

    public static Vente getVenteRandomSampleGenerator() {
        return new Vente()
            .id(longCount.incrementAndGet())
            .numeroTicket(UUID.randomUUID().toString())
            .referencePassager(UUID.randomUUID().toString())
            .referenceCarteEmbarquement(UUID.randomUUID().toString())
            .commentaire(UUID.randomUUID().toString());
    }
}
