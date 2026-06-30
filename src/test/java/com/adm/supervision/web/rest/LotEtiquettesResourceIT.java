package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.LotEtiquettesAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.LotEtiquettes;
import com.adm.supervision.repository.LotEtiquettesRepository;
import com.adm.supervision.service.dto.LotEtiquettesDTO;
import com.adm.supervision.service.mapper.LotEtiquettesMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link LotEtiquettesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LotEtiquettesResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_GENERATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_GENERATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_FORMAT_IMPRESSION = "AAAAAAAAAA";
    private static final String UPDATED_FORMAT_IMPRESSION = "BBBBBBBBBB";

    private static final Integer DEFAULT_NOMBRE_ETIQUETTES = 1;
    private static final Integer UPDATED_NOMBRE_ETIQUETTES = 2;
    private static final Integer SMALLER_NOMBRE_ETIQUETTES = 1 - 1;

    private static final String ENTITY_API_URL = "/api/lot-etiquettes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LotEtiquettesRepository lotEtiquettesRepository;

    @Autowired
    private LotEtiquettesMapper lotEtiquettesMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLotEtiquettesMockMvc;

    private LotEtiquettes lotEtiquettes;

    private LotEtiquettes insertedLotEtiquettes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LotEtiquettes createEntity() {
        return new LotEtiquettes()
            .reference(DEFAULT_REFERENCE)
            .dateGeneration(DEFAULT_DATE_GENERATION)
            .formatImpression(DEFAULT_FORMAT_IMPRESSION)
            .nombreEtiquettes(DEFAULT_NOMBRE_ETIQUETTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LotEtiquettes createUpdatedEntity() {
        return new LotEtiquettes()
            .reference(UPDATED_REFERENCE)
            .dateGeneration(UPDATED_DATE_GENERATION)
            .formatImpression(UPDATED_FORMAT_IMPRESSION)
            .nombreEtiquettes(UPDATED_NOMBRE_ETIQUETTES);
    }

    @BeforeEach
    void initTest() {
        lotEtiquettes = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLotEtiquettes != null) {
            lotEtiquettesRepository.delete(insertedLotEtiquettes);
            insertedLotEtiquettes = null;
        }
    }

    @Test
    @Transactional
    void createLotEtiquettes() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LotEtiquettes
        LotEtiquettesDTO lotEtiquettesDTO = lotEtiquettesMapper.toDto(lotEtiquettes);
        var returnedLotEtiquettesDTO = om.readValue(
            restLotEtiquettesMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lotEtiquettesDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LotEtiquettesDTO.class
        );

        // Validate the LotEtiquettes in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLotEtiquettes = lotEtiquettesMapper.toEntity(returnedLotEtiquettesDTO);
        assertLotEtiquettesUpdatableFieldsEquals(returnedLotEtiquettes, getPersistedLotEtiquettes(returnedLotEtiquettes));

        insertedLotEtiquettes = returnedLotEtiquettes;
    }

    @Test
    @Transactional
    void createLotEtiquettesWithExistingId() throws Exception {
        // Create the LotEtiquettes with an existing ID
        lotEtiquettes.setId(1L);
        LotEtiquettesDTO lotEtiquettesDTO = lotEtiquettesMapper.toDto(lotEtiquettes);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLotEtiquettesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lotEtiquettesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LotEtiquettes in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lotEtiquettes.setReference(null);

        // Create the LotEtiquettes, which fails.
        LotEtiquettesDTO lotEtiquettesDTO = lotEtiquettesMapper.toDto(lotEtiquettes);

        restLotEtiquettesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lotEtiquettesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateGenerationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lotEtiquettes.setDateGeneration(null);

        // Create the LotEtiquettes, which fails.
        LotEtiquettesDTO lotEtiquettesDTO = lotEtiquettesMapper.toDto(lotEtiquettes);

        restLotEtiquettesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lotEtiquettesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNombreEtiquettesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        lotEtiquettes.setNombreEtiquettes(null);

        // Create the LotEtiquettes, which fails.
        LotEtiquettesDTO lotEtiquettesDTO = lotEtiquettesMapper.toDto(lotEtiquettes);

        restLotEtiquettesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lotEtiquettesDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLotEtiquetteses() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList
        restLotEtiquettesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lotEtiquettes.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].dateGeneration").value(hasItem(DEFAULT_DATE_GENERATION.toString())))
            .andExpect(jsonPath("$.[*].formatImpression").value(hasItem(DEFAULT_FORMAT_IMPRESSION)))
            .andExpect(jsonPath("$.[*].nombreEtiquettes").value(hasItem(DEFAULT_NOMBRE_ETIQUETTES)));
    }

    @Test
    @Transactional
    void getLotEtiquettes() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get the lotEtiquettes
        restLotEtiquettesMockMvc
            .perform(get(ENTITY_API_URL_ID, lotEtiquettes.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(lotEtiquettes.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.dateGeneration").value(DEFAULT_DATE_GENERATION.toString()))
            .andExpect(jsonPath("$.formatImpression").value(DEFAULT_FORMAT_IMPRESSION))
            .andExpect(jsonPath("$.nombreEtiquettes").value(DEFAULT_NOMBRE_ETIQUETTES));
    }

    @Test
    @Transactional
    void getLotEtiquettesesByIdFiltering() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        Long id = lotEtiquettes.getId();

        defaultLotEtiquettesFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLotEtiquettesFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLotEtiquettesFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where reference equals to
        defaultLotEtiquettesFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where reference in
        defaultLotEtiquettesFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where reference is not null
        defaultLotEtiquettesFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where reference contains
        defaultLotEtiquettesFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where reference does not contain
        defaultLotEtiquettesFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByDateGenerationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where dateGeneration equals to
        defaultLotEtiquettesFiltering(
            "dateGeneration.equals=" + DEFAULT_DATE_GENERATION,
            "dateGeneration.equals=" + UPDATED_DATE_GENERATION
        );
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByDateGenerationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where dateGeneration in
        defaultLotEtiquettesFiltering(
            "dateGeneration.in=" + DEFAULT_DATE_GENERATION + "," + UPDATED_DATE_GENERATION,
            "dateGeneration.in=" + UPDATED_DATE_GENERATION
        );
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByDateGenerationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where dateGeneration is not null
        defaultLotEtiquettesFiltering("dateGeneration.specified=true", "dateGeneration.specified=false");
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByFormatImpressionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where formatImpression equals to
        defaultLotEtiquettesFiltering(
            "formatImpression.equals=" + DEFAULT_FORMAT_IMPRESSION,
            "formatImpression.equals=" + UPDATED_FORMAT_IMPRESSION
        );
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByFormatImpressionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where formatImpression in
        defaultLotEtiquettesFiltering(
            "formatImpression.in=" + DEFAULT_FORMAT_IMPRESSION + "," + UPDATED_FORMAT_IMPRESSION,
            "formatImpression.in=" + UPDATED_FORMAT_IMPRESSION
        );
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByFormatImpressionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where formatImpression is not null
        defaultLotEtiquettesFiltering("formatImpression.specified=true", "formatImpression.specified=false");
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByFormatImpressionContainsSomething() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where formatImpression contains
        defaultLotEtiquettesFiltering(
            "formatImpression.contains=" + DEFAULT_FORMAT_IMPRESSION,
            "formatImpression.contains=" + UPDATED_FORMAT_IMPRESSION
        );
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByFormatImpressionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where formatImpression does not contain
        defaultLotEtiquettesFiltering(
            "formatImpression.doesNotContain=" + UPDATED_FORMAT_IMPRESSION,
            "formatImpression.doesNotContain=" + DEFAULT_FORMAT_IMPRESSION
        );
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByNombreEtiquettesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where nombreEtiquettes equals to
        defaultLotEtiquettesFiltering(
            "nombreEtiquettes.equals=" + DEFAULT_NOMBRE_ETIQUETTES,
            "nombreEtiquettes.equals=" + UPDATED_NOMBRE_ETIQUETTES
        );
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByNombreEtiquettesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where nombreEtiquettes in
        defaultLotEtiquettesFiltering(
            "nombreEtiquettes.in=" + DEFAULT_NOMBRE_ETIQUETTES + "," + UPDATED_NOMBRE_ETIQUETTES,
            "nombreEtiquettes.in=" + UPDATED_NOMBRE_ETIQUETTES
        );
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByNombreEtiquettesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where nombreEtiquettes is not null
        defaultLotEtiquettesFiltering("nombreEtiquettes.specified=true", "nombreEtiquettes.specified=false");
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByNombreEtiquettesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where nombreEtiquettes is greater than or equal to
        defaultLotEtiquettesFiltering(
            "nombreEtiquettes.greaterThanOrEqual=" + DEFAULT_NOMBRE_ETIQUETTES,
            "nombreEtiquettes.greaterThanOrEqual=" + UPDATED_NOMBRE_ETIQUETTES
        );
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByNombreEtiquettesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where nombreEtiquettes is less than or equal to
        defaultLotEtiquettesFiltering(
            "nombreEtiquettes.lessThanOrEqual=" + DEFAULT_NOMBRE_ETIQUETTES,
            "nombreEtiquettes.lessThanOrEqual=" + SMALLER_NOMBRE_ETIQUETTES
        );
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByNombreEtiquettesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where nombreEtiquettes is less than
        defaultLotEtiquettesFiltering(
            "nombreEtiquettes.lessThan=" + UPDATED_NOMBRE_ETIQUETTES,
            "nombreEtiquettes.lessThan=" + DEFAULT_NOMBRE_ETIQUETTES
        );
    }

    @Test
    @Transactional
    void getAllLotEtiquettesesByNombreEtiquettesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        // Get all the lotEtiquettesList where nombreEtiquettes is greater than
        defaultLotEtiquettesFiltering(
            "nombreEtiquettes.greaterThan=" + SMALLER_NOMBRE_ETIQUETTES,
            "nombreEtiquettes.greaterThan=" + DEFAULT_NOMBRE_ETIQUETTES
        );
    }

    private void defaultLotEtiquettesFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLotEtiquettesShouldBeFound(shouldBeFound);
        defaultLotEtiquettesShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLotEtiquettesShouldBeFound(String filter) throws Exception {
        restLotEtiquettesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(lotEtiquettes.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].dateGeneration").value(hasItem(DEFAULT_DATE_GENERATION.toString())))
            .andExpect(jsonPath("$.[*].formatImpression").value(hasItem(DEFAULT_FORMAT_IMPRESSION)))
            .andExpect(jsonPath("$.[*].nombreEtiquettes").value(hasItem(DEFAULT_NOMBRE_ETIQUETTES)));

        // Check, that the count call also returns 1
        restLotEtiquettesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLotEtiquettesShouldNotBeFound(String filter) throws Exception {
        restLotEtiquettesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLotEtiquettesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLotEtiquettes() throws Exception {
        // Get the lotEtiquettes
        restLotEtiquettesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLotEtiquettes() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lotEtiquettes
        LotEtiquettes updatedLotEtiquettes = lotEtiquettesRepository.findById(lotEtiquettes.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLotEtiquettes are not directly saved in db
        em.detach(updatedLotEtiquettes);
        updatedLotEtiquettes
            .reference(UPDATED_REFERENCE)
            .dateGeneration(UPDATED_DATE_GENERATION)
            .formatImpression(UPDATED_FORMAT_IMPRESSION)
            .nombreEtiquettes(UPDATED_NOMBRE_ETIQUETTES);
        LotEtiquettesDTO lotEtiquettesDTO = lotEtiquettesMapper.toDto(updatedLotEtiquettes);

        restLotEtiquettesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lotEtiquettesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lotEtiquettesDTO))
            )
            .andExpect(status().isOk());

        // Validate the LotEtiquettes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLotEtiquettesToMatchAllProperties(updatedLotEtiquettes);
    }

    @Test
    @Transactional
    void putNonExistingLotEtiquettes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lotEtiquettes.setId(longCount.incrementAndGet());

        // Create the LotEtiquettes
        LotEtiquettesDTO lotEtiquettesDTO = lotEtiquettesMapper.toDto(lotEtiquettes);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLotEtiquettesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, lotEtiquettesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lotEtiquettesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LotEtiquettes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLotEtiquettes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lotEtiquettes.setId(longCount.incrementAndGet());

        // Create the LotEtiquettes
        LotEtiquettesDTO lotEtiquettesDTO = lotEtiquettesMapper.toDto(lotEtiquettes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLotEtiquettesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(lotEtiquettesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LotEtiquettes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLotEtiquettes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lotEtiquettes.setId(longCount.incrementAndGet());

        // Create the LotEtiquettes
        LotEtiquettesDTO lotEtiquettesDTO = lotEtiquettesMapper.toDto(lotEtiquettes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLotEtiquettesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(lotEtiquettesDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LotEtiquettes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLotEtiquettesWithPatch() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lotEtiquettes using partial update
        LotEtiquettes partialUpdatedLotEtiquettes = new LotEtiquettes();
        partialUpdatedLotEtiquettes.setId(lotEtiquettes.getId());

        partialUpdatedLotEtiquettes.formatImpression(UPDATED_FORMAT_IMPRESSION).nombreEtiquettes(UPDATED_NOMBRE_ETIQUETTES);

        restLotEtiquettesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLotEtiquettes.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLotEtiquettes))
            )
            .andExpect(status().isOk());

        // Validate the LotEtiquettes in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLotEtiquettesUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLotEtiquettes, lotEtiquettes),
            getPersistedLotEtiquettes(lotEtiquettes)
        );
    }

    @Test
    @Transactional
    void fullUpdateLotEtiquettesWithPatch() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the lotEtiquettes using partial update
        LotEtiquettes partialUpdatedLotEtiquettes = new LotEtiquettes();
        partialUpdatedLotEtiquettes.setId(lotEtiquettes.getId());

        partialUpdatedLotEtiquettes
            .reference(UPDATED_REFERENCE)
            .dateGeneration(UPDATED_DATE_GENERATION)
            .formatImpression(UPDATED_FORMAT_IMPRESSION)
            .nombreEtiquettes(UPDATED_NOMBRE_ETIQUETTES);

        restLotEtiquettesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLotEtiquettes.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLotEtiquettes))
            )
            .andExpect(status().isOk());

        // Validate the LotEtiquettes in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLotEtiquettesUpdatableFieldsEquals(partialUpdatedLotEtiquettes, getPersistedLotEtiquettes(partialUpdatedLotEtiquettes));
    }

    @Test
    @Transactional
    void patchNonExistingLotEtiquettes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lotEtiquettes.setId(longCount.incrementAndGet());

        // Create the LotEtiquettes
        LotEtiquettesDTO lotEtiquettesDTO = lotEtiquettesMapper.toDto(lotEtiquettes);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLotEtiquettesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, lotEtiquettesDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(lotEtiquettesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LotEtiquettes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLotEtiquettes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lotEtiquettes.setId(longCount.incrementAndGet());

        // Create the LotEtiquettes
        LotEtiquettesDTO lotEtiquettesDTO = lotEtiquettesMapper.toDto(lotEtiquettes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLotEtiquettesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(lotEtiquettesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LotEtiquettes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLotEtiquettes() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        lotEtiquettes.setId(longCount.incrementAndGet());

        // Create the LotEtiquettes
        LotEtiquettesDTO lotEtiquettesDTO = lotEtiquettesMapper.toDto(lotEtiquettes);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLotEtiquettesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(lotEtiquettesDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LotEtiquettes in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLotEtiquettes() throws Exception {
        // Initialize the database
        insertedLotEtiquettes = lotEtiquettesRepository.saveAndFlush(lotEtiquettes);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the lotEtiquettes
        restLotEtiquettesMockMvc
            .perform(delete(ENTITY_API_URL_ID, lotEtiquettes.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return lotEtiquettesRepository.count();
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

    protected LotEtiquettes getPersistedLotEtiquettes(LotEtiquettes lotEtiquettes) {
        return lotEtiquettesRepository.findById(lotEtiquettes.getId()).orElseThrow();
    }

    protected void assertPersistedLotEtiquettesToMatchAllProperties(LotEtiquettes expectedLotEtiquettes) {
        assertLotEtiquettesAllPropertiesEquals(expectedLotEtiquettes, getPersistedLotEtiquettes(expectedLotEtiquettes));
    }

    protected void assertPersistedLotEtiquettesToMatchUpdatableProperties(LotEtiquettes expectedLotEtiquettes) {
        assertLotEtiquettesAllUpdatablePropertiesEquals(expectedLotEtiquettes, getPersistedLotEtiquettes(expectedLotEtiquettes));
    }
}
