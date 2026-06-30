package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RegleRedevanceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static RegleRedevance getRegleRedevanceSample1() {
        return new RegleRedevance().id(1L).code("code1").priorite(1);
    }

    public static RegleRedevance getRegleRedevanceSample2() {
        return new RegleRedevance().id(2L).code("code2").priorite(2);
    }

    public static RegleRedevance getRegleRedevanceRandomSampleGenerator() {
        return new RegleRedevance().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).priorite(intCount.incrementAndGet());
    }
}
