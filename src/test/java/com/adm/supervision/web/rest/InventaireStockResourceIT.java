package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.InventaireStockAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.DepotStock;
import com.adm.supervision.domain.InventaireStock;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.enumeration.StatutInventaire;
import com.adm.supervision.domain.enumeration.TypeInventaire;
import com.adm.supervision.repository.InventaireStockRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.service.InventaireStockService;
import com.adm.supervision.service.dto.InventaireStockDTO;
import com.adm.supervision.service.mapper.InventaireStockMapper;
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
 * Integration tests for the {@link InventaireStockResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InventaireStockResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final TypeInventaire DEFAULT_TYPE_INVENTAIRE = TypeInventaire.TOURNANT;
    private static final TypeInventaire UPDATED_TYPE_INVENTAIRE = TypeInventaire.COMPLET;

    private static final StatutInventaire DEFAULT_STATUT = StatutInventaire.PLANIFIE;
    private static final StatutInventaire UPDATED_STATUT = StatutInventaire.EN_COURS;

    private static final Instant DEFAULT_DATE_DEBUT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_DEBUT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DATE_FIN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_FIN = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/inventaire-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InventaireStockRepository inventaireStockRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private InventaireStockRepository inventaireStockRepositoryMock;

    @Autowired
    private InventaireStockMapper inventaireStockMapper;

    @Mock
    private InventaireStockService inventaireStockServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInventaireStockMockMvc;

    private InventaireStock inventaireStock;

    private InventaireStock insertedInventaireStock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventaireStock createEntity(EntityManager em) {
        InventaireStock inventaireStock = new InventaireStock()
            .reference(DEFAULT_REFERENCE)
            .typeInventaire(DEFAULT_TYPE_INVENTAIRE)
            .statut(DEFAULT_STATUT)
            .dateDebut(DEFAULT_DATE_DEBUT)
            .dateFin(DEFAULT_DATE_FIN);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        inventaireStock.setBoutique(boutique);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        inventaireStock.setUtilisateur(user);
        return inventaireStock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventaireStock createUpdatedEntity(EntityManager em) {
        InventaireStock updatedInventaireStock = new InventaireStock()
            .reference(UPDATED_REFERENCE)
            .typeInventaire(UPDATED_TYPE_INVENTAIRE)
            .statut(UPDATED_STATUT)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createUpdatedEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        updatedInventaireStock.setBoutique(boutique);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedInventaireStock.setUtilisateur(user);
        return updatedInventaireStock;
    }

    @BeforeEach
    void initTest() {
        inventaireStock = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedInventaireStock != null) {
            inventaireStockRepository.delete(insertedInventaireStock);
            insertedInventaireStock = null;
        }
    }

    @Test
    @Transactional
    void createInventaireStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InventaireStock
        InventaireStockDTO inventaireStockDTO = inventaireStockMapper.toDto(inventaireStock);
        var returnedInventaireStockDTO = om.readValue(
            restInventaireStockMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventaireStockDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InventaireStockDTO.class
        );

        // Validate the InventaireStock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInventaireStock = inventaireStockMapper.toEntity(returnedInventaireStockDTO);
        assertInventaireStockUpdatableFieldsEquals(returnedInventaireStock, getPersistedInventaireStock(returnedInventaireStock));

        insertedInventaireStock = returnedInventaireStock;
    }

    @Test
    @Transactional
    void createInventaireStockWithExistingId() throws Exception {
        // Create the InventaireStock with an existing ID
        inventaireStock.setId(1L);
        InventaireStockDTO inventaireStockDTO = inventaireStockMapper.toDto(inventaireStock);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInventaireStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventaireStockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the InventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventaireStock.setReference(null);

        // Create the InventaireStock, which fails.
        InventaireStockDTO inventaireStockDTO = inventaireStockMapper.toDto(inventaireStock);

        restInventaireStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventaireStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeInventaireIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventaireStock.setTypeInventaire(null);

        // Create the InventaireStock, which fails.
        InventaireStockDTO inventaireStockDTO = inventaireStockMapper.toDto(inventaireStock);

        restInventaireStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventaireStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventaireStock.setStatut(null);

        // Create the InventaireStock, which fails.
        InventaireStockDTO inventaireStockDTO = inventaireStockMapper.toDto(inventaireStock);

        restInventaireStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventaireStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateDebutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventaireStock.setDateDebut(null);

        // Create the InventaireStock, which fails.
        InventaireStockDTO inventaireStockDTO = inventaireStockMapper.toDto(inventaireStock);

        restInventaireStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventaireStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInventaireStocks() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList
        restInventaireStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventaireStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].typeInventaire").value(hasItem(DEFAULT_TYPE_INVENTAIRE.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInventaireStocksWithEagerRelationshipsIsEnabled() throws Exception {
        when(inventaireStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventaireStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(inventaireStockServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInventaireStocksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(inventaireStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventaireStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(inventaireStockRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInventaireStock() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get the inventaireStock
        restInventaireStockMockMvc
            .perform(get(ENTITY_API_URL_ID, inventaireStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventaireStock.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.typeInventaire").value(DEFAULT_TYPE_INVENTAIRE.toString()))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.dateDebut").value(DEFAULT_DATE_DEBUT.toString()))
            .andExpect(jsonPath("$.dateFin").value(DEFAULT_DATE_FIN.toString()));
    }

    @Test
    @Transactional
    void getInventaireStocksByIdFiltering() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        Long id = inventaireStock.getId();

        defaultInventaireStockFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultInventaireStockFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultInventaireStockFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInventaireStocksByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where reference equals to
        defaultInventaireStockFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllInventaireStocksByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where reference in
        defaultInventaireStockFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllInventaireStocksByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where reference is not null
        defaultInventaireStockFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllInventaireStocksByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where reference contains
        defaultInventaireStockFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllInventaireStocksByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where reference does not contain
        defaultInventaireStockFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllInventaireStocksByTypeInventaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where typeInventaire equals to
        defaultInventaireStockFiltering(
            "typeInventaire.equals=" + DEFAULT_TYPE_INVENTAIRE,
            "typeInventaire.equals=" + UPDATED_TYPE_INVENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllInventaireStocksByTypeInventaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where typeInventaire in
        defaultInventaireStockFiltering(
            "typeInventaire.in=" + DEFAULT_TYPE_INVENTAIRE + "," + UPDATED_TYPE_INVENTAIRE,
            "typeInventaire.in=" + UPDATED_TYPE_INVENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllInventaireStocksByTypeInventaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where typeInventaire is not null
        defaultInventaireStockFiltering("typeInventaire.specified=true", "typeInventaire.specified=false");
    }

    @Test
    @Transactional
    void getAllInventaireStocksByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where statut equals to
        defaultInventaireStockFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllInventaireStocksByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where statut in
        defaultInventaireStockFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllInventaireStocksByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where statut is not null
        defaultInventaireStockFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllInventaireStocksByDateDebutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where dateDebut equals to
        defaultInventaireStockFiltering("dateDebut.equals=" + DEFAULT_DATE_DEBUT, "dateDebut.equals=" + UPDATED_DATE_DEBUT);
    }

    @Test
    @Transactional
    void getAllInventaireStocksByDateDebutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where dateDebut in
        defaultInventaireStockFiltering(
            "dateDebut.in=" + DEFAULT_DATE_DEBUT + "," + UPDATED_DATE_DEBUT,
            "dateDebut.in=" + UPDATED_DATE_DEBUT
        );
    }

    @Test
    @Transactional
    void getAllInventaireStocksByDateDebutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where dateDebut is not null
        defaultInventaireStockFiltering("dateDebut.specified=true", "dateDebut.specified=false");
    }

    @Test
    @Transactional
    void getAllInventaireStocksByDateFinIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where dateFin equals to
        defaultInventaireStockFiltering("dateFin.equals=" + DEFAULT_DATE_FIN, "dateFin.equals=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllInventaireStocksByDateFinIsInShouldWork() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where dateFin in
        defaultInventaireStockFiltering("dateFin.in=" + DEFAULT_DATE_FIN + "," + UPDATED_DATE_FIN, "dateFin.in=" + UPDATED_DATE_FIN);
    }

    @Test
    @Transactional
    void getAllInventaireStocksByDateFinIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        // Get all the inventaireStockList where dateFin is not null
        defaultInventaireStockFiltering("dateFin.specified=true", "dateFin.specified=false");
    }

    @Test
    @Transactional
    void getAllInventaireStocksByBoutiqueIsEqualToSomething() throws Exception {
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            inventaireStockRepository.saveAndFlush(inventaireStock);
            boutique = BoutiqueResourceIT.createEntity();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutique);
        em.flush();
        inventaireStock.setBoutique(boutique);
        inventaireStockRepository.saveAndFlush(inventaireStock);
        Long boutiqueId = boutique.getId();
        // Get all the inventaireStockList where boutique equals to boutiqueId
        defaultInventaireStockShouldBeFound("boutiqueId.equals=" + boutiqueId);

        // Get all the inventaireStockList where boutique equals to (boutiqueId + 1)
        defaultInventaireStockShouldNotBeFound("boutiqueId.equals=" + (boutiqueId + 1));
    }

    @Test
    @Transactional
    void getAllInventaireStocksByDepotIsEqualToSomething() throws Exception {
        DepotStock depot;
        if (TestUtil.findAll(em, DepotStock.class).isEmpty()) {
            inventaireStockRepository.saveAndFlush(inventaireStock);
            depot = DepotStockResourceIT.createEntity(em);
        } else {
            depot = TestUtil.findAll(em, DepotStock.class).get(0);
        }
        em.persist(depot);
        em.flush();
        inventaireStock.setDepot(depot);
        inventaireStockRepository.saveAndFlush(inventaireStock);
        Long depotId = depot.getId();
        // Get all the inventaireStockList where depot equals to depotId
        defaultInventaireStockShouldBeFound("depotId.equals=" + depotId);

        // Get all the inventaireStockList where depot equals to (depotId + 1)
        defaultInventaireStockShouldNotBeFound("depotId.equals=" + (depotId + 1));
    }

    @Test
    @Transactional
    void getAllInventaireStocksByUtilisateurIsEqualToSomething() throws Exception {
        User utilisateur;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            inventaireStockRepository.saveAndFlush(inventaireStock);
            utilisateur = UserResourceIT.createEntity();
        } else {
            utilisateur = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(utilisateur);
        em.flush();
        inventaireStock.setUtilisateur(utilisateur);
        inventaireStockRepository.saveAndFlush(inventaireStock);
        Long utilisateurId = utilisateur.getId();
        // Get all the inventaireStockList where utilisateur equals to utilisateurId
        defaultInventaireStockShouldBeFound("utilisateurId.equals=" + utilisateurId);

        // Get all the inventaireStockList where utilisateur equals to (utilisateurId + 1)
        defaultInventaireStockShouldNotBeFound("utilisateurId.equals=" + (utilisateurId + 1));
    }

    private void defaultInventaireStockFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultInventaireStockShouldBeFound(shouldBeFound);
        defaultInventaireStockShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInventaireStockShouldBeFound(String filter) throws Exception {
        restInventaireStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventaireStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].typeInventaire").value(hasItem(DEFAULT_TYPE_INVENTAIRE.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateDebut").value(hasItem(DEFAULT_DATE_DEBUT.toString())))
            .andExpect(jsonPath("$.[*].dateFin").value(hasItem(DEFAULT_DATE_FIN.toString())));

        // Check, that the count call also returns 1
        restInventaireStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInventaireStockShouldNotBeFound(String filter) throws Exception {
        restInventaireStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInventaireStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInventaireStock() throws Exception {
        // Get the inventaireStock
        restInventaireStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInventaireStock() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventaireStock
        InventaireStock updatedInventaireStock = inventaireStockRepository.findById(inventaireStock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInventaireStock are not directly saved in db
        em.detach(updatedInventaireStock);
        updatedInventaireStock
            .reference(UPDATED_REFERENCE)
            .typeInventaire(UPDATED_TYPE_INVENTAIRE)
            .statut(UPDATED_STATUT)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN);
        InventaireStockDTO inventaireStockDTO = inventaireStockMapper.toDto(updatedInventaireStock);

        restInventaireStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventaireStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventaireStockDTO))
            )
            .andExpect(status().isOk());

        // Validate the InventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInventaireStockToMatchAllProperties(updatedInventaireStock);
    }

    @Test
    @Transactional
    void putNonExistingInventaireStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventaireStock.setId(longCount.incrementAndGet());

        // Create the InventaireStock
        InventaireStockDTO inventaireStockDTO = inventaireStockMapper.toDto(inventaireStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventaireStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventaireStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventaireStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInventaireStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventaireStock.setId(longCount.incrementAndGet());

        // Create the InventaireStock
        InventaireStockDTO inventaireStockDTO = inventaireStockMapper.toDto(inventaireStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventaireStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventaireStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInventaireStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventaireStock.setId(longCount.incrementAndGet());

        // Create the InventaireStock
        InventaireStockDTO inventaireStockDTO = inventaireStockMapper.toDto(inventaireStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventaireStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventaireStockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInventaireStockWithPatch() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventaireStock using partial update
        InventaireStock partialUpdatedInventaireStock = new InventaireStock();
        partialUpdatedInventaireStock.setId(inventaireStock.getId());

        partialUpdatedInventaireStock.reference(UPDATED_REFERENCE).typeInventaire(UPDATED_TYPE_INVENTAIRE);

        restInventaireStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventaireStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventaireStock))
            )
            .andExpect(status().isOk());

        // Validate the InventaireStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventaireStockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInventaireStock, inventaireStock),
            getPersistedInventaireStock(inventaireStock)
        );
    }

    @Test
    @Transactional
    void fullUpdateInventaireStockWithPatch() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventaireStock using partial update
        InventaireStock partialUpdatedInventaireStock = new InventaireStock();
        partialUpdatedInventaireStock.setId(inventaireStock.getId());

        partialUpdatedInventaireStock
            .reference(UPDATED_REFERENCE)
            .typeInventaire(UPDATED_TYPE_INVENTAIRE)
            .statut(UPDATED_STATUT)
            .dateDebut(UPDATED_DATE_DEBUT)
            .dateFin(UPDATED_DATE_FIN);

        restInventaireStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventaireStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventaireStock))
            )
            .andExpect(status().isOk());

        // Validate the InventaireStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventaireStockUpdatableFieldsEquals(
            partialUpdatedInventaireStock,
            getPersistedInventaireStock(partialUpdatedInventaireStock)
        );
    }

    @Test
    @Transactional
    void patchNonExistingInventaireStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventaireStock.setId(longCount.incrementAndGet());

        // Create the InventaireStock
        InventaireStockDTO inventaireStockDTO = inventaireStockMapper.toDto(inventaireStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventaireStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inventaireStockDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventaireStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInventaireStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventaireStock.setId(longCount.incrementAndGet());

        // Create the InventaireStock
        InventaireStockDTO inventaireStockDTO = inventaireStockMapper.toDto(inventaireStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventaireStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventaireStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInventaireStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventaireStock.setId(longCount.incrementAndGet());

        // Create the InventaireStock
        InventaireStockDTO inventaireStockDTO = inventaireStockMapper.toDto(inventaireStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventaireStockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(inventaireStockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInventaireStock() throws Exception {
        // Initialize the database
        insertedInventaireStock = inventaireStockRepository.saveAndFlush(inventaireStock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inventaireStock
        restInventaireStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, inventaireStock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inventaireStockRepository.count();
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

    protected InventaireStock getPersistedInventaireStock(InventaireStock inventaireStock) {
        return inventaireStockRepository.findById(inventaireStock.getId()).orElseThrow();
    }

    protected void assertPersistedInventaireStockToMatchAllProperties(InventaireStock expectedInventaireStock) {
        assertInventaireStockAllPropertiesEquals(expectedInventaireStock, getPersistedInventaireStock(expectedInventaireStock));
    }

    protected void assertPersistedInventaireStockToMatchUpdatableProperties(InventaireStock expectedInventaireStock) {
        assertInventaireStockAllUpdatablePropertiesEquals(expectedInventaireStock, getPersistedInventaireStock(expectedInventaireStock));
    }
}
