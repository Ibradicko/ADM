package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.UniteMesureAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.UniteMesure;
import com.adm.supervision.repository.UniteMesureRepository;
import com.adm.supervision.service.dto.UniteMesureDTO;
import com.adm.supervision.service.mapper.UniteMesureMapper;
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
 * Integration tests for the {@link UniteMesureResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UniteMesureResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_SYMBOLE = "AAAAAAAAAA";
    private static final String UPDATED_SYMBOLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/unite-mesures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UniteMesureRepository uniteMesureRepository;

    @Autowired
    private UniteMesureMapper uniteMesureMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUniteMesureMockMvc;

    private UniteMesure uniteMesure;

    private UniteMesure insertedUniteMesure;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UniteMesure createEntity() {
        return new UniteMesure().code(DEFAULT_CODE).libelle(DEFAULT_LIBELLE).symbole(DEFAULT_SYMBOLE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UniteMesure createUpdatedEntity() {
        return new UniteMesure().code(UPDATED_CODE).libelle(UPDATED_LIBELLE).symbole(UPDATED_SYMBOLE);
    }

    @BeforeEach
    void initTest() {
        uniteMesure = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedUniteMesure != null) {
            uniteMesureRepository.delete(insertedUniteMesure);
            insertedUniteMesure = null;
        }
    }

    @Test
    @Transactional
    void createUniteMesure() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UniteMesure
        UniteMesureDTO uniteMesureDTO = uniteMesureMapper.toDto(uniteMesure);
        var returnedUniteMesureDTO = om.readValue(
            restUniteMesureMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uniteMesureDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            UniteMesureDTO.class
        );

        // Validate the UniteMesure in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUniteMesure = uniteMesureMapper.toEntity(returnedUniteMesureDTO);
        assertUniteMesureUpdatableFieldsEquals(returnedUniteMesure, getPersistedUniteMesure(returnedUniteMesure));

        insertedUniteMesure = returnedUniteMesure;
    }

    @Test
    @Transactional
    void createUniteMesureWithExistingId() throws Exception {
        // Create the UniteMesure with an existing ID
        uniteMesure.setId(1L);
        UniteMesureDTO uniteMesureDTO = uniteMesureMapper.toDto(uniteMesure);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUniteMesureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uniteMesureDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UniteMesure in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        uniteMesure.setCode(null);

        // Create the UniteMesure, which fails.
        UniteMesureDTO uniteMesureDTO = uniteMesureMapper.toDto(uniteMesure);

        restUniteMesureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uniteMesureDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        uniteMesure.setLibelle(null);

        // Create the UniteMesure, which fails.
        UniteMesureDTO uniteMesureDTO = uniteMesureMapper.toDto(uniteMesure);

        restUniteMesureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uniteMesureDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllUniteMesures() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList
        restUniteMesureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(uniteMesure.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].symbole").value(hasItem(DEFAULT_SYMBOLE)));
    }

    @Test
    @Transactional
    void getUniteMesure() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get the uniteMesure
        restUniteMesureMockMvc
            .perform(get(ENTITY_API_URL_ID, uniteMesure.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(uniteMesure.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.symbole").value(DEFAULT_SYMBOLE));
    }

    @Test
    @Transactional
    void getUniteMesuresByIdFiltering() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        Long id = uniteMesure.getId();

        defaultUniteMesureFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultUniteMesureFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultUniteMesureFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllUniteMesuresByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where code equals to
        defaultUniteMesureFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllUniteMesuresByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where code in
        defaultUniteMesureFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllUniteMesuresByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where code is not null
        defaultUniteMesureFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllUniteMesuresByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where code contains
        defaultUniteMesureFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllUniteMesuresByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where code does not contain
        defaultUniteMesureFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllUniteMesuresByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where libelle equals to
        defaultUniteMesureFiltering("libelle.equals=" + DEFAULT_LIBELLE, "libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllUniteMesuresByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where libelle in
        defaultUniteMesureFiltering("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE, "libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllUniteMesuresByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where libelle is not null
        defaultUniteMesureFiltering("libelle.specified=true", "libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllUniteMesuresByLibelleContainsSomething() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where libelle contains
        defaultUniteMesureFiltering("libelle.contains=" + DEFAULT_LIBELLE, "libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllUniteMesuresByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where libelle does not contain
        defaultUniteMesureFiltering("libelle.doesNotContain=" + UPDATED_LIBELLE, "libelle.doesNotContain=" + DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void getAllUniteMesuresBySymboleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where symbole equals to
        defaultUniteMesureFiltering("symbole.equals=" + DEFAULT_SYMBOLE, "symbole.equals=" + UPDATED_SYMBOLE);
    }

    @Test
    @Transactional
    void getAllUniteMesuresBySymboleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where symbole in
        defaultUniteMesureFiltering("symbole.in=" + DEFAULT_SYMBOLE + "," + UPDATED_SYMBOLE, "symbole.in=" + UPDATED_SYMBOLE);
    }

    @Test
    @Transactional
    void getAllUniteMesuresBySymboleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where symbole is not null
        defaultUniteMesureFiltering("symbole.specified=true", "symbole.specified=false");
    }

    @Test
    @Transactional
    void getAllUniteMesuresBySymboleContainsSomething() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where symbole contains
        defaultUniteMesureFiltering("symbole.contains=" + DEFAULT_SYMBOLE, "symbole.contains=" + UPDATED_SYMBOLE);
    }

    @Test
    @Transactional
    void getAllUniteMesuresBySymboleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        // Get all the uniteMesureList where symbole does not contain
        defaultUniteMesureFiltering("symbole.doesNotContain=" + UPDATED_SYMBOLE, "symbole.doesNotContain=" + DEFAULT_SYMBOLE);
    }

    private void defaultUniteMesureFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultUniteMesureShouldBeFound(shouldBeFound);
        defaultUniteMesureShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUniteMesureShouldBeFound(String filter) throws Exception {
        restUniteMesureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(uniteMesure.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].symbole").value(hasItem(DEFAULT_SYMBOLE)));

        // Check, that the count call also returns 1
        restUniteMesureMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUniteMesureShouldNotBeFound(String filter) throws Exception {
        restUniteMesureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUniteMesureMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingUniteMesure() throws Exception {
        // Get the uniteMesure
        restUniteMesureMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingUniteMesure() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the uniteMesure
        UniteMesure updatedUniteMesure = uniteMesureRepository.findById(uniteMesure.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedUniteMesure are not directly saved in db
        em.detach(updatedUniteMesure);
        updatedUniteMesure.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).symbole(UPDATED_SYMBOLE);
        UniteMesureDTO uniteMesureDTO = uniteMesureMapper.toDto(updatedUniteMesure);

        restUniteMesureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, uniteMesureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(uniteMesureDTO))
            )
            .andExpect(status().isOk());

        // Validate the UniteMesure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUniteMesureToMatchAllProperties(updatedUniteMesure);
    }

    @Test
    @Transactional
    void putNonExistingUniteMesure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uniteMesure.setId(longCount.incrementAndGet());

        // Create the UniteMesure
        UniteMesureDTO uniteMesureDTO = uniteMesureMapper.toDto(uniteMesure);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUniteMesureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, uniteMesureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(uniteMesureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UniteMesure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUniteMesure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uniteMesure.setId(longCount.incrementAndGet());

        // Create the UniteMesure
        UniteMesureDTO uniteMesureDTO = uniteMesureMapper.toDto(uniteMesure);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUniteMesureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(uniteMesureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UniteMesure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUniteMesure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uniteMesure.setId(longCount.incrementAndGet());

        // Create the UniteMesure
        UniteMesureDTO uniteMesureDTO = uniteMesureMapper.toDto(uniteMesure);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUniteMesureMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(uniteMesureDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UniteMesure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUniteMesureWithPatch() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the uniteMesure using partial update
        UniteMesure partialUpdatedUniteMesure = new UniteMesure();
        partialUpdatedUniteMesure.setId(uniteMesure.getId());

        restUniteMesureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUniteMesure.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUniteMesure))
            )
            .andExpect(status().isOk());

        // Validate the UniteMesure in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUniteMesureUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUniteMesure, uniteMesure),
            getPersistedUniteMesure(uniteMesure)
        );
    }

    @Test
    @Transactional
    void fullUpdateUniteMesureWithPatch() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the uniteMesure using partial update
        UniteMesure partialUpdatedUniteMesure = new UniteMesure();
        partialUpdatedUniteMesure.setId(uniteMesure.getId());

        partialUpdatedUniteMesure.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).symbole(UPDATED_SYMBOLE);

        restUniteMesureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUniteMesure.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedUniteMesure))
            )
            .andExpect(status().isOk());

        // Validate the UniteMesure in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUniteMesureUpdatableFieldsEquals(partialUpdatedUniteMesure, getPersistedUniteMesure(partialUpdatedUniteMesure));
    }

    @Test
    @Transactional
    void patchNonExistingUniteMesure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uniteMesure.setId(longCount.incrementAndGet());

        // Create the UniteMesure
        UniteMesureDTO uniteMesureDTO = uniteMesureMapper.toDto(uniteMesure);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUniteMesureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, uniteMesureDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(uniteMesureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UniteMesure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUniteMesure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uniteMesure.setId(longCount.incrementAndGet());

        // Create the UniteMesure
        UniteMesureDTO uniteMesureDTO = uniteMesureMapper.toDto(uniteMesure);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUniteMesureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(uniteMesureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the UniteMesure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUniteMesure() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        uniteMesure.setId(longCount.incrementAndGet());

        // Create the UniteMesure
        UniteMesureDTO uniteMesureDTO = uniteMesureMapper.toDto(uniteMesure);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUniteMesureMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(uniteMesureDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UniteMesure in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUniteMesure() throws Exception {
        // Initialize the database
        insertedUniteMesure = uniteMesureRepository.saveAndFlush(uniteMesure);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the uniteMesure
        restUniteMesureMockMvc
            .perform(delete(ENTITY_API_URL_ID, uniteMesure.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return uniteMesureRepository.count();
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

    protected UniteMesure getPersistedUniteMesure(UniteMesure uniteMesure) {
        return uniteMesureRepository.findById(uniteMesure.getId()).orElseThrow();
    }

    protected void assertPersistedUniteMesureToMatchAllProperties(UniteMesure expectedUniteMesure) {
        assertUniteMesureAllPropertiesEquals(expectedUniteMesure, getPersistedUniteMesure(expectedUniteMesure));
    }

    protected void assertPersistedUniteMesureToMatchUpdatableProperties(UniteMesure expectedUniteMesure) {
        assertUniteMesureAllUpdatablePropertiesEquals(expectedUniteMesure, getPersistedUniteMesure(expectedUniteMesure));
    }
}
