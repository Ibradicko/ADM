package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CodeBarresProduitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static CodeBarresProduit getCodeBarresProduitSample1() {
        return new CodeBarresProduit().id(1L).code("code1");
    }

    public static CodeBarresProduit getCodeBarresProduitSample2() {
        return new CodeBarresProduit().id(2L).code("code2");
    }

    public static CodeBarresProduit getCodeBarresProduitRandomSampleGenerator() {
        return new CodeBarresProduit().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString());
    }
}
