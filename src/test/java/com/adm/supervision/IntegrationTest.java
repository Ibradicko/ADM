package com.adm.supervision;

import com.adm.supervision.config.AsyncSyncConfiguration;
import com.adm.supervision.config.DatabaseTestcontainer;
import com.adm.supervision.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        AdmSupervisionVentesApp.class,
        JacksonConfiguration.class,
        AsyncSyncConfiguration.class,
        com.adm.supervision.config.JacksonHibernateConfiguration.class,
    }
)
@ImportTestcontainers(DatabaseTestcontainer.class)
public @interface IntegrationTest {}
