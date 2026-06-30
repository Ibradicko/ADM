package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.StockProduitAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.DepotStock;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.StockProduit;
import com.adm.supervision.repository.StockProduitRepository;
import com.adm.supervision.service.StockProduitService;
import com.adm.supervision.service.dto.StockProduitDTO;
import com.adm.supervision.service.mapper.StockProduitMapper;
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
 * Integration tests for the {@link StockProduitResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockProduitResourceIT {

    private static final BigDecimal DEFAULT_QUANTITE_THEORIQUE = new BigDecimal(0);
    private static final BigDecimal UPDATED_QUANTITE_THEORIQUE = new BigDecimal(1);
    private static final BigDecimal SMALLER_QUANTITE_THEORIQUE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_STOCK_ALERTE = new BigDecimal(0);
    private static final BigDecimal UPDATED_STOCK_ALERTE = new BigDecimal(1);
    private static final BigDecimal SMALLER_STOCK_ALERTE = new BigDecimal(0 - 1);

    private static final Instant DEFAULT_DATE_DERNIER_MOUVEMENT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_DERNIER_MOUVEMENT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/stock-produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StockProduitRepository stockProduitRepository;

    @Mock
    private StockProduitRepository stockProduitRepositoryMock;

    @Autowired
    private StockProduitMapper stockProduitMapper;

    @Mock
    private StockProduitService stockProduitServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockProduitMockMvc;

    private StockProduit stockProduit;

    private StockProduit insertedStockProduit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockProduit createEntity(EntityManager em) {
        StockProduit stockProduit = new StockProduit()
            .quantiteTheorique(DEFAULT_QUANTITE_THEORIQUE)
            .stockAlerte(DEFAULT_STOCK_ALERTE)
            .dateDernierMouvement(DEFAULT_DATE_DERNIER_MOUVEMENT);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        stockProduit.setProduit(produit);
        // Add required entity
        DepotStock depotStock;
        if (TestUtil.findAll(em, DepotStock.class).isEmpty()) {
            depotStock = DepotStockResourceIT.createEntity(em);
            em.persist(depotStock);
            em.flush();
        } else {
            depotStock = TestUtil.findAll(em, DepotStock.class).get(0);
        }
        stockProduit.setDepot(depotStock);
        return stockProduit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockProduit createUpdatedEntity(EntityManager em) {
        StockProduit updatedStockProduit = new StockProduit()
            .quantiteTheorique(UPDATED_QUANTITE_THEORIQUE)
            .stockAlerte(UPDATED_STOCK_ALERTE)
            .dateDernierMouvement(UPDATED_DATE_DERNIER_MOUVEMENT);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createUpdatedEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        updatedStockProduit.setProduit(produit);
        // Add required entity
        DepotStock depotStock;
        if (TestUtil.findAll(em, DepotStock.class).isEmpty()) {
            depotStock = DepotStockResourceIT.createUpdatedEntity(em);
            em.persist(depotStock);
            em.flush();
        } else {
            depotStock = TestUtil.findAll(em, DepotStock.class).get(0);
        }
        updatedStockProduit.setDepot(depotStock);
        return updatedStockProduit;
    }

    @BeforeEach
    void initTest() {
        stockProduit = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedStockProduit != null) {
            stockProduitRepository.delete(insertedStockProduit);
            insertedStockProduit = null;
        }
    }

    @Test
    @Transactional
    void createStockProduit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StockProduit
        StockProduitDTO stockProduitDTO = stockProduitMapper.toDto(stockProduit);
        var returnedStockProduitDTO = om.readValue(
            restStockProduitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockProduitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StockProduitDTO.class
        );

        // Validate the StockProduit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStockProduit = stockProduitMapper.toEntity(returnedStockProduitDTO);
        assertStockProduitUpdatableFieldsEquals(returnedStockProduit, getPersistedStockProduit(returnedStockProduit));

        insertedStockProduit = returnedStockProduit;
    }

    @Test
    @Transactional
    void createStockProduitWithExistingId() throws Exception {
        // Create the StockProduit with an existing ID
        stockProduit.setId(1L);
        StockProduitDTO stockProduitDTO = stockProduitMapper.toDto(stockProduit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockProduitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StockProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantiteTheoriqueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockProduit.setQuantiteTheorique(null);

        // Create the StockProduit, which fails.
        StockProduitDTO stockProduitDTO = stockProduitMapper.toDto(stockProduit);

        restStockProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStockProduits() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList
        restStockProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantiteTheorique").value(hasItem(sameNumber(DEFAULT_QUANTITE_THEORIQUE))))
            .andExpect(jsonPath("$.[*].stockAlerte").value(hasItem(sameNumber(DEFAULT_STOCK_ALERTE))))
            .andExpect(jsonPath("$.[*].dateDernierMouvement").value(hasItem(DEFAULT_DATE_DERNIER_MOUVEMENT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockProduitsWithEagerRelationshipsIsEnabled() throws Exception {
        when(stockProduitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(stockProduitServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockProduitsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(stockProduitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(stockProduitRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStockProduit() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get the stockProduit
        restStockProduitMockMvc
            .perform(get(ENTITY_API_URL_ID, stockProduit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockProduit.getId().intValue()))
            .andExpect(jsonPath("$.quantiteTheorique").value(sameNumber(DEFAULT_QUANTITE_THEORIQUE)))
            .andExpect(jsonPath("$.stockAlerte").value(sameNumber(DEFAULT_STOCK_ALERTE)))
            .andExpect(jsonPath("$.dateDernierMouvement").value(DEFAULT_DATE_DERNIER_MOUVEMENT.toString()));
    }

    @Test
    @Transactional
    void getStockProduitsByIdFiltering() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        Long id = stockProduit.getId();

        defaultStockProduitFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStockProduitFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStockProduitFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStockProduitsByQuantiteTheoriqueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where quantiteTheorique equals to
        defaultStockProduitFiltering(
            "quantiteTheorique.equals=" + DEFAULT_QUANTITE_THEORIQUE,
            "quantiteTheorique.equals=" + UPDATED_QUANTITE_THEORIQUE
        );
    }

    @Test
    @Transactional
    void getAllStockProduitsByQuantiteTheoriqueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where quantiteTheorique in
        defaultStockProduitFiltering(
            "quantiteTheorique.in=" + DEFAULT_QUANTITE_THEORIQUE + "," + UPDATED_QUANTITE_THEORIQUE,
            "quantiteTheorique.in=" + UPDATED_QUANTITE_THEORIQUE
        );
    }

    @Test
    @Transactional
    void getAllStockProduitsByQuantiteTheoriqueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where quantiteTheorique is not null
        defaultStockProduitFiltering("quantiteTheorique.specified=true", "quantiteTheorique.specified=false");
    }

    @Test
    @Transactional
    void getAllStockProduitsByQuantiteTheoriqueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where quantiteTheorique is greater than or equal to
        defaultStockProduitFiltering(
            "quantiteTheorique.greaterThanOrEqual=" + DEFAULT_QUANTITE_THEORIQUE,
            "quantiteTheorique.greaterThanOrEqual=" + UPDATED_QUANTITE_THEORIQUE
        );
    }

    @Test
    @Transactional
    void getAllStockProduitsByQuantiteTheoriqueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where quantiteTheorique is less than or equal to
        defaultStockProduitFiltering(
            "quantiteTheorique.lessThanOrEqual=" + DEFAULT_QUANTITE_THEORIQUE,
            "quantiteTheorique.lessThanOrEqual=" + SMALLER_QUANTITE_THEORIQUE
        );
    }

    @Test
    @Transactional
    void getAllStockProduitsByQuantiteTheoriqueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where quantiteTheorique is less than
        defaultStockProduitFiltering(
            "quantiteTheorique.lessThan=" + UPDATED_QUANTITE_THEORIQUE,
            "quantiteTheorique.lessThan=" + DEFAULT_QUANTITE_THEORIQUE
        );
    }

    @Test
    @Transactional
    void getAllStockProduitsByQuantiteTheoriqueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where quantiteTheorique is greater than
        defaultStockProduitFiltering(
            "quantiteTheorique.greaterThan=" + SMALLER_QUANTITE_THEORIQUE,
            "quantiteTheorique.greaterThan=" + DEFAULT_QUANTITE_THEORIQUE
        );
    }

    @Test
    @Transactional
    void getAllStockProduitsByStockAlerteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where stockAlerte equals to
        defaultStockProduitFiltering("stockAlerte.equals=" + DEFAULT_STOCK_ALERTE, "stockAlerte.equals=" + UPDATED_STOCK_ALERTE);
    }

    @Test
    @Transactional
    void getAllStockProduitsByStockAlerteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where stockAlerte in
        defaultStockProduitFiltering(
            "stockAlerte.in=" + DEFAULT_STOCK_ALERTE + "," + UPDATED_STOCK_ALERTE,
            "stockAlerte.in=" + UPDATED_STOCK_ALERTE
        );
    }

    @Test
    @Transactional
    void getAllStockProduitsByStockAlerteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where stockAlerte is not null
        defaultStockProduitFiltering("stockAlerte.specified=true", "stockAlerte.specified=false");
    }

    @Test
    @Transactional
    void getAllStockProduitsByStockAlerteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where stockAlerte is greater than or equal to
        defaultStockProduitFiltering(
            "stockAlerte.greaterThanOrEqual=" + DEFAULT_STOCK_ALERTE,
            "stockAlerte.greaterThanOrEqual=" + UPDATED_STOCK_ALERTE
        );
    }

    @Test
    @Transactional
    void getAllStockProduitsByStockAlerteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where stockAlerte is less than or equal to
        defaultStockProduitFiltering(
            "stockAlerte.lessThanOrEqual=" + DEFAULT_STOCK_ALERTE,
            "stockAlerte.lessThanOrEqual=" + SMALLER_STOCK_ALERTE
        );
    }

    @Test
    @Transactional
    void getAllStockProduitsByStockAlerteIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where stockAlerte is less than
        defaultStockProduitFiltering("stockAlerte.lessThan=" + UPDATED_STOCK_ALERTE, "stockAlerte.lessThan=" + DEFAULT_STOCK_ALERTE);
    }

    @Test
    @Transactional
    void getAllStockProduitsByStockAlerteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where stockAlerte is greater than
        defaultStockProduitFiltering("stockAlerte.greaterThan=" + SMALLER_STOCK_ALERTE, "stockAlerte.greaterThan=" + DEFAULT_STOCK_ALERTE);
    }

    @Test
    @Transactional
    void getAllStockProduitsByDateDernierMouvementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where dateDernierMouvement equals to
        defaultStockProduitFiltering(
            "dateDernierMouvement.equals=" + DEFAULT_DATE_DERNIER_MOUVEMENT,
            "dateDernierMouvement.equals=" + UPDATED_DATE_DERNIER_MOUVEMENT
        );
    }

    @Test
    @Transactional
    void getAllStockProduitsByDateDernierMouvementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where dateDernierMouvement in
        defaultStockProduitFiltering(
            "dateDernierMouvement.in=" + DEFAULT_DATE_DERNIER_MOUVEMENT + "," + UPDATED_DATE_DERNIER_MOUVEMENT,
            "dateDernierMouvement.in=" + UPDATED_DATE_DERNIER_MOUVEMENT
        );
    }

    @Test
    @Transactional
    void getAllStockProduitsByDateDernierMouvementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        // Get all the stockProduitList where dateDernierMouvement is not null
        defaultStockProduitFiltering("dateDernierMouvement.specified=true", "dateDernierMouvement.specified=false");
    }

    @Test
    @Transactional
    void getAllStockProduitsByProduitIsEqualToSomething() throws Exception {
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            stockProduitRepository.saveAndFlush(stockProduit);
            produit = ProduitResourceIT.createEntity(em);
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        em.persist(produit);
        em.flush();
        stockProduit.setProduit(produit);
        stockProduitRepository.saveAndFlush(stockProduit);
        Long produitId = produit.getId();
        // Get all the stockProduitList where produit equals to produitId
        defaultStockProduitShouldBeFound("produitId.equals=" + produitId);

        // Get all the stockProduitList where produit equals to (produitId + 1)
        defaultStockProduitShouldNotBeFound("produitId.equals=" + (produitId + 1));
    }

    @Test
    @Transactional
    void getAllStockProduitsByDepotIsEqualToSomething() throws Exception {
        DepotStock depot;
        if (TestUtil.findAll(em, DepotStock.class).isEmpty()) {
            stockProduitRepository.saveAndFlush(stockProduit);
            depot = DepotStockResourceIT.createEntity(em);
        } else {
            depot = TestUtil.findAll(em, DepotStock.class).get(0);
        }
        em.persist(depot);
        em.flush();
        stockProduit.setDepot(depot);
        stockProduitRepository.saveAndFlush(stockProduit);
        Long depotId = depot.getId();
        // Get all the stockProduitList where depot equals to depotId
        defaultStockProduitShouldBeFound("depotId.equals=" + depotId);

        // Get all the stockProduitList where depot equals to (depotId + 1)
        defaultStockProduitShouldNotBeFound("depotId.equals=" + (depotId + 1));
    }

    private void defaultStockProduitFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStockProduitShouldBeFound(shouldBeFound);
        defaultStockProduitShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockProduitShouldBeFound(String filter) throws Exception {
        restStockProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantiteTheorique").value(hasItem(sameNumber(DEFAULT_QUANTITE_THEORIQUE))))
            .andExpect(jsonPath("$.[*].stockAlerte").value(hasItem(sameNumber(DEFAULT_STOCK_ALERTE))))
            .andExpect(jsonPath("$.[*].dateDernierMouvement").value(hasItem(DEFAULT_DATE_DERNIER_MOUVEMENT.toString())));

        // Check, that the count call also returns 1
        restStockProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStockProduitShouldNotBeFound(String filter) throws Exception {
        restStockProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStockProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStockProduit() throws Exception {
        // Get the stockProduit
        restStockProduitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockProduit() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockProduit
        StockProduit updatedStockProduit = stockProduitRepository.findById(stockProduit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStockProduit are not directly saved in db
        em.detach(updatedStockProduit);
        updatedStockProduit
            .quantiteTheorique(UPDATED_QUANTITE_THEORIQUE)
            .stockAlerte(UPDATED_STOCK_ALERTE)
            .dateDernierMouvement(UPDATED_DATE_DERNIER_MOUVEMENT);
        StockProduitDTO stockProduitDTO = stockProduitMapper.toDto(updatedStockProduit);

        restStockProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockProduitDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStockProduitToMatchAllProperties(updatedStockProduit);
    }

    @Test
    @Transactional
    void putNonExistingStockProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockProduit.setId(longCount.incrementAndGet());

        // Create the StockProduit
        StockProduitDTO stockProduitDTO = stockProduitMapper.toDto(stockProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockProduit.setId(longCount.incrementAndGet());

        // Create the StockProduit
        StockProduitDTO stockProduitDTO = stockProduitMapper.toDto(stockProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockProduit.setId(longCount.incrementAndGet());

        // Create the StockProduit
        StockProduitDTO stockProduitDTO = stockProduitMapper.toDto(stockProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockProduitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockProduitWithPatch() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockProduit using partial update
        StockProduit partialUpdatedStockProduit = new StockProduit();
        partialUpdatedStockProduit.setId(stockProduit.getId());

        partialUpdatedStockProduit.quantiteTheorique(UPDATED_QUANTITE_THEORIQUE).stockAlerte(UPDATED_STOCK_ALERTE);

        restStockProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockProduit))
            )
            .andExpect(status().isOk());

        // Validate the StockProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockProduitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStockProduit, stockProduit),
            getPersistedStockProduit(stockProduit)
        );
    }

    @Test
    @Transactional
    void fullUpdateStockProduitWithPatch() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockProduit using partial update
        StockProduit partialUpdatedStockProduit = new StockProduit();
        partialUpdatedStockProduit.setId(stockProduit.getId());

        partialUpdatedStockProduit
            .quantiteTheorique(UPDATED_QUANTITE_THEORIQUE)
            .stockAlerte(UPDATED_STOCK_ALERTE)
            .dateDernierMouvement(UPDATED_DATE_DERNIER_MOUVEMENT);

        restStockProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockProduit))
            )
            .andExpect(status().isOk());

        // Validate the StockProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockProduitUpdatableFieldsEquals(partialUpdatedStockProduit, getPersistedStockProduit(partialUpdatedStockProduit));
    }

    @Test
    @Transactional
    void patchNonExistingStockProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockProduit.setId(longCount.incrementAndGet());

        // Create the StockProduit
        StockProduitDTO stockProduitDTO = stockProduitMapper.toDto(stockProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockProduitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockProduit.setId(longCount.incrementAndGet());

        // Create the StockProduit
        StockProduitDTO stockProduitDTO = stockProduitMapper.toDto(stockProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockProduit.setId(longCount.incrementAndGet());

        // Create the StockProduit
        StockProduitDTO stockProduitDTO = stockProduitMapper.toDto(stockProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockProduitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stockProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockProduit() throws Exception {
        // Initialize the database
        insertedStockProduit = stockProduitRepository.saveAndFlush(stockProduit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stockProduit
        restStockProduitMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockProduit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stockProduitRepository.count();
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

    protected StockProduit getPersistedStockProduit(StockProduit stockProduit) {
        return stockProduitRepository.findById(stockProduit.getId()).orElseThrow();
    }

    protected void assertPersistedStockProduitToMatchAllProperties(StockProduit expectedStockProduit) {
        assertStockProduitAllPropertiesEquals(expectedStockProduit, getPersistedStockProduit(expectedStockProduit));
    }

    protected void assertPersistedStockProduitToMatchUpdatableProperties(StockProduit expectedStockProduit) {
        assertStockProduitAllUpdatablePropertiesEquals(expectedStockProduit, getPersistedStockProduit(expectedStockProduit));
    }
}
