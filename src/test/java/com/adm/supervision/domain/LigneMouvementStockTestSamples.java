package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LigneMouvementStockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static LigneMouvementStock getLigneMouvementStockSample1() {
        return new LigneMouvementStock().id(1L).commentaire("commentaire1");
    }

    public static LigneMouvementStock getLigneMouvementStockSample2() {
        return new LigneMouvementStock().id(2L).commentaire("commentaire2");
    }

    public static LigneMouvementStock getLigneMouvementStockRandomSampleGenerator() {
        return new LigneMouvementStock().id(longCount.incrementAndGet()).commentaire(UUID.randomUUID().toString());
    }
}
