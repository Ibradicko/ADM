package com.adm.supervision.service;

import com.adm.supervision.config.Constants;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.domain.User;
import com.adm.supervision.repository.LocataireRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.security.AuthoritiesConstants;
import com.adm.supervision.service.dto.AdminUserDTO;
import com.adm.supervision.service.dto.LocataireDTO;
import com.adm.supervision.service.mapper.LocataireMapper;
import java.text.Normalizer;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.Locataire}.
 */
@Service
@Transactional
public class LocataireService {

    private static final Logger LOG = LoggerFactory.getLogger(LocataireService.class);

    private final LocataireRepository locataireRepository;
    private final LocataireMapper locataireMapper;
    private final UserRepository userRepository;
    private final UserService userService;

    public LocataireService(
        LocataireRepository locataireRepository,
        LocataireMapper locataireMapper,
        UserRepository userRepository,
        UserService userService
    ) {
        this.locataireRepository = locataireRepository;
        this.locataireMapper = locataireMapper;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Save a locataire. Crée automatiquement un compte utilisateur (ROLE_LOCATAIRE)
     * si c'est une nouvelle création sans compte déjà lié.
     * Retourne le loginGenere dans le DTO pour que l'admin puisse le communiquer au locataire.
     */
    public LocataireDTO save(LocataireDTO locataireDTO) {
        LOG.debug("Request to save Locataire : {}", locataireDTO);
        if (locataireDTO.getDateCreation() == null) {
            locataireDTO.setDateCreation(Instant.now());
        }
        Locataire locataire = locataireMapper.toEntity(locataireDTO);
        String loginGenere = null;
        if (locataire.getId() == null && locataire.getUser() == null) {
            loginGenere = creerCompteLocataire(locataire);
        }
        locataire = locataireRepository.save(locataire);
        LocataireDTO result = locataireMapper.toDto(locataire);
        if (loginGenere != null) {
            result.setLoginGenere(loginGenere);
        } else if (locataire.getUser() != null) {
            result.setLoginGenere(locataire.getUser().getLogin());
        }
        return result;
    }

    /**
     * Update a locataire. Préserve le User existant pour ne pas l'écraser.
     */
    public LocataireDTO update(LocataireDTO locataireDTO) {
        LOG.debug("Request to update Locataire : {}", locataireDTO);
        Locataire locataire = locataireMapper.toEntity(locataireDTO);
        if (locataireDTO.getId() != null) {
            Optional<Locataire> existingLocataire = locataireRepository.findById(locataireDTO.getId());
            if (existingLocataire.isPresent()) {
                locataire.setUser(existingLocataire.orElseThrow().getUser());
            }
        }
        locataire = locataireRepository.save(locataire);
        return locataireMapper.toDto(locataire);
    }

    /**
     * Partially update a locataire.
     */
    public Optional<LocataireDTO> partialUpdate(LocataireDTO locataireDTO) {
        LOG.debug("Request to partially update Locataire : {}", locataireDTO);
        return locataireRepository
            .findById(locataireDTO.getId())
            .map(existingLocataire -> {
                locataireMapper.partialUpdate(existingLocataire, locataireDTO);
                return existingLocataire;
            })
            .map(locataireRepository::save)
            .map(locataireMapper::toDto);
    }

    /**
     * Get one locataire by id.
     */
    @Transactional(readOnly = true)
    public Optional<LocataireDTO> findOne(Long id) {
        LOG.debug("Request to get Locataire : {}", id);
        return locataireRepository.findByIdWithUser(id).map(locataireMapper::toDto);
    }

    /**
     * Delete the locataire by id.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Locataire : {}", id);
        locataireRepository.deleteById(id);
    }

    /**
     * Réinitialise le mot de passe du compte locataire au mot de passe par défaut.
     * Le locataire sera forcé de le changer à sa prochaine connexion.
     */
    public String reinitialiserMotDePasse(Long id) {
        LOG.debug("Request to reset password for Locataire : {}", id);
        Locataire locataire = locataireRepository
            .findByIdWithUser(id)
            .orElseThrow(() -> new RuntimeException("Locataire introuvable : " + id));
        if (locataire.getUser() == null) {
            String loginGenere = creerCompteLocataire(locataire);
            locataireRepository.save(locataire);
            LOG.info("Compte locataire cree pendant reinitialisation : login='{}' pour locataire '{}'", loginGenere, locataire.getCode());
            return loginGenere;
        }
        String login = locataire.getUser().getLogin();
        userService.reinitialiserMotDePasseParAdmin(login, Constants.MOT_DE_PASSE_PAR_DEFAUT);
        LOG.info("Mot de passe réinitialisé pour locataire '{}' (login='{}')", locataire.getCode(), login);
        return login;
    }

    /**
     * Crée automatiquement un compte utilisateur ROLE_LOCATAIRE pour le locataire.
     * Login = code normalisé. Mot de passe = Adm@2026. mustChangePassword = true.
     * Retourne le login généré (pour l'afficher à l'admin).
     */
    private String creerCompteLocataire(Locataire locataire) {
        String login = normaliserLogin(locataire.getCode());
        if (login.isBlank()) {
            login = "locataire-" + System.currentTimeMillis();
        }
        login = truncate(login, 48);

        // Si le login est déjà pris, générer un suffixe unique
        if (userRepository.findOneByLogin(login).isPresent()) {
            String suffixe = login + "-" + (System.currentTimeMillis() % 10000);
            login = truncate(suffixe, 50);
            if (userRepository.findOneByLogin(login).isPresent()) {
                LOG.warn("Login '{}' déjà pris, skip auto-creation pour locataire '{}'", login, locataire.getCode());
                return null;
            }
        }

        String email =
            locataire.getEmail() != null && !locataire.getEmail().isBlank()
                ? locataire.getEmail().trim().toLowerCase()
                : login + "@adm.local";

        // Réutiliser le compte existant si même email
        Optional<User> existingByEmail = userRepository.findOneByEmailIgnoreCase(email);
        if (existingByEmail.isPresent()) {
            locataire.setUser(existingByEmail.get());
            LOG.info("Compte existant réutilisé pour locataire '{}': login='{}'", locataire.getCode(), existingByEmail.get().getLogin());
            return existingByEmail.get().getLogin();
        }

        AdminUserDTO userDTO = new AdminUserDTO();
        userDTO.setLogin(login);
        userDTO.setEmail(email);
        userDTO.setLastName(truncate(locataire.getNom(), 50));
        userDTO.setActivated(true);
        userDTO.setLangKey("fr");
        userDTO.setAuthorities(Set.of(AuthoritiesConstants.LOCATAIRE));

        // mustChangePassword=true : le locataire sera forcé de changer son MDP à la première connexion
        User user = userService.createUserWithInitialPassword(userDTO, Constants.MOT_DE_PASSE_PAR_DEFAUT, true);
        locataire.setUser(user);
        LOG.info("Compte locataire créé : login='{}' pour locataire '{}' (changement MDP requis)", login, locataire.getCode());
        return login;
    }

    private String normaliserLogin(String code) {
        if (code == null || code.isBlank()) return "";
        return Normalizer.normalize(code, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase()
            .replaceAll("[^a-z0-9_.@-]+", "-")
            .replaceAll("^-+|-+$", "");
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) return value;
        return value.substring(0, maxLength);
    }
}
