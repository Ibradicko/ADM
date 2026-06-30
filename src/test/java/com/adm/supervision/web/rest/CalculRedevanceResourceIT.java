package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.CalculRedevanceAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.domain.enumeration.StatutRedevance;
import com.adm.supervision.repository.CalculRedevanceRepository;
import com.adm.supervision.service.CalculRedevanceService;
import com.adm.supervision.service.dto.CalculRedevanceDTO;
import com.adm.supervision.service.mapper.CalculRedevanceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link CalculRedevanceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CalculRedevanceResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PERIODE_DEBUT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PERIODE_DEBUT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_PERIODE_DEBUT = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_PERIODE_FIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PERIODE_FIN = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_PERIODE_FIN = LocalDate.ofEpochDay(-1L);

    private static final BigDecimal DEFAULT_CHIFFRE_AFFAIRES = new BigDecimal(0);
    private static final BigDecimal UPDATED_CHIFFRE_AFFAIRES = new BigDecimal(1);
    private static final BigDecimal SMALLER_CHIFFRE_AFFAIRES = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_MONTANT_REDEVANCE = new BigDecimal(0);
    private static final BigDecimal UPDATED_MONTANT_REDEVANCE = new BigDecimal(1);
    private static final BigDecimal SMALLER_MONTANT_REDEVANCE = new BigDecimal(0 - 1);

    private static final StatutRedevance DEFAULT_STATUT = StatutRedevance.CALCULEE;
    private static final StatutRedevance UPDATED_STATUT = StatutRedevance.VALIDEE;

    private static final Instant DEFAULT_DATE_CALCUL = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CALCUL = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/calcul-redevances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CalculRedevanceRepository calculRedevanceRepository;

    @Mock
    private CalculRedevanceRepository calculRedevanceRepositoryMock;

    @Autowired
    private CalculRedevanceMapper calculRedevanceMapper;

    @Mock
    private CalculRedevanceService calculRedevanceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCalculRedevanceMockMvc;

    private CalculRedevance calculRedevance;

    private CalculRedevance insertedCalculRedevance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CalculRedevance createEntity(EntityManager em) {
        CalculRedevance calculRedevance = new CalculRedevance()
            .reference(DEFAULT_REFERENCE)
            .periodeDebut(DEFAULT_PERIODE_DEBUT)
            .periodeFin(DEFAULT_PERIODE_FIN)
            .chiffreAffaires(DEFAULT_CHIFFRE_AFFAIRES)
            .montantRedevance(DEFAULT_MONTANT_REDEVANCE)
            .statut(DEFAULT_STATUT)
            .dateCalcul(DEFAULT_DATE_CALCUL);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        calculRedevance.setBoutique(boutique);
        // Add required entity
        Locataire locataire;
        if (TestUtil.findAll(em, Locataire.class).isEmpty()) {
            locataire = LocataireResourceIT.createEntity();
            em.persist(locataire);
            em.flush();
        } else {
            locataire = TestUtil.findAll(em, Locataire.class).get(0);
        }
        calculRedevance.setLocataire(locataire);
        return calculRedevance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CalculRedevance createUpdatedEntity(EntityManager em) {
        CalculRedevance updatedCalculRedevance = new CalculRedevance()
            .reference(UPDATED_REFERENCE)
            .periodeDebut(UPDATED_PERIODE_DEBUT)
            .periodeFin(UPDATED_PERIODE_FIN)
            .chiffreAffaires(UPDATED_CHIFFRE_AFFAIRES)
            .montantRedevance(UPDATED_MONTANT_REDEVANCE)
            .statut(UPDATED_STATUT)
            .dateCalcul(UPDATED_DATE_CALCUL);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createUpdatedEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        updatedCalculRedevance.setBoutique(boutique);
        // Add required entity
        Locataire locataire;
        if (TestUtil.findAll(em, Locataire.class).isEmpty()) {
            locataire = LocataireResourceIT.createUpdatedEntity();
            em.persist(locataire);
            em.flush();
        } else {
            locataire = TestUtil.findAll(em, Locataire.class).get(0);
        }
        updatedCalculRedevance.setLocataire(locataire);
        return updatedCalculRedevance;
    }

    @BeforeEach
    void initTest() {
        calculRedevance = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCalculRedevance != null) {
            calculRedevanceRepository.delete(insertedCalculRedevance);
            insertedCalculRedevance = null;
        }
    }

    @Test
    @Transactional
    void createCalculRedevance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CalculRedevance
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);
        var returnedCalculRedevanceDTO = om.readValue(
            restCalculRedevanceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calculRedevanceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CalculRedevanceDTO.class
        );

        // Validate the CalculRedevance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCalculRedevance = calculRedevanceMapper.toEntity(returnedCalculRedevanceDTO);
        assertCalculRedevanceUpdatableFieldsEquals(returnedCalculRedevance, getPersistedCalculRedevance(returnedCalculRedevance));

        insertedCalculRedevance = returnedCalculRedevance;
    }

    @Test
    @Transactional
    void createCalculRedevanceWithExistingId() throws Exception {
        // Create the CalculRedevance with an existing ID
        calculRedevance.setId(1L);
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCalculRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calculRedevanceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        calculRedevance.setReference(null);

        // Create the CalculRedevance, which fails.
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        restCalculRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calculRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPeriodeDebutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        calculRedevance.setPeriodeDebut(null);

        // Create the CalculRedevance, which fails.
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        restCalculRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calculRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPeriodeFinIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        calculRedevance.setPeriodeFin(null);

        // Create the CalculRedevance, which fails.
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        restCalculRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calculRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkChiffreAffairesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        calculRedevance.setChiffreAffaires(null);

        // Create the CalculRedevance, which fails.
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        restCalculRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calculRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMontantRedevanceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        calculRedevance.setMontantRedevance(null);

        // Create the CalculRedevance, which fails.
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        restCalculRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calculRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        calculRedevance.setStatut(null);

        // Create the CalculRedevance, which fails.
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        restCalculRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calculRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCalculIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        calculRedevance.setDateCalcul(null);

        // Create the CalculRedevance, which fails.
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        restCalculRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calculRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCalculRedevances() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList
        restCalculRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(calculRedevance.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].periodeDebut").value(hasItem(DEFAULT_PERIODE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].periodeFin").value(hasItem(DEFAULT_PERIODE_FIN.toString())))
            .andExpect(jsonPath("$.[*].chiffreAffaires").value(hasItem(sameNumber(DEFAULT_CHIFFRE_AFFAIRES))))
            .andExpect(jsonPath("$.[*].montantRedevance").value(hasItem(sameNumber(DEFAULT_MONTANT_REDEVANCE))))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateCalcul").value(hasItem(DEFAULT_DATE_CALCUL.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCalculRedevancesWithEagerRelationshipsIsEnabled() throws Exception {
        when(calculRedevanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCalculRedevanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(calculRedevanceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCalculRedevancesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(calculRedevanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCalculRedevanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(calculRedevanceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCalculRedevance() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get the calculRedevance
        restCalculRedevanceMockMvc
            .perform(get(ENTITY_API_URL_ID, calculRedevance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(calculRedevance.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.periodeDebut").value(DEFAULT_PERIODE_DEBUT.toString()))
            .andExpect(jsonPath("$.periodeFin").value(DEFAULT_PERIODE_FIN.toString()))
            .andExpect(jsonPath("$.chiffreAffaires").value(sameNumber(DEFAULT_CHIFFRE_AFFAIRES)))
            .andExpect(jsonPath("$.montantRedevance").value(sameNumber(DEFAULT_MONTANT_REDEVANCE)))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.dateCalcul").value(DEFAULT_DATE_CALCUL.toString()));
    }

    @Test
    @Transactional
    void getCalculRedevancesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        Long id = calculRedevance.getId();

        defaultCalculRedevanceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCalculRedevanceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCalculRedevanceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where reference equals to
        defaultCalculRedevanceFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where reference in
        defaultCalculRedevanceFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where reference is not null
        defaultCalculRedevanceFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where reference contains
        defaultCalculRedevanceFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where reference does not contain
        defaultCalculRedevanceFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeDebutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeDebut equals to
        defaultCalculRedevanceFiltering("periodeDebut.equals=" + DEFAULT_PERIODE_DEBUT, "periodeDebut.equals=" + UPDATED_PERIODE_DEBUT);
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeDebutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeDebut in
        defaultCalculRedevanceFiltering(
            "periodeDebut.in=" + DEFAULT_PERIODE_DEBUT + "," + UPDATED_PERIODE_DEBUT,
            "periodeDebut.in=" + UPDATED_PERIODE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeDebutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeDebut is not null
        defaultCalculRedevanceFiltering("periodeDebut.specified=true", "periodeDebut.specified=false");
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeDebutIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeDebut is greater than or equal to
        defaultCalculRedevanceFiltering(
            "periodeDebut.greaterThanOrEqual=" + DEFAULT_PERIODE_DEBUT,
            "periodeDebut.greaterThanOrEqual=" + UPDATED_PERIODE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeDebutIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeDebut is less than or equal to
        defaultCalculRedevanceFiltering(
            "periodeDebut.lessThanOrEqual=" + DEFAULT_PERIODE_DEBUT,
            "periodeDebut.lessThanOrEqual=" + SMALLER_PERIODE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeDebutIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeDebut is less than
        defaultCalculRedevanceFiltering("periodeDebut.lessThan=" + UPDATED_PERIODE_DEBUT, "periodeDebut.lessThan=" + DEFAULT_PERIODE_DEBUT);
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeDebutIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeDebut is greater than
        defaultCalculRedevanceFiltering(
            "periodeDebut.greaterThan=" + SMALLER_PERIODE_DEBUT,
            "periodeDebut.greaterThan=" + DEFAULT_PERIODE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeFinIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeFin equals to
        defaultCalculRedevanceFiltering("periodeFin.equals=" + DEFAULT_PERIODE_FIN, "periodeFin.equals=" + UPDATED_PERIODE_FIN);
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeFinIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeFin in
        defaultCalculRedevanceFiltering(
            "periodeFin.in=" + DEFAULT_PERIODE_FIN + "," + UPDATED_PERIODE_FIN,
            "periodeFin.in=" + UPDATED_PERIODE_FIN
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeFinIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeFin is not null
        defaultCalculRedevanceFiltering("periodeFin.specified=true", "periodeFin.specified=false");
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeFinIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeFin is greater than or equal to
        defaultCalculRedevanceFiltering(
            "periodeFin.greaterThanOrEqual=" + DEFAULT_PERIODE_FIN,
            "periodeFin.greaterThanOrEqual=" + UPDATED_PERIODE_FIN
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeFinIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeFin is less than or equal to
        defaultCalculRedevanceFiltering(
            "periodeFin.lessThanOrEqual=" + DEFAULT_PERIODE_FIN,
            "periodeFin.lessThanOrEqual=" + SMALLER_PERIODE_FIN
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeFinIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeFin is less than
        defaultCalculRedevanceFiltering("periodeFin.lessThan=" + UPDATED_PERIODE_FIN, "periodeFin.lessThan=" + DEFAULT_PERIODE_FIN);
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByPeriodeFinIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where periodeFin is greater than
        defaultCalculRedevanceFiltering("periodeFin.greaterThan=" + SMALLER_PERIODE_FIN, "periodeFin.greaterThan=" + DEFAULT_PERIODE_FIN);
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByChiffreAffairesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where chiffreAffaires equals to
        defaultCalculRedevanceFiltering(
            "chiffreAffaires.equals=" + DEFAULT_CHIFFRE_AFFAIRES,
            "chiffreAffaires.equals=" + UPDATED_CHIFFRE_AFFAIRES
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByChiffreAffairesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where chiffreAffaires in
        defaultCalculRedevanceFiltering(
            "chiffreAffaires.in=" + DEFAULT_CHIFFRE_AFFAIRES + "," + UPDATED_CHIFFRE_AFFAIRES,
            "chiffreAffaires.in=" + UPDATED_CHIFFRE_AFFAIRES
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByChiffreAffairesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where chiffreAffaires is not null
        defaultCalculRedevanceFiltering("chiffreAffaires.specified=true", "chiffreAffaires.specified=false");
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByChiffreAffairesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where chiffreAffaires is greater than or equal to
        defaultCalculRedevanceFiltering(
            "chiffreAffaires.greaterThanOrEqual=" + DEFAULT_CHIFFRE_AFFAIRES,
            "chiffreAffaires.greaterThanOrEqual=" + UPDATED_CHIFFRE_AFFAIRES
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByChiffreAffairesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where chiffreAffaires is less than or equal to
        defaultCalculRedevanceFiltering(
            "chiffreAffaires.lessThanOrEqual=" + DEFAULT_CHIFFRE_AFFAIRES,
            "chiffreAffaires.lessThanOrEqual=" + SMALLER_CHIFFRE_AFFAIRES
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByChiffreAffairesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where chiffreAffaires is less than
        defaultCalculRedevanceFiltering(
            "chiffreAffaires.lessThan=" + UPDATED_CHIFFRE_AFFAIRES,
            "chiffreAffaires.lessThan=" + DEFAULT_CHIFFRE_AFFAIRES
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByChiffreAffairesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where chiffreAffaires is greater than
        defaultCalculRedevanceFiltering(
            "chiffreAffaires.greaterThan=" + SMALLER_CHIFFRE_AFFAIRES,
            "chiffreAffaires.greaterThan=" + DEFAULT_CHIFFRE_AFFAIRES
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByMontantRedevanceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where montantRedevance equals to
        defaultCalculRedevanceFiltering(
            "montantRedevance.equals=" + DEFAULT_MONTANT_REDEVANCE,
            "montantRedevance.equals=" + UPDATED_MONTANT_REDEVANCE
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByMontantRedevanceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where montantRedevance in
        defaultCalculRedevanceFiltering(
            "montantRedevance.in=" + DEFAULT_MONTANT_REDEVANCE + "," + UPDATED_MONTANT_REDEVANCE,
            "montantRedevance.in=" + UPDATED_MONTANT_REDEVANCE
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByMontantRedevanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where montantRedevance is not null
        defaultCalculRedevanceFiltering("montantRedevance.specified=true", "montantRedevance.specified=false");
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByMontantRedevanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where montantRedevance is greater than or equal to
        defaultCalculRedevanceFiltering(
            "montantRedevance.greaterThanOrEqual=" + DEFAULT_MONTANT_REDEVANCE,
            "montantRedevance.greaterThanOrEqual=" + UPDATED_MONTANT_REDEVANCE
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByMontantRedevanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where montantRedevance is less than or equal to
        defaultCalculRedevanceFiltering(
            "montantRedevance.lessThanOrEqual=" + DEFAULT_MONTANT_REDEVANCE,
            "montantRedevance.lessThanOrEqual=" + SMALLER_MONTANT_REDEVANCE
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByMontantRedevanceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where montantRedevance is less than
        defaultCalculRedevanceFiltering(
            "montantRedevance.lessThan=" + UPDATED_MONTANT_REDEVANCE,
            "montantRedevance.lessThan=" + DEFAULT_MONTANT_REDEVANCE
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByMontantRedevanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where montantRedevance is greater than
        defaultCalculRedevanceFiltering(
            "montantRedevance.greaterThan=" + SMALLER_MONTANT_REDEVANCE,
            "montantRedevance.greaterThan=" + DEFAULT_MONTANT_REDEVANCE
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where statut equals to
        defaultCalculRedevanceFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where statut in
        defaultCalculRedevanceFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where statut is not null
        defaultCalculRedevanceFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByDateCalculIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where dateCalcul equals to
        defaultCalculRedevanceFiltering("dateCalcul.equals=" + DEFAULT_DATE_CALCUL, "dateCalcul.equals=" + UPDATED_DATE_CALCUL);
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByDateCalculIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where dateCalcul in
        defaultCalculRedevanceFiltering(
            "dateCalcul.in=" + DEFAULT_DATE_CALCUL + "," + UPDATED_DATE_CALCUL,
            "dateCalcul.in=" + UPDATED_DATE_CALCUL
        );
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByDateCalculIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        // Get all the calculRedevanceList where dateCalcul is not null
        defaultCalculRedevanceFiltering("dateCalcul.specified=true", "dateCalcul.specified=false");
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByBoutiqueIsEqualToSomething() throws Exception {
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            calculRedevanceRepository.saveAndFlush(calculRedevance);
            boutique = BoutiqueResourceIT.createEntity();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutique);
        em.flush();
        calculRedevance.setBoutique(boutique);
        calculRedevanceRepository.saveAndFlush(calculRedevance);
        Long boutiqueId = boutique.getId();
        // Get all the calculRedevanceList where boutique equals to boutiqueId
        defaultCalculRedevanceShouldBeFound("boutiqueId.equals=" + boutiqueId);

        // Get all the calculRedevanceList where boutique equals to (boutiqueId + 1)
        defaultCalculRedevanceShouldNotBeFound("boutiqueId.equals=" + (boutiqueId + 1));
    }

    @Test
    @Transactional
    void getAllCalculRedevancesByLocataireIsEqualToSomething() throws Exception {
        Locataire locataire;
        if (TestUtil.findAll(em, Locataire.class).isEmpty()) {
            calculRedevanceRepository.saveAndFlush(calculRedevance);
            locataire = LocataireResourceIT.createEntity();
        } else {
            locataire = TestUtil.findAll(em, Locataire.class).get(0);
        }
        em.persist(locataire);
        em.flush();
        calculRedevance.setLocataire(locataire);
        calculRedevanceRepository.saveAndFlush(calculRedevance);
        Long locataireId = locataire.getId();
        // Get all the calculRedevanceList where locataire equals to locataireId
        defaultCalculRedevanceShouldBeFound("locataireId.equals=" + locataireId);

        // Get all the calculRedevanceList where locataire equals to (locataireId + 1)
        defaultCalculRedevanceShouldNotBeFound("locataireId.equals=" + (locataireId + 1));
    }

    private void defaultCalculRedevanceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCalculRedevanceShouldBeFound(shouldBeFound);
        defaultCalculRedevanceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCalculRedevanceShouldBeFound(String filter) throws Exception {
        restCalculRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(calculRedevance.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].periodeDebut").value(hasItem(DEFAULT_PERIODE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].periodeFin").value(hasItem(DEFAULT_PERIODE_FIN.toString())))
            .andExpect(jsonPath("$.[*].chiffreAffaires").value(hasItem(sameNumber(DEFAULT_CHIFFRE_AFFAIRES))))
            .andExpect(jsonPath("$.[*].montantRedevance").value(hasItem(sameNumber(DEFAULT_MONTANT_REDEVANCE))))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateCalcul").value(hasItem(DEFAULT_DATE_CALCUL.toString())));

        // Check, that the count call also returns 1
        restCalculRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCalculRedevanceShouldNotBeFound(String filter) throws Exception {
        restCalculRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCalculRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCalculRedevance() throws Exception {
        // Get the calculRedevance
        restCalculRedevanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCalculRedevance() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the calculRedevance
        CalculRedevance updatedCalculRedevance = calculRedevanceRepository.findById(calculRedevance.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCalculRedevance are not directly saved in db
        em.detach(updatedCalculRedevance);
        updatedCalculRedevance
            .reference(UPDATED_REFERENCE)
            .periodeDebut(UPDATED_PERIODE_DEBUT)
            .periodeFin(UPDATED_PERIODE_FIN)
            .chiffreAffaires(UPDATED_CHIFFRE_AFFAIRES)
            .montantRedevance(UPDATED_MONTANT_REDEVANCE)
            .statut(UPDATED_STATUT)
            .dateCalcul(UPDATED_DATE_CALCUL);
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(updatedCalculRedevance);

        restCalculRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, calculRedevanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(calculRedevanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the CalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCalculRedevanceToMatchAllProperties(updatedCalculRedevance);
    }

    @Test
    @Transactional
    void putNonExistingCalculRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calculRedevance.setId(longCount.incrementAndGet());

        // Create the CalculRedevance
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCalculRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, calculRedevanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(calculRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCalculRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calculRedevance.setId(longCount.incrementAndGet());

        // Create the CalculRedevance
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalculRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(calculRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCalculRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calculRedevance.setId(longCount.incrementAndGet());

        // Create the CalculRedevance
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalculRedevanceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(calculRedevanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCalculRedevanceWithPatch() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the calculRedevance using partial update
        CalculRedevance partialUpdatedCalculRedevance = new CalculRedevance();
        partialUpdatedCalculRedevance.setId(calculRedevance.getId());

        partialUpdatedCalculRedevance
            .periodeDebut(UPDATED_PERIODE_DEBUT)
            .periodeFin(UPDATED_PERIODE_FIN)
            .montantRedevance(UPDATED_MONTANT_REDEVANCE)
            .dateCalcul(UPDATED_DATE_CALCUL);

        restCalculRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCalculRedevance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCalculRedevance))
            )
            .andExpect(status().isOk());

        // Validate the CalculRedevance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCalculRedevanceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCalculRedevance, calculRedevance),
            getPersistedCalculRedevance(calculRedevance)
        );
    }

    @Test
    @Transactional
    void fullUpdateCalculRedevanceWithPatch() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the calculRedevance using partial update
        CalculRedevance partialUpdatedCalculRedevance = new CalculRedevance();
        partialUpdatedCalculRedevance.setId(calculRedevance.getId());

        partialUpdatedCalculRedevance
            .reference(UPDATED_REFERENCE)
            .periodeDebut(UPDATED_PERIODE_DEBUT)
            .periodeFin(UPDATED_PERIODE_FIN)
            .chiffreAffaires(UPDATED_CHIFFRE_AFFAIRES)
            .montantRedevance(UPDATED_MONTANT_REDEVANCE)
            .statut(UPDATED_STATUT)
            .dateCalcul(UPDATED_DATE_CALCUL);

        restCalculRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCalculRedevance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCalculRedevance))
            )
            .andExpect(status().isOk());

        // Validate the CalculRedevance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCalculRedevanceUpdatableFieldsEquals(
            partialUpdatedCalculRedevance,
            getPersistedCalculRedevance(partialUpdatedCalculRedevance)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCalculRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calculRedevance.setId(longCount.incrementAndGet());

        // Create the CalculRedevance
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCalculRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, calculRedevanceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(calculRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCalculRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calculRedevance.setId(longCount.incrementAndGet());

        // Create the CalculRedevance
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalculRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(calculRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCalculRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        calculRedevance.setId(longCount.incrementAndGet());

        // Create the CalculRedevance
        CalculRedevanceDTO calculRedevanceDTO = calculRedevanceMapper.toDto(calculRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCalculRedevanceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(calculRedevanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCalculRedevance() throws Exception {
        // Initialize the database
        insertedCalculRedevance = calculRedevanceRepository.saveAndFlush(calculRedevance);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the calculRedevance
        restCalculRedevanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, calculRedevance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return calculRedevanceRepository.count();
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

    protected CalculRedevance getPersistedCalculRedevance(CalculRedevance calculRedevance) {
        return calculRedevanceRepository.findById(calculRedevance.getId()).orElseThrow();
    }

    protected void assertPersistedCalculRedevanceToMatchAllProperties(CalculRedevance expectedCalculRedevance) {
        assertCalculRedevanceAllPropertiesEquals(expectedCalculRedevance, getPersistedCalculRedevance(expectedCalculRedevance));
    }

    protected void assertPersistedCalculRedevanceToMatchUpdatableProperties(CalculRedevance expectedCalculRedevance) {
        assertCalculRedevanceAllUpdatablePropertiesEquals(expectedCalculRedevance, getPersistedCalculRedevance(expectedCalculRedevance));
    }
}
