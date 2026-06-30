package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.ModePaiementRefAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.ModePaiementRef;
import com.adm.supervision.repository.ModePaiementRefRepository;
import com.adm.supervision.service.dto.ModePaiementRefDTO;
import com.adm.supervision.service.mapper.ModePaiementRefMapper;
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
 * Integration tests for the {@link ModePaiementRefResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ModePaiementRefResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final String ENTITY_API_URL = "/api/mode-paiement-refs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModePaiementRefRepository modePaiementRefRepository;

    @Autowired
    private ModePaiementRefMapper modePaiementRefMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restModePaiementRefMockMvc;

    private ModePaiementRef modePaiementRef;

    private ModePaiementRef insertedModePaiementRef;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModePaiementRef createEntity() {
        return new ModePaiementRef().code(DEFAULT_CODE).libelle(DEFAULT_LIBELLE).actif(DEFAULT_ACTIF);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModePaiementRef createUpdatedEntity() {
        return new ModePaiementRef().code(UPDATED_CODE).libelle(UPDATED_LIBELLE).actif(UPDATED_ACTIF);
    }

    @BeforeEach
    void initTest() {
        modePaiementRef = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedModePaiementRef != null) {
            modePaiementRefRepository.delete(insertedModePaiementRef);
            insertedModePaiementRef = null;
        }
    }

    @Test
    @Transactional
    void createModePaiementRef() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ModePaiementRef
        ModePaiementRefDTO modePaiementRefDTO = modePaiementRefMapper.toDto(modePaiementRef);
        var returnedModePaiementRefDTO = om.readValue(
            restModePaiementRefMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modePaiementRefDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ModePaiementRefDTO.class
        );

        // Validate the ModePaiementRef in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedModePaiementRef = modePaiementRefMapper.toEntity(returnedModePaiementRefDTO);
        assertModePaiementRefUpdatableFieldsEquals(returnedModePaiementRef, getPersistedModePaiementRef(returnedModePaiementRef));

        insertedModePaiementRef = returnedModePaiementRef;
    }

    @Test
    @Transactional
    void createModePaiementRefWithExistingId() throws Exception {
        // Create the ModePaiementRef with an existing ID
        modePaiementRef.setId(1L);
        ModePaiementRefDTO modePaiementRefDTO = modePaiementRefMapper.toDto(modePaiementRef);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restModePaiementRefMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modePaiementRefDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ModePaiementRef in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        modePaiementRef.setCode(null);

        // Create the ModePaiementRef, which fails.
        ModePaiementRefDTO modePaiementRefDTO = modePaiementRefMapper.toDto(modePaiementRef);

        restModePaiementRefMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modePaiementRefDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        modePaiementRef.setLibelle(null);

        // Create the ModePaiementRef, which fails.
        ModePaiementRefDTO modePaiementRefDTO = modePaiementRefMapper.toDto(modePaiementRef);

        restModePaiementRefMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modePaiementRefDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        modePaiementRef.setActif(null);

        // Create the ModePaiementRef, which fails.
        ModePaiementRefDTO modePaiementRefDTO = modePaiementRefMapper.toDto(modePaiementRef);

        restModePaiementRefMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modePaiementRefDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllModePaiementRefs() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList
        restModePaiementRefMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(modePaiementRef.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));
    }

    @Test
    @Transactional
    void getModePaiementRef() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get the modePaiementRef
        restModePaiementRefMockMvc
            .perform(get(ENTITY_API_URL_ID, modePaiementRef.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(modePaiementRef.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF));
    }

    @Test
    @Transactional
    void getModePaiementRefsByIdFiltering() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        Long id = modePaiementRef.getId();

        defaultModePaiementRefFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultModePaiementRefFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultModePaiementRefFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllModePaiementRefsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList where code equals to
        defaultModePaiementRefFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllModePaiementRefsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList where code in
        defaultModePaiementRefFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllModePaiementRefsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList where code is not null
        defaultModePaiementRefFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllModePaiementRefsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList where code contains
        defaultModePaiementRefFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllModePaiementRefsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList where code does not contain
        defaultModePaiementRefFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllModePaiementRefsByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList where libelle equals to
        defaultModePaiementRefFiltering("libelle.equals=" + DEFAULT_LIBELLE, "libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModePaiementRefsByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList where libelle in
        defaultModePaiementRefFiltering("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE, "libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModePaiementRefsByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList where libelle is not null
        defaultModePaiementRefFiltering("libelle.specified=true", "libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllModePaiementRefsByLibelleContainsSomething() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList where libelle contains
        defaultModePaiementRefFiltering("libelle.contains=" + DEFAULT_LIBELLE, "libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModePaiementRefsByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList where libelle does not contain
        defaultModePaiementRefFiltering("libelle.doesNotContain=" + UPDATED_LIBELLE, "libelle.doesNotContain=" + DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void getAllModePaiementRefsByActifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList where actif equals to
        defaultModePaiementRefFiltering("actif.equals=" + DEFAULT_ACTIF, "actif.equals=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllModePaiementRefsByActifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList where actif in
        defaultModePaiementRefFiltering("actif.in=" + DEFAULT_ACTIF + "," + UPDATED_ACTIF, "actif.in=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllModePaiementRefsByActifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        // Get all the modePaiementRefList where actif is not null
        defaultModePaiementRefFiltering("actif.specified=true", "actif.specified=false");
    }

    private void defaultModePaiementRefFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultModePaiementRefShouldBeFound(shouldBeFound);
        defaultModePaiementRefShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultModePaiementRefShouldBeFound(String filter) throws Exception {
        restModePaiementRefMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(modePaiementRef.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));

        // Check, that the count call also returns 1
        restModePaiementRefMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultModePaiementRefShouldNotBeFound(String filter) throws Exception {
        restModePaiementRefMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restModePaiementRefMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingModePaiementRef() throws Exception {
        // Get the modePaiementRef
        restModePaiementRefMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingModePaiementRef() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the modePaiementRef
        ModePaiementRef updatedModePaiementRef = modePaiementRefRepository.findById(modePaiementRef.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedModePaiementRef are not directly saved in db
        em.detach(updatedModePaiementRef);
        updatedModePaiementRef.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).actif(UPDATED_ACTIF);
        ModePaiementRefDTO modePaiementRefDTO = modePaiementRefMapper.toDto(updatedModePaiementRef);

        restModePaiementRefMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modePaiementRefDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(modePaiementRefDTO))
            )
            .andExpect(status().isOk());

        // Validate the ModePaiementRef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedModePaiementRefToMatchAllProperties(updatedModePaiementRef);
    }

    @Test
    @Transactional
    void putNonExistingModePaiementRef() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        modePaiementRef.setId(longCount.incrementAndGet());

        // Create the ModePaiementRef
        ModePaiementRefDTO modePaiementRefDTO = modePaiementRefMapper.toDto(modePaiementRef);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModePaiementRefMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modePaiementRefDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(modePaiementRefDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModePaiementRef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchModePaiementRef() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        modePaiementRef.setId(longCount.incrementAndGet());

        // Create the ModePaiementRef
        ModePaiementRefDTO modePaiementRefDTO = modePaiementRefMapper.toDto(modePaiementRef);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModePaiementRefMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(modePaiementRefDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModePaiementRef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamModePaiementRef() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        modePaiementRef.setId(longCount.incrementAndGet());

        // Create the ModePaiementRef
        ModePaiementRefDTO modePaiementRefDTO = modePaiementRefMapper.toDto(modePaiementRef);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModePaiementRefMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modePaiementRefDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModePaiementRef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateModePaiementRefWithPatch() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the modePaiementRef using partial update
        ModePaiementRef partialUpdatedModePaiementRef = new ModePaiementRef();
        partialUpdatedModePaiementRef.setId(modePaiementRef.getId());

        partialUpdatedModePaiementRef.code(UPDATED_CODE).libelle(UPDATED_LIBELLE);

        restModePaiementRefMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModePaiementRef.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedModePaiementRef))
            )
            .andExpect(status().isOk());

        // Validate the ModePaiementRef in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertModePaiementRefUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedModePaiementRef, modePaiementRef),
            getPersistedModePaiementRef(modePaiementRef)
        );
    }

    @Test
    @Transactional
    void fullUpdateModePaiementRefWithPatch() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the modePaiementRef using partial update
        ModePaiementRef partialUpdatedModePaiementRef = new ModePaiementRef();
        partialUpdatedModePaiementRef.setId(modePaiementRef.getId());

        partialUpdatedModePaiementRef.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).actif(UPDATED_ACTIF);

        restModePaiementRefMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModePaiementRef.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedModePaiementRef))
            )
            .andExpect(status().isOk());

        // Validate the ModePaiementRef in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertModePaiementRefUpdatableFieldsEquals(
            partialUpdatedModePaiementRef,
            getPersistedModePaiementRef(partialUpdatedModePaiementRef)
        );
    }

    @Test
    @Transactional
    void patchNonExistingModePaiementRef() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        modePaiementRef.setId(longCount.incrementAndGet());

        // Create the ModePaiementRef
        ModePaiementRefDTO modePaiementRefDTO = modePaiementRefMapper.toDto(modePaiementRef);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModePaiementRefMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, modePaiementRefDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(modePaiementRefDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModePaiementRef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchModePaiementRef() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        modePaiementRef.setId(longCount.incrementAndGet());

        // Create the ModePaiementRef
        ModePaiementRefDTO modePaiementRefDTO = modePaiementRefMapper.toDto(modePaiementRef);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModePaiementRefMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(modePaiementRefDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModePaiementRef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamModePaiementRef() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        modePaiementRef.setId(longCount.incrementAndGet());

        // Create the ModePaiementRef
        ModePaiementRefDTO modePaiementRefDTO = modePaiementRefMapper.toDto(modePaiementRef);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModePaiementRefMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(modePaiementRefDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModePaiementRef in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteModePaiementRef() throws Exception {
        // Initialize the database
        insertedModePaiementRef = modePaiementRefRepository.saveAndFlush(modePaiementRef);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the modePaiementRef
        restModePaiementRefMockMvc
            .perform(delete(ENTITY_API_URL_ID, modePaiementRef.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return modePaiementRefRepository.count();
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

    protected ModePaiementRef getPersistedModePaiementRef(ModePaiementRef modePaiementRef) {
        return modePaiementRefRepository.findById(modePaiementRef.getId()).orElseThrow();
    }

    protected void assertPersistedModePaiementRefToMatchAllProperties(ModePaiementRef expectedModePaiementRef) {
        assertModePaiementRefAllPropertiesEquals(expectedModePaiementRef, getPersistedModePaiementRef(expectedModePaiementRef));
    }

    protected void assertPersistedModePaiementRefToMatchUpdatableProperties(ModePaiementRef expectedModePaiementRef) {
        assertModePaiementRefAllUpdatablePropertiesEquals(expectedModePaiementRef, getPersistedModePaiementRef(expectedModePaiementRef));
    }
}
