package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.RegularisationRedevanceAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.RegularisationRedevance;
import com.adm.supervision.repository.RegularisationRedevanceRepository;
import com.adm.supervision.service.RegularisationRedevanceService;
import com.adm.supervision.service.dto.RegularisationRedevanceDTO;
import com.adm.supervision.service.mapper.RegularisationRedevanceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link RegularisationRedevanceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RegularisationRedevanceResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_MONTANT = new BigDecimal(1);
    private static final BigDecimal UPDATED_MONTANT = new BigDecimal(2);
    private static final BigDecimal SMALLER_MONTANT = new BigDecimal(1 - 1);

    private static final String DEFAULT_MOTIF = "AAAAAAAAAA";
    private static final String UPDATED_MOTIF = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_REGULARISATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_REGULARISATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/regularisation-redevances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RegularisationRedevanceRepository regularisationRedevanceRepository;

    @Mock
    private RegularisationRedevanceRepository regularisationRedevanceRepositoryMock;

    @Autowired
    private RegularisationRedevanceMapper regularisationRedevanceMapper;

    @Mock
    private RegularisationRedevanceService regularisationRedevanceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRegularisationRedevanceMockMvc;

    private RegularisationRedevance regularisationRedevance;

    private RegularisationRedevance insertedRegularisationRedevance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RegularisationRedevance createEntity(EntityManager em) {
        RegularisationRedevance regularisationRedevance = new RegularisationRedevance()
            .reference(DEFAULT_REFERENCE)
            .montant(DEFAULT_MONTANT)
            .motif(DEFAULT_MOTIF)
            .dateRegularisation(DEFAULT_DATE_REGULARISATION);
        // Add required entity
        CalculRedevance calculRedevance;
        if (TestUtil.findAll(em, CalculRedevance.class).isEmpty()) {
            calculRedevance = CalculRedevanceResourceIT.createEntity(em);
            em.persist(calculRedevance);
            em.flush();
        } else {
            calculRedevance = TestUtil.findAll(em, CalculRedevance.class).get(0);
        }
        regularisationRedevance.setCalcul(calculRedevance);
        return regularisationRedevance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RegularisationRedevance createUpdatedEntity(EntityManager em) {
        RegularisationRedevance updatedRegularisationRedevance = new RegularisationRedevance()
            .reference(UPDATED_REFERENCE)
            .montant(UPDATED_MONTANT)
            .motif(UPDATED_MOTIF)
            .dateRegularisation(UPDATED_DATE_REGULARISATION);
        // Add required entity
        CalculRedevance calculRedevance;
        if (TestUtil.findAll(em, CalculRedevance.class).isEmpty()) {
            calculRedevance = CalculRedevanceResourceIT.createUpdatedEntity(em);
            em.persist(calculRedevance);
            em.flush();
        } else {
            calculRedevance = TestUtil.findAll(em, CalculRedevance.class).get(0);
        }
        updatedRegularisationRedevance.setCalcul(calculRedevance);
        return updatedRegularisationRedevance;
    }

    @BeforeEach
    void initTest() {
        regularisationRedevance = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedRegularisationRedevance != null) {
            regularisationRedevanceRepository.delete(insertedRegularisationRedevance);
            insertedRegularisationRedevance = null;
        }
    }

    @Test
    @Transactional
    void createRegularisationRedevance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RegularisationRedevance
        RegularisationRedevanceDTO regularisationRedevanceDTO = regularisationRedevanceMapper.toDto(regularisationRedevance);
        var returnedRegularisationRedevanceDTO = om.readValue(
            restRegularisationRedevanceMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regularisationRedevanceDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RegularisationRedevanceDTO.class
        );

        // Validate the RegularisationRedevance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRegularisationRedevance = regularisationRedevanceMapper.toEntity(returnedRegularisationRedevanceDTO);
        assertRegularisationRedevanceUpdatableFieldsEquals(
            returnedRegularisationRedevance,
            getPersistedRegularisationRedevance(returnedRegularisationRedevance)
        );

        insertedRegularisationRedevance = returnedRegularisationRedevance;
    }

    @Test
    @Transactional
    void createRegularisationRedevanceWithExistingId() throws Exception {
        // Create the RegularisationRedevance with an existing ID
        regularisationRedevance.setId(1L);
        RegularisationRedevanceDTO regularisationRedevanceDTO = regularisationRedevanceMapper.toDto(regularisationRedevance);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRegularisationRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regularisationRedevanceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RegularisationRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        regularisationRedevance.setReference(null);

        // Create the RegularisationRedevance, which fails.
        RegularisationRedevanceDTO regularisationRedevanceDTO = regularisationRedevanceMapper.toDto(regularisationRedevance);

        restRegularisationRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regularisationRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMontantIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        regularisationRedevance.setMontant(null);

        // Create the RegularisationRedevance, which fails.
        RegularisationRedevanceDTO regularisationRedevanceDTO = regularisationRedevanceMapper.toDto(regularisationRedevance);

        restRegularisationRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regularisationRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMotifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        regularisationRedevance.setMotif(null);

        // Create the RegularisationRedevance, which fails.
        RegularisationRedevanceDTO regularisationRedevanceDTO = regularisationRedevanceMapper.toDto(regularisationRedevance);

        restRegularisationRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regularisationRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateRegularisationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        regularisationRedevance.setDateRegularisation(null);

        // Create the RegularisationRedevance, which fails.
        RegularisationRedevanceDTO regularisationRedevanceDTO = regularisationRedevanceMapper.toDto(regularisationRedevance);

        restRegularisationRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regularisationRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRegularisationRedevances() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList
        restRegularisationRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(regularisationRedevance.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(sameNumber(DEFAULT_MONTANT))))
            .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)))
            .andExpect(jsonPath("$.[*].dateRegularisation").value(hasItem(DEFAULT_DATE_REGULARISATION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRegularisationRedevancesWithEagerRelationshipsIsEnabled() throws Exception {
        when(regularisationRedevanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRegularisationRedevanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(regularisationRedevanceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRegularisationRedevancesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(regularisationRedevanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRegularisationRedevanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(regularisationRedevanceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRegularisationRedevance() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get the regularisationRedevance
        restRegularisationRedevanceMockMvc
            .perform(get(ENTITY_API_URL_ID, regularisationRedevance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(regularisationRedevance.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.montant").value(sameNumber(DEFAULT_MONTANT)))
            .andExpect(jsonPath("$.motif").value(DEFAULT_MOTIF))
            .andExpect(jsonPath("$.dateRegularisation").value(DEFAULT_DATE_REGULARISATION.toString()));
    }

    @Test
    @Transactional
    void getRegularisationRedevancesByIdFiltering() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        Long id = regularisationRedevance.getId();

        defaultRegularisationRedevanceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRegularisationRedevanceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRegularisationRedevanceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where reference equals to
        defaultRegularisationRedevanceFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where reference in
        defaultRegularisationRedevanceFiltering(
            "reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE,
            "reference.in=" + UPDATED_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where reference is not null
        defaultRegularisationRedevanceFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where reference contains
        defaultRegularisationRedevanceFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where reference does not contain
        defaultRegularisationRedevanceFiltering(
            "reference.doesNotContain=" + UPDATED_REFERENCE,
            "reference.doesNotContain=" + DEFAULT_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByMontantIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where montant equals to
        defaultRegularisationRedevanceFiltering("montant.equals=" + DEFAULT_MONTANT, "montant.equals=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByMontantIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where montant in
        defaultRegularisationRedevanceFiltering("montant.in=" + DEFAULT_MONTANT + "," + UPDATED_MONTANT, "montant.in=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByMontantIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where montant is not null
        defaultRegularisationRedevanceFiltering("montant.specified=true", "montant.specified=false");
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByMontantIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where montant is greater than or equal to
        defaultRegularisationRedevanceFiltering(
            "montant.greaterThanOrEqual=" + DEFAULT_MONTANT,
            "montant.greaterThanOrEqual=" + UPDATED_MONTANT
        );
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByMontantIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where montant is less than or equal to
        defaultRegularisationRedevanceFiltering("montant.lessThanOrEqual=" + DEFAULT_MONTANT, "montant.lessThanOrEqual=" + SMALLER_MONTANT);
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByMontantIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where montant is less than
        defaultRegularisationRedevanceFiltering("montant.lessThan=" + UPDATED_MONTANT, "montant.lessThan=" + DEFAULT_MONTANT);
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByMontantIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where montant is greater than
        defaultRegularisationRedevanceFiltering("montant.greaterThan=" + SMALLER_MONTANT, "montant.greaterThan=" + DEFAULT_MONTANT);
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByMotifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where motif equals to
        defaultRegularisationRedevanceFiltering("motif.equals=" + DEFAULT_MOTIF, "motif.equals=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByMotifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where motif in
        defaultRegularisationRedevanceFiltering("motif.in=" + DEFAULT_MOTIF + "," + UPDATED_MOTIF, "motif.in=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByMotifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where motif is not null
        defaultRegularisationRedevanceFiltering("motif.specified=true", "motif.specified=false");
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByMotifContainsSomething() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where motif contains
        defaultRegularisationRedevanceFiltering("motif.contains=" + DEFAULT_MOTIF, "motif.contains=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByMotifNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where motif does not contain
        defaultRegularisationRedevanceFiltering("motif.doesNotContain=" + UPDATED_MOTIF, "motif.doesNotContain=" + DEFAULT_MOTIF);
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByDateRegularisationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where dateRegularisation equals to
        defaultRegularisationRedevanceFiltering(
            "dateRegularisation.equals=" + DEFAULT_DATE_REGULARISATION,
            "dateRegularisation.equals=" + UPDATED_DATE_REGULARISATION
        );
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByDateRegularisationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where dateRegularisation in
        defaultRegularisationRedevanceFiltering(
            "dateRegularisation.in=" + DEFAULT_DATE_REGULARISATION + "," + UPDATED_DATE_REGULARISATION,
            "dateRegularisation.in=" + UPDATED_DATE_REGULARISATION
        );
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByDateRegularisationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        // Get all the regularisationRedevanceList where dateRegularisation is not null
        defaultRegularisationRedevanceFiltering("dateRegularisation.specified=true", "dateRegularisation.specified=false");
    }

    @Test
    @Transactional
    void getAllRegularisationRedevancesByCalculIsEqualToSomething() throws Exception {
        CalculRedevance calcul;
        if (TestUtil.findAll(em, CalculRedevance.class).isEmpty()) {
            regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);
            calcul = CalculRedevanceResourceIT.createEntity(em);
        } else {
            calcul = TestUtil.findAll(em, CalculRedevance.class).get(0);
        }
        em.persist(calcul);
        em.flush();
        regularisationRedevance.setCalcul(calcul);
        regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);
        Long calculId = calcul.getId();
        // Get all the regularisationRedevanceList where calcul equals to calculId
        defaultRegularisationRedevanceShouldBeFound("calculId.equals=" + calculId);

        // Get all the regularisationRedevanceList where calcul equals to (calculId + 1)
        defaultRegularisationRedevanceShouldNotBeFound("calculId.equals=" + (calculId + 1));
    }

    private void defaultRegularisationRedevanceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRegularisationRedevanceShouldBeFound(shouldBeFound);
        defaultRegularisationRedevanceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRegularisationRedevanceShouldBeFound(String filter) throws Exception {
        restRegularisationRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(regularisationRedevance.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(sameNumber(DEFAULT_MONTANT))))
            .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)))
            .andExpect(jsonPath("$.[*].dateRegularisation").value(hasItem(DEFAULT_DATE_REGULARISATION.toString())));

        // Check, that the count call also returns 1
        restRegularisationRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRegularisationRedevanceShouldNotBeFound(String filter) throws Exception {
        restRegularisationRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRegularisationRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRegularisationRedevance() throws Exception {
        // Get the regularisationRedevance
        restRegularisationRedevanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRegularisationRedevance() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the regularisationRedevance
        RegularisationRedevance updatedRegularisationRedevance = regularisationRedevanceRepository
            .findById(regularisationRedevance.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedRegularisationRedevance are not directly saved in db
        em.detach(updatedRegularisationRedevance);
        updatedRegularisationRedevance
            .reference(UPDATED_REFERENCE)
            .montant(UPDATED_MONTANT)
            .motif(UPDATED_MOTIF)
            .dateRegularisation(UPDATED_DATE_REGULARISATION);
        RegularisationRedevanceDTO regularisationRedevanceDTO = regularisationRedevanceMapper.toDto(updatedRegularisationRedevance);

        restRegularisationRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, regularisationRedevanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(regularisationRedevanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the RegularisationRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRegularisationRedevanceToMatchAllProperties(updatedRegularisationRedevance);
    }

    @Test
    @Transactional
    void putNonExistingRegularisationRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        regularisationRedevance.setId(longCount.incrementAndGet());

        // Create the RegularisationRedevance
        RegularisationRedevanceDTO regularisationRedevanceDTO = regularisationRedevanceMapper.toDto(regularisationRedevance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRegularisationRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, regularisationRedevanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(regularisationRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RegularisationRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRegularisationRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        regularisationRedevance.setId(longCount.incrementAndGet());

        // Create the RegularisationRedevance
        RegularisationRedevanceDTO regularisationRedevanceDTO = regularisationRedevanceMapper.toDto(regularisationRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegularisationRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(regularisationRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RegularisationRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRegularisationRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        regularisationRedevance.setId(longCount.incrementAndGet());

        // Create the RegularisationRedevance
        RegularisationRedevanceDTO regularisationRedevanceDTO = regularisationRedevanceMapper.toDto(regularisationRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegularisationRedevanceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(regularisationRedevanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RegularisationRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRegularisationRedevanceWithPatch() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the regularisationRedevance using partial update
        RegularisationRedevance partialUpdatedRegularisationRedevance = new RegularisationRedevance();
        partialUpdatedRegularisationRedevance.setId(regularisationRedevance.getId());

        partialUpdatedRegularisationRedevance.dateRegularisation(UPDATED_DATE_REGULARISATION);

        restRegularisationRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRegularisationRedevance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRegularisationRedevance))
            )
            .andExpect(status().isOk());

        // Validate the RegularisationRedevance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRegularisationRedevanceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRegularisationRedevance, regularisationRedevance),
            getPersistedRegularisationRedevance(regularisationRedevance)
        );
    }

    @Test
    @Transactional
    void fullUpdateRegularisationRedevanceWithPatch() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the regularisationRedevance using partial update
        RegularisationRedevance partialUpdatedRegularisationRedevance = new RegularisationRedevance();
        partialUpdatedRegularisationRedevance.setId(regularisationRedevance.getId());

        partialUpdatedRegularisationRedevance
            .reference(UPDATED_REFERENCE)
            .montant(UPDATED_MONTANT)
            .motif(UPDATED_MOTIF)
            .dateRegularisation(UPDATED_DATE_REGULARISATION);

        restRegularisationRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRegularisationRedevance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRegularisationRedevance))
            )
            .andExpect(status().isOk());

        // Validate the RegularisationRedevance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRegularisationRedevanceUpdatableFieldsEquals(
            partialUpdatedRegularisationRedevance,
            getPersistedRegularisationRedevance(partialUpdatedRegularisationRedevance)
        );
    }

    @Test
    @Transactional
    void patchNonExistingRegularisationRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        regularisationRedevance.setId(longCount.incrementAndGet());

        // Create the RegularisationRedevance
        RegularisationRedevanceDTO regularisationRedevanceDTO = regularisationRedevanceMapper.toDto(regularisationRedevance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRegularisationRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, regularisationRedevanceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(regularisationRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RegularisationRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRegularisationRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        regularisationRedevance.setId(longCount.incrementAndGet());

        // Create the RegularisationRedevance
        RegularisationRedevanceDTO regularisationRedevanceDTO = regularisationRedevanceMapper.toDto(regularisationRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegularisationRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(regularisationRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RegularisationRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRegularisationRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        regularisationRedevance.setId(longCount.incrementAndGet());

        // Create the RegularisationRedevance
        RegularisationRedevanceDTO regularisationRedevanceDTO = regularisationRedevanceMapper.toDto(regularisationRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRegularisationRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(regularisationRedevanceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the RegularisationRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRegularisationRedevance() throws Exception {
        // Initialize the database
        insertedRegularisationRedevance = regularisationRedevanceRepository.saveAndFlush(regularisationRedevance);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the regularisationRedevance
        restRegularisationRedevanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, regularisationRedevance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return regularisationRedevanceRepository.count();
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

    protected RegularisationRedevance getPersistedRegularisationRedevance(RegularisationRedevance regularisationRedevance) {
        return regularisationRedevanceRepository.findById(regularisationRedevance.getId()).orElseThrow();
    }

    protected void assertPersistedRegularisationRedevanceToMatchAllProperties(RegularisationRedevance expectedRegularisationRedevance) {
        assertRegularisationRedevanceAllPropertiesEquals(
            expectedRegularisationRedevance,
            getPersistedRegularisationRedevance(expectedRegularisationRedevance)
        );
    }

    protected void assertPersistedRegularisationRedevanceToMatchUpdatableProperties(
        RegularisationRedevance expectedRegularisationRedevance
    ) {
        assertRegularisationRedevanceAllUpdatablePropertiesEquals(
            expectedRegularisationRedevance,
            getPersistedRegularisationRedevance(expectedRegularisationRedevance)
        );
    }
}
