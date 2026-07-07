package com.adm.supervision.security;

import com.adm.supervision.domain.AffectationUtilisateur;
import com.adm.supervision.domain.ExploitationBoutique;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.domain.PermissionMetier;
import com.adm.supervision.domain.ProfilMetier;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.repository.AffectationUtilisateurRepository;
import com.adm.supervision.repository.ExploitationBoutiqueRepository;
import com.adm.supervision.repository.LocataireRepository;
import com.adm.supervision.repository.ProfilMetierRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.service.criteria.AffectationUtilisateurCriteria;
import com.adm.supervision.service.dto.AffectationUtilisateurDTO;
import com.adm.supervision.service.dto.ParametreGlobalDTO;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service("businessAuthorizationService")
public class BusinessAuthorizationService {

    private static final Set<String> MANAGEMENT_PROFILE_CODES = Set.of("ADMINISTRATEUR", "MANAGER_ADM");
    private static final Set<String> USER_PROFILE_CODES = Set.of("ADMINISTRATEUR", "MANAGER_ADM", "MANAGER_BOUTIQUE");
    private static final Set<String> SALES_READ_PROFILE_CODES = Set.of("ADMINISTRATEUR", "MANAGER_ADM", "MANAGER_BOUTIQUE", "VENDEUR");
    private static final Set<String> SALES_MANAGE_PROFILE_CODES = Set.of("ADMINISTRATEUR", "MANAGER_BOUTIQUE", "VENDEUR");
    private static final Set<String> STOCK_PROFILE_CODES = Set.of("ADMINISTRATEUR", "MANAGER_BOUTIQUE", "VENDEUR");
    private static final Set<String> SUPERVISION_PROFILE_CODES = Set.of("ADMINISTRATEUR", "MANAGER_ADM", "MANAGER_BOUTIQUE");
    private static final Set<String> ROYALTY_READ_PROFILE_CODES = Set.of("ADMINISTRATEUR", "MANAGER_ADM", "MANAGER_BOUTIQUE", "VENDEUR");
    private static final Set<String> DASHBOARD_PROFILE_CODES = Set.of("ADMINISTRATEUR", "MANAGER_ADM", "MANAGER_BOUTIQUE", "VENDEUR");

    private final AffectationUtilisateurRepository affectationUtilisateurRepository;
    private final UserRepository userRepository;
    private final LocataireRepository locataireRepository;
    private final ExploitationBoutiqueRepository exploitationBoutiqueRepository;
    private final ProfilMetierRepository profilMetierRepository;

    public BusinessAuthorizationService(
        AffectationUtilisateurRepository affectationUtilisateurRepository,
        UserRepository userRepository,
        LocataireRepository locataireRepository,
        ExploitationBoutiqueRepository exploitationBoutiqueRepository,
        ProfilMetierRepository profilMetierRepository
    ) {
        this.affectationUtilisateurRepository = affectationUtilisateurRepository;
        this.userRepository = userRepository;
        this.locataireRepository = locataireRepository;
        this.exploitationBoutiqueRepository = exploitationBoutiqueRepository;
        this.profilMetierRepository = profilMetierRepository;
    }

    public boolean canReadUsers() {
        if (isCurrentUserLocataire()) {
            return !getAccessibleBoutiqueIds().isEmpty();
        }
        return hasUserManagementPermission(BusinessPermissions.USER_MANAGE, BusinessPermissions.USER_READ);
    }

    public boolean canReadOwnAssignments(AffectationUtilisateurCriteria criteria) {
        if (criteria == null || criteria.getUserId() == null || criteria.getUserId().getEquals() == null) {
            return false;
        }

        Long requestedUserId = criteria.getUserId().getEquals();
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .map(user -> requestedUserId.equals(user.getId()))
            .orElse(false);
    }

    public boolean canCreateUsers() {
        if (isCurrentUserLocataire()) {
            return !getAccessibleBoutiqueIds().isEmpty();
        }
        return hasUserManagementPermission(BusinessPermissions.USER_MANAGE, BusinessPermissions.USER_CREATE);
    }

    public boolean canUpdateUsers() {
        return hasUserManagementPermission(BusinessPermissions.USER_MANAGE, BusinessPermissions.USER_UPDATE);
    }

