package com.adm.supervision.web.rest;

import com.adm.supervision.config.Constants;
import com.adm.supervision.domain.AffectationUtilisateur;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.ProfilMetier;
import com.adm.supervision.domain.User;
import com.adm.supervision.repository.AffectationUtilisateurRepository;
import com.adm.supervision.repository.BoutiqueRepository;
import com.adm.supervision.repository.ProfilMetierRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.security.AuthoritiesConstants;
import com.adm.supervision.security.BusinessAuthorizationService;
import com.adm.supervision.service.BusinessValidationException;
import com.adm.supervision.service.MailService;
import com.adm.supervision.service.UserService;
import com.adm.supervision.service.dto.AdminUserDTO;
import com.adm.supervision.service.dto.AffectationUtilisateurDTO;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.dto.ProfilMetierDTO;
import com.adm.supervision.web.rest.errors.BadRequestAlertException;
import com.adm.supervision.web.rest.errors.EmailAlreadyUsedException;
import com.adm.supervision.web.rest.errors.LoginAlreadyUsedException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing users.
 * <p>
 * This class accesses the {@link com.adm.supervision.domain.User} entity, and needs to fetch its collection of authorities.
 * <p>
 * For a normal use-case, it would be better to have an eager relationship between User and Authority,
 * and send everything to the client side: there would be no View Model and DTO, a lot less code, and an outer-join
 * which would be good for performance.
 * <p>
 * We use a View Model and a DTO for 3 reasons:
 * <ul>
 * <li>We want to keep a lazy association between the user and the authorities, because people will
 * quite often do relationships with the user, and we don't want them to get the authorities all
 * the time for nothing (for performance reasons). This is the #1 goal: we should not impact our users'
 * application because of this use-case.</li>
 * <li> Not having an outer join causes n+1 requests to the database. This is not a real issue as
 * we have by default a second-level cache. This means on the first HTTP call we do the n+1 requests,
 * but then all authorities come from the cache, so in fact it's much better than doing an outer join
 * (which will get lots of data from the database, for each HTTP call).</li>
 * <li> As this manages users, for security reasons, we'd rather have a DTO layer.</li>
 * </ul>
 * <p>
 * Another option would be to have a specific JPA entity graph to handle this case.
 */
@RestController
@RequestMapping("/api/admin")
public class UserResource {

    private static final List<String> ALLOWED_ORDERED_PROPERTIES = Collections.unmodifiableList(
        Arrays.asList(
            "id",
            "login",
            "firstName",
            "lastName",
            "email",
            "activated",
            "langKey",
            "createdBy",
            "createdDate",
            "lastModifiedBy",
            "lastModifiedDate"
        )
    );

    private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final UserService userService;

    private final UserRepository userRepository;

    private final MailService mailService;

    private final BusinessAuthorizationService businessAuthorizationService;

    private final AffectationUtilisateurRepository affectationUtilisateurRepository;

    private final BoutiqueRepository boutiqueRepository;

    private final ProfilMetierRepository profilMetierRepository;

    public UserResource(
        UserService userService,
        UserRepository userRepository,
        MailService mailService,
        BusinessAuthorizationService businessAuthorizationService,
        AffectationUtilisateurRepository affectationUtilisateurRepository,
        BoutiqueRepository boutiqueRepository,
        ProfilMetierRepository profilMetierRepository
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.mailService = mailService;
        this.businessAuthorizationService = businessAuthorizationService;
        this.affectationUtilisateurRepository = affectationUtilisateurRepository;
        this.boutiqueRepository = boutiqueRepository;
        this.profilMetierRepository = profilMetierRepository;
    }

