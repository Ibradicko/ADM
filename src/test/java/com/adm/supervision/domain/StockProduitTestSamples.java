package com.adm.supervision.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class StockProduitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static StockProduit getStockProduitSample1() {
        return new StockProduit().id(1L);
    }

    public static StockProduit getStockProduitSample2() {
        return new StockProduit().id(2L);
    }

    public static StockProduit getStockProduitRandomSampleGenerator() {
        return new StockProduit().id(longCount.incrementAndGet());
    }
}