    public boolean canDeactivateUsers() {
        if (isCurrentUserLocataire()) {
            return !getAccessibleBoutiqueIds().isEmpty();
        }
        return hasUserManagementPermission(BusinessPermissions.USER_MANAGE, BusinessPermissions.USER_DEACTIVATE);
    }

    public boolean canManageAffectationUtilisateur(AffectationUtilisateurDTO affectationUtilisateurDTO) {
        if (affectationUtilisateurDTO == null || affectationUtilisateurDTO.getBoutique() == null) {
            return canCreateUsers();
        }

        Long boutiqueId = affectationUtilisateurDTO.getBoutique().getId();
        // Le code du profil est résolu en base à partir de l'id : ne jamais faire confiance au
        // champ "code" éventuellement envoyé par le client, qui ne reflète pas forcément le profil réel.
        String profilCode = Optional.ofNullable(affectationUtilisateurDTO.getProfil())
            .map(profil -> profil.getId())
            .flatMap(profilMetierRepository::findById)
            .map(profil -> normalize(profil.getCode()))
            .orElse("");

        // Le locataire peut créer Manager Boutique et Vendeur pour ses propres boutiques uniquement
        if (isCurrentUserLocataire()) {
            return canManageOwnBoutiqueUsers(boutiqueId) && Set.of("MANAGER_BOUTIQUE", "VENDEUR").contains(profilCode);
        }

        if (!canCreateUsers() || !canAccessBoutique(boutiqueId)) {
            return false;
        }

        if (isAdmin() || hasActiveProfile("MANAGER_ADM")) {
            return true;
        }

        // Manager Boutique : peut créer Vendeur seulement
        return "VENDEUR".equals(profilCode);
    }

    public boolean canReadSales() {
        return hasProfileAndPermission(SALES_READ_PROFILE_CODES, BusinessPermissions.SALES_MANAGE, BusinessPermissions.SALES_READ);
    }

    public boolean canManageSales() {
        return hasProfileAndPermission(SALES_MANAGE_PROFILE_CODES, BusinessPermissions.SALES_MANAGE);
    }

    public boolean canReadStock() {
        return (
            hasProfileAndPermission(STOCK_PROFILE_CODES, BusinessPermissions.STOCK_MANAGE, BusinessPermissions.STOCK_READ) ||
            hasProfileAndPermission(Set.of("VENDEUR"), BusinessPermissions.SALES_MANAGE, BusinessPermissions.SALES_READ)
        );
    }

    public boolean canManageStock() {
        return hasProfileAndPermission(Set.of("ADMINISTRATEUR", "MANAGER_BOUTIQUE"), BusinessPermissions.STOCK_MANAGE);
    }

    public boolean canManageCatalogue() {
        return hasProfileAndPermission(Set.of("ADMINISTRATEUR", "MANAGER_BOUTIQUE"), BusinessPermissions.STOCK_MANAGE);
    }

    public boolean canAssignBarcode() {
        return canManageStock();
    }

    public boolean canReadReporting() {
        if (isCurrentUserLocataire()) {
            return !getAccessibleBoutiqueIds().isEmpty();
        }
        return (
            hasProfileAndPermission(SUPERVISION_PROFILE_CODES, BusinessPermissions.REPORTING_EXPORT, BusinessPermissions.REPORTING_READ) ||
            hasProfileAndPermission(DASHBOARD_PROFILE_CODES, BusinessPermissions.SALES_MANAGE, BusinessPermissions.SALES_READ)
        );
    }

    public boolean canExportReporting() {
        return hasProfileAndPermission(MANAGEMENT_PROFILE_CODES, BusinessPermissions.REPORTING_EXPORT);
    }

    public boolean canAccessReportingExports() {
        if (isCurrentUserLocataire()) {
            return !getAccessibleBoutiqueIds().isEmpty();
        }
        return (
            canExportReporting() ||
            hasProfileAndPermission(SUPERVISION_PROFILE_CODES, BusinessPermissions.REPORTING_EXPORT, BusinessPermissions.REPORTING_READ)
        );
    }

    public boolean canReadRoyalties() {
        if (isCurrentUserLocataire()) {
            return !getAccessibleBoutiqueIds().isEmpty();
        }
        return hasProfileAndPermission(ROYALTY_READ_PROFILE_CODES, BusinessPermissions.ROYALTY_MANAGE, BusinessPermissions.ROYALTY_READ);
    }