    /**
     * {@code POST  /admin/users}  : Creates a new user.
     * <p>
     * Creates a new user if the login and email are not already used, and sends a
     * mail with an activation link.
     * The user needs to be activated on creation.
     *
     * @param userDTO the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new user, or with status {@code 400 (Bad Request)} if the login or email is already in use.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the login or email is already in use.
     */
    @PostMapping("/users")
    @PreAuthorize("@businessAuthorizationService.canCreateUsers()")
    @Transactional
    public ResponseEntity<User> createUser(
        @Valid @RequestBody AdminUserDTO userDTO,
        @RequestParam(required = false) Long boutiqueId,
        @RequestParam(required = false) Long profilId
    ) throws URISyntaxException {
        LOG.debug("REST request to save User : {}", userDTO);

        if (userDTO.getId() != null) {
            throw new BadRequestAlertException("A new user cannot already have an ID", "userManagement", "idexists");
            // Lowercase the user login before comparing with database
        } else if (userRepository.findOneByLogin(userDTO.getLogin().toLowerCase()).isPresent()) {
            throw new LoginAlreadyUsedException();
        } else if (userRepository.findOneByEmailIgnoreCase(userDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException();
        } else {
            normalizeAuthoritiesForScopedManager(userDTO);
            User newUser = userService.createUserWithInitialPassword(userDTO, Constants.MOT_DE_PASSE_PAR_DEFAUT, true);
            if (!businessAuthorizationService.isAdmin() && !businessAuthorizationService.canManageBoutiques()) {
                assignScopedCreatedUserToBoutique(newUser, boutiqueId, profilId);
            }
            mailService.sendCreationEmail(newUser);
            return ResponseEntity.created(new URI("/api/admin/users/" + newUser.getLogin()))
                .headers(HeaderUtil.createAlert(applicationName, "userManagement.created", newUser.getLogin()))
                .body(newUser);
        }
    }

    /**
     * {@code PUT /admin/users} : Updates an existing User.
     *
     * @param userDTO the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated user.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already in use.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already in use.
     */
    @PutMapping({ "/users", "/users/{login}" })
    @PreAuthorize("@businessAuthorizationService.canUpdateUsers()")
    public ResponseEntity<AdminUserDTO> updateUser(
        @PathVariable(name = "login", required = false) @Pattern(regexp = Constants.LOGIN_REGEX) String login,
        @Valid @RequestBody AdminUserDTO userDTO
    ) {
        LOG.debug("REST request to update User : {}", userDTO);
        assertUserAccessible(userDTO.getId());
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
            throw new EmailAlreadyUsedException();
        }
        existingUser = userRepository.findOneByLogin(userDTO.getLogin().toLowerCase());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getId().equals(userDTO.getId()))) {
            throw new LoginAlreadyUsedException();
        }
        Optional<AdminUserDTO> updatedUser = userService.updateUser(userDTO);

        return ResponseUtil.wrapOrNotFound(
            updatedUser,
            HeaderUtil.createAlert(applicationName, "userManagement.updated", userDTO.getLogin())
        );
    }

    /**
     * {@code GET /admin/users} : get all users with all the details - calling this are only allowed for the administrators.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    @PreAuthorize("@businessAuthorizationService.canReadUsers()")
    public ResponseEntity<List<AdminUserDTO>> getAllUsers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get managed users");
        if (!onlyContainsAllowedProperties(pageable)) {
            return ResponseEntity.badRequest().build();
        }

        final Page<AdminUserDTO> page = businessAuthorizationService.isAdmin()
            ? userService.getAllManagedUsers(pageable)
            : userService.getManagedUsersByBoutiques(businessAuthorizationService.getAccessibleBoutiqueIds(), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    private boolean onlyContainsAllowedProperties(Pageable pageable) {
        return pageable.getSort().stream().map(Sort.Order::getProperty).allMatch(ALLOWED_ORDERED_PROPERTIES::contains);
    }

    /**
     * {@code GET /admin/users/:login} : get the "login" user.
     *
     * @param login the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "login" user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/users/{login}")
    @PreAuthorize("@businessAuthorizationService.canReadUsers()")
    public ResponseEntity<AdminUserDTO> getUser(@PathVariable("login") @Pattern(regexp = Constants.LOGIN_REGEX) String login) {
        LOG.debug("REST request to get User : {}", login);
        assertUserAccessible(login);
        return ResponseUtil.wrapOrNotFound(userService.getUserWithAuthoritiesByLogin(login).map(AdminUserDTO::new));
    }

    /**
     * {@code DELETE /admin/users/:login} : delete the "login" User.
     *
     * @param login the login of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/users/{login}")
    @PreAuthorize("@businessAuthorizationService.canDeactivateUsers()")
    public ResponseEntity<Void> deleteUser(@PathVariable("login") @Pattern(regexp = Constants.LOGIN_REGEX) String login) {
        LOG.debug("REST request to deactivate User: {}", login);
        assertUserAccessible(login);
        userService.deactivateUser(login);
        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "userManagement.deactivated", login)).build();
    }

    @PostMapping("/users/{login}/activate")
    @PreAuthorize("@businessAuthorizationService.canUpdateUsers()")
    public ResponseEntity<Void> activateUser(@PathVariable("login") @Pattern(regexp = Constants.LOGIN_REGEX) String login) {
        LOG.debug("REST request to activate User: {}", login);
        assertUserAccessible(login);
        userService.activateUser(login, true);
        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "userManagement.activated", login)).build();
    }

    @PostMapping("/seller-assignments/{id}/activate")
    @PreAuthorize("@businessAuthorizationService.canUpdateUsers()")
    @Transactional
    public ResponseEntity<Void> activateSellerAssignment(@PathVariable("id") Long id) {
        LOG.debug("REST request to activate seller assignment: {}", id);
        AffectationUtilisateur affectation = getManagedSellerAssignment(id);
        affectation.setActif(true);
        affectation.setDateFin(null);
        affectationUtilisateurRepository.save(affectation);
        userService.activateUser(affectation.getUser().getLogin(), true);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createAlert(applicationName, "userManagement.sellerActivated", affectation.getUser().getLogin()))
            .build();
    }

    @PostMapping("/seller-assignments/{id}/deactivate")
    @PreAuthorize("@businessAuthorizationService.canDeactivateUsers()")
    @Transactional
    public ResponseEntity<Void> deactivateSellerAssignment(@PathVariable("id") Long id) {
        LOG.debug("REST request to deactivate seller assignment: {}", id);
        AffectationUtilisateur affectation = getManagedSellerAssignment(id);
        affectation.setActif(false);
        affectation.setDateFin(LocalDate.now());
        affectationUtilisateurRepository.save(affectation);
        userService.deactivateUserAccount(affectation.getUser());
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createAlert(applicationName, "userManagement.sellerDeactivated", affectation.getUser().getLogin()))
            .build();
    }

    private void assertUserAccessible(String login) {
        if (!businessAuthorizationService.isAdmin() && !businessAuthorizationService.canAccessUser(login)) {
            throw new AccessDeniedException("Access denied to requested user");
        }
    }

    private void assertUserAccessible(Long userId) {
        if (userId == null) {
            return;
        }
        if (!businessAuthorizationService.isAdmin() && !businessAuthorizationService.canAccessUser(userId)) {
            throw new AccessDeniedException("Access denied to requested user");
        }
    }

    private AffectationUtilisateur getManagedSellerAssignment(Long id) {
        AffectationUtilisateur affectation = affectationUtilisateurRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("userManagement", "assignmentNotFound", "Affectation introuvable"));

        String profilCode = Optional.ofNullable(affectation.getProfil()).map(ProfilMetier::getCode).orElse("");
        if (!"VENDEUR".equalsIgnoreCase(profilCode)) {
            throw new BusinessValidationException("userManagement", "sellerOnly", "Cette action est reservee aux vendeurs");
        }

        if (
            !businessAuthorizationService.canManageAffectationUtilisateur(
                toAuthorizationDTO(affectation.getBoutique().getId(), affectation.getProfil().getId())
            )
        ) {
            throw new AccessDeniedException("Acces refuse a ce vendeur");
        }

        return affectation;
    }

    private void normalizeAuthoritiesForScopedManager(AdminUserDTO userDTO) {
        if (businessAuthorizationService.isAdmin()) {
            return;
        }

        if (userDTO.getAuthorities() != null && userDTO.getAuthorities().contains(AuthoritiesConstants.ADMIN)) {
            throw new AccessDeniedException("Only administrators can create administrator accounts");
        }

        Set<String> safeScopedRoles = Set.of(AuthoritiesConstants.VENDEUR, AuthoritiesConstants.MANAGER_BOUTIQUE);
        Set<String> normalized = new HashSet<>(Set.of(AuthoritiesConstants.USER));
        if (userDTO.getAuthorities() != null) {
            for (String role : userDTO.getAuthorities()) {
                if (safeScopedRoles.contains(role)) {
                    normalized.add(role);
                }
            }
        }
        userDTO.setAuthorities(normalized);
    }

    private void assignScopedCreatedUserToBoutique(User newUser, Long boutiqueId, Long profilId) {
        if (boutiqueId == null || profilId == null) {
            throw new BusinessValidationException(
                "userManagement",
                "boutiqueAndProfilRequired",
                "Une boutique et un profil sont requis pour creer un utilisateur"
            );
        }

        Boutique boutique = boutiqueRepository
            .findById(boutiqueId)
            .orElseThrow(() -> new BusinessValidationException("userManagement", "boutiqueNotFound", "Boutique introuvable"));
        ProfilMetier profil = profilMetierRepository
            .findById(profilId)
            .orElseThrow(() -> new BusinessValidationException("userManagement", "profilNotFound", "Profil introuvable"));

        if (!businessAuthorizationService.canManageAffectationUtilisateur(toAuthorizationDTO(boutiqueId, profilId))) {
            throw new AccessDeniedException("Acces refuse a cette affectation utilisateur");
        }

        AffectationUtilisateur affectation = new AffectationUtilisateur()
            .user(newUser)
            .boutique(boutique)
            .profil(profil)
            .dateDebut(LocalDate.now())
            .actif(true);
        affectationUtilisateurRepository.save(affectation);
    }

    private AffectationUtilisateurDTO toAuthorizationDTO(Long boutiqueId, Long profilId) {
        BoutiqueDTO boutiqueDTO = new BoutiqueDTO();
        boutiqueDTO.setId(boutiqueId);
        ProfilMetierDTO profilDTO = new ProfilMetierDTO();
        profilDTO.setId(profilId);
        AffectationUtilisateurDTO dto = new AffectationUtilisateurDTO();
        dto.setBoutique(boutiqueDTO);
        dto.setProfil(profilDTO);
        return dto;
    }
}
