package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CalculRedevanceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static CalculRedevance getCalculRedevanceSample1() {
        return new CalculRedevance().id(1L).reference("reference1");
    }

    public static CalculRedevance getCalculRedevanceSample2() {
        return new CalculRedevance().id(2L).reference("reference2");
    }

    public static CalculRedevance getCalculRedevanceRandomSampleGenerator() {
        return new CalculRedevance().id(longCount.incrementAndGet()).reference(UUID.randomUUID().toString());
    }
}
