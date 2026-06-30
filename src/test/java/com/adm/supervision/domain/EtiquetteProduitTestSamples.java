package com.adm.supervision.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EtiquetteProduitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static EtiquetteProduit getEtiquetteProduitSample1() {
        return new EtiquetteProduit().id(1L).quantite(1);
    }

    public static EtiquetteProduit getEtiquetteProduitSample2() {
        return new EtiquetteProduit().id(2L).quantite(2);
    }

    public static EtiquetteProduit getEtiquetteProduitRandomSampleGenerator() {
        return new EtiquetteProduit().id(longCount.incrementAndGet()).quantite(intCount.incrementAndGet());
    }
}
