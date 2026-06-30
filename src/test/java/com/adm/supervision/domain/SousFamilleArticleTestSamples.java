package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SousFamilleArticleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static SousFamilleArticle getSousFamilleArticleSample1() {
        return new SousFamilleArticle().id(1L).code("code1").libelle("libelle1");
    }

    public static SousFamilleArticle getSousFamilleArticleSample2() {
        return new SousFamilleArticle().id(2L).code("code2").libelle("libelle2");
    }

    public static SousFamilleArticle getSousFamilleArticleRandomSampleGenerator() {
        return new SousFamilleArticle()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .libelle(UUID.randomUUID().toString());
    }
}
