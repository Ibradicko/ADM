package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.RapportExportAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.domain.RapportExport;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.enumeration.FormatExport;
import com.adm.supervision.repository.RapportExportRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.service.RapportExportService;
import com.adm.supervision.service.dto.RapportExportDTO;
import com.adm.supervision.service.mapper.RapportExportMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link RapportExportResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RapportExportResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE_RAPPORT = "AAAAAAAAAA";
    private static final String UPDATED_TYPE_RAPPORT = "BBBBBBBBBB";

    private static final FormatExport DEFAULT_FORMAT = FormatExport.PDF;
    private static final FormatExport UPDATED_FORMAT = FormatExport.EXCEL;

    private static final LocalDate DEFAULT_PERIODE_DEBUT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PERIODE_DEBUT = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_PERIODE_DEBUT = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_PERIODE_FIN = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PERIODE_FIN = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_PERIODE_FIN = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_CHEMIN_FICHIER = "AAAAAAAAAA";
    private static final String UPDATED_CHEMIN_FICHIER = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_GENERATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_GENERATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/rapport-exports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RapportExportRepository rapportExportRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private RapportExportRepository rapportExportRepositoryMock;

    @Autowired
    private RapportExportMapper rapportExportMapper;

    @Mock
    private RapportExportService rapportExportServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRapportExportMockMvc;

    private RapportExport rapportExport;

    private RapportExport insertedRapportExport;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RapportExport createEntity(EntityManager em) {
        RapportExport rapportExport = new RapportExport()
            .reference(DEFAULT_REFERENCE)
            .typeRapport(DEFAULT_TYPE_RAPPORT)
            .format(DEFAULT_FORMAT)
            .periodeDebut(DEFAULT_PERIODE_DEBUT)
            .periodeFin(DEFAULT_PERIODE_FIN)
            .cheminFichier(DEFAULT_CHEMIN_FICHIER)
            .dateGeneration(DEFAULT_DATE_GENERATION);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        rapportExport.setUtilisateur(user);
        return rapportExport;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RapportExport createUpdatedEntity(EntityManager em) {
        RapportExport updatedRapportExport = new RapportExport()
            .reference(UPDATED_REFERENCE)
            .typeRapport(UPDATED_TYPE_RAPPORT)
            .format(UPDATED_FORMAT)
            .periodeDebut(UPDATED_PERIODE_DEBUT)
            .periodeFin(UPDATED_PERIODE_FIN)
            .cheminFichier(UPDATED_CHEMIN_FICHIER)
            .dateGeneration(UPDATED_DATE_GENERATION);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedRapportExport.setUtilisateur(user);
        return updatedRapportExport;
    }

    @BeforeEach
    void initTest() {
        rapportExport = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedRapportExport != null) {
            rapportExportRepository.delete(insertedRapportExport);
            insertedRapportExport = null;
        }
    }

    @Test
    @Transactional
    void createRapportExport() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RapportExport
        RapportExportDTO rapportExportDTO = rapportExportMapper.toDto(rapportExport);
        var returnedRapportExportDTO = om.readValue(
            restRapportExportMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rapportExportDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RapportExportDTO.class
        );

        // Validate the RapportExport in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRapportExport = rapportExportMapper.toEntity(returnedRapportExportDTO);
        assertRapportExportUpdatableFieldsEquals(returnedRapportExport, getPersistedRapportExport(returnedRapportExport));

        insertedRapportExport = returnedRapportExport;
    }

    @Test
    @Transactional
    void createRapportExportWithExistingId() throws Exception {
        // Create the RapportExport with an existing ID
        rapportExport.setId(1L);
        RapportExportDTO rapportExportDTO = rapportExportMapper.toDto(rapportExport);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRapportExportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rapportExportDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RapportExport in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rapportExport.setReference(null);

        // Create the RapportExport, which fails.
        RapportExportDTO rapportExportDTO = rapportExportMapper.toDto(rapportExport);

        restRapportExportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rapportExportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeRapportIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rapportExport.setTypeRapport(null);

        // Create the RapportExport, which fails.
        RapportExportDTO rapportExportDTO = rapportExportMapper.toDto(rapportExport);

        restRapportExportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rapportExportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFormatIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rapportExport.setFormat(null);

        // Create the RapportExport, which fails.
        RapportExportDTO rapportExportDTO = rapportExportMapper.toDto(rapportExport);

        restRapportExportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rapportExportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateGenerationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        rapportExport.setDateGeneration(null);

        // Create the RapportExport, which fails.
        RapportExportDTO rapportExportDTO = rapportExportMapper.toDto(rapportExport);

        restRapportExportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rapportExportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRapportExports() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList
        restRapportExportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rapportExport.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].typeRapport").value(hasItem(DEFAULT_TYPE_RAPPORT)))
            .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT.toString())))
            .andExpect(jsonPath("$.[*].periodeDebut").value(hasItem(DEFAULT_PERIODE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].periodeFin").value(hasItem(DEFAULT_PERIODE_FIN.toString())))
            .andExpect(jsonPath("$.[*].cheminFichier").value(hasItem(DEFAULT_CHEMIN_FICHIER)))
            .andExpect(jsonPath("$.[*].dateGeneration").value(hasItem(DEFAULT_DATE_GENERATION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRapportExportsWithEagerRelationshipsIsEnabled() throws Exception {
        when(rapportExportServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRapportExportMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(rapportExportServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRapportExportsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(rapportExportServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRapportExportMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(rapportExportRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRapportExport() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get the rapportExport
        restRapportExportMockMvc
            .perform(get(ENTITY_API_URL_ID, rapportExport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(rapportExport.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.typeRapport").value(DEFAULT_TYPE_RAPPORT))
            .andExpect(jsonPath("$.format").value(DEFAULT_FORMAT.toString()))
            .andExpect(jsonPath("$.periodeDebut").value(DEFAULT_PERIODE_DEBUT.toString()))
            .andExpect(jsonPath("$.periodeFin").value(DEFAULT_PERIODE_FIN.toString()))
            .andExpect(jsonPath("$.cheminFichier").value(DEFAULT_CHEMIN_FICHIER))
            .andExpect(jsonPath("$.dateGeneration").value(DEFAULT_DATE_GENERATION.toString()));
    }

    @Test
    @Transactional
    void getRapportExportsByIdFiltering() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        Long id = rapportExport.getId();

        defaultRapportExportFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRapportExportFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRapportExportFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRapportExportsByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where reference equals to
        defaultRapportExportFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRapportExportsByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where reference in
        defaultRapportExportFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRapportExportsByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where reference is not null
        defaultRapportExportFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllRapportExportsByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where reference contains
        defaultRapportExportFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRapportExportsByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where reference does not contain
        defaultRapportExportFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllRapportExportsByTypeRapportIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where typeRapport equals to
        defaultRapportExportFiltering("typeRapport.equals=" + DEFAULT_TYPE_RAPPORT, "typeRapport.equals=" + UPDATED_TYPE_RAPPORT);
    }

    @Test
    @Transactional
    void getAllRapportExportsByTypeRapportIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where typeRapport in
        defaultRapportExportFiltering(
            "typeRapport.in=" + DEFAULT_TYPE_RAPPORT + "," + UPDATED_TYPE_RAPPORT,
            "typeRapport.in=" + UPDATED_TYPE_RAPPORT
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByTypeRapportIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where typeRapport is not null
        defaultRapportExportFiltering("typeRapport.specified=true", "typeRapport.specified=false");
    }

    @Test
    @Transactional
    void getAllRapportExportsByTypeRapportContainsSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where typeRapport contains
        defaultRapportExportFiltering("typeRapport.contains=" + DEFAULT_TYPE_RAPPORT, "typeRapport.contains=" + UPDATED_TYPE_RAPPORT);
    }

    @Test
    @Transactional
    void getAllRapportExportsByTypeRapportNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where typeRapport does not contain
        defaultRapportExportFiltering(
            "typeRapport.doesNotContain=" + UPDATED_TYPE_RAPPORT,
            "typeRapport.doesNotContain=" + DEFAULT_TYPE_RAPPORT
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByFormatIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where format equals to
        defaultRapportExportFiltering("format.equals=" + DEFAULT_FORMAT, "format.equals=" + UPDATED_FORMAT);
    }

    @Test
    @Transactional
    void getAllRapportExportsByFormatIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where format in
        defaultRapportExportFiltering("format.in=" + DEFAULT_FORMAT + "," + UPDATED_FORMAT, "format.in=" + UPDATED_FORMAT);
    }

    @Test
    @Transactional
    void getAllRapportExportsByFormatIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where format is not null
        defaultRapportExportFiltering("format.specified=true", "format.specified=false");
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeDebutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeDebut equals to
        defaultRapportExportFiltering("periodeDebut.equals=" + DEFAULT_PERIODE_DEBUT, "periodeDebut.equals=" + UPDATED_PERIODE_DEBUT);
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeDebutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeDebut in
        defaultRapportExportFiltering(
            "periodeDebut.in=" + DEFAULT_PERIODE_DEBUT + "," + UPDATED_PERIODE_DEBUT,
            "periodeDebut.in=" + UPDATED_PERIODE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeDebutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeDebut is not null
        defaultRapportExportFiltering("periodeDebut.specified=true", "periodeDebut.specified=false");
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeDebutIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeDebut is greater than or equal to
        defaultRapportExportFiltering(
            "periodeDebut.greaterThanOrEqual=" + DEFAULT_PERIODE_DEBUT,
            "periodeDebut.greaterThanOrEqual=" + UPDATED_PERIODE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeDebutIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeDebut is less than or equal to
        defaultRapportExportFiltering(
            "periodeDebut.lessThanOrEqual=" + DEFAULT_PERIODE_DEBUT,
            "periodeDebut.lessThanOrEqual=" + SMALLER_PERIODE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeDebutIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeDebut is less than
        defaultRapportExportFiltering("periodeDebut.lessThan=" + UPDATED_PERIODE_DEBUT, "periodeDebut.lessThan=" + DEFAULT_PERIODE_DEBUT);
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeDebutIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeDebut is greater than
        defaultRapportExportFiltering(
            "periodeDebut.greaterThan=" + SMALLER_PERIODE_DEBUT,
            "periodeDebut.greaterThan=" + DEFAULT_PERIODE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeFinIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeFin equals to
        defaultRapportExportFiltering("periodeFin.equals=" + DEFAULT_PERIODE_FIN, "periodeFin.equals=" + UPDATED_PERIODE_FIN);
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeFinIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeFin in
        defaultRapportExportFiltering(
            "periodeFin.in=" + DEFAULT_PERIODE_FIN + "," + UPDATED_PERIODE_FIN,
            "periodeFin.in=" + UPDATED_PERIODE_FIN
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeFinIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeFin is not null
        defaultRapportExportFiltering("periodeFin.specified=true", "periodeFin.specified=false");
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeFinIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeFin is greater than or equal to
        defaultRapportExportFiltering(
            "periodeFin.greaterThanOrEqual=" + DEFAULT_PERIODE_FIN,
            "periodeFin.greaterThanOrEqual=" + UPDATED_PERIODE_FIN
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeFinIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeFin is less than or equal to
        defaultRapportExportFiltering(
            "periodeFin.lessThanOrEqual=" + DEFAULT_PERIODE_FIN,
            "periodeFin.lessThanOrEqual=" + SMALLER_PERIODE_FIN
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeFinIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeFin is less than
        defaultRapportExportFiltering("periodeFin.lessThan=" + UPDATED_PERIODE_FIN, "periodeFin.lessThan=" + DEFAULT_PERIODE_FIN);
    }

    @Test
    @Transactional
    void getAllRapportExportsByPeriodeFinIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where periodeFin is greater than
        defaultRapportExportFiltering("periodeFin.greaterThan=" + SMALLER_PERIODE_FIN, "periodeFin.greaterThan=" + DEFAULT_PERIODE_FIN);
    }

    @Test
    @Transactional
    void getAllRapportExportsByCheminFichierIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where cheminFichier equals to
        defaultRapportExportFiltering("cheminFichier.equals=" + DEFAULT_CHEMIN_FICHIER, "cheminFichier.equals=" + UPDATED_CHEMIN_FICHIER);
    }

    @Test
    @Transactional
    void getAllRapportExportsByCheminFichierIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where cheminFichier in
        defaultRapportExportFiltering(
            "cheminFichier.in=" + DEFAULT_CHEMIN_FICHIER + "," + UPDATED_CHEMIN_FICHIER,
            "cheminFichier.in=" + UPDATED_CHEMIN_FICHIER
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByCheminFichierIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where cheminFichier is not null
        defaultRapportExportFiltering("cheminFichier.specified=true", "cheminFichier.specified=false");
    }

    @Test
    @Transactional
    void getAllRapportExportsByCheminFichierContainsSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where cheminFichier contains
        defaultRapportExportFiltering(
            "cheminFichier.contains=" + DEFAULT_CHEMIN_FICHIER,
            "cheminFichier.contains=" + UPDATED_CHEMIN_FICHIER
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByCheminFichierNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where cheminFichier does not contain
        defaultRapportExportFiltering(
            "cheminFichier.doesNotContain=" + UPDATED_CHEMIN_FICHIER,
            "cheminFichier.doesNotContain=" + DEFAULT_CHEMIN_FICHIER
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByDateGenerationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where dateGeneration equals to
        defaultRapportExportFiltering(
            "dateGeneration.equals=" + DEFAULT_DATE_GENERATION,
            "dateGeneration.equals=" + UPDATED_DATE_GENERATION
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByDateGenerationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where dateGeneration in
        defaultRapportExportFiltering(
            "dateGeneration.in=" + DEFAULT_DATE_GENERATION + "," + UPDATED_DATE_GENERATION,
            "dateGeneration.in=" + UPDATED_DATE_GENERATION
        );
    }

    @Test
    @Transactional
    void getAllRapportExportsByDateGenerationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        // Get all the rapportExportList where dateGeneration is not null
        defaultRapportExportFiltering("dateGeneration.specified=true", "dateGeneration.specified=false");
    }

    @Test
    @Transactional
    void getAllRapportExportsByBoutiqueIsEqualToSomething() throws Exception {
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            rapportExportRepository.saveAndFlush(rapportExport);
            boutique = BoutiqueResourceIT.createEntity();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutique);
        em.flush();
        rapportExport.setBoutique(boutique);
        rapportExportRepository.saveAndFlush(rapportExport);
        Long boutiqueId = boutique.getId();
        // Get all the rapportExportList where boutique equals to boutiqueId
        defaultRapportExportShouldBeFound("boutiqueId.equals=" + boutiqueId);

        // Get all the rapportExportList where boutique equals to (boutiqueId + 1)
        defaultRapportExportShouldNotBeFound("boutiqueId.equals=" + (boutiqueId + 1));
    }

    @Test
    @Transactional
    void getAllRapportExportsByLocataireIsEqualToSomething() throws Exception {
        Locataire locataire;
        if (TestUtil.findAll(em, Locataire.class).isEmpty()) {
            rapportExportRepository.saveAndFlush(rapportExport);
            locataire = LocataireResourceIT.createEntity();
        } else {
            locataire = TestUtil.findAll(em, Locataire.class).get(0);
        }
        em.persist(locataire);
        em.flush();
        rapportExport.setLocataire(locataire);
        rapportExportRepository.saveAndFlush(rapportExport);
        Long locataireId = locataire.getId();
        // Get all the rapportExportList where locataire equals to locataireId
        defaultRapportExportShouldBeFound("locataireId.equals=" + locataireId);

        // Get all the rapportExportList where locataire equals to (locataireId + 1)
        defaultRapportExportShouldNotBeFound("locataireId.equals=" + (locataireId + 1));
    }

    @Test
    @Transactional
    void getAllRapportExportsByUtilisateurIsEqualToSomething() throws Exception {
        User utilisateur;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            rapportExportRepository.saveAndFlush(rapportExport);
            utilisateur = UserResourceIT.createEntity();
        } else {
            utilisateur = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(utilisateur);
        em.flush();
        rapportExport.setUtilisateur(utilisateur);
        rapportExportRepository.saveAndFlush(rapportExport);
        Long utilisateurId = utilisateur.getId();
        // Get all the rapportExportList where utilisateur equals to utilisateurId
        defaultRapportExportShouldBeFound("utilisateurId.equals=" + utilisateurId);

        // Get all the rapportExportList where utilisateur equals to (utilisateurId + 1)
        defaultRapportExportShouldNotBeFound("utilisateurId.equals=" + (utilisateurId + 1));
    }

    private void defaultRapportExportFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRapportExportShouldBeFound(shouldBeFound);
        defaultRapportExportShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRapportExportShouldBeFound(String filter) throws Exception {
        restRapportExportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rapportExport.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].typeRapport").value(hasItem(DEFAULT_TYPE_RAPPORT)))
            .andExpect(jsonPath("$.[*].format").value(hasItem(DEFAULT_FORMAT.toString())))
            .andExpect(jsonPath("$.[*].periodeDebut").value(hasItem(DEFAULT_PERIODE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].periodeFin").value(hasItem(DEFAULT_PERIODE_FIN.toString())))
            .andExpect(jsonPath("$.[*].cheminFichier").value(hasItem(DEFAULT_CHEMIN_FICHIER)))
            .andExpect(jsonPath("$.[*].dateGeneration").value(hasItem(DEFAULT_DATE_GENERATION.toString())));

        // Check, that the count call also returns 1
        restRapportExportMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRapportExportShouldNotBeFound(String filter) throws Exception {
        restRapportExportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRapportExportMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRapportExport() throws Exception {
        // Get the rapportExport
        restRapportExportMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRapportExport() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rapportExport
        RapportExport updatedRapportExport = rapportExportRepository.findById(rapportExport.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRapportExport are not directly saved in db
        em.detach(updatedRapportExport);
        updatedRapportExport
            .reference(UPDATED_REFERENCE)
            .typeRapport(UPDATED_TYPE_RAPPORT)
            .format(UPDATED_FORMAT)
            .periodeDebut(UPDATED_PERIODE_DEBUT)
            .periodeFin(UPDATED_PERIODE_FIN)
            .cheminFichier(UPDATED_CHEMIN_FICHIER)
            .dateGeneration(UPDATED_DATE_GENERATION);
        RapportExportDTO rapportExportDTO = rapportExportMapper.toDto(updatedRapportExport);

        restRapportExportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rapportExportDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rapportExportDTO))
            )
            .andExpect(status().isOk());

        // Validate the RapportExport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRapportExportToMatchAllProperties(updatedRapportExport);
    }

    @Test
    @Transactional
    void putNonExistingRapportExport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rapportExport.setId(longCount.incrementAndGet());

        // Create the RapportExport
        RapportExportDTO rapportExportDTO = rapportExportMapper.toDto(rapportExport);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRapportExportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, rapportExportDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rapportExportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RapportExport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRapportExport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rapportExport.setId(longCount.incrementAndGet());

        // Create the RapportExport
        RapportExportDTO rapportExportDTO = rapportExportMapper.toDto(rapportExport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRapportExportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(rapportExportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RapportExport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRapportExport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rapportExport.setId(longCount.incrementAndGet());

        // Create the RapportExport
        RapportExportDTO rapportExportDTO = rapportExportMapper.toDto(rapportExport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRapportExportMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(rapportExportDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RapportExport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRapportExportWithPatch() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rapportExport using partial update
        RapportExport partialUpdatedRapportExport = new RapportExport();
        partialUpdatedRapportExport.setId(rapportExport.getId());

        partialUpdatedRapportExport.reference(UPDATED_REFERENCE).format(UPDATED_FORMAT).dateGeneration(UPDATED_DATE_GENERATION);

        restRapportExportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRapportExport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRapportExport))
            )
            .andExpect(status().isOk());

        // Validate the RapportExport in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRapportExportUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRapportExport, rapportExport),
            getPersistedRapportExport(rapportExport)
        );
    }

    @Test
    @Transactional
    void fullUpdateRapportExportWithPatch() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the rapportExport using partial update
        RapportExport partialUpdatedRapportExport = new RapportExport();
        partialUpdatedRapportExport.setId(rapportExport.getId());

        partialUpdatedRapportExport
            .reference(UPDATED_REFERENCE)
            .typeRapport(UPDATED_TYPE_RAPPORT)
            .format(UPDATED_FORMAT)
            .periodeDebut(UPDATED_PERIODE_DEBUT)
            .periodeFin(UPDATED_PERIODE_FIN)
            .cheminFichier(UPDATED_CHEMIN_FICHIER)
            .dateGeneration(UPDATED_DATE_GENERATION);

        restRapportExportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRapportExport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRapportExport))
            )
            .andExpect(status().isOk());

        // Validate the RapportExport in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRapportExportUpdatableFieldsEquals(partialUpdatedRapportExport, getPersistedRapportExport(partialUpdatedRapportExport));
    }

    @Test
    @Transactional
    void patchNonExistingRapportExport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rapportExport.setId(longCount.incrementAndGet());

        // Create the RapportExport
        RapportExportDTO rapportExportDTO = rapportExportMapper.toDto(rapportExport);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRapportExportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, rapportExportDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(rapportExportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RapportExport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRapportExport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rapportExport.setId(longCount.incrementAndGet());

        // Create the RapportExport
        RapportExportDTO rapportExportDTO = rapportExportMapper.toDto(rapportExport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRapportExportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(rapportExportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RapportExport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRapportExport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        rapportExport.setId(longCount.incrementAndGet());

        // Create the RapportExport
        RapportExportDTO rapportExportDTO = rapportExportMapper.toDto(rapportExport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRapportExportMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(rapportExportDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RapportExport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRapportExport() throws Exception {
        // Initialize the database
        insertedRapportExport = rapportExportRepository.saveAndFlush(rapportExport);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the rapportExport
        restRapportExportMockMvc
            .perform(delete(ENTITY_API_URL_ID, rapportExport.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return rapportExportRepository.count();
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

    protected RapportExport getPersistedRapportExport(RapportExport rapportExport) {
        return rapportExportRepository.findById(rapportExport.getId()).orElseThrow();
    }

    protected void assertPersistedRapportExportToMatchAllProperties(RapportExport expectedRapportExport) {
        assertRapportExportAllPropertiesEquals(expectedRapportExport, getPersistedRapportExport(expectedRapportExport));
    }

    protected void assertPersistedRapportExportToMatchUpdatableProperties(RapportExport expectedRapportExport) {
        assertRapportExportAllUpdatablePropertiesEquals(expectedRapportExport, getPersistedRapportExport(expectedRapportExport));
    }
}
