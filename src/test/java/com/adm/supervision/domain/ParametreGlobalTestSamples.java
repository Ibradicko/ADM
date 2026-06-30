package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ParametreGlobalTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ParametreGlobal getParametreGlobalSample1() {
        return new ParametreGlobal().id(1L).code("code1").valeur("valeur1");
    }

    public static ParametreGlobal getParametreGlobalSample2() {
        return new ParametreGlobal().id(2L).code("code2").valeur("valeur2");
    }

    public static ParametreGlobal getParametreGlobalRandomSampleGenerator() {
        return new ParametreGlobal()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .valeur(UUID.randomUUID().toString());
    }
}
