package com.adm.supervision.service;

import com.adm.supervision.domain.User;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.security.BusinessAuthorizationService;
import com.adm.supervision.security.SecurityUtils;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ModuleSecurityService {

    private final BusinessAuthorizationService businessAuthorizationService;
    private final UserRepository userRepository;

    public ModuleSecurityService(BusinessAuthorizationService businessAuthorizationService, UserRepository userRepository) {
        this.businessAuthorizationService = businessAuthorizationService;
        this.userRepository = userRepository;
    }

    public boolean isAdmin() {
        return businessAuthorizationService.isAdmin();
    }

    public boolean hasGlobalBoutiqueAccess() {
        return businessAuthorizationService.canManageBoutiques();
    }

    public Set<Long> getAccessibleBoutiqueIds() {
        return businessAuthorizationService.getAccessibleBoutiqueIds();
    }

    public void assertBoutiqueAccess(Long boutiqueId, String message) {
        if (!businessAuthorizationService.canAccessBoutique(boutiqueId)) {
            throw new AccessDeniedException(message);
        }
    }

    public void assertAllBoutiquesAccess(Collection<Long> boutiqueIds, String message) {
        if (hasGlobalBoutiqueAccess()) {
            return;
        }
        Set<Long> accessibleBoutiqueIds = getAccessibleBoutiqueIds();
        boolean allowed = boutiqueIds != null && boutiqueIds.stream().filter(Objects::nonNull).allMatch(accessibleBoutiqueIds::contains);
        if (!allowed) {
            throw new AccessDeniedException(message);
        }
    }

    public User getCurrentUser() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccessDeniedException("Utilisateur courant introuvable"));
        return userRepository.findOneByLogin(login).orElseThrow(() -> new AccessDeniedException("Utilisateur courant introuvable"));
    }
}