    public boolean canManageRoyalties() {
        return hasProfileAndPermission(MANAGEMENT_PROFILE_CODES, BusinessPermissions.ROYALTY_MANAGE);
    }

    public boolean canReadSettings() {
        return hasPermission(MANAGEMENT_PROFILE_CODES, BusinessPermissions.SETTINGS_MANAGE, BusinessPermissions.SETTINGS_READ);
    }

    public boolean canManageSettings() {
        return hasPermission(MANAGEMENT_PROFILE_CODES, BusinessPermissions.SETTINGS_MANAGE);
    }

    public boolean canManageParametreGlobal(ParametreGlobalDTO parametreGlobalDTO) {
        if (parametreGlobalDTO == null || parametreGlobalDTO.getCode() == null) {
            return canManageSettings();
        }

        Long boutiqueId = extractBoutiqueScopedParameterId(parametreGlobalDTO.getCode());
        if (boutiqueId == null) {
            return canManageSettings();
        }

        return canManageBoutiqueSettings(boutiqueId);
    }

    public boolean canReadPaymentSettings() {
        return canReadSettings() || canReadSales() || canReadRoyalties();
    }

    public boolean canReadAudit() {
        if (isCurrentUserLocataire()) {
            return !getAccessibleBoutiqueIds().isEmpty();
        }
        return hasPermission(
            MANAGEMENT_PROFILE_CODES,
            BusinessPermissions.AUDIT_READ,
            BusinessPermissions.REPORTING_READ,
            BusinessPermissions.REPORTING_EXPORT
        );
    }

