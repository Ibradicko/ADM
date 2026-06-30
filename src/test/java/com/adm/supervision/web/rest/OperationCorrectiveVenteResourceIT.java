package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.OperationCorrectiveVenteAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.OperationCorrectiveVente;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.TypeOperationCorrective;
import com.adm.supervision.repository.OperationCorrectiveVenteRepository;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.service.OperationCorrectiveVenteService;
import com.adm.supervision.service.dto.OperationCorrectiveVenteDTO;
import com.adm.supervision.service.mapper.OperationCorrectiveVenteMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link OperationCorrectiveVenteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class OperationCorrectiveVenteResourceIT {

    private static final TypeOperationCorrective DEFAULT_TYPE_OPERATION = TypeOperationCorrective.ANNULATION;
    private static final TypeOperationCorrective UPDATED_TYPE_OPERATION = TypeOperationCorrective.RETOUR;

    private static final String DEFAULT_MOTIF = "AAAAAAAAAA";
    private static final String UPDATED_MOTIF = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_MONTANT_IMPACT = new BigDecimal(0);
    private static final BigDecimal UPDATED_MONTANT_IMPACT = new BigDecimal(1);
    private static final BigDecimal SMALLER_MONTANT_IMPACT = new BigDecimal(0 - 1);

    private static final Instant DEFAULT_DATE_OPERATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_OPERATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/operation-corrective-ventes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OperationCorrectiveVenteRepository operationCorrectiveVenteRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private OperationCorrectiveVenteRepository operationCorrectiveVenteRepositoryMock;

    @Autowired
    private OperationCorrectiveVenteMapper operationCorrectiveVenteMapper;

    @Mock
    private OperationCorrectiveVenteService operationCorrectiveVenteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOperationCorrectiveVenteMockMvc;

    private OperationCorrectiveVente operationCorrectiveVente;

    private OperationCorrectiveVente insertedOperationCorrectiveVente;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OperationCorrectiveVente createEntity(EntityManager em) {
        OperationCorrectiveVente operationCorrectiveVente = new OperationCorrectiveVente()
            .typeOperation(DEFAULT_TYPE_OPERATION)
            .motif(DEFAULT_MOTIF)
            .montantImpact(DEFAULT_MONTANT_IMPACT)
            .dateOperation(DEFAULT_DATE_OPERATION);
        // Add required entity
        Vente vente;
        if (TestUtil.findAll(em, Vente.class).isEmpty()) {
            vente = VenteResourceIT.createEntity(em);
            em.persist(vente);
            em.flush();
        } else {
            vente = TestUtil.findAll(em, Vente.class).get(0);
        }
        operationCorrectiveVente.setVente(vente);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        operationCorrectiveVente.setUtilisateur(user);
        return operationCorrectiveVente;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OperationCorrectiveVente createUpdatedEntity(EntityManager em) {
        OperationCorrectiveVente updatedOperationCorrectiveVente = new OperationCorrectiveVente()
            .typeOperation(UPDATED_TYPE_OPERATION)
            .motif(UPDATED_MOTIF)
            .montantImpact(UPDATED_MONTANT_IMPACT)
            .dateOperation(UPDATED_DATE_OPERATION);
        // Add required entity
        Vente vente;
        if (TestUtil.findAll(em, Vente.class).isEmpty()) {
            vente = VenteResourceIT.createUpdatedEntity(em);
            em.persist(vente);
            em.flush();
        } else {
            vente = TestUtil.findAll(em, Vente.class).get(0);
        }
        updatedOperationCorrectiveVente.setVente(vente);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedOperationCorrectiveVente.setUtilisateur(user);
        return updatedOperationCorrectiveVente;
    }

    @BeforeEach
    void initTest() {
        operationCorrectiveVente = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedOperationCorrectiveVente != null) {
            operationCorrectiveVenteRepository.delete(insertedOperationCorrectiveVente);
            insertedOperationCorrectiveVente = null;
        }
    }

    @Test
    @Transactional
    void createOperationCorrectiveVente() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the OperationCorrectiveVente
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO = operationCorrectiveVenteMapper.toDto(operationCorrectiveVente);
        var returnedOperationCorrectiveVenteDTO = om.readValue(
            restOperationCorrectiveVenteMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(operationCorrectiveVenteDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            OperationCorrectiveVenteDTO.class
        );

        // Validate the OperationCorrectiveVente in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedOperationCorrectiveVente = operationCorrectiveVenteMapper.toEntity(returnedOperationCorrectiveVenteDTO);
        assertOperationCorrectiveVenteUpdatableFieldsEquals(
            returnedOperationCorrectiveVente,
            getPersistedOperationCorrectiveVente(returnedOperationCorrectiveVente)
        );

        insertedOperationCorrectiveVente = returnedOperationCorrectiveVente;
    }

    @Test
    @Transactional
    void createOperationCorrectiveVenteWithExistingId() throws Exception {
        // Create the OperationCorrectiveVente with an existing ID
        operationCorrectiveVente.setId(1L);
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO = operationCorrectiveVenteMapper.toDto(operationCorrectiveVente);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOperationCorrectiveVenteMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(operationCorrectiveVenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OperationCorrectiveVente in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeOperationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        operationCorrectiveVente.setTypeOperation(null);

        // Create the OperationCorrectiveVente, which fails.
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO = operationCorrectiveVenteMapper.toDto(operationCorrectiveVente);

        restOperationCorrectiveVenteMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(operationCorrectiveVenteDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMotifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        operationCorrectiveVente.setMotif(null);

        // Create the OperationCorrectiveVente, which fails.
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO = operationCorrectiveVenteMapper.toDto(operationCorrectiveVente);

        restOperationCorrectiveVenteMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(operationCorrectiveVenteDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateOperationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        operationCorrectiveVente.setDateOperation(null);

        // Create the OperationCorrectiveVente, which fails.
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO = operationCorrectiveVenteMapper.toDto(operationCorrectiveVente);

        restOperationCorrectiveVenteMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(operationCorrectiveVenteDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentes() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList
        restOperationCorrectiveVenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operationCorrectiveVente.getId().intValue())))
            .andExpect(jsonPath("$.[*].typeOperation").value(hasItem(DEFAULT_TYPE_OPERATION.toString())))
            .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)))
            .andExpect(jsonPath("$.[*].montantImpact").value(hasItem(sameNumber(DEFAULT_MONTANT_IMPACT))))
            .andExpect(jsonPath("$.[*].dateOperation").value(hasItem(DEFAULT_DATE_OPERATION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOperationCorrectiveVentesWithEagerRelationshipsIsEnabled() throws Exception {
        when(operationCorrectiveVenteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOperationCorrectiveVenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(operationCorrectiveVenteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllOperationCorrectiveVentesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(operationCorrectiveVenteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restOperationCorrectiveVenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(operationCorrectiveVenteRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getOperationCorrectiveVente() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get the operationCorrectiveVente
        restOperationCorrectiveVenteMockMvc
            .perform(get(ENTITY_API_URL_ID, operationCorrectiveVente.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(operationCorrectiveVente.getId().intValue()))
            .andExpect(jsonPath("$.typeOperation").value(DEFAULT_TYPE_OPERATION.toString()))
            .andExpect(jsonPath("$.motif").value(DEFAULT_MOTIF))
            .andExpect(jsonPath("$.montantImpact").value(sameNumber(DEFAULT_MONTANT_IMPACT)))
            .andExpect(jsonPath("$.dateOperation").value(DEFAULT_DATE_OPERATION.toString()));
    }

    @Test
    @Transactional
    void getOperationCorrectiveVentesByIdFiltering() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        Long id = operationCorrectiveVente.getId();

        defaultOperationCorrectiveVenteFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultOperationCorrectiveVenteFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultOperationCorrectiveVenteFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByTypeOperationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where typeOperation equals to
        defaultOperationCorrectiveVenteFiltering(
            "typeOperation.equals=" + DEFAULT_TYPE_OPERATION,
            "typeOperation.equals=" + UPDATED_TYPE_OPERATION
        );
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByTypeOperationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where typeOperation in
        defaultOperationCorrectiveVenteFiltering(
            "typeOperation.in=" + DEFAULT_TYPE_OPERATION + "," + UPDATED_TYPE_OPERATION,
            "typeOperation.in=" + UPDATED_TYPE_OPERATION
        );
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByTypeOperationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where typeOperation is not null
        defaultOperationCorrectiveVenteFiltering("typeOperation.specified=true", "typeOperation.specified=false");
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByMotifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where motif equals to
        defaultOperationCorrectiveVenteFiltering("motif.equals=" + DEFAULT_MOTIF, "motif.equals=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByMotifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where motif in
        defaultOperationCorrectiveVenteFiltering("motif.in=" + DEFAULT_MOTIF + "," + UPDATED_MOTIF, "motif.in=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByMotifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where motif is not null
        defaultOperationCorrectiveVenteFiltering("motif.specified=true", "motif.specified=false");
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByMotifContainsSomething() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where motif contains
        defaultOperationCorrectiveVenteFiltering("motif.contains=" + DEFAULT_MOTIF, "motif.contains=" + UPDATED_MOTIF);
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByMotifNotContainsSomething() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where motif does not contain
        defaultOperationCorrectiveVenteFiltering("motif.doesNotContain=" + UPDATED_MOTIF, "motif.doesNotContain=" + DEFAULT_MOTIF);
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByMontantImpactIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where montantImpact equals to
        defaultOperationCorrectiveVenteFiltering(
            "montantImpact.equals=" + DEFAULT_MONTANT_IMPACT,
            "montantImpact.equals=" + UPDATED_MONTANT_IMPACT
        );
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByMontantImpactIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where montantImpact in
        defaultOperationCorrectiveVenteFiltering(
            "montantImpact.in=" + DEFAULT_MONTANT_IMPACT + "," + UPDATED_MONTANT_IMPACT,
            "montantImpact.in=" + UPDATED_MONTANT_IMPACT
        );
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByMontantImpactIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where montantImpact is not null
        defaultOperationCorrectiveVenteFiltering("montantImpact.specified=true", "montantImpact.specified=false");
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByMontantImpactIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where montantImpact is greater than or equal to
        defaultOperationCorrectiveVenteFiltering(
            "montantImpact.greaterThanOrEqual=" + DEFAULT_MONTANT_IMPACT,
            "montantImpact.greaterThanOrEqual=" + UPDATED_MONTANT_IMPACT
        );
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByMontantImpactIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where montantImpact is less than or equal to
        defaultOperationCorrectiveVenteFiltering(
            "montantImpact.lessThanOrEqual=" + DEFAULT_MONTANT_IMPACT,
            "montantImpact.lessThanOrEqual=" + SMALLER_MONTANT_IMPACT
        );
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByMontantImpactIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where montantImpact is less than
        defaultOperationCorrectiveVenteFiltering(
            "montantImpact.lessThan=" + UPDATED_MONTANT_IMPACT,
            "montantImpact.lessThan=" + DEFAULT_MONTANT_IMPACT
        );
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByMontantImpactIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where montantImpact is greater than
        defaultOperationCorrectiveVenteFiltering(
            "montantImpact.greaterThan=" + SMALLER_MONTANT_IMPACT,
            "montantImpact.greaterThan=" + DEFAULT_MONTANT_IMPACT
        );
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByDateOperationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where dateOperation equals to
        defaultOperationCorrectiveVenteFiltering(
            "dateOperation.equals=" + DEFAULT_DATE_OPERATION,
            "dateOperation.equals=" + UPDATED_DATE_OPERATION
        );
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByDateOperationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where dateOperation in
        defaultOperationCorrectiveVenteFiltering(
            "dateOperation.in=" + DEFAULT_DATE_OPERATION + "," + UPDATED_DATE_OPERATION,
            "dateOperation.in=" + UPDATED_DATE_OPERATION
        );
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByDateOperationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        // Get all the operationCorrectiveVenteList where dateOperation is not null
        defaultOperationCorrectiveVenteFiltering("dateOperation.specified=true", "dateOperation.specified=false");
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByVenteIsEqualToSomething() throws Exception {
        Vente vente;
        if (TestUtil.findAll(em, Vente.class).isEmpty()) {
            operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);
            vente = VenteResourceIT.createEntity(em);
        } else {
            vente = TestUtil.findAll(em, Vente.class).get(0);
        }
        em.persist(vente);
        em.flush();
        operationCorrectiveVente.setVente(vente);
        operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);
        Long venteId = vente.getId();
        // Get all the operationCorrectiveVenteList where vente equals to venteId
        defaultOperationCorrectiveVenteShouldBeFound("venteId.equals=" + venteId);

        // Get all the operationCorrectiveVenteList where vente equals to (venteId + 1)
        defaultOperationCorrectiveVenteShouldNotBeFound("venteId.equals=" + (venteId + 1));
    }

    @Test
    @Transactional
    void getAllOperationCorrectiveVentesByUtilisateurIsEqualToSomething() throws Exception {
        User utilisateur;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);
            utilisateur = UserResourceIT.createEntity();
        } else {
            utilisateur = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(utilisateur);
        em.flush();
        operationCorrectiveVente.setUtilisateur(utilisateur);
        operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);
        Long utilisateurId = utilisateur.getId();
        // Get all the operationCorrectiveVenteList where utilisateur equals to utilisateurId
        defaultOperationCorrectiveVenteShouldBeFound("utilisateurId.equals=" + utilisateurId);

        // Get all the operationCorrectiveVenteList where utilisateur equals to (utilisateurId + 1)
        defaultOperationCorrectiveVenteShouldNotBeFound("utilisateurId.equals=" + (utilisateurId + 1));
    }

    private void defaultOperationCorrectiveVenteFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultOperationCorrectiveVenteShouldBeFound(shouldBeFound);
        defaultOperationCorrectiveVenteShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOperationCorrectiveVenteShouldBeFound(String filter) throws Exception {
        restOperationCorrectiveVenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(operationCorrectiveVente.getId().intValue())))
            .andExpect(jsonPath("$.[*].typeOperation").value(hasItem(DEFAULT_TYPE_OPERATION.toString())))
            .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)))
            .andExpect(jsonPath("$.[*].montantImpact").value(hasItem(sameNumber(DEFAULT_MONTANT_IMPACT))))
            .andExpect(jsonPath("$.[*].dateOperation").value(hasItem(DEFAULT_DATE_OPERATION.toString())));

        // Check, that the count call also returns 1
        restOperationCorrectiveVenteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOperationCorrectiveVenteShouldNotBeFound(String filter) throws Exception {
        restOperationCorrectiveVenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOperationCorrectiveVenteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOperationCorrectiveVente() throws Exception {
        // Get the operationCorrectiveVente
        restOperationCorrectiveVenteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOperationCorrectiveVente() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the operationCorrectiveVente
        OperationCorrectiveVente updatedOperationCorrectiveVente = operationCorrectiveVenteRepository
            .findById(operationCorrectiveVente.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedOperationCorrectiveVente are not directly saved in db
        em.detach(updatedOperationCorrectiveVente);
        updatedOperationCorrectiveVente
            .typeOperation(UPDATED_TYPE_OPERATION)
            .motif(UPDATED_MOTIF)
            .montantImpact(UPDATED_MONTANT_IMPACT)
            .dateOperation(UPDATED_DATE_OPERATION);
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO = operationCorrectiveVenteMapper.toDto(updatedOperationCorrectiveVente);

        restOperationCorrectiveVenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, operationCorrectiveVenteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(operationCorrectiveVenteDTO))
            )
            .andExpect(status().isOk());

        // Validate the OperationCorrectiveVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedOperationCorrectiveVenteToMatchAllProperties(updatedOperationCorrectiveVente);
    }

    @Test
    @Transactional
    void putNonExistingOperationCorrectiveVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        operationCorrectiveVente.setId(longCount.incrementAndGet());

        // Create the OperationCorrectiveVente
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO = operationCorrectiveVenteMapper.toDto(operationCorrectiveVente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOperationCorrectiveVenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, operationCorrectiveVenteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(operationCorrectiveVenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OperationCorrectiveVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOperationCorrectiveVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        operationCorrectiveVente.setId(longCount.incrementAndGet());

        // Create the OperationCorrectiveVente
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO = operationCorrectiveVenteMapper.toDto(operationCorrectiveVente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperationCorrectiveVenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(operationCorrectiveVenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OperationCorrectiveVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOperationCorrectiveVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        operationCorrectiveVente.setId(longCount.incrementAndGet());

        // Create the OperationCorrectiveVente
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO = operationCorrectiveVenteMapper.toDto(operationCorrectiveVente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperationCorrectiveVenteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(operationCorrectiveVenteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OperationCorrectiveVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOperationCorrectiveVenteWithPatch() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the operationCorrectiveVente using partial update
        OperationCorrectiveVente partialUpdatedOperationCorrectiveVente = new OperationCorrectiveVente();
        partialUpdatedOperationCorrectiveVente.setId(operationCorrectiveVente.getId());

        partialUpdatedOperationCorrectiveVente.typeOperation(UPDATED_TYPE_OPERATION);

        restOperationCorrectiveVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOperationCorrectiveVente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOperationCorrectiveVente))
            )
            .andExpect(status().isOk());

        // Validate the OperationCorrectiveVente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOperationCorrectiveVenteUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedOperationCorrectiveVente, operationCorrectiveVente),
            getPersistedOperationCorrectiveVente(operationCorrectiveVente)
        );
    }

    @Test
    @Transactional
    void fullUpdateOperationCorrectiveVenteWithPatch() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the operationCorrectiveVente using partial update
        OperationCorrectiveVente partialUpdatedOperationCorrectiveVente = new OperationCorrectiveVente();
        partialUpdatedOperationCorrectiveVente.setId(operationCorrectiveVente.getId());

        partialUpdatedOperationCorrectiveVente
            .typeOperation(UPDATED_TYPE_OPERATION)
            .motif(UPDATED_MOTIF)
            .montantImpact(UPDATED_MONTANT_IMPACT)
            .dateOperation(UPDATED_DATE_OPERATION);

        restOperationCorrectiveVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOperationCorrectiveVente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedOperationCorrectiveVente))
            )
            .andExpect(status().isOk());

        // Validate the OperationCorrectiveVente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertOperationCorrectiveVenteUpdatableFieldsEquals(
            partialUpdatedOperationCorrectiveVente,
            getPersistedOperationCorrectiveVente(partialUpdatedOperationCorrectiveVente)
        );
    }

    @Test
    @Transactional
    void patchNonExistingOperationCorrectiveVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        operationCorrectiveVente.setId(longCount.incrementAndGet());

        // Create the OperationCorrectiveVente
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO = operationCorrectiveVenteMapper.toDto(operationCorrectiveVente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOperationCorrectiveVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, operationCorrectiveVenteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(operationCorrectiveVenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OperationCorrectiveVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOperationCorrectiveVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        operationCorrectiveVente.setId(longCount.incrementAndGet());

        // Create the OperationCorrectiveVente
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO = operationCorrectiveVenteMapper.toDto(operationCorrectiveVente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperationCorrectiveVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(operationCorrectiveVenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OperationCorrectiveVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOperationCorrectiveVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        operationCorrectiveVente.setId(longCount.incrementAndGet());

        // Create the OperationCorrectiveVente
        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO = operationCorrectiveVenteMapper.toDto(operationCorrectiveVente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOperationCorrectiveVenteMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(operationCorrectiveVenteDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OperationCorrectiveVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOperationCorrectiveVente() throws Exception {
        // Initialize the database
        insertedOperationCorrectiveVente = operationCorrectiveVenteRepository.saveAndFlush(operationCorrectiveVente);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the operationCorrectiveVente
        restOperationCorrectiveVenteMockMvc
            .perform(delete(ENTITY_API_URL_ID, operationCorrectiveVente.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return operationCorrectiveVenteRepository.count();
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

    protected OperationCorrectiveVente getPersistedOperationCorrectiveVente(OperationCorrectiveVente operationCorrectiveVente) {
        return operationCorrectiveVenteRepository.findById(operationCorrectiveVente.getId()).orElseThrow();
    }

    protected void assertPersistedOperationCorrectiveVenteToMatchAllProperties(OperationCorrectiveVente expectedOperationCorrectiveVente) {
        assertOperationCorrectiveVenteAllPropertiesEquals(
            expectedOperationCorrectiveVente,
            getPersistedOperationCorrectiveVente(expectedOperationCorrectiveVente)
        );
    }

    protected void assertPersistedOperationCorrectiveVenteToMatchUpdatableProperties(
        OperationCorrectiveVente expectedOperationCorrectiveVente
    ) {
        assertOperationCorrectiveVenteAllUpdatablePropertiesEquals(
            expectedOperationCorrectiveVente,
            getPersistedOperationCorrectiveVente(expectedOperationCorrectiveVente)
        );
    }
}
