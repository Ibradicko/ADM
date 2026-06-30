package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FamilleArticleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static FamilleArticle getFamilleArticleSample1() {
        return new FamilleArticle().id(1L).code("code1").libelle("libelle1");
    }

    public static FamilleArticle getFamilleArticleSample2() {
        return new FamilleArticle().id(2L).code("code2").libelle("libelle2");
    }

    public static FamilleArticle getFamilleArticleRandomSampleGenerator() {
        return new FamilleArticle()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .libelle(UUID.randomUUID().toString());
    }
}
