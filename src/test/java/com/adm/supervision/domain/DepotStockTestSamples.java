package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DepotStockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static DepotStock getDepotStockSample1() {
        return new DepotStock().id(1L).code("code1").libelle("libelle1").emplacement("emplacement1");
    }

    public static DepotStock getDepotStockSample2() {
        return new DepotStock().id(2L).code("code2").libelle("libelle2").emplacement("emplacement2");
    }

    public static DepotStock getDepotStockRandomSampleGenerator() {
        return new DepotStock()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .libelle(UUID.randomUUID().toString())
            .emplacement(UUID.randomUUID().toString());
    }
}
