package com.adm.supervision.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class JournalAuditTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static JournalAudit getJournalAuditSample1() {
        return new JournalAudit()
            .id(1L)
            .entiteConcernee("entiteConcernee1")
            .identifiantEntite("identifiantEntite1")
            .adresseIp("adresseIp1");
    }

    public static JournalAudit getJournalAuditSample2() {
        return new JournalAudit()
            .id(2L)
            .entiteConcernee("entiteConcernee2")
            .identifiantEntite("identifiantEntite2")
            .adresseIp("adresseIp2");
    }

    public static JournalAudit getJournalAuditRandomSampleGenerator() {
        return new JournalAudit()
            .id(longCount.incrementAndGet())
            .entiteConcernee(UUID.randomUUID().toString())
            .identifiantEntite(UUID.randomUUID().toString())
            .adresseIp(UUID.randomUUID().toString());
    }
}
