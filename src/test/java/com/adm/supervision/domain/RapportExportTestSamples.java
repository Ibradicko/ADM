package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RapportExportTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static RapportExport getRapportExportSample1() {
        return new RapportExport().id(1L).reference("reference1").typeRapport("typeRapport1").cheminFichier("cheminFichier1");
    }

    public static RapportExport getRapportExportSample2() {
        return new RapportExport().id(2L).reference("reference2").typeRapport("typeRapport2").cheminFichier("cheminFichier2");
    }

    public static RapportExport getRapportExportRandomSampleGenerator() {
        return new RapportExport()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .typeRapport(UUID.randomUUID().toString())
            .cheminFichier(UUID.randomUUID().toString());
    }
}
