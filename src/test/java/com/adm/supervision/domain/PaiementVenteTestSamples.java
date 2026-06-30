package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaiementVenteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static PaiementVente getPaiementVenteSample1() {
        return new PaiementVente().id(1L).referencePaiement("referencePaiement1");
    }

    public static PaiementVente getPaiementVenteSample2() {
        return new PaiementVente().id(2L).referencePaiement("referencePaiement2");
    }

    public static PaiementVente getPaiementVenteRandomSampleGenerator() {
        return new PaiementVente().id(longCount.incrementAndGet()).referencePaiement(UUID.randomUUID().toString());
    }
}
