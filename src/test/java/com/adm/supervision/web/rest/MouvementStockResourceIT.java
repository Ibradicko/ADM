package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.MouvementStockAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.MouvementStock;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.enumeration.StatutMouvementStock;
import com.adm.supervision.domain.enumeration.TypeMouvementStock;
import com.adm.supervision.repository.MouvementStockRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.service.MouvementStockService;
import com.adm.supervision.service.dto.MouvementStockDTO;
import com.adm.supervision.service.mapper.MouvementStockMapper;
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
 * Integration tests for the {@link MouvementStockResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MouvementStockResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final TypeMouvementStock DEFAULT_TYPE_MOUVEMENT = TypeMouvementStock.ENTREE;
    private static final TypeMouvementStock UPDATED_TYPE_MOUVEMENT = TypeMouvementStock.SORTIE;

    private static final StatutMouvementStock DEFAULT_STATUT = StatutMouvementStock.BROUILLON;
    private static final StatutMouvementStock UPDATED_STATUT = StatutMouvementStock.VALIDE;

    private static final Instant DEFAULT_DATE_MOUVEMENT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_MOUVEMENT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_MOTIF = "AAAAAAAAAA";
    private static final String UPDATED_MOTIF = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mouvement-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MouvementStockRepository mouvementStockRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private MouvementStockRepository mouvementStockRepositoryMock;

    @Autowired
    private MouvementStockMapper mouvementStockMapper;

    @Mock
    private MouvementStockService mouvementStockServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMouvementStockMockMvc;

    private MouvementStock mouvementStock;

    private MouvementStock insertedMouvementStock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MouvementStock createEntity(EntityManager em) {
        MouvementStock mouvementStock = new MouvementStock()
            .reference(DEFAULT_REFERENCE)
            .typeMouvement(DEFAULT_TYPE_MOUVEMENT)
            .statut(DEFAULT_STATUT)
            .dateMouvement(DEFAULT_DATE_MOUVEMENT)
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
        mouvementStock.setBoutique(boutique);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        mouvementStock.setUtilisateur(user);
        return mouvementStock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MouvementStock createUpdatedEntity(EntityManager em) {
        MouvementStock updatedMouvementStock = new MouvementStock()
            .reference(UPDATED_REFERENCE)
            .typeMouvement(UPDATED_TYPE_MOUVEMENT)
            .statut(UPDATED_STATUT)
            .dateMouvement(UPDATED_DATE_MOUVEMENT)
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
        updatedMouvementStock.setBoutique(boutique);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedMouvementStock.setUtilisateur(user);
        return updatedMouvementStock;
    }

    @BeforeEach
    void initTest() {
        mouvementStock = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedMouvementStock != null) {
            mouvementStockRepository.delete(insertedMouvementStock);
            insertedMouvementStock = null;
        }
    }

    @Test
    @Transactional
    void createMouvementStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MouvementStock
        MouvementStockDTO mouvementStockDTO = mouvementStockMapper.toDto(mouvementStock);
        var returnedMouvementStockDTO = om.readValue(
            restMouvementStockMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mouvementStockDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MouvementStockDTO.class
        );

        // Validate the MouvementStock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMouvementStock = mouvementStockMapper.toEntity(returnedMouvementStockDTO);
        assertMouvementStockUpdatableFieldsEquals(returnedMouvementStock, getPersistedMouvementStock(returnedMouvementStock));

        insertedMouvementStock = returnedMouvementStock;
    }

    @Test
    @Transactional
    void createMouvementStockWithExistingId() throws Exception {
        // Create the MouvementStock with an existing ID
        mouvementStock.setId(1L);
        MouvementStockDTO mouvementStockDTO = mouvementStockMapper.toDto(mouvementStock);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMouvementStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mouvementStockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mouvementStock.setReference(null);

        // Create the MouvementStock, which fails.
        MouvementStockDTO mouvementStockDTO = mouvementStockMapper.toDto(mouvementStock);

        restMouvementStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mouvementStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeMouvementIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mouvementStock.setTypeMouvement(null);

        // Create the MouvementStock, which fails.
        MouvementStockDTO mouvementStockDTO = mouvementStockMapper.toDto(mouvementStock);

        restMouvementStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mouvementStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mouvementStock.setStatut(null);

        // Create the MouvementStock, which fails.
        MouvementStockDTO mouvementStockDTO = mouvementStockMapper.toDto(mouvementStock);

        restMouvementStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mouvementStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateMouvementIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        mouvementStock.setDateMouvement(null);

        // Create the MouvementStock, which fails.
        MouvementStockDTO mouvementStockDTO = mouvementStockMapper.toDto(mouvementStock);

        restMouvementStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mouvementStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMouvementStocks() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList
        restMouvementStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mouvementStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].typeMouvement").value(hasItem(DEFAULT_TYPE_MOUVEMENT.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateMouvement").value(hasItem(DEFAULT_DATE_MOUVEMENT.toString())))
            .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMouvementStocksWithEagerRelationshipsIsEnabled() throws Exception {
        when(mouvementStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMouvementStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(mouvementStockServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMouvementStocksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(mouvementStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMouvementStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(mouvementStockRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMouvementStock() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get the mouvementStock
        restMouvementStockMockMvc
            .perform(get(ENTITY_API_URL_ID, mouvementStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mouvementStock.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.typeMouvement").value(DEFAULT_TYPE_MOUVEMENT.toString()))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.dateMouvement").value(DEFAULT_DATE_MOUVEMENT.toString()))
            .andExpect(jsonPath("$.motif").value(DEFAULT_MOTIF));
    }

    @Test
    @Transactional
    void getMouvementStocksByIdFiltering() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        Long id = mouvementStock.getId();

        defaultMouvementStockFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultMouvementStockFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultMouvementStockFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMouvementStocksByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where reference equals to
        defaultMouvementStockFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllMouvementStocksByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where reference in
        defaultMouvementStockFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllMouvementStocksByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where reference is not null
        defaultMouvementStockFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllMouvementStocksByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where reference contains
        defaultMouvementStockFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllMouvementStocksByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where reference does not contain
        defaultMouvementStockFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllMouvementStocksByTypeMouvementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where typeMouvement equals to
        defaultMouvementStockFiltering("typeMouvement.equals=" + DEFAULT_TYPE_MOUVEMENT, "typeMouvement.equals=" + UPDATED_TYPE_MOUVEMENT);
    }

    @Test
    @Transactional
    void getAllMouvementStocksByTypeMouvementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where typeMouvement in
        defaultMouvementStockFiltering(
            "typeMouvement.in=" + DEFAULT_TYPE_MOUVEMENT + "," + UPDATED_TYPE_MOUVEMENT,
            "typeMouvement.in=" + UPDATED_TYPE_MOUVEMENT
        );
    }

    @Test
    @Transactional
    void getAllMouvementStocksByTypeMouvementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where typeMouvement is not null
        defaultMouvementStockFiltering("typeMouvement.specified=true", "typeMouvement.specified=false");
    }

    @Test
    @Transactional
    void getAllMouvementStocksByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where statut equals to
        defaultMouvementStockFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllMouvementStocksByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where statut in
        defaultMouvementStockFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllMouvementStocksByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where statut is not null
        defaultMouvementStockFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllMouvementStocksByDateMouvementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where dateMouvement equals to
        defaultMouvementStockFiltering("dateMouvement.equals=" + DEFAULT_DATE_MOUVEMENT, "dateMouvement.equals=" + UPDATED_DATE_MOUVEMENT);
    }

    @Test
    @Transactional
    void getAllMouvementStocksByDateMouvementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where dateMouvement in
        defaultMouvementStockFiltering(
            "dateMouvement.in=" + DEFAULT_DATE_MOUVEMENT + "," + UPDATED_DATE_MOUVEMENT,
            "dateMouvement.in=" + UPDATED_DATE_MOUVEMENT
        );
    }

    @Test
    @Transactional
    void getAllMouvementStocksByDateMouvementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where dateMouvement is not null
        defaultMouvementStockFiltering("dateMouvement.specified=true", "dateMouvement.specified=false");
    }

    @Test
    @Transactional
    void getAllMouvementStocksByMotifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where motif equals to
        defaultMouvementStockFiltering("motif.equals=" + DEFAULT_MOTIF, "motif.equals=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllMouvementStocksByMotifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where motif in
        defaultMouvementStockFiltering("motif.in=" + DEFAULT_MOTIF + "," + UPDATED_MOTIF, "motif.in=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllMouvementStocksByMotifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where motif is not null
        defaultMouvementStockFiltering("motif.specified=true", "motif.specified=false");
    }

    @Test
    @Transactional
    void getAllMouvementStocksByMotifContainsSomething() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where motif contains
        defaultMouvementStockFiltering("motif.contains=" + DEFAULT_MOTIF, "motif.contains=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllMouvementStocksByMotifNotContainsSomething() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        // Get all the mouvementStockList where motif does not contain
        defaultMouvementStockFiltering("motif.doesNotContain=" + UPDATED_MOTIF, "motif.doesNotContain=" + DEFAULT_MOTIF);
    }

    @Test
    @Transactional
    void getAllMouvementStocksByBoutiqueIsEqualToSomething() throws Exception {
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            mouvementStockRepository.saveAndFlush(mouvementStock);
            boutique = BoutiqueResourceIT.createEntity();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutique);
        em.flush();
        mouvementStock.setBoutique(boutique);
        mouvementStockRepository.saveAndFlush(mouvementStock);
        Long boutiqueId = boutique.getId();
        // Get all the mouvementStockList where boutique equals to boutiqueId
        defaultMouvementStockShouldBeFound("boutiqueId.equals=" + boutiqueId);

        // Get all the mouvementStockList where boutique equals to (boutiqueId + 1)
        defaultMouvementStockShouldNotBeFound("boutiqueId.equals=" + (boutiqueId + 1));
    }

    @Test
    @Transactional
    void getAllMouvementStocksByUtilisateurIsEqualToSomething() throws Exception {
        User utilisateur;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            mouvementStockRepository.saveAndFlush(mouvementStock);
            utilisateur = UserResourceIT.createEntity();
        } else {
            utilisateur = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(utilisateur);
        em.flush();
        mouvementStock.setUtilisateur(utilisateur);
        mouvementStockRepository.saveAndFlush(mouvementStock);
        Long utilisateurId = utilisateur.getId();
        // Get all the mouvementStockList where utilisateur equals to utilisateurId
        defaultMouvementStockShouldBeFound("utilisateurId.equals=" + utilisateurId);

        // Get all the mouvementStockList where utilisateur equals to (utilisateurId + 1)
        defaultMouvementStockShouldNotBeFound("utilisateurId.equals=" + (utilisateurId + 1));
    }

    private void defaultMouvementStockFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultMouvementStockShouldBeFound(shouldBeFound);
        defaultMouvementStockShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMouvementStockShouldBeFound(String filter) throws Exception {
        restMouvementStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mouvementStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].typeMouvement").value(hasItem(DEFAULT_TYPE_MOUVEMENT.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateMouvement").value(hasItem(DEFAULT_DATE_MOUVEMENT.toString())))
            .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)));

        // Check, that the count call also returns 1
        restMouvementStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMouvementStockShouldNotBeFound(String filter) throws Exception {
        restMouvementStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMouvementStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMouvementStock() throws Exception {
        // Get the mouvementStock
        restMouvementStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMouvementStock() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mouvementStock
        MouvementStock updatedMouvementStock = mouvementStockRepository.findById(mouvementStock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMouvementStock are not directly saved in db
        em.detach(updatedMouvementStock);
        updatedMouvementStock
            .reference(UPDATED_REFERENCE)
            .typeMouvement(UPDATED_TYPE_MOUVEMENT)
            .statut(UPDATED_STATUT)
            .dateMouvement(UPDATED_DATE_MOUVEMENT)
            .motif(UPDATED_MOTIF);
        MouvementStockDTO mouvementStockDTO = mouvementStockMapper.toDto(updatedMouvementStock);

        restMouvementStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mouvementStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mouvementStockDTO))
            )
            .andExpect(status().isOk());

        // Validate the MouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMouvementStockToMatchAllProperties(updatedMouvementStock);
    }

    @Test
    @Transactional
    void putNonExistingMouvementStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mouvementStock.setId(longCount.incrementAndGet());

        // Create the MouvementStock
        MouvementStockDTO mouvementStockDTO = mouvementStockMapper.toDto(mouvementStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMouvementStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mouvementStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mouvementStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMouvementStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mouvementStock.setId(longCount.incrementAndGet());

        // Create the MouvementStock
        MouvementStockDTO mouvementStockDTO = mouvementStockMapper.toDto(mouvementStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMouvementStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mouvementStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMouvementStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mouvementStock.setId(longCount.incrementAndGet());

        // Create the MouvementStock
        MouvementStockDTO mouvementStockDTO = mouvementStockMapper.toDto(mouvementStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMouvementStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mouvementStockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMouvementStockWithPatch() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mouvementStock using partial update
        MouvementStock partialUpdatedMouvementStock = new MouvementStock();
        partialUpdatedMouvementStock.setId(mouvementStock.getId());

        partialUpdatedMouvementStock.typeMouvement(UPDATED_TYPE_MOUVEMENT).dateMouvement(UPDATED_DATE_MOUVEMENT).motif(UPDATED_MOTIF);

        restMouvementStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMouvementStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMouvementStock))
            )
            .andExpect(status().isOk());

        // Validate the MouvementStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMouvementStockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMouvementStock, mouvementStock),
            getPersistedMouvementStock(mouvementStock)
        );
    }

    @Test
    @Transactional
    void fullUpdateMouvementStockWithPatch() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mouvementStock using partial update
        MouvementStock partialUpdatedMouvementStock = new MouvementStock();
        partialUpdatedMouvementStock.setId(mouvementStock.getId());

        partialUpdatedMouvementStock
            .reference(UPDATED_REFERENCE)
            .typeMouvement(UPDATED_TYPE_MOUVEMENT)
            .statut(UPDATED_STATUT)
            .dateMouvement(UPDATED_DATE_MOUVEMENT)
            .motif(UPDATED_MOTIF);

        restMouvementStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMouvementStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMouvementStock))
            )
            .andExpect(status().isOk());

        // Validate the MouvementStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMouvementStockUpdatableFieldsEquals(partialUpdatedMouvementStock, getPersistedMouvementStock(partialUpdatedMouvementStock));
    }

    @Test
    @Transactional
    void patchNonExistingMouvementStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mouvementStock.setId(longCount.incrementAndGet());

        // Create the MouvementStock
        MouvementStockDTO mouvementStockDTO = mouvementStockMapper.toDto(mouvementStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMouvementStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, mouvementStockDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(mouvementStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMouvementStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mouvementStock.setId(longCount.incrementAndGet());

        // Create the MouvementStock
        MouvementStockDTO mouvementStockDTO = mouvementStockMapper.toDto(mouvementStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMouvementStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(mouvementStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMouvementStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mouvementStock.setId(longCount.incrementAndGet());

        // Create the MouvementStock
        MouvementStockDTO mouvementStockDTO = mouvementStockMapper.toDto(mouvementStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMouvementStockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(mouvementStockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMouvementStock() throws Exception {
        // Initialize the database
        insertedMouvementStock = mouvementStockRepository.saveAndFlush(mouvementStock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the mouvementStock
        restMouvementStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, mouvementStock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return mouvementStockRepository.count();
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

    protected MouvementStock getPersistedMouvementStock(MouvementStock mouvementStock) {
        return mouvementStockRepository.findById(mouvementStock.getId()).orElseThrow();
    }

    protected void assertPersistedMouvementStockToMatchAllProperties(MouvementStock expectedMouvementStock) {
        assertMouvementStockAllPropertiesEquals(expectedMouvementStock, getPersistedMouvementStock(expectedMouvementStock));
    }

    protected void assertPersistedMouvementStockToMatchUpdatableProperties(MouvementStock expectedMouvementStock) {
        assertMouvementStockAllUpdatablePropertiesEquals(expectedMouvementStock, getPersistedMouvementStock(expectedMouvementStock));
    }
}
