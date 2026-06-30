package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LigneReceptionProduitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static LigneReceptionProduit getLigneReceptionProduitSample1() {
        return new LigneReceptionProduit().id(1L).codeBarresScanne("codeBarresScanne1");
    }

    public static LigneReceptionProduit getLigneReceptionProduitSample2() {
        return new LigneReceptionProduit().id(2L).codeBarresScanne("codeBarresScanne2");
    }

    public static LigneReceptionProduit getLigneReceptionProduitRandomSampleGenerator() {
        return new LigneReceptionProduit().id(longCount.incrementAndGet()).codeBarresScanne(UUID.randomUUID().toString());
    }
}
