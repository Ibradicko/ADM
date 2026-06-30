package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.FamilleArticleAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.FamilleArticle;
import com.adm.supervision.domain.GroupeArticle;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.repository.FamilleArticleRepository;
import com.adm.supervision.service.FamilleArticleService;
import com.adm.supervision.service.dto.FamilleArticleDTO;
import com.adm.supervision.service.mapper.FamilleArticleMapper;
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
 * Integration tests for the {@link FamilleArticleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FamilleArticleResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final StatutGeneral DEFAULT_STATUT = StatutGeneral.ACTIF;
    private static final StatutGeneral UPDATED_STATUT = StatutGeneral.INACTIF;

    private static final String ENTITY_API_URL = "/api/famille-articles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FamilleArticleRepository familleArticleRepository;

    @Mock
    private FamilleArticleRepository familleArticleRepositoryMock;

    @Autowired
    private FamilleArticleMapper familleArticleMapper;

    @Mock
    private FamilleArticleService familleArticleServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFamilleArticleMockMvc;

    private FamilleArticle familleArticle;

    private FamilleArticle insertedFamilleArticle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FamilleArticle createEntity(EntityManager em) {
        FamilleArticle familleArticle = new FamilleArticle().code(DEFAULT_CODE).libelle(DEFAULT_LIBELLE).statut(DEFAULT_STATUT);
        // Add required entity
        GroupeArticle groupeArticle;
        if (TestUtil.findAll(em, GroupeArticle.class).isEmpty()) {
            groupeArticle = GroupeArticleResourceIT.createEntity(em);
            em.persist(groupeArticle);
            em.flush();
        } else {
            groupeArticle = TestUtil.findAll(em, GroupeArticle.class).get(0);
        }
        familleArticle.setGroupeArticle(groupeArticle);
        return familleArticle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FamilleArticle createUpdatedEntity(EntityManager em) {
        FamilleArticle updatedFamilleArticle = new FamilleArticle().code(UPDATED_CODE).libelle(UPDATED_LIBELLE).statut(UPDATED_STATUT);
        // Add required entity
        GroupeArticle groupeArticle;
        if (TestUtil.findAll(em, GroupeArticle.class).isEmpty()) {
            groupeArticle = GroupeArticleResourceIT.createUpdatedEntity(em);
            em.persist(groupeArticle);
            em.flush();
        } else {
            groupeArticle = TestUtil.findAll(em, GroupeArticle.class).get(0);
        }
        updatedFamilleArticle.setGroupeArticle(groupeArticle);
        return updatedFamilleArticle;
    }

    @BeforeEach
    void initTest() {
        familleArticle = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedFamilleArticle != null) {
            familleArticleRepository.delete(insertedFamilleArticle);
            insertedFamilleArticle = null;
        }
    }

    @Test
    @Transactional
    void createFamilleArticle() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FamilleArticle
        FamilleArticleDTO familleArticleDTO = familleArticleMapper.toDto(familleArticle);
        var returnedFamilleArticleDTO = om.readValue(
            restFamilleArticleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(familleArticleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FamilleArticleDTO.class
        );

        // Validate the FamilleArticle in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFamilleArticle = familleArticleMapper.toEntity(returnedFamilleArticleDTO);
        assertFamilleArticleUpdatableFieldsEquals(returnedFamilleArticle, getPersistedFamilleArticle(returnedFamilleArticle));

        insertedFamilleArticle = returnedFamilleArticle;
    }

    @Test
    @Transactional
    void createFamilleArticleWithExistingId() throws Exception {
        // Create the FamilleArticle with an existing ID
        familleArticle.setId(1L);
        FamilleArticleDTO familleArticleDTO = familleArticleMapper.toDto(familleArticle);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFamilleArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(familleArticleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        familleArticle.setCode(null);

        // Create the FamilleArticle, which fails.
        FamilleArticleDTO familleArticleDTO = familleArticleMapper.toDto(familleArticle);

        restFamilleArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(familleArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        familleArticle.setLibelle(null);

        // Create the FamilleArticle, which fails.
        FamilleArticleDTO familleArticleDTO = familleArticleMapper.toDto(familleArticle);

        restFamilleArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(familleArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        familleArticle.setStatut(null);

        // Create the FamilleArticle, which fails.
        FamilleArticleDTO familleArticleDTO = familleArticleMapper.toDto(familleArticle);

        restFamilleArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(familleArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFamilleArticles() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList
        restFamilleArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(familleArticle.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFamilleArticlesWithEagerRelationshipsIsEnabled() throws Exception {
        when(familleArticleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFamilleArticleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(familleArticleServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFamilleArticlesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(familleArticleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFamilleArticleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(familleArticleRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFamilleArticle() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get the familleArticle
        restFamilleArticleMockMvc
            .perform(get(ENTITY_API_URL_ID, familleArticle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(familleArticle.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()));
    }

    @Test
    @Transactional
    void getFamilleArticlesByIdFiltering() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        Long id = familleArticle.getId();

        defaultFamilleArticleFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultFamilleArticleFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultFamilleArticleFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList where code equals to
        defaultFamilleArticleFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList where code in
        defaultFamilleArticleFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList where code is not null
        defaultFamilleArticleFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList where code contains
        defaultFamilleArticleFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList where code does not contain
        defaultFamilleArticleFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList where libelle equals to
        defaultFamilleArticleFiltering("libelle.equals=" + DEFAULT_LIBELLE, "libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList where libelle in
        defaultFamilleArticleFiltering("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE, "libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList where libelle is not null
        defaultFamilleArticleFiltering("libelle.specified=true", "libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByLibelleContainsSomething() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList where libelle contains
        defaultFamilleArticleFiltering("libelle.contains=" + DEFAULT_LIBELLE, "libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList where libelle does not contain
        defaultFamilleArticleFiltering("libelle.doesNotContain=" + UPDATED_LIBELLE, "libelle.doesNotContain=" + DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList where statut equals to
        defaultFamilleArticleFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList where statut in
        defaultFamilleArticleFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        // Get all the familleArticleList where statut is not null
        defaultFamilleArticleFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllFamilleArticlesByGroupeArticleIsEqualToSomething() throws Exception {
        GroupeArticle groupeArticle;
        if (TestUtil.findAll(em, GroupeArticle.class).isEmpty()) {
            familleArticleRepository.saveAndFlush(familleArticle);
            groupeArticle = GroupeArticleResourceIT.createEntity(em);
        } else {
            groupeArticle = TestUtil.findAll(em, GroupeArticle.class).get(0);
        }
        em.persist(groupeArticle);
        em.flush();
        familleArticle.setGroupeArticle(groupeArticle);
        familleArticleRepository.saveAndFlush(familleArticle);
        Long groupeArticleId = groupeArticle.getId();
        // Get all the familleArticleList where groupeArticle equals to groupeArticleId
        defaultFamilleArticleShouldBeFound("groupeArticleId.equals=" + groupeArticleId);

        // Get all the familleArticleList where groupeArticle equals to (groupeArticleId + 1)
        defaultFamilleArticleShouldNotBeFound("groupeArticleId.equals=" + (groupeArticleId + 1));
    }

    private void defaultFamilleArticleFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultFamilleArticleShouldBeFound(shouldBeFound);
        defaultFamilleArticleShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFamilleArticleShouldBeFound(String filter) throws Exception {
        restFamilleArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(familleArticle.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));

        // Check, that the count call also returns 1
        restFamilleArticleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFamilleArticleShouldNotBeFound(String filter) throws Exception {
        restFamilleArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFamilleArticleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFamilleArticle() throws Exception {
        // Get the familleArticle
        restFamilleArticleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFamilleArticle() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the familleArticle
        FamilleArticle updatedFamilleArticle = familleArticleRepository.findById(familleArticle.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFamilleArticle are not directly saved in db
        em.detach(updatedFamilleArticle);
        updatedFamilleArticle.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).statut(UPDATED_STATUT);
        FamilleArticleDTO familleArticleDTO = familleArticleMapper.toDto(updatedFamilleArticle);

        restFamilleArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, familleArticleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(familleArticleDTO))
            )
            .andExpect(status().isOk());

        // Validate the FamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFamilleArticleToMatchAllProperties(updatedFamilleArticle);
    }

    @Test
    @Transactional
    void putNonExistingFamilleArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        familleArticle.setId(longCount.incrementAndGet());

        // Create the FamilleArticle
        FamilleArticleDTO familleArticleDTO = familleArticleMapper.toDto(familleArticle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFamilleArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, familleArticleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(familleArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFamilleArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        familleArticle.setId(longCount.incrementAndGet());

        // Create the FamilleArticle
        FamilleArticleDTO familleArticleDTO = familleArticleMapper.toDto(familleArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFamilleArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(familleArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFamilleArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        familleArticle.setId(longCount.incrementAndGet());

        // Create the FamilleArticle
        FamilleArticleDTO familleArticleDTO = familleArticleMapper.toDto(familleArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFamilleArticleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(familleArticleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFamilleArticleWithPatch() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the familleArticle using partial update
        FamilleArticle partialUpdatedFamilleArticle = new FamilleArticle();
        partialUpdatedFamilleArticle.setId(familleArticle.getId());

        partialUpdatedFamilleArticle.code(UPDATED_CODE);

        restFamilleArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFamilleArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFamilleArticle))
            )
            .andExpect(status().isOk());

        // Validate the FamilleArticle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFamilleArticleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFamilleArticle, familleArticle),
            getPersistedFamilleArticle(familleArticle)
        );
    }

    @Test
    @Transactional
    void fullUpdateFamilleArticleWithPatch() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the familleArticle using partial update
        FamilleArticle partialUpdatedFamilleArticle = new FamilleArticle();
        partialUpdatedFamilleArticle.setId(familleArticle.getId());

        partialUpdatedFamilleArticle.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).statut(UPDATED_STATUT);

        restFamilleArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFamilleArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFamilleArticle))
            )
            .andExpect(status().isOk());

        // Validate the FamilleArticle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFamilleArticleUpdatableFieldsEquals(partialUpdatedFamilleArticle, getPersistedFamilleArticle(partialUpdatedFamilleArticle));
    }

    @Test
    @Transactional
    void patchNonExistingFamilleArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        familleArticle.setId(longCount.incrementAndGet());

        // Create the FamilleArticle
        FamilleArticleDTO familleArticleDTO = familleArticleMapper.toDto(familleArticle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFamilleArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, familleArticleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(familleArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFamilleArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        familleArticle.setId(longCount.incrementAndGet());

        // Create the FamilleArticle
        FamilleArticleDTO familleArticleDTO = familleArticleMapper.toDto(familleArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFamilleArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(familleArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFamilleArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        familleArticle.setId(longCount.incrementAndGet());

        // Create the FamilleArticle
        FamilleArticleDTO familleArticleDTO = familleArticleMapper.toDto(familleArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFamilleArticleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(familleArticleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FamilleArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFamilleArticle() throws Exception {
        // Initialize the database
        insertedFamilleArticle = familleArticleRepository.saveAndFlush(familleArticle);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the familleArticle
        restFamilleArticleMockMvc
            .perform(delete(ENTITY_API_URL_ID, familleArticle.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return familleArticleRepository.count();
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

    protected FamilleArticle getPersistedFamilleArticle(FamilleArticle familleArticle) {
        return familleArticleRepository.findById(familleArticle.getId()).orElseThrow();
    }

    protected void assertPersistedFamilleArticleToMatchAllProperties(FamilleArticle expectedFamilleArticle) {
        assertFamilleArticleAllPropertiesEquals(expectedFamilleArticle, getPersistedFamilleArticle(expectedFamilleArticle));
    }

    protected void assertPersistedFamilleArticleToMatchUpdatableProperties(FamilleArticle expectedFamilleArticle) {
        assertFamilleArticleAllUpdatablePropertiesEquals(expectedFamilleArticle, getPersistedFamilleArticle(expectedFamilleArticle));
    }
}
