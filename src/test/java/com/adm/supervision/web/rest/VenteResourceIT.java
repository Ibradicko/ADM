package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.VenteAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.Locataire;
import com.adm.supervision.domain.User;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.StatutVente;
import com.adm.supervision.repository.UserRepository;
import com.adm.supervision.repository.VenteRepository;
import com.adm.supervision.service.VenteService;
import com.adm.supervision.service.dto.VenteDTO;
import com.adm.supervision.service.mapper.VenteMapper;
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
 * Integration tests for the {@link VenteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VenteResourceIT {

    private static final String DEFAULT_NUMERO_TICKET = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO_TICKET = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_HEURE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_HEURE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final StatutVente DEFAULT_STATUT = StatutVente.BROUILLON;
    private static final StatutVente UPDATED_STATUT = StatutVente.VALIDEE;

    private static final String DEFAULT_REFERENCE_PASSAGER = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE_PASSAGER = "BBBBBBBBBB";

    private static final String DEFAULT_REFERENCE_CARTE_EMBARQUEMENT = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE_CARTE_EMBARQUEMENT = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_MONTANT_BRUT = new BigDecimal(0);
    private static final BigDecimal UPDATED_MONTANT_BRUT = new BigDecimal(1);
    private static final BigDecimal SMALLER_MONTANT_BRUT = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_MONTANT_REMISE = new BigDecimal(0);
    private static final BigDecimal UPDATED_MONTANT_REMISE = new BigDecimal(1);
    private static final BigDecimal SMALLER_MONTANT_REMISE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_MONTANT_NET = new BigDecimal(0);
    private static final BigDecimal UPDATED_MONTANT_NET = new BigDecimal(1);
    private static final BigDecimal SMALLER_MONTANT_NET = new BigDecimal(0 - 1);

    private static final String DEFAULT_COMMENTAIRE = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ventes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VenteRepository venteRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private VenteRepository venteRepositoryMock;

    @Autowired
    private VenteMapper venteMapper;

    @Mock
    private VenteService venteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVenteMockMvc;

    private Vente vente;

    private Vente insertedVente;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vente createEntity(EntityManager em) {
        Vente vente = new Vente()
            .numeroTicket(DEFAULT_NUMERO_TICKET)
            .dateHeure(DEFAULT_DATE_HEURE)
            .statut(DEFAULT_STATUT)
            .referencePassager(DEFAULT_REFERENCE_PASSAGER)
            .referenceCarteEmbarquement(DEFAULT_REFERENCE_CARTE_EMBARQUEMENT)
            .montantBrut(DEFAULT_MONTANT_BRUT)
            .montantRemise(DEFAULT_MONTANT_REMISE)
            .montantNet(DEFAULT_MONTANT_NET)
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
        vente.setBoutique(boutique);
        // Add required entity
        Locataire locataire;
        if (TestUtil.findAll(em, Locataire.class).isEmpty()) {
            locataire = LocataireResourceIT.createEntity();
            em.persist(locataire);
            em.flush();
        } else {
            locataire = TestUtil.findAll(em, Locataire.class).get(0);
        }
        vente.setLocataire(locataire);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        vente.setVendeur(user);
        return vente;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vente createUpdatedEntity(EntityManager em) {
        Vente updatedVente = new Vente()
            .numeroTicket(UPDATED_NUMERO_TICKET)
            .dateHeure(UPDATED_DATE_HEURE)
            .statut(UPDATED_STATUT)
            .referencePassager(UPDATED_REFERENCE_PASSAGER)
            .referenceCarteEmbarquement(UPDATED_REFERENCE_CARTE_EMBARQUEMENT)
            .montantBrut(UPDATED_MONTANT_BRUT)
            .montantRemise(UPDATED_MONTANT_REMISE)
            .montantNet(UPDATED_MONTANT_NET)
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
        updatedVente.setBoutique(boutique);
        // Add required entity
        Locataire locataire;
        if (TestUtil.findAll(em, Locataire.class).isEmpty()) {
            locataire = LocataireResourceIT.createUpdatedEntity();
            em.persist(locataire);
            em.flush();
        } else {
            locataire = TestUtil.findAll(em, Locataire.class).get(0);
        }
        updatedVente.setLocataire(locataire);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedVente.setVendeur(user);
        return updatedVente;
    }

    @BeforeEach
    void initTest() {
        vente = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedVente != null) {
            venteRepository.delete(insertedVente);
            insertedVente = null;
        }
    }

    @Test
    @Transactional
    void createVente() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Vente
        VenteDTO venteDTO = venteMapper.toDto(vente);
        var returnedVenteDTO = om.readValue(
            restVenteMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(venteDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VenteDTO.class
        );

        // Validate the Vente in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVente = venteMapper.toEntity(returnedVenteDTO);
        assertVenteUpdatableFieldsEquals(returnedVente, getPersistedVente(returnedVente));

        insertedVente = returnedVente;
    }

    @Test
    @Transactional
    void createVenteWithExistingId() throws Exception {
        // Create the Vente with an existing ID
        vente.setId(1L);
        VenteDTO venteDTO = venteMapper.toDto(vente);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(venteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Vente in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNumeroTicketIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        vente.setNumeroTicket(null);

        // Create the Vente, which fails.
        VenteDTO venteDTO = venteMapper.toDto(vente);

        restVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(venteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateHeureIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        vente.setDateHeure(null);

        // Create the Vente, which fails.
        VenteDTO venteDTO = venteMapper.toDto(vente);

        restVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(venteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        vente.setStatut(null);

        // Create the Vente, which fails.
        VenteDTO venteDTO = venteMapper.toDto(vente);

        restVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(venteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMontantBrutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        vente.setMontantBrut(null);

        // Create the Vente, which fails.
        VenteDTO venteDTO = venteMapper.toDto(vente);

        restVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(venteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMontantNetIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        vente.setMontantNet(null);

        // Create the Vente, which fails.
        VenteDTO venteDTO = venteMapper.toDto(vente);

        restVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(venteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVentes() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList
        restVenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vente.getId().intValue())))
            .andExpect(jsonPath("$.[*].numeroTicket").value(hasItem(DEFAULT_NUMERO_TICKET)))
            .andExpect(jsonPath("$.[*].dateHeure").value(hasItem(DEFAULT_DATE_HEURE.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].referencePassager").value(hasItem(DEFAULT_REFERENCE_PASSAGER)))
            .andExpect(jsonPath("$.[*].referenceCarteEmbarquement").value(hasItem(DEFAULT_REFERENCE_CARTE_EMBARQUEMENT)))
            .andExpect(jsonPath("$.[*].montantBrut").value(hasItem(sameNumber(DEFAULT_MONTANT_BRUT))))
            .andExpect(jsonPath("$.[*].montantRemise").value(hasItem(sameNumber(DEFAULT_MONTANT_REMISE))))
            .andExpect(jsonPath("$.[*].montantNet").value(hasItem(sameNumber(DEFAULT_MONTANT_NET))))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVentesWithEagerRelationshipsIsEnabled() throws Exception {
        when(venteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(venteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVentesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(venteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(venteRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getVente() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get the vente
        restVenteMockMvc
            .perform(get(ENTITY_API_URL_ID, vente.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(vente.getId().intValue()))
            .andExpect(jsonPath("$.numeroTicket").value(DEFAULT_NUMERO_TICKET))
            .andExpect(jsonPath("$.dateHeure").value(DEFAULT_DATE_HEURE.toString()))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.referencePassager").value(DEFAULT_REFERENCE_PASSAGER))
            .andExpect(jsonPath("$.referenceCarteEmbarquement").value(DEFAULT_REFERENCE_CARTE_EMBARQUEMENT))
            .andExpect(jsonPath("$.montantBrut").value(sameNumber(DEFAULT_MONTANT_BRUT)))
            .andExpect(jsonPath("$.montantRemise").value(sameNumber(DEFAULT_MONTANT_REMISE)))
            .andExpect(jsonPath("$.montantNet").value(sameNumber(DEFAULT_MONTANT_NET)))
            .andExpect(jsonPath("$.commentaire").value(DEFAULT_COMMENTAIRE));
    }

    @Test
    @Transactional
    void getVentesByIdFiltering() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        Long id = vente.getId();

        defaultVenteFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultVenteFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultVenteFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllVentesByNumeroTicketIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where numeroTicket equals to
        defaultVenteFiltering("numeroTicket.equals=" + DEFAULT_NUMERO_TICKET, "numeroTicket.equals=" + UPDATED_NUMERO_TICKET);
    }

    @Test
    @Transactional
    void getAllVentesByNumeroTicketIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where numeroTicket in
        defaultVenteFiltering(
            "numeroTicket.in=" + DEFAULT_NUMERO_TICKET + "," + UPDATED_NUMERO_TICKET,
            "numeroTicket.in=" + UPDATED_NUMERO_TICKET
        );
    }

    @Test
    @Transactional
    void getAllVentesByNumeroTicketIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where numeroTicket is not null
        defaultVenteFiltering("numeroTicket.specified=true", "numeroTicket.specified=false");
    }

    @Test
    @Transactional
    void getAllVentesByNumeroTicketContainsSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where numeroTicket contains
        defaultVenteFiltering("numeroTicket.contains=" + DEFAULT_NUMERO_TICKET, "numeroTicket.contains=" + UPDATED_NUMERO_TICKET);
    }

    @Test
    @Transactional
    void getAllVentesByNumeroTicketNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where numeroTicket does not contain
        defaultVenteFiltering(
            "numeroTicket.doesNotContain=" + UPDATED_NUMERO_TICKET,
            "numeroTicket.doesNotContain=" + DEFAULT_NUMERO_TICKET
        );
    }

    @Test
    @Transactional
    void getAllVentesByDateHeureIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where dateHeure equals to
        defaultVenteFiltering("dateHeure.equals=" + DEFAULT_DATE_HEURE, "dateHeure.equals=" + UPDATED_DATE_HEURE);
    }

    @Test
    @Transactional
    void getAllVentesByDateHeureIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where dateHeure in
        defaultVenteFiltering("dateHeure.in=" + DEFAULT_DATE_HEURE + "," + UPDATED_DATE_HEURE, "dateHeure.in=" + UPDATED_DATE_HEURE);
    }

    @Test
    @Transactional
    void getAllVentesByDateHeureIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where dateHeure is not null
        defaultVenteFiltering("dateHeure.specified=true", "dateHeure.specified=false");
    }

    @Test
    @Transactional
    void getAllVentesByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where statut equals to
        defaultVenteFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllVentesByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where statut in
        defaultVenteFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllVentesByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where statut is not null
        defaultVenteFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllVentesByReferencePassagerIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where referencePassager equals to
        defaultVenteFiltering(
            "referencePassager.equals=" + DEFAULT_REFERENCE_PASSAGER,
            "referencePassager.equals=" + UPDATED_REFERENCE_PASSAGER
        );
    }

    @Test
    @Transactional
    void getAllVentesByReferencePassagerIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where referencePassager in
        defaultVenteFiltering(
            "referencePassager.in=" + DEFAULT_REFERENCE_PASSAGER + "," + UPDATED_REFERENCE_PASSAGER,
            "referencePassager.in=" + UPDATED_REFERENCE_PASSAGER
        );
    }

    @Test
    @Transactional
    void getAllVentesByReferencePassagerIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where referencePassager is not null
        defaultVenteFiltering("referencePassager.specified=true", "referencePassager.specified=false");
    }

    @Test
    @Transactional
    void getAllVentesByReferencePassagerContainsSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where referencePassager contains
        defaultVenteFiltering(
            "referencePassager.contains=" + DEFAULT_REFERENCE_PASSAGER,
            "referencePassager.contains=" + UPDATED_REFERENCE_PASSAGER
        );
    }

    @Test
    @Transactional
    void getAllVentesByReferencePassagerNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where referencePassager does not contain
        defaultVenteFiltering(
            "referencePassager.doesNotContain=" + UPDATED_REFERENCE_PASSAGER,
            "referencePassager.doesNotContain=" + DEFAULT_REFERENCE_PASSAGER
        );
    }

    @Test
    @Transactional
    void getAllVentesByReferenceCarteEmbarquementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where referenceCarteEmbarquement equals to
        defaultVenteFiltering(
            "referenceCarteEmbarquement.equals=" + DEFAULT_REFERENCE_CARTE_EMBARQUEMENT,
            "referenceCarteEmbarquement.equals=" + UPDATED_REFERENCE_CARTE_EMBARQUEMENT
        );
    }

    @Test
    @Transactional
    void getAllVentesByReferenceCarteEmbarquementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where referenceCarteEmbarquement in
        defaultVenteFiltering(
            "referenceCarteEmbarquement.in=" + DEFAULT_REFERENCE_CARTE_EMBARQUEMENT + "," + UPDATED_REFERENCE_CARTE_EMBARQUEMENT,
            "referenceCarteEmbarquement.in=" + UPDATED_REFERENCE_CARTE_EMBARQUEMENT
        );
    }

    @Test
    @Transactional
    void getAllVentesByReferenceCarteEmbarquementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where referenceCarteEmbarquement is not null
        defaultVenteFiltering("referenceCarteEmbarquement.specified=true", "referenceCarteEmbarquement.specified=false");
    }

    @Test
    @Transactional
    void getAllVentesByReferenceCarteEmbarquementContainsSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where referenceCarteEmbarquement contains
        defaultVenteFiltering(
            "referenceCarteEmbarquement.contains=" + DEFAULT_REFERENCE_CARTE_EMBARQUEMENT,
            "referenceCarteEmbarquement.contains=" + UPDATED_REFERENCE_CARTE_EMBARQUEMENT
        );
    }

    @Test
    @Transactional
    void getAllVentesByReferenceCarteEmbarquementNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where referenceCarteEmbarquement does not contain
        defaultVenteFiltering(
            "referenceCarteEmbarquement.doesNotContain=" + UPDATED_REFERENCE_CARTE_EMBARQUEMENT,
            "referenceCarteEmbarquement.doesNotContain=" + DEFAULT_REFERENCE_CARTE_EMBARQUEMENT
        );
    }

    @Test
    @Transactional
    void getAllVentesByMontantBrutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantBrut equals to
        defaultVenteFiltering("montantBrut.equals=" + DEFAULT_MONTANT_BRUT, "montantBrut.equals=" + UPDATED_MONTANT_BRUT);
    }

    @Test
    @Transactional
    void getAllVentesByMontantBrutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantBrut in
        defaultVenteFiltering(
            "montantBrut.in=" + DEFAULT_MONTANT_BRUT + "," + UPDATED_MONTANT_BRUT,
            "montantBrut.in=" + UPDATED_MONTANT_BRUT
        );
    }

    @Test
    @Transactional
    void getAllVentesByMontantBrutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantBrut is not null
        defaultVenteFiltering("montantBrut.specified=true", "montantBrut.specified=false");
    }

    @Test
    @Transactional
    void getAllVentesByMontantBrutIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantBrut is greater than or equal to
        defaultVenteFiltering(
            "montantBrut.greaterThanOrEqual=" + DEFAULT_MONTANT_BRUT,
            "montantBrut.greaterThanOrEqual=" + UPDATED_MONTANT_BRUT
        );
    }

    @Test
    @Transactional
    void getAllVentesByMontantBrutIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantBrut is less than or equal to
        defaultVenteFiltering("montantBrut.lessThanOrEqual=" + DEFAULT_MONTANT_BRUT, "montantBrut.lessThanOrEqual=" + SMALLER_MONTANT_BRUT);
    }

    @Test
    @Transactional
    void getAllVentesByMontantBrutIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantBrut is less than
        defaultVenteFiltering("montantBrut.lessThan=" + UPDATED_MONTANT_BRUT, "montantBrut.lessThan=" + DEFAULT_MONTANT_BRUT);
    }

    @Test
    @Transactional
    void getAllVentesByMontantBrutIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantBrut is greater than
        defaultVenteFiltering("montantBrut.greaterThan=" + SMALLER_MONTANT_BRUT, "montantBrut.greaterThan=" + DEFAULT_MONTANT_BRUT);
    }

    @Test
    @Transactional
    void getAllVentesByMontantRemiseIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantRemise equals to
        defaultVenteFiltering("montantRemise.equals=" + DEFAULT_MONTANT_REMISE, "montantRemise.equals=" + UPDATED_MONTANT_REMISE);
    }

    @Test
    @Transactional
    void getAllVentesByMontantRemiseIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantRemise in
        defaultVenteFiltering(
            "montantRemise.in=" + DEFAULT_MONTANT_REMISE + "," + UPDATED_MONTANT_REMISE,
            "montantRemise.in=" + UPDATED_MONTANT_REMISE
        );
    }

    @Test
    @Transactional
    void getAllVentesByMontantRemiseIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantRemise is not null
        defaultVenteFiltering("montantRemise.specified=true", "montantRemise.specified=false");
    }

    @Test
    @Transactional
    void getAllVentesByMontantRemiseIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantRemise is greater than or equal to
        defaultVenteFiltering(
            "montantRemise.greaterThanOrEqual=" + DEFAULT_MONTANT_REMISE,
            "montantRemise.greaterThanOrEqual=" + UPDATED_MONTANT_REMISE
        );
    }

    @Test
    @Transactional
    void getAllVentesByMontantRemiseIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantRemise is less than or equal to
        defaultVenteFiltering(
            "montantRemise.lessThanOrEqual=" + DEFAULT_MONTANT_REMISE,
            "montantRemise.lessThanOrEqual=" + SMALLER_MONTANT_REMISE
        );
    }

    @Test
    @Transactional
    void getAllVentesByMontantRemiseIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantRemise is less than
        defaultVenteFiltering("montantRemise.lessThan=" + UPDATED_MONTANT_REMISE, "montantRemise.lessThan=" + DEFAULT_MONTANT_REMISE);
    }

    @Test
    @Transactional
    void getAllVentesByMontantRemiseIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantRemise is greater than
        defaultVenteFiltering("montantRemise.greaterThan=" + SMALLER_MONTANT_REMISE, "montantRemise.greaterThan=" + DEFAULT_MONTANT_REMISE);
    }

    @Test
    @Transactional
    void getAllVentesByMontantNetIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantNet equals to
        defaultVenteFiltering("montantNet.equals=" + DEFAULT_MONTANT_NET, "montantNet.equals=" + UPDATED_MONTANT_NET);
    }

    @Test
    @Transactional
    void getAllVentesByMontantNetIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantNet in
        defaultVenteFiltering("montantNet.in=" + DEFAULT_MONTANT_NET + "," + UPDATED_MONTANT_NET, "montantNet.in=" + UPDATED_MONTANT_NET);
    }

    @Test
    @Transactional
    void getAllVentesByMontantNetIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantNet is not null
        defaultVenteFiltering("montantNet.specified=true", "montantNet.specified=false");
    }

    @Test
    @Transactional
    void getAllVentesByMontantNetIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantNet is greater than or equal to
        defaultVenteFiltering(
            "montantNet.greaterThanOrEqual=" + DEFAULT_MONTANT_NET,
            "montantNet.greaterThanOrEqual=" + UPDATED_MONTANT_NET
        );
    }

    @Test
    @Transactional
    void getAllVentesByMontantNetIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantNet is less than or equal to
        defaultVenteFiltering("montantNet.lessThanOrEqual=" + DEFAULT_MONTANT_NET, "montantNet.lessThanOrEqual=" + SMALLER_MONTANT_NET);
    }

    @Test
    @Transactional
    void getAllVentesByMontantNetIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantNet is less than
        defaultVenteFiltering("montantNet.lessThan=" + UPDATED_MONTANT_NET, "montantNet.lessThan=" + DEFAULT_MONTANT_NET);
    }

    @Test
    @Transactional
    void getAllVentesByMontantNetIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where montantNet is greater than
        defaultVenteFiltering("montantNet.greaterThan=" + SMALLER_MONTANT_NET, "montantNet.greaterThan=" + DEFAULT_MONTANT_NET);
    }

    @Test
    @Transactional
    void getAllVentesByCommentaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where commentaire equals to
        defaultVenteFiltering("commentaire.equals=" + DEFAULT_COMMENTAIRE, "commentaire.equals=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllVentesByCommentaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where commentaire in
        defaultVenteFiltering("commentaire.in=" + DEFAULT_COMMENTAIRE + "," + UPDATED_COMMENTAIRE, "commentaire.in=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllVentesByCommentaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where commentaire is not null
        defaultVenteFiltering("commentaire.specified=true", "commentaire.specified=false");
    }

    @Test
    @Transactional
    void getAllVentesByCommentaireContainsSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where commentaire contains
        defaultVenteFiltering("commentaire.contains=" + DEFAULT_COMMENTAIRE, "commentaire.contains=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllVentesByCommentaireNotContainsSomething() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        // Get all the venteList where commentaire does not contain
        defaultVenteFiltering("commentaire.doesNotContain=" + UPDATED_COMMENTAIRE, "commentaire.doesNotContain=" + DEFAULT_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllVentesByBoutiqueIsEqualToSomething() throws Exception {
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            venteRepository.saveAndFlush(vente);
            boutique = BoutiqueResourceIT.createEntity();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutique);
        em.flush();
        vente.setBoutique(boutique);
        venteRepository.saveAndFlush(vente);
        Long boutiqueId = boutique.getId();
        // Get all the venteList where boutique equals to boutiqueId
        defaultVenteShouldBeFound("boutiqueId.equals=" + boutiqueId);

        // Get all the venteList where boutique equals to (boutiqueId + 1)
        defaultVenteShouldNotBeFound("boutiqueId.equals=" + (boutiqueId + 1));
    }

    @Test
    @Transactional
    void getAllVentesByLocataireIsEqualToSomething() throws Exception {
        Locataire locataire;
        if (TestUtil.findAll(em, Locataire.class).isEmpty()) {
            venteRepository.saveAndFlush(vente);
            locataire = LocataireResourceIT.createEntity();
        } else {
            locataire = TestUtil.findAll(em, Locataire.class).get(0);
        }
        em.persist(locataire);
        em.flush();
        vente.setLocataire(locataire);
        venteRepository.saveAndFlush(vente);
        Long locataireId = locataire.getId();
        // Get all the venteList where locataire equals to locataireId
        defaultVenteShouldBeFound("locataireId.equals=" + locataireId);

        // Get all the venteList where locataire equals to (locataireId + 1)
        defaultVenteShouldNotBeFound("locataireId.equals=" + (locataireId + 1));
    }

    @Test
    @Transactional
    void getAllVentesByVendeurIsEqualToSomething() throws Exception {
        User vendeur;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            venteRepository.saveAndFlush(vente);
            vendeur = UserResourceIT.createEntity();
        } else {
            vendeur = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(vendeur);
        em.flush();
        vente.setVendeur(vendeur);
        venteRepository.saveAndFlush(vente);
        Long vendeurId = vendeur.getId();
        // Get all the venteList where vendeur equals to vendeurId
        defaultVenteShouldBeFound("vendeurId.equals=" + vendeurId);

        // Get all the venteList where vendeur equals to (vendeurId + 1)
        defaultVenteShouldNotBeFound("vendeurId.equals=" + (vendeurId + 1));
    }

    private void defaultVenteFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultVenteShouldBeFound(shouldBeFound);
        defaultVenteShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultVenteShouldBeFound(String filter) throws Exception {
        restVenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vente.getId().intValue())))
            .andExpect(jsonPath("$.[*].numeroTicket").value(hasItem(DEFAULT_NUMERO_TICKET)))
            .andExpect(jsonPath("$.[*].dateHeure").value(hasItem(DEFAULT_DATE_HEURE.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].referencePassager").value(hasItem(DEFAULT_REFERENCE_PASSAGER)))
            .andExpect(jsonPath("$.[*].referenceCarteEmbarquement").value(hasItem(DEFAULT_REFERENCE_CARTE_EMBARQUEMENT)))
            .andExpect(jsonPath("$.[*].montantBrut").value(hasItem(sameNumber(DEFAULT_MONTANT_BRUT))))
            .andExpect(jsonPath("$.[*].montantRemise").value(hasItem(sameNumber(DEFAULT_MONTANT_REMISE))))
            .andExpect(jsonPath("$.[*].montantNet").value(hasItem(sameNumber(DEFAULT_MONTANT_NET))))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));

        // Check, that the count call also returns 1
        restVenteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultVenteShouldNotBeFound(String filter) throws Exception {
        restVenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restVenteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingVente() throws Exception {
        // Get the vente
        restVenteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVente() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vente
        Vente updatedVente = venteRepository.findById(vente.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVente are not directly saved in db
        em.detach(updatedVente);
        updatedVente
            .numeroTicket(UPDATED_NUMERO_TICKET)
            .dateHeure(UPDATED_DATE_HEURE)
            .statut(UPDATED_STATUT)
            .referencePassager(UPDATED_REFERENCE_PASSAGER)
            .referenceCarteEmbarquement(UPDATED_REFERENCE_CARTE_EMBARQUEMENT)
            .montantBrut(UPDATED_MONTANT_BRUT)
            .montantRemise(UPDATED_MONTANT_REMISE)
            .montantNet(UPDATED_MONTANT_NET)
            .commentaire(UPDATED_COMMENTAIRE);
        VenteDTO venteDTO = venteMapper.toDto(updatedVente);

        restVenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, venteDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(venteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Vente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVenteToMatchAllProperties(updatedVente);
    }

    @Test
    @Transactional
    void putNonExistingVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vente.setId(longCount.incrementAndGet());

        // Create the Vente
        VenteDTO venteDTO = venteMapper.toDto(vente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, venteDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(venteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vente.setId(longCount.incrementAndGet());

        // Create the Vente
        VenteDTO venteDTO = venteMapper.toDto(vente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(venteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vente.setId(longCount.incrementAndGet());

        // Create the Vente
        VenteDTO venteDTO = venteMapper.toDto(vente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVenteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(venteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Vente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVenteWithPatch() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vente using partial update
        Vente partialUpdatedVente = new Vente();
        partialUpdatedVente.setId(vente.getId());

        partialUpdatedVente.statut(UPDATED_STATUT).montantBrut(UPDATED_MONTANT_BRUT).montantRemise(UPDATED_MONTANT_REMISE);

        restVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVente))
            )
            .andExpect(status().isOk());

        // Validate the Vente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVenteUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVente, vente), getPersistedVente(vente));
    }

    @Test
    @Transactional
    void fullUpdateVenteWithPatch() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the vente using partial update
        Vente partialUpdatedVente = new Vente();
        partialUpdatedVente.setId(vente.getId());

        partialUpdatedVente
            .numeroTicket(UPDATED_NUMERO_TICKET)
            .dateHeure(UPDATED_DATE_HEURE)
            .statut(UPDATED_STATUT)
            .referencePassager(UPDATED_REFERENCE_PASSAGER)
            .referenceCarteEmbarquement(UPDATED_REFERENCE_CARTE_EMBARQUEMENT)
            .montantBrut(UPDATED_MONTANT_BRUT)
            .montantRemise(UPDATED_MONTANT_REMISE)
            .montantNet(UPDATED_MONTANT_NET)
            .commentaire(UPDATED_COMMENTAIRE);

        restVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVente))
            )
            .andExpect(status().isOk());

        // Validate the Vente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVenteUpdatableFieldsEquals(partialUpdatedVente, getPersistedVente(partialUpdatedVente));
    }

    @Test
    @Transactional
    void patchNonExistingVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vente.setId(longCount.incrementAndGet());

        // Create the Vente
        VenteDTO venteDTO = venteMapper.toDto(vente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, venteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(venteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vente.setId(longCount.incrementAndGet());

        // Create the Vente
        VenteDTO venteDTO = venteMapper.toDto(vente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(venteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Vente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        vente.setId(longCount.incrementAndGet());

        // Create the Vente
        VenteDTO venteDTO = venteMapper.toDto(vente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVenteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(venteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Vente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVente() throws Exception {
        // Initialize the database
        insertedVente = venteRepository.saveAndFlush(vente);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the vente
        restVenteMockMvc
            .perform(delete(ENTITY_API_URL_ID, vente.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return venteRepository.count();
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

    protected Vente getPersistedVente(Vente vente) {
        return venteRepository.findById(vente.getId()).orElseThrow();
    }

    protected void assertPersistedVenteToMatchAllProperties(Vente expectedVente) {
        assertVenteAllPropertiesEquals(expectedVente, getPersistedVente(expectedVente));
    }

    protected void assertPersistedVenteToMatchUpdatableProperties(Vente expectedVente) {
        assertVenteAllUpdatablePropertiesEquals(expectedVente, getPersistedVente(expectedVente));
    }
}
