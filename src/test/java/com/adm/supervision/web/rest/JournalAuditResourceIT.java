package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.JournalAuditAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.JournalAudit;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.JournalAuditRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.service.JournalAuditService;
import com.adm.supervision.service.dto.JournalAuditDTO;
import com.adm.supervision.service.mapper.JournalAuditMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link JournalAuditResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class JournalAuditResourceIT {

    private static final TypeActionAudit DEFAULT_TYPE_ACTION = TypeActionAudit.CONNEXION;
    private static final TypeActionAudit UPDATED_TYPE_ACTION = TypeActionAudit.DECONNEXION;

    private static final String DEFAULT_ENTITE_CONCERNEE = "AAAAAAAAAA";
    private static final String UPDATED_ENTITE_CONCERNEE = "BBBBBBBBBB";

    private static final String DEFAULT_IDENTIFIANT_ENTITE = "AAAAAAAAAA";
    private static final String UPDATED_IDENTIFIANT_ENTITE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE_IP = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE_IP = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_ACTION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_ACTION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/journal-audits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private JournalAuditRepository journalAuditRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private JournalAuditRepository journalAuditRepositoryMock;

    @Autowired
    private JournalAuditMapper journalAuditMapper;

    @Mock
    private JournalAuditService journalAuditServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJournalAuditMockMvc;

    private JournalAudit journalAudit;

    private JournalAudit insertedJournalAudit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JournalAudit createEntity() {
        return new JournalAudit()
            .typeAction(DEFAULT_TYPE_ACTION)
            .entiteConcernee(DEFAULT_ENTITE_CONCERNEE)
            .identifiantEntite(DEFAULT_IDENTIFIANT_ENTITE)
            .description(DEFAULT_DESCRIPTION)
            .adresseIp(DEFAULT_ADRESSE_IP)
            .dateAction(DEFAULT_DATE_ACTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JournalAudit createUpdatedEntity() {
        return new JournalAudit()
            .typeAction(UPDATED_TYPE_ACTION)
            .entiteConcernee(UPDATED_ENTITE_CONCERNEE)
            .identifiantEntite(UPDATED_IDENTIFIANT_ENTITE)
            .description(UPDATED_DESCRIPTION)
            .adresseIp(UPDATED_ADRESSE_IP)
            .dateAction(UPDATED_DATE_ACTION);
    }

    @BeforeEach
    void initTest() {
        journalAudit = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedJournalAudit != null) {
            journalAuditRepository.delete(insertedJournalAudit);
            insertedJournalAudit = null;
        }
    }

    @Test
    @Transactional
    void createJournalAudit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the JournalAudit
        JournalAuditDTO journalAuditDTO = journalAuditMapper.toDto(journalAudit);
        var returnedJournalAuditDTO = om.readValue(
            restJournalAuditMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalAuditDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            JournalAuditDTO.class
        );

        // Validate the JournalAudit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedJournalAudit = journalAuditMapper.toEntity(returnedJournalAuditDTO);
        assertJournalAuditUpdatableFieldsEquals(returnedJournalAudit, getPersistedJournalAudit(returnedJournalAudit));

        insertedJournalAudit = returnedJournalAudit;
    }

    @Test
    @Transactional
    void createJournalAuditWithExistingId() throws Exception {
        // Create the JournalAudit with an existing ID
        journalAudit.setId(1L);
        JournalAuditDTO journalAuditDTO = journalAuditMapper.toDto(journalAudit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJournalAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalAuditDTO)))
            .andExpect(status().isBadRequest());

        // Validate the JournalAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeActionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        journalAudit.setTypeAction(null);

        // Create the JournalAudit, which fails.
        JournalAuditDTO journalAuditDTO = journalAuditMapper.toDto(journalAudit);

        restJournalAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalAuditDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateActionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        journalAudit.setDateAction(null);

        // Create the JournalAudit, which fails.
        JournalAuditDTO journalAuditDTO = journalAuditMapper.toDto(journalAudit);

        restJournalAuditMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalAuditDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllJournalAudits() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList
        restJournalAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(journalAudit.getId().intValue())))
            .andExpect(jsonPath("$.[*].typeAction").value(hasItem(DEFAULT_TYPE_ACTION.toString())))
            .andExpect(jsonPath("$.[*].entiteConcernee").value(hasItem(DEFAULT_ENTITE_CONCERNEE)))
            .andExpect(jsonPath("$.[*].identifiantEntite").value(hasItem(DEFAULT_IDENTIFIANT_ENTITE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].adresseIp").value(hasItem(DEFAULT_ADRESSE_IP)))
            .andExpect(jsonPath("$.[*].dateAction").value(hasItem(DEFAULT_DATE_ACTION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllJournalAuditsWithEagerRelationshipsIsEnabled() throws Exception {
        when(journalAuditServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restJournalAuditMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(journalAuditServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllJournalAuditsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(journalAuditServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restJournalAuditMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(journalAuditRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getJournalAudit() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get the journalAudit
        restJournalAuditMockMvc
            .perform(get(ENTITY_API_URL_ID, journalAudit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(journalAudit.getId().intValue()))
            .andExpect(jsonPath("$.typeAction").value(DEFAULT_TYPE_ACTION.toString()))
            .andExpect(jsonPath("$.entiteConcernee").value(DEFAULT_ENTITE_CONCERNEE))
            .andExpect(jsonPath("$.identifiantEntite").value(DEFAULT_IDENTIFIANT_ENTITE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.adresseIp").value(DEFAULT_ADRESSE_IP))
            .andExpect(jsonPath("$.dateAction").value(DEFAULT_DATE_ACTION.toString()));
    }

    @Test
    @Transactional
    void getJournalAuditsByIdFiltering() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        Long id = journalAudit.getId();

        defaultJournalAuditFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultJournalAuditFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultJournalAuditFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllJournalAuditsByTypeActionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where typeAction equals to
        defaultJournalAuditFiltering("typeAction.equals=" + DEFAULT_TYPE_ACTION, "typeAction.equals=" + UPDATED_TYPE_ACTION);
    }

    @Test
    @Transactional
    void getAllJournalAuditsByTypeActionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where typeAction in
        defaultJournalAuditFiltering(
            "typeAction.in=" + DEFAULT_TYPE_ACTION + "," + UPDATED_TYPE_ACTION,
            "typeAction.in=" + UPDATED_TYPE_ACTION
        );
    }

    @Test
    @Transactional
    void getAllJournalAuditsByTypeActionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where typeAction is not null
        defaultJournalAuditFiltering("typeAction.specified=true", "typeAction.specified=false");
    }

    @Test
    @Transactional
    void getAllJournalAuditsByEntiteConcerneeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where entiteConcernee equals to
        defaultJournalAuditFiltering(
            "entiteConcernee.equals=" + DEFAULT_ENTITE_CONCERNEE,
            "entiteConcernee.equals=" + UPDATED_ENTITE_CONCERNEE
        );
    }

    @Test
    @Transactional
    void getAllJournalAuditsByEntiteConcerneeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where entiteConcernee in
        defaultJournalAuditFiltering(
            "entiteConcernee.in=" + DEFAULT_ENTITE_CONCERNEE + "," + UPDATED_ENTITE_CONCERNEE,
            "entiteConcernee.in=" + UPDATED_ENTITE_CONCERNEE
        );
    }

    @Test
    @Transactional
    void getAllJournalAuditsByEntiteConcerneeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where entiteConcernee is not null
        defaultJournalAuditFiltering("entiteConcernee.specified=true", "entiteConcernee.specified=false");
    }

    @Test
    @Transactional
    void getAllJournalAuditsByEntiteConcerneeContainsSomething() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where entiteConcernee contains
        defaultJournalAuditFiltering(
            "entiteConcernee.contains=" + DEFAULT_ENTITE_CONCERNEE,
            "entiteConcernee.contains=" + UPDATED_ENTITE_CONCERNEE
        );
    }

    @Test
    @Transactional
    void getAllJournalAuditsByEntiteConcerneeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where entiteConcernee does not contain
        defaultJournalAuditFiltering(
            "entiteConcernee.doesNotContain=" + UPDATED_ENTITE_CONCERNEE,
            "entiteConcernee.doesNotContain=" + DEFAULT_ENTITE_CONCERNEE
        );
    }

    @Test
    @Transactional
    void getAllJournalAuditsByIdentifiantEntiteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where identifiantEntite equals to
        defaultJournalAuditFiltering(
            "identifiantEntite.equals=" + DEFAULT_IDENTIFIANT_ENTITE,
            "identifiantEntite.equals=" + UPDATED_IDENTIFIANT_ENTITE
        );
    }

    @Test
    @Transactional
    void getAllJournalAuditsByIdentifiantEntiteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where identifiantEntite in
        defaultJournalAuditFiltering(
            "identifiantEntite.in=" + DEFAULT_IDENTIFIANT_ENTITE + "," + UPDATED_IDENTIFIANT_ENTITE,
            "identifiantEntite.in=" + UPDATED_IDENTIFIANT_ENTITE
        );
    }

    @Test
    @Transactional
    void getAllJournalAuditsByIdentifiantEntiteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where identifiantEntite is not null
        defaultJournalAuditFiltering("identifiantEntite.specified=true", "identifiantEntite.specified=false");
    }

    @Test
    @Transactional
    void getAllJournalAuditsByIdentifiantEntiteContainsSomething() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where identifiantEntite contains
        defaultJournalAuditFiltering(
            "identifiantEntite.contains=" + DEFAULT_IDENTIFIANT_ENTITE,
            "identifiantEntite.contains=" + UPDATED_IDENTIFIANT_ENTITE
        );
    }

    @Test
    @Transactional
    void getAllJournalAuditsByIdentifiantEntiteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where identifiantEntite does not contain
        defaultJournalAuditFiltering(
            "identifiantEntite.doesNotContain=" + UPDATED_IDENTIFIANT_ENTITE,
            "identifiantEntite.doesNotContain=" + DEFAULT_IDENTIFIANT_ENTITE
        );
    }

    @Test
    @Transactional
    void getAllJournalAuditsByAdresseIpIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where adresseIp equals to
        defaultJournalAuditFiltering("adresseIp.equals=" + DEFAULT_ADRESSE_IP, "adresseIp.equals=" + UPDATED_ADRESSE_IP);
    }

    @Test
    @Transactional
    void getAllJournalAuditsByAdresseIpIsInShouldWork() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where adresseIp in
        defaultJournalAuditFiltering("adresseIp.in=" + DEFAULT_ADRESSE_IP + "," + UPDATED_ADRESSE_IP, "adresseIp.in=" + UPDATED_ADRESSE_IP);
    }

    @Test
    @Transactional
    void getAllJournalAuditsByAdresseIpIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where adresseIp is not null
        defaultJournalAuditFiltering("adresseIp.specified=true", "adresseIp.specified=false");
    }

    @Test
    @Transactional
    void getAllJournalAuditsByAdresseIpContainsSomething() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where adresseIp contains
        defaultJournalAuditFiltering("adresseIp.contains=" + DEFAULT_ADRESSE_IP, "adresseIp.contains=" + UPDATED_ADRESSE_IP);
    }

    @Test
    @Transactional
    void getAllJournalAuditsByAdresseIpNotContainsSomething() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where adresseIp does not contain
        defaultJournalAuditFiltering("adresseIp.doesNotContain=" + UPDATED_ADRESSE_IP, "adresseIp.doesNotContain=" + DEFAULT_ADRESSE_IP);
    }

    @Test
    @Transactional
    void getAllJournalAuditsByDateActionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where dateAction equals to
        defaultJournalAuditFiltering("dateAction.equals=" + DEFAULT_DATE_ACTION, "dateAction.equals=" + UPDATED_DATE_ACTION);
    }

    @Test
    @Transactional
    void getAllJournalAuditsByDateActionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where dateAction in
        defaultJournalAuditFiltering(
            "dateAction.in=" + DEFAULT_DATE_ACTION + "," + UPDATED_DATE_ACTION,
            "dateAction.in=" + UPDATED_DATE_ACTION
        );
    }

    @Test
    @Transactional
    void getAllJournalAuditsByDateActionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        // Get all the journalAuditList where dateAction is not null
        defaultJournalAuditFiltering("dateAction.specified=true", "dateAction.specified=false");
    }

    @Test
    @Transactional
    void getAllJournalAuditsByBoutiqueIsEqualToSomething() throws Exception {
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            journalAuditRepository.saveAndFlush(journalAudit);
            boutique = BoutiqueResourceIT.createEntity();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutique);
        em.flush();
        journalAudit.setBoutique(boutique);
        journalAuditRepository.saveAndFlush(journalAudit);
        Long boutiqueId = boutique.getId();
        // Get all the journalAuditList where boutique equals to boutiqueId
        defaultJournalAuditShouldBeFound("boutiqueId.equals=" + boutiqueId);

        // Get all the journalAuditList where boutique equals to (boutiqueId + 1)
        defaultJournalAuditShouldNotBeFound("boutiqueId.equals=" + (boutiqueId + 1));
    }

    @Test
    @Transactional
    void getAllJournalAuditsByUtilisateurIsEqualToSomething() throws Exception {
        User utilisateur;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            journalAuditRepository.saveAndFlush(journalAudit);
            utilisateur = UserResourceIT.createEntity();
        } else {
            utilisateur = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(utilisateur);
        em.flush();
        journalAudit.setUtilisateur(utilisateur);
        journalAuditRepository.saveAndFlush(journalAudit);
        Long utilisateurId = utilisateur.getId();
        // Get all the journalAuditList where utilisateur equals to utilisateurId
        defaultJournalAuditShouldBeFound("utilisateurId.equals=" + utilisateurId);

        // Get all the journalAuditList where utilisateur equals to (utilisateurId + 1)
        defaultJournalAuditShouldNotBeFound("utilisateurId.equals=" + (utilisateurId + 1));
    }

    private void defaultJournalAuditFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultJournalAuditShouldBeFound(shouldBeFound);
        defaultJournalAuditShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultJournalAuditShouldBeFound(String filter) throws Exception {
        restJournalAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(journalAudit.getId().intValue())))
            .andExpect(jsonPath("$.[*].typeAction").value(hasItem(DEFAULT_TYPE_ACTION.toString())))
            .andExpect(jsonPath("$.[*].entiteConcernee").value(hasItem(DEFAULT_ENTITE_CONCERNEE)))
            .andExpect(jsonPath("$.[*].identifiantEntite").value(hasItem(DEFAULT_IDENTIFIANT_ENTITE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].adresseIp").value(hasItem(DEFAULT_ADRESSE_IP)))
            .andExpect(jsonPath("$.[*].dateAction").value(hasItem(DEFAULT_DATE_ACTION.toString())));

        // Check, that the count call also returns 1
        restJournalAuditMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultJournalAuditShouldNotBeFound(String filter) throws Exception {
        restJournalAuditMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restJournalAuditMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingJournalAudit() throws Exception {
        // Get the journalAudit
        restJournalAuditMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingJournalAudit() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the journalAudit
        JournalAudit updatedJournalAudit = journalAuditRepository.findById(journalAudit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedJournalAudit are not directly saved in db
        em.detach(updatedJournalAudit);
        updatedJournalAudit
            .typeAction(UPDATED_TYPE_ACTION)
            .entiteConcernee(UPDATED_ENTITE_CONCERNEE)
            .identifiantEntite(UPDATED_IDENTIFIANT_ENTITE)
            .description(UPDATED_DESCRIPTION)
            .adresseIp(UPDATED_ADRESSE_IP)
            .dateAction(UPDATED_DATE_ACTION);
        JournalAuditDTO journalAuditDTO = journalAuditMapper.toDto(updatedJournalAudit);

        restJournalAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, journalAuditDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(journalAuditDTO))
            )
            .andExpect(status().isOk());

        // Validate the JournalAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedJournalAuditToMatchAllProperties(updatedJournalAudit);
    }

    @Test
    @Transactional
    void putNonExistingJournalAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalAudit.setId(longCount.incrementAndGet());

        // Create the JournalAudit
        JournalAuditDTO journalAuditDTO = journalAuditMapper.toDto(journalAudit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJournalAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, journalAuditDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(journalAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJournalAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalAudit.setId(longCount.incrementAndGet());

        // Create the JournalAudit
        JournalAuditDTO journalAuditDTO = journalAuditMapper.toDto(journalAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalAuditMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(journalAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJournalAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalAudit.setId(longCount.incrementAndGet());

        // Create the JournalAudit
        JournalAuditDTO journalAuditDTO = journalAuditMapper.toDto(journalAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalAuditMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(journalAuditDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the JournalAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJournalAuditWithPatch() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the journalAudit using partial update
        JournalAudit partialUpdatedJournalAudit = new JournalAudit();
        partialUpdatedJournalAudit.setId(journalAudit.getId());

        partialUpdatedJournalAudit
            .identifiantEntite(UPDATED_IDENTIFIANT_ENTITE)
            .description(UPDATED_DESCRIPTION)
            .adresseIp(UPDATED_ADRESSE_IP)
            .dateAction(UPDATED_DATE_ACTION);

        restJournalAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJournalAudit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJournalAudit))
            )
            .andExpect(status().isOk());

        // Validate the JournalAudit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJournalAuditUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedJournalAudit, journalAudit),
            getPersistedJournalAudit(journalAudit)
        );
    }

    @Test
    @Transactional
    void fullUpdateJournalAuditWithPatch() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the journalAudit using partial update
        JournalAudit partialUpdatedJournalAudit = new JournalAudit();
        partialUpdatedJournalAudit.setId(journalAudit.getId());

        partialUpdatedJournalAudit
            .typeAction(UPDATED_TYPE_ACTION)
            .entiteConcernee(UPDATED_ENTITE_CONCERNEE)
            .identifiantEntite(UPDATED_IDENTIFIANT_ENTITE)
            .description(UPDATED_DESCRIPTION)
            .adresseIp(UPDATED_ADRESSE_IP)
            .dateAction(UPDATED_DATE_ACTION);

        restJournalAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJournalAudit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJournalAudit))
            )
            .andExpect(status().isOk());

        // Validate the JournalAudit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJournalAuditUpdatableFieldsEquals(partialUpdatedJournalAudit, getPersistedJournalAudit(partialUpdatedJournalAudit));
    }

    @Test
    @Transactional
    void patchNonExistingJournalAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalAudit.setId(longCount.incrementAndGet());

        // Create the JournalAudit
        JournalAuditDTO journalAuditDTO = journalAuditMapper.toDto(journalAudit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJournalAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, journalAuditDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(journalAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJournalAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalAudit.setId(longCount.incrementAndGet());

        // Create the JournalAudit
        JournalAuditDTO journalAuditDTO = journalAuditMapper.toDto(journalAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalAuditMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(journalAuditDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the JournalAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJournalAudit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        journalAudit.setId(longCount.incrementAndGet());

        // Create the JournalAudit
        JournalAuditDTO journalAuditDTO = journalAuditMapper.toDto(journalAudit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJournalAuditMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(journalAuditDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the JournalAudit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJournalAudit() throws Exception {
        // Initialize the database
        insertedJournalAudit = journalAuditRepository.saveAndFlush(journalAudit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the journalAudit
        restJournalAuditMockMvc
            .perform(delete(ENTITY_API_URL_ID, journalAudit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return journalAuditRepository.count();
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

    protected JournalAudit getPersistedJournalAudit(JournalAudit journalAudit) {
        return journalAuditRepository.findById(journalAudit.getId()).orElseThrow();
    }

    protected void assertPersistedJournalAuditToMatchAllProperties(JournalAudit expectedJournalAudit) {
        assertJournalAuditAllPropertiesEquals(expectedJournalAudit, getPersistedJournalAudit(expectedJournalAudit));
    }

    protected void assertPersistedJournalAuditToMatchUpdatableProperties(JournalAudit expectedJournalAudit) {
        assertJournalAuditAllUpdatablePropertiesEquals(expectedJournalAudit, getPersistedJournalAudit(expectedJournalAudit));
    }
}
