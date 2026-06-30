package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ModePaiementRefTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ModePaiementRef getModePaiementRefSample1() {
        return new ModePaiementRef().id(1L).code("code1").libelle("libelle1");
    }

    public static ModePaiementRef getModePaiementRefSample2() {
        return new ModePaiementRef().id(2L).code("code2").libelle("libelle2");
    }

    public static ModePaiementRef getModePaiementRefRandomSampleGenerator() {
        return new ModePaiementRef()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .libelle(UUID.randomUUID().toString());
    }
}
