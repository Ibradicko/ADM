package com.adm.supervision.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.adm.supervision.domain.AffectationUtilisateur;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.PermissionMetier;
import com.adm.supervision.domain.ProfilMetier;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.repository.AffectationUtilisateurRepository;
import com.adm.supervision.repository.LocataireRepository;
import com.adm.supervision.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class BusinessAuthorizationServiceTest {

    @Mock
    private AffectationUtilisateurRepository affectationUtilisateurRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LocataireRepository locataireRepository;

    private BusinessAuthorizationService service;

    @BeforeEach
    void setUp() {
        service = new BusinessAuthorizationService(affectationUtilisateurRepository, userRepository, locataireRepository);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("profile-test", "password"));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void sellerCannotUseStockEvenWithAnAccidentalStockPermission() {
        mockAssignments("VENDEUR", BusinessPermissions.SALES_MANAGE, BusinessPermissions.STOCK_MANAGE);

        assertThat(service.canManageSales()).isTrue();
        assertThat(service.canReadStock()).isFalse();
        assertThat(service.canManageStock()).isFalse();
    }

    @Test
    void managerBoutiqueCanManageStockButNotRoyalties() {
        mockAssignments(
            "MANAGER_BOUTIQUE",
            BusinessPermissions.STOCK_MANAGE,
            BusinessPermissions.STOCK_READ,
            BusinessPermissions.ROYALTY_MANAGE
        );

        assertThat(service.canManageStock()).isTrue();
        assertThat(service.canReadStock()).isTrue();
        assertThat(service.canManageRoyalties()).isFalse();
        assertThat(service.canManageBoutiques()).isFalse();
    }

    @Test
    void admManagerHasGlobalBoutiqueAccess() {
        mockAssignments("MANAGER_ADM");

        assertThat(service.canAccessBoutique(null)).isTrue();
        assertThat(service.canAccessBoutique(999L)).isTrue();
        assertThat(service.canManageBoutiques()).isTrue();
    }

    @Test
    void admManagerCanSuperviseSalesButCannotSellOrManageStock() {
        mockAssignments(
            "MANAGER_ADM",
            BusinessPermissions.SALES_READ,
            BusinessPermissions.SALES_MANAGE,
            BusinessPermissions.STOCK_READ,
            BusinessPermissions.STOCK_MANAGE,
            BusinessPermissions.ROYALTY_MANAGE
        );

        assertThat(service.canReadSales()).isTrue();
        assertThat(service.canManageSales()).isFalse();
        assertThat(service.canReadStock()).isFalse();
        assertThat(service.canManageStock()).isFalse();
        assertThat(service.canAssignBarcode()).isFalse();
        assertThat(service.canManageRoyalties()).isTrue();
    }

    private void mockAssignments(String profileCode, String... permissionCodes) {
        ProfilMetier profile = new ProfilMetier();
        profile.setCode(profileCode);
        profile.setStatut(StatutGeneral.ACTIF);
        for (String permissionCode : permissionCodes) {
            PermissionMetier permission = new PermissionMetier();
            permission.setCode(permissionCode);
            profile.addPermissions(permission);
        }

        Boutique boutique = new Boutique();
        boutique.setId(10L);
        User user = new User();
        user.setLogin("profile-test");

        AffectationUtilisateur assignment = new AffectationUtilisateur();
        assignment.setUser(user);
        assignment.setBoutique(boutique);
        assignment.setProfil(profile);
        assignment.setActif(true);
        assignment.setDateDebut(LocalDate.now().minusDays(1));

        when(affectationUtilisateurRepository.findActiveAssignmentsForSecurity(eq("profile-test"), any(LocalDate.class))).thenReturn(
            List.of(assignment)
        );
    }
}
