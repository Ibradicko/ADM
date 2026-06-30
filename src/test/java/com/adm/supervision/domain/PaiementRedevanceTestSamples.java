package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaiementRedevanceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static PaiementRedevance getPaiementRedevanceSample1() {
        return new PaiementRedevance().id(1L).reference("reference1").modePaiement("modePaiement1").commentaire("commentaire1");
    }

    public static PaiementRedevance getPaiementRedevanceSample2() {
        return new PaiementRedevance().id(2L).reference("reference2").modePaiement("modePaiement2").commentaire("commentaire2");
    }

    public static PaiementRedevance getPaiementRedevanceRandomSampleGenerator() {
        return new PaiementRedevance()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .modePaiement(UUID.randomUUID().toString())
            .commentaire(UUID.randomUUID().toString());
    }
}
