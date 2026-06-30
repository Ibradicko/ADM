package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LocataireTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Locataire getLocataireSample1() {
        return new Locataire()
            .id(1L)
            .code("code1")
            .nom("nom1")
            .numeroIdentification("numeroIdentification1")
            .telephone("telephone1")
            .email("email1")
            .adresse("adresse1");
    }

    public static Locataire getLocataireSample2() {
        return new Locataire()
            .id(2L)
            .code("code2")
            .nom("nom2")
            .numeroIdentification("numeroIdentification2")
            .telephone("telephone2")
            .email("email2")
            .adresse("adresse2");
    }

    public static Locataire getLocataireRandomSampleGenerator() {
        return new Locataire()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .nom(UUID.randomUUID().toString())
            .numeroIdentification(UUID.randomUUID().toString())
            .telephone(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .adresse(UUID.randomUUID().toString());
    }
}
