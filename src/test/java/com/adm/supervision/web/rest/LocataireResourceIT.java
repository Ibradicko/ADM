package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.LocataireAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.TypeLocataire;
import com.adm.supervision.repository.LocataireRepository;
import com.adm.supervision.service.dto.LocataireDTO;
import com.adm.supervision.service.mapper.LocataireMapper;
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
 * Integration tests for the {@link LocataireResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LocataireResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final TypeLocataire DEFAULT_TYPE_LOCATAIRE = TypeLocataire.PERSONNE_PHYSIQUE;
    private static final TypeLocataire UPDATED_TYPE_LOCATAIRE = TypeLocataire.PERSONNE_MORALE;

    private static final String DEFAULT_NUMERO_IDENTIFICATION = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO_IDENTIFICATION = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final StatutGeneral DEFAULT_STATUT = StatutGeneral.ACTIF;
    private static final StatutGeneral UPDATED_STATUT = StatutGeneral.INACTIF;

    private static final Instant DEFAULT_DATE_CREATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/locataires";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LocataireRepository locataireRepository;

    @Autowired
    private LocataireMapper locataireMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLocataireMockMvc;

    private Locataire locataire;

    private Locataire insertedLocataire;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Locataire createEntity() {
        return new Locataire()
            .code(DEFAULT_CODE)
            .nom(DEFAULT_NOM)
            .typeLocataire(DEFAULT_TYPE_LOCATAIRE)
            .numeroIdentification(DEFAULT_NUMERO_IDENTIFICATION)
            .telephone(DEFAULT_TELEPHONE)
            .email(DEFAULT_EMAIL)
            .adresse(DEFAULT_ADRESSE)
            .statut(DEFAULT_STATUT)
            .dateCreation(DEFAULT_DATE_CREATION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Locataire createUpdatedEntity() {
        return new Locataire()
            .code(UPDATED_CODE)
            .nom(UPDATED_NOM)
            .typeLocataire(UPDATED_TYPE_LOCATAIRE)
            .numeroIdentification(UPDATED_NUMERO_IDENTIFICATION)
            .telephone(UPDATED_TELEPHONE)
            .email(UPDATED_EMAIL)
            .adresse(UPDATED_ADRESSE)
            .statut(UPDATED_STATUT)
            .dateCreation(UPDATED_DATE_CREATION);
    }

    @BeforeEach
    void initTest() {
        locataire = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLocataire != null) {
            locataireRepository.delete(insertedLocataire);
            insertedLocataire = null;
        }
    }

    @Test
    @Transactional
    void createLocataire() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Locataire
        LocataireDTO locataireDTO = locataireMapper.toDto(locataire);
        var returnedLocataireDTO = om.readValue(
            restLocataireMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locataireDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LocataireDTO.class
        );

        // Validate the Locataire in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLocataire = locataireMapper.toEntity(returnedLocataireDTO);
        assertLocataireUpdatableFieldsEquals(returnedLocataire, getPersistedLocataire(returnedLocataire));

        insertedLocataire = returnedLocataire;
    }

    @Test
    @Transactional
    void createLocataireWithExistingId() throws Exception {
        // Create the Locataire with an existing ID
        locataire.setId(1L);
        LocataireDTO locataireDTO = locataireMapper.toDto(locataire);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLocataireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locataireDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Locataire in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        locataire.setCode(null);

        // Create the Locataire, which fails.
        LocataireDTO locataireDTO = locataireMapper.toDto(locataire);

        restLocataireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locataireDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        locataire.setNom(null);

        // Create the Locataire, which fails.
        LocataireDTO locataireDTO = locataireMapper.toDto(locataire);

        restLocataireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locataireDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeLocataireIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        locataire.setTypeLocataire(null);

        // Create the Locataire, which fails.
        LocataireDTO locataireDTO = locataireMapper.toDto(locataire);

        restLocataireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locataireDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        locataire.setStatut(null);

        // Create the Locataire, which fails.
        LocataireDTO locataireDTO = locataireMapper.toDto(locataire);

        restLocataireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locataireDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        locataire.setDateCreation(null);

        // Create the Locataire, which fails.
        LocataireDTO locataireDTO = locataireMapper.toDto(locataire);

        restLocataireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locataireDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLocataires() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList
        restLocataireMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locataire.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].typeLocataire").value(hasItem(DEFAULT_TYPE_LOCATAIRE.toString())))
            .andExpect(jsonPath("$.[*].numeroIdentification").value(hasItem(DEFAULT_NUMERO_IDENTIFICATION)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())));
    }

    @Test
    @Transactional
    void getLocataire() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get the locataire
        restLocataireMockMvc
            .perform(get(ENTITY_API_URL_ID, locataire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(locataire.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.typeLocataire").value(DEFAULT_TYPE_LOCATAIRE.toString()))
            .andExpect(jsonPath("$.numeroIdentification").value(DEFAULT_NUMERO_IDENTIFICATION))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()));
    }

    @Test
    @Transactional
    void getLocatairesByIdFiltering() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        Long id = locataire.getId();

        defaultLocataireFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLocataireFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLocataireFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLocatairesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where code equals to
        defaultLocataireFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllLocatairesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where code in
        defaultLocataireFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllLocatairesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where code is not null
        defaultLocataireFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllLocatairesByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where code contains
        defaultLocataireFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllLocatairesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where code does not contain
        defaultLocataireFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllLocatairesByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where nom equals to
        defaultLocataireFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllLocatairesByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where nom in
        defaultLocataireFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllLocatairesByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where nom is not null
        defaultLocataireFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllLocatairesByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where nom contains
        defaultLocataireFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllLocatairesByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where nom does not contain
        defaultLocataireFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    @Transactional
    void getAllLocatairesByTypeLocataireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where typeLocataire equals to
        defaultLocataireFiltering("typeLocataire.equals=" + DEFAULT_TYPE_LOCATAIRE, "typeLocataire.equals=" + UPDATED_TYPE_LOCATAIRE);
    }

    @Test
    @Transactional
    void getAllLocatairesByTypeLocataireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where typeLocataire in
        defaultLocataireFiltering(
            "typeLocataire.in=" + DEFAULT_TYPE_LOCATAIRE + "," + UPDATED_TYPE_LOCATAIRE,
            "typeLocataire.in=" + UPDATED_TYPE_LOCATAIRE
        );
    }

    @Test
    @Transactional
    void getAllLocatairesByTypeLocataireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where typeLocataire is not null
        defaultLocataireFiltering("typeLocataire.specified=true", "typeLocataire.specified=false");
    }

    @Test
    @Transactional
    void getAllLocatairesByNumeroIdentificationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where numeroIdentification equals to
        defaultLocataireFiltering(
            "numeroIdentification.equals=" + DEFAULT_NUMERO_IDENTIFICATION,
            "numeroIdentification.equals=" + UPDATED_NUMERO_IDENTIFICATION
        );
    }

    @Test
    @Transactional
    void getAllLocatairesByNumeroIdentificationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where numeroIdentification in
        defaultLocataireFiltering(
            "numeroIdentification.in=" + DEFAULT_NUMERO_IDENTIFICATION + "," + UPDATED_NUMERO_IDENTIFICATION,
            "numeroIdentification.in=" + UPDATED_NUMERO_IDENTIFICATION
        );
    }

    @Test
    @Transactional
    void getAllLocatairesByNumeroIdentificationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where numeroIdentification is not null
        defaultLocataireFiltering("numeroIdentification.specified=true", "numeroIdentification.specified=false");
    }

    @Test
    @Transactional
    void getAllLocatairesByNumeroIdentificationContainsSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where numeroIdentification contains
        defaultLocataireFiltering(
            "numeroIdentification.contains=" + DEFAULT_NUMERO_IDENTIFICATION,
            "numeroIdentification.contains=" + UPDATED_NUMERO_IDENTIFICATION
        );
    }

    @Test
    @Transactional
    void getAllLocatairesByNumeroIdentificationNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where numeroIdentification does not contain
        defaultLocataireFiltering(
            "numeroIdentification.doesNotContain=" + UPDATED_NUMERO_IDENTIFICATION,
            "numeroIdentification.doesNotContain=" + DEFAULT_NUMERO_IDENTIFICATION
        );
    }

    @Test
    @Transactional
    void getAllLocatairesByTelephoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where telephone equals to
        defaultLocataireFiltering("telephone.equals=" + DEFAULT_TELEPHONE, "telephone.equals=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllLocatairesByTelephoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where telephone in
        defaultLocataireFiltering("telephone.in=" + DEFAULT_TELEPHONE + "," + UPDATED_TELEPHONE, "telephone.in=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllLocatairesByTelephoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where telephone is not null
        defaultLocataireFiltering("telephone.specified=true", "telephone.specified=false");
    }

    @Test
    @Transactional
    void getAllLocatairesByTelephoneContainsSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where telephone contains
        defaultLocataireFiltering("telephone.contains=" + DEFAULT_TELEPHONE, "telephone.contains=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllLocatairesByTelephoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where telephone does not contain
        defaultLocataireFiltering("telephone.doesNotContain=" + UPDATED_TELEPHONE, "telephone.doesNotContain=" + DEFAULT_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllLocatairesByEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where email equals to
        defaultLocataireFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllLocatairesByEmailIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where email in
        defaultLocataireFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllLocatairesByEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where email is not null
        defaultLocataireFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    @Transactional
    void getAllLocatairesByEmailContainsSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where email contains
        defaultLocataireFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    @Transactional
    void getAllLocatairesByEmailNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where email does not contain
        defaultLocataireFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    @Transactional
    void getAllLocatairesByAdresseIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where adresse equals to
        defaultLocataireFiltering("adresse.equals=" + DEFAULT_ADRESSE, "adresse.equals=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllLocatairesByAdresseIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where adresse in
        defaultLocataireFiltering("adresse.in=" + DEFAULT_ADRESSE + "," + UPDATED_ADRESSE, "adresse.in=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllLocatairesByAdresseIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where adresse is not null
        defaultLocataireFiltering("adresse.specified=true", "adresse.specified=false");
    }

    @Test
    @Transactional
    void getAllLocatairesByAdresseContainsSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where adresse contains
        defaultLocataireFiltering("adresse.contains=" + DEFAULT_ADRESSE, "adresse.contains=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllLocatairesByAdresseNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where adresse does not contain
        defaultLocataireFiltering("adresse.doesNotContain=" + UPDATED_ADRESSE, "adresse.doesNotContain=" + DEFAULT_ADRESSE);
    }

    @Test
    @Transactional
    void getAllLocatairesByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where statut equals to
        defaultLocataireFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllLocatairesByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where statut in
        defaultLocataireFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllLocatairesByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where statut is not null
        defaultLocataireFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllLocatairesByDateCreationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where dateCreation equals to
        defaultLocataireFiltering("dateCreation.equals=" + DEFAULT_DATE_CREATION, "dateCreation.equals=" + UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    void getAllLocatairesByDateCreationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where dateCreation in
        defaultLocataireFiltering(
            "dateCreation.in=" + DEFAULT_DATE_CREATION + "," + UPDATED_DATE_CREATION,
            "dateCreation.in=" + UPDATED_DATE_CREATION
        );
    }

    @Test
    @Transactional
    void getAllLocatairesByDateCreationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        // Get all the locataireList where dateCreation is not null
        defaultLocataireFiltering("dateCreation.specified=true", "dateCreation.specified=false");
    }

    private void defaultLocataireFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLocataireShouldBeFound(shouldBeFound);
        defaultLocataireShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLocataireShouldBeFound(String filter) throws Exception {
        restLocataireMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locataire.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].typeLocataire").value(hasItem(DEFAULT_TYPE_LOCATAIRE.toString())))
            .andExpect(jsonPath("$.[*].numeroIdentification").value(hasItem(DEFAULT_NUMERO_IDENTIFICATION)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())));

        // Check, that the count call also returns 1
        restLocataireMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLocataireShouldNotBeFound(String filter) throws Exception {
        restLocataireMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLocataireMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLocataire() throws Exception {
        // Get the locataire
        restLocataireMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLocataire() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locataire
        Locataire updatedLocataire = locataireRepository.findById(locataire.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLocataire are not directly saved in db
        em.detach(updatedLocataire);
        updatedLocataire
            .code(UPDATED_CODE)
            .nom(UPDATED_NOM)
            .typeLocataire(UPDATED_TYPE_LOCATAIRE)
            .numeroIdentification(UPDATED_NUMERO_IDENTIFICATION)
            .telephone(UPDATED_TELEPHONE)
            .email(UPDATED_EMAIL)
            .adresse(UPDATED_ADRESSE)
            .statut(UPDATED_STATUT)
            .dateCreation(UPDATED_DATE_CREATION);
        LocataireDTO locataireDTO = locataireMapper.toDto(updatedLocataire);

        restLocataireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, locataireDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locataireDTO))
            )
            .andExpect(status().isOk());

        // Validate the Locataire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLocataireToMatchAllProperties(updatedLocataire);
    }

    @Test
    @Transactional
    void putNonExistingLocataire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locataire.setId(longCount.incrementAndGet());

        // Create the Locataire
        LocataireDTO locataireDTO = locataireMapper.toDto(locataire);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocataireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, locataireDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locataireDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Locataire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLocataire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locataire.setId(longCount.incrementAndGet());

        // Create the Locataire
        LocataireDTO locataireDTO = locataireMapper.toDto(locataire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocataireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(locataireDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Locataire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLocataire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locataire.setId(longCount.incrementAndGet());

        // Create the Locataire
        LocataireDTO locataireDTO = locataireMapper.toDto(locataire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocataireMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(locataireDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Locataire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLocataireWithPatch() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locataire using partial update
        Locataire partialUpdatedLocataire = new Locataire();
        partialUpdatedLocataire.setId(locataire.getId());

        partialUpdatedLocataire
            .typeLocataire(UPDATED_TYPE_LOCATAIRE)
            .numeroIdentification(UPDATED_NUMERO_IDENTIFICATION)
            .email(UPDATED_EMAIL)
            .statut(UPDATED_STATUT);

        restLocataireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocataire.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLocataire))
            )
            .andExpect(status().isOk());

        // Validate the Locataire in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocataireUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLocataire, locataire),
            getPersistedLocataire(locataire)
        );
    }

    @Test
    @Transactional
    void fullUpdateLocataireWithPatch() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the locataire using partial update
        Locataire partialUpdatedLocataire = new Locataire();
        partialUpdatedLocataire.setId(locataire.getId());

        partialUpdatedLocataire
            .code(UPDATED_CODE)
            .nom(UPDATED_NOM)
            .typeLocataire(UPDATED_TYPE_LOCATAIRE)
            .numeroIdentification(UPDATED_NUMERO_IDENTIFICATION)
            .telephone(UPDATED_TELEPHONE)
            .email(UPDATED_EMAIL)
            .adresse(UPDATED_ADRESSE)
            .statut(UPDATED_STATUT)
            .dateCreation(UPDATED_DATE_CREATION);

        restLocataireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocataire.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLocataire))
            )
            .andExpect(status().isOk());

        // Validate the Locataire in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocataireUpdatableFieldsEquals(partialUpdatedLocataire, getPersistedLocataire(partialUpdatedLocataire));
    }

    @Test
    @Transactional
    void patchNonExistingLocataire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locataire.setId(longCount.incrementAndGet());

        // Create the Locataire
        LocataireDTO locataireDTO = locataireMapper.toDto(locataire);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocataireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, locataireDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(locataireDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Locataire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLocataire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locataire.setId(longCount.incrementAndGet());

        // Create the Locataire
        LocataireDTO locataireDTO = locataireMapper.toDto(locataire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocataireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(locataireDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Locataire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLocataire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        locataire.setId(longCount.incrementAndGet());

        // Create the Locataire
        LocataireDTO locataireDTO = locataireMapper.toDto(locataire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocataireMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(locataireDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Locataire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLocataire() throws Exception {
        // Initialize the database
        insertedLocataire = locataireRepository.saveAndFlush(locataire);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the locataire
        restLocataireMockMvc
            .perform(delete(ENTITY_API_URL_ID, locataire.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return locataireRepository.count();
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

    protected Locataire getPersistedLocataire(Locataire locataire) {
        return locataireRepository.findById(locataire.getId()).orElseThrow();
    }

    protected void assertPersistedLocataireToMatchAllProperties(Locataire expectedLocataire) {
        assertLocataireAllPropertiesEquals(expectedLocataire, getPersistedLocataire(expectedLocataire));
    }

    protected void assertPersistedLocataireToMatchUpdatableProperties(Locataire expectedLocataire) {
        assertLocataireAllUpdatablePropertiesEquals(expectedLocataire, getPersistedLocataire(expectedLocataire));
    }
}
