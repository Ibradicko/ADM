package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OperationCorrectiveVenteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static OperationCorrectiveVente getOperationCorrectiveVenteSample1() {
        return new OperationCorrectiveVente().id(1L).motif("motif1");
    }

    public static OperationCorrectiveVente getOperationCorrectiveVenteSample2() {
        return new OperationCorrectiveVente().id(2L).motif("motif2");
    }

    public static OperationCorrectiveVente getOperationCorrectiveVenteRandomSampleGenerator() {
        return new OperationCorrectiveVente().id(longCount.incrementAndGet()).motif(UUID.randomUUID().toString());
    }
}
