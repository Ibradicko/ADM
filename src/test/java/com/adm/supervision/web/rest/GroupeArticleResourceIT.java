package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.GroupeArticleAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.GroupeArticle;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.repository.GroupeArticleRepository;
import com.adm.supervision.service.GroupeArticleService;
import com.adm.supervision.service.dto.GroupeArticleDTO;
import com.adm.supervision.service.mapper.GroupeArticleMapper;
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
 * Integration tests for the {@link GroupeArticleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class GroupeArticleResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final StatutGeneral DEFAULT_STATUT = StatutGeneral.ACTIF;
    private static final StatutGeneral UPDATED_STATUT = StatutGeneral.INACTIF;

    private static final String ENTITY_API_URL = "/api/groupe-articles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GroupeArticleRepository groupeArticleRepository;

    @Mock
    private GroupeArticleRepository groupeArticleRepositoryMock;

    @Autowired
    private GroupeArticleMapper groupeArticleMapper;

    @Mock
    private GroupeArticleService groupeArticleServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGroupeArticleMockMvc;

    private GroupeArticle groupeArticle;

    private GroupeArticle insertedGroupeArticle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GroupeArticle createEntity(EntityManager em) {
        GroupeArticle groupeArticle = new GroupeArticle().code(DEFAULT_CODE).libelle(DEFAULT_LIBELLE).statut(DEFAULT_STATUT);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        groupeArticle.setBoutique(boutique);
        return groupeArticle;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GroupeArticle createUpdatedEntity(EntityManager em) {
        GroupeArticle updatedGroupeArticle = new GroupeArticle().code(UPDATED_CODE).libelle(UPDATED_LIBELLE).statut(UPDATED_STATUT);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createUpdatedEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        updatedGroupeArticle.setBoutique(boutique);
        return updatedGroupeArticle;
    }

    @BeforeEach
    void initTest() {
        groupeArticle = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedGroupeArticle != null) {
            groupeArticleRepository.delete(insertedGroupeArticle);
            insertedGroupeArticle = null;
        }
    }

    @Test
    @Transactional
    void createGroupeArticle() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the GroupeArticle
        GroupeArticleDTO groupeArticleDTO = groupeArticleMapper.toDto(groupeArticle);
        var returnedGroupeArticleDTO = om.readValue(
            restGroupeArticleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(groupeArticleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GroupeArticleDTO.class
        );

        // Validate the GroupeArticle in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedGroupeArticle = groupeArticleMapper.toEntity(returnedGroupeArticleDTO);
        assertGroupeArticleUpdatableFieldsEquals(returnedGroupeArticle, getPersistedGroupeArticle(returnedGroupeArticle));

        insertedGroupeArticle = returnedGroupeArticle;
    }

    @Test
    @Transactional
    void createGroupeArticleWithExistingId() throws Exception {
        // Create the GroupeArticle with an existing ID
        groupeArticle.setId(1L);
        GroupeArticleDTO groupeArticleDTO = groupeArticleMapper.toDto(groupeArticle);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGroupeArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(groupeArticleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the GroupeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        groupeArticle.setCode(null);

        // Create the GroupeArticle, which fails.
        GroupeArticleDTO groupeArticleDTO = groupeArticleMapper.toDto(groupeArticle);

        restGroupeArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(groupeArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        groupeArticle.setLibelle(null);

        // Create the GroupeArticle, which fails.
        GroupeArticleDTO groupeArticleDTO = groupeArticleMapper.toDto(groupeArticle);

        restGroupeArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(groupeArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        groupeArticle.setStatut(null);

        // Create the GroupeArticle, which fails.
        GroupeArticleDTO groupeArticleDTO = groupeArticleMapper.toDto(groupeArticle);

        restGroupeArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(groupeArticleDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGroupeArticles() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList
        restGroupeArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groupeArticle.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGroupeArticlesWithEagerRelationshipsIsEnabled() throws Exception {
        when(groupeArticleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGroupeArticleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(groupeArticleServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllGroupeArticlesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(groupeArticleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restGroupeArticleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(groupeArticleRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getGroupeArticle() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get the groupeArticle
        restGroupeArticleMockMvc
            .perform(get(ENTITY_API_URL_ID, groupeArticle.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(groupeArticle.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()));
    }

    @Test
    @Transactional
    void getGroupeArticlesByIdFiltering() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        Long id = groupeArticle.getId();

        defaultGroupeArticleFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultGroupeArticleFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultGroupeArticleFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList where code equals to
        defaultGroupeArticleFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList where code in
        defaultGroupeArticleFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList where code is not null
        defaultGroupeArticleFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList where code contains
        defaultGroupeArticleFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList where code does not contain
        defaultGroupeArticleFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList where libelle equals to
        defaultGroupeArticleFiltering("libelle.equals=" + DEFAULT_LIBELLE, "libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList where libelle in
        defaultGroupeArticleFiltering("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE, "libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList where libelle is not null
        defaultGroupeArticleFiltering("libelle.specified=true", "libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByLibelleContainsSomething() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList where libelle contains
        defaultGroupeArticleFiltering("libelle.contains=" + DEFAULT_LIBELLE, "libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList where libelle does not contain
        defaultGroupeArticleFiltering("libelle.doesNotContain=" + UPDATED_LIBELLE, "libelle.doesNotContain=" + DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList where statut equals to
        defaultGroupeArticleFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList where statut in
        defaultGroupeArticleFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        // Get all the groupeArticleList where statut is not null
        defaultGroupeArticleFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllGroupeArticlesByBoutiqueIsEqualToSomething() throws Exception {
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            groupeArticleRepository.saveAndFlush(groupeArticle);
            boutique = BoutiqueResourceIT.createEntity();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutique);
        em.flush();
        groupeArticle.setBoutique(boutique);
        groupeArticleRepository.saveAndFlush(groupeArticle);
        Long boutiqueId = boutique.getId();
        // Get all the groupeArticleList where boutique equals to boutiqueId
        defaultGroupeArticleShouldBeFound("boutiqueId.equals=" + boutiqueId);

        // Get all the groupeArticleList where boutique equals to (boutiqueId + 1)
        defaultGroupeArticleShouldNotBeFound("boutiqueId.equals=" + (boutiqueId + 1));
    }

    private void defaultGroupeArticleFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultGroupeArticleShouldBeFound(shouldBeFound);
        defaultGroupeArticleShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultGroupeArticleShouldBeFound(String filter) throws Exception {
        restGroupeArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groupeArticle.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));

        // Check, that the count call also returns 1
        restGroupeArticleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultGroupeArticleShouldNotBeFound(String filter) throws Exception {
        restGroupeArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restGroupeArticleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingGroupeArticle() throws Exception {
        // Get the groupeArticle
        restGroupeArticleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGroupeArticle() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the groupeArticle
        GroupeArticle updatedGroupeArticle = groupeArticleRepository.findById(groupeArticle.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGroupeArticle are not directly saved in db
        em.detach(updatedGroupeArticle);
        updatedGroupeArticle.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).statut(UPDATED_STATUT);
        GroupeArticleDTO groupeArticleDTO = groupeArticleMapper.toDto(updatedGroupeArticle);

        restGroupeArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, groupeArticleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(groupeArticleDTO))
            )
            .andExpect(status().isOk());

        // Validate the GroupeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGroupeArticleToMatchAllProperties(updatedGroupeArticle);
    }

    @Test
    @Transactional
    void putNonExistingGroupeArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        groupeArticle.setId(longCount.incrementAndGet());

        // Create the GroupeArticle
        GroupeArticleDTO groupeArticleDTO = groupeArticleMapper.toDto(groupeArticle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroupeArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, groupeArticleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(groupeArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroupeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGroupeArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        groupeArticle.setId(longCount.incrementAndGet());

        // Create the GroupeArticle
        GroupeArticleDTO groupeArticleDTO = groupeArticleMapper.toDto(groupeArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupeArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(groupeArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroupeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGroupeArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        groupeArticle.setId(longCount.incrementAndGet());

        // Create the GroupeArticle
        GroupeArticleDTO groupeArticleDTO = groupeArticleMapper.toDto(groupeArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupeArticleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(groupeArticleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GroupeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGroupeArticleWithPatch() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the groupeArticle using partial update
        GroupeArticle partialUpdatedGroupeArticle = new GroupeArticle();
        partialUpdatedGroupeArticle.setId(groupeArticle.getId());

        restGroupeArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGroupeArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGroupeArticle))
            )
            .andExpect(status().isOk());

        // Validate the GroupeArticle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGroupeArticleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedGroupeArticle, groupeArticle),
            getPersistedGroupeArticle(groupeArticle)
        );
    }

    @Test
    @Transactional
    void fullUpdateGroupeArticleWithPatch() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the groupeArticle using partial update
        GroupeArticle partialUpdatedGroupeArticle = new GroupeArticle();
        partialUpdatedGroupeArticle.setId(groupeArticle.getId());

        partialUpdatedGroupeArticle.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).statut(UPDATED_STATUT);

        restGroupeArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGroupeArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGroupeArticle))
            )
            .andExpect(status().isOk());

        // Validate the GroupeArticle in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGroupeArticleUpdatableFieldsEquals(partialUpdatedGroupeArticle, getPersistedGroupeArticle(partialUpdatedGroupeArticle));
    }

    @Test
    @Transactional
    void patchNonExistingGroupeArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        groupeArticle.setId(longCount.incrementAndGet());

        // Create the GroupeArticle
        GroupeArticleDTO groupeArticleDTO = groupeArticleMapper.toDto(groupeArticle);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroupeArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, groupeArticleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(groupeArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroupeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGroupeArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        groupeArticle.setId(longCount.incrementAndGet());

        // Create the GroupeArticle
        GroupeArticleDTO groupeArticleDTO = groupeArticleMapper.toDto(groupeArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupeArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(groupeArticleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroupeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGroupeArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        groupeArticle.setId(longCount.incrementAndGet());

        // Create the GroupeArticle
        GroupeArticleDTO groupeArticleDTO = groupeArticleMapper.toDto(groupeArticle);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroupeArticleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(groupeArticleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the GroupeArticle in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGroupeArticle() throws Exception {
        // Initialize the database
        insertedGroupeArticle = groupeArticleRepository.saveAndFlush(groupeArticle);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the groupeArticle
        restGroupeArticleMockMvc
            .perform(delete(ENTITY_API_URL_ID, groupeArticle.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return groupeArticleRepository.count();
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

    protected GroupeArticle getPersistedGroupeArticle(GroupeArticle groupeArticle) {
        return groupeArticleRepository.findById(groupeArticle.getId()).orElseThrow();
    }

    protected void assertPersistedGroupeArticleToMatchAllProperties(GroupeArticle expectedGroupeArticle) {
        assertGroupeArticleAllPropertiesEquals(expectedGroupeArticle, getPersistedGroupeArticle(expectedGroupeArticle));
    }

    protected void assertPersistedGroupeArticleToMatchUpdatableProperties(GroupeArticle expectedGroupeArticle) {
        assertGroupeArticleAllUpdatablePropertiesEquals(expectedGroupeArticle, getPersistedGroupeArticle(expectedGroupeArticle));
    }
}
