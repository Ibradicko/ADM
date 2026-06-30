package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.TransfertStockAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.TransfertStock;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.enumeration.StatutMouvementStock;
import com.adm.supervision.repository.TransfertStockRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.service.TransfertStockService;
import com.adm.supervision.service.dto.TransfertStockDTO;
import com.adm.supervision.service.mapper.TransfertStockMapper;
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
 * Integration tests for the {@link TransfertStockResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TransfertStockResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_TRANSFERT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_TRANSFERT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final StatutMouvementStock DEFAULT_STATUT = StatutMouvementStock.BROUILLON;
    private static final StatutMouvementStock UPDATED_STATUT = StatutMouvementStock.VALIDE;

    private static final String DEFAULT_MOTIF = "AAAAAAAAAA";
    private static final String UPDATED_MOTIF = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transfert-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TransfertStockRepository transfertStockRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private TransfertStockRepository transfertStockRepositoryMock;

    @Autowired
    private TransfertStockMapper transfertStockMapper;

    @Mock
    private TransfertStockService transfertStockServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransfertStockMockMvc;

    private TransfertStock transfertStock;

    private TransfertStock insertedTransfertStock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransfertStock createEntity(EntityManager em) {
        TransfertStock transfertStock = new TransfertStock()
            .reference(DEFAULT_REFERENCE)
            .dateTransfert(DEFAULT_DATE_TRANSFERT)
            .statut(DEFAULT_STATUT)
            .motif(DEFAULT_MOTIF);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        transfertStock.setBoutiqueOrigine(boutique);
        // Add required entity
        transfertStock.setBoutiqueDestination(boutique);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        transfertStock.setUtilisateur(user);
        return transfertStock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransfertStock createUpdatedEntity(EntityManager em) {
        TransfertStock updatedTransfertStock = new TransfertStock()
            .reference(UPDATED_REFERENCE)
            .dateTransfert(UPDATED_DATE_TRANSFERT)
            .statut(UPDATED_STATUT)
            .motif(UPDATED_MOTIF);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createUpdatedEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        updatedTransfertStock.setBoutiqueOrigine(boutique);
        // Add required entity
        updatedTransfertStock.setBoutiqueDestination(boutique);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedTransfertStock.setUtilisateur(user);
        return updatedTransfertStock;
    }

    @BeforeEach
    void initTest() {
        transfertStock = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTransfertStock != null) {
            transfertStockRepository.delete(insertedTransfertStock);
            insertedTransfertStock = null;
        }
    }

    @Test
    @Transactional
    void createTransfertStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TransfertStock
        TransfertStockDTO transfertStockDTO = transfertStockMapper.toDto(transfertStock);
        var returnedTransfertStockDTO = om.readValue(
            restTransfertStockMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transfertStockDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TransfertStockDTO.class
        );

        // Validate the TransfertStock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTransfertStock = transfertStockMapper.toEntity(returnedTransfertStockDTO);
        assertTransfertStockUpdatableFieldsEquals(returnedTransfertStock, getPersistedTransfertStock(returnedTransfertStock));

        insertedTransfertStock = returnedTransfertStock;
    }

    @Test
    @Transactional
    void createTransfertStockWithExistingId() throws Exception {
        // Create the TransfertStock with an existing ID
        transfertStock.setId(1L);
        TransfertStockDTO transfertStockDTO = transfertStockMapper.toDto(transfertStock);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransfertStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transfertStockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transfertStock.setReference(null);

        // Create the TransfertStock, which fails.
        TransfertStockDTO transfertStockDTO = transfertStockMapper.toDto(transfertStock);

        restTransfertStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transfertStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateTransfertIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transfertStock.setDateTransfert(null);

        // Create the TransfertStock, which fails.
        TransfertStockDTO transfertStockDTO = transfertStockMapper.toDto(transfertStock);

        restTransfertStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transfertStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transfertStock.setStatut(null);

        // Create the TransfertStock, which fails.
        TransfertStockDTO transfertStockDTO = transfertStockMapper.toDto(transfertStock);

        restTransfertStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transfertStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransfertStocks() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList
        restTransfertStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transfertStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].dateTransfert").value(hasItem(DEFAULT_DATE_TRANSFERT.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransfertStocksWithEagerRelationshipsIsEnabled() throws Exception {
        when(transfertStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransfertStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(transfertStockServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTransfertStocksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(transfertStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTransfertStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(transfertStockRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTransfertStock() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get the transfertStock
        restTransfertStockMockMvc
            .perform(get(ENTITY_API_URL_ID, transfertStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transfertStock.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.dateTransfert").value(DEFAULT_DATE_TRANSFERT.toString()))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.motif").value(DEFAULT_MOTIF));
    }

    @Test
    @Transactional
    void getTransfertStocksByIdFiltering() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        Long id = transfertStock.getId();

        defaultTransfertStockFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTransfertStockFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTransfertStockFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransfertStocksByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where reference equals to
        defaultTransfertStockFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransfertStocksByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where reference in
        defaultTransfertStockFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransfertStocksByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where reference is not null
        defaultTransfertStockFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllTransfertStocksByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where reference contains
        defaultTransfertStockFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransfertStocksByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where reference does not contain
        defaultTransfertStockFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransfertStocksByDateTransfertIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where dateTransfert equals to
        defaultTransfertStockFiltering("dateTransfert.equals=" + DEFAULT_DATE_TRANSFERT, "dateTransfert.equals=" + UPDATED_DATE_TRANSFERT);
    }

    @Test
    @Transactional
    void getAllTransfertStocksByDateTransfertIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where dateTransfert in
        defaultTransfertStockFiltering(
            "dateTransfert.in=" + DEFAULT_DATE_TRANSFERT + "," + UPDATED_DATE_TRANSFERT,
            "dateTransfert.in=" + UPDATED_DATE_TRANSFERT
        );
    }

    @Test
    @Transactional
    void getAllTransfertStocksByDateTransfertIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where dateTransfert is not null
        defaultTransfertStockFiltering("dateTransfert.specified=true", "dateTransfert.specified=false");
    }

    @Test
    @Transactional
    void getAllTransfertStocksByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where statut equals to
        defaultTransfertStockFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllTransfertStocksByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where statut in
        defaultTransfertStockFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllTransfertStocksByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where statut is not null
        defaultTransfertStockFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllTransfertStocksByMotifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where motif equals to
        defaultTransfertStockFiltering("motif.equals=" + DEFAULT_MOTIF, "motif.equals=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllTransfertStocksByMotifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where motif in
        defaultTransfertStockFiltering("motif.in=" + DEFAULT_MOTIF + "," + UPDATED_MOTIF, "motif.in=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllTransfertStocksByMotifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where motif is not null
        defaultTransfertStockFiltering("motif.specified=true", "motif.specified=false");
    }

    @Test
    @Transactional
    void getAllTransfertStocksByMotifContainsSomething() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where motif contains
        defaultTransfertStockFiltering("motif.contains=" + DEFAULT_MOTIF, "motif.contains=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllTransfertStocksByMotifNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        // Get all the transfertStockList where motif does not contain
        defaultTransfertStockFiltering("motif.doesNotContain=" + UPDATED_MOTIF, "motif.doesNotContain=" + DEFAULT_MOTIF);
    }

    @Test
    @Transactional
    void getAllTransfertStocksByBoutiqueOrigineIsEqualToSomething() throws Exception {
        Boutique boutiqueOrigine;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            transfertStockRepository.saveAndFlush(transfertStock);
            boutiqueOrigine = BoutiqueResourceIT.createEntity();
        } else {
            boutiqueOrigine = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutiqueOrigine);
        em.flush();
        transfertStock.setBoutiqueOrigine(boutiqueOrigine);
        transfertStockRepository.saveAndFlush(transfertStock);
        Long boutiqueOrigineId = boutiqueOrigine.getId();
        // Get all the transfertStockList where boutiqueOrigine equals to boutiqueOrigineId
        defaultTransfertStockShouldBeFound("boutiqueOrigineId.equals=" + boutiqueOrigineId);

        // Get all the transfertStockList where boutiqueOrigine equals to (boutiqueOrigineId + 1)
        defaultTransfertStockShouldNotBeFound("boutiqueOrigineId.equals=" + (boutiqueOrigineId + 1));
    }

    @Test
    @Transactional
    void getAllTransfertStocksByBoutiqueDestinationIsEqualToSomething() throws Exception {
        Boutique boutiqueDestination;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            transfertStockRepository.saveAndFlush(transfertStock);
            boutiqueDestination = BoutiqueResourceIT.createEntity();
        } else {
            boutiqueDestination = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutiqueDestination);
        em.flush();
        transfertStock.setBoutiqueDestination(boutiqueDestination);
        transfertStockRepository.saveAndFlush(transfertStock);
        Long boutiqueDestinationId = boutiqueDestination.getId();
        // Get all the transfertStockList where boutiqueDestination equals to boutiqueDestinationId
        defaultTransfertStockShouldBeFound("boutiqueDestinationId.equals=" + boutiqueDestinationId);

        // Get all the transfertStockList where boutiqueDestination equals to (boutiqueDestinationId + 1)
        defaultTransfertStockShouldNotBeFound("boutiqueDestinationId.equals=" + (boutiqueDestinationId + 1));
    }

    @Test
    @Transactional
    void getAllTransfertStocksByUtilisateurIsEqualToSomething() throws Exception {
        User utilisateur;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            transfertStockRepository.saveAndFlush(transfertStock);
            utilisateur = UserResourceIT.createEntity();
        } else {
            utilisateur = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(utilisateur);
        em.flush();
        transfertStock.setUtilisateur(utilisateur);
        transfertStockRepository.saveAndFlush(transfertStock);
        Long utilisateurId = utilisateur.getId();
        // Get all the transfertStockList where utilisateur equals to utilisateurId
        defaultTransfertStockShouldBeFound("utilisateurId.equals=" + utilisateurId);

        // Get all the transfertStockList where utilisateur equals to (utilisateurId + 1)
        defaultTransfertStockShouldNotBeFound("utilisateurId.equals=" + (utilisateurId + 1));
    }

    private void defaultTransfertStockFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTransfertStockShouldBeFound(shouldBeFound);
        defaultTransfertStockShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransfertStockShouldBeFound(String filter) throws Exception {
        restTransfertStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transfertStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].dateTransfert").value(hasItem(DEFAULT_DATE_TRANSFERT.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)));

        // Check, that the count call also returns 1
        restTransfertStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransfertStockShouldNotBeFound(String filter) throws Exception {
        restTransfertStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransfertStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransfertStock() throws Exception {
        // Get the transfertStock
        restTransfertStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransfertStock() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transfertStock
        TransfertStock updatedTransfertStock = transfertStockRepository.findById(transfertStock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTransfertStock are not directly saved in db
        em.detach(updatedTransfertStock);
        updatedTransfertStock
            .reference(UPDATED_REFERENCE)
            .dateTransfert(UPDATED_DATE_TRANSFERT)
            .statut(UPDATED_STATUT)
            .motif(UPDATED_MOTIF);
        TransfertStockDTO transfertStockDTO = transfertStockMapper.toDto(updatedTransfertStock);

        restTransfertStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transfertStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transfertStockDTO))
            )
            .andExpect(status().isOk());

        // Validate the TransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTransfertStockToMatchAllProperties(updatedTransfertStock);
    }

    @Test
    @Transactional
    void putNonExistingTransfertStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transfertStock.setId(longCount.incrementAndGet());

        // Create the TransfertStock
        TransfertStockDTO transfertStockDTO = transfertStockMapper.toDto(transfertStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransfertStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transfertStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transfertStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransfertStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transfertStock.setId(longCount.incrementAndGet());

        // Create the TransfertStock
        TransfertStockDTO transfertStockDTO = transfertStockMapper.toDto(transfertStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransfertStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transfertStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransfertStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transfertStock.setId(longCount.incrementAndGet());

        // Create the TransfertStock
        TransfertStockDTO transfertStockDTO = transfertStockMapper.toDto(transfertStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransfertStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transfertStockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransfertStockWithPatch() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transfertStock using partial update
        TransfertStock partialUpdatedTransfertStock = new TransfertStock();
        partialUpdatedTransfertStock.setId(transfertStock.getId());

        partialUpdatedTransfertStock.statut(UPDATED_STATUT).motif(UPDATED_MOTIF);

        restTransfertStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransfertStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransfertStock))
            )
            .andExpect(status().isOk());

        // Validate the TransfertStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransfertStockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTransfertStock, transfertStock),
            getPersistedTransfertStock(transfertStock)
        );
    }

    @Test
    @Transactional
    void fullUpdateTransfertStockWithPatch() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transfertStock using partial update
        TransfertStock partialUpdatedTransfertStock = new TransfertStock();
        partialUpdatedTransfertStock.setId(transfertStock.getId());

        partialUpdatedTransfertStock
            .reference(UPDATED_REFERENCE)
            .dateTransfert(UPDATED_DATE_TRANSFERT)
            .statut(UPDATED_STATUT)
            .motif(UPDATED_MOTIF);

        restTransfertStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransfertStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransfertStock))
            )
            .andExpect(status().isOk());

        // Validate the TransfertStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransfertStockUpdatableFieldsEquals(partialUpdatedTransfertStock, getPersistedTransfertStock(partialUpdatedTransfertStock));
    }

    @Test
    @Transactional
    void patchNonExistingTransfertStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transfertStock.setId(longCount.incrementAndGet());

        // Create the TransfertStock
        TransfertStockDTO transfertStockDTO = transfertStockMapper.toDto(transfertStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransfertStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transfertStockDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transfertStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransfertStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transfertStock.setId(longCount.incrementAndGet());

        // Create the TransfertStock
        TransfertStockDTO transfertStockDTO = transfertStockMapper.toDto(transfertStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransfertStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transfertStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransfertStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transfertStock.setId(longCount.incrementAndGet());

        // Create the TransfertStock
        TransfertStockDTO transfertStockDTO = transfertStockMapper.toDto(transfertStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransfertStockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(transfertStockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransfertStock() throws Exception {
        // Initialize the database
        insertedTransfertStock = transfertStockRepository.saveAndFlush(transfertStock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the transfertStock
        restTransfertStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, transfertStock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return transfertStockRepository.count();
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

    protected TransfertStock getPersistedTransfertStock(TransfertStock transfertStock) {
        return transfertStockRepository.findById(transfertStock.getId()).orElseThrow();
    }

    protected void assertPersistedTransfertStockToMatchAllProperties(TransfertStock expectedTransfertStock) {
        assertTransfertStockAllPropertiesEquals(expectedTransfertStock, getPersistedTransfertStock(expectedTransfertStock));
    }

    protected void assertPersistedTransfertStockToMatchUpdatableProperties(TransfertStock expectedTransfertStock) {
        assertTransfertStockAllUpdatablePropertiesEquals(expectedTransfertStock, getPersistedTransfertStock(expectedTransfertStock));
    }
}
