package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.PermissionMetierAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.PermissionMetier;
import com.adm.supervision.domain.ProfilMetier;
import com.adm.supervision.repository.PermissionMetierRepository;
import com.adm.supervision.service.dto.PermissionMetierDTO;
import com.adm.supervision.service.mapper.PermissionMetierMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PermissionMetierResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PermissionMetierResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_MODULE = "AAAAAAAAAA";
    private static final String UPDATED_MODULE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/permission-metiers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PermissionMetierRepository permissionMetierRepository;

    @Autowired
    private PermissionMetierMapper permissionMetierMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPermissionMetierMockMvc;

    private PermissionMetier permissionMetier;

    private PermissionMetier insertedPermissionMetier;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PermissionMetier createEntity() {
        return new PermissionMetier().code(DEFAULT_CODE).libelle(DEFAULT_LIBELLE).module(DEFAULT_MODULE).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PermissionMetier createUpdatedEntity() {
        return new PermissionMetier().code(UPDATED_CODE).libelle(UPDATED_LIBELLE).module(UPDATED_MODULE).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        permissionMetier = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPermissionMetier != null) {
            permissionMetierRepository.delete(insertedPermissionMetier);
            insertedPermissionMetier = null;
        }
    }

    @Test
    @Transactional
    void createPermissionMetier() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PermissionMetier
        PermissionMetierDTO permissionMetierDTO = permissionMetierMapper.toDto(permissionMetier);
        var returnedPermissionMetierDTO = om.readValue(
            restPermissionMetierMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(permissionMetierDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PermissionMetierDTO.class
        );

        // Validate the PermissionMetier in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPermissionMetier = permissionMetierMapper.toEntity(returnedPermissionMetierDTO);
        assertPermissionMetierUpdatableFieldsEquals(returnedPermissionMetier, getPersistedPermissionMetier(returnedPermissionMetier));

        insertedPermissionMetier = returnedPermissionMetier;
    }

    @Test
    @Transactional
    void createPermissionMetierWithExistingId() throws Exception {
        // Create the PermissionMetier with an existing ID
        permissionMetier.setId(1L);
        PermissionMetierDTO permissionMetierDTO = permissionMetierMapper.toDto(permissionMetier);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPermissionMetierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(permissionMetierDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PermissionMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        permissionMetier.setCode(null);

        // Create the PermissionMetier, which fails.
        PermissionMetierDTO permissionMetierDTO = permissionMetierMapper.toDto(permissionMetier);

        restPermissionMetierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(permissionMetierDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        permissionMetier.setLibelle(null);

        // Create the PermissionMetier, which fails.
        PermissionMetierDTO permissionMetierDTO = permissionMetierMapper.toDto(permissionMetier);

        restPermissionMetierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(permissionMetierDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkModuleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        permissionMetier.setModule(null);

        // Create the PermissionMetier, which fails.
        PermissionMetierDTO permissionMetierDTO = permissionMetierMapper.toDto(permissionMetier);

        restPermissionMetierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(permissionMetierDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPermissionMetiers() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList
        restPermissionMetierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(permissionMetier.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].module").value(hasItem(DEFAULT_MODULE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getPermissionMetier() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get the permissionMetier
        restPermissionMetierMockMvc
            .perform(get(ENTITY_API_URL_ID, permissionMetier.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(permissionMetier.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.module").value(DEFAULT_MODULE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getPermissionMetiersByIdFiltering() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        Long id = permissionMetier.getId();

        defaultPermissionMetierFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultPermissionMetierFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultPermissionMetierFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where code equals to
        defaultPermissionMetierFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where code in
        defaultPermissionMetierFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where code is not null
        defaultPermissionMetierFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where code contains
        defaultPermissionMetierFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where code does not contain
        defaultPermissionMetierFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where libelle equals to
        defaultPermissionMetierFiltering("libelle.equals=" + DEFAULT_LIBELLE, "libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where libelle in
        defaultPermissionMetierFiltering("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE, "libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where libelle is not null
        defaultPermissionMetierFiltering("libelle.specified=true", "libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByLibelleContainsSomething() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where libelle contains
        defaultPermissionMetierFiltering("libelle.contains=" + DEFAULT_LIBELLE, "libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where libelle does not contain
        defaultPermissionMetierFiltering("libelle.doesNotContain=" + UPDATED_LIBELLE, "libelle.doesNotContain=" + DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByModuleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where module equals to
        defaultPermissionMetierFiltering("module.equals=" + DEFAULT_MODULE, "module.equals=" + UPDATED_MODULE);
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByModuleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where module in
        defaultPermissionMetierFiltering("module.in=" + DEFAULT_MODULE + "," + UPDATED_MODULE, "module.in=" + UPDATED_MODULE);
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByModuleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where module is not null
        defaultPermissionMetierFiltering("module.specified=true", "module.specified=false");
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByModuleContainsSomething() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where module contains
        defaultPermissionMetierFiltering("module.contains=" + DEFAULT_MODULE, "module.contains=" + UPDATED_MODULE);
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByModuleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        // Get all the permissionMetierList where module does not contain
        defaultPermissionMetierFiltering("module.doesNotContain=" + UPDATED_MODULE, "module.doesNotContain=" + DEFAULT_MODULE);
    }

    @Test
    @Transactional
    void getAllPermissionMetiersByProfilsIsEqualToSomething() throws Exception {
        ProfilMetier profils;
        if (TestUtil.findAll(em, ProfilMetier.class).isEmpty()) {
            permissionMetierRepository.saveAndFlush(permissionMetier);
            profils = ProfilMetierResourceIT.createEntity();
        } else {
            profils = TestUtil.findAll(em, ProfilMetier.class).get(0);
        }
        em.persist(profils);
        em.flush();
        permissionMetier.addProfils(profils);
        permissionMetierRepository.saveAndFlush(permissionMetier);
        Long profilsId = profils.getId();
        // Get all the permissionMetierList where profils equals to profilsId
        defaultPermissionMetierShouldBeFound("profilsId.equals=" + profilsId);

        // Get all the permissionMetierList where profils equals to (profilsId + 1)
        defaultPermissionMetierShouldNotBeFound("profilsId.equals=" + (profilsId + 1));
    }

    private void defaultPermissionMetierFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultPermissionMetierShouldBeFound(shouldBeFound);
        defaultPermissionMetierShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPermissionMetierShouldBeFound(String filter) throws Exception {
        restPermissionMetierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(permissionMetier.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].module").value(hasItem(DEFAULT_MODULE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restPermissionMetierMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPermissionMetierShouldNotBeFound(String filter) throws Exception {
        restPermissionMetierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPermissionMetierMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPermissionMetier() throws Exception {
        // Get the permissionMetier
        restPermissionMetierMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPermissionMetier() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the permissionMetier
        PermissionMetier updatedPermissionMetier = permissionMetierRepository.findById(permissionMetier.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPermissionMetier are not directly saved in db
        em.detach(updatedPermissionMetier);
        updatedPermissionMetier.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).module(UPDATED_MODULE).description(UPDATED_DESCRIPTION);
        PermissionMetierDTO permissionMetierDTO = permissionMetierMapper.toDto(updatedPermissionMetier);

        restPermissionMetierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, permissionMetierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(permissionMetierDTO))
            )
            .andExpect(status().isOk());

        // Validate the PermissionMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPermissionMetierToMatchAllProperties(updatedPermissionMetier);
    }

    @Test
    @Transactional
    void putNonExistingPermissionMetier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        permissionMetier.setId(longCount.incrementAndGet());

        // Create the PermissionMetier
        PermissionMetierDTO permissionMetierDTO = permissionMetierMapper.toDto(permissionMetier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPermissionMetierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, permissionMetierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(permissionMetierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PermissionMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPermissionMetier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        permissionMetier.setId(longCount.incrementAndGet());

        // Create the PermissionMetier
        PermissionMetierDTO permissionMetierDTO = permissionMetierMapper.toDto(permissionMetier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPermissionMetierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(permissionMetierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PermissionMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPermissionMetier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        permissionMetier.setId(longCount.incrementAndGet());

        // Create the PermissionMetier
        PermissionMetierDTO permissionMetierDTO = permissionMetierMapper.toDto(permissionMetier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPermissionMetierMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(permissionMetierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PermissionMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePermissionMetierWithPatch() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the permissionMetier using partial update
        PermissionMetier partialUpdatedPermissionMetier = new PermissionMetier();
        partialUpdatedPermissionMetier.setId(permissionMetier.getId());

        partialUpdatedPermissionMetier.module(UPDATED_MODULE).description(UPDATED_DESCRIPTION);

        restPermissionMetierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPermissionMetier.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPermissionMetier))
            )
            .andExpect(status().isOk());

        // Validate the PermissionMetier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPermissionMetierUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPermissionMetier, permissionMetier),
            getPersistedPermissionMetier(permissionMetier)
        );
    }

    @Test
    @Transactional
    void fullUpdatePermissionMetierWithPatch() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the permissionMetier using partial update
        PermissionMetier partialUpdatedPermissionMetier = new PermissionMetier();
        partialUpdatedPermissionMetier.setId(permissionMetier.getId());

        partialUpdatedPermissionMetier.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).module(UPDATED_MODULE).description(UPDATED_DESCRIPTION);

        restPermissionMetierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPermissionMetier.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPermissionMetier))
            )
            .andExpect(status().isOk());

        // Validate the PermissionMetier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPermissionMetierUpdatableFieldsEquals(
            partialUpdatedPermissionMetier,
            getPersistedPermissionMetier(partialUpdatedPermissionMetier)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPermissionMetier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        permissionMetier.setId(longCount.incrementAndGet());

        // Create the PermissionMetier
        PermissionMetierDTO permissionMetierDTO = permissionMetierMapper.toDto(permissionMetier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPermissionMetierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, permissionMetierDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(permissionMetierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PermissionMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPermissionMetier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        permissionMetier.setId(longCount.incrementAndGet());

        // Create the PermissionMetier
        PermissionMetierDTO permissionMetierDTO = permissionMetierMapper.toDto(permissionMetier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPermissionMetierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(permissionMetierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PermissionMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPermissionMetier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        permissionMetier.setId(longCount.incrementAndGet());

        // Create the PermissionMetier
        PermissionMetierDTO permissionMetierDTO = permissionMetierMapper.toDto(permissionMetier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPermissionMetierMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(permissionMetierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PermissionMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePermissionMetier() throws Exception {
        // Initialize the database
        insertedPermissionMetier = permissionMetierRepository.saveAndFlush(permissionMetier);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the permissionMetier
        restPermissionMetierMockMvc
            .perform(delete(ENTITY_API_URL_ID, permissionMetier.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return permissionMetierRepository.count();
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

    protected PermissionMetier getPersistedPermissionMetier(PermissionMetier permissionMetier) {
        return permissionMetierRepository.findById(permissionMetier.getId()).orElseThrow();
    }

    protected void assertPersistedPermissionMetierToMatchAllProperties(PermissionMetier expectedPermissionMetier) {
        assertPermissionMetierAllPropertiesEquals(expectedPermissionMetier, getPersistedPermissionMetier(expectedPermissionMetier));
    }

    protected void assertPersistedPermissionMetierToMatchUpdatableProperties(PermissionMetier expectedPermissionMetier) {
        assertPermissionMetierAllUpdatablePropertiesEquals(
            expectedPermissionMetier,
            getPersistedPermissionMetier(expectedPermissionMetier)
        );
    }
}
