package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UniteMesureTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static UniteMesure getUniteMesureSample1() {
        return new UniteMesure().id(1L).code("code1").libelle("libelle1").symbole("symbole1");
    }

    public static UniteMesure getUniteMesureSample2() {
        return new UniteMesure().id(2L).code("code2").libelle("libelle2").symbole("symbole2");
    }

    public static UniteMesure getUniteMesureRandomSampleGenerator() {
        return new UniteMesure()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .libelle(UUID.randomUUID().toString())
            .symbole(UUID.randomUUID().toString());
    }
}
