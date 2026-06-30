package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LigneInventaireStockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static LigneInventaireStock getLigneInventaireStockSample1() {
        return new LigneInventaireStock().id(1L).commentaire("commentaire1");
    }

    public static LigneInventaireStock getLigneInventaireStockSample2() {
        return new LigneInventaireStock().id(2L).commentaire("commentaire2");
    }

    public static LigneInventaireStock getLigneInventaireStockRandomSampleGenerator() {
        return new LigneInventaireStock().id(longCount.incrementAndGet()).commentaire(UUID.randomUUID().toString());
    }
}
