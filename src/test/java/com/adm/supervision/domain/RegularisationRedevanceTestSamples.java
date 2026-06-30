package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RegularisationRedevanceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static RegularisationRedevance getRegularisationRedevanceSample1() {
        return new RegularisationRedevance().id(1L).reference("reference1").motif("motif1");
    }

    public static RegularisationRedevance getRegularisationRedevanceSample2() {
        return new RegularisationRedevance().id(2L).reference("reference2").motif("motif2");
    }

    public static RegularisationRedevance getRegularisationRedevanceRandomSampleGenerator() {
        return new RegularisationRedevance()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .motif(UUID.randomUUID().toString());
    }
}
