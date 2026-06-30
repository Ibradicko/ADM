package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TransfertStockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static TransfertStock getTransfertStockSample1() {
        return new TransfertStock().id(1L).reference("reference1").motif("motif1");
    }

    public static TransfertStock getTransfertStockSample2() {
        return new TransfertStock().id(2L).reference("reference2").motif("motif2");
    }

    public static TransfertStock getTransfertStockRandomSampleGenerator() {
        return new TransfertStock()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .motif(UUID.randomUUID().toString());
    }
}
