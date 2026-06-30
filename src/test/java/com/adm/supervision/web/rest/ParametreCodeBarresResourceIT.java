package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.ParametreCodeBarresAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.ParametreCodeBarres;
import com.adm.supervision.domain.enumeration.TypeCodeBarres;
import com.adm.supervision.repository.ParametreCodeBarresRepository;
import com.adm.supervision.service.dto.ParametreCodeBarresDTO;
import com.adm.supervision.service.mapper.ParametreCodeBarresMapper;
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
 * Integration tests for the {@link ParametreCodeBarresResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParametreCodeBarresResourceIT {

    private static final TypeCodeBarres DEFAULT_FORMAT_PAR_DEFAUT = TypeCodeBarres.EAN13;
    private static final TypeCodeBarres UPDATED_FORMAT_PAR_DEFAUT = TypeCodeBarres.EAN8;

    private static final String DEFAULT_PREFIXE = "AAAAAAAAAA";
    private static final String UPDATED_PREFIXE = "BBBBBBBBBB";

    private static final Integer DEFAULT_LONGUEUR = 8;
    private static final Integer UPDATED_LONGUEUR = 9;
    private static final Integer SMALLER_LONGUEUR = 8 - 1;

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final String ENTITY_API_URL = "/api/parametre-code-barres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParametreCodeBarresRepository parametreCodeBarresRepository;

    @Autowired
    private ParametreCodeBarresMapper parametreCodeBarresMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParametreCodeBarresMockMvc;

    private ParametreCodeBarres parametreCodeBarres;

    private ParametreCodeBarres insertedParametreCodeBarres;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParametreCodeBarres createEntity() {
        return new ParametreCodeBarres()
            .formatParDefaut(DEFAULT_FORMAT_PAR_DEFAUT)
            .prefixe(DEFAULT_PREFIXE)
            .longueur(DEFAULT_LONGUEUR)
            .actif(DEFAULT_ACTIF);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParametreCodeBarres createUpdatedEntity() {
        return new ParametreCodeBarres()
            .formatParDefaut(UPDATED_FORMAT_PAR_DEFAUT)
            .prefixe(UPDATED_PREFIXE)
            .longueur(UPDATED_LONGUEUR)
            .actif(UPDATED_ACTIF);
    }

    @BeforeEach
    void initTest() {
        parametreCodeBarres = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedParametreCodeBarres != null) {
            parametreCodeBarresRepository.delete(insertedParametreCodeBarres);
            insertedParametreCodeBarres = null;
        }
    }

    @Test
    @Transactional
    void createParametreCodeBarres() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ParametreCodeBarres
        ParametreCodeBarresDTO parametreCodeBarresDTO = parametreCodeBarresMapper.toDto(parametreCodeBarres);
        var returnedParametreCodeBarresDTO = om.readValue(
            restParametreCodeBarresMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametreCodeBarresDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ParametreCodeBarresDTO.class
        );

        // Validate the ParametreCodeBarres in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedParametreCodeBarres = parametreCodeBarresMapper.toEntity(returnedParametreCodeBarresDTO);
        assertParametreCodeBarresUpdatableFieldsEquals(
            returnedParametreCodeBarres,
            getPersistedParametreCodeBarres(returnedParametreCodeBarres)
        );

        insertedParametreCodeBarres = returnedParametreCodeBarres;
    }

    @Test
    @Transactional
    void createParametreCodeBarresWithExistingId() throws Exception {
        // Create the ParametreCodeBarres with an existing ID
        parametreCodeBarres.setId(1L);
        ParametreCodeBarresDTO parametreCodeBarresDTO = parametreCodeBarresMapper.toDto(parametreCodeBarres);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParametreCodeBarresMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametreCodeBarresDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ParametreCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFormatParDefautIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        parametreCodeBarres.setFormatParDefaut(null);

        // Create the ParametreCodeBarres, which fails.
        ParametreCodeBarresDTO parametreCodeBarresDTO = parametreCodeBarresMapper.toDto(parametreCodeBarres);

        restParametreCodeBarresMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametreCodeBarresDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        parametreCodeBarres.setActif(null);

        // Create the ParametreCodeBarres, which fails.
        ParametreCodeBarresDTO parametreCodeBarresDTO = parametreCodeBarresMapper.toDto(parametreCodeBarres);

        restParametreCodeBarresMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametreCodeBarresDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllParametreCodeBarreses() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList
        restParametreCodeBarresMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parametreCodeBarres.getId().intValue())))
            .andExpect(jsonPath("$.[*].formatParDefaut").value(hasItem(DEFAULT_FORMAT_PAR_DEFAUT.toString())))
            .andExpect(jsonPath("$.[*].prefixe").value(hasItem(DEFAULT_PREFIXE)))
            .andExpect(jsonPath("$.[*].longueur").value(hasItem(DEFAULT_LONGUEUR)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));
    }

    @Test
    @Transactional
    void getParametreCodeBarres() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get the parametreCodeBarres
        restParametreCodeBarresMockMvc
            .perform(get(ENTITY_API_URL_ID, parametreCodeBarres.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(parametreCodeBarres.getId().intValue()))
            .andExpect(jsonPath("$.formatParDefaut").value(DEFAULT_FORMAT_PAR_DEFAUT.toString()))
            .andExpect(jsonPath("$.prefixe").value(DEFAULT_PREFIXE))
            .andExpect(jsonPath("$.longueur").value(DEFAULT_LONGUEUR))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF));
    }

    @Test
    @Transactional
    void getParametreCodeBarresesByIdFiltering() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        Long id = parametreCodeBarres.getId();

        defaultParametreCodeBarresFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultParametreCodeBarresFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultParametreCodeBarresFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByFormatParDefautIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where formatParDefaut equals to
        defaultParametreCodeBarresFiltering(
            "formatParDefaut.equals=" + DEFAULT_FORMAT_PAR_DEFAUT,
            "formatParDefaut.equals=" + UPDATED_FORMAT_PAR_DEFAUT
        );
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByFormatParDefautIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where formatParDefaut in
        defaultParametreCodeBarresFiltering(
            "formatParDefaut.in=" + DEFAULT_FORMAT_PAR_DEFAUT + "," + UPDATED_FORMAT_PAR_DEFAUT,
            "formatParDefaut.in=" + UPDATED_FORMAT_PAR_DEFAUT
        );
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByFormatParDefautIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where formatParDefaut is not null
        defaultParametreCodeBarresFiltering("formatParDefaut.specified=true", "formatParDefaut.specified=false");
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByPrefixeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where prefixe equals to
        defaultParametreCodeBarresFiltering("prefixe.equals=" + DEFAULT_PREFIXE, "prefixe.equals=" + UPDATED_PREFIXE);
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByPrefixeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where prefixe in
        defaultParametreCodeBarresFiltering("prefixe.in=" + DEFAULT_PREFIXE + "," + UPDATED_PREFIXE, "prefixe.in=" + UPDATED_PREFIXE);
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByPrefixeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where prefixe is not null
        defaultParametreCodeBarresFiltering("prefixe.specified=true", "prefixe.specified=false");
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByPrefixeContainsSomething() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where prefixe contains
        defaultParametreCodeBarresFiltering("prefixe.contains=" + DEFAULT_PREFIXE, "prefixe.contains=" + UPDATED_PREFIXE);
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByPrefixeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where prefixe does not contain
        defaultParametreCodeBarresFiltering("prefixe.doesNotContain=" + UPDATED_PREFIXE, "prefixe.doesNotContain=" + DEFAULT_PREFIXE);
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByLongueurIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where longueur equals to
        defaultParametreCodeBarresFiltering("longueur.equals=" + DEFAULT_LONGUEUR, "longueur.equals=" + UPDATED_LONGUEUR);
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByLongueurIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where longueur in
        defaultParametreCodeBarresFiltering("longueur.in=" + DEFAULT_LONGUEUR + "," + UPDATED_LONGUEUR, "longueur.in=" + UPDATED_LONGUEUR);
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByLongueurIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where longueur is not null
        defaultParametreCodeBarresFiltering("longueur.specified=true", "longueur.specified=false");
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByLongueurIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where longueur is greater than or equal to
        defaultParametreCodeBarresFiltering(
            "longueur.greaterThanOrEqual=" + DEFAULT_LONGUEUR,
            "longueur.greaterThanOrEqual=" + (DEFAULT_LONGUEUR + 1)
        );
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByLongueurIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where longueur is less than or equal to
        defaultParametreCodeBarresFiltering("longueur.lessThanOrEqual=" + DEFAULT_LONGUEUR, "longueur.lessThanOrEqual=" + SMALLER_LONGUEUR);
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByLongueurIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where longueur is less than
        defaultParametreCodeBarresFiltering("longueur.lessThan=" + (DEFAULT_LONGUEUR + 1), "longueur.lessThan=" + DEFAULT_LONGUEUR);
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByLongueurIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where longueur is greater than
        defaultParametreCodeBarresFiltering("longueur.greaterThan=" + SMALLER_LONGUEUR, "longueur.greaterThan=" + DEFAULT_LONGUEUR);
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByActifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where actif equals to
        defaultParametreCodeBarresFiltering("actif.equals=" + DEFAULT_ACTIF, "actif.equals=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByActifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where actif in
        defaultParametreCodeBarresFiltering("actif.in=" + DEFAULT_ACTIF + "," + UPDATED_ACTIF, "actif.in=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllParametreCodeBarresesByActifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        // Get all the parametreCodeBarresList where actif is not null
        defaultParametreCodeBarresFiltering("actif.specified=true", "actif.specified=false");
    }

    private void defaultParametreCodeBarresFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultParametreCodeBarresShouldBeFound(shouldBeFound);
        defaultParametreCodeBarresShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultParametreCodeBarresShouldBeFound(String filter) throws Exception {
        restParametreCodeBarresMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parametreCodeBarres.getId().intValue())))
            .andExpect(jsonPath("$.[*].formatParDefaut").value(hasItem(DEFAULT_FORMAT_PAR_DEFAUT.toString())))
            .andExpect(jsonPath("$.[*].prefixe").value(hasItem(DEFAULT_PREFIXE)))
            .andExpect(jsonPath("$.[*].longueur").value(hasItem(DEFAULT_LONGUEUR)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));

        // Check, that the count call also returns 1
        restParametreCodeBarresMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultParametreCodeBarresShouldNotBeFound(String filter) throws Exception {
        restParametreCodeBarresMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restParametreCodeBarresMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingParametreCodeBarres() throws Exception {
        // Get the parametreCodeBarres
        restParametreCodeBarresMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParametreCodeBarres() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parametreCodeBarres
        ParametreCodeBarres updatedParametreCodeBarres = parametreCodeBarresRepository.findById(parametreCodeBarres.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParametreCodeBarres are not directly saved in db
        em.detach(updatedParametreCodeBarres);
        updatedParametreCodeBarres
            .formatParDefaut(UPDATED_FORMAT_PAR_DEFAUT)
            .prefixe(UPDATED_PREFIXE)
            .longueur(UPDATED_LONGUEUR)
            .actif(UPDATED_ACTIF);
        ParametreCodeBarresDTO parametreCodeBarresDTO = parametreCodeBarresMapper.toDto(updatedParametreCodeBarres);

        restParametreCodeBarresMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parametreCodeBarresDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parametreCodeBarresDTO))
            )
            .andExpect(status().isOk());

        // Validate the ParametreCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParametreCodeBarresToMatchAllProperties(updatedParametreCodeBarres);
    }

    @Test
    @Transactional
    void putNonExistingParametreCodeBarres() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parametreCodeBarres.setId(longCount.incrementAndGet());

        // Create the ParametreCodeBarres
        ParametreCodeBarresDTO parametreCodeBarresDTO = parametreCodeBarresMapper.toDto(parametreCodeBarres);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParametreCodeBarresMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parametreCodeBarresDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parametreCodeBarresDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParametreCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParametreCodeBarres() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parametreCodeBarres.setId(longCount.incrementAndGet());

        // Create the ParametreCodeBarres
        ParametreCodeBarresDTO parametreCodeBarresDTO = parametreCodeBarresMapper.toDto(parametreCodeBarres);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParametreCodeBarresMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parametreCodeBarresDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParametreCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParametreCodeBarres() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parametreCodeBarres.setId(longCount.incrementAndGet());

        // Create the ParametreCodeBarres
        ParametreCodeBarresDTO parametreCodeBarresDTO = parametreCodeBarresMapper.toDto(parametreCodeBarres);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParametreCodeBarresMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametreCodeBarresDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParametreCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParametreCodeBarresWithPatch() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parametreCodeBarres using partial update
        ParametreCodeBarres partialUpdatedParametreCodeBarres = new ParametreCodeBarres();
        partialUpdatedParametreCodeBarres.setId(parametreCodeBarres.getId());

        partialUpdatedParametreCodeBarres.longueur(UPDATED_LONGUEUR).actif(UPDATED_ACTIF);

        restParametreCodeBarresMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParametreCodeBarres.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParametreCodeBarres))
            )
            .andExpect(status().isOk());

        // Validate the ParametreCodeBarres in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParametreCodeBarresUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedParametreCodeBarres, parametreCodeBarres),
            getPersistedParametreCodeBarres(parametreCodeBarres)
        );
    }

    @Test
    @Transactional
    void fullUpdateParametreCodeBarresWithPatch() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parametreCodeBarres using partial update
        ParametreCodeBarres partialUpdatedParametreCodeBarres = new ParametreCodeBarres();
        partialUpdatedParametreCodeBarres.setId(parametreCodeBarres.getId());

        partialUpdatedParametreCodeBarres
            .formatParDefaut(UPDATED_FORMAT_PAR_DEFAUT)
            .prefixe(UPDATED_PREFIXE)
            .longueur(UPDATED_LONGUEUR)
            .actif(UPDATED_ACTIF);

        restParametreCodeBarresMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParametreCodeBarres.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParametreCodeBarres))
            )
            .andExpect(status().isOk());

        // Validate the ParametreCodeBarres in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParametreCodeBarresUpdatableFieldsEquals(
            partialUpdatedParametreCodeBarres,
            getPersistedParametreCodeBarres(partialUpdatedParametreCodeBarres)
        );
    }

    @Test
    @Transactional
    void patchNonExistingParametreCodeBarres() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parametreCodeBarres.setId(longCount.incrementAndGet());

        // Create the ParametreCodeBarres
        ParametreCodeBarresDTO parametreCodeBarresDTO = parametreCodeBarresMapper.toDto(parametreCodeBarres);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParametreCodeBarresMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, parametreCodeBarresDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(parametreCodeBarresDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParametreCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParametreCodeBarres() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parametreCodeBarres.setId(longCount.incrementAndGet());

        // Create the ParametreCodeBarres
        ParametreCodeBarresDTO parametreCodeBarresDTO = parametreCodeBarresMapper.toDto(parametreCodeBarres);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParametreCodeBarresMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(parametreCodeBarresDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParametreCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParametreCodeBarres() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        parametreCodeBarres.setId(longCount.incrementAndGet());

        // Create the ParametreCodeBarres
        ParametreCodeBarresDTO parametreCodeBarresDTO = parametreCodeBarresMapper.toDto(parametreCodeBarres);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParametreCodeBarresMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(parametreCodeBarresDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParametreCodeBarres in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParametreCodeBarres() throws Exception {
        // Initialize the database
        insertedParametreCodeBarres = parametreCodeBarresRepository.saveAndFlush(parametreCodeBarres);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the parametreCodeBarres
        restParametreCodeBarresMockMvc
            .perform(delete(ENTITY_API_URL_ID, parametreCodeBarres.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return parametreCodeBarresRepository.count();
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

    protected ParametreCodeBarres getPersistedParametreCodeBarres(ParametreCodeBarres parametreCodeBarres) {
        return parametreCodeBarresRepository.findById(parametreCodeBarres.getId()).orElseThrow();
    }

    protected void assertPersistedParametreCodeBarresToMatchAllProperties(ParametreCodeBarres expectedParametreCodeBarres) {
        assertParametreCodeBarresAllPropertiesEquals(
            expectedParametreCodeBarres,
            getPersistedParametreCodeBarres(expectedParametreCodeBarres)
        );
    }

    protected void assertPersistedParametreCodeBarresToMatchUpdatableProperties(ParametreCodeBarres expectedParametreCodeBarres) {
        assertParametreCodeBarresAllUpdatablePropertiesEquals(
            expectedParametreCodeBarres,
            getPersistedParametreCodeBarres(expectedParametreCodeBarres)
        );
    }
}
