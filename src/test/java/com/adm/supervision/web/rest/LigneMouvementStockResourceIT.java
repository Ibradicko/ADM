package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.LigneMouvementStockAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.DepotStock;
import com.adm.supervision.domain.LigneMouvementStock;
import com.adm.supervision.domain.MouvementStock;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.repository.LigneMouvementStockRepository;
import com.adm.supervision.service.LigneMouvementStockService;
import com.adm.supervision.service.dto.LigneMouvementStockDTO;
import com.adm.supervision.service.mapper.LigneMouvementStockMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link LigneMouvementStockResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LigneMouvementStockResourceIT {

    private static final BigDecimal DEFAULT_QUANTITE = new BigDecimal(0);
    private static final BigDecimal UPDATED_QUANTITE = new BigDecimal(1);
    private static final BigDecimal SMALLER_QUANTITE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_STOCK_AVANT = new BigDecimal(1);
    private static final BigDecimal UPDATED_STOCK_AVANT = new BigDecimal(2);
    private static final BigDecimal SMALLER_STOCK_AVANT = new BigDecimal(1 - 1);

    private static final BigDecimal DEFAULT_STOCK_APRES = new BigDecimal(1);
    private static final BigDecimal UPDATED_STOCK_APRES = new BigDecimal(2);
    private static final BigDecimal SMALLER_STOCK_APRES = new BigDecimal(1 - 1);

    private static final String DEFAULT_COMMENTAIRE = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ligne-mouvement-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LigneMouvementStockRepository ligneMouvementStockRepository;

    @Mock
    private LigneMouvementStockRepository ligneMouvementStockRepositoryMock;

    @Autowired
    private LigneMouvementStockMapper ligneMouvementStockMapper;

    @Mock
    private LigneMouvementStockService ligneMouvementStockServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLigneMouvementStockMockMvc;

    private LigneMouvementStock ligneMouvementStock;

    private LigneMouvementStock insertedLigneMouvementStock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneMouvementStock createEntity(EntityManager em) {
        LigneMouvementStock ligneMouvementStock = new LigneMouvementStock()
            .quantite(DEFAULT_QUANTITE)
            .stockAvant(DEFAULT_STOCK_AVANT)
            .stockApres(DEFAULT_STOCK_APRES)
            .commentaire(DEFAULT_COMMENTAIRE);
        // Add required entity
        MouvementStock mouvementStock;
        if (TestUtil.findAll(em, MouvementStock.class).isEmpty()) {
            mouvementStock = MouvementStockResourceIT.createEntity(em);
            em.persist(mouvementStock);
            em.flush();
        } else {
            mouvementStock = TestUtil.findAll(em, MouvementStock.class).get(0);
        }
        ligneMouvementStock.setMouvement(mouvementStock);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        ligneMouvementStock.setProduit(produit);
        // Add required entity
        DepotStock depotStock;
        if (TestUtil.findAll(em, DepotStock.class).isEmpty()) {
            depotStock = DepotStockResourceIT.createEntity(em);
            em.persist(depotStock);
            em.flush();
        } else {
            depotStock = TestUtil.findAll(em, DepotStock.class).get(0);
        }
        ligneMouvementStock.setDepot(depotStock);
        return ligneMouvementStock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneMouvementStock createUpdatedEntity(EntityManager em) {
        LigneMouvementStock updatedLigneMouvementStock = new LigneMouvementStock()
            .quantite(UPDATED_QUANTITE)
            .stockAvant(UPDATED_STOCK_AVANT)
            .stockApres(UPDATED_STOCK_APRES)
            .commentaire(UPDATED_COMMENTAIRE);
        // Add required entity
        MouvementStock mouvementStock;
        if (TestUtil.findAll(em, MouvementStock.class).isEmpty()) {
            mouvementStock = MouvementStockResourceIT.createUpdatedEntity(em);
            em.persist(mouvementStock);
            em.flush();
        } else {
            mouvementStock = TestUtil.findAll(em, MouvementStock.class).get(0);
        }
        updatedLigneMouvementStock.setMouvement(mouvementStock);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createUpdatedEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        updatedLigneMouvementStock.setProduit(produit);
        // Add required entity
        DepotStock depotStock;
        if (TestUtil.findAll(em, DepotStock.class).isEmpty()) {
            depotStock = DepotStockResourceIT.createUpdatedEntity(em);
            em.persist(depotStock);
            em.flush();
        } else {
            depotStock = TestUtil.findAll(em, DepotStock.class).get(0);
        }
        updatedLigneMouvementStock.setDepot(depotStock);
        return updatedLigneMouvementStock;
    }

    @BeforeEach
    void initTest() {
        ligneMouvementStock = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLigneMouvementStock != null) {
            ligneMouvementStockRepository.delete(insertedLigneMouvementStock);
            insertedLigneMouvementStock = null;
        }
    }

    @Test
    @Transactional
    void createLigneMouvementStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LigneMouvementStock
        LigneMouvementStockDTO ligneMouvementStockDTO = ligneMouvementStockMapper.toDto(ligneMouvementStock);
        var returnedLigneMouvementStockDTO = om.readValue(
            restLigneMouvementStockMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneMouvementStockDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LigneMouvementStockDTO.class
        );

        // Validate the LigneMouvementStock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLigneMouvementStock = ligneMouvementStockMapper.toEntity(returnedLigneMouvementStockDTO);
        assertLigneMouvementStockUpdatableFieldsEquals(
            returnedLigneMouvementStock,
            getPersistedLigneMouvementStock(returnedLigneMouvementStock)
        );

        insertedLigneMouvementStock = returnedLigneMouvementStock;
    }

    @Test
    @Transactional
    void createLigneMouvementStockWithExistingId() throws Exception {
        // Create the LigneMouvementStock with an existing ID
        ligneMouvementStock.setId(1L);
        LigneMouvementStockDTO ligneMouvementStockDTO = ligneMouvementStockMapper.toDto(ligneMouvementStock);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLigneMouvementStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneMouvementStockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LigneMouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantiteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ligneMouvementStock.setQuantite(null);

        // Create the LigneMouvementStock, which fails.
        LigneMouvementStockDTO ligneMouvementStockDTO = ligneMouvementStockMapper.toDto(ligneMouvementStock);

        restLigneMouvementStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneMouvementStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocks() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList
        restLigneMouvementStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligneMouvementStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantite").value(hasItem(sameNumber(DEFAULT_QUANTITE))))
            .andExpect(jsonPath("$.[*].stockAvant").value(hasItem(sameNumber(DEFAULT_STOCK_AVANT))))
            .andExpect(jsonPath("$.[*].stockApres").value(hasItem(sameNumber(DEFAULT_STOCK_APRES))))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLigneMouvementStocksWithEagerRelationshipsIsEnabled() throws Exception {
        when(ligneMouvementStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLigneMouvementStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ligneMouvementStockServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLigneMouvementStocksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ligneMouvementStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLigneMouvementStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ligneMouvementStockRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLigneMouvementStock() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get the ligneMouvementStock
        restLigneMouvementStockMockMvc
            .perform(get(ENTITY_API_URL_ID, ligneMouvementStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ligneMouvementStock.getId().intValue()))
            .andExpect(jsonPath("$.quantite").value(sameNumber(DEFAULT_QUANTITE)))
            .andExpect(jsonPath("$.stockAvant").value(sameNumber(DEFAULT_STOCK_AVANT)))
            .andExpect(jsonPath("$.stockApres").value(sameNumber(DEFAULT_STOCK_APRES)))
            .andExpect(jsonPath("$.commentaire").value(DEFAULT_COMMENTAIRE));
    }

    @Test
    @Transactional
    void getLigneMouvementStocksByIdFiltering() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        Long id = ligneMouvementStock.getId();

        defaultLigneMouvementStockFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLigneMouvementStockFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLigneMouvementStockFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByQuantiteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where quantite equals to
        defaultLigneMouvementStockFiltering("quantite.equals=" + DEFAULT_QUANTITE, "quantite.equals=" + UPDATED_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByQuantiteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where quantite in
        defaultLigneMouvementStockFiltering("quantite.in=" + DEFAULT_QUANTITE + "," + UPDATED_QUANTITE, "quantite.in=" + UPDATED_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByQuantiteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where quantite is not null
        defaultLigneMouvementStockFiltering("quantite.specified=true", "quantite.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByQuantiteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where quantite is greater than or equal to
        defaultLigneMouvementStockFiltering(
            "quantite.greaterThanOrEqual=" + DEFAULT_QUANTITE,
            "quantite.greaterThanOrEqual=" + UPDATED_QUANTITE
        );
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByQuantiteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where quantite is less than or equal to
        defaultLigneMouvementStockFiltering("quantite.lessThanOrEqual=" + DEFAULT_QUANTITE, "quantite.lessThanOrEqual=" + SMALLER_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByQuantiteIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where quantite is less than
        defaultLigneMouvementStockFiltering("quantite.lessThan=" + UPDATED_QUANTITE, "quantite.lessThan=" + DEFAULT_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByQuantiteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where quantite is greater than
        defaultLigneMouvementStockFiltering("quantite.greaterThan=" + SMALLER_QUANTITE, "quantite.greaterThan=" + DEFAULT_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockAvantIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockAvant equals to
        defaultLigneMouvementStockFiltering("stockAvant.equals=" + DEFAULT_STOCK_AVANT, "stockAvant.equals=" + UPDATED_STOCK_AVANT);
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockAvantIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockAvant in
        defaultLigneMouvementStockFiltering(
            "stockAvant.in=" + DEFAULT_STOCK_AVANT + "," + UPDATED_STOCK_AVANT,
            "stockAvant.in=" + UPDATED_STOCK_AVANT
        );
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockAvantIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockAvant is not null
        defaultLigneMouvementStockFiltering("stockAvant.specified=true", "stockAvant.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockAvantIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockAvant is greater than or equal to
        defaultLigneMouvementStockFiltering(
            "stockAvant.greaterThanOrEqual=" + DEFAULT_STOCK_AVANT,
            "stockAvant.greaterThanOrEqual=" + UPDATED_STOCK_AVANT
        );
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockAvantIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockAvant is less than or equal to
        defaultLigneMouvementStockFiltering(
            "stockAvant.lessThanOrEqual=" + DEFAULT_STOCK_AVANT,
            "stockAvant.lessThanOrEqual=" + SMALLER_STOCK_AVANT
        );
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockAvantIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockAvant is less than
        defaultLigneMouvementStockFiltering("stockAvant.lessThan=" + UPDATED_STOCK_AVANT, "stockAvant.lessThan=" + DEFAULT_STOCK_AVANT);
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockAvantIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockAvant is greater than
        defaultLigneMouvementStockFiltering(
            "stockAvant.greaterThan=" + SMALLER_STOCK_AVANT,
            "stockAvant.greaterThan=" + DEFAULT_STOCK_AVANT
        );
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockApresIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockApres equals to
        defaultLigneMouvementStockFiltering("stockApres.equals=" + DEFAULT_STOCK_APRES, "stockApres.equals=" + UPDATED_STOCK_APRES);
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockApresIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockApres in
        defaultLigneMouvementStockFiltering(
            "stockApres.in=" + DEFAULT_STOCK_APRES + "," + UPDATED_STOCK_APRES,
            "stockApres.in=" + UPDATED_STOCK_APRES
        );
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockApresIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockApres is not null
        defaultLigneMouvementStockFiltering("stockApres.specified=true", "stockApres.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockApresIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockApres is greater than or equal to
        defaultLigneMouvementStockFiltering(
            "stockApres.greaterThanOrEqual=" + DEFAULT_STOCK_APRES,
            "stockApres.greaterThanOrEqual=" + UPDATED_STOCK_APRES
        );
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockApresIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockApres is less than or equal to
        defaultLigneMouvementStockFiltering(
            "stockApres.lessThanOrEqual=" + DEFAULT_STOCK_APRES,
            "stockApres.lessThanOrEqual=" + SMALLER_STOCK_APRES
        );
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockApresIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockApres is less than
        defaultLigneMouvementStockFiltering("stockApres.lessThan=" + UPDATED_STOCK_APRES, "stockApres.lessThan=" + DEFAULT_STOCK_APRES);
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByStockApresIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where stockApres is greater than
        defaultLigneMouvementStockFiltering(
            "stockApres.greaterThan=" + SMALLER_STOCK_APRES,
            "stockApres.greaterThan=" + DEFAULT_STOCK_APRES
        );
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByCommentaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where commentaire equals to
        defaultLigneMouvementStockFiltering("commentaire.equals=" + DEFAULT_COMMENTAIRE, "commentaire.equals=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByCommentaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where commentaire in
        defaultLigneMouvementStockFiltering(
            "commentaire.in=" + DEFAULT_COMMENTAIRE + "," + UPDATED_COMMENTAIRE,
            "commentaire.in=" + UPDATED_COMMENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByCommentaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where commentaire is not null
        defaultLigneMouvementStockFiltering("commentaire.specified=true", "commentaire.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByCommentaireContainsSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where commentaire contains
        defaultLigneMouvementStockFiltering("commentaire.contains=" + DEFAULT_COMMENTAIRE, "commentaire.contains=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByCommentaireNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        // Get all the ligneMouvementStockList where commentaire does not contain
        defaultLigneMouvementStockFiltering(
            "commentaire.doesNotContain=" + UPDATED_COMMENTAIRE,
            "commentaire.doesNotContain=" + DEFAULT_COMMENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByMouvementIsEqualToSomething() throws Exception {
        MouvementStock mouvement;
        if (TestUtil.findAll(em, MouvementStock.class).isEmpty()) {
            ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);
            mouvement = MouvementStockResourceIT.createEntity(em);
        } else {
            mouvement = TestUtil.findAll(em, MouvementStock.class).get(0);
        }
        em.persist(mouvement);
        em.flush();
        ligneMouvementStock.setMouvement(mouvement);
        ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);
        Long mouvementId = mouvement.getId();
        // Get all the ligneMouvementStockList where mouvement equals to mouvementId
        defaultLigneMouvementStockShouldBeFound("mouvementId.equals=" + mouvementId);

        // Get all the ligneMouvementStockList where mouvement equals to (mouvementId + 1)
        defaultLigneMouvementStockShouldNotBeFound("mouvementId.equals=" + (mouvementId + 1));
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByProduitIsEqualToSomething() throws Exception {
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);
            produit = ProduitResourceIT.createEntity(em);
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        em.persist(produit);
        em.flush();
        ligneMouvementStock.setProduit(produit);
        ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);
        Long produitId = produit.getId();
        // Get all the ligneMouvementStockList where produit equals to produitId
        defaultLigneMouvementStockShouldBeFound("produitId.equals=" + produitId);

        // Get all the ligneMouvementStockList where produit equals to (produitId + 1)
        defaultLigneMouvementStockShouldNotBeFound("produitId.equals=" + (produitId + 1));
    }

    @Test
    @Transactional
    void getAllLigneMouvementStocksByDepotIsEqualToSomething() throws Exception {
        DepotStock depot;
        if (TestUtil.findAll(em, DepotStock.class).isEmpty()) {
            ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);
            depot = DepotStockResourceIT.createEntity(em);
        } else {
            depot = TestUtil.findAll(em, DepotStock.class).get(0);
        }
        em.persist(depot);
        em.flush();
        ligneMouvementStock.setDepot(depot);
        ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);
        Long depotId = depot.getId();
        // Get all the ligneMouvementStockList where depot equals to depotId
        defaultLigneMouvementStockShouldBeFound("depotId.equals=" + depotId);

        // Get all the ligneMouvementStockList where depot equals to (depotId + 1)
        defaultLigneMouvementStockShouldNotBeFound("depotId.equals=" + (depotId + 1));
    }

    private void defaultLigneMouvementStockFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLigneMouvementStockShouldBeFound(shouldBeFound);
        defaultLigneMouvementStockShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLigneMouvementStockShouldBeFound(String filter) throws Exception {
        restLigneMouvementStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligneMouvementStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantite").value(hasItem(sameNumber(DEFAULT_QUANTITE))))
            .andExpect(jsonPath("$.[*].stockAvant").value(hasItem(sameNumber(DEFAULT_STOCK_AVANT))))
            .andExpect(jsonPath("$.[*].stockApres").value(hasItem(sameNumber(DEFAULT_STOCK_APRES))))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));

        // Check, that the count call also returns 1
        restLigneMouvementStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLigneMouvementStockShouldNotBeFound(String filter) throws Exception {
        restLigneMouvementStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLigneMouvementStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLigneMouvementStock() throws Exception {
        // Get the ligneMouvementStock
        restLigneMouvementStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLigneMouvementStock() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneMouvementStock
        LigneMouvementStock updatedLigneMouvementStock = ligneMouvementStockRepository.findById(ligneMouvementStock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLigneMouvementStock are not directly saved in db
        em.detach(updatedLigneMouvementStock);
        updatedLigneMouvementStock
            .quantite(UPDATED_QUANTITE)
            .stockAvant(UPDATED_STOCK_AVANT)
            .stockApres(UPDATED_STOCK_APRES)
            .commentaire(UPDATED_COMMENTAIRE);
        LigneMouvementStockDTO ligneMouvementStockDTO = ligneMouvementStockMapper.toDto(updatedLigneMouvementStock);

        restLigneMouvementStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneMouvementStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneMouvementStockDTO))
            )
            .andExpect(status().isOk());

        // Validate the LigneMouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLigneMouvementStockToMatchAllProperties(updatedLigneMouvementStock);
    }

    @Test
    @Transactional
    void putNonExistingLigneMouvementStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneMouvementStock.setId(longCount.incrementAndGet());

        // Create the LigneMouvementStock
        LigneMouvementStockDTO ligneMouvementStockDTO = ligneMouvementStockMapper.toDto(ligneMouvementStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneMouvementStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneMouvementStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneMouvementStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneMouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLigneMouvementStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneMouvementStock.setId(longCount.incrementAndGet());

        // Create the LigneMouvementStock
        LigneMouvementStockDTO ligneMouvementStockDTO = ligneMouvementStockMapper.toDto(ligneMouvementStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneMouvementStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneMouvementStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneMouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLigneMouvementStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneMouvementStock.setId(longCount.incrementAndGet());

        // Create the LigneMouvementStock
        LigneMouvementStockDTO ligneMouvementStockDTO = ligneMouvementStockMapper.toDto(ligneMouvementStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneMouvementStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneMouvementStockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneMouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLigneMouvementStockWithPatch() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneMouvementStock using partial update
        LigneMouvementStock partialUpdatedLigneMouvementStock = new LigneMouvementStock();
        partialUpdatedLigneMouvementStock.setId(ligneMouvementStock.getId());

        partialUpdatedLigneMouvementStock.quantite(UPDATED_QUANTITE).stockApres(UPDATED_STOCK_APRES);

        restLigneMouvementStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneMouvementStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneMouvementStock))
            )
            .andExpect(status().isOk());

        // Validate the LigneMouvementStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneMouvementStockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLigneMouvementStock, ligneMouvementStock),
            getPersistedLigneMouvementStock(ligneMouvementStock)
        );
    }

    @Test
    @Transactional
    void fullUpdateLigneMouvementStockWithPatch() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneMouvementStock using partial update
        LigneMouvementStock partialUpdatedLigneMouvementStock = new LigneMouvementStock();
        partialUpdatedLigneMouvementStock.setId(ligneMouvementStock.getId());

        partialUpdatedLigneMouvementStock
            .quantite(UPDATED_QUANTITE)
            .stockAvant(UPDATED_STOCK_AVANT)
            .stockApres(UPDATED_STOCK_APRES)
            .commentaire(UPDATED_COMMENTAIRE);

        restLigneMouvementStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneMouvementStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneMouvementStock))
            )
            .andExpect(status().isOk());

        // Validate the LigneMouvementStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneMouvementStockUpdatableFieldsEquals(
            partialUpdatedLigneMouvementStock,
            getPersistedLigneMouvementStock(partialUpdatedLigneMouvementStock)
        );
    }

    @Test
    @Transactional
    void patchNonExistingLigneMouvementStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneMouvementStock.setId(longCount.incrementAndGet());

        // Create the LigneMouvementStock
        LigneMouvementStockDTO ligneMouvementStockDTO = ligneMouvementStockMapper.toDto(ligneMouvementStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneMouvementStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ligneMouvementStockDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneMouvementStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneMouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLigneMouvementStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneMouvementStock.setId(longCount.incrementAndGet());

        // Create the LigneMouvementStock
        LigneMouvementStockDTO ligneMouvementStockDTO = ligneMouvementStockMapper.toDto(ligneMouvementStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneMouvementStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneMouvementStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneMouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLigneMouvementStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneMouvementStock.setId(longCount.incrementAndGet());

        // Create the LigneMouvementStock
        LigneMouvementStockDTO ligneMouvementStockDTO = ligneMouvementStockMapper.toDto(ligneMouvementStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneMouvementStockMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ligneMouvementStockDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneMouvementStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLigneMouvementStock() throws Exception {
        // Initialize the database
        insertedLigneMouvementStock = ligneMouvementStockRepository.saveAndFlush(ligneMouvementStock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ligneMouvementStock
        restLigneMouvementStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, ligneMouvementStock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ligneMouvementStockRepository.count();
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

    protected LigneMouvementStock getPersistedLigneMouvementStock(LigneMouvementStock ligneMouvementStock) {
        return ligneMouvementStockRepository.findById(ligneMouvementStock.getId()).orElseThrow();
    }

    protected void assertPersistedLigneMouvementStockToMatchAllProperties(LigneMouvementStock expectedLigneMouvementStock) {
        assertLigneMouvementStockAllPropertiesEquals(
            expectedLigneMouvementStock,
            getPersistedLigneMouvementStock(expectedLigneMouvementStock)
        );
    }

    protected void assertPersistedLigneMouvementStockToMatchUpdatableProperties(LigneMouvementStock expectedLigneMouvementStock) {
        assertLigneMouvementStockAllUpdatablePropertiesEquals(
            expectedLigneMouvementStock,
            getPersistedLigneMouvementStock(expectedLigneMouvementStock)
        );
    }
}
