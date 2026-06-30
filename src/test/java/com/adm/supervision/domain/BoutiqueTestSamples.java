package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BoutiqueTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Boutique getBoutiqueSample1() {
        return new Boutique().id(1L).code("code1").nom("nom1").emplacement("emplacement1").telephone("telephone1");
    }

    public static Boutique getBoutiqueSample2() {
        return new Boutique().id(2L).code("code2").nom("nom2").emplacement("emplacement2").telephone("telephone2");
    }

    public static Boutique getBoutiqueRandomSampleGenerator() {
        return new Boutique()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .nom(UUID.randomUUID().toString())
            .emplacement(UUID.randomUUID().toString())
            .telephone(UUID.randomUUID().toString());
    }
}
