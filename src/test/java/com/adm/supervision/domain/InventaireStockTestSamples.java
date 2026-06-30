package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InventaireStockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static InventaireStock getInventaireStockSample1() {
        return new InventaireStock().id(1L).reference("reference1");
    }

    public static InventaireStock getInventaireStockSample2() {
        return new InventaireStock().id(2L).reference("reference2");
    }

    public static InventaireStock getInventaireStockRandomSampleGenerator() {
        return new InventaireStock().id(longCount.incrementAndGet()).reference(UUID.randomUUID().toString());
    }
}
