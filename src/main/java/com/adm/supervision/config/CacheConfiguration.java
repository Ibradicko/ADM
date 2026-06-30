package com.adm.supervision.config;

import com.github.benmanes.caffeine.jcache.configuration.CaffeineConfiguration;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Caffeine caffeine = jHipsterProperties.getCache().getCaffeine();

        CaffeineConfiguration<Object, Object> caffeineConfiguration = new CaffeineConfiguration<>();
        caffeineConfiguration.setMaximumSize(OptionalLong.of(caffeine.getMaxEntries()));
        caffeineConfiguration.setExpireAfterWrite(OptionalLong.of(TimeUnit.SECONDS.toNanos(caffeine.getTimeToLiveSeconds())));
        caffeineConfiguration.setStatisticsEnabled(true);
        jcacheConfiguration = caffeineConfiguration;
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.adm.supervision.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.adm.supervision.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.adm.supervision.domain.User.class.getName());
            createCache(cm, com.adm.supervision.domain.Authority.class.getName());
            createCache(cm, com.adm.supervision.domain.User.class.getName() + ".authorities");
            createCache(cm, com.adm.supervision.domain.Boutique.class.getName());
            createCache(cm, com.adm.supervision.domain.Locataire.class.getName());
            createCache(cm, com.adm.supervision.domain.ExploitationBoutique.class.getName());
            createCache(cm, com.adm.supervision.domain.ProfilMetier.class.getName());
            createCache(cm, com.adm.supervision.domain.ProfilMetier.class.getName() + ".permissionses");
            createCache(cm, com.adm.supervision.domain.PermissionMetier.class.getName());
            createCache(cm, com.adm.supervision.domain.PermissionMetier.class.getName() + ".profilses");
            createCache(cm, com.adm.supervision.domain.AffectationUtilisateur.class.getName());
            createCache(cm, com.adm.supervision.domain.GroupeArticle.class.getName());
            createCache(cm, com.adm.supervision.domain.FamilleArticle.class.getName());
            createCache(cm, com.adm.supervision.domain.SousFamilleArticle.class.getName());
            createCache(cm, com.adm.supervision.domain.UniteMesure.class.getName());
            createCache(cm, com.adm.supervision.domain.Produit.class.getName());
            createCache(cm, com.adm.supervision.domain.TarifProduit.class.getName());
            createCache(cm, com.adm.supervision.domain.CodeBarresProduit.class.getName());
            createCache(cm, com.adm.supervision.domain.HistoriqueCodeBarres.class.getName());
            createCache(cm, com.adm.supervision.domain.ParametreCodeBarres.class.getName());
            createCache(cm, com.adm.supervision.domain.LotEtiquettes.class.getName());
            createCache(cm, com.adm.supervision.domain.EtiquetteProduit.class.getName());
            createCache(cm, com.adm.supervision.domain.ScanInconnu.class.getName());
            createCache(cm, com.adm.supervision.domain.ModePaiementRef.class.getName());
            createCache(cm, com.adm.supervision.domain.Vente.class.getName());
            createCache(cm, com.adm.supervision.domain.LigneVente.class.getName());
            createCache(cm, com.adm.supervision.domain.PaiementVente.class.getName());
            createCache(cm, com.adm.supervision.domain.OperationCorrectiveVente.class.getName());
            createCache(cm, com.adm.supervision.domain.TicketCaisse.class.getName());
            createCache(cm, com.adm.supervision.domain.DepotStock.class.getName());
            createCache(cm, com.adm.supervision.domain.StockProduit.class.getName());
            createCache(cm, com.adm.supervision.domain.MouvementStock.class.getName());
            createCache(cm, com.adm.supervision.domain.LigneMouvementStock.class.getName());
            createCache(cm, com.adm.supervision.domain.ReceptionProduit.class.getName());
            createCache(cm, com.adm.supervision.domain.LigneReceptionProduit.class.getName());
            createCache(cm, com.adm.supervision.domain.InventaireStock.class.getName());
            createCache(cm, com.adm.supervision.domain.LigneInventaireStock.class.getName());
            createCache(cm, com.adm.supervision.domain.TransfertStock.class.getName());
            createCache(cm, com.adm.supervision.domain.LigneTransfertStock.class.getName());
            createCache(cm, com.adm.supervision.domain.RegleRedevance.class.getName());
            createCache(cm, com.adm.supervision.domain.CalculRedevance.class.getName());
            createCache(cm, com.adm.supervision.domain.LigneCalculRedevance.class.getName());
            createCache(cm, com.adm.supervision.domain.PaiementRedevance.class.getName());
            createCache(cm, com.adm.supervision.domain.RegularisationRedevance.class.getName());
            createCache(cm, com.adm.supervision.domain.JournalAudit.class.getName());
            createCache(cm, com.adm.supervision.domain.RapportExport.class.getName());
            createCache(cm, com.adm.supervision.domain.ParametreGlobal.class.getName());
            // jhipster-needle-caffeine-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }
}
