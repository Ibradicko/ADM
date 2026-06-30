package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.HistoriqueCodeBarresAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.HistoriqueCodeBarres;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.User;
import com.adm.supervision.repository.HistoriqueCodeBarresRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.service.HistoriqueCodeBarresService;
import com.adm.supervision.service.dto.HistoriqueCodeBarresDTO;
import com.adm.supervision.service.mapper.HistoriqueCodeBarresMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link HistoriqueCodeBarresResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class HistoriqueCodeBarresResourceIT {

    private static final String DEFAULT_ANCIEN_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ANCIEN_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NOUVEAU_CODE = "AAAAAAAAAA";
    private static final String UPDATED_NOUVEAU_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_MOTIF = "AAAAAAAAAA";
    private static final String UPDATED_MOTIF = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_CHANGEMENT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CHANGEMENT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/historique-code-barres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HistoriqueCodeBarresRepository historiqueCodeBarresRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private HistoriqueCodeBarresRepository historiqueCodeBarresRepositoryMock;

    @Autowired
    private HistoriqueCodeBarresMapper historiqueCodeBarresMapper;

    @Mock
    private HistoriqueCodeBarresService historiqueCodeBarresServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHistoriqueCodeBarresMockMvc;

    private HistoriqueCodeBarres historiqueCodeBarres;

    private HistoriqueCodeBarres insertedHistoriqueCodeBarres;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoriqueCodeBarres createEntity(EntityManager em) {
        HistoriqueCodeBarres historiqueCodeBarres = new HistoriqueCodeBarres()
            .ancienCode(DEFAULT_ANCIEN_CODE)
            .nouveauCode(DEFAULT_NOUVEAU_CODE)
            .motif(DEFAULT_MOTIF)
            .dateChangement(DEFAULT_DATE_CHANGEMENT);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        historiqueCodeBarres.setProduit(produit);
        return historiqueCodeBarres;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoriqueCodeBarres createUpdatedEntity(EntityManager em) {
        HistoriqueCodeBarres updatedHistoriqueCodeBarres = new HistoriqueCodeBarres()
            .ancienCode(UPDATED_ANCIEN_CODE)
            .nouveauCode(UPDATED_NOUVEAU_CODE)
            .motif(UPDATED_MOTIF)
            .dateChangement(UPDATED_DATE_CHANGEMENT);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createUpdatedEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        updatedHistoriqueCodeBarres.setProduit(produit);
        return updatedHistoriqueCodeBarres;
    }

    @BeforeEach
    void initTest() {
        historiqueCodeBarres = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedHistoriqueCodeBarres != null) {
            historiqueCodeBarresRepository.delete(insertedHistoriqueCodeBarres);
            insertedHistoriqueCodeBarres = null;
        }
    }

    @Test
    @Transactional
    void createHistoriqueCodeBarres() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the HistoriqueCodeBarres
        HistoriqueCodeBarresDTO historiqueCodeBarresDTO = historiqueCodeBarresMapper.toDto(historiqueCodeBarres);
        var returnedHistoriqueCodeBarresDTO = om.readValue(
            restHistoriqueCodeBarresMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(historiqueCodeBarresDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HistoriqueCodeBarresDTO.class
        );

        // Validate the HistoriqueCodeBarres in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedHistoriqueCodeBarres = historiqueCodeBarresMapper.toEntity(returnedHistoriqueCodeBarresDTO);
        assertHistoriqueCodeBarresUpdatableFieldsEquals(
            returnedHistoriqueCodeBarres,
            getPersistedHistoriqueCodeBarres(returnedHistoriqueCodeBarres)
        );

        insertedHistoriqueCodeBarres = returnedHistoriqueCodeBarres;
    }

    @Test
    @Transactional
    void createHistoriqueCodeBarresWithExistingId() throws Exception {
        // Create the HistoriqueCodeBarres with an existing ID
        historiqueCodeBarres.setId(1L);
        HistoriqueCodeBarresDTO historiqueCodeBarresDTO = historiqueCodeBarresMapper.toDto(historiqueCodeBarres);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHistoriqueCodeBarresMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(historiqueCodeBarresDTO)))
            .andExpect(status().isBadRequest());

        // Validate the HistoriqueCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNouveauCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        historiqueCodeBarres.setNouveauCode(null);

        // Create the HistoriqueCodeBarres, which fails.
        HistoriqueCodeBarresDTO historiqueCodeBarresDTO = historiqueCodeBarresMapper.toDto(historiqueCodeBarres);

        restHistoriqueCodeBarresMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(historiqueCodeBarresDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateChangementIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        historiqueCodeBarres.setDateChangement(null);

        // Create the HistoriqueCodeBarres, which fails.
        HistoriqueCodeBarresDTO historiqueCodeBarresDTO = historiqueCodeBarresMapper.toDto(historiqueCodeBarres);

        restHistoriqueCodeBarresMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(historiqueCodeBarresDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarreses() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList
        restHistoriqueCodeBarresMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(historiqueCodeBarres.getId().intValue())))
            .andExpect(jsonPath("$.[*].ancienCode").value(hasItem(DEFAULT_ANCIEN_CODE)))
            .andExpect(jsonPath("$.[*].nouveauCode").value(hasItem(DEFAULT_NOUVEAU_CODE)))
            .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)))
            .andExpect(jsonPath("$.[*].dateChangement").value(hasItem(DEFAULT_DATE_CHANGEMENT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHistoriqueCodeBarresesWithEagerRelationshipsIsEnabled() throws Exception {
        when(historiqueCodeBarresServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHistoriqueCodeBarresMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(historiqueCodeBarresServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHistoriqueCodeBarresesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(historiqueCodeBarresServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHistoriqueCodeBarresMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(historiqueCodeBarresRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getHistoriqueCodeBarres() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get the historiqueCodeBarres
        restHistoriqueCodeBarresMockMvc
            .perform(get(ENTITY_API_URL_ID, historiqueCodeBarres.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(historiqueCodeBarres.getId().intValue()))
            .andExpect(jsonPath("$.ancienCode").value(DEFAULT_ANCIEN_CODE))
            .andExpect(jsonPath("$.nouveauCode").value(DEFAULT_NOUVEAU_CODE))
            .andExpect(jsonPath("$.motif").value(DEFAULT_MOTIF))
            .andExpect(jsonPath("$.dateChangement").value(DEFAULT_DATE_CHANGEMENT.toString()));
    }

    @Test
    @Transactional
    void getHistoriqueCodeBarresesByIdFiltering() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        Long id = historiqueCodeBarres.getId();

        defaultHistoriqueCodeBarresFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultHistoriqueCodeBarresFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultHistoriqueCodeBarresFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByAncienCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where ancienCode equals to
        defaultHistoriqueCodeBarresFiltering("ancienCode.equals=" + DEFAULT_ANCIEN_CODE, "ancienCode.equals=" + UPDATED_ANCIEN_CODE);
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByAncienCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where ancienCode in
        defaultHistoriqueCodeBarresFiltering(
            "ancienCode.in=" + DEFAULT_ANCIEN_CODE + "," + UPDATED_ANCIEN_CODE,
            "ancienCode.in=" + UPDATED_ANCIEN_CODE
        );
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByAncienCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where ancienCode is not null
        defaultHistoriqueCodeBarresFiltering("ancienCode.specified=true", "ancienCode.specified=false");
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByAncienCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where ancienCode contains
        defaultHistoriqueCodeBarresFiltering("ancienCode.contains=" + DEFAULT_ANCIEN_CODE, "ancienCode.contains=" + UPDATED_ANCIEN_CODE);
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByAncienCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where ancienCode does not contain
        defaultHistoriqueCodeBarresFiltering(
            "ancienCode.doesNotContain=" + UPDATED_ANCIEN_CODE,
            "ancienCode.doesNotContain=" + DEFAULT_ANCIEN_CODE
        );
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByNouveauCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where nouveauCode equals to
        defaultHistoriqueCodeBarresFiltering("nouveauCode.equals=" + DEFAULT_NOUVEAU_CODE, "nouveauCode.equals=" + UPDATED_NOUVEAU_CODE);
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByNouveauCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where nouveauCode in
        defaultHistoriqueCodeBarresFiltering(
            "nouveauCode.in=" + DEFAULT_NOUVEAU_CODE + "," + UPDATED_NOUVEAU_CODE,
            "nouveauCode.in=" + UPDATED_NOUVEAU_CODE
        );
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByNouveauCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where nouveauCode is not null
        defaultHistoriqueCodeBarresFiltering("nouveauCode.specified=true", "nouveauCode.specified=false");
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByNouveauCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where nouveauCode contains
        defaultHistoriqueCodeBarresFiltering(
            "nouveauCode.contains=" + DEFAULT_NOUVEAU_CODE,
            "nouveauCode.contains=" + UPDATED_NOUVEAU_CODE
        );
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByNouveauCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where nouveauCode does not contain
        defaultHistoriqueCodeBarresFiltering(
            "nouveauCode.doesNotContain=" + UPDATED_NOUVEAU_CODE,
            "nouveauCode.doesNotContain=" + DEFAULT_NOUVEAU_CODE
        );
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByMotifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where motif equals to
        defaultHistoriqueCodeBarresFiltering("motif.equals=" + DEFAULT_MOTIF, "motif.equals=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByMotifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where motif in
        defaultHistoriqueCodeBarresFiltering("motif.in=" + DEFAULT_MOTIF + "," + UPDATED_MOTIF, "motif.in=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByMotifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where motif is not null
        defaultHistoriqueCodeBarresFiltering("motif.specified=true", "motif.specified=false");
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByMotifContainsSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where motif contains
        defaultHistoriqueCodeBarresFiltering("motif.contains=" + DEFAULT_MOTIF, "motif.contains=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByMotifNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where motif does not contain
        defaultHistoriqueCodeBarresFiltering("motif.doesNotContain=" + UPDATED_MOTIF, "motif.doesNotContain=" + DEFAULT_MOTIF);
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByDateChangementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where dateChangement equals to
        defaultHistoriqueCodeBarresFiltering(
            "dateChangement.equals=" + DEFAULT_DATE_CHANGEMENT,
            "dateChangement.equals=" + UPDATED_DATE_CHANGEMENT
        );
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByDateChangementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where dateChangement in
        defaultHistoriqueCodeBarresFiltering(
            "dateChangement.in=" + DEFAULT_DATE_CHANGEMENT + "," + UPDATED_DATE_CHANGEMENT,
            "dateChangement.in=" + UPDATED_DATE_CHANGEMENT
        );
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByDateChangementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        // Get all the historiqueCodeBarresList where dateChangement is not null
        defaultHistoriqueCodeBarresFiltering("dateChangement.specified=true", "dateChangement.specified=false");
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByProduitIsEqualToSomething() throws Exception {
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);
            produit = ProduitResourceIT.createEntity(em);
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        em.persist(produit);
        em.flush();
        historiqueCodeBarres.setProduit(produit);
        historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);
        Long produitId = produit.getId();
        // Get all the historiqueCodeBarresList where produit equals to produitId
        defaultHistoriqueCodeBarresShouldBeFound("produitId.equals=" + produitId);

        // Get all the historiqueCodeBarresList where produit equals to (produitId + 1)
        defaultHistoriqueCodeBarresShouldNotBeFound("produitId.equals=" + (produitId + 1));
    }

    @Test
    @Transactional
    void getAllHistoriqueCodeBarresesByUtilisateurIsEqualToSomething() throws Exception {
        User utilisateur;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);
            utilisateur = UserResourceIT.createEntity();
        } else {
            utilisateur = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(utilisateur);
        em.flush();
        historiqueCodeBarres.setUtilisateur(utilisateur);
        historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);
        Long utilisateurId = utilisateur.getId();
        // Get all the historiqueCodeBarresList where utilisateur equals to utilisateurId
        defaultHistoriqueCodeBarresShouldBeFound("utilisateurId.equals=" + utilisateurId);

        // Get all the historiqueCodeBarresList where utilisateur equals to (utilisateurId + 1)
        defaultHistoriqueCodeBarresShouldNotBeFound("utilisateurId.equals=" + (utilisateurId + 1));
    }

    private void defaultHistoriqueCodeBarresFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultHistoriqueCodeBarresShouldBeFound(shouldBeFound);
        defaultHistoriqueCodeBarresShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultHistoriqueCodeBarresShouldBeFound(String filter) throws Exception {
        restHistoriqueCodeBarresMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(historiqueCodeBarres.getId().intValue())))
            .andExpect(jsonPath("$.[*].ancienCode").value(hasItem(DEFAULT_ANCIEN_CODE)))
            .andExpect(jsonPath("$.[*].nouveauCode").value(hasItem(DEFAULT_NOUVEAU_CODE)))
            .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)))
            .andExpect(jsonPath("$.[*].dateChangement").value(hasItem(DEFAULT_DATE_CHANGEMENT.toString())));

        // Check, that the count call also returns 1
        restHistoriqueCodeBarresMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultHistoriqueCodeBarresShouldNotBeFound(String filter) throws Exception {
        restHistoriqueCodeBarresMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restHistoriqueCodeBarresMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingHistoriqueCodeBarres() throws Exception {
        // Get the historiqueCodeBarres
        restHistoriqueCodeBarresMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHistoriqueCodeBarres() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the historiqueCodeBarres
        HistoriqueCodeBarres updatedHistoriqueCodeBarres = historiqueCodeBarresRepository
            .findById(historiqueCodeBarres.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedHistoriqueCodeBarres are not directly saved in db
        em.detach(updatedHistoriqueCodeBarres);
        updatedHistoriqueCodeBarres
            .ancienCode(UPDATED_ANCIEN_CODE)
            .nouveauCode(UPDATED_NOUVEAU_CODE)
            .motif(UPDATED_MOTIF)
            .dateChangement(UPDATED_DATE_CHANGEMENT);
        HistoriqueCodeBarresDTO historiqueCodeBarresDTO = historiqueCodeBarresMapper.toDto(updatedHistoriqueCodeBarres);

        restHistoriqueCodeBarresMockMvc
            .perform(
                put(ENTITY_API_URL_ID, historiqueCodeBarresDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(historiqueCodeBarresDTO))
            )
            .andExpect(status().isOk());

        // Validate the HistoriqueCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHistoriqueCodeBarresToMatchAllProperties(updatedHistoriqueCodeBarres);
    }

    @Test
    @Transactional
    void putNonExistingHistoriqueCodeBarres() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        historiqueCodeBarres.setId(longCount.incrementAndGet());

        // Create the HistoriqueCodeBarres
        HistoriqueCodeBarresDTO historiqueCodeBarresDTO = historiqueCodeBarresMapper.toDto(historiqueCodeBarres);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistoriqueCodeBarresMockMvc
            .perform(
                put(ENTITY_API_URL_ID, historiqueCodeBarresDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(historiqueCodeBarresDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoriqueCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHistoriqueCodeBarres() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        historiqueCodeBarres.setId(longCount.incrementAndGet());

        // Create the HistoriqueCodeBarres
        HistoriqueCodeBarresDTO historiqueCodeBarresDTO = historiqueCodeBarresMapper.toDto(historiqueCodeBarres);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoriqueCodeBarresMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(historiqueCodeBarresDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoriqueCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHistoriqueCodeBarres() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        historiqueCodeBarres.setId(longCount.incrementAndGet());

        // Create the HistoriqueCodeBarres
        HistoriqueCodeBarresDTO historiqueCodeBarresDTO = historiqueCodeBarresMapper.toDto(historiqueCodeBarres);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoriqueCodeBarresMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(historiqueCodeBarresDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistoriqueCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHistoriqueCodeBarresWithPatch() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the historiqueCodeBarres using partial update
        HistoriqueCodeBarres partialUpdatedHistoriqueCodeBarres = new HistoriqueCodeBarres();
        partialUpdatedHistoriqueCodeBarres.setId(historiqueCodeBarres.getId());

        partialUpdatedHistoriqueCodeBarres.nouveauCode(UPDATED_NOUVEAU_CODE).motif(UPDATED_MOTIF);

        restHistoriqueCodeBarresMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistoriqueCodeBarres.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHistoriqueCodeBarres))
            )
            .andExpect(status().isOk());

        // Validate the HistoriqueCodeBarres in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHistoriqueCodeBarresUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedHistoriqueCodeBarres, historiqueCodeBarres),
            getPersistedHistoriqueCodeBarres(historiqueCodeBarres)
        );
    }

    @Test
    @Transactional
    void fullUpdateHistoriqueCodeBarresWithPatch() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the historiqueCodeBarres using partial update
        HistoriqueCodeBarres partialUpdatedHistoriqueCodeBarres = new HistoriqueCodeBarres();
        partialUpdatedHistoriqueCodeBarres.setId(historiqueCodeBarres.getId());

        partialUpdatedHistoriqueCodeBarres
            .ancienCode(UPDATED_ANCIEN_CODE)
            .nouveauCode(UPDATED_NOUVEAU_CODE)
            .motif(UPDATED_MOTIF)
            .dateChangement(UPDATED_DATE_CHANGEMENT);

        restHistoriqueCodeBarresMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistoriqueCodeBarres.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHistoriqueCodeBarres))
            )
            .andExpect(status().isOk());

        // Validate the HistoriqueCodeBarres in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHistoriqueCodeBarresUpdatableFieldsEquals(
            partialUpdatedHistoriqueCodeBarres,
            getPersistedHistoriqueCodeBarres(partialUpdatedHistoriqueCodeBarres)
        );
    }

    @Test
    @Transactional
    void patchNonExistingHistoriqueCodeBarres() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        historiqueCodeBarres.setId(longCount.incrementAndGet());

        // Create the HistoriqueCodeBarres
        HistoriqueCodeBarresDTO historiqueCodeBarresDTO = historiqueCodeBarresMapper.toDto(historiqueCodeBarres);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistoriqueCodeBarresMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, historiqueCodeBarresDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(historiqueCodeBarresDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoriqueCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHistoriqueCodeBarres() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        historiqueCodeBarres.setId(longCount.incrementAndGet());

        // Create the HistoriqueCodeBarres
        HistoriqueCodeBarresDTO historiqueCodeBarresDTO = historiqueCodeBarresMapper.toDto(historiqueCodeBarres);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoriqueCodeBarresMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(historiqueCodeBarresDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoriqueCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHistoriqueCodeBarres() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        historiqueCodeBarres.setId(longCount.incrementAndGet());

        // Create the HistoriqueCodeBarres
        HistoriqueCodeBarresDTO historiqueCodeBarresDTO = historiqueCodeBarresMapper.toDto(historiqueCodeBarres);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoriqueCodeBarresMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(historiqueCodeBarresDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistoriqueCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHistoriqueCodeBarres() throws Exception {
        // Initialize the database
        insertedHistoriqueCodeBarres = historiqueCodeBarresRepository.saveAndFlush(historiqueCodeBarres);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the historiqueCodeBarres
        restHistoriqueCodeBarresMockMvc
            .perform(delete(ENTITY_API_URL_ID, historiqueCodeBarres.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return historiqueCodeBarresRepository.count();
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

    protected HistoriqueCodeBarres getPersistedHistoriqueCodeBarres(HistoriqueCodeBarres historiqueCodeBarres) {
        return historiqueCodeBarresRepository.findById(historiqueCodeBarres.getId()).orElseThrow();
    }

    protected void assertPersistedHistoriqueCodeBarresToMatchAllProperties(HistoriqueCodeBarres expectedHistoriqueCodeBarres) {
        assertHistoriqueCodeBarresAllPropertiesEquals(
            expectedHistoriqueCodeBarres,
            getPersistedHistoriqueCodeBarres(expectedHistoriqueCodeBarres)
        );
    }

    protected void assertPersistedHistoriqueCodeBarresToMatchUpdatableProperties(HistoriqueCodeBarres expectedHistoriqueCodeBarres) {
        assertHistoriqueCodeBarresAllUpdatablePropertiesEquals(
            expectedHistoriqueCodeBarres,
            getPersistedHistoriqueCodeBarres(expectedHistoriqueCodeBarres)
        );
    }
}
