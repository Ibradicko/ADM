package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LotEtiquettesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static LotEtiquettes getLotEtiquettesSample1() {
        return new LotEtiquettes().id(1L).reference("reference1").formatImpression("formatImpression1").nombreEtiquettes(1);
    }

    public static LotEtiquettes getLotEtiquettesSample2() {
        return new LotEtiquettes().id(2L).reference("reference2").formatImpression("formatImpression2").nombreEtiquettes(2);
    }

    public static LotEtiquettes getLotEtiquettesRandomSampleGenerator() {
        return new LotEtiquettes()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .formatImpression(UUID.randomUUID().toString())
            .nombreEtiquettes(intCount.incrementAndGet());
    }
}
