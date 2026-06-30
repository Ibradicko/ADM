package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MouvementStockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static MouvementStock getMouvementStockSample1() {
        return new MouvementStock().id(1L).reference("reference1").motif("motif1");
    }

    public static MouvementStock getMouvementStockSample2() {
        return new MouvementStock().id(2L).reference("reference2").motif("motif2");
    }

    public static MouvementStock getMouvementStockRandomSampleGenerator() {
        return new MouvementStock()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .motif(UUID.randomUUID().toString());
    }
}
