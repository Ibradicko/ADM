package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.RegleRedevanceAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.GroupeArticle;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.RegleRedevance;
import com.adm.supervision.domain.enumeration.TypeRegleRedevance;
import com.adm.supervision.repository.RegleRedevanceRepository;
import com.adm.supervision.service.RegleRedevanceService;
import com.adm.supervision.service.dto.RegleRedevanceDTO;
import com.adm.supervision.service.mapper.RegleRedevanceMapper;
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
 * Integration tests for the {@link RegleRedevanceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RegleRedevanceResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final TypeRegleRedevance DEFAULT_TYPE_REGLE = TypeRegleRedevance.BOUTIQUE;
    private static final TypeRegleRedevance UPDATED_TYPE_REGLE = TypeRegleRedevance.LOCATAIRE;

    private static final BigDecimal DEFAULT_TAUX = new BigDecimal(0);
    private static final BigDecimal UPDATED_TAUX = new BigDecimal(1);
    private static final BigDecimal SMALLER_TAUX = new BigDecimal(0 - 1);

    private static final LocalDate DEFAULT_DATE_DEBUT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_DEBUT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_DEBUT = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DATE_FIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FIN = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_FIN = LocalDate.ofEpochDay(-1L);

    private static final Integer DEFAULT_PRIORITE = 1;
    private static final Integer UPDATED_PRIORITE = 2;
    private static final Integer SMALLER_PRIORITE = 1 - 1;

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final String ENTITY_API_URL = "/api/regle-redevances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RegleRedevanceRepository regleRedevanceRepository;

    @Mock
    private RegleRedevanceRepository regleRedevanceRepositoryMock;

    @Autowired
    private RegleRedevanceMapper regleRedevanceMapper;

    @Mock
    private RegleRedevanceService regleRedevanceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRegleRedevanceMockMvc;

    private RegleRedevance regleRedevance;

    private RegleRedevance insertedRegleRedevance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RegleRedevance createEntity() {
        return new RegleRedevance()
            .code(DEFAULT_CODE)
            .typeRegle(DEFAULT_TYPE_REGLE)
            .taux(DEFAULT_TAUX)
            .dateDebut(DEFAULT_DATE_DEBUT)
            .dateFin(DEFAULT_DATE_FIN)
            .priorite(DEFAULT_PRIORITE)
            .actif(DEFAULT_ACTIF);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RegleRedevance createUpdatedEntity() {
        return new RegleRedevance()
            .code(UPDATED_CODE)
            .typeRegle(UPDATED_TYPE_REGLE)
            .taux(UPDATED_TAUX)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .priorite(UPDATED_PRIORITE)
            .actif(UPDATED_ACTIF);
    }

    @BeforeEach
    void initTest() {
        regleRedevance = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRegleRedevance != null) {
            regleRedevanceRepository.delete(insertedRegleRedevance);
            insertedRegleRedevance = null;
        }
    }

    @Test
    @Transactional
    void createRegleRedevance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RegleRedevance
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(regleRedevance);
        var returnedRegleRedevanceDTO = om.readValue(
            restRegleRedevanceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regleRedevanceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RegleRedevanceDTO.class
        );

        // Validate the RegleRedevance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRegleRedevance = regleRedevanceMapper.toEntity(returnedRegleRedevanceDTO);
        assertRegleRedevanceUpdatableFieldsEquals(returnedRegleRedevance, getPersistedRegleRedevance(returnedRegleRedevance));

        insertedRegleRedevance = returnedRegleRedevance;
    }

    @Test
    @Transactional
    void createRegleRedevanceWithExistingId() throws Exception {
        // Create the RegleRedevance with an existing ID
        regleRedevance.setId(1L);
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(regleRedevance);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRegleRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regleRedevanceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RegleRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        regleRedevance.setCode(null);

        // Create the RegleRedevance, which fails.
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(regleRedevance);

        restRegleRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regleRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeRegleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        regleRedevance.setTypeRegle(null);

        // Create the RegleRedevance, which fails.
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(regleRedevance);

        restRegleRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regleRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTauxIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        regleRedevance.setTaux(null);

        // Create the RegleRedevance, which fails.
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(regleRedevance);

        restRegleRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regleRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateDebutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        regleRedevance.setDateDebut(null);

        // Create the RegleRedevance, which fails.
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(regleRedevance);

        restRegleRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regleRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        regleRedevance.setActif(null);

        // Create the RegleRedevance, which fails.
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(regleRedevance);

        restRegleRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regleRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRegleRedevances() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList
        restRegleRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(regleRedevance.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].typeRegle").value(hasItem(DEFAULT_TYPE_REGLE.toString())))
            .andExpect(jsonPath("$.[*].taux").value(hasItem(sameNumber(DEFAULT_TAUX))))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())))
            .andExpect(jsonPath("$.[*].priorite").value(hasItem(DEFAULT_PRIORITE)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRegleRedevancesWithEagerRelationshipsIsEnabled() throws Exception {
        when(regleRedevanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRegleRedevanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(regleRedevanceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRegleRedevancesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(regleRedevanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRegleRedevanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(regleRedevanceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRegleRedevance() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get the regleRedevance
        restRegleRedevanceMockMvc
            .perform(get(ENTITY_API_URL_ID, regleRedevance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(regleRedevance.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.typeRegle").value(DEFAULT_TYPE_REGLE.toString()))
            .andExpect(jsonPath("$.taux").value(sameNumber(DEFAULT_TAUX)))
            .andExpect(jsonPath("$.dateDebut").value(DEFAULT_DATE_DEBUT.toString()))
            .andExpect(jsonPath("$.dateFin").value(DEFAULT_DATE_FIN.toString()))
            .andExpect(jsonPath("$.priorite").value(DEFAULT_PRIORITE))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF));
    }

    @Test
    @Transactional
    void getRegleRedevancesByIdFiltering() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        Long id = regleRedevance.getId();

        defaultRegleRedevanceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRegleRedevanceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRegleRedevanceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where code equals to
        defaultRegleRedevanceFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where code in
        defaultRegleRedevanceFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where code is not null
        defaultRegleRedevanceFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where code contains
        defaultRegleRedevanceFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where code does not contain
        defaultRegleRedevanceFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByTypeRegleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where typeRegle equals to
        defaultRegleRedevanceFiltering("typeRegle.equals=" + DEFAULT_TYPE_REGLE, "typeRegle.equals=" + UPDATED_TYPE_REGLE);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByTypeRegleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where typeRegle in
        defaultRegleRedevanceFiltering(
            "typeRegle.in=" + DEFAULT_TYPE_REGLE + "," + UPDATED_TYPE_REGLE,
            "typeRegle.in=" + UPDATED_TYPE_REGLE
        );
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByTypeRegleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where typeRegle is not null
        defaultRegleRedevanceFiltering("typeRegle.specified=true", "typeRegle.specified=false");
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByTauxIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where taux equals to
        defaultRegleRedevanceFiltering("taux.equals=" + DEFAULT_TAUX, "taux.equals=" + UPDATED_TAUX);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByTauxIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where taux in
        defaultRegleRedevanceFiltering("taux.in=" + DEFAULT_TAUX + "," + UPDATED_TAUX, "taux.in=" + UPDATED_TAUX);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByTauxIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where taux is not null
        defaultRegleRedevanceFiltering("taux.specified=true", "taux.specified=false");
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByTauxIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where taux is greater than or equal to
        defaultRegleRedevanceFiltering(
            "taux.greaterThanOrEqual=" + DEFAULT_TAUX,
            "taux.greaterThanOrEqual=" + (DEFAULT_TAUX.add(BigDecimal.ONE))
        );
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByTauxIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where taux is less than or equal to
        defaultRegleRedevanceFiltering("taux.lessThanOrEqual=" + DEFAULT_TAUX, "taux.lessThanOrEqual=" + SMALLER_TAUX);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByTauxIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where taux is less than
        defaultRegleRedevanceFiltering("taux.lessThan=" + (DEFAULT_TAUX.add(BigDecimal.ONE)), "taux.lessThan=" + DEFAULT_TAUX);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByTauxIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where taux is greater than
        defaultRegleRedevanceFiltering("taux.greaterThan=" + SMALLER_TAUX, "taux.greaterThan=" + DEFAULT_TAUX);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateDebutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateDebut equals to
        defaultRegleRedevanceFiltering("dateDebut.equals=" + DEFAULT_DATE_DEBUT, "dateDebut.equals=" + UPDATED_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateDebutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateDebut in
        defaultRegleRedevanceFiltering(
            "dateDebut.in=" + DEFAULT_DATE_DEBUT + "," + UPDATED_DATE_DEBUT,
            "dateDebut.in=" + UPDATED_DATE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateDebutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateDebut is not null
        defaultRegleRedevanceFiltering("dateDebut.specified=true", "dateDebut.specified=false");
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateDebutIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateDebut is greater than or equal to
        defaultRegleRedevanceFiltering(
            "dateDebut.greaterThanOrEqual=" + DEFAULT_DATE_DEBUT,
            "dateDebut.greaterThanOrEqual=" + UPDATED_DATE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateDebutIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateDebut is less than or equal to
        defaultRegleRedevanceFiltering(
            "dateDebut.lessThanOrEqual=" + DEFAULT_DATE_DEBUT,
            "dateDebut.lessThanOrEqual=" + SMALLER_DATE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateDebutIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateDebut is less than
        defaultRegleRedevanceFiltering("dateDebut.lessThan=" + UPDATED_DATE_DEBUT, "dateDebut.lessThan=" + DEFAULT_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateDebutIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateDebut is greater than
        defaultRegleRedevanceFiltering("dateDebut.greaterThan=" + SMALLER_DATE_DEBUT, "dateDebut.greaterThan=" + DEFAULT_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateFinIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateFin equals to
        defaultRegleRedevanceFiltering("dateFin.equals=" + DEFAULT_DATE_FIN, "dateFin.equals=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateFinIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateFin in
        defaultRegleRedevanceFiltering("dateFin.in=" + DEFAULT_DATE_FIN + "," + UPDATED_DATE_FIN, "dateFin.in=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateFinIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateFin is not null
        defaultRegleRedevanceFiltering("dateFin.specified=true", "dateFin.specified=false");
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateFinIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateFin is greater than or equal to
        defaultRegleRedevanceFiltering("dateFin.greaterThanOrEqual=" + DEFAULT_DATE_FIN, "dateFin.greaterThanOrEqual=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateFinIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateFin is less than or equal to
        defaultRegleRedevanceFiltering("dateFin.lessThanOrEqual=" + DEFAULT_DATE_FIN, "dateFin.lessThanOrEqual=" + SMALLER_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateFinIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateFin is less than
        defaultRegleRedevanceFiltering("dateFin.lessThan=" + UPDATED_DATE_FIN, "dateFin.lessThan=" + DEFAULT_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByDateFinIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where dateFin is greater than
        defaultRegleRedevanceFiltering("dateFin.greaterThan=" + SMALLER_DATE_FIN, "dateFin.greaterThan=" + DEFAULT_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByPrioriteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where priorite equals to
        defaultRegleRedevanceFiltering("priorite.equals=" + DEFAULT_PRIORITE, "priorite.equals=" + UPDATED_PRIORITE);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByPrioriteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where priorite in
        defaultRegleRedevanceFiltering("priorite.in=" + DEFAULT_PRIORITE + "," + UPDATED_PRIORITE, "priorite.in=" + UPDATED_PRIORITE);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByPrioriteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where priorite is not null
        defaultRegleRedevanceFiltering("priorite.specified=true", "priorite.specified=false");
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByPrioriteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where priorite is greater than or equal to
        defaultRegleRedevanceFiltering(
            "priorite.greaterThanOrEqual=" + DEFAULT_PRIORITE,
            "priorite.greaterThanOrEqual=" + UPDATED_PRIORITE
        );
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByPrioriteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where priorite is less than or equal to
        defaultRegleRedevanceFiltering("priorite.lessThanOrEqual=" + DEFAULT_PRIORITE, "priorite.lessThanOrEqual=" + SMALLER_PRIORITE);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByPrioriteIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where priorite is less than
        defaultRegleRedevanceFiltering("priorite.lessThan=" + UPDATED_PRIORITE, "priorite.lessThan=" + DEFAULT_PRIORITE);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByPrioriteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where priorite is greater than
        defaultRegleRedevanceFiltering("priorite.greaterThan=" + SMALLER_PRIORITE, "priorite.greaterThan=" + DEFAULT_PRIORITE);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByActifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where actif equals to
        defaultRegleRedevanceFiltering("actif.equals=" + DEFAULT_ACTIF, "actif.equals=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByActifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where actif in
        defaultRegleRedevanceFiltering("actif.in=" + DEFAULT_ACTIF + "," + UPDATED_ACTIF, "actif.in=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByActifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        // Get all the regleRedevanceList where actif is not null
        defaultRegleRedevanceFiltering("actif.specified=true", "actif.specified=false");
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByBoutiqueIsEqualToSomething() throws Exception {
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            regleRedevanceRepository.saveAndFlush(regleRedevance);
            boutique = BoutiqueResourceIT.createEntity();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutique);
        em.flush();
        regleRedevance.setBoutique(boutique);
        regleRedevanceRepository.saveAndFlush(regleRedevance);
        Long boutiqueId = boutique.getId();
        // Get all the regleRedevanceList where boutique equals to boutiqueId
        defaultRegleRedevanceShouldBeFound("boutiqueId.equals=" + boutiqueId);

        // Get all the regleRedevanceList where boutique equals to (boutiqueId + 1)
        defaultRegleRedevanceShouldNotBeFound("boutiqueId.equals=" + (boutiqueId + 1));
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByLocataireIsEqualToSomething() throws Exception {
        Locataire locataire;
        if (TestUtil.findAll(em, Locataire.class).isEmpty()) {
            regleRedevanceRepository.saveAndFlush(regleRedevance);
            locataire = LocataireResourceIT.createEntity();
        } else {
            locataire = TestUtil.findAll(em, Locataire.class).get(0);
        }
        em.persist(locataire);
        em.flush();
        regleRedevance.setLocataire(locataire);
        regleRedevanceRepository.saveAndFlush(regleRedevance);
        Long locataireId = locataire.getId();
        // Get all the regleRedevanceList where locataire equals to locataireId
        defaultRegleRedevanceShouldBeFound("locataireId.equals=" + locataireId);

        // Get all the regleRedevanceList where locataire equals to (locataireId + 1)
        defaultRegleRedevanceShouldNotBeFound("locataireId.equals=" + (locataireId + 1));
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByGroupeArticleIsEqualToSomething() throws Exception {
        GroupeArticle groupeArticle;
        if (TestUtil.findAll(em, GroupeArticle.class).isEmpty()) {
            regleRedevanceRepository.saveAndFlush(regleRedevance);
            groupeArticle = GroupeArticleResourceIT.createEntity(em);
        } else {
            groupeArticle = TestUtil.findAll(em, GroupeArticle.class).get(0);
        }
        em.persist(groupeArticle);
        em.flush();
        regleRedevance.setGroupeArticle(groupeArticle);
        regleRedevanceRepository.saveAndFlush(regleRedevance);
        Long groupeArticleId = groupeArticle.getId();
        // Get all the regleRedevanceList where groupeArticle equals to groupeArticleId
        defaultRegleRedevanceShouldBeFound("groupeArticleId.equals=" + groupeArticleId);

        // Get all the regleRedevanceList where groupeArticle equals to (groupeArticleId + 1)
        defaultRegleRedevanceShouldNotBeFound("groupeArticleId.equals=" + (groupeArticleId + 1));
    }

    @Test
    @Transactional
    void getAllRegleRedevancesByProduitIsEqualToSomething() throws Exception {
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            regleRedevanceRepository.saveAndFlush(regleRedevance);
            produit = ProduitResourceIT.createEntity(em);
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        em.persist(produit);
        em.flush();
        regleRedevance.setProduit(produit);
        regleRedevanceRepository.saveAndFlush(regleRedevance);
        Long produitId = produit.getId();
        // Get all the regleRedevanceList where produit equals to produitId
        defaultRegleRedevanceShouldBeFound("produitId.equals=" + produitId);

        // Get all the regleRedevanceList where produit equals to (produitId + 1)
        defaultRegleRedevanceShouldNotBeFound("produitId.equals=" + (produitId + 1));
    }

    private void defaultRegleRedevanceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRegleRedevanceShouldBeFound(shouldBeFound);
        defaultRegleRedevanceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRegleRedevanceShouldBeFound(String filter) throws Exception {
        restRegleRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(regleRedevance.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].typeRegle").value(hasItem(DEFAULT_TYPE_REGLE.toString())))
            .andExpect(jsonPath("$.[*].taux").value(hasItem(sameNumber(DEFAULT_TAUX))))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())))
            .andExpect(jsonPath("$.[*].priorite").value(hasItem(DEFAULT_PRIORITE)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));

        // Check, that the count call also returns 1
        restRegleRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRegleRedevanceShouldNotBeFound(String filter) throws Exception {
        restRegleRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRegleRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRegleRedevance() throws Exception {
        // Get the regleRedevance
        restRegleRedevanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRegleRedevance() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the regleRedevance
        RegleRedevance updatedRegleRedevance = regleRedevanceRepository.findById(regleRedevance.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRegleRedevance are not directly saved in db
        em.detach(updatedRegleRedevance);
        updatedRegleRedevance
            .code(UPDATED_CODE)
            .typeRegle(UPDATED_TYPE_REGLE)
            .taux(UPDATED_TAUX)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .priorite(UPDATED_PRIORITE)
            .actif(UPDATED_ACTIF);
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(updatedRegleRedevance);

        restRegleRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, regleRedevanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(regleRedevanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the RegleRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRegleRedevanceToMatchAllProperties(updatedRegleRedevance);
    }

    @Test
    @Transactional
    void putNonExistingRegleRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        regleRedevance.setId(longCount.incrementAndGet());

        // Create the RegleRedevance
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(regleRedevance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRegleRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, regleRedevanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(regleRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RegleRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRegleRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        regleRedevance.setId(longCount.incrementAndGet());

        // Create the RegleRedevance
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(regleRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegleRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(regleRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RegleRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRegleRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        regleRedevance.setId(longCount.incrementAndGet());

        // Create the RegleRedevance
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(regleRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegleRedevanceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regleRedevanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RegleRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRegleRedevanceWithPatch() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the regleRedevance using partial update
        RegleRedevance partialUpdatedRegleRedevance = new RegleRedevance();
        partialUpdatedRegleRedevance.setId(regleRedevance.getId());

        partialUpdatedRegleRedevance.code(UPDATED_CODE).taux(UPDATED_TAUX);

        restRegleRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRegleRedevance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRegleRedevance))
            )
            .andExpect(status().isOk());

        // Validate the RegleRedevance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRegleRedevanceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRegleRedevance, regleRedevance),
            getPersistedRegleRedevance(regleRedevance)
        );
    }

    @Test
    @Transactional
    void fullUpdateRegleRedevanceWithPatch() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the regleRedevance using partial update
        RegleRedevance partialUpdatedRegleRedevance = new RegleRedevance();
        partialUpdatedRegleRedevance.setId(regleRedevance.getId());

        partialUpdatedRegleRedevance
            .code(UPDATED_CODE)
            .typeRegle(UPDATED_TYPE_REGLE)
            .taux(UPDATED_TAUX)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN)
            .priorite(UPDATED_PRIORITE)
            .actif(UPDATED_ACTIF);

        restRegleRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRegleRedevance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRegleRedevance))
            )
            .andExpect(status().isOk());

        // Validate the RegleRedevance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRegleRedevanceUpdatableFieldsEquals(partialUpdatedRegleRedevance, getPersistedRegleRedevance(partialUpdatedRegleRedevance));
    }

    @Test
    @Transactional
    void patchNonExistingRegleRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        regleRedevance.setId(longCount.incrementAndGet());

        // Create the RegleRedevance
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(regleRedevance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRegleRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, regleRedevanceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(regleRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RegleRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRegleRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        regleRedevance.setId(longCount.incrementAndGet());

        // Create the RegleRedevance
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(regleRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegleRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(regleRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RegleRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRegleRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        regleRedevance.setId(longCount.incrementAndGet());

        // Create the RegleRedevance
        RegleRedevanceDTO regleRedevanceDTO = regleRedevanceMapper.toDto(regleRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegleRedevanceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(regleRedevanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RegleRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRegleRedevance() throws Exception {
        // Initialize the database
        insertedRegleRedevance = regleRedevanceRepository.saveAndFlush(regleRedevance);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the regleRedevance
        restRegleRedevanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, regleRedevance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return regleRedevanceRepository.count();
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

    protected RegleRedevance getPersistedRegleRedevance(RegleRedevance regleRedevance) {
        return regleRedevanceRepository.findById(regleRedevance.getId()).orElseThrow();
    }

    protected void assertPersistedRegleRedevanceToMatchAllProperties(RegleRedevance expectedRegleRedevance) {
        assertRegleRedevanceAllPropertiesEquals(expectedRegleRedevance, getPersistedRegleRedevance(expectedRegleRedevance));
    }

    protected void assertPersistedRegleRedevanceToMatchUpdatableProperties(RegleRedevance expectedRegleRedevance) {
        assertRegleRedevanceAllUpdatablePropertiesEquals(expectedRegleRedevance, getPersistedRegleRedevance(expectedRegleRedevance));
    }
}
