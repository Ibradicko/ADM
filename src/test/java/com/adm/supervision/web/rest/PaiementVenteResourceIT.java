package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.PaiementVenteAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.ModePaiementRef;
import com.adm.supervision.domain.PaiementVente;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.StatutPaiement;
import com.adm.supervision.repository.PaiementVenteRepository;
import com.adm.supervision.service.PaiementVenteService;
import com.adm.supervision.service.dto.PaiementVenteDTO;
import com.adm.supervision.service.mapper.PaiementVenteMapper;
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
 * Integration tests for the {@link PaiementVenteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PaiementVenteResourceIT {

    private static final BigDecimal DEFAULT_MONTANT = new BigDecimal(0);
    private static final BigDecimal UPDATED_MONTANT = new BigDecimal(1);
    private static final BigDecimal SMALLER_MONTANT = new BigDecimal(0 - 1);

    private static final StatutPaiement DEFAULT_STATUT = StatutPaiement.EN_ATTENTE;
    private static final StatutPaiement UPDATED_STATUT = StatutPaiement.PARTIEL;

    private static final String DEFAULT_REFERENCE_PAIEMENT = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE_PAIEMENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_PAIEMENT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_PAIEMENT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/paiement-ventes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PaiementVenteRepository paiementVenteRepository;

    @Mock
    private PaiementVenteRepository paiementVenteRepositoryMock;

    @Autowired
    private PaiementVenteMapper paiementVenteMapper;

    @Mock
    private PaiementVenteService paiementVenteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaiementVenteMockMvc;

    private PaiementVente paiementVente;

    private PaiementVente insertedPaiementVente;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaiementVente createEntity(EntityManager em) {
        PaiementVente paiementVente = new PaiementVente()
            .montant(DEFAULT_MONTANT)
            .statut(DEFAULT_STATUT)
            .referencePaiement(DEFAULT_REFERENCE_PAIEMENT)
            .datePaiement(DEFAULT_DATE_PAIEMENT);
        // Add required entity
        Vente vente;
        if (TestUtil.findAll(em, Vente.class).isEmpty()) {
            vente = VenteResourceIT.createEntity(em);
            em.persist(vente);
            em.flush();
        } else {
            vente = TestUtil.findAll(em, Vente.class).get(0);
        }
        paiementVente.setVente(vente);
        // Add required entity
        ModePaiementRef modePaiementRef;
        if (TestUtil.findAll(em, ModePaiementRef.class).isEmpty()) {
            modePaiementRef = ModePaiementRefResourceIT.createEntity();
            em.persist(modePaiementRef);
            em.flush();
        } else {
            modePaiementRef = TestUtil.findAll(em, ModePaiementRef.class).get(0);
        }
        paiementVente.setModePaiement(modePaiementRef);
        return paiementVente;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PaiementVente createUpdatedEntity(EntityManager em) {
        PaiementVente updatedPaiementVente = new PaiementVente()
            .montant(UPDATED_MONTANT)
            .statut(UPDATED_STATUT)
            .referencePaiement(UPDATED_REFERENCE_PAIEMENT)
            .datePaiement(UPDATED_DATE_PAIEMENT);
        // Add required entity
        Vente vente;
        if (TestUtil.findAll(em, Vente.class).isEmpty()) {
            vente = VenteResourceIT.createUpdatedEntity(em);
            em.persist(vente);
            em.flush();
        } else {
            vente = TestUtil.findAll(em, Vente.class).get(0);
        }
        updatedPaiementVente.setVente(vente);
        // Add required entity
        ModePaiementRef modePaiementRef;
        if (TestUtil.findAll(em, ModePaiementRef.class).isEmpty()) {
            modePaiementRef = ModePaiementRefResourceIT.createUpdatedEntity();
            em.persist(modePaiementRef);
            em.flush();
        } else {
            modePaiementRef = TestUtil.findAll(em, ModePaiementRef.class).get(0);
        }
        updatedPaiementVente.setModePaiement(modePaiementRef);
        return updatedPaiementVente;
    }

    @BeforeEach
    void initTest() {
        paiementVente = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPaiementVente != null) {
            paiementVenteRepository.delete(insertedPaiementVente);
            insertedPaiementVente = null;
        }
    }

    @Test
    @Transactional
    void createPaiementVente() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PaiementVente
        PaiementVenteDTO paiementVenteDTO = paiementVenteMapper.toDto(paiementVente);
        var returnedPaiementVenteDTO = om.readValue(
            restPaiementVenteMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementVenteDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PaiementVenteDTO.class
        );

        // Validate the PaiementVente in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPaiementVente = paiementVenteMapper.toEntity(returnedPaiementVenteDTO);
        assertPaiementVenteUpdatableFieldsEquals(returnedPaiementVente, getPersistedPaiementVente(returnedPaiementVente));

        insertedPaiementVente = returnedPaiementVente;
    }

    @Test
    @Transactional
    void createPaiementVenteWithExistingId() throws Exception {
        // Create the PaiementVente with an existing ID
        paiementVente.setId(1L);
        PaiementVenteDTO paiementVenteDTO = paiementVenteMapper.toDto(paiementVente);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaiementVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementVenteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PaiementVente in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMontantIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paiementVente.setMontant(null);

        // Create the PaiementVente, which fails.
        PaiementVenteDTO paiementVenteDTO = paiementVenteMapper.toDto(paiementVente);

        restPaiementVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementVenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paiementVente.setStatut(null);

        // Create the PaiementVente, which fails.
        PaiementVenteDTO paiementVenteDTO = paiementVenteMapper.toDto(paiementVente);

        restPaiementVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementVenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDatePaiementIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        paiementVente.setDatePaiement(null);

        // Create the PaiementVente, which fails.
        PaiementVenteDTO paiementVenteDTO = paiementVenteMapper.toDto(paiementVente);

        restPaiementVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementVenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPaiementVentes() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList
        restPaiementVenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paiementVente.getId().intValue())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(sameNumber(DEFAULT_MONTANT))))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].referencePaiement").value(hasItem(DEFAULT_REFERENCE_PAIEMENT)))
            .andExpect(jsonPath("$.[*].datePaiement").value(hasItem(DEFAULT_DATE_PAIEMENT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPaiementVentesWithEagerRelationshipsIsEnabled() throws Exception {
        when(paiementVenteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPaiementVenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(paiementVenteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPaiementVentesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(paiementVenteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPaiementVenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(paiementVenteRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPaiementVente() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get the paiementVente
        restPaiementVenteMockMvc
            .perform(get(ENTITY_API_URL_ID, paiementVente.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paiementVente.getId().intValue()))
            .andExpect(jsonPath("$.montant").value(sameNumber(DEFAULT_MONTANT)))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.referencePaiement").value(DEFAULT_REFERENCE_PAIEMENT))
            .andExpect(jsonPath("$.datePaiement").value(DEFAULT_DATE_PAIEMENT.toString()));
    }

    @Test
    @Transactional
    void getPaiementVentesByIdFiltering() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        Long id = paiementVente.getId();

        defaultPaiementVenteFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPaiementVenteFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPaiementVenteFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPaiementVentesByMontantIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where montant equals to
        defaultPaiementVenteFiltering("montant.equals=" + DEFAULT_MONTANT, "montant.equals=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllPaiementVentesByMontantIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where montant in
        defaultPaiementVenteFiltering("montant.in=" + DEFAULT_MONTANT + "," + UPDATED_MONTANT, "montant.in=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllPaiementVentesByMontantIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where montant is not null
        defaultPaiementVenteFiltering("montant.specified=true", "montant.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementVentesByMontantIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where montant is greater than or equal to
        defaultPaiementVenteFiltering("montant.greaterThanOrEqual=" + DEFAULT_MONTANT, "montant.greaterThanOrEqual=" + UPDATED_MONTANT);
    }

    @Test
    @Transactional
    void getAllPaiementVentesByMontantIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where montant is less than or equal to
        defaultPaiementVenteFiltering("montant.lessThanOrEqual=" + DEFAULT_MONTANT, "montant.lessThanOrEqual=" + SMALLER_MONTANT);
    }

    @Test
    @Transactional
    void getAllPaiementVentesByMontantIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where montant is less than
        defaultPaiementVenteFiltering("montant.lessThan=" + UPDATED_MONTANT, "montant.lessThan=" + DEFAULT_MONTANT);
    }

    @Test
    @Transactional
    void getAllPaiementVentesByMontantIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where montant is greater than
        defaultPaiementVenteFiltering("montant.greaterThan=" + SMALLER_MONTANT, "montant.greaterThan=" + DEFAULT_MONTANT);
    }

    @Test
    @Transactional
    void getAllPaiementVentesByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where statut equals to
        defaultPaiementVenteFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllPaiementVentesByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where statut in
        defaultPaiementVenteFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllPaiementVentesByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where statut is not null
        defaultPaiementVenteFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementVentesByReferencePaiementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where referencePaiement equals to
        defaultPaiementVenteFiltering(
            "referencePaiement.equals=" + DEFAULT_REFERENCE_PAIEMENT,
            "referencePaiement.equals=" + UPDATED_REFERENCE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementVentesByReferencePaiementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where referencePaiement in
        defaultPaiementVenteFiltering(
            "referencePaiement.in=" + DEFAULT_REFERENCE_PAIEMENT + "," + UPDATED_REFERENCE_PAIEMENT,
            "referencePaiement.in=" + UPDATED_REFERENCE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementVentesByReferencePaiementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where referencePaiement is not null
        defaultPaiementVenteFiltering("referencePaiement.specified=true", "referencePaiement.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementVentesByReferencePaiementContainsSomething() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where referencePaiement contains
        defaultPaiementVenteFiltering(
            "referencePaiement.contains=" + DEFAULT_REFERENCE_PAIEMENT,
            "referencePaiement.contains=" + UPDATED_REFERENCE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementVentesByReferencePaiementNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where referencePaiement does not contain
        defaultPaiementVenteFiltering(
            "referencePaiement.doesNotContain=" + UPDATED_REFERENCE_PAIEMENT,
            "referencePaiement.doesNotContain=" + DEFAULT_REFERENCE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementVentesByDatePaiementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where datePaiement equals to
        defaultPaiementVenteFiltering("datePaiement.equals=" + DEFAULT_DATE_PAIEMENT, "datePaiement.equals=" + UPDATED_DATE_PAIEMENT);
    }

    @Test
    @Transactional
    void getAllPaiementVentesByDatePaiementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where datePaiement in
        defaultPaiementVenteFiltering(
            "datePaiement.in=" + DEFAULT_DATE_PAIEMENT + "," + UPDATED_DATE_PAIEMENT,
            "datePaiement.in=" + UPDATED_DATE_PAIEMENT
        );
    }

    @Test
    @Transactional
    void getAllPaiementVentesByDatePaiementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        // Get all the paiementVenteList where datePaiement is not null
        defaultPaiementVenteFiltering("datePaiement.specified=true", "datePaiement.specified=false");
    }

    @Test
    @Transactional
    void getAllPaiementVentesByVenteIsEqualToSomething() throws Exception {
        Vente vente;
        if (TestUtil.findAll(em, Vente.class).isEmpty()) {
            paiementVenteRepository.saveAndFlush(paiementVente);
            vente = VenteResourceIT.createEntity(em);
        } else {
            vente = TestUtil.findAll(em, Vente.class).get(0);
        }
        em.persist(vente);
        em.flush();
        paiementVente.setVente(vente);
        paiementVenteRepository.saveAndFlush(paiementVente);
        Long venteId = vente.getId();
        // Get all the paiementVenteList where vente equals to venteId
        defaultPaiementVenteShouldBeFound("venteId.equals=" + venteId);

        // Get all the paiementVenteList where vente equals to (venteId + 1)
        defaultPaiementVenteShouldNotBeFound("venteId.equals=" + (venteId + 1));
    }

    @Test
    @Transactional
    void getAllPaiementVentesByModePaiementIsEqualToSomething() throws Exception {
        ModePaiementRef modePaiement;
        if (TestUtil.findAll(em, ModePaiementRef.class).isEmpty()) {
            paiementVenteRepository.saveAndFlush(paiementVente);
            modePaiement = ModePaiementRefResourceIT.createEntity();
        } else {
            modePaiement = TestUtil.findAll(em, ModePaiementRef.class).get(0);
        }
        em.persist(modePaiement);
        em.flush();
        paiementVente.setModePaiement(modePaiement);
        paiementVenteRepository.saveAndFlush(paiementVente);
        Long modePaiementId = modePaiement.getId();
        // Get all the paiementVenteList where modePaiement equals to modePaiementId
        defaultPaiementVenteShouldBeFound("modePaiementId.equals=" + modePaiementId);

        // Get all the paiementVenteList where modePaiement equals to (modePaiementId + 1)
        defaultPaiementVenteShouldNotBeFound("modePaiementId.equals=" + (modePaiementId + 1));
    }

    private void defaultPaiementVenteFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPaiementVenteShouldBeFound(shouldBeFound);
        defaultPaiementVenteShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPaiementVenteShouldBeFound(String filter) throws Exception {
        restPaiementVenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paiementVente.getId().intValue())))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(sameNumber(DEFAULT_MONTANT))))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].referencePaiement").value(hasItem(DEFAULT_REFERENCE_PAIEMENT)))
            .andExpect(jsonPath("$.[*].datePaiement").value(hasItem(DEFAULT_DATE_PAIEMENT.toString())));

        // Check, that the count call also returns 1
        restPaiementVenteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPaiementVenteShouldNotBeFound(String filter) throws Exception {
        restPaiementVenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPaiementVenteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPaiementVente() throws Exception {
        // Get the paiementVente
        restPaiementVenteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPaiementVente() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paiementVente
        PaiementVente updatedPaiementVente = paiementVenteRepository.findById(paiementVente.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPaiementVente are not directly saved in db
        em.detach(updatedPaiementVente);
        updatedPaiementVente
            .montant(UPDATED_MONTANT)
            .statut(UPDATED_STATUT)
            .referencePaiement(UPDATED_REFERENCE_PAIEMENT)
            .datePaiement(UPDATED_DATE_PAIEMENT);
        PaiementVenteDTO paiementVenteDTO = paiementVenteMapper.toDto(updatedPaiementVente);

        restPaiementVenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paiementVenteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paiementVenteDTO))
            )
            .andExpect(status().isOk());

        // Validate the PaiementVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPaiementVenteToMatchAllProperties(updatedPaiementVente);
    }

    @Test
    @Transactional
    void putNonExistingPaiementVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiementVente.setId(longCount.incrementAndGet());

        // Create the PaiementVente
        PaiementVenteDTO paiementVenteDTO = paiementVenteMapper.toDto(paiementVente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaiementVenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paiementVenteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paiementVenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaiementVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPaiementVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiementVente.setId(longCount.incrementAndGet());

        // Create the PaiementVente
        PaiementVenteDTO paiementVenteDTO = paiementVenteMapper.toDto(paiementVente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementVenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paiementVenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaiementVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPaiementVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiementVente.setId(longCount.incrementAndGet());

        // Create the PaiementVente
        PaiementVenteDTO paiementVenteDTO = paiementVenteMapper.toDto(paiementVente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementVenteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paiementVenteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaiementVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePaiementVenteWithPatch() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paiementVente using partial update
        PaiementVente partialUpdatedPaiementVente = new PaiementVente();
        partialUpdatedPaiementVente.setId(paiementVente.getId());

        restPaiementVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaiementVente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaiementVente))
            )
            .andExpect(status().isOk());

        // Validate the PaiementVente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaiementVenteUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPaiementVente, paiementVente),
            getPersistedPaiementVente(paiementVente)
        );
    }

    @Test
    @Transactional
    void fullUpdatePaiementVenteWithPatch() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paiementVente using partial update
        PaiementVente partialUpdatedPaiementVente = new PaiementVente();
        partialUpdatedPaiementVente.setId(paiementVente.getId());

        partialUpdatedPaiementVente
            .montant(UPDATED_MONTANT)
            .statut(UPDATED_STATUT)
            .referencePaiement(UPDATED_REFERENCE_PAIEMENT)
            .datePaiement(UPDATED_DATE_PAIEMENT);

        restPaiementVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPaiementVente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPaiementVente))
            )
            .andExpect(status().isOk());

        // Validate the PaiementVente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPaiementVenteUpdatableFieldsEquals(partialUpdatedPaiementVente, getPersistedPaiementVente(partialUpdatedPaiementVente));
    }

    @Test
    @Transactional
    void patchNonExistingPaiementVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiementVente.setId(longCount.incrementAndGet());

        // Create the PaiementVente
        PaiementVenteDTO paiementVenteDTO = paiementVenteMapper.toDto(paiementVente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaiementVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paiementVenteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paiementVenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaiementVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPaiementVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiementVente.setId(longCount.incrementAndGet());

        // Create the PaiementVente
        PaiementVenteDTO paiementVenteDTO = paiementVenteMapper.toDto(paiementVente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paiementVenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PaiementVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPaiementVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        paiementVente.setId(longCount.incrementAndGet());

        // Create the PaiementVente
        PaiementVenteDTO paiementVenteDTO = paiementVenteMapper.toDto(paiementVente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaiementVenteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(paiementVenteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PaiementVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePaiementVente() throws Exception {
        // Initialize the database
        insertedPaiementVente = paiementVenteRepository.saveAndFlush(paiementVente);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the paiementVente
        restPaiementVenteMockMvc
            .perform(delete(ENTITY_API_URL_ID, paiementVente.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return paiementVenteRepository.count();
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

    protected PaiementVente getPersistedPaiementVente(PaiementVente paiementVente) {
        return paiementVenteRepository.findById(paiementVente.getId()).orElseThrow();
    }

    protected void assertPersistedPaiementVenteToMatchAllProperties(PaiementVente expectedPaiementVente) {
        assertPaiementVenteAllPropertiesEquals(expectedPaiementVente, getPersistedPaiementVente(expectedPaiementVente));
    }

    protected void assertPersistedPaiementVenteToMatchUpdatableProperties(PaiementVente expectedPaiementVente) {
        assertPaiementVenteAllUpdatablePropertiesEquals(expectedPaiementVente, getPersistedPaiementVente(expectedPaiementVente));
    }
}
