package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LigneTransfertStockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static LigneTransfertStock getLigneTransfertStockSample1() {
        return new LigneTransfertStock().id(1L).commentaire("commentaire1");
    }

    public static LigneTransfertStock getLigneTransfertStockSample2() {
        return new LigneTransfertStock().id(2L).commentaire("commentaire2");
    }

    public static LigneTransfertStock getLigneTransfertStockRandomSampleGenerator() {
        return new LigneTransfertStock().id(longCount.incrementAndGet()).commentaire(UUID.randomUUID().toString());
    }
}
