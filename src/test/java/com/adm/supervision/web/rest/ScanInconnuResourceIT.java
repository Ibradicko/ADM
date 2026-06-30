package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.ScanInconnuAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.ScanInconnu;
import com.adm.supervision.repository.ScanInconnuRepository;
import com.adm.supervision.service.ScanInconnuService;
import com.adm.supervision.service.dto.ScanInconnuDTO;
import com.adm.supervision.service.mapper.ScanInconnuMapper;
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
 * Integration tests for the {@link ScanInconnuResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ScanInconnuResourceIT {

    private static final String DEFAULT_CODE_SCANNE = "AAAAAAAAAA";
    private static final String UPDATED_CODE_SCANNE = "BBBBBBBBBB";

    private static final String DEFAULT_ECRAN_ORIGINE = "AAAAAAAAAA";
    private static final String UPDATED_ECRAN_ORIGINE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_SCAN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_SCAN = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_COMMENTAIRE = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_RESOLU = false;
    private static final Boolean UPDATED_RESOLU = true;

    private static final String ENTITY_API_URL = "/api/scan-inconnus";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ScanInconnuRepository scanInconnuRepository;

    @Mock
    private ScanInconnuRepository scanInconnuRepositoryMock;

    @Autowired
    private ScanInconnuMapper scanInconnuMapper;

    @Mock
    private ScanInconnuService scanInconnuServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restScanInconnuMockMvc;

    private ScanInconnu scanInconnu;

    private ScanInconnu insertedScanInconnu;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScanInconnu createEntity(EntityManager em) {
        ScanInconnu scanInconnu = new ScanInconnu()
            .codeScanne(DEFAULT_CODE_SCANNE)
            .ecranOrigine(DEFAULT_ECRAN_ORIGINE)
            .dateScan(DEFAULT_DATE_SCAN)
            .commentaire(DEFAULT_COMMENTAIRE)
            .resolu(DEFAULT_RESOLU);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        scanInconnu.setBoutique(boutique);
        return scanInconnu;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScanInconnu createUpdatedEntity(EntityManager em) {
        ScanInconnu updatedScanInconnu = new ScanInconnu()
            .codeScanne(UPDATED_CODE_SCANNE)
            .ecranOrigine(UPDATED_ECRAN_ORIGINE)
            .dateScan(UPDATED_DATE_SCAN)
            .commentaire(UPDATED_COMMENTAIRE)
            .resolu(UPDATED_RESOLU);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createUpdatedEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        updatedScanInconnu.setBoutique(boutique);
        return updatedScanInconnu;
    }

    @BeforeEach
    void initTest() {
        scanInconnu = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedScanInconnu != null) {
            scanInconnuRepository.delete(insertedScanInconnu);
            insertedScanInconnu = null;
        }
    }

    @Test
    @Transactional
    void createScanInconnu() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ScanInconnu
        ScanInconnuDTO scanInconnuDTO = scanInconnuMapper.toDto(scanInconnu);
        var returnedScanInconnuDTO = om.readValue(
            restScanInconnuMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scanInconnuDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ScanInconnuDTO.class
        );

        // Validate the ScanInconnu in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedScanInconnu = scanInconnuMapper.toEntity(returnedScanInconnuDTO);
        assertScanInconnuUpdatableFieldsEquals(returnedScanInconnu, getPersistedScanInconnu(returnedScanInconnu));

        insertedScanInconnu = returnedScanInconnu;
    }

    @Test
    @Transactional
    void createScanInconnuWithExistingId() throws Exception {
        // Create the ScanInconnu with an existing ID
        scanInconnu.setId(1L);
        ScanInconnuDTO scanInconnuDTO = scanInconnuMapper.toDto(scanInconnu);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restScanInconnuMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scanInconnuDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ScanInconnu in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeScanneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scanInconnu.setCodeScanne(null);

        // Create the ScanInconnu, which fails.
        ScanInconnuDTO scanInconnuDTO = scanInconnuMapper.toDto(scanInconnu);

        restScanInconnuMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scanInconnuDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateScanIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scanInconnu.setDateScan(null);

        // Create the ScanInconnu, which fails.
        ScanInconnuDTO scanInconnuDTO = scanInconnuMapper.toDto(scanInconnu);

        restScanInconnuMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scanInconnuDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkResoluIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        scanInconnu.setResolu(null);

        // Create the ScanInconnu, which fails.
        ScanInconnuDTO scanInconnuDTO = scanInconnuMapper.toDto(scanInconnu);

        restScanInconnuMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scanInconnuDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllScanInconnus() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList
        restScanInconnuMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scanInconnu.getId().intValue())))
            .andExpect(jsonPath("$.[*].codeScanne").value(hasItem(DEFAULT_CODE_SCANNE)))
            .andExpect(jsonPath("$.[*].ecranOrigine").value(hasItem(DEFAULT_ECRAN_ORIGINE)))
            .andExpect(jsonPath("$.[*].dateScan").value(hasItem(DEFAULT_DATE_SCAN.toString())))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)))
            .andExpect(jsonPath("$.[*].resolu").value(hasItem(DEFAULT_RESOLU)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllScanInconnusWithEagerRelationshipsIsEnabled() throws Exception {
        when(scanInconnuServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restScanInconnuMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(scanInconnuServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllScanInconnusWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(scanInconnuServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restScanInconnuMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(scanInconnuRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getScanInconnu() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get the scanInconnu
        restScanInconnuMockMvc
            .perform(get(ENTITY_API_URL_ID, scanInconnu.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(scanInconnu.getId().intValue()))
            .andExpect(jsonPath("$.codeScanne").value(DEFAULT_CODE_SCANNE))
            .andExpect(jsonPath("$.ecranOrigine").value(DEFAULT_ECRAN_ORIGINE))
            .andExpect(jsonPath("$.dateScan").value(DEFAULT_DATE_SCAN.toString()))
            .andExpect(jsonPath("$.commentaire").value(DEFAULT_COMMENTAIRE))
            .andExpect(jsonPath("$.resolu").value(DEFAULT_RESOLU));
    }

    @Test
    @Transactional
    void getScanInconnusByIdFiltering() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        Long id = scanInconnu.getId();

        defaultScanInconnuFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultScanInconnuFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultScanInconnuFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllScanInconnusByCodeScanneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where codeScanne equals to
        defaultScanInconnuFiltering("codeScanne.equals=" + DEFAULT_CODE_SCANNE, "codeScanne.equals=" + UPDATED_CODE_SCANNE);
    }

    @Test
    @Transactional
    void getAllScanInconnusByCodeScanneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where codeScanne in
        defaultScanInconnuFiltering(
            "codeScanne.in=" + DEFAULT_CODE_SCANNE + "," + UPDATED_CODE_SCANNE,
            "codeScanne.in=" + UPDATED_CODE_SCANNE
        );
    }

    @Test
    @Transactional
    void getAllScanInconnusByCodeScanneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where codeScanne is not null
        defaultScanInconnuFiltering("codeScanne.specified=true", "codeScanne.specified=false");
    }

    @Test
    @Transactional
    void getAllScanInconnusByCodeScanneContainsSomething() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where codeScanne contains
        defaultScanInconnuFiltering("codeScanne.contains=" + DEFAULT_CODE_SCANNE, "codeScanne.contains=" + UPDATED_CODE_SCANNE);
    }

    @Test
    @Transactional
    void getAllScanInconnusByCodeScanneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where codeScanne does not contain
        defaultScanInconnuFiltering("codeScanne.doesNotContain=" + UPDATED_CODE_SCANNE, "codeScanne.doesNotContain=" + DEFAULT_CODE_SCANNE);
    }

    @Test
    @Transactional
    void getAllScanInconnusByEcranOrigineIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where ecranOrigine equals to
        defaultScanInconnuFiltering("ecranOrigine.equals=" + DEFAULT_ECRAN_ORIGINE, "ecranOrigine.equals=" + UPDATED_ECRAN_ORIGINE);
    }

    @Test
    @Transactional
    void getAllScanInconnusByEcranOrigineIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where ecranOrigine in
        defaultScanInconnuFiltering(
            "ecranOrigine.in=" + DEFAULT_ECRAN_ORIGINE + "," + UPDATED_ECRAN_ORIGINE,
            "ecranOrigine.in=" + UPDATED_ECRAN_ORIGINE
        );
    }

    @Test
    @Transactional
    void getAllScanInconnusByEcranOrigineIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where ecranOrigine is not null
        defaultScanInconnuFiltering("ecranOrigine.specified=true", "ecranOrigine.specified=false");
    }

    @Test
    @Transactional
    void getAllScanInconnusByEcranOrigineContainsSomething() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where ecranOrigine contains
        defaultScanInconnuFiltering("ecranOrigine.contains=" + DEFAULT_ECRAN_ORIGINE, "ecranOrigine.contains=" + UPDATED_ECRAN_ORIGINE);
    }

    @Test
    @Transactional
    void getAllScanInconnusByEcranOrigineNotContainsSomething() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where ecranOrigine does not contain
        defaultScanInconnuFiltering(
            "ecranOrigine.doesNotContain=" + UPDATED_ECRAN_ORIGINE,
            "ecranOrigine.doesNotContain=" + DEFAULT_ECRAN_ORIGINE
        );
    }

    @Test
    @Transactional
    void getAllScanInconnusByDateScanIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where dateScan equals to
        defaultScanInconnuFiltering("dateScan.equals=" + DEFAULT_DATE_SCAN, "dateScan.equals=" + UPDATED_DATE_SCAN);
    }

    @Test
    @Transactional
    void getAllScanInconnusByDateScanIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where dateScan in
        defaultScanInconnuFiltering("dateScan.in=" + DEFAULT_DATE_SCAN + "," + UPDATED_DATE_SCAN, "dateScan.in=" + UPDATED_DATE_SCAN);
    }

    @Test
    @Transactional
    void getAllScanInconnusByDateScanIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where dateScan is not null
        defaultScanInconnuFiltering("dateScan.specified=true", "dateScan.specified=false");
    }

    @Test
    @Transactional
    void getAllScanInconnusByCommentaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where commentaire equals to
        defaultScanInconnuFiltering("commentaire.equals=" + DEFAULT_COMMENTAIRE, "commentaire.equals=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllScanInconnusByCommentaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where commentaire in
        defaultScanInconnuFiltering(
            "commentaire.in=" + DEFAULT_COMMENTAIRE + "," + UPDATED_COMMENTAIRE,
            "commentaire.in=" + UPDATED_COMMENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllScanInconnusByCommentaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where commentaire is not null
        defaultScanInconnuFiltering("commentaire.specified=true", "commentaire.specified=false");
    }

    @Test
    @Transactional
    void getAllScanInconnusByCommentaireContainsSomething() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where commentaire contains
        defaultScanInconnuFiltering("commentaire.contains=" + DEFAULT_COMMENTAIRE, "commentaire.contains=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllScanInconnusByCommentaireNotContainsSomething() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where commentaire does not contain
        defaultScanInconnuFiltering(
            "commentaire.doesNotContain=" + UPDATED_COMMENTAIRE,
            "commentaire.doesNotContain=" + DEFAULT_COMMENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllScanInconnusByResoluIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where resolu equals to
        defaultScanInconnuFiltering("resolu.equals=" + DEFAULT_RESOLU, "resolu.equals=" + UPDATED_RESOLU);
    }

    @Test
    @Transactional
    void getAllScanInconnusByResoluIsInShouldWork() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where resolu in
        defaultScanInconnuFiltering("resolu.in=" + DEFAULT_RESOLU + "," + UPDATED_RESOLU, "resolu.in=" + UPDATED_RESOLU);
    }

    @Test
    @Transactional
    void getAllScanInconnusByResoluIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        // Get all the scanInconnuList where resolu is not null
        defaultScanInconnuFiltering("resolu.specified=true", "resolu.specified=false");
    }

    @Test
    @Transactional
    void getAllScanInconnusByBoutiqueIsEqualToSomething() throws Exception {
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            scanInconnuRepository.saveAndFlush(scanInconnu);
            boutique = BoutiqueResourceIT.createEntity();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutique);
        em.flush();
        scanInconnu.setBoutique(boutique);
        scanInconnuRepository.saveAndFlush(scanInconnu);
        Long boutiqueId = boutique.getId();
        // Get all the scanInconnuList where boutique equals to boutiqueId
        defaultScanInconnuShouldBeFound("boutiqueId.equals=" + boutiqueId);

        // Get all the scanInconnuList where boutique equals to (boutiqueId + 1)
        defaultScanInconnuShouldNotBeFound("boutiqueId.equals=" + (boutiqueId + 1));
    }

    @Test
    @Transactional
    void getAllScanInconnusByProduitAffecteIsEqualToSomething() throws Exception {
        Produit produitAffecte;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            scanInconnuRepository.saveAndFlush(scanInconnu);
            produitAffecte = ProduitResourceIT.createEntity(em);
        } else {
            produitAffecte = TestUtil.findAll(em, Produit.class).get(0);
        }
        em.persist(produitAffecte);
        em.flush();
        scanInconnu.setProduitAffecte(produitAffecte);
        scanInconnuRepository.saveAndFlush(scanInconnu);
        Long produitAffecteId = produitAffecte.getId();
        // Get all the scanInconnuList where produitAffecte equals to produitAffecteId
        defaultScanInconnuShouldBeFound("produitAffecteId.equals=" + produitAffecteId);

        // Get all the scanInconnuList where produitAffecte equals to (produitAffecteId + 1)
        defaultScanInconnuShouldNotBeFound("produitAffecteId.equals=" + (produitAffecteId + 1));
    }

    private void defaultScanInconnuFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultScanInconnuShouldBeFound(shouldBeFound);
        defaultScanInconnuShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultScanInconnuShouldBeFound(String filter) throws Exception {
        restScanInconnuMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(scanInconnu.getId().intValue())))
            .andExpect(jsonPath("$.[*].codeScanne").value(hasItem(DEFAULT_CODE_SCANNE)))
            .andExpect(jsonPath("$.[*].ecranOrigine").value(hasItem(DEFAULT_ECRAN_ORIGINE)))
            .andExpect(jsonPath("$.[*].dateScan").value(hasItem(DEFAULT_DATE_SCAN.toString())))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)))
            .andExpect(jsonPath("$.[*].resolu").value(hasItem(DEFAULT_RESOLU)));

        // Check, that the count call also returns 1
        restScanInconnuMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultScanInconnuShouldNotBeFound(String filter) throws Exception {
        restScanInconnuMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restScanInconnuMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingScanInconnu() throws Exception {
        // Get the scanInconnu
        restScanInconnuMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingScanInconnu() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scanInconnu
        ScanInconnu updatedScanInconnu = scanInconnuRepository.findById(scanInconnu.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedScanInconnu are not directly saved in db
        em.detach(updatedScanInconnu);
        updatedScanInconnu
            .codeScanne(UPDATED_CODE_SCANNE)
            .ecranOrigine(UPDATED_ECRAN_ORIGINE)
            .dateScan(UPDATED_DATE_SCAN)
            .commentaire(UPDATED_COMMENTAIRE)
            .resolu(UPDATED_RESOLU);
        ScanInconnuDTO scanInconnuDTO = scanInconnuMapper.toDto(updatedScanInconnu);

        restScanInconnuMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scanInconnuDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scanInconnuDTO))
            )
            .andExpect(status().isOk());

        // Validate the ScanInconnu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedScanInconnuToMatchAllProperties(updatedScanInconnu);
    }

    @Test
    @Transactional
    void putNonExistingScanInconnu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scanInconnu.setId(longCount.incrementAndGet());

        // Create the ScanInconnu
        ScanInconnuDTO scanInconnuDTO = scanInconnuMapper.toDto(scanInconnu);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScanInconnuMockMvc
            .perform(
                put(ENTITY_API_URL_ID, scanInconnuDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scanInconnuDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScanInconnu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchScanInconnu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scanInconnu.setId(longCount.incrementAndGet());

        // Create the ScanInconnu
        ScanInconnuDTO scanInconnuDTO = scanInconnuMapper.toDto(scanInconnu);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScanInconnuMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(scanInconnuDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScanInconnu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamScanInconnu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scanInconnu.setId(longCount.incrementAndGet());

        // Create the ScanInconnu
        ScanInconnuDTO scanInconnuDTO = scanInconnuMapper.toDto(scanInconnu);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScanInconnuMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(scanInconnuDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScanInconnu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateScanInconnuWithPatch() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scanInconnu using partial update
        ScanInconnu partialUpdatedScanInconnu = new ScanInconnu();
        partialUpdatedScanInconnu.setId(scanInconnu.getId());

        partialUpdatedScanInconnu
            .codeScanne(UPDATED_CODE_SCANNE)
            .ecranOrigine(UPDATED_ECRAN_ORIGINE)
            .dateScan(UPDATED_DATE_SCAN)
            .commentaire(UPDATED_COMMENTAIRE)
            .resolu(UPDATED_RESOLU);

        restScanInconnuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScanInconnu.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScanInconnu))
            )
            .andExpect(status().isOk());

        // Validate the ScanInconnu in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScanInconnuUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedScanInconnu, scanInconnu),
            getPersistedScanInconnu(scanInconnu)
        );
    }

    @Test
    @Transactional
    void fullUpdateScanInconnuWithPatch() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the scanInconnu using partial update
        ScanInconnu partialUpdatedScanInconnu = new ScanInconnu();
        partialUpdatedScanInconnu.setId(scanInconnu.getId());

        partialUpdatedScanInconnu
            .codeScanne(UPDATED_CODE_SCANNE)
            .ecranOrigine(UPDATED_ECRAN_ORIGINE)
            .dateScan(UPDATED_DATE_SCAN)
            .commentaire(UPDATED_COMMENTAIRE)
            .resolu(UPDATED_RESOLU);

        restScanInconnuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedScanInconnu.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedScanInconnu))
            )
            .andExpect(status().isOk());

        // Validate the ScanInconnu in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertScanInconnuUpdatableFieldsEquals(partialUpdatedScanInconnu, getPersistedScanInconnu(partialUpdatedScanInconnu));
    }

    @Test
    @Transactional
    void patchNonExistingScanInconnu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scanInconnu.setId(longCount.incrementAndGet());

        // Create the ScanInconnu
        ScanInconnuDTO scanInconnuDTO = scanInconnuMapper.toDto(scanInconnu);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restScanInconnuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, scanInconnuDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scanInconnuDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScanInconnu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchScanInconnu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scanInconnu.setId(longCount.incrementAndGet());

        // Create the ScanInconnu
        ScanInconnuDTO scanInconnuDTO = scanInconnuMapper.toDto(scanInconnu);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScanInconnuMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(scanInconnuDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ScanInconnu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamScanInconnu() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        scanInconnu.setId(longCount.incrementAndGet());

        // Create the ScanInconnu
        ScanInconnuDTO scanInconnuDTO = scanInconnuMapper.toDto(scanInconnu);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restScanInconnuMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(scanInconnuDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ScanInconnu in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteScanInconnu() throws Exception {
        // Initialize the database
        insertedScanInconnu = scanInconnuRepository.saveAndFlush(scanInconnu);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the scanInconnu
        restScanInconnuMockMvc
            .perform(delete(ENTITY_API_URL_ID, scanInconnu.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return scanInconnuRepository.count();
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

    protected ScanInconnu getPersistedScanInconnu(ScanInconnu scanInconnu) {
        return scanInconnuRepository.findById(scanInconnu.getId()).orElseThrow();
    }

    protected void assertPersistedScanInconnuToMatchAllProperties(ScanInconnu expectedScanInconnu) {
        assertScanInconnuAllPropertiesEquals(expectedScanInconnu, getPersistedScanInconnu(expectedScanInconnu));
    }

    protected void assertPersistedScanInconnuToMatchUpdatableProperties(ScanInconnu expectedScanInconnu) {
        assertScanInconnuAllUpdatablePropertiesEquals(expectedScanInconnu, getPersistedScanInconnu(expectedScanInconnu));
    }
}
