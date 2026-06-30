package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.ParametreGlobalAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.ParametreGlobal;
import com.adm.supervision.repository.ParametreGlobalRepository;
import com.adm.supervision.service.dto.ParametreGlobalDTO;
import com.adm.supervision.service.mapper.ParametreGlobalMapper;
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
 * Integration tests for the {@link ParametreGlobalResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParametreGlobalResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_VALEUR = "AAAAAAAAAA";
    private static final String UPDATED_VALEUR = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final String ENTITY_API_URL = "/api/parametre-globals";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParametreGlobalRepository parametreGlobalRepository;

    @Autowired
    private ParametreGlobalMapper parametreGlobalMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParametreGlobalMockMvc;

    private ParametreGlobal parametreGlobal;

    private ParametreGlobal insertedParametreGlobal;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParametreGlobal createEntity() {
        return new ParametreGlobal().code(DEFAULT_CODE).valeur(DEFAULT_VALEUR).description(DEFAULT_DESCRIPTION).actif(DEFAULT_ACTIF);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParametreGlobal createUpdatedEntity() {
        return new ParametreGlobal().code(UPDATED_CODE).valeur(UPDATED_VALEUR).description(UPDATED_DESCRIPTION).actif(UPDATED_ACTIF);
    }

    @BeforeEach
    void initTest() {
        parametreGlobal = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedParametreGlobal != null) {
            parametreGlobalRepository.delete(insertedParametreGlobal);
            insertedParametreGlobal = null;
        }
    }

    @Test
    @Transactional
    void createParametreGlobal() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ParametreGlobal
        ParametreGlobalDTO parametreGlobalDTO = parametreGlobalMapper.toDto(parametreGlobal);
        var returnedParametreGlobalDTO = om.readValue(
            restParametreGlobalMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametreGlobalDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ParametreGlobalDTO.class
        );

        // Validate the ParametreGlobal in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedParametreGlobal = parametreGlobalMapper.toEntity(returnedParametreGlobalDTO);
        assertParametreGlobalUpdatableFieldsEquals(returnedParametreGlobal, getPersistedParametreGlobal(returnedParametreGlobal));

        insertedParametreGlobal = returnedParametreGlobal;
    }

    @Test
    @Transactional
    void createParametreGlobalWithExistingId() throws Exception {
        // Create the ParametreGlobal with an existing ID
        parametreGlobal.setId(1L);
        ParametreGlobalDTO parametreGlobalDTO = parametreGlobalMapper.toDto(parametreGlobal);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParametreGlobalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametreGlobalDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ParametreGlobal in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        parametreGlobal.setCode(null);

        // Create the ParametreGlobal, which fails.
        ParametreGlobalDTO parametreGlobalDTO = parametreGlobalMapper.toDto(parametreGlobal);

        restParametreGlobalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametreGlobalDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValeurIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        parametreGlobal.setValeur(null);

        // Create the ParametreGlobal, which fails.
        ParametreGlobalDTO parametreGlobalDTO = parametreGlobalMapper.toDto(parametreGlobal);

        restParametreGlobalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametreGlobalDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        parametreGlobal.setActif(null);

        // Create the ParametreGlobal, which fails.
        ParametreGlobalDTO parametreGlobalDTO = parametreGlobalMapper.toDto(parametreGlobal);

        restParametreGlobalMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametreGlobalDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllParametreGlobals() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList
        restParametreGlobalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parametreGlobal.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].valeur").value(hasItem(DEFAULT_VALEUR)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));
    }

    @Test
    @Transactional
    void getParametreGlobal() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get the parametreGlobal
        restParametreGlobalMockMvc
            .perform(get(ENTITY_API_URL_ID, parametreGlobal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(parametreGlobal.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.valeur").value(DEFAULT_VALEUR))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF));
    }

    @Test
    @Transactional
    void getParametreGlobalsByIdFiltering() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        Long id = parametreGlobal.getId();

        defaultParametreGlobalFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultParametreGlobalFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultParametreGlobalFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllParametreGlobalsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList where code equals to
        defaultParametreGlobalFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllParametreGlobalsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList where code in
        defaultParametreGlobalFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllParametreGlobalsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList where code is not null
        defaultParametreGlobalFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllParametreGlobalsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList where code contains
        defaultParametreGlobalFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllParametreGlobalsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList where code does not contain
        defaultParametreGlobalFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllParametreGlobalsByValeurIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList where valeur equals to
        defaultParametreGlobalFiltering("valeur.equals=" + DEFAULT_VALEUR, "valeur.equals=" + UPDATED_VALEUR);
    }

    @Test
    @Transactional
    void getAllParametreGlobalsByValeurIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList where valeur in
        defaultParametreGlobalFiltering("valeur.in=" + DEFAULT_VALEUR + "," + UPDATED_VALEUR, "valeur.in=" + UPDATED_VALEUR);
    }

    @Test
    @Transactional
    void getAllParametreGlobalsByValeurIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList where valeur is not null
        defaultParametreGlobalFiltering("valeur.specified=true", "valeur.specified=false");
    }

    @Test
    @Transactional
    void getAllParametreGlobalsByValeurContainsSomething() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList where valeur contains
        defaultParametreGlobalFiltering("valeur.contains=" + DEFAULT_VALEUR, "valeur.contains=" + UPDATED_VALEUR);
    }

    @Test
    @Transactional
    void getAllParametreGlobalsByValeurNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList where valeur does not contain
        defaultParametreGlobalFiltering("valeur.doesNotContain=" + UPDATED_VALEUR, "valeur.doesNotContain=" + DEFAULT_VALEUR);
    }

    @Test
    @Transactional
    void getAllParametreGlobalsByActifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList where actif equals to
        defaultParametreGlobalFiltering("actif.equals=" + DEFAULT_ACTIF, "actif.equals=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllParametreGlobalsByActifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList where actif in
        defaultParametreGlobalFiltering("actif.in=" + DEFAULT_ACTIF + "," + UPDATED_ACTIF, "actif.in=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllParametreGlobalsByActifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        // Get all the parametreGlobalList where actif is not null
        defaultParametreGlobalFiltering("actif.specified=true", "actif.specified=false");
    }

    private void defaultParametreGlobalFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultParametreGlobalShouldBeFound(shouldBeFound);
        defaultParametreGlobalShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultParametreGlobalShouldBeFound(String filter) throws Exception {
        restParametreGlobalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parametreGlobal.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].valeur").value(hasItem(DEFAULT_VALEUR)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));

        // Check, that the count call also returns 1
        restParametreGlobalMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultParametreGlobalShouldNotBeFound(String filter) throws Exception {
        restParametreGlobalMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restParametreGlobalMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingParametreGlobal() throws Exception {
        // Get the parametreGlobal
        restParametreGlobalMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParametreGlobal() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parametreGlobal
        ParametreGlobal updatedParametreGlobal = parametreGlobalRepository.findById(parametreGlobal.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParametreGlobal are not directly saved in db
        em.detach(updatedParametreGlobal);
        updatedParametreGlobal.code(UPDATED_CODE).valeur(UPDATED_VALEUR).description(UPDATED_DESCRIPTION).actif(UPDATED_ACTIF);
        ParametreGlobalDTO parametreGlobalDTO = parametreGlobalMapper.toDto(updatedParametreGlobal);

        restParametreGlobalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parametreGlobalDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parametreGlobalDTO))
            )
            .andExpect(status().isOk());

        // Validate the ParametreGlobal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParametreGlobalToMatchAllProperties(updatedParametreGlobal);
    }

    @Test
    @Transactional
    void putNonExistingParametreGlobal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parametreGlobal.setId(longCount.incrementAndGet());

        // Create the ParametreGlobal
        ParametreGlobalDTO parametreGlobalDTO = parametreGlobalMapper.toDto(parametreGlobal);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParametreGlobalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parametreGlobalDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parametreGlobalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParametreGlobal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParametreGlobal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parametreGlobal.setId(longCount.incrementAndGet());

        // Create the ParametreGlobal
        ParametreGlobalDTO parametreGlobalDTO = parametreGlobalMapper.toDto(parametreGlobal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParametreGlobalMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parametreGlobalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParametreGlobal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParametreGlobal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parametreGlobal.setId(longCount.incrementAndGet());

        // Create the ParametreGlobal
        ParametreGlobalDTO parametreGlobalDTO = parametreGlobalMapper.toDto(parametreGlobal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParametreGlobalMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametreGlobalDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParametreGlobal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParametreGlobalWithPatch() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parametreGlobal using partial update
        ParametreGlobal partialUpdatedParametreGlobal = new ParametreGlobal();
        partialUpdatedParametreGlobal.setId(parametreGlobal.getId());

        partialUpdatedParametreGlobal.description(UPDATED_DESCRIPTION);

        restParametreGlobalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParametreGlobal.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParametreGlobal))
            )
            .andExpect(status().isOk());

        // Validate the ParametreGlobal in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParametreGlobalUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedParametreGlobal, parametreGlobal),
            getPersistedParametreGlobal(parametreGlobal)
        );
    }

    @Test
    @Transactional
    void fullUpdateParametreGlobalWithPatch() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parametreGlobal using partial update
        ParametreGlobal partialUpdatedParametreGlobal = new ParametreGlobal();
        partialUpdatedParametreGlobal.setId(parametreGlobal.getId());

        partialUpdatedParametreGlobal.code(UPDATED_CODE).valeur(UPDATED_VALEUR).description(UPDATED_DESCRIPTION).actif(UPDATED_ACTIF);

        restParametreGlobalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParametreGlobal.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParametreGlobal))
            )
            .andExpect(status().isOk());

        // Validate the ParametreGlobal in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParametreGlobalUpdatableFieldsEquals(
            partialUpdatedParametreGlobal,
            getPersistedParametreGlobal(partialUpdatedParametreGlobal)
        );
    }

    @Test
    @Transactional
    void patchNonExistingParametreGlobal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parametreGlobal.setId(longCount.incrementAndGet());

        // Create the ParametreGlobal
        ParametreGlobalDTO parametreGlobalDTO = parametreGlobalMapper.toDto(parametreGlobal);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParametreGlobalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, parametreGlobalDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(parametreGlobalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParametreGlobal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParametreGlobal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parametreGlobal.setId(longCount.incrementAndGet());

        // Create the ParametreGlobal
        ParametreGlobalDTO parametreGlobalDTO = parametreGlobalMapper.toDto(parametreGlobal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParametreGlobalMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(parametreGlobalDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParametreGlobal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParametreGlobal() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parametreGlobal.setId(longCount.incrementAndGet());

        // Create the ParametreGlobal
        ParametreGlobalDTO parametreGlobalDTO = parametreGlobalMapper.toDto(parametreGlobal);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParametreGlobalMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(parametreGlobalDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParametreGlobal in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParametreGlobal() throws Exception {
        // Initialize the database
        insertedParametreGlobal = parametreGlobalRepository.saveAndFlush(parametreGlobal);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the parametreGlobal
        restParametreGlobalMockMvc
            .perform(delete(ENTITY_API_URL_ID, parametreGlobal.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return parametreGlobalRepository.count();
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

    protected ParametreGlobal getPersistedParametreGlobal(ParametreGlobal parametreGlobal) {
        return parametreGlobalRepository.findById(parametreGlobal.getId()).orElseThrow();
    }

    protected void assertPersistedParametreGlobalToMatchAllProperties(ParametreGlobal expectedParametreGlobal) {
        assertParametreGlobalAllPropertiesEquals(expectedParametreGlobal, getPersistedParametreGlobal(expectedParametreGlobal));
    }

    protected void assertPersistedParametreGlobalToMatchUpdatableProperties(ParametreGlobal expectedParametreGlobal) {
        assertParametreGlobalAllUpdatablePropertiesEquals(expectedParametreGlobal, getPersistedParametreGlobal(expectedParametreGlobal));
    }
}
