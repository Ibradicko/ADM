package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.AffectationUtilisateurAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.AffectationUtilisateur;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.ProfilMetier;
import com.adm.supervision.domain.User;
import com.adm.supervision.repository.AffectationUtilisateurRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.service.AffectationUtilisateurService;
import com.adm.supervision.service.dto.AffectationUtilisateurDTO;
import com.adm.supervision.service.mapper.AffectationUtilisateurMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AffectationUtilisateurResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AffectationUtilisateurResourceIT {

    private static final LocalDate DEFAULT_DATE_DEBUT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_DEBUT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_DEBUT = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DATE_FIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FIN = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_FIN = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final String ENTITY_API_URL = "/api/affectation-utilisateurs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AffectationUtilisateurRepository affectationUtilisateurRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private AffectationUtilisateurRepository affectationUtilisateurRepositoryMock;

    @Autowired
    private AffectationUtilisateurMapper affectationUtilisateurMapper;

    @Mock
    private AffectationUtilisateurService affectationUtilisateurServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAffectationUtilisateurMockMvc;

    private AffectationUtilisateur affectationUtilisateur;

    private AffectationUtilisateur insertedAffectationUtilisateur;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AffectationUtilisateur createEntity(EntityManager em) {
        AffectationUtilisateur affectationUtilisateur = new AffectationUtilisateur()
            .dateDebut(DEFAULT_DATE_DEBUT)
            .dateFin(DEFAULT_DATE_FIN)
            .actif(DEFAULT_ACTIF);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        affectationUtilisateur.setUser(user);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        affectationUtilisateur.setBoutique(boutique);
        // Add required entity
        ProfilMetier profilMetier;
        if (TestUtil.findAll(em, ProfilMetier.class).isEmpty()) {
            profilMetier = ProfilMetierResourceIT.createEntity();
            em.persist(profilMetier);
            em.flush();
        } else {
            profilMetier = TestUtil.findAll(em, ProfilMetier.class).get(0);
        }
        affectationUtilisateur.setProfil(profilMetier);
        return affectationUtilisateur;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AffectationUtilisateur createUpdatedEntity(EntityManager em) {
        AffectationUtilisateur updatedAffectationUtilisateur = new AffectationUtilisateur()
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .actif(UPDATED_ACTIF);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedAffectationUtilisateur.setUser(user);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createUpdatedEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        updatedAffectationUtilisateur.setBoutique(boutique);
        // Add required entity
        ProfilMetier profilMetier;
        if (TestUtil.findAll(em, ProfilMetier.class).isEmpty()) {
            profilMetier = ProfilMetierResourceIT.createUpdatedEntity();
            em.persist(profilMetier);
            em.flush();
        } else {
            profilMetier = TestUtil.findAll(em, ProfilMetier.class).get(0);
        }
        updatedAffectationUtilisateur.setProfil(profilMetier);
        return updatedAffectationUtilisateur;
    }

    @BeforeEach
    void initTest() {
        affectationUtilisateur = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedAffectationUtilisateur != null) {
            affectationUtilisateurRepository.delete(insertedAffectationUtilisateur);
            insertedAffectationUtilisateur = null;
        }
    }

    @Test
    @Transactional
    void createAffectationUtilisateur() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AffectationUtilisateur
        AffectationUtilisateurDTO affectationUtilisateurDTO = affectationUtilisateurMapper.toDto(affectationUtilisateur);
        var returnedAffectationUtilisateurDTO = om.readValue(
            restAffectationUtilisateurMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(affectationUtilisateurDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AffectationUtilisateurDTO.class
        );

        // Validate the AffectationUtilisateur in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAffectationUtilisateur = affectationUtilisateurMapper.toEntity(returnedAffectationUtilisateurDTO);
        assertAffectationUtilisateurUpdatableFieldsEquals(
            returnedAffectationUtilisateur,
            getPersistedAffectationUtilisateur(returnedAffectationUtilisateur)
        );

        insertedAffectationUtilisateur = returnedAffectationUtilisateur;
    }

    @Test
    @Transactional
    void createAffectationUtilisateurWithExistingId() throws Exception {
        // Create the AffectationUtilisateur with an existing ID
        affectationUtilisateur.setId(1L);
        AffectationUtilisateurDTO affectationUtilisateurDTO = affectationUtilisateurMapper.toDto(affectationUtilisateur);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAffectationUtilisateurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(affectationUtilisateurDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AffectationUtilisateur in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateDebutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        affectationUtilisateur.setDateDebut(null);

        // Create the AffectationUtilisateur, which fails.
        AffectationUtilisateurDTO affectationUtilisateurDTO = affectationUtilisateurMapper.toDto(affectationUtilisateur);

        restAffectationUtilisateurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(affectationUtilisateurDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        affectationUtilisateur.setActif(null);

        // Create the AffectationUtilisateur, which fails.
        AffectationUtilisateurDTO affectationUtilisateurDTO = affectationUtilisateurMapper.toDto(affectationUtilisateur);

        restAffectationUtilisateurMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(affectationUtilisateurDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateurs() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList
        restAffectationUtilisateurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(affectationUtilisateur.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAffectationUtilisateursWithEagerRelationshipsIsEnabled() throws Exception {
        when(affectationUtilisateurServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAffectationUtilisateurMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(affectationUtilisateurServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAffectationUtilisateursWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(affectationUtilisateurServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAffectationUtilisateurMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(affectationUtilisateurRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAffectationUtilisateur() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get the affectationUtilisateur
        restAffectationUtilisateurMockMvc
            .perform(get(ENTITY_API_URL_ID, affectationUtilisateur.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(affectationUtilisateur.getId().intValue()))
            .andExpect(jsonPath("$.dateDebut").value(DEFAULT_DATE_DEBUT.toString()))
            .andExpect(jsonPath("$.dateFin").value(DEFAULT_DATE_FIN.toString()))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF));
    }

    @Test
    @Transactional
    void getAffectationUtilisateursByIdFiltering() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        Long id = affectationUtilisateur.getId();

        defaultAffectationUtilisateurFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAffectationUtilisateurFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAffectationUtilisateurFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateDebutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateDebut equals to
        defaultAffectationUtilisateurFiltering("dateDebut.equals=" + DEFAULT_DATE_DEBUT, "dateDebut.equals=" + UPDATED_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateDebutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateDebut in
        defaultAffectationUtilisateurFiltering(
            "dateDebut.in=" + DEFAULT_DATE_DEBUT + "," + UPDATED_DATE_DEBUT,
            "dateDebut.in=" + UPDATED_DATE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateDebutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateDebut is not null
        defaultAffectationUtilisateurFiltering("dateDebut.specified=true", "dateDebut.specified=false");
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateDebutIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateDebut is greater than or equal to
        defaultAffectationUtilisateurFiltering(
            "dateDebut.greaterThanOrEqual=" + DEFAULT_DATE_DEBUT,
            "dateDebut.greaterThanOrEqual=" + UPDATED_DATE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateDebutIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateDebut is less than or equal to
        defaultAffectationUtilisateurFiltering(
            "dateDebut.lessThanOrEqual=" + DEFAULT_DATE_DEBUT,
            "dateDebut.lessThanOrEqual=" + SMALLER_DATE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateDebutIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateDebut is less than
        defaultAffectationUtilisateurFiltering("dateDebut.lessThan=" + UPDATED_DATE_DEBUT, "dateDebut.lessThan=" + DEFAULT_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateDebutIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateDebut is greater than
        defaultAffectationUtilisateurFiltering(
            "dateDebut.greaterThan=" + SMALLER_DATE_DEBUT,
            "dateDebut.greaterThan=" + DEFAULT_DATE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateFinIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateFin equals to
        defaultAffectationUtilisateurFiltering("dateFin.equals=" + DEFAULT_DATE_FIN, "dateFin.equals=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateFinIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateFin in
        defaultAffectationUtilisateurFiltering("dateFin.in=" + DEFAULT_DATE_FIN + "," + UPDATED_DATE_FIN, "dateFin.in=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateFinIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateFin is not null
        defaultAffectationUtilisateurFiltering("dateFin.specified=true", "dateFin.specified=false");
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateFinIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateFin is greater than or equal to
        defaultAffectationUtilisateurFiltering(
            "dateFin.greaterThanOrEqual=" + DEFAULT_DATE_FIN,
            "dateFin.greaterThanOrEqual=" + UPDATED_DATE_FIN
        );
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateFinIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateFin is less than or equal to
        defaultAffectationUtilisateurFiltering(
            "dateFin.lessThanOrEqual=" + DEFAULT_DATE_FIN,
            "dateFin.lessThanOrEqual=" + SMALLER_DATE_FIN
        );
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateFinIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateFin is less than
        defaultAffectationUtilisateurFiltering("dateFin.lessThan=" + UPDATED_DATE_FIN, "dateFin.lessThan=" + DEFAULT_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByDateFinIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where dateFin is greater than
        defaultAffectationUtilisateurFiltering("dateFin.greaterThan=" + SMALLER_DATE_FIN, "dateFin.greaterThan=" + DEFAULT_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByActifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where actif equals to
        defaultAffectationUtilisateurFiltering("actif.equals=" + DEFAULT_ACTIF, "actif.equals=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByActifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where actif in
        defaultAffectationUtilisateurFiltering("actif.in=" + DEFAULT_ACTIF + "," + UPDATED_ACTIF, "actif.in=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByActifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        // Get all the affectationUtilisateurList where actif is not null
        defaultAffectationUtilisateurFiltering("actif.specified=true", "actif.specified=false");
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        affectationUtilisateur.setUser(user);
        affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);
        Long userId = user.getId();
        // Get all the affectationUtilisateurList where user equals to userId
        defaultAffectationUtilisateurShouldBeFound("userId.equals=" + userId);

        // Get all the affectationUtilisateurList where user equals to (userId + 1)
        defaultAffectationUtilisateurShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByBoutiqueIsEqualToSomething() throws Exception {
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);
            boutique = BoutiqueResourceIT.createEntity();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutique);
        em.flush();
        affectationUtilisateur.setBoutique(boutique);
        affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);
        Long boutiqueId = boutique.getId();
        // Get all the affectationUtilisateurList where boutique equals to boutiqueId
        defaultAffectationUtilisateurShouldBeFound("boutiqueId.equals=" + boutiqueId);

        // Get all the affectationUtilisateurList where boutique equals to (boutiqueId + 1)
        defaultAffectationUtilisateurShouldNotBeFound("boutiqueId.equals=" + (boutiqueId + 1));
    }

    @Test
    @Transactional
    void getAllAffectationUtilisateursByProfilIsEqualToSomething() throws Exception {
        ProfilMetier profil;
        if (TestUtil.findAll(em, ProfilMetier.class).isEmpty()) {
            affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);
            profil = ProfilMetierResourceIT.createEntity();
        } else {
            profil = TestUtil.findAll(em, ProfilMetier.class).get(0);
        }
        em.persist(profil);
        em.flush();
        affectationUtilisateur.setProfil(profil);
        affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);
        Long profilId = profil.getId();
        // Get all the affectationUtilisateurList where profil equals to profilId
        defaultAffectationUtilisateurShouldBeFound("profilId.equals=" + profilId);

        // Get all the affectationUtilisateurList where profil equals to (profilId + 1)
        defaultAffectationUtilisateurShouldNotBeFound("profilId.equals=" + (profilId + 1));
    }

    private void defaultAffectationUtilisateurFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAffectationUtilisateurShouldBeFound(shouldBeFound);
        defaultAffectationUtilisateurShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAffectationUtilisateurShouldBeFound(String filter) throws Exception {
        restAffectationUtilisateurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(affectationUtilisateur.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));

        // Check, that the count call also returns 1
        restAffectationUtilisateurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAffectationUtilisateurShouldNotBeFound(String filter) throws Exception {
        restAffectationUtilisateurMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAffectationUtilisateurMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAffectationUtilisateur() throws Exception {
        // Get the affectationUtilisateur
        restAffectationUtilisateurMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAffectationUtilisateur() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the affectationUtilisateur
        AffectationUtilisateur updatedAffectationUtilisateur = affectationUtilisateurRepository
            .findById(affectationUtilisateur.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedAffectationUtilisateur are not directly saved in db
        em.detach(updatedAffectationUtilisateur);
        updatedAffectationUtilisateur.dateDebut(UPDATED_DATE_DEBUT).dateFin(UPDATED_DATE_FIN).actif(UPDATED_ACTIF);
        AffectationUtilisateurDTO affectationUtilisateurDTO = affectationUtilisateurMapper.toDto(updatedAffectationUtilisateur);

        restAffectationUtilisateurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, affectationUtilisateurDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(affectationUtilisateurDTO))
            )
            .andExpect(status().isOk());

        // Validate the AffectationUtilisateur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAffectationUtilisateurToMatchAllProperties(updatedAffectationUtilisateur);
    }

    @Test
    @Transactional
    void putNonExistingAffectationUtilisateur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        affectationUtilisateur.setId(longCount.incrementAndGet());

        // Create the AffectationUtilisateur
        AffectationUtilisateurDTO affectationUtilisateurDTO = affectationUtilisateurMapper.toDto(affectationUtilisateur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAffectationUtilisateurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, affectationUtilisateurDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(affectationUtilisateurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AffectationUtilisateur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAffectationUtilisateur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        affectationUtilisateur.setId(longCount.incrementAndGet());

        // Create the AffectationUtilisateur
        AffectationUtilisateurDTO affectationUtilisateurDTO = affectationUtilisateurMapper.toDto(affectationUtilisateur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAffectationUtilisateurMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(affectationUtilisateurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AffectationUtilisateur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAffectationUtilisateur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        affectationUtilisateur.setId(longCount.incrementAndGet());

        // Create the AffectationUtilisateur
        AffectationUtilisateurDTO affectationUtilisateurDTO = affectationUtilisateurMapper.toDto(affectationUtilisateur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAffectationUtilisateurMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(affectationUtilisateurDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AffectationUtilisateur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAffectationUtilisateurWithPatch() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the affectationUtilisateur using partial update
        AffectationUtilisateur partialUpdatedAffectationUtilisateur = new AffectationUtilisateur();
        partialUpdatedAffectationUtilisateur.setId(affectationUtilisateur.getId());

        partialUpdatedAffectationUtilisateur.dateDebut(UPDATED_DATE_DEBUT).dateFin(UPDATED_DATE_FIN).actif(UPDATED_ACTIF);

        restAffectationUtilisateurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAffectationUtilisateur.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAffectationUtilisateur))
            )
            .andExpect(status().isOk());

        // Validate the AffectationUtilisateur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAffectationUtilisateurUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAffectationUtilisateur, affectationUtilisateur),
            getPersistedAffectationUtilisateur(affectationUtilisateur)
        );
    }

    @Test
    @Transactional
    void fullUpdateAffectationUtilisateurWithPatch() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the affectationUtilisateur using partial update
        AffectationUtilisateur partialUpdatedAffectationUtilisateur = new AffectationUtilisateur();
        partialUpdatedAffectationUtilisateur.setId(affectationUtilisateur.getId());

        partialUpdatedAffectationUtilisateur.dateDebut(UPDATED_DATE_DEBUT).dateFin(UPDATED_DATE_FIN).actif(UPDATED_ACTIF);

        restAffectationUtilisateurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAffectationUtilisateur.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAffectationUtilisateur))
            )
            .andExpect(status().isOk());

        // Validate the AffectationUtilisateur in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAffectationUtilisateurUpdatableFieldsEquals(
            partialUpdatedAffectationUtilisateur,
            getPersistedAffectationUtilisateur(partialUpdatedAffectationUtilisateur)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAffectationUtilisateur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        affectationUtilisateur.setId(longCount.incrementAndGet());

        // Create the AffectationUtilisateur
        AffectationUtilisateurDTO affectationUtilisateurDTO = affectationUtilisateurMapper.toDto(affectationUtilisateur);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAffectationUtilisateurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, affectationUtilisateurDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(affectationUtilisateurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AffectationUtilisateur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAffectationUtilisateur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        affectationUtilisateur.setId(longCount.incrementAndGet());

        // Create the AffectationUtilisateur
        AffectationUtilisateurDTO affectationUtilisateurDTO = affectationUtilisateurMapper.toDto(affectationUtilisateur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAffectationUtilisateurMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(affectationUtilisateurDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AffectationUtilisateur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAffectationUtilisateur() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        affectationUtilisateur.setId(longCount.incrementAndGet());

        // Create the AffectationUtilisateur
        AffectationUtilisateurDTO affectationUtilisateurDTO = affectationUtilisateurMapper.toDto(affectationUtilisateur);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAffectationUtilisateurMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(affectationUtilisateurDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AffectationUtilisateur in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAffectationUtilisateur() throws Exception {
        // Initialize the database
        insertedAffectationUtilisateur = affectationUtilisateurRepository.saveAndFlush(affectationUtilisateur);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the affectationUtilisateur
        restAffectationUtilisateurMockMvc
            .perform(delete(ENTITY_API_URL_ID, affectationUtilisateur.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return affectationUtilisateurRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected AffectationUtilisateur getPersistedAffectationUtilisateur(AffectationUtilisateur affectationUtilisateur) {
        return affectationUtilisateurRepository.findById(affectationUtilisateur.getId()).orElseThrow();
    }

    protected void assertPersistedAffectationUtilisateurToMatchAllProperties(AffectationUtilisateur expectedAffectationUtilisateur) {
        assertAffectationUtilisateurAllPropertiesEquals(
            expectedAffectationUtilisateur,
            getPersistedAffectationUtilisateur(expectedAffectationUtilisateur)
        );
    }

    protected void assertPersistedAffectationUtilisateurToMatchUpdatableProperties(AffectationUtilisateur expectedAffectationUtilisateur) {
        assertAffectationUtilisateurAllUpdatablePropertiesEquals(
            expectedAffectationUtilisateur,
            getPersistedAffectationUtilisateur(expectedAffectationUtilisateur)
        );
    }
}