    public boolean isAdmin() {
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN);
    }

    public boolean isCurrentUserLocataire() {
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.LOCATAIRE);
    }

    public Optional<Long> getCurrentLocataireId() {
        return SecurityUtils.getCurrentUserLogin().flatMap(locataireRepository::findOneByUserLogin).map(Locataire::getId);
    }

    /**
     * Un locataire peut affecter Manager Boutique / Vendeur uniquement sur ses boutiques actives.
     * Admin et Manager ADM peuvent tout faire.
     */
    public boolean canManageOwnBoutiqueUsers(Long boutiqueId) {
        if (isAdmin() || hasActiveProfile("MANAGER_ADM")) {
            return true;
        }
        if (!isCurrentUserLocataire()) {
            return false;
        }
        return boutiqueId != null && getAccessibleBoutiqueIds().contains(boutiqueId);
    }

    public Set<Long> getAccessibleBoutiqueIds() {
        Set<Long> boutiqueIds = getActiveAssignments()
            .stream()
            .map(AffectationUtilisateur::getBoutique)
            .filter(boutique -> boutique != null && boutique.getId() != null)
            .map(boutique -> boutique.getId())
            .collect(Collectors.toCollection(LinkedHashSet::new));

        if (isCurrentUserLocataire()) {
            // Un locataire doit toujours accéder à ses boutiques sous contrat actif, même si
            // l'affectation MANAGER_BOUTIQUE automatique n'a pas (encore) été créée.
            getOwnedActiveExploitations()
                .stream()
                .map(ExploitationBoutique::getBoutique)
                .filter(boutique -> boutique != null && boutique.getId() != null)
                .forEach(boutique -> boutiqueIds.add(boutique.getId()));
        }

        return boutiqueIds;
    }

    public boolean canAccessBoutique(Long boutiqueId) {
        if (boutiqueId == null) {
            return isAdmin() || hasActiveProfile("MANAGER_ADM");
        }
        return isAdmin() || hasActiveProfile("MANAGER_ADM") || getAccessibleBoutiqueIds().contains(boutiqueId);
    }

    public boolean canManageBoutiques() {
        return isAdmin() || hasActiveProfile("MANAGER_ADM");
    }

    public boolean canAccessUser(String login) {
        if (isAdmin()) {
            return true;
        }
        Set<Long> boutiqueIds = getAccessibleBoutiqueIds();
        return !boutiqueIds.isEmpty() && userRepository.countManagedUsersByLoginAndBoutiqueIds(login, boutiqueIds, LocalDate.now()) > 0;
    }

    public boolean canAccessUser(Long userId) {
        if (isAdmin()) {
            return true;
        }
        Set<Long> boutiqueIds = getAccessibleBoutiqueIds();
        return !boutiqueIds.isEmpty() && userRepository.countManagedUsersByIdAndBoutiqueIds(userId, boutiqueIds, LocalDate.now()) > 0;
    }

    private boolean hasUserManagementPermission(String... permissionCodes) {
        return hasProfileAndPermission(USER_PROFILE_CODES, permissionCodes);
    }

    private boolean hasProfileAndPermission(Set<String> allowedProfileCodes, String... permissionCodes) {
        if (isAdmin()) {
            return true;
        }

        Set<String> normalizedPermissionCodes = Arrays.stream(permissionCodes)
            .map(code -> code.toUpperCase(Locale.ROOT))
            .collect(Collectors.toSet());

        return getActiveAssignments()
            .stream()
            .map(AffectationUtilisateur::getProfil)
            .filter(this::isActiveProfile)
            .filter(profil -> allowedProfileCodes.contains(normalize(profil.getCode())))
            .anyMatch(
                profil ->
                    MANAGEMENT_PROFILE_CODES.contains(normalize(profil.getCode())) ||
                    hasOneOfPermissions(profil.getPermissionses(), normalizedPermissionCodes)
            );
    }

    private boolean hasPermission(Set<String> fallbackProfileCodes, String... permissionCodes) {
        if (isAdmin()) {
            return true;
        }

        Set<String> normalizedPermissionCodes = Arrays.stream(permissionCodes)
            .map(code -> code.toUpperCase(Locale.ROOT))
            .collect(Collectors.toSet());

        return getActiveAssignments()
            .stream()
            .map(AffectationUtilisateur::getProfil)
            .filter(this::isActiveProfile)
            .anyMatch(
                profil ->
                    fallbackProfileCodes.contains(normalize(profil.getCode())) ||
                    hasOneOfPermissions(profil.getPermissionses(), normalizedPermissionCodes)
            );
    }

    private boolean hasActiveProfile(String profileCode) {
        String normalizedProfileCode = normalize(profileCode);
        return getActiveAssignments()
            .stream()
            .map(AffectationUtilisateur::getProfil)
            .filter(this::isActiveProfile)
            .map(ProfilMetier::getCode)
            .map(this::normalize)
            .anyMatch(normalizedProfileCode::equals);
    }

    private boolean hasOneOfPermissions(Collection<PermissionMetier> permissions, Set<String> expectedCodes) {
        if (permissions == null) {
            return false;
        }
        return permissions.stream().map(PermissionMetier::getCode).map(this::normalize).anyMatch(expectedCodes::contains);
    }

    private boolean canManageBoutiqueSettings(Long boutiqueId) {
        if (isAdmin()) {
            return true;
        }

        return getActiveAssignments()
            .stream()
            .filter(affectation -> affectation.getBoutique() != null && boutiqueId.equals(affectation.getBoutique().getId()))
            .map(AffectationUtilisateur::getProfil)
            .filter(this::isActiveProfile)
            .anyMatch(
                profil ->
                    Set.of("MANAGER_ADM", "MANAGER_BOUTIQUE").contains(normalize(profil.getCode())) ||
                    hasOneOfPermissions(profil.getPermissionses(), Set.of(BusinessPermissions.SETTINGS_MANAGE))
            );
    }

    private Long extractBoutiqueScopedParameterId(String code) {
        String normalizedCode = normalize(code);
        if (!normalizedCode.startsWith("BOUTIQUE_")) {
            return null;
        }

        String[] parts = normalizedCode.split("_", 3);
        if (parts.length < 3) {
            return null;
        }

        try {
            return Long.valueOf(parts[1]);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private List<AffectationUtilisateur> getActiveAssignments() {
        return SecurityUtils.getCurrentUserLogin()
            .map(login ->
                affectationUtilisateurRepository.findActiveAssignmentsForSecurity(login.toLowerCase(Locale.ROOT), LocalDate.now())
            )
            .orElseGet(List::of);
    }

    private List<ExploitationBoutique> getOwnedActiveExploitations() {
        return SecurityUtils.getCurrentUserLogin()
            .map(exploitationBoutiqueRepository::findByLocataireUserLogin)
            .orElseGet(List::of)
            .stream()
            .filter(exploitation -> exploitation.getStatut() == StatutGeneral.ACTIF)
            .toList();
    }

    private boolean isActiveProfile(ProfilMetier profil) {
        return profil != null && profil.getStatut() == StatutGeneral.ACTIF;
    }

    private String normalize(String value) {
        return value == null ? "" : value.toUpperCase(Locale.ROOT);
    }
}
