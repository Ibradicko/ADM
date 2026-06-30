package com.adm.supervision.service;

import com.adm.supervision.repository.VenteRepository;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import tech.jhipster.service.filter.LongFilter;

@Service
public class BoutiqueCriteriaScopeService {

    private final ModuleSecurityService moduleSecurityService;
    private final VenteRepository venteRepository;

    public BoutiqueCriteriaScopeService(ModuleSecurityService moduleSecurityService, VenteRepository venteRepository) {
        this.moduleSecurityService = moduleSecurityService;
        this.venteRepository = venteRepository;
    }

    public LongFilter scopeBoutiqueFilter(LongFilter existingFilter, String message) {
        if (moduleSecurityService.hasGlobalBoutiqueAccess()) {
            return existingFilter;
        }

        Set<Long> accessibleBoutiqueIds = moduleSecurityService.getAccessibleBoutiqueIds();
        if (accessibleBoutiqueIds.isEmpty()) {
            throw new AccessDeniedException(message);
        }

        LongFilter scopedFilter = existingFilter == null ? new LongFilter() : existingFilter;
        if (scopedFilter.getEquals() != null && !accessibleBoutiqueIds.contains(scopedFilter.getEquals())) {
            throw new AccessDeniedException(message);
        }

        if (scopedFilter.getIn() != null && !scopedFilter.getIn().isEmpty()) {
            List<Long> scopedIds = scopedFilter.getIn().stream().filter(accessibleBoutiqueIds::contains).toList();
            if (scopedIds.isEmpty()) {
                throw new AccessDeniedException(message);
            }
            scopedFilter.setIn(scopedIds);
            return scopedFilter;
        }

        scopedFilter.setIn(new ArrayList<>(accessibleBoutiqueIds));
        return scopedFilter;
    }

    public void assertAtLeastOneBoutiqueAccessible(List<Long> boutiqueIds, String message) {
        if (moduleSecurityService.hasGlobalBoutiqueAccess()) {
            return;
        }
        Set<Long> accessibleBoutiqueIds = moduleSecurityService.getAccessibleBoutiqueIds();
        Set<Long> requestedBoutiqueIds =
            boutiqueIds == null ? Set.of() : boutiqueIds.stream().collect(Collectors.toCollection(LinkedHashSet::new));
        if (requestedBoutiqueIds.isEmpty() || requestedBoutiqueIds.stream().noneMatch(accessibleBoutiqueIds::contains)) {
            throw new AccessDeniedException(message);
        }
    }

    public void assertAllBoutiquesAccessible(List<Long> boutiqueIds, String message) {
        moduleSecurityService.assertAllBoutiquesAccess(boutiqueIds, message);
    }

    public LongFilter scopeVenteFilter(LongFilter existingFilter, String message) {
        if (moduleSecurityService.hasGlobalBoutiqueAccess()) {
            return existingFilter;
        }

        Set<Long> accessibleBoutiqueIds = moduleSecurityService.getAccessibleBoutiqueIds();
        if (accessibleBoutiqueIds.isEmpty()) {
            throw new AccessDeniedException(message);
        }

        Set<Long> accessibleVenteIds = venteRepository
            .findIdsByBoutiqueIds(accessibleBoutiqueIds)
            .stream()
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (accessibleVenteIds.isEmpty()) {
            LongFilter emptyFilter = existingFilter == null ? new LongFilter() : existingFilter;
            if (emptyFilter.getEquals() != null || (emptyFilter.getIn() != null && !emptyFilter.getIn().isEmpty())) {
                throw new AccessDeniedException(message);
            }
            emptyFilter.setEquals(-1L);
            return emptyFilter;
        }

        LongFilter scopedFilter = existingFilter == null ? new LongFilter() : existingFilter;
        if (scopedFilter.getEquals() != null && !accessibleVenteIds.contains(scopedFilter.getEquals())) {
            throw new AccessDeniedException(message);
        }

        if (scopedFilter.getIn() != null && !scopedFilter.getIn().isEmpty()) {
            List<Long> scopedIds = scopedFilter.getIn().stream().filter(accessibleVenteIds::contains).toList();
            if (scopedIds.isEmpty()) {
                throw new AccessDeniedException(message);
            }
            scopedFilter.setIn(scopedIds);
            return scopedFilter;
        }

        scopedFilter.setIn(new ArrayList<>(accessibleVenteIds));
        return scopedFilter;
    }
}
