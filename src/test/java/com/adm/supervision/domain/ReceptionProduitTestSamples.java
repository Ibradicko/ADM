package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReceptionProduitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ReceptionProduit getReceptionProduitSample1() {
        return new ReceptionProduit().id(1L).reference("reference1").fournisseur("fournisseur1").commentaire("commentaire1");
    }

    public static ReceptionProduit getReceptionProduitSample2() {
        return new ReceptionProduit().id(2L).reference("reference2").fournisseur("fournisseur2").commentaire("commentaire2");
    }

    public static ReceptionProduit getReceptionProduitRandomSampleGenerator() {
        return new ReceptionProduit()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .fournisseur(UUID.randomUUID().toString())
            .commentaire(UUID.randomUUID().toString());
    }
}
