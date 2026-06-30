package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.EtiquetteProduitAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.EtiquetteProduit;
import com.adm.supervision.domain.LotEtiquettes;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.repository.EtiquetteProduitRepository;
import com.adm.supervision.service.EtiquetteProduitService;
import com.adm.supervision.service.dto.EtiquetteProduitDTO;
import com.adm.supervision.service.mapper.EtiquetteProduitMapper;
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
 * Integration tests for the {@link EtiquetteProduitResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EtiquetteProduitResourceIT {

    private static final Integer DEFAULT_QUANTITE = 1;
    private static final Integer UPDATED_QUANTITE = 2;
    private static final Integer SMALLER_QUANTITE = 1 - 1;

    private static final Boolean DEFAULT_IMPRIMEE = false;
    private static final Boolean UPDATED_IMPRIMEE = true;

    private static final Instant DEFAULT_DATE_IMPRESSION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_IMPRESSION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/etiquette-produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EtiquetteProduitRepository etiquetteProduitRepository;

    @Mock
    private EtiquetteProduitRepository etiquetteProduitRepositoryMock;

    @Autowired
    private EtiquetteProduitMapper etiquetteProduitMapper;

    @Mock
    private EtiquetteProduitService etiquetteProduitServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEtiquetteProduitMockMvc;

    private EtiquetteProduit etiquetteProduit;

    private EtiquetteProduit insertedEtiquetteProduit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EtiquetteProduit createEntity(EntityManager em) {
        EtiquetteProduit etiquetteProduit = new EtiquetteProduit()
            .quantite(DEFAULT_QUANTITE)
            .imprimee(DEFAULT_IMPRIMEE)
            .dateImpression(DEFAULT_DATE_IMPRESSION);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        etiquetteProduit.setProduit(produit);
        // Add required entity
        LotEtiquettes lotEtiquettes;
        if (TestUtil.findAll(em, LotEtiquettes.class).isEmpty()) {
            lotEtiquettes = LotEtiquettesResourceIT.createEntity();
            em.persist(lotEtiquettes);
            em.flush();
        } else {
            lotEtiquettes = TestUtil.findAll(em, LotEtiquettes.class).get(0);
        }
        etiquetteProduit.setLot(lotEtiquettes);
        return etiquetteProduit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EtiquetteProduit createUpdatedEntity(EntityManager em) {
        EtiquetteProduit updatedEtiquetteProduit = new EtiquetteProduit()
            .quantite(UPDATED_QUANTITE)
            .imprimee(UPDATED_IMPRIMEE)
            .dateImpression(UPDATED_DATE_IMPRESSION);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createUpdatedEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        updatedEtiquetteProduit.setProduit(produit);
        // Add required entity
        LotEtiquettes lotEtiquettes;
        if (TestUtil.findAll(em, LotEtiquettes.class).isEmpty()) {
            lotEtiquettes = LotEtiquettesResourceIT.createUpdatedEntity();
            em.persist(lotEtiquettes);
            em.flush();
        } else {
            lotEtiquettes = TestUtil.findAll(em, LotEtiquettes.class).get(0);
        }
        updatedEtiquetteProduit.setLot(lotEtiquettes);
        return updatedEtiquetteProduit;
    }

    @BeforeEach
    void initTest() {
        etiquetteProduit = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedEtiquetteProduit != null) {
            etiquetteProduitRepository.delete(insertedEtiquetteProduit);
            insertedEtiquetteProduit = null;
        }
    }

    @Test
    @Transactional
    void createEtiquetteProduit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EtiquetteProduit
        EtiquetteProduitDTO etiquetteProduitDTO = etiquetteProduitMapper.toDto(etiquetteProduit);
        var returnedEtiquetteProduitDTO = om.readValue(
            restEtiquetteProduitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etiquetteProduitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EtiquetteProduitDTO.class
        );

        // Validate the EtiquetteProduit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEtiquetteProduit = etiquetteProduitMapper.toEntity(returnedEtiquetteProduitDTO);
        assertEtiquetteProduitUpdatableFieldsEquals(returnedEtiquetteProduit, getPersistedEtiquetteProduit(returnedEtiquetteProduit));

        insertedEtiquetteProduit = returnedEtiquetteProduit;
    }

    @Test
    @Transactional
    void createEtiquetteProduitWithExistingId() throws Exception {
        // Create the EtiquetteProduit with an existing ID
        etiquetteProduit.setId(1L);
        EtiquetteProduitDTO etiquetteProduitDTO = etiquetteProduitMapper.toDto(etiquetteProduit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEtiquetteProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etiquetteProduitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EtiquetteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantiteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etiquetteProduit.setQuantite(null);

        // Create the EtiquetteProduit, which fails.
        EtiquetteProduitDTO etiquetteProduitDTO = etiquetteProduitMapper.toDto(etiquetteProduit);

        restEtiquetteProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etiquetteProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkImprimeeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etiquetteProduit.setImprimee(null);

        // Create the EtiquetteProduit, which fails.
        EtiquetteProduitDTO etiquetteProduitDTO = etiquetteProduitMapper.toDto(etiquetteProduit);

        restEtiquetteProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etiquetteProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEtiquetteProduits() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList
        restEtiquetteProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(etiquetteProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantite").value(hasItem(DEFAULT_QUANTITE)))
            .andExpect(jsonPath("$.[*].imprimee").value(hasItem(DEFAULT_IMPRIMEE)))
            .andExpect(jsonPath("$.[*].dateImpression").value(hasItem(DEFAULT_DATE_IMPRESSION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEtiquetteProduitsWithEagerRelationshipsIsEnabled() throws Exception {
        when(etiquetteProduitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEtiquetteProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(etiquetteProduitServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEtiquetteProduitsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(etiquetteProduitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEtiquetteProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(etiquetteProduitRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEtiquetteProduit() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get the etiquetteProduit
        restEtiquetteProduitMockMvc
            .perform(get(ENTITY_API_URL_ID, etiquetteProduit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(etiquetteProduit.getId().intValue()))
            .andExpect(jsonPath("$.quantite").value(DEFAULT_QUANTITE))
            .andExpect(jsonPath("$.imprimee").value(DEFAULT_IMPRIMEE))
            .andExpect(jsonPath("$.dateImpression").value(DEFAULT_DATE_IMPRESSION.toString()));
    }

    @Test
    @Transactional
    void getEtiquetteProduitsByIdFiltering() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        Long id = etiquetteProduit.getId();

        defaultEtiquetteProduitFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEtiquetteProduitFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEtiquetteProduitFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByQuantiteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList where quantite equals to
        defaultEtiquetteProduitFiltering("quantite.equals=" + DEFAULT_QUANTITE, "quantite.equals=" + UPDATED_QUANTITE);
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByQuantiteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList where quantite in
        defaultEtiquetteProduitFiltering("quantite.in=" + DEFAULT_QUANTITE + "," + UPDATED_QUANTITE, "quantite.in=" + UPDATED_QUANTITE);
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByQuantiteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList where quantite is not null
        defaultEtiquetteProduitFiltering("quantite.specified=true", "quantite.specified=false");
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByQuantiteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList where quantite is greater than or equal to
        defaultEtiquetteProduitFiltering(
            "quantite.greaterThanOrEqual=" + DEFAULT_QUANTITE,
            "quantite.greaterThanOrEqual=" + UPDATED_QUANTITE
        );
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByQuantiteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList where quantite is less than or equal to
        defaultEtiquetteProduitFiltering("quantite.lessThanOrEqual=" + DEFAULT_QUANTITE, "quantite.lessThanOrEqual=" + SMALLER_QUANTITE);
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByQuantiteIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList where quantite is less than
        defaultEtiquetteProduitFiltering("quantite.lessThan=" + UPDATED_QUANTITE, "quantite.lessThan=" + DEFAULT_QUANTITE);
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByQuantiteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList where quantite is greater than
        defaultEtiquetteProduitFiltering("quantite.greaterThan=" + SMALLER_QUANTITE, "quantite.greaterThan=" + DEFAULT_QUANTITE);
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByImprimeeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList where imprimee equals to
        defaultEtiquetteProduitFiltering("imprimee.equals=" + DEFAULT_IMPRIMEE, "imprimee.equals=" + UPDATED_IMPRIMEE);
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByImprimeeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList where imprimee in
        defaultEtiquetteProduitFiltering("imprimee.in=" + DEFAULT_IMPRIMEE + "," + UPDATED_IMPRIMEE, "imprimee.in=" + UPDATED_IMPRIMEE);
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByImprimeeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList where imprimee is not null
        defaultEtiquetteProduitFiltering("imprimee.specified=true", "imprimee.specified=false");
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByDateImpressionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList where dateImpression equals to
        defaultEtiquetteProduitFiltering(
            "dateImpression.equals=" + DEFAULT_DATE_IMPRESSION,
            "dateImpression.equals=" + UPDATED_DATE_IMPRESSION
        );
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByDateImpressionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList where dateImpression in
        defaultEtiquetteProduitFiltering(
            "dateImpression.in=" + DEFAULT_DATE_IMPRESSION + "," + UPDATED_DATE_IMPRESSION,
            "dateImpression.in=" + UPDATED_DATE_IMPRESSION
        );
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByDateImpressionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        // Get all the etiquetteProduitList where dateImpression is not null
        defaultEtiquetteProduitFiltering("dateImpression.specified=true", "dateImpression.specified=false");
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByProduitIsEqualToSomething() throws Exception {
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            etiquetteProduitRepository.saveAndFlush(etiquetteProduit);
            produit = ProduitResourceIT.createEntity(em);
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        em.persist(produit);
        em.flush();
        etiquetteProduit.setProduit(produit);
        etiquetteProduitRepository.saveAndFlush(etiquetteProduit);
        Long produitId = produit.getId();
        // Get all the etiquetteProduitList where produit equals to produitId
        defaultEtiquetteProduitShouldBeFound("produitId.equals=" + produitId);

        // Get all the etiquetteProduitList where produit equals to (produitId + 1)
        defaultEtiquetteProduitShouldNotBeFound("produitId.equals=" + (produitId + 1));
    }

    @Test
    @Transactional
    void getAllEtiquetteProduitsByLotIsEqualToSomething() throws Exception {
        LotEtiquettes lot;
        if (TestUtil.findAll(em, LotEtiquettes.class).isEmpty()) {
            etiquetteProduitRepository.saveAndFlush(etiquetteProduit);
            lot = LotEtiquettesResourceIT.createEntity();
        } else {
            lot = TestUtil.findAll(em, LotEtiquettes.class).get(0);
        }
        em.persist(lot);
        em.flush();
        etiquetteProduit.setLot(lot);
        etiquetteProduitRepository.saveAndFlush(etiquetteProduit);
        Long lotId = lot.getId();
        // Get all the etiquetteProduitList where lot equals to lotId
        defaultEtiquetteProduitShouldBeFound("lotId.equals=" + lotId);

        // Get all the etiquetteProduitList where lot equals to (lotId + 1)
        defaultEtiquetteProduitShouldNotBeFound("lotId.equals=" + (lotId + 1));
    }

    private void defaultEtiquetteProduitFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEtiquetteProduitShouldBeFound(shouldBeFound);
        defaultEtiquetteProduitShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEtiquetteProduitShouldBeFound(String filter) throws Exception {
        restEtiquetteProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(etiquetteProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantite").value(hasItem(DEFAULT_QUANTITE)))
            .andExpect(jsonPath("$.[*].imprimee").value(hasItem(DEFAULT_IMPRIMEE)))
            .andExpect(jsonPath("$.[*].dateImpression").value(hasItem(DEFAULT_DATE_IMPRESSION.toString())));

        // Check, that the count call also returns 1
        restEtiquetteProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEtiquetteProduitShouldNotBeFound(String filter) throws Exception {
        restEtiquetteProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEtiquetteProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEtiquetteProduit() throws Exception {
        // Get the etiquetteProduit
        restEtiquetteProduitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEtiquetteProduit() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etiquetteProduit
        EtiquetteProduit updatedEtiquetteProduit = etiquetteProduitRepository.findById(etiquetteProduit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEtiquetteProduit are not directly saved in db
        em.detach(updatedEtiquetteProduit);
        updatedEtiquetteProduit.quantite(UPDATED_QUANTITE).imprimee(UPDATED_IMPRIMEE).dateImpression(UPDATED_DATE_IMPRESSION);
        EtiquetteProduitDTO etiquetteProduitDTO = etiquetteProduitMapper.toDto(updatedEtiquetteProduit);

        restEtiquetteProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, etiquetteProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(etiquetteProduitDTO))
            )
            .andExpect(status().isOk());

        // Validate the EtiquetteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEtiquetteProduitToMatchAllProperties(updatedEtiquetteProduit);
    }

    @Test
    @Transactional
    void putNonExistingEtiquetteProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiquetteProduit.setId(longCount.incrementAndGet());

        // Create the EtiquetteProduit
        EtiquetteProduitDTO etiquetteProduitDTO = etiquetteProduitMapper.toDto(etiquetteProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEtiquetteProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, etiquetteProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(etiquetteProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtiquetteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEtiquetteProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiquetteProduit.setId(longCount.incrementAndGet());

        // Create the EtiquetteProduit
        EtiquetteProduitDTO etiquetteProduitDTO = etiquetteProduitMapper.toDto(etiquetteProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtiquetteProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(etiquetteProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtiquetteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEtiquetteProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiquetteProduit.setId(longCount.incrementAndGet());

        // Create the EtiquetteProduit
        EtiquetteProduitDTO etiquetteProduitDTO = etiquetteProduitMapper.toDto(etiquetteProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtiquetteProduitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etiquetteProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EtiquetteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEtiquetteProduitWithPatch() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etiquetteProduit using partial update
        EtiquetteProduit partialUpdatedEtiquetteProduit = new EtiquetteProduit();
        partialUpdatedEtiquetteProduit.setId(etiquetteProduit.getId());

        restEtiquetteProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEtiquetteProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEtiquetteProduit))
            )
            .andExpect(status().isOk());

        // Validate the EtiquetteProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEtiquetteProduitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEtiquetteProduit, etiquetteProduit),
            getPersistedEtiquetteProduit(etiquetteProduit)
        );
    }

    @Test
    @Transactional
    void fullUpdateEtiquetteProduitWithPatch() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etiquetteProduit using partial update
        EtiquetteProduit partialUpdatedEtiquetteProduit = new EtiquetteProduit();
        partialUpdatedEtiquetteProduit.setId(etiquetteProduit.getId());

        partialUpdatedEtiquetteProduit.quantite(UPDATED_QUANTITE).imprimee(UPDATED_IMPRIMEE).dateImpression(UPDATED_DATE_IMPRESSION);

        restEtiquetteProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEtiquetteProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEtiquetteProduit))
            )
            .andExpect(status().isOk());

        // Validate the EtiquetteProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEtiquetteProduitUpdatableFieldsEquals(
            partialUpdatedEtiquetteProduit,
            getPersistedEtiquetteProduit(partialUpdatedEtiquetteProduit)
        );
    }

    @Test
    @Transactional
    void patchNonExistingEtiquetteProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiquetteProduit.setId(longCount.incrementAndGet());

        // Create the EtiquetteProduit
        EtiquetteProduitDTO etiquetteProduitDTO = etiquetteProduitMapper.toDto(etiquetteProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEtiquetteProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, etiquetteProduitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(etiquetteProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtiquetteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEtiquetteProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiquetteProduit.setId(longCount.incrementAndGet());

        // Create the EtiquetteProduit
        EtiquetteProduitDTO etiquetteProduitDTO = etiquetteProduitMapper.toDto(etiquetteProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtiquetteProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(etiquetteProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtiquetteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEtiquetteProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etiquetteProduit.setId(longCount.incrementAndGet());

        // Create the EtiquetteProduit
        EtiquetteProduitDTO etiquetteProduitDTO = etiquetteProduitMapper.toDto(etiquetteProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtiquetteProduitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(etiquetteProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EtiquetteProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEtiquetteProduit() throws Exception {
        // Initialize the database
        insertedEtiquetteProduit = etiquetteProduitRepository.saveAndFlush(etiquetteProduit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the etiquetteProduit
        restEtiquetteProduitMockMvc
            .perform(delete(ENTITY_API_URL_ID, etiquetteProduit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return etiquetteProduitRepository.count();
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

    protected EtiquetteProduit getPersistedEtiquetteProduit(EtiquetteProduit etiquetteProduit) {
        return etiquetteProduitRepository.findById(etiquetteProduit.getId()).orElseThrow();
    }

    protected void assertPersistedEtiquetteProduitToMatchAllProperties(EtiquetteProduit expectedEtiquetteProduit) {
        assertEtiquetteProduitAllPropertiesEquals(expectedEtiquetteProduit, getPersistedEtiquetteProduit(expectedEtiquetteProduit));
    }

    protected void assertPersistedEtiquetteProduitToMatchUpdatableProperties(EtiquetteProduit expectedEtiquetteProduit) {
        assertEtiquetteProduitAllUpdatablePropertiesEquals(
            expectedEtiquetteProduit,
            getPersistedEtiquetteProduit(expectedEtiquetteProduit)
        );
    }
}
