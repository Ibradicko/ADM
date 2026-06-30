package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class HistoriqueCodeBarresTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static HistoriqueCodeBarres getHistoriqueCodeBarresSample1() {
        return new HistoriqueCodeBarres().id(1L).ancienCode("ancienCode1").nouveauCode("nouveauCode1").motif("motif1");
    }

    public static HistoriqueCodeBarres getHistoriqueCodeBarresSample2() {
        return new HistoriqueCodeBarres().id(2L).ancienCode("ancienCode2").nouveauCode("nouveauCode2").motif("motif2");
    }

    public static HistoriqueCodeBarres getHistoriqueCodeBarresRandomSampleGenerator() {
        return new HistoriqueCodeBarres()
            .id(longCount.incrementAndGet())
            .ancienCode(UUID.randomUUID().toString())
            .nouveauCode(UUID.randomUUID().toString())
            .motif(UUID.randomUUID().toString());
    }
}
