package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.PaiementRedevanceAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.PaiementRedevance;
import com.adm.supervision.repository.PaiementRedevanceRepository;
import com.adm.supervision.service.PaiementRedevanceService;
import com.adm.supervision.service.dto.PaiementRedevanceDTO;
import com.adm.supervision.service.mapper.PaiementRedevanceMapper;
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
 * Integration tests for the {@link PaiementRedevanceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PaiementRedevanceResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_MONTANT = new BigDecimal(0);
    private static final BigDecimal UPDATED_MONTANT = new BigDecimal(1);
    private static final BigDecimal SMALLER_MONTANT = new BigDecimal(0 - 1);

    private static final LocalDate DEFAULT_DATE_PAIEMENT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_PAIEMENT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE_PAIEMENT = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_MODE_PAIEMENT = "AAAAAAAAAA";
    private static final String UPDATED_MODE_PAIEMENT = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENTAIRE = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/paiement-redevances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaiementRedevanceRepository paiementRedevanceRepository;

    @Mock
    private PaiementRedevanceRepository paiementRedevanceRepositoryMock;

    @Autowired
    private PaiementRedevanceMapper paiementRedevanceMapper;

    @Mock
    private PaiementRedevanceService paiementRedevanceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaiementRedevanceMockMvc;

    private PaiementRedevance paiementRedevance;

    private PaiementRedevance insertedPaiementRedevance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaiementRedevance createEntity(EntityManager em) {
        PaiementRedevance paiementRedevance = new PaiementRedevance()
            .reference(DEFAULT_REFERENCE)
            .montant(DEFAULT_MONTANT)
            .datePaiement(DEFAULT_DATE_PAIEMENT)
            .modePaiement(DEFAULT_MODE_PAIEMENT)
            .commentaire(DEFAULT_COMMENTAIRE);
        // Add required entity
        CalculRedevance calculRedevance;
        if (TestUtil.findAll(em, CalculRedevance.class).isEmpty()) {
            calculRedevance = CalculRedevanceResourceIT.createEntity(em);
            em.persist(calculRedevance);
            em.flush();
        } else {
            calculRedevance = TestUtil.findAll(em, CalculRedevance.class).get(0);
        }
        paiementRedevance.setCalcul(calculRedevance);
        return paiementRedevance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaiementRedevance createUpdatedEntity(EntityManager em) {
        PaiementRedevance updatedPaiementRedevance = new PaiementRedevance()
            .reference(UPDATED_REFERENCE)
            .montant(UPDATED_MONTANT)
            .datePaiement(UPDATED_DATE_PAIEMENT)
            .modePaiement(UPDATED_MODE_PAIEMENT)
            .commentaire(UPDATED_COMMENTAIRE);
        // Add required entity
        CalculRedevance calculRedevance;
        if (TestUtil.findAll(em, CalculRedevance.class).isEmpty()) {
            calculRedevance = CalculRedevanceResourceIT.createUpdatedEntity(em);
            em.persist(calculRedevance);
            em.flush();
        } else {
            calculRedevance = TestUtil.findAll(em, CalculRedevance.class).get(0);
        }
        updatedPaiementRedevance.setCalcul(calculRedevance);
        return updatedPaiementRedevance;
    }

    @BeforeEach
    void initTest() {
        paiementRedevance = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPaiementRedevance != null) {
            paiementRedevanceRepository.delete(insertedPaiementRedevance);
            insertedPaiementRedevance = null;
        }
    }

    @Test
    @Transactional
    void createPaiementRedevance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PaiementRedevance
        PaiementRedevanceDTO paiementRedevanceDTO = paiementRedevanceMapper.toDto(paiementRedevance);
        var returnedPaiementRedevanceDTO = om.readValue(
            restPaiementRedevanceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementRedevanceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PaiementRedevanceDTO.class
        );

        // Validate the PaiementRedevance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPaiementRedevance = paiementRedevanceMapper.toEntity(returnedPaiementRedevanceDTO);
        assertPaiementRedevanceUpdatableFieldsEquals(returnedPaiementRedevance, getPersistedPaiementRedevance(returnedPaiementRedevance));

        insertedPaiementRedevance = returnedPaiementRedevance;
    }

    @Test
    @Transactional
    void createPaiementRedevanceWithExistingId() throws Exception {
        // Create the PaiementRedevance with an existing ID
        paiementRedevance.setId(1L);
        PaiementRedevanceDTO paiementRedevanceDTO = paiementRedevanceMapper.toDto(paiementRedevance);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaiementRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementRedevanceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PaiementRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paiementRedevance.setReference(null);

        // Create the PaiementRedevance, which fails.
        PaiementRedevanceDTO paiementRedevanceDTO = paiementRedevanceMapper.toDto(paiementRedevance);

        restPaiementRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMontantIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paiementRedevance.setMontant(null);

        // Create the PaiementRedevance, which fails.
        PaiementRedevanceDTO paiementRedevanceDTO = paiementRedevanceMapper.toDto(paiementRedevance);

        restPaiementRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDatePaiementIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paiementRedevance.setDatePaiement(null);

        // Create the PaiementRedevance, which fails.
        PaiementRedevanceDTO paiementRedevanceDTO = paiementRedevanceMapper.toDto(paiementRedevance);

        restPaiementRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPaiementRedevances() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList
        restPaiementRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paiementRedevance.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(sameNumber(DEFAULT_MONTANT))))
            .andExpect(jsonPath("$.[*].datePaiement").value(hasItem(DEFAULT_DATE_PAIEMENT.toString())))
            .andExpect(jsonPath("$.[*].modePaiement").value(hasItem(DEFAULT_MODE_PAIEMENT)))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPaiementRedevancesWithEagerRelationshipsIsEnabled() throws Exception {
        when(paiementRedevanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPaiementRedevanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(paiementRedevanceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPaiementRedevancesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(paiementRedevanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPaiementRedevanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(paiementRedevanceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPaiementRedevance() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get the paiementRedevance
        restPaiementRedevanceMockMvc
            .perform(get(ENTITY_API_URL_ID, paiementRedevance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paiementRedevance.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.montant").value(sameNumber(DEFAULT_MONTANT)))
            .andExpect(jsonPath("$.datePaiement").value(DEFAULT_DATE_PAIEMENT.toString()))
            .andExpect(jsonPath("$.modePaiement").value(DEFAULT_MODE_PAIEMENT))
            .andExpect(jsonPath("$.commentaire").value(DEFAULT_COMMENTAIRE));
    }

    @Test
    @Transactional
    void getPaiementRedevancesByIdFiltering() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        Long id = paiementRedevance.getId();

        defaultPaiementRedevanceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPaiementRedevanceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPaiementRedevanceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where reference equals to
        defaultPaiementRedevanceFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where reference in
        defaultPaiementRedevanceFiltering(
            "reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE,
            "reference.in=" + UPDATED_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where reference is not null
        defaultPaiementRedevanceFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where reference contains
        defaultPaiementRedevanceFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where reference does not contain
        defaultPaiementRedevanceFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByMontantIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where montant equals to
        defaultPaiementRedevanceFiltering("montant.equals=" + DEFAULT_MONTANT, "montant.equals=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByMontantIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where montant in
        defaultPaiementRedevanceFiltering("montant.in=" + DEFAULT_MONTANT + "," + UPDATED_MONTANT, "montant.in=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByMontantIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where montant is not null
        defaultPaiementRedevanceFiltering("montant.specified=true", "montant.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByMontantIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where montant is greater than or equal to
        defaultPaiementRedevanceFiltering("montant.greaterThanOrEqual=" + DEFAULT_MONTANT, "montant.greaterThanOrEqual=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByMontantIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where montant is less than or equal to
        defaultPaiementRedevanceFiltering("montant.lessThanOrEqual=" + DEFAULT_MONTANT, "montant.lessThanOrEqual=" + SMALLER_MONTANT);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByMontantIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where montant is less than
        defaultPaiementRedevanceFiltering("montant.lessThan=" + UPDATED_MONTANT, "montant.lessThan=" + DEFAULT_MONTANT);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByMontantIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where montant is greater than
        defaultPaiementRedevanceFiltering("montant.greaterThan=" + SMALLER_MONTANT, "montant.greaterThan=" + DEFAULT_MONTANT);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByDatePaiementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where datePaiement equals to
        defaultPaiementRedevanceFiltering("datePaiement.equals=" + DEFAULT_DATE_PAIEMENT, "datePaiement.equals=" + UPDATED_DATE_PAIEMENT);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByDatePaiementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where datePaiement in
        defaultPaiementRedevanceFiltering(
            "datePaiement.in=" + DEFAULT_DATE_PAIEMENT + "," + UPDATED_DATE_PAIEMENT,
            "datePaiement.in=" + UPDATED_DATE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByDatePaiementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where datePaiement is not null
        defaultPaiementRedevanceFiltering("datePaiement.specified=true", "datePaiement.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByDatePaiementIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where datePaiement is greater than or equal to
        defaultPaiementRedevanceFiltering(
            "datePaiement.greaterThanOrEqual=" + DEFAULT_DATE_PAIEMENT,
            "datePaiement.greaterThanOrEqual=" + UPDATED_DATE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByDatePaiementIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where datePaiement is less than or equal to
        defaultPaiementRedevanceFiltering(
            "datePaiement.lessThanOrEqual=" + DEFAULT_DATE_PAIEMENT,
            "datePaiement.lessThanOrEqual=" + SMALLER_DATE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByDatePaiementIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where datePaiement is less than
        defaultPaiementRedevanceFiltering(
            "datePaiement.lessThan=" + UPDATED_DATE_PAIEMENT,
            "datePaiement.lessThan=" + DEFAULT_DATE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByDatePaiementIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where datePaiement is greater than
        defaultPaiementRedevanceFiltering(
            "datePaiement.greaterThan=" + SMALLER_DATE_PAIEMENT,
            "datePaiement.greaterThan=" + DEFAULT_DATE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByModePaiementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where modePaiement equals to
        defaultPaiementRedevanceFiltering("modePaiement.equals=" + DEFAULT_MODE_PAIEMENT, "modePaiement.equals=" + UPDATED_MODE_PAIEMENT);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByModePaiementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where modePaiement in
        defaultPaiementRedevanceFiltering(
            "modePaiement.in=" + DEFAULT_MODE_PAIEMENT + "," + UPDATED_MODE_PAIEMENT,
            "modePaiement.in=" + UPDATED_MODE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByModePaiementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where modePaiement is not null
        defaultPaiementRedevanceFiltering("modePaiement.specified=true", "modePaiement.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByModePaiementContainsSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where modePaiement contains
        defaultPaiementRedevanceFiltering(
            "modePaiement.contains=" + DEFAULT_MODE_PAIEMENT,
            "modePaiement.contains=" + UPDATED_MODE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByModePaiementNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where modePaiement does not contain
        defaultPaiementRedevanceFiltering(
            "modePaiement.doesNotContain=" + UPDATED_MODE_PAIEMENT,
            "modePaiement.doesNotContain=" + DEFAULT_MODE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByCommentaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where commentaire equals to
        defaultPaiementRedevanceFiltering("commentaire.equals=" + DEFAULT_COMMENTAIRE, "commentaire.equals=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByCommentaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where commentaire in
        defaultPaiementRedevanceFiltering(
            "commentaire.in=" + DEFAULT_COMMENTAIRE + "," + UPDATED_COMMENTAIRE,
            "commentaire.in=" + UPDATED_COMMENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByCommentaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where commentaire is not null
        defaultPaiementRedevanceFiltering("commentaire.specified=true", "commentaire.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByCommentaireContainsSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where commentaire contains
        defaultPaiementRedevanceFiltering("commentaire.contains=" + DEFAULT_COMMENTAIRE, "commentaire.contains=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByCommentaireNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        // Get all the paiementRedevanceList where commentaire does not contain
        defaultPaiementRedevanceFiltering(
            "commentaire.doesNotContain=" + UPDATED_COMMENTAIRE,
            "commentaire.doesNotContain=" + DEFAULT_COMMENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllPaiementRedevancesByCalculIsEqualToSomething() throws Exception {
        CalculRedevance calcul;
        if (TestUtil.findAll(em, CalculRedevance.class).isEmpty()) {
            paiementRedevanceRepository.saveAndFlush(paiementRedevance);
            calcul = CalculRedevanceResourceIT.createEntity(em);
        } else {
            calcul = TestUtil.findAll(em, CalculRedevance.class).get(0);
        }
        em.persist(calcul);
        em.flush();
        paiementRedevance.setCalcul(calcul);
        paiementRedevanceRepository.saveAndFlush(paiementRedevance);
        Long calculId = calcul.getId();
        // Get all the paiementRedevanceList where calcul equals to calculId
        defaultPaiementRedevanceShouldBeFound("calculId.equals=" + calculId);

        // Get all the paiementRedevanceList where calcul equals to (calculId + 1)
        defaultPaiementRedevanceShouldNotBeFound("calculId.equals=" + (calculId + 1));
    }

    private void defaultPaiementRedevanceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPaiementRedevanceShouldBeFound(shouldBeFound);
        defaultPaiementRedevanceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPaiementRedevanceShouldBeFound(String filter) throws Exception {
        restPaiementRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paiementRedevance.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(sameNumber(DEFAULT_MONTANT))))
            .andExpect(jsonPath("$.[*].datePaiement").value(hasItem(DEFAULT_DATE_PAIEMENT.toString())))
            .andExpect(jsonPath("$.[*].modePaiement").value(hasItem(DEFAULT_MODE_PAIEMENT)))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));

        // Check, that the count call also returns 1
        restPaiementRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPaiementRedevanceShouldNotBeFound(String filter) throws Exception {
        restPaiementRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPaiementRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPaiementRedevance() throws Exception {
        // Get the paiementRedevance
        restPaiementRedevanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaiementRedevance() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paiementRedevance
        PaiementRedevance updatedPaiementRedevance = paiementRedevanceRepository.findById(paiementRedevance.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPaiementRedevance are not directly saved in db
        em.detach(updatedPaiementRedevance);
        updatedPaiementRedevance
            .reference(UPDATED_REFERENCE)
            .montant(UPDATED_MONTANT)
            .datePaiement(UPDATED_DATE_PAIEMENT)
            .modePaiement(UPDATED_MODE_PAIEMENT)
            .commentaire(UPDATED_COMMENTAIRE);
        PaiementRedevanceDTO paiementRedevanceDTO = paiementRedevanceMapper.toDto(updatedPaiementRedevance);

        restPaiementRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paiementRedevanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paiementRedevanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the PaiementRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaiementRedevanceToMatchAllProperties(updatedPaiementRedevance);
    }

    @Test
    @Transactional
    void putNonExistingPaiementRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiementRedevance.setId(longCount.incrementAndGet());

        // Create the PaiementRedevance
        PaiementRedevanceDTO paiementRedevanceDTO = paiementRedevanceMapper.toDto(paiementRedevance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaiementRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paiementRedevanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paiementRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaiementRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaiementRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiementRedevance.setId(longCount.incrementAndGet());

        // Create the PaiementRedevance
        PaiementRedevanceDTO paiementRedevanceDTO = paiementRedevanceMapper.toDto(paiementRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paiementRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaiementRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaiementRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiementRedevance.setId(longCount.incrementAndGet());

        // Create the PaiementRedevance
        PaiementRedevanceDTO paiementRedevanceDTO = paiementRedevanceMapper.toDto(paiementRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementRedevanceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementRedevanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaiementRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaiementRedevanceWithPatch() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paiementRedevance using partial update
        PaiementRedevance partialUpdatedPaiementRedevance = new PaiementRedevance();
        partialUpdatedPaiementRedevance.setId(paiementRedevance.getId());

        partialUpdatedPaiementRedevance.reference(UPDATED_REFERENCE).montant(UPDATED_MONTANT);

        restPaiementRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaiementRedevance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaiementRedevance))
            )
            .andExpect(status().isOk());

        // Validate the PaiementRedevance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaiementRedevanceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPaiementRedevance, paiementRedevance),
            getPersistedPaiementRedevance(paiementRedevance)
        );
    }

    @Test
    @Transactional
    void fullUpdatePaiementRedevanceWithPatch() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paiementRedevance using partial update
        PaiementRedevance partialUpdatedPaiementRedevance = new PaiementRedevance();
        partialUpdatedPaiementRedevance.setId(paiementRedevance.getId());

        partialUpdatedPaiementRedevance
            .reference(UPDATED_REFERENCE)
            .montant(UPDATED_MONTANT)
            .datePaiement(UPDATED_DATE_PAIEMENT)
            .modePaiement(UPDATED_MODE_PAIEMENT)
            .commentaire(UPDATED_COMMENTAIRE);

        restPaiementRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaiementRedevance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaiementRedevance))
            )
            .andExpect(status().isOk());

        // Validate the PaiementRedevance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaiementRedevanceUpdatableFieldsEquals(
            partialUpdatedPaiementRedevance,
            getPersistedPaiementRedevance(partialUpdatedPaiementRedevance)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPaiementRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiementRedevance.setId(longCount.incrementAndGet());

        // Create the PaiementRedevance
        PaiementRedevanceDTO paiementRedevanceDTO = paiementRedevanceMapper.toDto(paiementRedevance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaiementRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paiementRedevanceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paiementRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaiementRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaiementRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiementRedevance.setId(longCount.incrementAndGet());

        // Create the PaiementRedevance
        PaiementRedevanceDTO paiementRedevanceDTO = paiementRedevanceMapper.toDto(paiementRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paiementRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaiementRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaiementRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiementRedevance.setId(longCount.incrementAndGet());

        // Create the PaiementRedevance
        PaiementRedevanceDTO paiementRedevanceDTO = paiementRedevanceMapper.toDto(paiementRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementRedevanceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(paiementRedevanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaiementRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaiementRedevance() throws Exception {
        // Initialize the database
        insertedPaiementRedevance = paiementRedevanceRepository.saveAndFlush(paiementRedevance);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the paiementRedevance
        restPaiementRedevanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, paiementRedevance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paiementRedevanceRepository.count();
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

    protected PaiementRedevance getPersistedPaiementRedevance(PaiementRedevance paiementRedevance) {
        return paiementRedevanceRepository.findById(paiementRedevance.getId()).orElseThrow();
    }

    protected void assertPersistedPaiementRedevanceToMatchAllProperties(PaiementRedevance expectedPaiementRedevance) {
        assertPaiementRedevanceAllPropertiesEquals(expectedPaiementRedevance, getPersistedPaiementRedevance(expectedPaiementRedevance));
    }

    protected void assertPersistedPaiementRedevanceToMatchUpdatableProperties(PaiementRedevance expectedPaiementRedevance) {
        assertPaiementRedevanceAllUpdatablePropertiesEquals(
            expectedPaiementRedevance,
            getPersistedPaiementRedevance(expectedPaiementRedevance)
        );
    }
}
