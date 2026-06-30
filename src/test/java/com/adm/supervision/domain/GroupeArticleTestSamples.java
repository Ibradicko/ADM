package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class GroupeArticleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static GroupeArticle getGroupeArticleSample1() {
        return new GroupeArticle().id(1L).code("code1").libelle("libelle1");
    }

    public static GroupeArticle getGroupeArticleSample2() {
        return new GroupeArticle().id(2L).code("code2").libelle("libelle2");
    }

    public static GroupeArticle getGroupeArticleRandomSampleGenerator() {
        return new GroupeArticle().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).libelle(UUID.randomUUID().toString());
    }
}
