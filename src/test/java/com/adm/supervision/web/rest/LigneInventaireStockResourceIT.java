package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.LigneInventaireStockAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.InventaireStock;
import com.adm.supervision.domain.LigneInventaireStock;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.repository.LigneInventaireStockRepository;
import com.adm.supervision.service.LigneInventaireStockService;
import com.adm.supervision.service.dto.LigneInventaireStockDTO;
import com.adm.supervision.service.mapper.LigneInventaireStockMapper;
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
 * Integration tests for the {@link LigneInventaireStockResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LigneInventaireStockResourceIT {

    private static final BigDecimal DEFAULT_QUANTITE_THEORIQUE = new BigDecimal(0);
    private static final BigDecimal UPDATED_QUANTITE_THEORIQUE = new BigDecimal(1);
    private static final BigDecimal SMALLER_QUANTITE_THEORIQUE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_QUANTITE_COMPTEE = new BigDecimal(0);
    private static final BigDecimal UPDATED_QUANTITE_COMPTEE = new BigDecimal(1);
    private static final BigDecimal SMALLER_QUANTITE_COMPTEE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_ECART = new BigDecimal(1);
    private static final BigDecimal UPDATED_ECART = new BigDecimal(2);
    private static final BigDecimal SMALLER_ECART = new BigDecimal(1 - 1);

    private static final String DEFAULT_COMMENTAIRE = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ligne-inventaire-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LigneInventaireStockRepository ligneInventaireStockRepository;

    @Mock
    private LigneInventaireStockRepository ligneInventaireStockRepositoryMock;

    @Autowired
    private LigneInventaireStockMapper ligneInventaireStockMapper;

    @Mock
    private LigneInventaireStockService ligneInventaireStockServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLigneInventaireStockMockMvc;

    private LigneInventaireStock ligneInventaireStock;

    private LigneInventaireStock insertedLigneInventaireStock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneInventaireStock createEntity(EntityManager em) {
        LigneInventaireStock ligneInventaireStock = new LigneInventaireStock()
            .quantiteTheorique(DEFAULT_QUANTITE_THEORIQUE)
            .quantiteComptee(DEFAULT_QUANTITE_COMPTEE)
            .ecart(DEFAULT_ECART)
            .commentaire(DEFAULT_COMMENTAIRE);
        // Add required entity
        InventaireStock inventaireStock;
        if (TestUtil.findAll(em, InventaireStock.class).isEmpty()) {
            inventaireStock = InventaireStockResourceIT.createEntity(em);
            em.persist(inventaireStock);
            em.flush();
        } else {
            inventaireStock = TestUtil.findAll(em, InventaireStock.class).get(0);
        }
        ligneInventaireStock.setInventaire(inventaireStock);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        ligneInventaireStock.setProduit(produit);
        return ligneInventaireStock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneInventaireStock createUpdatedEntity(EntityManager em) {
        LigneInventaireStock updatedLigneInventaireStock = new LigneInventaireStock()
            .quantiteTheorique(UPDATED_QUANTITE_THEORIQUE)
            .quantiteComptee(UPDATED_QUANTITE_COMPTEE)
            .ecart(UPDATED_ECART)
            .commentaire(UPDATED_COMMENTAIRE);
        // Add required entity
        InventaireStock inventaireStock;
        if (TestUtil.findAll(em, InventaireStock.class).isEmpty()) {
            inventaireStock = InventaireStockResourceIT.createUpdatedEntity(em);
            em.persist(inventaireStock);
            em.flush();
        } else {
            inventaireStock = TestUtil.findAll(em, InventaireStock.class).get(0);
        }
        updatedLigneInventaireStock.setInventaire(inventaireStock);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createUpdatedEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        updatedLigneInventaireStock.setProduit(produit);
        return updatedLigneInventaireStock;
    }

    @BeforeEach
    void initTest() {
        ligneInventaireStock = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLigneInventaireStock != null) {
            ligneInventaireStockRepository.delete(insertedLigneInventaireStock);
            insertedLigneInventaireStock = null;
        }
    }

    @Test
    @Transactional
    void createLigneInventaireStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LigneInventaireStock
        LigneInventaireStockDTO ligneInventaireStockDTO = ligneInventaireStockMapper.toDto(ligneInventaireStock);
        var returnedLigneInventaireStockDTO = om.readValue(
            restLigneInventaireStockMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneInventaireStockDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LigneInventaireStockDTO.class
        );

        // Validate the LigneInventaireStock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLigneInventaireStock = ligneInventaireStockMapper.toEntity(returnedLigneInventaireStockDTO);
        assertLigneInventaireStockUpdatableFieldsEquals(
            returnedLigneInventaireStock,
            getPersistedLigneInventaireStock(returnedLigneInventaireStock)
        );

        insertedLigneInventaireStock = returnedLigneInventaireStock;
    }

    @Test
    @Transactional
    void createLigneInventaireStockWithExistingId() throws Exception {
        // Create the LigneInventaireStock with an existing ID
        ligneInventaireStock.setId(1L);
        LigneInventaireStockDTO ligneInventaireStockDTO = ligneInventaireStockMapper.toDto(ligneInventaireStock);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLigneInventaireStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneInventaireStockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LigneInventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantiteTheoriqueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ligneInventaireStock.setQuantiteTheorique(null);

        // Create the LigneInventaireStock, which fails.
        LigneInventaireStockDTO ligneInventaireStockDTO = ligneInventaireStockMapper.toDto(ligneInventaireStock);

        restLigneInventaireStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneInventaireStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantiteCompteeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ligneInventaireStock.setQuantiteComptee(null);

        // Create the LigneInventaireStock, which fails.
        LigneInventaireStockDTO ligneInventaireStockDTO = ligneInventaireStockMapper.toDto(ligneInventaireStock);

        restLigneInventaireStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneInventaireStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocks() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList
        restLigneInventaireStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligneInventaireStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantiteTheorique").value(hasItem(sameNumber(DEFAULT_QUANTITE_THEORIQUE))))
            .andExpect(jsonPath("$.[*].quantiteComptee").value(hasItem(sameNumber(DEFAULT_QUANTITE_COMPTEE))))
            .andExpect(jsonPath("$.[*].ecart").value(hasItem(sameNumber(DEFAULT_ECART))))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLigneInventaireStocksWithEagerRelationshipsIsEnabled() throws Exception {
        when(ligneInventaireStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLigneInventaireStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ligneInventaireStockServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLigneInventaireStocksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ligneInventaireStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLigneInventaireStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ligneInventaireStockRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLigneInventaireStock() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get the ligneInventaireStock
        restLigneInventaireStockMockMvc
            .perform(get(ENTITY_API_URL_ID, ligneInventaireStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ligneInventaireStock.getId().intValue()))
            .andExpect(jsonPath("$.quantiteTheorique").value(sameNumber(DEFAULT_QUANTITE_THEORIQUE)))
            .andExpect(jsonPath("$.quantiteComptee").value(sameNumber(DEFAULT_QUANTITE_COMPTEE)))
            .andExpect(jsonPath("$.ecart").value(sameNumber(DEFAULT_ECART)))
            .andExpect(jsonPath("$.commentaire").value(DEFAULT_COMMENTAIRE));
    }

    @Test
    @Transactional
    void getLigneInventaireStocksByIdFiltering() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        Long id = ligneInventaireStock.getId();

        defaultLigneInventaireStockFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLigneInventaireStockFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLigneInventaireStockFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteTheoriqueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteTheorique equals to
        defaultLigneInventaireStockFiltering(
            "quantiteTheorique.equals=" + DEFAULT_QUANTITE_THEORIQUE,
            "quantiteTheorique.equals=" + UPDATED_QUANTITE_THEORIQUE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteTheoriqueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteTheorique in
        defaultLigneInventaireStockFiltering(
            "quantiteTheorique.in=" + DEFAULT_QUANTITE_THEORIQUE + "," + UPDATED_QUANTITE_THEORIQUE,
            "quantiteTheorique.in=" + UPDATED_QUANTITE_THEORIQUE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteTheoriqueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteTheorique is not null
        defaultLigneInventaireStockFiltering("quantiteTheorique.specified=true", "quantiteTheorique.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteTheoriqueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteTheorique is greater than or equal to
        defaultLigneInventaireStockFiltering(
            "quantiteTheorique.greaterThanOrEqual=" + DEFAULT_QUANTITE_THEORIQUE,
            "quantiteTheorique.greaterThanOrEqual=" + UPDATED_QUANTITE_THEORIQUE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteTheoriqueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteTheorique is less than or equal to
        defaultLigneInventaireStockFiltering(
            "quantiteTheorique.lessThanOrEqual=" + DEFAULT_QUANTITE_THEORIQUE,
            "quantiteTheorique.lessThanOrEqual=" + SMALLER_QUANTITE_THEORIQUE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteTheoriqueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteTheorique is less than
        defaultLigneInventaireStockFiltering(
            "quantiteTheorique.lessThan=" + UPDATED_QUANTITE_THEORIQUE,
            "quantiteTheorique.lessThan=" + DEFAULT_QUANTITE_THEORIQUE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteTheoriqueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteTheorique is greater than
        defaultLigneInventaireStockFiltering(
            "quantiteTheorique.greaterThan=" + SMALLER_QUANTITE_THEORIQUE,
            "quantiteTheorique.greaterThan=" + DEFAULT_QUANTITE_THEORIQUE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteCompteeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteComptee equals to
        defaultLigneInventaireStockFiltering(
            "quantiteComptee.equals=" + DEFAULT_QUANTITE_COMPTEE,
            "quantiteComptee.equals=" + UPDATED_QUANTITE_COMPTEE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteCompteeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteComptee in
        defaultLigneInventaireStockFiltering(
            "quantiteComptee.in=" + DEFAULT_QUANTITE_COMPTEE + "," + UPDATED_QUANTITE_COMPTEE,
            "quantiteComptee.in=" + UPDATED_QUANTITE_COMPTEE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteCompteeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteComptee is not null
        defaultLigneInventaireStockFiltering("quantiteComptee.specified=true", "quantiteComptee.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteCompteeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteComptee is greater than or equal to
        defaultLigneInventaireStockFiltering(
            "quantiteComptee.greaterThanOrEqual=" + DEFAULT_QUANTITE_COMPTEE,
            "quantiteComptee.greaterThanOrEqual=" + UPDATED_QUANTITE_COMPTEE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteCompteeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteComptee is less than or equal to
        defaultLigneInventaireStockFiltering(
            "quantiteComptee.lessThanOrEqual=" + DEFAULT_QUANTITE_COMPTEE,
            "quantiteComptee.lessThanOrEqual=" + SMALLER_QUANTITE_COMPTEE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteCompteeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteComptee is less than
        defaultLigneInventaireStockFiltering(
            "quantiteComptee.lessThan=" + UPDATED_QUANTITE_COMPTEE,
            "quantiteComptee.lessThan=" + DEFAULT_QUANTITE_COMPTEE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByQuantiteCompteeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where quantiteComptee is greater than
        defaultLigneInventaireStockFiltering(
            "quantiteComptee.greaterThan=" + SMALLER_QUANTITE_COMPTEE,
            "quantiteComptee.greaterThan=" + DEFAULT_QUANTITE_COMPTEE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByEcartIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where ecart equals to
        defaultLigneInventaireStockFiltering("ecart.equals=" + DEFAULT_ECART, "ecart.equals=" + UPDATED_ECART);
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByEcartIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where ecart in
        defaultLigneInventaireStockFiltering("ecart.in=" + DEFAULT_ECART + "," + UPDATED_ECART, "ecart.in=" + UPDATED_ECART);
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByEcartIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where ecart is not null
        defaultLigneInventaireStockFiltering("ecart.specified=true", "ecart.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByEcartIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where ecart is greater than or equal to
        defaultLigneInventaireStockFiltering("ecart.greaterThanOrEqual=" + DEFAULT_ECART, "ecart.greaterThanOrEqual=" + UPDATED_ECART);
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByEcartIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where ecart is less than or equal to
        defaultLigneInventaireStockFiltering("ecart.lessThanOrEqual=" + DEFAULT_ECART, "ecart.lessThanOrEqual=" + SMALLER_ECART);
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByEcartIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where ecart is less than
        defaultLigneInventaireStockFiltering("ecart.lessThan=" + UPDATED_ECART, "ecart.lessThan=" + DEFAULT_ECART);
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByEcartIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where ecart is greater than
        defaultLigneInventaireStockFiltering("ecart.greaterThan=" + SMALLER_ECART, "ecart.greaterThan=" + DEFAULT_ECART);
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByCommentaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where commentaire equals to
        defaultLigneInventaireStockFiltering("commentaire.equals=" + DEFAULT_COMMENTAIRE, "commentaire.equals=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByCommentaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where commentaire in
        defaultLigneInventaireStockFiltering(
            "commentaire.in=" + DEFAULT_COMMENTAIRE + "," + UPDATED_COMMENTAIRE,
            "commentaire.in=" + UPDATED_COMMENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByCommentaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where commentaire is not null
        defaultLigneInventaireStockFiltering("commentaire.specified=true", "commentaire.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByCommentaireContainsSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where commentaire contains
        defaultLigneInventaireStockFiltering("commentaire.contains=" + DEFAULT_COMMENTAIRE, "commentaire.contains=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByCommentaireNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        // Get all the ligneInventaireStockList where commentaire does not contain
        defaultLigneInventaireStockFiltering(
            "commentaire.doesNotContain=" + UPDATED_COMMENTAIRE,
            "commentaire.doesNotContain=" + DEFAULT_COMMENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByInventaireIsEqualToSomething() throws Exception {
        InventaireStock inventaire;
        if (TestUtil.findAll(em, InventaireStock.class).isEmpty()) {
            ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);
            inventaire = InventaireStockResourceIT.createEntity(em);
        } else {
            inventaire = TestUtil.findAll(em, InventaireStock.class).get(0);
        }
        em.persist(inventaire);
        em.flush();
        ligneInventaireStock.setInventaire(inventaire);
        ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);
        Long inventaireId = inventaire.getId();
        // Get all the ligneInventaireStockList where inventaire equals to inventaireId
        defaultLigneInventaireStockShouldBeFound("inventaireId.equals=" + inventaireId);

        // Get all the ligneInventaireStockList where inventaire equals to (inventaireId + 1)
        defaultLigneInventaireStockShouldNotBeFound("inventaireId.equals=" + (inventaireId + 1));
    }

    @Test
    @Transactional
    void getAllLigneInventaireStocksByProduitIsEqualToSomething() throws Exception {
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);
            produit = ProduitResourceIT.createEntity(em);
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        em.persist(produit);
        em.flush();
        ligneInventaireStock.setProduit(produit);
        ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);
        Long produitId = produit.getId();
        // Get all the ligneInventaireStockList where produit equals to produitId
        defaultLigneInventaireStockShouldBeFound("produitId.equals=" + produitId);

        // Get all the ligneInventaireStockList where produit equals to (produitId + 1)
        defaultLigneInventaireStockShouldNotBeFound("produitId.equals=" + (produitId + 1));
    }

    private void defaultLigneInventaireStockFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLigneInventaireStockShouldBeFound(shouldBeFound);
        defaultLigneInventaireStockShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLigneInventaireStockShouldBeFound(String filter) throws Exception {
        restLigneInventaireStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligneInventaireStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantiteTheorique").value(hasItem(sameNumber(DEFAULT_QUANTITE_THEORIQUE))))
            .andExpect(jsonPath("$.[*].quantiteComptee").value(hasItem(sameNumber(DEFAULT_QUANTITE_COMPTEE))))
            .andExpect(jsonPath("$.[*].ecart").value(hasItem(sameNumber(DEFAULT_ECART))))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));

        // Check, that the count call also returns 1
        restLigneInventaireStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLigneInventaireStockShouldNotBeFound(String filter) throws Exception {
        restLigneInventaireStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLigneInventaireStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLigneInventaireStock() throws Exception {
        // Get the ligneInventaireStock
        restLigneInventaireStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLigneInventaireStock() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneInventaireStock
        LigneInventaireStock updatedLigneInventaireStock = ligneInventaireStockRepository
            .findById(ligneInventaireStock.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedLigneInventaireStock are not directly saved in db
        em.detach(updatedLigneInventaireStock);
        updatedLigneInventaireStock
            .quantiteTheorique(UPDATED_QUANTITE_THEORIQUE)
            .quantiteComptee(UPDATED_QUANTITE_COMPTEE)
            .ecart(UPDATED_ECART)
            .commentaire(UPDATED_COMMENTAIRE);
        LigneInventaireStockDTO ligneInventaireStockDTO = ligneInventaireStockMapper.toDto(updatedLigneInventaireStock);

        restLigneInventaireStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneInventaireStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneInventaireStockDTO))
            )
            .andExpect(status().isOk());

        // Validate the LigneInventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLigneInventaireStockToMatchAllProperties(updatedLigneInventaireStock);
    }

    @Test
    @Transactional
    void putNonExistingLigneInventaireStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneInventaireStock.setId(longCount.incrementAndGet());

        // Create the LigneInventaireStock
        LigneInventaireStockDTO ligneInventaireStockDTO = ligneInventaireStockMapper.toDto(ligneInventaireStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneInventaireStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneInventaireStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneInventaireStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneInventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLigneInventaireStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneInventaireStock.setId(longCount.incrementAndGet());

        // Create the LigneInventaireStock
        LigneInventaireStockDTO ligneInventaireStockDTO = ligneInventaireStockMapper.toDto(ligneInventaireStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneInventaireStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneInventaireStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneInventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLigneInventaireStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneInventaireStock.setId(longCount.incrementAndGet());

        // Create the LigneInventaireStock
        LigneInventaireStockDTO ligneInventaireStockDTO = ligneInventaireStockMapper.toDto(ligneInventaireStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneInventaireStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneInventaireStockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneInventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLigneInventaireStockWithPatch() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneInventaireStock using partial update
        LigneInventaireStock partialUpdatedLigneInventaireStock = new LigneInventaireStock();
        partialUpdatedLigneInventaireStock.setId(ligneInventaireStock.getId());

        partialUpdatedLigneInventaireStock.quantiteTheorique(UPDATED_QUANTITE_THEORIQUE).quantiteComptee(UPDATED_QUANTITE_COMPTEE);

        restLigneInventaireStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneInventaireStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneInventaireStock))
            )
            .andExpect(status().isOk());

        // Validate the LigneInventaireStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneInventaireStockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLigneInventaireStock, ligneInventaireStock),
            getPersistedLigneInventaireStock(ligneInventaireStock)
        );
    }

    @Test
    @Transactional
    void fullUpdateLigneInventaireStockWithPatch() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneInventaireStock using partial update
        LigneInventaireStock partialUpdatedLigneInventaireStock = new LigneInventaireStock();
        partialUpdatedLigneInventaireStock.setId(ligneInventaireStock.getId());

        partialUpdatedLigneInventaireStock
            .quantiteTheorique(UPDATED_QUANTITE_THEORIQUE)
            .quantiteComptee(UPDATED_QUANTITE_COMPTEE)
            .ecart(UPDATED_ECART)
            .commentaire(UPDATED_COMMENTAIRE);

        restLigneInventaireStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneInventaireStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneInventaireStock))
            )
            .andExpect(status().isOk());

        // Validate the LigneInventaireStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneInventaireStockUpdatableFieldsEquals(
            partialUpdatedLigneInventaireStock,
            getPersistedLigneInventaireStock(partialUpdatedLigneInventaireStock)
        );
    }

    @Test
    @Transactional
    void patchNonExistingLigneInventaireStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneInventaireStock.setId(longCount.incrementAndGet());

        // Create the LigneInventaireStock
        LigneInventaireStockDTO ligneInventaireStockDTO = ligneInventaireStockMapper.toDto(ligneInventaireStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneInventaireStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ligneInventaireStockDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneInventaireStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneInventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLigneInventaireStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneInventaireStock.setId(longCount.incrementAndGet());

        // Create the LigneInventaireStock
        LigneInventaireStockDTO ligneInventaireStockDTO = ligneInventaireStockMapper.toDto(ligneInventaireStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneInventaireStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneInventaireStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneInventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLigneInventaireStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneInventaireStock.setId(longCount.incrementAndGet());

        // Create the LigneInventaireStock
        LigneInventaireStockDTO ligneInventaireStockDTO = ligneInventaireStockMapper.toDto(ligneInventaireStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneInventaireStockMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ligneInventaireStockDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneInventaireStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLigneInventaireStock() throws Exception {
        // Initialize the database
        insertedLigneInventaireStock = ligneInventaireStockRepository.saveAndFlush(ligneInventaireStock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ligneInventaireStock
        restLigneInventaireStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, ligneInventaireStock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ligneInventaireStockRepository.count();
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

    protected LigneInventaireStock getPersistedLigneInventaireStock(LigneInventaireStock ligneInventaireStock) {
        return ligneInventaireStockRepository.findById(ligneInventaireStock.getId()).orElseThrow();
    }

    protected void assertPersistedLigneInventaireStockToMatchAllProperties(LigneInventaireStock expectedLigneInventaireStock) {
        assertLigneInventaireStockAllPropertiesEquals(
            expectedLigneInventaireStock,
            getPersistedLigneInventaireStock(expectedLigneInventaireStock)
        );
    }

    protected void assertPersistedLigneInventaireStockToMatchUpdatableProperties(LigneInventaireStock expectedLigneInventaireStock) {
        assertLigneInventaireStockAllUpdatablePropertiesEquals(
            expectedLigneInventaireStock,
            getPersistedLigneInventaireStock(expectedLigneInventaireStock)
        );
    }
}
