package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProfilMetierTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ProfilMetier getProfilMetierSample1() {
        return new ProfilMetier().id(1L).code("code1").libelle("libelle1");
    }

    public static ProfilMetier getProfilMetierSample2() {
        return new ProfilMetier().id(2L).code("code2").libelle("libelle2");
    }

    public static ProfilMetier getProfilMetierRandomSampleGenerator() {
        return new ProfilMetier().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).libelle(UUID.randomUUID().toString());
    }
}
