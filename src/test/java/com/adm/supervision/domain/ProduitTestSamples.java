package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProduitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Produit getProduitSample1() {
        return new Produit().id(1L).codeInterne("codeInterne1").designation("designation1");
    }

    public static Produit getProduitSample2() {
        return new Produit().id(2L).codeInterne("codeInterne2").designation("designation2");
    }

    public static Produit getProduitRandomSampleGenerator() {
        return new Produit()
            .id(longCount.incrementAndGet())
            .codeInterne(UUID.randomUUID().toString())
            .designation(UUID.randomUUID().toString());
    }
}
