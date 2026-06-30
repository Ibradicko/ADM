package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.BoutiqueAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.TypeBoutique;
import com.adm.supervision.repository.BoutiqueRepository;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.service.mapper.BoutiqueMapper;
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
 * Integration tests for the {@link BoutiqueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BoutiqueResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final TypeBoutique DEFAULT_TYPE = TypeBoutique.DUTY_FREE;
    private static final TypeBoutique UPDATED_TYPE = TypeBoutique.RESTAURATION;

    private static final String DEFAULT_EMPLACEMENT = "AAAAAAAAAA";
    private static final String UPDATED_EMPLACEMENT = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final StatutGeneral DEFAULT_STATUT = StatutGeneral.ACTIF;
    private static final StatutGeneral UPDATED_STATUT = StatutGeneral.INACTIF;

    private static final Instant DEFAULT_DATE_CREATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/boutiques";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BoutiqueRepository boutiqueRepository;

    @Autowired
    private BoutiqueMapper boutiqueMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBoutiqueMockMvc;

    private Boutique boutique;

    private Boutique insertedBoutique;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Boutique createEntity() {
        return new Boutique()
            .code(DEFAULT_CODE)
            .nom(DEFAULT_NOM)
            .type(DEFAULT_TYPE)
            .emplacement(DEFAULT_EMPLACEMENT)
            .telephone(DEFAULT_TELEPHONE)
            .statut(DEFAULT_STATUT)
            .dateCreation(DEFAULT_DATE_CREATION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Boutique createUpdatedEntity() {
        return new Boutique()
            .code(UPDATED_CODE)
            .nom(UPDATED_NOM)
            .type(UPDATED_TYPE)
            .emplacement(UPDATED_EMPLACEMENT)
            .telephone(UPDATED_TELEPHONE)
            .statut(UPDATED_STATUT)
            .dateCreation(UPDATED_DATE_CREATION);
    }

    @BeforeEach
    void initTest() {
        boutique = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBoutique != null) {
            boutiqueRepository.delete(insertedBoutique);
            insertedBoutique = null;
        }
    }

    @Test
    @Transactional
    void createBoutique() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Boutique
        BoutiqueDTO boutiqueDTO = boutiqueMapper.toDto(boutique);
        var returnedBoutiqueDTO = om.readValue(
            restBoutiqueMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boutiqueDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BoutiqueDTO.class
        );

        // Validate the Boutique in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBoutique = boutiqueMapper.toEntity(returnedBoutiqueDTO);
        assertBoutiqueUpdatableFieldsEquals(returnedBoutique, getPersistedBoutique(returnedBoutique));

        insertedBoutique = returnedBoutique;
    }

    @Test
    @Transactional
    void createBoutiqueWithExistingId() throws Exception {
        // Create the Boutique with an existing ID
        boutique.setId(1L);
        BoutiqueDTO boutiqueDTO = boutiqueMapper.toDto(boutique);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBoutiqueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boutiqueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Boutique in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        boutique.setCode(null);

        // Create the Boutique, which fails.
        BoutiqueDTO boutiqueDTO = boutiqueMapper.toDto(boutique);

        restBoutiqueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boutiqueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        boutique.setNom(null);

        // Create the Boutique, which fails.
        BoutiqueDTO boutiqueDTO = boutiqueMapper.toDto(boutique);

        restBoutiqueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boutiqueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        boutique.setStatut(null);

        // Create the Boutique, which fails.
        BoutiqueDTO boutiqueDTO = boutiqueMapper.toDto(boutique);

        restBoutiqueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boutiqueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        boutique.setDateCreation(null);

        // Create the Boutique, which fails.
        BoutiqueDTO boutiqueDTO = boutiqueMapper.toDto(boutique);

        restBoutiqueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boutiqueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBoutiques() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList
        restBoutiqueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(boutique.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].emplacement").value(hasItem(DEFAULT_EMPLACEMENT)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())));
    }

    @Test
    @Transactional
    void getBoutique() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get the boutique
        restBoutiqueMockMvc
            .perform(get(ENTITY_API_URL_ID, boutique.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(boutique.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.emplacement").value(DEFAULT_EMPLACEMENT))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()));
    }

    @Test
    @Transactional
    void getBoutiquesByIdFiltering() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        Long id = boutique.getId();

        defaultBoutiqueFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBoutiqueFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBoutiqueFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllBoutiquesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where code equals to
        defaultBoutiqueFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllBoutiquesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where code in
        defaultBoutiqueFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllBoutiquesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where code is not null
        defaultBoutiqueFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllBoutiquesByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where code contains
        defaultBoutiqueFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllBoutiquesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where code does not contain
        defaultBoutiqueFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllBoutiquesByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where nom equals to
        defaultBoutiqueFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllBoutiquesByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where nom in
        defaultBoutiqueFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllBoutiquesByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where nom is not null
        defaultBoutiqueFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllBoutiquesByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where nom contains
        defaultBoutiqueFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllBoutiquesByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where nom does not contain
        defaultBoutiqueFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    @Transactional
    void getAllBoutiquesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where type equals to
        defaultBoutiqueFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllBoutiquesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where type in
        defaultBoutiqueFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllBoutiquesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where type is not null
        defaultBoutiqueFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllBoutiquesByEmplacementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where emplacement equals to
        defaultBoutiqueFiltering("emplacement.equals=" + DEFAULT_EMPLACEMENT, "emplacement.equals=" + UPDATED_EMPLACEMENT);
    }

    @Test
    @Transactional
    void getAllBoutiquesByEmplacementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where emplacement in
        defaultBoutiqueFiltering(
            "emplacement.in=" + DEFAULT_EMPLACEMENT + "," + UPDATED_EMPLACEMENT,
            "emplacement.in=" + UPDATED_EMPLACEMENT
        );
    }

    @Test
    @Transactional
    void getAllBoutiquesByEmplacementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where emplacement is not null
        defaultBoutiqueFiltering("emplacement.specified=true", "emplacement.specified=false");
    }

    @Test
    @Transactional
    void getAllBoutiquesByEmplacementContainsSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where emplacement contains
        defaultBoutiqueFiltering("emplacement.contains=" + DEFAULT_EMPLACEMENT, "emplacement.contains=" + UPDATED_EMPLACEMENT);
    }

    @Test
    @Transactional
    void getAllBoutiquesByEmplacementNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where emplacement does not contain
        defaultBoutiqueFiltering("emplacement.doesNotContain=" + UPDATED_EMPLACEMENT, "emplacement.doesNotContain=" + DEFAULT_EMPLACEMENT);
    }

    @Test
    @Transactional
    void getAllBoutiquesByTelephoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where telephone equals to
        defaultBoutiqueFiltering("telephone.equals=" + DEFAULT_TELEPHONE, "telephone.equals=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllBoutiquesByTelephoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where telephone in
        defaultBoutiqueFiltering("telephone.in=" + DEFAULT_TELEPHONE + "," + UPDATED_TELEPHONE, "telephone.in=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllBoutiquesByTelephoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where telephone is not null
        defaultBoutiqueFiltering("telephone.specified=true", "telephone.specified=false");
    }

    @Test
    @Transactional
    void getAllBoutiquesByTelephoneContainsSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where telephone contains
        defaultBoutiqueFiltering("telephone.contains=" + DEFAULT_TELEPHONE, "telephone.contains=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllBoutiquesByTelephoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where telephone does not contain
        defaultBoutiqueFiltering("telephone.doesNotContain=" + UPDATED_TELEPHONE, "telephone.doesNotContain=" + DEFAULT_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllBoutiquesByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where statut equals to
        defaultBoutiqueFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllBoutiquesByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where statut in
        defaultBoutiqueFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllBoutiquesByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where statut is not null
        defaultBoutiqueFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllBoutiquesByDateCreationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where dateCreation equals to
        defaultBoutiqueFiltering("dateCreation.equals=" + DEFAULT_DATE_CREATION, "dateCreation.equals=" + UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    void getAllBoutiquesByDateCreationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where dateCreation in
        defaultBoutiqueFiltering(
            "dateCreation.in=" + DEFAULT_DATE_CREATION + "," + UPDATED_DATE_CREATION,
            "dateCreation.in=" + UPDATED_DATE_CREATION
        );
    }

    @Test
    @Transactional
    void getAllBoutiquesByDateCreationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        // Get all the boutiqueList where dateCreation is not null
        defaultBoutiqueFiltering("dateCreation.specified=true", "dateCreation.specified=false");
    }

    private void defaultBoutiqueFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultBoutiqueShouldBeFound(shouldBeFound);
        defaultBoutiqueShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBoutiqueShouldBeFound(String filter) throws Exception {
        restBoutiqueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(boutique.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].emplacement").value(hasItem(DEFAULT_EMPLACEMENT)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())));

        // Check, that the count call also returns 1
        restBoutiqueMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBoutiqueShouldNotBeFound(String filter) throws Exception {
        restBoutiqueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restBoutiqueMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingBoutique() throws Exception {
        // Get the boutique
        restBoutiqueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBoutique() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the boutique
        Boutique updatedBoutique = boutiqueRepository.findById(boutique.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBoutique are not directly saved in db
        em.detach(updatedBoutique);
        updatedBoutique
            .code(UPDATED_CODE)
            .nom(UPDATED_NOM)
            .type(UPDATED_TYPE)
            .emplacement(UPDATED_EMPLACEMENT)
            .telephone(UPDATED_TELEPHONE)
            .statut(UPDATED_STATUT)
            .dateCreation(UPDATED_DATE_CREATION);
        BoutiqueDTO boutiqueDTO = boutiqueMapper.toDto(updatedBoutique);

        restBoutiqueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, boutiqueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boutiqueDTO))
            )
            .andExpect(status().isOk());

        // Validate the Boutique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBoutiqueToMatchAllProperties(updatedBoutique);
    }

    @Test
    @Transactional
    void putNonExistingBoutique() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boutique.setId(longCount.incrementAndGet());

        // Create the Boutique
        BoutiqueDTO boutiqueDTO = boutiqueMapper.toDto(boutique);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoutiqueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, boutiqueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boutiqueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boutique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBoutique() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boutique.setId(longCount.incrementAndGet());

        // Create the Boutique
        BoutiqueDTO boutiqueDTO = boutiqueMapper.toDto(boutique);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoutiqueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boutiqueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boutique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBoutique() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boutique.setId(longCount.incrementAndGet());

        // Create the Boutique
        BoutiqueDTO boutiqueDTO = boutiqueMapper.toDto(boutique);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoutiqueMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boutiqueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Boutique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBoutiqueWithPatch() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the boutique using partial update
        Boutique partialUpdatedBoutique = new Boutique();
        partialUpdatedBoutique.setId(boutique.getId());

        partialUpdatedBoutique.emplacement(UPDATED_EMPLACEMENT).dateCreation(UPDATED_DATE_CREATION);

        restBoutiqueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoutique.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBoutique))
            )
            .andExpect(status().isOk());

        // Validate the Boutique in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBoutiqueUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedBoutique, boutique), getPersistedBoutique(boutique));
    }

    @Test
    @Transactional
    void fullUpdateBoutiqueWithPatch() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the boutique using partial update
        Boutique partialUpdatedBoutique = new Boutique();
        partialUpdatedBoutique.setId(boutique.getId());

        partialUpdatedBoutique
            .code(UPDATED_CODE)
            .nom(UPDATED_NOM)
            .type(UPDATED_TYPE)
            .emplacement(UPDATED_EMPLACEMENT)
            .telephone(UPDATED_TELEPHONE)
            .statut(UPDATED_STATUT)
            .dateCreation(UPDATED_DATE_CREATION);

        restBoutiqueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoutique.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBoutique))
            )
            .andExpect(status().isOk());

        // Validate the Boutique in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBoutiqueUpdatableFieldsEquals(partialUpdatedBoutique, getPersistedBoutique(partialUpdatedBoutique));
    }

    @Test
    @Transactional
    void patchNonExistingBoutique() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boutique.setId(longCount.incrementAndGet());

        // Create the Boutique
        BoutiqueDTO boutiqueDTO = boutiqueMapper.toDto(boutique);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoutiqueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, boutiqueDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(boutiqueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boutique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBoutique() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boutique.setId(longCount.incrementAndGet());

        // Create the Boutique
        BoutiqueDTO boutiqueDTO = boutiqueMapper.toDto(boutique);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoutiqueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(boutiqueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Boutique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBoutique() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boutique.setId(longCount.incrementAndGet());

        // Create the Boutique
        BoutiqueDTO boutiqueDTO = boutiqueMapper.toDto(boutique);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoutiqueMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(boutiqueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Boutique in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBoutique() throws Exception {
        // Initialize the database
        insertedBoutique = boutiqueRepository.saveAndFlush(boutique);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the boutique
        restBoutiqueMockMvc
            .perform(delete(ENTITY_API_URL_ID, boutique.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return boutiqueRepository.count();
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

    protected Boutique getPersistedBoutique(Boutique boutique) {
        return boutiqueRepository.findById(boutique.getId()).orElseThrow();
    }

    protected void assertPersistedBoutiqueToMatchAllProperties(Boutique expectedBoutique) {
        assertBoutiqueAllPropertiesEquals(expectedBoutique, getPersistedBoutique(expectedBoutique));
    }

    protected void assertPersistedBoutiqueToMatchUpdatableProperties(Boutique expectedBoutique) {
        assertBoutiqueAllUpdatablePropertiesEquals(expectedBoutique, getPersistedBoutique(expectedBoutique));
    }
}
