package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.DepotStockAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.DepotStock;
import com.adm.supervision.repository.DepotStockRepository;
import com.adm.supervision.service.DepotStockService;
import com.adm.supervision.service.dto.DepotStockDTO;
import com.adm.supervision.service.mapper.DepotStockMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link DepotStockResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DepotStockResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_EMPLACEMENT = "AAAAAAAAAA";
    private static final String UPDATED_EMPLACEMENT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final String ENTITY_API_URL = "/api/depot-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DepotStockRepository depotStockRepository;

    @Mock
    private DepotStockRepository depotStockRepositoryMock;

    @Autowired
    private DepotStockMapper depotStockMapper;

    @Mock
    private DepotStockService depotStockServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDepotStockMockMvc;

    private DepotStock depotStock;

    private DepotStock insertedDepotStock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DepotStock createEntity(EntityManager em) {
        DepotStock depotStock = new DepotStock()
            .code(DEFAULT_CODE)
            .libelle(DEFAULT_LIBELLE)
            .emplacement(DEFAULT_EMPLACEMENT)
            .actif(DEFAULT_ACTIF);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        depotStock.setBoutique(boutique);
        return depotStock;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DepotStock createUpdatedEntity(EntityManager em) {
        DepotStock updatedDepotStock = new DepotStock()
            .code(UPDATED_CODE)
            .libelle(UPDATED_LIBELLE)
            .emplacement(UPDATED_EMPLACEMENT)
            .actif(UPDATED_ACTIF);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createUpdatedEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        updatedDepotStock.setBoutique(boutique);
        return updatedDepotStock;
    }

    @BeforeEach
    void initTest() {
        depotStock = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDepotStock != null) {
            depotStockRepository.delete(insertedDepotStock);
            insertedDepotStock = null;
        }
    }

    @Test
    @Transactional
    void createDepotStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DepotStock
        DepotStockDTO depotStockDTO = depotStockMapper.toDto(depotStock);
        var returnedDepotStockDTO = om.readValue(
            restDepotStockMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(depotStockDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DepotStockDTO.class
        );

        // Validate the DepotStock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDepotStock = depotStockMapper.toEntity(returnedDepotStockDTO);
        assertDepotStockUpdatableFieldsEquals(returnedDepotStock, getPersistedDepotStock(returnedDepotStock));

        insertedDepotStock = returnedDepotStock;
    }

    @Test
    @Transactional
    void createDepotStockWithExistingId() throws Exception {
        // Create the DepotStock with an existing ID
        depotStock.setId(1L);
        DepotStockDTO depotStockDTO = depotStockMapper.toDto(depotStock);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDepotStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(depotStockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DepotStock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        depotStock.setCode(null);

        // Create the DepotStock, which fails.
        DepotStockDTO depotStockDTO = depotStockMapper.toDto(depotStock);

        restDepotStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(depotStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        depotStock.setLibelle(null);

        // Create the DepotStock, which fails.
        DepotStockDTO depotStockDTO = depotStockMapper.toDto(depotStock);

        restDepotStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(depotStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        depotStock.setActif(null);

        // Create the DepotStock, which fails.
        DepotStockDTO depotStockDTO = depotStockMapper.toDto(depotStock);

        restDepotStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(depotStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDepotStocks() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList
        restDepotStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(depotStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].emplacement").value(hasItem(DEFAULT_EMPLACEMENT)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDepotStocksWithEagerRelationshipsIsEnabled() throws Exception {
        when(depotStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDepotStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(depotStockServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDepotStocksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(depotStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDepotStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(depotStockRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDepotStock() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get the depotStock
        restDepotStockMockMvc
            .perform(get(ENTITY_API_URL_ID, depotStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(depotStock.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.emplacement").value(DEFAULT_EMPLACEMENT))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF));
    }

    @Test
    @Transactional
    void getDepotStocksByIdFiltering() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        Long id = depotStock.getId();

        defaultDepotStockFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDepotStockFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDepotStockFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDepotStocksByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where code equals to
        defaultDepotStockFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDepotStocksByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where code in
        defaultDepotStockFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDepotStocksByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where code is not null
        defaultDepotStockFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllDepotStocksByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where code contains
        defaultDepotStockFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDepotStocksByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where code does not contain
        defaultDepotStockFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllDepotStocksByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where libelle equals to
        defaultDepotStockFiltering("libelle.equals=" + DEFAULT_LIBELLE, "libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllDepotStocksByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where libelle in
        defaultDepotStockFiltering("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE, "libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllDepotStocksByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where libelle is not null
        defaultDepotStockFiltering("libelle.specified=true", "libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllDepotStocksByLibelleContainsSomething() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where libelle contains
        defaultDepotStockFiltering("libelle.contains=" + DEFAULT_LIBELLE, "libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllDepotStocksByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where libelle does not contain
        defaultDepotStockFiltering("libelle.doesNotContain=" + UPDATED_LIBELLE, "libelle.doesNotContain=" + DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void getAllDepotStocksByEmplacementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where emplacement equals to
        defaultDepotStockFiltering("emplacement.equals=" + DEFAULT_EMPLACEMENT, "emplacement.equals=" + UPDATED_EMPLACEMENT);
    }

    @Test
    @Transactional
    void getAllDepotStocksByEmplacementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where emplacement in
        defaultDepotStockFiltering(
            "emplacement.in=" + DEFAULT_EMPLACEMENT + "," + UPDATED_EMPLACEMENT,
            "emplacement.in=" + UPDATED_EMPLACEMENT
        );
    }

    @Test
    @Transactional
    void getAllDepotStocksByEmplacementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where emplacement is not null
        defaultDepotStockFiltering("emplacement.specified=true", "emplacement.specified=false");
    }

    @Test
    @Transactional
    void getAllDepotStocksByEmplacementContainsSomething() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where emplacement contains
        defaultDepotStockFiltering("emplacement.contains=" + DEFAULT_EMPLACEMENT, "emplacement.contains=" + UPDATED_EMPLACEMENT);
    }

    @Test
    @Transactional
    void getAllDepotStocksByEmplacementNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where emplacement does not contain
        defaultDepotStockFiltering(
            "emplacement.doesNotContain=" + UPDATED_EMPLACEMENT,
            "emplacement.doesNotContain=" + DEFAULT_EMPLACEMENT
        );
    }

    @Test
    @Transactional
    void getAllDepotStocksByActifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where actif equals to
        defaultDepotStockFiltering("actif.equals=" + DEFAULT_ACTIF, "actif.equals=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllDepotStocksByActifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where actif in
        defaultDepotStockFiltering("actif.in=" + DEFAULT_ACTIF + "," + UPDATED_ACTIF, "actif.in=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllDepotStocksByActifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        // Get all the depotStockList where actif is not null
        defaultDepotStockFiltering("actif.specified=true", "actif.specified=false");
    }

    @Test
    @Transactional
    void getAllDepotStocksByBoutiqueIsEqualToSomething() throws Exception {
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            depotStockRepository.saveAndFlush(depotStock);
            boutique = BoutiqueResourceIT.createEntity();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutique);
        em.flush();
        depotStock.setBoutique(boutique);
        depotStockRepository.saveAndFlush(depotStock);
        Long boutiqueId = boutique.getId();
        // Get all the depotStockList where boutique equals to boutiqueId
        defaultDepotStockShouldBeFound("boutiqueId.equals=" + boutiqueId);

        // Get all the depotStockList where boutique equals to (boutiqueId + 1)
        defaultDepotStockShouldNotBeFound("boutiqueId.equals=" + (boutiqueId + 1));
    }

    private void defaultDepotStockFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDepotStockShouldBeFound(shouldBeFound);
        defaultDepotStockShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDepotStockShouldBeFound(String filter) throws Exception {
        restDepotStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(depotStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].emplacement").value(hasItem(DEFAULT_EMPLACEMENT)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));

        // Check, that the count call also returns 1
        restDepotStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDepotStockShouldNotBeFound(String filter) throws Exception {
        restDepotStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDepotStockMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDepotStock() throws Exception {
        // Get the depotStock
        restDepotStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDepotStock() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the depotStock
        DepotStock updatedDepotStock = depotStockRepository.findById(depotStock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDepotStock are not directly saved in db
        em.detach(updatedDepotStock);
        updatedDepotStock.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).emplacement(UPDATED_EMPLACEMENT).actif(UPDATED_ACTIF);
        DepotStockDTO depotStockDTO = depotStockMapper.toDto(updatedDepotStock);

        restDepotStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, depotStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(depotStockDTO))
            )
            .andExpect(status().isOk());

        // Validate the DepotStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDepotStockToMatchAllProperties(updatedDepotStock);
    }

    @Test
    @Transactional
    void putNonExistingDepotStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        depotStock.setId(longCount.incrementAndGet());

        // Create the DepotStock
        DepotStockDTO depotStockDTO = depotStockMapper.toDto(depotStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDepotStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, depotStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(depotStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DepotStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDepotStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        depotStock.setId(longCount.incrementAndGet());

        // Create the DepotStock
        DepotStockDTO depotStockDTO = depotStockMapper.toDto(depotStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDepotStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(depotStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DepotStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDepotStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        depotStock.setId(longCount.incrementAndGet());

        // Create the DepotStock
        DepotStockDTO depotStockDTO = depotStockMapper.toDto(depotStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDepotStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(depotStockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DepotStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDepotStockWithPatch() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the depotStock using partial update
        DepotStock partialUpdatedDepotStock = new DepotStock();
        partialUpdatedDepotStock.setId(depotStock.getId());

        partialUpdatedDepotStock.code(UPDATED_CODE).actif(UPDATED_ACTIF);

        restDepotStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDepotStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDepotStock))
            )
            .andExpect(status().isOk());

        // Validate the DepotStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDepotStockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDepotStock, depotStock),
            getPersistedDepotStock(depotStock)
        );
    }

    @Test
    @Transactional
    void fullUpdateDepotStockWithPatch() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the depotStock using partial update
        DepotStock partialUpdatedDepotStock = new DepotStock();
        partialUpdatedDepotStock.setId(depotStock.getId());

        partialUpdatedDepotStock.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).emplacement(UPDATED_EMPLACEMENT).actif(UPDATED_ACTIF);

        restDepotStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDepotStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDepotStock))
            )
            .andExpect(status().isOk());

        // Validate the DepotStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDepotStockUpdatableFieldsEquals(partialUpdatedDepotStock, getPersistedDepotStock(partialUpdatedDepotStock));
    }

    @Test
    @Transactional
    void patchNonExistingDepotStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        depotStock.setId(longCount.incrementAndGet());

        // Create the DepotStock
        DepotStockDTO depotStockDTO = depotStockMapper.toDto(depotStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDepotStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, depotStockDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(depotStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DepotStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDepotStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        depotStock.setId(longCount.incrementAndGet());

        // Create the DepotStock
        DepotStockDTO depotStockDTO = depotStockMapper.toDto(depotStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDepotStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(depotStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DepotStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDepotStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        depotStock.setId(longCount.incrementAndGet());

        // Create the DepotStock
        DepotStockDTO depotStockDTO = depotStockMapper.toDto(depotStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDepotStockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(depotStockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DepotStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDepotStock() throws Exception {
        // Initialize the database
        insertedDepotStock = depotStockRepository.saveAndFlush(depotStock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the depotStock
        restDepotStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, depotStock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return depotStockRepository.count();
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

    protected DepotStock getPersistedDepotStock(DepotStock depotStock) {
        return depotStockRepository.findById(depotStock.getId()).orElseThrow();
    }

    protected void assertPersistedDepotStockToMatchAllProperties(DepotStock expectedDepotStock) {
        assertDepotStockAllPropertiesEquals(expectedDepotStock, getPersistedDepotStock(expectedDepotStock));
    }

    protected void assertPersistedDepotStockToMatchUpdatableProperties(DepotStock expectedDepotStock) {
        assertDepotStockAllUpdatablePropertiesEquals(expectedDepotStock, getPersistedDepotStock(expectedDepotStock));
    }
}
