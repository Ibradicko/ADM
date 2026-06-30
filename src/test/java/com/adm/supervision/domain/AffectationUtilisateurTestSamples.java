package com.adm.supervision.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AffectationUtilisateurTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static AffectationUtilisateur getAffectationUtilisateurSample1() {
        return new AffectationUtilisateur().id(1L);
    }

    public static AffectationUtilisateur getAffectationUtilisateurSample2() {
        return new AffectationUtilisateur().id(2L);
    }

    public static AffectationUtilisateur getAffectationUtilisateurRandomSampleGenerator() {
        return new AffectationUtilisateur().id(longCount.incrementAndGet());
    }
}
