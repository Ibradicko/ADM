package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.TarifProduitAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.TarifProduit;
import com.adm.supervision.domain.enumeration.TypePrix;
import com.adm.supervision.repository.TarifProduitRepository;
import com.adm.supervision.service.TarifProduitService;
import com.adm.supervision.service.dto.TarifProduitDTO;
import com.adm.supervision.service.mapper.TarifProduitMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link TarifProduitResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TarifProduitResourceIT {

    private static final BigDecimal DEFAULT_MONTANT = new BigDecimal(0);
    private static final BigDecimal UPDATED_MONTANT = new BigDecimal(1);
    private static final BigDecimal SMALLER_MONTANT = new BigDecimal(0 - 1);

    private static final TypePrix DEFAULT_TYPE_PRIX = TypePrix.STANDARD;
    private static final TypePrix UPDATED_TYPE_PRIX = TypePrix.PROMOTION;

    private static final LocalDate DEFAULT_DATE_DEBUT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_DEBUT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_DEBUT = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DATE_FIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FIN = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_FIN = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final String ENTITY_API_URL = "/api/tarif-produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TarifProduitRepository tarifProduitRepository;

    @Mock
    private TarifProduitRepository tarifProduitRepositoryMock;

    @Autowired
    private TarifProduitMapper tarifProduitMapper;

    @Mock
    private TarifProduitService tarifProduitServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTarifProduitMockMvc;

    private TarifProduit tarifProduit;

    private TarifProduit insertedTarifProduit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TarifProduit createEntity(EntityManager em) {
        TarifProduit tarifProduit = new TarifProduit()
            .montant(DEFAULT_MONTANT)
            .typePrix(DEFAULT_TYPE_PRIX)
            .dateDebut(DEFAULT_DATE_DEBUT)
            .dateFin(DEFAULT_DATE_FIN)
            .actif(DEFAULT_ACTIF);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        tarifProduit.setProduit(produit);
        return tarifProduit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TarifProduit createUpdatedEntity(EntityManager em) {
        TarifProduit updatedTarifProduit = new TarifProduit()
            .montant(UPDATED_MONTANT)
            .typePrix(UPDATED_TYPE_PRIX)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .actif(UPDATED_ACTIF);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createUpdatedEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        updatedTarifProduit.setProduit(produit);
        return updatedTarifProduit;
    }

    @BeforeEach
    void initTest() {
        tarifProduit = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTarifProduit != null) {
            tarifProduitRepository.delete(insertedTarifProduit);
            insertedTarifProduit = null;
        }
    }

    @Test
    @Transactional
    void createTarifProduit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TarifProduit
        TarifProduitDTO tarifProduitDTO = tarifProduitMapper.toDto(tarifProduit);
        var returnedTarifProduitDTO = om.readValue(
            restTarifProduitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tarifProduitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TarifProduitDTO.class
        );

        // Validate the TarifProduit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTarifProduit = tarifProduitMapper.toEntity(returnedTarifProduitDTO);
        assertTarifProduitUpdatableFieldsEquals(returnedTarifProduit, getPersistedTarifProduit(returnedTarifProduit));

        insertedTarifProduit = returnedTarifProduit;
    }

    @Test
    @Transactional
    void createTarifProduitWithExistingId() throws Exception {
        // Create the TarifProduit with an existing ID
        tarifProduit.setId(1L);
        TarifProduitDTO tarifProduitDTO = tarifProduitMapper.toDto(tarifProduit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTarifProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tarifProduitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TarifProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMontantIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tarifProduit.setMontant(null);

        // Create the TarifProduit, which fails.
        TarifProduitDTO tarifProduitDTO = tarifProduitMapper.toDto(tarifProduit);

        restTarifProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tarifProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypePrixIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tarifProduit.setTypePrix(null);

        // Create the TarifProduit, which fails.
        TarifProduitDTO tarifProduitDTO = tarifProduitMapper.toDto(tarifProduit);

        restTarifProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tarifProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateDebutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tarifProduit.setDateDebut(null);

        // Create the TarifProduit, which fails.
        TarifProduitDTO tarifProduitDTO = tarifProduitMapper.toDto(tarifProduit);

        restTarifProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tarifProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tarifProduit.setActif(null);

        // Create the TarifProduit, which fails.
        TarifProduitDTO tarifProduitDTO = tarifProduitMapper.toDto(tarifProduit);

        restTarifProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tarifProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTarifProduits() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList
        restTarifProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tarifProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(sameNumber(DEFAULT_MONTANT))))
            .andExpect(jsonPath("$.[*].typePrix").value(hasItem(DEFAULT_TYPE_PRIX.toString())))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTarifProduitsWithEagerRelationshipsIsEnabled() throws Exception {
        when(tarifProduitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTarifProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(tarifProduitServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTarifProduitsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(tarifProduitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTarifProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(tarifProduitRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTarifProduit() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get the tarifProduit
        restTarifProduitMockMvc
            .perform(get(ENTITY_API_URL_ID, tarifProduit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tarifProduit.getId().intValue()))
            .andExpect(jsonPath("$.montant").value(sameNumber(DEFAULT_MONTANT)))
            .andExpect(jsonPath("$.typePrix").value(DEFAULT_TYPE_PRIX.toString()))
            .andExpect(jsonPath("$.dateDebut").value(DEFAULT_DATE_DEBUT.toString()))
            .andExpect(jsonPath("$.dateFin").value(DEFAULT_DATE_FIN.toString()))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF));
    }

    @Test
    @Transactional
    void getTarifProduitsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        Long id = tarifProduit.getId();

        defaultTarifProduitFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTarifProduitFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTarifProduitFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByMontantIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where montant equals to
        defaultTarifProduitFiltering("montant.equals=" + DEFAULT_MONTANT, "montant.equals=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByMontantIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where montant in
        defaultTarifProduitFiltering("montant.in=" + DEFAULT_MONTANT + "," + UPDATED_MONTANT, "montant.in=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByMontantIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where montant is not null
        defaultTarifProduitFiltering("montant.specified=true", "montant.specified=false");
    }

    @Test
    @Transactional
    void getAllTarifProduitsByMontantIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where montant is greater than or equal to
        defaultTarifProduitFiltering("montant.greaterThanOrEqual=" + DEFAULT_MONTANT, "montant.greaterThanOrEqual=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByMontantIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where montant is less than or equal to
        defaultTarifProduitFiltering("montant.lessThanOrEqual=" + DEFAULT_MONTANT, "montant.lessThanOrEqual=" + SMALLER_MONTANT);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByMontantIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where montant is less than
        defaultTarifProduitFiltering("montant.lessThan=" + UPDATED_MONTANT, "montant.lessThan=" + DEFAULT_MONTANT);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByMontantIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where montant is greater than
        defaultTarifProduitFiltering("montant.greaterThan=" + SMALLER_MONTANT, "montant.greaterThan=" + DEFAULT_MONTANT);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByTypePrixIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where typePrix equals to
        defaultTarifProduitFiltering("typePrix.equals=" + DEFAULT_TYPE_PRIX, "typePrix.equals=" + UPDATED_TYPE_PRIX);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByTypePrixIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where typePrix in
        defaultTarifProduitFiltering("typePrix.in=" + DEFAULT_TYPE_PRIX + "," + UPDATED_TYPE_PRIX, "typePrix.in=" + UPDATED_TYPE_PRIX);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByTypePrixIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where typePrix is not null
        defaultTarifProduitFiltering("typePrix.specified=true", "typePrix.specified=false");
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateDebutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateDebut equals to
        defaultTarifProduitFiltering("dateDebut.equals=" + DEFAULT_DATE_DEBUT, "dateDebut.equals=" + UPDATED_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateDebutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateDebut in
        defaultTarifProduitFiltering("dateDebut.in=" + DEFAULT_DATE_DEBUT + "," + UPDATED_DATE_DEBUT, "dateDebut.in=" + UPDATED_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateDebutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateDebut is not null
        defaultTarifProduitFiltering("dateDebut.specified=true", "dateDebut.specified=false");
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateDebutIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateDebut is greater than or equal to
        defaultTarifProduitFiltering(
            "dateDebut.greaterThanOrEqual=" + DEFAULT_DATE_DEBUT,
            "dateDebut.greaterThanOrEqual=" + UPDATED_DATE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateDebutIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateDebut is less than or equal to
        defaultTarifProduitFiltering("dateDebut.lessThanOrEqual=" + DEFAULT_DATE_DEBUT, "dateDebut.lessThanOrEqual=" + SMALLER_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateDebutIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateDebut is less than
        defaultTarifProduitFiltering("dateDebut.lessThan=" + UPDATED_DATE_DEBUT, "dateDebut.lessThan=" + DEFAULT_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateDebutIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateDebut is greater than
        defaultTarifProduitFiltering("dateDebut.greaterThan=" + SMALLER_DATE_DEBUT, "dateDebut.greaterThan=" + DEFAULT_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateFinIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateFin equals to
        defaultTarifProduitFiltering("dateFin.equals=" + DEFAULT_DATE_FIN, "dateFin.equals=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateFinIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateFin in
        defaultTarifProduitFiltering("dateFin.in=" + DEFAULT_DATE_FIN + "," + UPDATED_DATE_FIN, "dateFin.in=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateFinIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateFin is not null
        defaultTarifProduitFiltering("dateFin.specified=true", "dateFin.specified=false");
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateFinIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateFin is greater than or equal to
        defaultTarifProduitFiltering("dateFin.greaterThanOrEqual=" + DEFAULT_DATE_FIN, "dateFin.greaterThanOrEqual=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateFinIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateFin is less than or equal to
        defaultTarifProduitFiltering("dateFin.lessThanOrEqual=" + DEFAULT_DATE_FIN, "dateFin.lessThanOrEqual=" + SMALLER_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateFinIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateFin is less than
        defaultTarifProduitFiltering("dateFin.lessThan=" + UPDATED_DATE_FIN, "dateFin.lessThan=" + DEFAULT_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByDateFinIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where dateFin is greater than
        defaultTarifProduitFiltering("dateFin.greaterThan=" + SMALLER_DATE_FIN, "dateFin.greaterThan=" + DEFAULT_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByActifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where actif equals to
        defaultTarifProduitFiltering("actif.equals=" + DEFAULT_ACTIF, "actif.equals=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByActifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where actif in
        defaultTarifProduitFiltering("actif.in=" + DEFAULT_ACTIF + "," + UPDATED_ACTIF, "actif.in=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllTarifProduitsByActifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        // Get all the tarifProduitList where actif is not null
        defaultTarifProduitFiltering("actif.specified=true", "actif.specified=false");
    }

    @Test
    @Transactional
    void getAllTarifProduitsByProduitIsEqualToSomething() throws Exception {
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            tarifProduitRepository.saveAndFlush(tarifProduit);
            produit = ProduitResourceIT.createEntity(em);
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        em.persist(produit);
        em.flush();
        tarifProduit.setProduit(produit);
        tarifProduitRepository.saveAndFlush(tarifProduit);
        Long produitId = produit.getId();
        // Get all the tarifProduitList where produit equals to produitId
        defaultTarifProduitShouldBeFound("produitId.equals=" + produitId);

        // Get all the tarifProduitList where produit equals to (produitId + 1)
        defaultTarifProduitShouldNotBeFound("produitId.equals=" + (produitId + 1));
    }

    private void defaultTarifProduitFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTarifProduitShouldBeFound(shouldBeFound);
        defaultTarifProduitShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTarifProduitShouldBeFound(String filter) throws Exception {
        restTarifProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tarifProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(sameNumber(DEFAULT_MONTANT))))
            .andExpect(jsonPath("$.[*].typePrix").value(hasItem(DEFAULT_TYPE_PRIX.toString())))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));

        // Check, that the count call also returns 1
        restTarifProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTarifProduitShouldNotBeFound(String filter) throws Exception {
        restTarifProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTarifProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTarifProduit() throws Exception {
        // Get the tarifProduit
        restTarifProduitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTarifProduit() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tarifProduit
        TarifProduit updatedTarifProduit = tarifProduitRepository.findById(tarifProduit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTarifProduit are not directly saved in db
        em.detach(updatedTarifProduit);
        updatedTarifProduit
            .montant(UPDATED_MONTANT)
            .typePrix(UPDATED_TYPE_PRIX)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .actif(UPDATED_ACTIF);
        TarifProduitDTO tarifProduitDTO = tarifProduitMapper.toDto(updatedTarifProduit);

        restTarifProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tarifProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tarifProduitDTO))
            )
            .andExpect(status().isOk());

        // Validate the TarifProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTarifProduitToMatchAllProperties(updatedTarifProduit);
    }

    @Test
    @Transactional
    void putNonExistingTarifProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tarifProduit.setId(longCount.incrementAndGet());

        // Create the TarifProduit
        TarifProduitDTO tarifProduitDTO = tarifProduitMapper.toDto(tarifProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTarifProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tarifProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tarifProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TarifProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTarifProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tarifProduit.setId(longCount.incrementAndGet());

        // Create the TarifProduit
        TarifProduitDTO tarifProduitDTO = tarifProduitMapper.toDto(tarifProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTarifProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tarifProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TarifProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTarifProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tarifProduit.setId(longCount.incrementAndGet());

        // Create the TarifProduit
        TarifProduitDTO tarifProduitDTO = tarifProduitMapper.toDto(tarifProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTarifProduitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tarifProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TarifProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTarifProduitWithPatch() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tarifProduit using partial update
        TarifProduit partialUpdatedTarifProduit = new TarifProduit();
        partialUpdatedTarifProduit.setId(tarifProduit.getId());

        partialUpdatedTarifProduit.montant(UPDATED_MONTANT).typePrix(UPDATED_TYPE_PRIX).dateDebut(UPDATED_DATE_DEBUT).actif(UPDATED_ACTIF);

        restTarifProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTarifProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTarifProduit))
            )
            .andExpect(status().isOk());

        // Validate the TarifProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTarifProduitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTarifProduit, tarifProduit),
            getPersistedTarifProduit(tarifProduit)
        );
    }

    @Test
    @Transactional
    void fullUpdateTarifProduitWithPatch() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tarifProduit using partial update
        TarifProduit partialUpdatedTarifProduit = new TarifProduit();
        partialUpdatedTarifProduit.setId(tarifProduit.getId());

        partialUpdatedTarifProduit
            .montant(UPDATED_MONTANT)
            .typePrix(UPDATED_TYPE_PRIX)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .actif(UPDATED_ACTIF);

        restTarifProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTarifProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTarifProduit))
            )
            .andExpect(status().isOk());

        // Validate the TarifProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTarifProduitUpdatableFieldsEquals(partialUpdatedTarifProduit, getPersistedTarifProduit(partialUpdatedTarifProduit));
    }

    @Test
    @Transactional
    void patchNonExistingTarifProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tarifProduit.setId(longCount.incrementAndGet());

        // Create the TarifProduit
        TarifProduitDTO tarifProduitDTO = tarifProduitMapper.toDto(tarifProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTarifProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tarifProduitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tarifProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TarifProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTarifProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tarifProduit.setId(longCount.incrementAndGet());

        // Create the TarifProduit
        TarifProduitDTO tarifProduitDTO = tarifProduitMapper.toDto(tarifProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTarifProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tarifProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TarifProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTarifProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tarifProduit.setId(longCount.incrementAndGet());

        // Create the TarifProduit
        TarifProduitDTO tarifProduitDTO = tarifProduitMapper.toDto(tarifProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTarifProduitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tarifProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TarifProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTarifProduit() throws Exception {
        // Initialize the database
        insertedTarifProduit = tarifProduitRepository.saveAndFlush(tarifProduit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tarifProduit
        restTarifProduitMockMvc
            .perform(delete(ENTITY_API_URL_ID, tarifProduit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tarifProduitRepository.count();
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

    protected TarifProduit getPersistedTarifProduit(TarifProduit tarifProduit) {
        return tarifProduitRepository.findById(tarifProduit.getId()).orElseThrow();
    }

    protected void assertPersistedTarifProduitToMatchAllProperties(TarifProduit expectedTarifProduit) {
        assertTarifProduitAllPropertiesEquals(expectedTarifProduit, getPersistedTarifProduit(expectedTarifProduit));
    }

    protected void assertPersistedTarifProduitToMatchUpdatableProperties(TarifProduit expectedTarifProduit) {
        assertTarifProduitAllUpdatablePropertiesEquals(expectedTarifProduit, getPersistedTarifProduit(expectedTarifProduit));
    }
}
