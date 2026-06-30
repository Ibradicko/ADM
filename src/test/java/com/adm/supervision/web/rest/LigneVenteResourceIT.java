package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.LigneVenteAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.LigneVente;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.repository.LigneVenteRepository;
import com.adm.supervision.service.LigneVenteService;
import com.adm.supervision.service.dto.LigneVenteDTO;
import com.adm.supervision.service.mapper.LigneVenteMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link LigneVenteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LigneVenteResourceIT {

    private static final BigDecimal DEFAULT_QUANTITE = new BigDecimal(0);
    private static final BigDecimal UPDATED_QUANTITE = new BigDecimal(1);
    private static final BigDecimal SMALLER_QUANTITE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_PRIX_UNITAIRE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRIX_UNITAIRE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRIX_UNITAIRE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_REMISE = new BigDecimal(0);
    private static final BigDecimal UPDATED_REMISE = new BigDecimal(1);
    private static final BigDecimal SMALLER_REMISE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_MONTANT_LIGNE = new BigDecimal(0);
    private static final BigDecimal UPDATED_MONTANT_LIGNE = new BigDecimal(1);
    private static final BigDecimal SMALLER_MONTANT_LIGNE = new BigDecimal(0 - 1);

    private static final String DEFAULT_CODE_BARRES_SCANNE = "AAAAAAAAAA";
    private static final String UPDATED_CODE_BARRES_SCANNE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ligne-ventes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LigneVenteRepository ligneVenteRepository;

    @Mock
    private LigneVenteRepository ligneVenteRepositoryMock;

    @Autowired
    private LigneVenteMapper ligneVenteMapper;

    @Mock
    private LigneVenteService ligneVenteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLigneVenteMockMvc;

    private LigneVente ligneVente;

    private LigneVente insertedLigneVente;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneVente createEntity(EntityManager em) {
        LigneVente ligneVente = new LigneVente()
            .quantite(DEFAULT_QUANTITE)
            .prixUnitaire(DEFAULT_PRIX_UNITAIRE)
            .remise(DEFAULT_REMISE)
            .montantLigne(DEFAULT_MONTANT_LIGNE)
            .codeBarresScanne(DEFAULT_CODE_BARRES_SCANNE);
        // Add required entity
        Vente vente;
        if (TestUtil.findAll(em, Vente.class).isEmpty()) {
            vente = VenteResourceIT.createEntity(em);
            em.persist(vente);
            em.flush();
        } else {
            vente = TestUtil.findAll(em, Vente.class).get(0);
        }
        ligneVente.setVente(vente);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        ligneVente.setProduit(produit);
        return ligneVente;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneVente createUpdatedEntity(EntityManager em) {
        LigneVente updatedLigneVente = new LigneVente()
            .quantite(UPDATED_QUANTITE)
            .prixUnitaire(UPDATED_PRIX_UNITAIRE)
            .remise(UPDATED_REMISE)
            .montantLigne(UPDATED_MONTANT_LIGNE)
            .codeBarresScanne(UPDATED_CODE_BARRES_SCANNE);
        // Add required entity
        Vente vente;
        if (TestUtil.findAll(em, Vente.class).isEmpty()) {
            vente = VenteResourceIT.createUpdatedEntity(em);
            em.persist(vente);
            em.flush();
        } else {
            vente = TestUtil.findAll(em, Vente.class).get(0);
        }
        updatedLigneVente.setVente(vente);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createUpdatedEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        updatedLigneVente.setProduit(produit);
        return updatedLigneVente;
    }

    @BeforeEach
    void initTest() {
        ligneVente = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLigneVente != null) {
            ligneVenteRepository.delete(insertedLigneVente);
            insertedLigneVente = null;
        }
    }

    @Test
    @Transactional
    void createLigneVente() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LigneVente
        LigneVenteDTO ligneVenteDTO = ligneVenteMapper.toDto(ligneVente);
        var returnedLigneVenteDTO = om.readValue(
            restLigneVenteMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneVenteDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LigneVenteDTO.class
        );

        // Validate the LigneVente in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLigneVente = ligneVenteMapper.toEntity(returnedLigneVenteDTO);
        assertLigneVenteUpdatableFieldsEquals(returnedLigneVente, getPersistedLigneVente(returnedLigneVente));

        insertedLigneVente = returnedLigneVente;
    }

    @Test
    @Transactional
    void createLigneVenteWithExistingId() throws Exception {
        // Create the LigneVente with an existing ID
        ligneVente.setId(1L);
        LigneVenteDTO ligneVenteDTO = ligneVenteMapper.toDto(ligneVente);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLigneVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneVenteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LigneVente in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantiteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ligneVente.setQuantite(null);

        // Create the LigneVente, which fails.
        LigneVenteDTO ligneVenteDTO = ligneVenteMapper.toDto(ligneVente);

        restLigneVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneVenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrixUnitaireIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ligneVente.setPrixUnitaire(null);

        // Create the LigneVente, which fails.
        LigneVenteDTO ligneVenteDTO = ligneVenteMapper.toDto(ligneVente);

        restLigneVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneVenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMontantLigneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ligneVente.setMontantLigne(null);

        // Create the LigneVente, which fails.
        LigneVenteDTO ligneVenteDTO = ligneVenteMapper.toDto(ligneVente);

        restLigneVenteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneVenteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLigneVentes() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList
        restLigneVenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligneVente.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantite").value(hasItem(sameNumber(DEFAULT_QUANTITE))))
            .andExpect(jsonPath("$.[*].prixUnitaire").value(hasItem(sameNumber(DEFAULT_PRIX_UNITAIRE))))
            .andExpect(jsonPath("$.[*].remise").value(hasItem(sameNumber(DEFAULT_REMISE))))
            .andExpect(jsonPath("$.[*].montantLigne").value(hasItem(sameNumber(DEFAULT_MONTANT_LIGNE))))
            .andExpect(jsonPath("$.[*].codeBarresScanne").value(hasItem(DEFAULT_CODE_BARRES_SCANNE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLigneVentesWithEagerRelationshipsIsEnabled() throws Exception {
        when(ligneVenteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLigneVenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ligneVenteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLigneVentesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ligneVenteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLigneVenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ligneVenteRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLigneVente() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get the ligneVente
        restLigneVenteMockMvc
            .perform(get(ENTITY_API_URL_ID, ligneVente.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ligneVente.getId().intValue()))
            .andExpect(jsonPath("$.quantite").value(sameNumber(DEFAULT_QUANTITE)))
            .andExpect(jsonPath("$.prixUnitaire").value(sameNumber(DEFAULT_PRIX_UNITAIRE)))
            .andExpect(jsonPath("$.remise").value(sameNumber(DEFAULT_REMISE)))
            .andExpect(jsonPath("$.montantLigne").value(sameNumber(DEFAULT_MONTANT_LIGNE)))
            .andExpect(jsonPath("$.codeBarresScanne").value(DEFAULT_CODE_BARRES_SCANNE));
    }

    @Test
    @Transactional
    void getLigneVentesByIdFiltering() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        Long id = ligneVente.getId();

        defaultLigneVenteFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLigneVenteFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLigneVenteFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLigneVentesByQuantiteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where quantite equals to
        defaultLigneVenteFiltering("quantite.equals=" + DEFAULT_QUANTITE, "quantite.equals=" + UPDATED_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByQuantiteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where quantite in
        defaultLigneVenteFiltering("quantite.in=" + DEFAULT_QUANTITE + "," + UPDATED_QUANTITE, "quantite.in=" + UPDATED_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByQuantiteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where quantite is not null
        defaultLigneVenteFiltering("quantite.specified=true", "quantite.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneVentesByQuantiteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where quantite is greater than or equal to
        defaultLigneVenteFiltering("quantite.greaterThanOrEqual=" + DEFAULT_QUANTITE, "quantite.greaterThanOrEqual=" + UPDATED_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByQuantiteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where quantite is less than or equal to
        defaultLigneVenteFiltering("quantite.lessThanOrEqual=" + DEFAULT_QUANTITE, "quantite.lessThanOrEqual=" + SMALLER_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByQuantiteIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where quantite is less than
        defaultLigneVenteFiltering("quantite.lessThan=" + UPDATED_QUANTITE, "quantite.lessThan=" + DEFAULT_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByQuantiteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where quantite is greater than
        defaultLigneVenteFiltering("quantite.greaterThan=" + SMALLER_QUANTITE, "quantite.greaterThan=" + DEFAULT_QUANTITE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByPrixUnitaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where prixUnitaire equals to
        defaultLigneVenteFiltering("prixUnitaire.equals=" + DEFAULT_PRIX_UNITAIRE, "prixUnitaire.equals=" + UPDATED_PRIX_UNITAIRE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByPrixUnitaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where prixUnitaire in
        defaultLigneVenteFiltering(
            "prixUnitaire.in=" + DEFAULT_PRIX_UNITAIRE + "," + UPDATED_PRIX_UNITAIRE,
            "prixUnitaire.in=" + UPDATED_PRIX_UNITAIRE
        );
    }

    @Test
    @Transactional
    void getAllLigneVentesByPrixUnitaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where prixUnitaire is not null
        defaultLigneVenteFiltering("prixUnitaire.specified=true", "prixUnitaire.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneVentesByPrixUnitaireIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where prixUnitaire is greater than or equal to
        defaultLigneVenteFiltering(
            "prixUnitaire.greaterThanOrEqual=" + DEFAULT_PRIX_UNITAIRE,
            "prixUnitaire.greaterThanOrEqual=" + UPDATED_PRIX_UNITAIRE
        );
    }

    @Test
    @Transactional
    void getAllLigneVentesByPrixUnitaireIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where prixUnitaire is less than or equal to
        defaultLigneVenteFiltering(
            "prixUnitaire.lessThanOrEqual=" + DEFAULT_PRIX_UNITAIRE,
            "prixUnitaire.lessThanOrEqual=" + SMALLER_PRIX_UNITAIRE
        );
    }

    @Test
    @Transactional
    void getAllLigneVentesByPrixUnitaireIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where prixUnitaire is less than
        defaultLigneVenteFiltering("prixUnitaire.lessThan=" + UPDATED_PRIX_UNITAIRE, "prixUnitaire.lessThan=" + DEFAULT_PRIX_UNITAIRE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByPrixUnitaireIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where prixUnitaire is greater than
        defaultLigneVenteFiltering(
            "prixUnitaire.greaterThan=" + SMALLER_PRIX_UNITAIRE,
            "prixUnitaire.greaterThan=" + DEFAULT_PRIX_UNITAIRE
        );
    }

    @Test
    @Transactional
    void getAllLigneVentesByRemiseIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where remise equals to
        defaultLigneVenteFiltering("remise.equals=" + DEFAULT_REMISE, "remise.equals=" + UPDATED_REMISE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByRemiseIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where remise in
        defaultLigneVenteFiltering("remise.in=" + DEFAULT_REMISE + "," + UPDATED_REMISE, "remise.in=" + UPDATED_REMISE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByRemiseIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where remise is not null
        defaultLigneVenteFiltering("remise.specified=true", "remise.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneVentesByRemiseIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where remise is greater than or equal to
        defaultLigneVenteFiltering("remise.greaterThanOrEqual=" + DEFAULT_REMISE, "remise.greaterThanOrEqual=" + UPDATED_REMISE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByRemiseIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where remise is less than or equal to
        defaultLigneVenteFiltering("remise.lessThanOrEqual=" + DEFAULT_REMISE, "remise.lessThanOrEqual=" + SMALLER_REMISE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByRemiseIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where remise is less than
        defaultLigneVenteFiltering("remise.lessThan=" + UPDATED_REMISE, "remise.lessThan=" + DEFAULT_REMISE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByRemiseIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where remise is greater than
        defaultLigneVenteFiltering("remise.greaterThan=" + SMALLER_REMISE, "remise.greaterThan=" + DEFAULT_REMISE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByMontantLigneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where montantLigne equals to
        defaultLigneVenteFiltering("montantLigne.equals=" + DEFAULT_MONTANT_LIGNE, "montantLigne.equals=" + UPDATED_MONTANT_LIGNE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByMontantLigneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where montantLigne in
        defaultLigneVenteFiltering(
            "montantLigne.in=" + DEFAULT_MONTANT_LIGNE + "," + UPDATED_MONTANT_LIGNE,
            "montantLigne.in=" + UPDATED_MONTANT_LIGNE
        );
    }

    @Test
    @Transactional
    void getAllLigneVentesByMontantLigneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where montantLigne is not null
        defaultLigneVenteFiltering("montantLigne.specified=true", "montantLigne.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneVentesByMontantLigneIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where montantLigne is greater than or equal to
        defaultLigneVenteFiltering(
            "montantLigne.greaterThanOrEqual=" + DEFAULT_MONTANT_LIGNE,
            "montantLigne.greaterThanOrEqual=" + UPDATED_MONTANT_LIGNE
        );
    }

    @Test
    @Transactional
    void getAllLigneVentesByMontantLigneIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where montantLigne is less than or equal to
        defaultLigneVenteFiltering(
            "montantLigne.lessThanOrEqual=" + DEFAULT_MONTANT_LIGNE,
            "montantLigne.lessThanOrEqual=" + SMALLER_MONTANT_LIGNE
        );
    }

    @Test
    @Transactional
    void getAllLigneVentesByMontantLigneIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where montantLigne is less than
        defaultLigneVenteFiltering("montantLigne.lessThan=" + UPDATED_MONTANT_LIGNE, "montantLigne.lessThan=" + DEFAULT_MONTANT_LIGNE);
    }

    @Test
    @Transactional
    void getAllLigneVentesByMontantLigneIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where montantLigne is greater than
        defaultLigneVenteFiltering(
            "montantLigne.greaterThan=" + SMALLER_MONTANT_LIGNE,
            "montantLigne.greaterThan=" + DEFAULT_MONTANT_LIGNE
        );
    }

    @Test
    @Transactional
    void getAllLigneVentesByCodeBarresScanneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where codeBarresScanne equals to
        defaultLigneVenteFiltering(
            "codeBarresScanne.equals=" + DEFAULT_CODE_BARRES_SCANNE,
            "codeBarresScanne.equals=" + UPDATED_CODE_BARRES_SCANNE
        );
    }

    @Test
    @Transactional
    void getAllLigneVentesByCodeBarresScanneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where codeBarresScanne in
        defaultLigneVenteFiltering(
            "codeBarresScanne.in=" + DEFAULT_CODE_BARRES_SCANNE + "," + UPDATED_CODE_BARRES_SCANNE,
            "codeBarresScanne.in=" + UPDATED_CODE_BARRES_SCANNE
        );
    }

    @Test
    @Transactional
    void getAllLigneVentesByCodeBarresScanneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where codeBarresScanne is not null
        defaultLigneVenteFiltering("codeBarresScanne.specified=true", "codeBarresScanne.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneVentesByCodeBarresScanneContainsSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where codeBarresScanne contains
        defaultLigneVenteFiltering(
            "codeBarresScanne.contains=" + DEFAULT_CODE_BARRES_SCANNE,
            "codeBarresScanne.contains=" + UPDATED_CODE_BARRES_SCANNE
        );
    }

    @Test
    @Transactional
    void getAllLigneVentesByCodeBarresScanneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        // Get all the ligneVenteList where codeBarresScanne does not contain
        defaultLigneVenteFiltering(
            "codeBarresScanne.doesNotContain=" + UPDATED_CODE_BARRES_SCANNE,
            "codeBarresScanne.doesNotContain=" + DEFAULT_CODE_BARRES_SCANNE
        );
    }

    @Test
    @Transactional
    void getAllLigneVentesByVenteIsEqualToSomething() throws Exception {
        Vente vente;
        if (TestUtil.findAll(em, Vente.class).isEmpty()) {
            ligneVenteRepository.saveAndFlush(ligneVente);
            vente = VenteResourceIT.createEntity(em);
        } else {
            vente = TestUtil.findAll(em, Vente.class).get(0);
        }
        em.persist(vente);
        em.flush();
        ligneVente.setVente(vente);
        ligneVenteRepository.saveAndFlush(ligneVente);
        Long venteId = vente.getId();
        // Get all the ligneVenteList where vente equals to venteId
        defaultLigneVenteShouldBeFound("venteId.equals=" + venteId);

        // Get all the ligneVenteList where vente equals to (venteId + 1)
        defaultLigneVenteShouldNotBeFound("venteId.equals=" + (venteId + 1));
    }

    @Test
    @Transactional
    void getAllLigneVentesByProduitIsEqualToSomething() throws Exception {
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            ligneVenteRepository.saveAndFlush(ligneVente);
            produit = ProduitResourceIT.createEntity(em);
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        em.persist(produit);
        em.flush();
        ligneVente.setProduit(produit);
        ligneVenteRepository.saveAndFlush(ligneVente);
        Long produitId = produit.getId();
        // Get all the ligneVenteList where produit equals to produitId
        defaultLigneVenteShouldBeFound("produitId.equals=" + produitId);

        // Get all the ligneVenteList where produit equals to (produitId + 1)
        defaultLigneVenteShouldNotBeFound("produitId.equals=" + (produitId + 1));
    }

    private void defaultLigneVenteFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLigneVenteShouldBeFound(shouldBeFound);
        defaultLigneVenteShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLigneVenteShouldBeFound(String filter) throws Exception {
        restLigneVenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligneVente.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantite").value(hasItem(sameNumber(DEFAULT_QUANTITE))))
            .andExpect(jsonPath("$.[*].prixUnitaire").value(hasItem(sameNumber(DEFAULT_PRIX_UNITAIRE))))
            .andExpect(jsonPath("$.[*].remise").value(hasItem(sameNumber(DEFAULT_REMISE))))
            .andExpect(jsonPath("$.[*].montantLigne").value(hasItem(sameNumber(DEFAULT_MONTANT_LIGNE))))
            .andExpect(jsonPath("$.[*].codeBarresScanne").value(hasItem(DEFAULT_CODE_BARRES_SCANNE)));

        // Check, that the count call also returns 1
        restLigneVenteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLigneVenteShouldNotBeFound(String filter) throws Exception {
        restLigneVenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLigneVenteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLigneVente() throws Exception {
        // Get the ligneVente
        restLigneVenteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLigneVente() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneVente
        LigneVente updatedLigneVente = ligneVenteRepository.findById(ligneVente.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLigneVente are not directly saved in db
        em.detach(updatedLigneVente);
        updatedLigneVente
            .quantite(UPDATED_QUANTITE)
            .prixUnitaire(UPDATED_PRIX_UNITAIRE)
            .remise(UPDATED_REMISE)
            .montantLigne(UPDATED_MONTANT_LIGNE)
            .codeBarresScanne(UPDATED_CODE_BARRES_SCANNE);
        LigneVenteDTO ligneVenteDTO = ligneVenteMapper.toDto(updatedLigneVente);

        restLigneVenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneVenteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneVenteDTO))
            )
            .andExpect(status().isOk());

        // Validate the LigneVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLigneVenteToMatchAllProperties(updatedLigneVente);
    }

    @Test
    @Transactional
    void putNonExistingLigneVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneVente.setId(longCount.incrementAndGet());

        // Create the LigneVente
        LigneVenteDTO ligneVenteDTO = ligneVenteMapper.toDto(ligneVente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneVenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneVenteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneVenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLigneVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneVente.setId(longCount.incrementAndGet());

        // Create the LigneVente
        LigneVenteDTO ligneVenteDTO = ligneVenteMapper.toDto(ligneVente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneVenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneVenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLigneVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneVente.setId(longCount.incrementAndGet());

        // Create the LigneVente
        LigneVenteDTO ligneVenteDTO = ligneVenteMapper.toDto(ligneVente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneVenteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneVenteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLigneVenteWithPatch() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneVente using partial update
        LigneVente partialUpdatedLigneVente = new LigneVente();
        partialUpdatedLigneVente.setId(ligneVente.getId());

        partialUpdatedLigneVente
            .quantite(UPDATED_QUANTITE)
            .prixUnitaire(UPDATED_PRIX_UNITAIRE)
            .remise(UPDATED_REMISE)
            .codeBarresScanne(UPDATED_CODE_BARRES_SCANNE);

        restLigneVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneVente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneVente))
            )
            .andExpect(status().isOk());

        // Validate the LigneVente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneVenteUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLigneVente, ligneVente),
            getPersistedLigneVente(ligneVente)
        );
    }

    @Test
    @Transactional
    void fullUpdateLigneVenteWithPatch() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneVente using partial update
        LigneVente partialUpdatedLigneVente = new LigneVente();
        partialUpdatedLigneVente.setId(ligneVente.getId());

        partialUpdatedLigneVente
            .quantite(UPDATED_QUANTITE)
            .prixUnitaire(UPDATED_PRIX_UNITAIRE)
            .remise(UPDATED_REMISE)
            .montantLigne(UPDATED_MONTANT_LIGNE)
            .codeBarresScanne(UPDATED_CODE_BARRES_SCANNE);

        restLigneVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneVente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneVente))
            )
            .andExpect(status().isOk());

        // Validate the LigneVente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneVenteUpdatableFieldsEquals(partialUpdatedLigneVente, getPersistedLigneVente(partialUpdatedLigneVente));
    }

    @Test
    @Transactional
    void patchNonExistingLigneVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneVente.setId(longCount.incrementAndGet());

        // Create the LigneVente
        LigneVenteDTO ligneVenteDTO = ligneVenteMapper.toDto(ligneVente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ligneVenteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneVenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLigneVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneVente.setId(longCount.incrementAndGet());

        // Create the LigneVente
        LigneVenteDTO ligneVenteDTO = ligneVenteMapper.toDto(ligneVente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneVenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneVenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLigneVente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneVente.setId(longCount.incrementAndGet());

        // Create the LigneVente
        LigneVenteDTO ligneVenteDTO = ligneVenteMapper.toDto(ligneVente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneVenteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ligneVenteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneVente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLigneVente() throws Exception {
        // Initialize the database
        insertedLigneVente = ligneVenteRepository.saveAndFlush(ligneVente);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ligneVente
        restLigneVenteMockMvc
            .perform(delete(ENTITY_API_URL_ID, ligneVente.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ligneVenteRepository.count();
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

    protected LigneVente getPersistedLigneVente(LigneVente ligneVente) {
        return ligneVenteRepository.findById(ligneVente.getId()).orElseThrow();
    }

    protected void assertPersistedLigneVenteToMatchAllProperties(LigneVente expectedLigneVente) {
        assertLigneVenteAllPropertiesEquals(expectedLigneVente, getPersistedLigneVente(expectedLigneVente));
    }

    protected void assertPersistedLigneVenteToMatchUpdatableProperties(LigneVente expectedLigneVente) {
        assertLigneVenteAllUpdatablePropertiesEquals(expectedLigneVente, getPersistedLigneVente(expectedLigneVente));
    }
}
