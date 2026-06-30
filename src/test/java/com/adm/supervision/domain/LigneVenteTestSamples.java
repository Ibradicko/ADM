package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LigneVenteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static LigneVente getLigneVenteSample1() {
        return new LigneVente().id(1L).codeBarresScanne("codeBarresScanne1");
    }

    public static LigneVente getLigneVenteSample2() {
        return new LigneVente().id(2L).codeBarresScanne("codeBarresScanne2");
    }

    public static LigneVente getLigneVenteRandomSampleGenerator() {
        return new LigneVente().id(longCount.incrementAndGet()).codeBarresScanne(UUID.randomUUID().toString());
    }
}
