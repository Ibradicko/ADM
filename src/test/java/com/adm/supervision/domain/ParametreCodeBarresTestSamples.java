package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ParametreCodeBarresTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ParametreCodeBarres getParametreCodeBarresSample1() {
        return new ParametreCodeBarres().id(1L).prefixe("prefixe1").longueur(1);
    }

    public static ParametreCodeBarres getParametreCodeBarresSample2() {
        return new ParametreCodeBarres().id(2L).prefixe("prefixe2").longueur(2);
    }

    public static ParametreCodeBarres getParametreCodeBarresRandomSampleGenerator() {
        return new ParametreCodeBarres()
            .id(longCount.incrementAndGet())
            .prefixe(UUID.randomUUID().toString())
            .longueur(intCount.incrementAndGet());
    }
}
