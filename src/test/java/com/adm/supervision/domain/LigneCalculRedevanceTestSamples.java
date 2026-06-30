package com.adm.supervision.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class LigneCalculRedevanceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static LigneCalculRedevance getLigneCalculRedevanceSample1() {
        return new LigneCalculRedevance().id(1L);
    }

    public static LigneCalculRedevance getLigneCalculRedevanceSample2() {
        return new LigneCalculRedevance().id(2L);
    }

    public static LigneCalculRedevance getLigneCalculRedevanceRandomSampleGenerator() {
        return new LigneCalculRedevance().id(longCount.incrementAndGet());
    }
}
