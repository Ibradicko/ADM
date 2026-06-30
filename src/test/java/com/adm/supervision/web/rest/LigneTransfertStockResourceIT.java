package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.LigneTransfertStockAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.LigneTransfertStock;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.TransfertStock;
import com.adm.supervision.repository.LigneTransfertStockRepository;
import com.adm.supervision.service.LigneTransfertStockService;
import com.adm.supervision.service.dto.LigneTransfertStockDTO;
import com.adm.supervision.service.mapper.LigneTransfertStockMapper;
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
 * Integration tests for the {@link LigneTransfertStockResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LigneTransfertStockResourceIT {

    private static final BigDecimal DEFAULT_QUANTITE = new BigDecimal(0);
    private static final BigDecimal UPDATED_QUANTITE = new BigDecimal(1);
    private static final BigDecimal SMALLER_QUANTITE = new BigDecimal(0 - 1);

    private static final String DEFAULT_COMMENTAIRE = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ligne-transfert-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LigneTransfertStockRepository ligneTransfertStockRepository;

    @Mock
    private LigneTransfertStockRepository ligneTransfertStockRepositoryMock;

    @Autowired
    private LigneTransfertStockMapper ligneTransfertStockMapper;

    @Mock
    private LigneTransfertStockService ligneTransfertStockServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLigneTransfertStockMockMvc;

    private LigneTransfertStock ligneTransfertStock;

    private LigneTransfertStock insertedLigneTransfertStock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneTransfertStock createEntity(EntityManager em) {
        LigneTransfertStock ligneTransfertStock = new LigneTransfertStock().quantite(DEFAULT_QUANTITE).commentaire(DEFAULT_COMMENTAIRE);
        // Add required entity
        TransfertStock transfertStock;
        if (TestUtil.findAll(em, TransfertStock.class).isEmpty()) {
            transfertStock = TransfertStockResourceIT.createEntity(em);
            em.persist(transfertStock);
            em.flush();
        } else {
            transfertStock = TestUtil.findAll(em, TransfertStock.class).get(0);
        }
        ligneTransfertStock.setTransfert(transfertStock);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        ligneTransfertStock.setProduit(produit);
        return ligneTransfertStock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneTransfertStock createUpdatedEntity(EntityManager em) {
        LigneTransfertStock updatedLigneTransfertStock = new LigneTransfertStock()
            .quantite(UPDATED_QUANTITE)
            .commentaire(UPDATED_COMMENTAIRE);
        // Add required entity
        TransfertStock transfertStock;
        if (TestUtil.findAll(em, TransfertStock.class).isEmpty()) {
            transfertStock = TransfertStockResourceIT.createUpdatedEntity(em);
            em.persist(transfertStock);
            em.flush();
        } else {
            transfertStock = TestUtil.findAll(em, TransfertStock.class).get(0);
        }
        updatedLigneTransfertStock.setTransfert(transfertStock);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createUpdatedEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        updatedLigneTransfertStock.setProduit(produit);
        return updatedLigneTransfertStock;
    }

    @BeforeEach
    void initTest() {
        ligneTransfertStock = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLigneTransfertStock != null) {
            ligneTransfertStockRepository.delete(insertedLigneTransfertStock);
            insertedLigneTransfertStock = null;
        }
    }

    @Test
    @Transactional
    void createLigneTransfertStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LigneTransfertStock
        LigneTransfertStockDTO ligneTransfertStockDTO = ligneTransfertStockMapper.toDto(ligneTransfertStock);
        var returnedLigneTransfertStockDTO = om.readValue(
            restLigneTransfertStockMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneTransfertStockDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LigneTransfertStockDTO.class
        );

        // Validate the LigneTransfertStock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLigneTransfertStock = ligneTransfertStockMapper.toEntity(returnedLigneTransfertStockDTO);
        assertLigneTransfertStockUpdatableFieldsEquals(
            returnedLigneTransfertStock,
            getPersistedLigneTransfertStock(returnedLigneTransfertStock)
        );

        insertedLigneTransfertStock = returnedLigneTransfertStock;
    }

    @Test
    @Transactional
    void createLigneTransfertStockWithExistingId() throws Exception {
        // Create the LigneTransfertStock with an existing ID
        ligneTransfertStock.setId(1L);
        LigneTransfertStockDTO ligneTransfertStockDTO = ligneTransfertStockMapper.toDto(ligneTransfertStock);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLigneTransfertStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneTransfertStockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LigneTransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantiteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ligneTransfertStock.setQuantite(null);

        // Create the LigneTransfertStock, which fails.
        LigneTransfertStockDTO ligneTransfertStockDTO = ligneTransfertStockMapper.toDto(ligneTransfertStock);

        restLigneTransfertStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneTransfertStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocks() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get all the ligneTransfertStockList
        restLigneTransfertStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligneTransfertStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantite").value(hasItem(sameNumber(DEFAULT_QUANTITE))))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLigneTransfertStocksWithEagerRelationshipsIsEnabled() throws Exception {
        when(ligneTransfertStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLigneTransfertStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ligneTransfertStockServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLigneTransfertStocksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ligneTransfertStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLigneTransfertStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ligneTransfertStockRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLigneTransfertStock() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get the ligneTransfertStock
        restLigneTransfertStockMockMvc
            .perform(get(ENTITY_API_URL_ID, ligneTransfertStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ligneTransfertStock.getId().intValue()))
            .andExpect(jsonPath("$.quantite").value(sameNumber(DEFAULT_QUANTITE)))
            .andExpect(jsonPath("$.commentaire").value(DEFAULT_COMMENTAIRE));
    }

    @Test
    @Transactional
    void getLigneTransfertStocksByIdFiltering() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        Long id = ligneTransfertStock.getId();

        defaultLigneTransfertStockFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLigneTransfertStockFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLigneTransfertStockFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByQuantiteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get all the ligneTransfertStockList where quantite equals to
        defaultLigneTransfertStockFiltering("quantite.equals=" + DEFAULT_QUANTITE, "quantite.equals=" + UPDATED_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByQuantiteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get all the ligneTransfertStockList where quantite in
        defaultLigneTransfertStockFiltering("quantite.in=" + DEFAULT_QUANTITE + "," + UPDATED_QUANTITE, "quantite.in=" + UPDATED_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByQuantiteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get all the ligneTransfertStockList where quantite is not null
        defaultLigneTransfertStockFiltering("quantite.specified=true", "quantite.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByQuantiteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get all the ligneTransfertStockList where quantite is greater than or equal to
        defaultLigneTransfertStockFiltering(
            "quantite.greaterThanOrEqual=" + DEFAULT_QUANTITE,
            "quantite.greaterThanOrEqual=" + UPDATED_QUANTITE
        );
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByQuantiteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get all the ligneTransfertStockList where quantite is less than or equal to
        defaultLigneTransfertStockFiltering("quantite.lessThanOrEqual=" + DEFAULT_QUANTITE, "quantite.lessThanOrEqual=" + SMALLER_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByQuantiteIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get all the ligneTransfertStockList where quantite is less than
        defaultLigneTransfertStockFiltering("quantite.lessThan=" + UPDATED_QUANTITE, "quantite.lessThan=" + DEFAULT_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByQuantiteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get all the ligneTransfertStockList where quantite is greater than
        defaultLigneTransfertStockFiltering("quantite.greaterThan=" + SMALLER_QUANTITE, "quantite.greaterThan=" + DEFAULT_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByCommentaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get all the ligneTransfertStockList where commentaire equals to
        defaultLigneTransfertStockFiltering("commentaire.equals=" + DEFAULT_COMMENTAIRE, "commentaire.equals=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByCommentaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get all the ligneTransfertStockList where commentaire in
        defaultLigneTransfertStockFiltering(
            "commentaire.in=" + DEFAULT_COMMENTAIRE + "," + UPDATED_COMMENTAIRE,
            "commentaire.in=" + UPDATED_COMMENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByCommentaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get all the ligneTransfertStockList where commentaire is not null
        defaultLigneTransfertStockFiltering("commentaire.specified=true", "commentaire.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByCommentaireContainsSomething() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get all the ligneTransfertStockList where commentaire contains
        defaultLigneTransfertStockFiltering("commentaire.contains=" + DEFAULT_COMMENTAIRE, "commentaire.contains=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByCommentaireNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        // Get all the ligneTransfertStockList where commentaire does not contain
        defaultLigneTransfertStockFiltering(
            "commentaire.doesNotContain=" + UPDATED_COMMENTAIRE,
            "commentaire.doesNotContain=" + DEFAULT_COMMENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByTransfertIsEqualToSomething() throws Exception {
        TransfertStock transfert;
        if (TestUtil.findAll(em, TransfertStock.class).isEmpty()) {
            ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);
            transfert = TransfertStockResourceIT.createEntity(em);
        } else {
            transfert = TestUtil.findAll(em, TransfertStock.class).get(0);
        }
        em.persist(transfert);
        em.flush();
        ligneTransfertStock.setTransfert(transfert);
        ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);
        Long transfertId = transfert.getId();
        // Get all the ligneTransfertStockList where transfert equals to transfertId
        defaultLigneTransfertStockShouldBeFound("transfertId.equals=" + transfertId);

        // Get all the ligneTransfertStockList where transfert equals to (transfertId + 1)
        defaultLigneTransfertStockShouldNotBeFound("transfertId.equals=" + (transfertId + 1));
    }

    @Test
    @Transactional
    void getAllLigneTransfertStocksByProduitIsEqualToSomething() throws Exception {
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);
            produit = ProduitResourceIT.createEntity(em);
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        em.persist(produit);
        em.flush();
        ligneTransfertStock.setProduit(produit);
        ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);
        Long produitId = produit.getId();
        // Get all the ligneTransfertStockList where produit equals to produitId
        defaultLigneTransfertStockShouldBeFound("produitId.equals=" + produitId);

        // Get all the ligneTransfertStockList where produit equals to (produitId + 1)
        defaultLigneTransfertStockShouldNotBeFound("produitId.equals=" + (produitId + 1));
    }

    private void defaultLigneTransfertStockFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLigneTransfertStockShouldBeFound(shouldBeFound);
        defaultLigneTransfertStockShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLigneTransfertStockShouldBeFound(String filter) throws Exception {
        restLigneTransfertStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligneTransfertStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantite").value(hasItem(sameNumber(DEFAULT_QUANTITE))))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));

        // Check, that the count call also returns 1
        restLigneTransfertStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLigneTransfertStockShouldNotBeFound(String filter) throws Exception {
        restLigneTransfertStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLigneTransfertStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLigneTransfertStock() throws Exception {
        // Get the ligneTransfertStock
        restLigneTransfertStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLigneTransfertStock() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneTransfertStock
        LigneTransfertStock updatedLigneTransfertStock = ligneTransfertStockRepository.findById(ligneTransfertStock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLigneTransfertStock are not directly saved in db
        em.detach(updatedLigneTransfertStock);
        updatedLigneTransfertStock.quantite(UPDATED_QUANTITE).commentaire(UPDATED_COMMENTAIRE);
        LigneTransfertStockDTO ligneTransfertStockDTO = ligneTransfertStockMapper.toDto(updatedLigneTransfertStock);

        restLigneTransfertStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneTransfertStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneTransfertStockDTO))
            )
            .andExpect(status().isOk());

        // Validate the LigneTransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLigneTransfertStockToMatchAllProperties(updatedLigneTransfertStock);
    }

    @Test
    @Transactional
    void putNonExistingLigneTransfertStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneTransfertStock.setId(longCount.incrementAndGet());

        // Create the LigneTransfertStock
        LigneTransfertStockDTO ligneTransfertStockDTO = ligneTransfertStockMapper.toDto(ligneTransfertStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneTransfertStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneTransfertStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneTransfertStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneTransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLigneTransfertStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneTransfertStock.setId(longCount.incrementAndGet());

        // Create the LigneTransfertStock
        LigneTransfertStockDTO ligneTransfertStockDTO = ligneTransfertStockMapper.toDto(ligneTransfertStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneTransfertStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneTransfertStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneTransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLigneTransfertStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneTransfertStock.setId(longCount.incrementAndGet());

        // Create the LigneTransfertStock
        LigneTransfertStockDTO ligneTransfertStockDTO = ligneTransfertStockMapper.toDto(ligneTransfertStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneTransfertStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneTransfertStockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneTransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLigneTransfertStockWithPatch() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneTransfertStock using partial update
        LigneTransfertStock partialUpdatedLigneTransfertStock = new LigneTransfertStock();
        partialUpdatedLigneTransfertStock.setId(ligneTransfertStock.getId());

        partialUpdatedLigneTransfertStock.quantite(UPDATED_QUANTITE);

        restLigneTransfertStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneTransfertStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneTransfertStock))
            )
            .andExpect(status().isOk());

        // Validate the LigneTransfertStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneTransfertStockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLigneTransfertStock, ligneTransfertStock),
            getPersistedLigneTransfertStock(ligneTransfertStock)
        );
    }

    @Test
    @Transactional
    void fullUpdateLigneTransfertStockWithPatch() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneTransfertStock using partial update
        LigneTransfertStock partialUpdatedLigneTransfertStock = new LigneTransfertStock();
        partialUpdatedLigneTransfertStock.setId(ligneTransfertStock.getId());

        partialUpdatedLigneTransfertStock.quantite(UPDATED_QUANTITE).commentaire(UPDATED_COMMENTAIRE);

        restLigneTransfertStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneTransfertStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneTransfertStock))
            )
            .andExpect(status().isOk());

        // Validate the LigneTransfertStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneTransfertStockUpdatableFieldsEquals(
            partialUpdatedLigneTransfertStock,
            getPersistedLigneTransfertStock(partialUpdatedLigneTransfertStock)
        );
    }

    @Test
    @Transactional
    void patchNonExistingLigneTransfertStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneTransfertStock.setId(longCount.incrementAndGet());

        // Create the LigneTransfertStock
        LigneTransfertStockDTO ligneTransfertStockDTO = ligneTransfertStockMapper.toDto(ligneTransfertStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneTransfertStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ligneTransfertStockDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneTransfertStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneTransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLigneTransfertStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneTransfertStock.setId(longCount.incrementAndGet());

        // Create the LigneTransfertStock
        LigneTransfertStockDTO ligneTransfertStockDTO = ligneTransfertStockMapper.toDto(ligneTransfertStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneTransfertStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneTransfertStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneTransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLigneTransfertStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneTransfertStock.setId(longCount.incrementAndGet());

        // Create the LigneTransfertStock
        LigneTransfertStockDTO ligneTransfertStockDTO = ligneTransfertStockMapper.toDto(ligneTransfertStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneTransfertStockMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ligneTransfertStockDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneTransfertStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLigneTransfertStock() throws Exception {
        // Initialize the database
        insertedLigneTransfertStock = ligneTransfertStockRepository.saveAndFlush(ligneTransfertStock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ligneTransfertStock
        restLigneTransfertStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, ligneTransfertStock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ligneTransfertStockRepository.count();
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

    protected LigneTransfertStock getPersistedLigneTransfertStock(LigneTransfertStock ligneTransfertStock) {
        return ligneTransfertStockRepository.findById(ligneTransfertStock.getId()).orElseThrow();
    }

    protected void assertPersistedLigneTransfertStockToMatchAllProperties(LigneTransfertStock expectedLigneTransfertStock) {
        assertLigneTransfertStockAllPropertiesEquals(
            expectedLigneTransfertStock,
            getPersistedLigneTransfertStock(expectedLigneTransfertStock)
        );
    }

    protected void assertPersistedLigneTransfertStockToMatchUpdatableProperties(LigneTransfertStock expectedLigneTransfertStock) {
        assertLigneTransfertStockAllUpdatablePropertiesEquals(
            expectedLigneTransfertStock,
            getPersistedLigneTransfertStock(expectedLigneTransfertStock)
        );
    }
}
