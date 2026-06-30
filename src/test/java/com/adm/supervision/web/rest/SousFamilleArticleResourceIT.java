package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.SousFamilleArticleAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.FamilleArticle;
import com.adm.supervision.domain.SousFamilleArticle;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.repository.SousFamilleArticleRepository;
import com.adm.supervision.service.SousFamilleArticleService;
import com.adm.supervision.service.dto.SousFamilleArticleDTO;
import com.adm.supervision.service.mapper.SousFamilleArticleMapper;
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
 * Integration tests for the {@link SousFamilleArticleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SousFamilleArticleResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final StatutGeneral DEFAULT_STATUT = StatutGeneral.ACTIF;
    private static final StatutGeneral UPDATED_STATUT = StatutGeneral.INACTIF;

    private static final String ENTITY_API_URL = "/api/sous-famille-articles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SousFamilleArticleRepository sousFamilleArticleRepository;

    @Mock
    private SousFamilleArticleRepository sousFamilleArticleRepositoryMock;

    @Autowired
    private SousFamilleArticleMapper sousFamilleArticleMapper;

    @Mock
    private SousFamilleArticleService sousFamilleArticleServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSousFamilleArticleMockMvc;

    private SousFamilleArticle sousFamilleArticle;

    private SousFamilleArticle insertedSousFamilleArticle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SousFamilleArticle createEntity(EntityManager em) {
        SousFamilleArticle sousFamilleArticle = new SousFamilleArticle().code(DEFAULT_CODE).libelle(DEFAULT_LIBELLE).statut(DEFAULT_STATUT);
        // Add required entity
        FamilleArticle familleArticle;
        if (TestUtil.findAll(em, FamilleArticle.class).isEmpty()) {
            familleArticle = FamilleArticleResourceIT.createEntity(em);
            em.persist(familleArticle);
            em.flush();
        } else {
            familleArticle = TestUtil.findAll(em, FamilleArticle.class).get(0);
        }
        sousFamilleArticle.setFamilleArticle(familleArticle);
        return sousFamilleArticle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SousFamilleArticle createUpdatedEntity(EntityManager em) {
        SousFamilleArticle updatedSousFamilleArticle = new SousFamilleArticle()
            .code(UPDATED_CODE)
            .libelle(UPDATED_LIBELLE)
            .statut(UPDATED_STATUT);
        // Add required entity
        FamilleArticle familleArticle;
        if (TestUtil.findAll(em, FamilleArticle.class).isEmpty()) {
            familleArticle = FamilleArticleResourceIT.createUpdatedEntity(em);
            em.persist(familleArticle);
            em.flush();
        } else {
            familleArticle = TestUtil.findAll(em, FamilleArticle.class).get(0);
        }
        updatedSousFamilleArticle.setFamilleArticle(familleArticle);
        return updatedSousFamilleArticle;
    }

    @BeforeEach
    void initTest() {
        sousFamilleArticle = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedSousFamilleArticle != null) {
            sousFamilleArticleRepository.delete(insertedSousFamilleArticle);
            insertedSousFamilleArticle = null;
        }
    }

    @Test
    @Transactional
    void createSousFamilleArticle() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SousFamilleArticle
        SousFamilleArticleDTO sousFamilleArticleDTO = sousFamilleArticleMapper.toDto(sousFamilleArticle);
        var returnedSousFamilleArticleDTO = om.readValue(
            restSousFamilleArticleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sousFamilleArticleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SousFamilleArticleDTO.class
        );

        // Validate the SousFamilleArticle in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSousFamilleArticle = sousFamilleArticleMapper.toEntity(returnedSousFamilleArticleDTO);
        assertSousFamilleArticleUpdatableFieldsEquals(
            returnedSousFamilleArticle,
            getPersistedSousFamilleArticle(returnedSousFamilleArticle)
        );

        insertedSousFamilleArticle = returnedSousFamilleArticle;
    }

    @Test
    @Transactional
    void createSousFamilleArticleWithExistingId() throws Exception {
        // Create the SousFamilleArticle with an existing ID
        sousFamilleArticle.setId(1L);
        SousFamilleArticleDTO sousFamilleArticleDTO = sousFamilleArticleMapper.toDto(sousFamilleArticle);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSousFamilleArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sousFamilleArticleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SousFamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sousFamilleArticle.setCode(null);

        // Create the SousFamilleArticle, which fails.
        SousFamilleArticleDTO sousFamilleArticleDTO = sousFamilleArticleMapper.toDto(sousFamilleArticle);

        restSousFamilleArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sousFamilleArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sousFamilleArticle.setLibelle(null);

        // Create the SousFamilleArticle, which fails.
        SousFamilleArticleDTO sousFamilleArticleDTO = sousFamilleArticleMapper.toDto(sousFamilleArticle);

        restSousFamilleArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sousFamilleArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        sousFamilleArticle.setStatut(null);

        // Create the SousFamilleArticle, which fails.
        SousFamilleArticleDTO sousFamilleArticleDTO = sousFamilleArticleMapper.toDto(sousFamilleArticle);

        restSousFamilleArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sousFamilleArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSousFamilleArticles() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList
        restSousFamilleArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sousFamilleArticle.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSousFamilleArticlesWithEagerRelationshipsIsEnabled() throws Exception {
        when(sousFamilleArticleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSousFamilleArticleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(sousFamilleArticleServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSousFamilleArticlesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(sousFamilleArticleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSousFamilleArticleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(sousFamilleArticleRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSousFamilleArticle() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get the sousFamilleArticle
        restSousFamilleArticleMockMvc
            .perform(get(ENTITY_API_URL_ID, sousFamilleArticle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sousFamilleArticle.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()));
    }

    @Test
    @Transactional
    void getSousFamilleArticlesByIdFiltering() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        Long id = sousFamilleArticle.getId();

        defaultSousFamilleArticleFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultSousFamilleArticleFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultSousFamilleArticleFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList where code equals to
        defaultSousFamilleArticleFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList where code in
        defaultSousFamilleArticleFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList where code is not null
        defaultSousFamilleArticleFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList where code contains
        defaultSousFamilleArticleFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList where code does not contain
        defaultSousFamilleArticleFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList where libelle equals to
        defaultSousFamilleArticleFiltering("libelle.equals=" + DEFAULT_LIBELLE, "libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList where libelle in
        defaultSousFamilleArticleFiltering("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE, "libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList where libelle is not null
        defaultSousFamilleArticleFiltering("libelle.specified=true", "libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByLibelleContainsSomething() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList where libelle contains
        defaultSousFamilleArticleFiltering("libelle.contains=" + DEFAULT_LIBELLE, "libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList where libelle does not contain
        defaultSousFamilleArticleFiltering("libelle.doesNotContain=" + UPDATED_LIBELLE, "libelle.doesNotContain=" + DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList where statut equals to
        defaultSousFamilleArticleFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList where statut in
        defaultSousFamilleArticleFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        // Get all the sousFamilleArticleList where statut is not null
        defaultSousFamilleArticleFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllSousFamilleArticlesByFamilleArticleIsEqualToSomething() throws Exception {
        FamilleArticle familleArticle;
        if (TestUtil.findAll(em, FamilleArticle.class).isEmpty()) {
            sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);
            familleArticle = FamilleArticleResourceIT.createEntity(em);
        } else {
            familleArticle = TestUtil.findAll(em, FamilleArticle.class).get(0);
        }
        em.persist(familleArticle);
        em.flush();
        sousFamilleArticle.setFamilleArticle(familleArticle);
        sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);
        Long familleArticleId = familleArticle.getId();
        // Get all the sousFamilleArticleList where familleArticle equals to familleArticleId
        defaultSousFamilleArticleShouldBeFound("familleArticleId.equals=" + familleArticleId);

        // Get all the sousFamilleArticleList where familleArticle equals to (familleArticleId + 1)
        defaultSousFamilleArticleShouldNotBeFound("familleArticleId.equals=" + (familleArticleId + 1));
    }

    private void defaultSousFamilleArticleFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultSousFamilleArticleShouldBeFound(shouldBeFound);
        defaultSousFamilleArticleShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSousFamilleArticleShouldBeFound(String filter) throws Exception {
        restSousFamilleArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sousFamilleArticle.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));

        // Check, that the count call also returns 1
        restSousFamilleArticleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSousFamilleArticleShouldNotBeFound(String filter) throws Exception {
        restSousFamilleArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSousFamilleArticleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSousFamilleArticle() throws Exception {
        // Get the sousFamilleArticle
        restSousFamilleArticleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSousFamilleArticle() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sousFamilleArticle
        SousFamilleArticle updatedSousFamilleArticle = sousFamilleArticleRepository.findById(sousFamilleArticle.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSousFamilleArticle are not directly saved in db
        em.detach(updatedSousFamilleArticle);
        updatedSousFamilleArticle.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).statut(UPDATED_STATUT);
        SousFamilleArticleDTO sousFamilleArticleDTO = sousFamilleArticleMapper.toDto(updatedSousFamilleArticle);

        restSousFamilleArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sousFamilleArticleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sousFamilleArticleDTO))
            )
            .andExpect(status().isOk());

        // Validate the SousFamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSousFamilleArticleToMatchAllProperties(updatedSousFamilleArticle);
    }

    @Test
    @Transactional
    void putNonExistingSousFamilleArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sousFamilleArticle.setId(longCount.incrementAndGet());

        // Create the SousFamilleArticle
        SousFamilleArticleDTO sousFamilleArticleDTO = sousFamilleArticleMapper.toDto(sousFamilleArticle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSousFamilleArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sousFamilleArticleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sousFamilleArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SousFamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSousFamilleArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sousFamilleArticle.setId(longCount.incrementAndGet());

        // Create the SousFamilleArticle
        SousFamilleArticleDTO sousFamilleArticleDTO = sousFamilleArticleMapper.toDto(sousFamilleArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSousFamilleArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(sousFamilleArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SousFamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSousFamilleArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sousFamilleArticle.setId(longCount.incrementAndGet());

        // Create the SousFamilleArticle
        SousFamilleArticleDTO sousFamilleArticleDTO = sousFamilleArticleMapper.toDto(sousFamilleArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSousFamilleArticleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(sousFamilleArticleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SousFamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSousFamilleArticleWithPatch() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sousFamilleArticle using partial update
        SousFamilleArticle partialUpdatedSousFamilleArticle = new SousFamilleArticle();
        partialUpdatedSousFamilleArticle.setId(sousFamilleArticle.getId());

        partialUpdatedSousFamilleArticle.code(UPDATED_CODE);

        restSousFamilleArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSousFamilleArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSousFamilleArticle))
            )
            .andExpect(status().isOk());

        // Validate the SousFamilleArticle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSousFamilleArticleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSousFamilleArticle, sousFamilleArticle),
            getPersistedSousFamilleArticle(sousFamilleArticle)
        );
    }

    @Test
    @Transactional
    void fullUpdateSousFamilleArticleWithPatch() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the sousFamilleArticle using partial update
        SousFamilleArticle partialUpdatedSousFamilleArticle = new SousFamilleArticle();
        partialUpdatedSousFamilleArticle.setId(sousFamilleArticle.getId());

        partialUpdatedSousFamilleArticle.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).statut(UPDATED_STATUT);

        restSousFamilleArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSousFamilleArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSousFamilleArticle))
            )
            .andExpect(status().isOk());

        // Validate the SousFamilleArticle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSousFamilleArticleUpdatableFieldsEquals(
            partialUpdatedSousFamilleArticle,
            getPersistedSousFamilleArticle(partialUpdatedSousFamilleArticle)
        );
    }

    @Test
    @Transactional
    void patchNonExistingSousFamilleArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sousFamilleArticle.setId(longCount.incrementAndGet());

        // Create the SousFamilleArticle
        SousFamilleArticleDTO sousFamilleArticleDTO = sousFamilleArticleMapper.toDto(sousFamilleArticle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSousFamilleArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sousFamilleArticleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sousFamilleArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SousFamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSousFamilleArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sousFamilleArticle.setId(longCount.incrementAndGet());

        // Create the SousFamilleArticle
        SousFamilleArticleDTO sousFamilleArticleDTO = sousFamilleArticleMapper.toDto(sousFamilleArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSousFamilleArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(sousFamilleArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SousFamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSousFamilleArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        sousFamilleArticle.setId(longCount.incrementAndGet());

        // Create the SousFamilleArticle
        SousFamilleArticleDTO sousFamilleArticleDTO = sousFamilleArticleMapper.toDto(sousFamilleArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSousFamilleArticleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(sousFamilleArticleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SousFamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSousFamilleArticle() throws Exception {
        // Initialize the database
        insertedSousFamilleArticle = sousFamilleArticleRepository.saveAndFlush(sousFamilleArticle);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the sousFamilleArticle
        restSousFamilleArticleMockMvc
            .perform(delete(ENTITY_API_URL_ID, sousFamilleArticle.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return sousFamilleArticleRepository.count();
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

    protected SousFamilleArticle getPersistedSousFamilleArticle(SousFamilleArticle sousFamilleArticle) {
        return sousFamilleArticleRepository.findById(sousFamilleArticle.getId()).orElseThrow();
    }

    protected void assertPersistedSousFamilleArticleToMatchAllProperties(SousFamilleArticle expectedSousFamilleArticle) {
        assertSousFamilleArticleAllPropertiesEquals(expectedSousFamilleArticle, getPersistedSousFamilleArticle(expectedSousFamilleArticle));
    }

    protected void assertPersistedSousFamilleArticleToMatchUpdatableProperties(SousFamilleArticle expectedSousFamilleArticle) {
        assertSousFamilleArticleAllUpdatablePropertiesEquals(
            expectedSousFamilleArticle,
            getPersistedSousFamilleArticle(expectedSousFamilleArticle)
        );
    }
}
