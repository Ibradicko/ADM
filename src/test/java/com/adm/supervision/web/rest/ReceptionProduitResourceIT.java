package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.ReceptionProduitAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.ReceptionProduit;
import com.adm.supervision.domain.User;
import com.adm.supervision.repository.ReceptionProduitRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.service.ReceptionProduitService;
import com.adm.supervision.service.dto.ReceptionProduitDTO;
import com.adm.supervision.service.mapper.ReceptionProduitMapper;
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
 * Integration tests for the {@link ReceptionProduitResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReceptionProduitResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_RECEPTION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_RECEPTION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_FOURNISSEUR = "AAAAAAAAAA";
    private static final String UPDATED_FOURNISSEUR = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENTAIRE = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/reception-produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReceptionProduitRepository receptionProduitRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private ReceptionProduitRepository receptionProduitRepositoryMock;

    @Autowired
    private ReceptionProduitMapper receptionProduitMapper;

    @Mock
    private ReceptionProduitService receptionProduitServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReceptionProduitMockMvc;

    private ReceptionProduit receptionProduit;

    private ReceptionProduit insertedReceptionProduit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReceptionProduit createEntity(EntityManager em) {
        ReceptionProduit receptionProduit = new ReceptionProduit()
            .reference(DEFAULT_REFERENCE)
            .dateReception(DEFAULT_DATE_RECEPTION)
            .fournisseur(DEFAULT_FOURNISSEUR)
            .commentaire(DEFAULT_COMMENTAIRE);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        receptionProduit.setBoutique(boutique);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        receptionProduit.setUtilisateur(user);
        return receptionProduit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReceptionProduit createUpdatedEntity(EntityManager em) {
        ReceptionProduit updatedReceptionProduit = new ReceptionProduit()
            .reference(UPDATED_REFERENCE)
            .dateReception(UPDATED_DATE_RECEPTION)
            .fournisseur(UPDATED_FOURNISSEUR)
            .commentaire(UPDATED_COMMENTAIRE);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createUpdatedEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        updatedReceptionProduit.setBoutique(boutique);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedReceptionProduit.setUtilisateur(user);
        return updatedReceptionProduit;
    }

    @BeforeEach
    void initTest() {
        receptionProduit = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedReceptionProduit != null) {
            receptionProduitRepository.delete(insertedReceptionProduit);
            insertedReceptionProduit = null;
        }
    }

    @Test
    @Transactional
    void createReceptionProduit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReceptionProduit
        ReceptionProduitDTO receptionProduitDTO = receptionProduitMapper.toDto(receptionProduit);
        var returnedReceptionProduitDTO = om.readValue(
            restReceptionProduitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(receptionProduitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReceptionProduitDTO.class
        );

        // Validate the ReceptionProduit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReceptionProduit = receptionProduitMapper.toEntity(returnedReceptionProduitDTO);
        assertReceptionProduitUpdatableFieldsEquals(returnedReceptionProduit, getPersistedReceptionProduit(returnedReceptionProduit));

        insertedReceptionProduit = returnedReceptionProduit;
    }

    @Test
    @Transactional
    void createReceptionProduitWithExistingId() throws Exception {
        // Create the ReceptionProduit with an existing ID
        receptionProduit.setId(1L);
        ReceptionProduitDTO receptionProduitDTO = receptionProduitMapper.toDto(receptionProduit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReceptionProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(receptionProduitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        receptionProduit.setReference(null);

        // Create the ReceptionProduit, which fails.
        ReceptionProduitDTO receptionProduitDTO = receptionProduitMapper.toDto(receptionProduit);

        restReceptionProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(receptionProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateReceptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        receptionProduit.setDateReception(null);

        // Create the ReceptionProduit, which fails.
        ReceptionProduitDTO receptionProduitDTO = receptionProduitMapper.toDto(receptionProduit);

        restReceptionProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(receptionProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReceptionProduits() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList
        restReceptionProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(receptionProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].dateReception").value(hasItem(DEFAULT_DATE_RECEPTION.toString())))
            .andExpect(jsonPath("$.[*].fournisseur").value(hasItem(DEFAULT_FOURNISSEUR)))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReceptionProduitsWithEagerRelationshipsIsEnabled() throws Exception {
        when(receptionProduitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReceptionProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(receptionProduitServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReceptionProduitsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(receptionProduitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReceptionProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(receptionProduitRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReceptionProduit() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get the receptionProduit
        restReceptionProduitMockMvc
            .perform(get(ENTITY_API_URL_ID, receptionProduit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(receptionProduit.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.dateReception").value(DEFAULT_DATE_RECEPTION.toString()))
            .andExpect(jsonPath("$.fournisseur").value(DEFAULT_FOURNISSEUR))
            .andExpect(jsonPath("$.commentaire").value(DEFAULT_COMMENTAIRE));
    }

    @Test
    @Transactional
    void getReceptionProduitsByIdFiltering() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        Long id = receptionProduit.getId();

        defaultReceptionProduitFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReceptionProduitFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReceptionProduitFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where reference equals to
        defaultReceptionProduitFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where reference in
        defaultReceptionProduitFiltering(
            "reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE,
            "reference.in=" + UPDATED_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where reference is not null
        defaultReceptionProduitFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where reference contains
        defaultReceptionProduitFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where reference does not contain
        defaultReceptionProduitFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByDateReceptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where dateReception equals to
        defaultReceptionProduitFiltering(
            "dateReception.equals=" + DEFAULT_DATE_RECEPTION,
            "dateReception.equals=" + UPDATED_DATE_RECEPTION
        );
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByDateReceptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where dateReception in
        defaultReceptionProduitFiltering(
            "dateReception.in=" + DEFAULT_DATE_RECEPTION + "," + UPDATED_DATE_RECEPTION,
            "dateReception.in=" + UPDATED_DATE_RECEPTION
        );
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByDateReceptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where dateReception is not null
        defaultReceptionProduitFiltering("dateReception.specified=true", "dateReception.specified=false");
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByFournisseurIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where fournisseur equals to
        defaultReceptionProduitFiltering("fournisseur.equals=" + DEFAULT_FOURNISSEUR, "fournisseur.equals=" + UPDATED_FOURNISSEUR);
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByFournisseurIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where fournisseur in
        defaultReceptionProduitFiltering(
            "fournisseur.in=" + DEFAULT_FOURNISSEUR + "," + UPDATED_FOURNISSEUR,
            "fournisseur.in=" + UPDATED_FOURNISSEUR
        );
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByFournisseurIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where fournisseur is not null
        defaultReceptionProduitFiltering("fournisseur.specified=true", "fournisseur.specified=false");
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByFournisseurContainsSomething() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where fournisseur contains
        defaultReceptionProduitFiltering("fournisseur.contains=" + DEFAULT_FOURNISSEUR, "fournisseur.contains=" + UPDATED_FOURNISSEUR);
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByFournisseurNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where fournisseur does not contain
        defaultReceptionProduitFiltering(
            "fournisseur.doesNotContain=" + UPDATED_FOURNISSEUR,
            "fournisseur.doesNotContain=" + DEFAULT_FOURNISSEUR
        );
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByCommentaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where commentaire equals to
        defaultReceptionProduitFiltering("commentaire.equals=" + DEFAULT_COMMENTAIRE, "commentaire.equals=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByCommentaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where commentaire in
        defaultReceptionProduitFiltering(
            "commentaire.in=" + DEFAULT_COMMENTAIRE + "," + UPDATED_COMMENTAIRE,
            "commentaire.in=" + UPDATED_COMMENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByCommentaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where commentaire is not null
        defaultReceptionProduitFiltering("commentaire.specified=true", "commentaire.specified=false");
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByCommentaireContainsSomething() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where commentaire contains
        defaultReceptionProduitFiltering("commentaire.contains=" + DEFAULT_COMMENTAIRE, "commentaire.contains=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByCommentaireNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        // Get all the receptionProduitList where commentaire does not contain
        defaultReceptionProduitFiltering(
            "commentaire.doesNotContain=" + UPDATED_COMMENTAIRE,
            "commentaire.doesNotContain=" + DEFAULT_COMMENTAIRE
        );
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByBoutiqueIsEqualToSomething() throws Exception {
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            receptionProduitRepository.saveAndFlush(receptionProduit);
            boutique = BoutiqueResourceIT.createEntity();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutique);
        em.flush();
        receptionProduit.setBoutique(boutique);
        receptionProduitRepository.saveAndFlush(receptionProduit);
        Long boutiqueId = boutique.getId();
        // Get all the receptionProduitList where boutique equals to boutiqueId
        defaultReceptionProduitShouldBeFound("boutiqueId.equals=" + boutiqueId);

        // Get all the receptionProduitList where boutique equals to (boutiqueId + 1)
        defaultReceptionProduitShouldNotBeFound("boutiqueId.equals=" + (boutiqueId + 1));
    }

    @Test
    @Transactional
    void getAllReceptionProduitsByUtilisateurIsEqualToSomething() throws Exception {
        User utilisateur;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            receptionProduitRepository.saveAndFlush(receptionProduit);
            utilisateur = UserResourceIT.createEntity();
        } else {
            utilisateur = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(utilisateur);
        em.flush();
        receptionProduit.setUtilisateur(utilisateur);
        receptionProduitRepository.saveAndFlush(receptionProduit);
        Long utilisateurId = utilisateur.getId();
        // Get all the receptionProduitList where utilisateur equals to utilisateurId
        defaultReceptionProduitShouldBeFound("utilisateurId.equals=" + utilisateurId);

        // Get all the receptionProduitList where utilisateur equals to (utilisateurId + 1)
        defaultReceptionProduitShouldNotBeFound("utilisateurId.equals=" + (utilisateurId + 1));
    }

    private void defaultReceptionProduitFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReceptionProduitShouldBeFound(shouldBeFound);
        defaultReceptionProduitShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReceptionProduitShouldBeFound(String filter) throws Exception {
        restReceptionProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(receptionProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].dateReception").value(hasItem(DEFAULT_DATE_RECEPTION.toString())))
            .andExpect(jsonPath("$.[*].fournisseur").value(hasItem(DEFAULT_FOURNISSEUR)))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));

        // Check, that the count call also returns 1
        restReceptionProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReceptionProduitShouldNotBeFound(String filter) throws Exception {
        restReceptionProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReceptionProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReceptionProduit() throws Exception {
        // Get the receptionProduit
        restReceptionProduitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReceptionProduit() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the receptionProduit
        ReceptionProduit updatedReceptionProduit = receptionProduitRepository.findById(receptionProduit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReceptionProduit are not directly saved in db
        em.detach(updatedReceptionProduit);
        updatedReceptionProduit
            .reference(UPDATED_REFERENCE)
            .dateReception(UPDATED_DATE_RECEPTION)
            .fournisseur(UPDATED_FOURNISSEUR)
            .commentaire(UPDATED_COMMENTAIRE);
        ReceptionProduitDTO receptionProduitDTO = receptionProduitMapper.toDto(updatedReceptionProduit);

        restReceptionProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, receptionProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(receptionProduitDTO))
            )
            .andExpect(status().isOk());

        // Validate the ReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReceptionProduitToMatchAllProperties(updatedReceptionProduit);
    }

    @Test
    @Transactional
    void putNonExistingReceptionProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        receptionProduit.setId(longCount.incrementAndGet());

        // Create the ReceptionProduit
        ReceptionProduitDTO receptionProduitDTO = receptionProduitMapper.toDto(receptionProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReceptionProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, receptionProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(receptionProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReceptionProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        receptionProduit.setId(longCount.incrementAndGet());

        // Create the ReceptionProduit
        ReceptionProduitDTO receptionProduitDTO = receptionProduitMapper.toDto(receptionProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReceptionProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(receptionProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReceptionProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        receptionProduit.setId(longCount.incrementAndGet());

        // Create the ReceptionProduit
        ReceptionProduitDTO receptionProduitDTO = receptionProduitMapper.toDto(receptionProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReceptionProduitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(receptionProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReceptionProduitWithPatch() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the receptionProduit using partial update
        ReceptionProduit partialUpdatedReceptionProduit = new ReceptionProduit();
        partialUpdatedReceptionProduit.setId(receptionProduit.getId());

        restReceptionProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReceptionProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReceptionProduit))
            )
            .andExpect(status().isOk());

        // Validate the ReceptionProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReceptionProduitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReceptionProduit, receptionProduit),
            getPersistedReceptionProduit(receptionProduit)
        );
    }

    @Test
    @Transactional
    void fullUpdateReceptionProduitWithPatch() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the receptionProduit using partial update
        ReceptionProduit partialUpdatedReceptionProduit = new ReceptionProduit();
        partialUpdatedReceptionProduit.setId(receptionProduit.getId());

        partialUpdatedReceptionProduit
            .reference(UPDATED_REFERENCE)
            .dateReception(UPDATED_DATE_RECEPTION)
            .fournisseur(UPDATED_FOURNISSEUR)
            .commentaire(UPDATED_COMMENTAIRE);

        restReceptionProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReceptionProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReceptionProduit))
            )
            .andExpect(status().isOk());

        // Validate the ReceptionProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReceptionProduitUpdatableFieldsEquals(
            partialUpdatedReceptionProduit,
            getPersistedReceptionProduit(partialUpdatedReceptionProduit)
        );
    }

    @Test
    @Transactional
    void patchNonExistingReceptionProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        receptionProduit.setId(longCount.incrementAndGet());

        // Create the ReceptionProduit
        ReceptionProduitDTO receptionProduitDTO = receptionProduitMapper.toDto(receptionProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReceptionProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, receptionProduitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(receptionProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReceptionProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        receptionProduit.setId(longCount.incrementAndGet());

        // Create the ReceptionProduit
        ReceptionProduitDTO receptionProduitDTO = receptionProduitMapper.toDto(receptionProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReceptionProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(receptionProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReceptionProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        receptionProduit.setId(longCount.incrementAndGet());

        // Create the ReceptionProduit
        ReceptionProduitDTO receptionProduitDTO = receptionProduitMapper.toDto(receptionProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReceptionProduitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(receptionProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReceptionProduit() throws Exception {
        // Initialize the database
        insertedReceptionProduit = receptionProduitRepository.saveAndFlush(receptionProduit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the receptionProduit
        restReceptionProduitMockMvc
            .perform(delete(ENTITY_API_URL_ID, receptionProduit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return receptionProduitRepository.count();
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

    protected ReceptionProduit getPersistedReceptionProduit(ReceptionProduit receptionProduit) {
        return receptionProduitRepository.findById(receptionProduit.getId()).orElseThrow();
    }

    protected void assertPersistedReceptionProduitToMatchAllProperties(ReceptionProduit expectedReceptionProduit) {
        assertReceptionProduitAllPropertiesEquals(expectedReceptionProduit, getPersistedReceptionProduit(expectedReceptionProduit));
    }

    protected void assertPersistedReceptionProduitToMatchUpdatableProperties(ReceptionProduit expectedReceptionProduit) {
        assertReceptionProduitAllUpdatablePropertiesEquals(
            expectedReceptionProduit,
            getPersistedReceptionProduit(expectedReceptionProduit)
        );
    }
}
