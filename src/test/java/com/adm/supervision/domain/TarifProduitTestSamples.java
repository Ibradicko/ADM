package com.adm.supervision.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TarifProduitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static TarifProduit getTarifProduitSample1() {
        return new TarifProduit().id(1L);
    }

    public static TarifProduit getTarifProduitSample2() {
        return new TarifProduit().id(2L);
    }

    public static TarifProduit getTarifProduitRandomSampleGenerator() {
        return new TarifProduit().id(longCount.incrementAndGet());
    }
}
