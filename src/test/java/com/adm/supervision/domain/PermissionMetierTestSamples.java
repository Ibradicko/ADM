package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PermissionMetierTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static PermissionMetier getPermissionMetierSample1() {
        return new PermissionMetier().id(1L).code("code1").libelle("libelle1").module("module1");
    }

    public static PermissionMetier getPermissionMetierSample2() {
        return new PermissionMetier().id(2L).code("code2").libelle("libelle2").module("module2");
    }

    public static PermissionMetier getPermissionMetierRandomSampleGenerator() {
        return new PermissionMetier()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .libelle(UUID.randomUUID().toString())
            .module(UUID.randomUUID().toString());
    }
}
