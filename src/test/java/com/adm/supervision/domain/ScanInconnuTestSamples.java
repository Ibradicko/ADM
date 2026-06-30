package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ScanInconnuTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ScanInconnu getScanInconnuSample1() {
        return new ScanInconnu().id(1L).codeScanne("codeScanne1").ecranOrigine("ecranOrigine1").commentaire("commentaire1");
    }

    public static ScanInconnu getScanInconnuSample2() {
        return new ScanInconnu().id(2L).codeScanne("codeScanne2").ecranOrigine("ecranOrigine2").commentaire("commentaire2");
    }

    public static ScanInconnu getScanInconnuRandomSampleGenerator() {
        return new ScanInconnu()
            .id(longCount.incrementAndGet())
            .codeScanne(UUID.randomUUID().toString())
            .ecranOrigine(UUID.randomUUID().toString())
            .commentaire(UUID.randomUUID().toString());
    }
}
