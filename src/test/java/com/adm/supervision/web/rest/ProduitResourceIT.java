package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.ProduitAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.FamilleArticle;
import com.adm.supervision.domain.GroupeArticle;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.SousFamilleArticle;
import com.adm.supervision.domain.UniteMesure;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.TypePrix;
import com.adm.supervision.repository.ProduitRepository;
import com.adm.supervision.service.ProduitService;
import com.adm.supervision.service.dto.ProduitDTO;
import com.adm.supervision.service.mapper.ProduitMapper;
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
 * Integration tests for the {@link ProduitResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
class ProduitResourceIT {

    private static final String DEFAULT_CODE_INTERNE = "AAAAAAAAAA";
    private static final String UPDATED_CODE_INTERNE = "BBBBBBBBBB";

    private static final String DEFAULT_DESIGNATION = "AAAAAAAAAA";
    private static final String UPDATED_DESIGNATION = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final TypePrix DEFAULT_TYPE_PRIX = TypePrix.STANDARD;
    private static final TypePrix UPDATED_TYPE_PRIX = TypePrix.PROMOTION;

    private static final BigDecimal DEFAULT_PRIX_VENTE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRIX_VENTE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PRIX_VENTE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_TAUX_REDEVANCE_APPLICABLE = new BigDecimal(0);
    private static final BigDecimal UPDATED_TAUX_REDEVANCE_APPLICABLE = new BigDecimal(1);
    private static final BigDecimal SMALLER_TAUX_REDEVANCE_APPLICABLE = new BigDecimal(0 - 1);

    private static final StatutGeneral DEFAULT_STATUT = StatutGeneral.ACTIF;
    private static final StatutGeneral UPDATED_STATUT = StatutGeneral.INACTIF;

    private static final Instant DEFAULT_DATE_CREATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProduitRepository produitRepository;

    @Mock
    private ProduitRepository produitRepositoryMock;

    @Autowired
    private ProduitMapper produitMapper;

    @Mock
    private ProduitService produitServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProduitMockMvc;

    private Produit produit;

    private Produit insertedProduit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produit createEntity(EntityManager em) {
        Produit produit = new Produit()
            .codeInterne(DEFAULT_CODE_INTERNE)
            .designation(DEFAULT_DESIGNATION)
            .description(DEFAULT_DESCRIPTION)
            .typePrix(DEFAULT_TYPE_PRIX)
            .prixVente(DEFAULT_PRIX_VENTE)
            .tauxRedevanceApplicable(DEFAULT_TAUX_REDEVANCE_APPLICABLE)
            .statut(DEFAULT_STATUT)
            .dateCreation(DEFAULT_DATE_CREATION);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        produit.setBoutique(boutique);
        // Add required entity
        UniteMesure uniteMesure;
        if (TestUtil.findAll(em, UniteMesure.class).isEmpty()) {
            uniteMesure = UniteMesureResourceIT.createEntity();
            em.persist(uniteMesure);
            em.flush();
        } else {
            uniteMesure = TestUtil.findAll(em, UniteMesure.class).get(0);
        }
        produit.setUniteMesure(uniteMesure);
        return produit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produit createUpdatedEntity(EntityManager em) {
        Produit updatedProduit = new Produit()
            .codeInterne(UPDATED_CODE_INTERNE)
            .designation(UPDATED_DESIGNATION)
            .description(UPDATED_DESCRIPTION)
            .typePrix(UPDATED_TYPE_PRIX)
            .prixVente(UPDATED_PRIX_VENTE)
            .tauxRedevanceApplicable(UPDATED_TAUX_REDEVANCE_APPLICABLE)
            .statut(UPDATED_STATUT)
            .dateCreation(UPDATED_DATE_CREATION);
        // Add required entity
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            boutique = BoutiqueResourceIT.createUpdatedEntity();
            em.persist(boutique);
            em.flush();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        updatedProduit.setBoutique(boutique);
        // Add required entity
        UniteMesure uniteMesure;
        if (TestUtil.findAll(em, UniteMesure.class).isEmpty()) {
            uniteMesure = UniteMesureResourceIT.createUpdatedEntity();
            em.persist(uniteMesure);
            em.flush();
        } else {
            uniteMesure = TestUtil.findAll(em, UniteMesure.class).get(0);
        }
        updatedProduit.setUniteMesure(uniteMesure);
        return updatedProduit;
    }

    @BeforeEach
    void initTest() {
        produit = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedProduit != null) {
            produitRepository.delete(insertedProduit);
            insertedProduit = null;
        }
    }

    @Test
    @Transactional
    void createProduit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);
        var returnedProduitDTO = om.readValue(
            restProduitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProduitDTO.class
        );

        // Validate the Produit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProduit = produitMapper.toEntity(returnedProduitDTO);
        assertProduitUpdatableFieldsEquals(returnedProduit, getPersistedProduit(returnedProduit));

        insertedProduit = returnedProduit;
    }

    @Test
    @Transactional
    void createProduitWithExistingId() throws Exception {
        // Create the Produit with an existing ID
        produit.setId(1L);
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeInterneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        produit.setCodeInterne(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        restProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDesignationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        produit.setDesignation(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        restProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypePrixIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        produit.setTypePrix(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        restProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrixVenteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        produit.setPrixVente(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        restProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        produit.setStatut(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        restProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        produit.setDateCreation(null);

        // Create the Produit, which fails.
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        restProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProduits() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList
        restProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produit.getId().intValue())))
            .andExpect(jsonPath("$.[*].codeInterne").value(hasItem(DEFAULT_CODE_INTERNE)))
            .andExpect(jsonPath("$.[*].designation").value(hasItem(DEFAULT_DESIGNATION)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].typePrix").value(hasItem(DEFAULT_TYPE_PRIX.toString())))
            .andExpect(jsonPath("$.[*].prixVente").value(hasItem(sameNumber(DEFAULT_PRIX_VENTE))))
            .andExpect(jsonPath("$.[*].tauxRedevanceApplicable").value(hasItem(sameNumber(DEFAULT_TAUX_REDEVANCE_APPLICABLE))))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProduitsWithEagerRelationshipsIsEnabled() throws Exception {
        when(produitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(produitServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProduitsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(produitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(produitRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProduit() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get the produit
        restProduitMockMvc
            .perform(get(ENTITY_API_URL_ID, produit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(produit.getId().intValue()))
            .andExpect(jsonPath("$.codeInterne").value(DEFAULT_CODE_INTERNE))
            .andExpect(jsonPath("$.designation").value(DEFAULT_DESIGNATION))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.typePrix").value(DEFAULT_TYPE_PRIX.toString()))
            .andExpect(jsonPath("$.prixVente").value(sameNumber(DEFAULT_PRIX_VENTE)))
            .andExpect(jsonPath("$.tauxRedevanceApplicable").value(sameNumber(DEFAULT_TAUX_REDEVANCE_APPLICABLE)))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()));
    }

    @Test
    @Transactional
    void getProduitsByIdFiltering() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        Long id = produit.getId();

        defaultProduitFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProduitFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProduitFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProduitsByCodeInterneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where codeInterne equals to
        defaultProduitFiltering("codeInterne.equals=" + DEFAULT_CODE_INTERNE, "codeInterne.equals=" + UPDATED_CODE_INTERNE);
    }

    @Test
    @Transactional
    void getAllProduitsByCodeInterneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where codeInterne in
        defaultProduitFiltering(
            "codeInterne.in=" + DEFAULT_CODE_INTERNE + "," + UPDATED_CODE_INTERNE,
            "codeInterne.in=" + UPDATED_CODE_INTERNE
        );
    }

    @Test
    @Transactional
    void getAllProduitsByCodeInterneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where codeInterne is not null
        defaultProduitFiltering("codeInterne.specified=true", "codeInterne.specified=false");
    }

    @Test
    @Transactional
    void getAllProduitsByCodeInterneContainsSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where codeInterne contains
        defaultProduitFiltering("codeInterne.contains=" + DEFAULT_CODE_INTERNE, "codeInterne.contains=" + UPDATED_CODE_INTERNE);
    }

    @Test
    @Transactional
    void getAllProduitsByCodeInterneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where codeInterne does not contain
        defaultProduitFiltering("codeInterne.doesNotContain=" + UPDATED_CODE_INTERNE, "codeInterne.doesNotContain=" + DEFAULT_CODE_INTERNE);
    }

    @Test
    @Transactional
    void getAllProduitsByDesignationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where designation equals to
        defaultProduitFiltering("designation.equals=" + DEFAULT_DESIGNATION, "designation.equals=" + UPDATED_DESIGNATION);
    }

    @Test
    @Transactional
    void getAllProduitsByDesignationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where designation in
        defaultProduitFiltering(
            "designation.in=" + DEFAULT_DESIGNATION + "," + UPDATED_DESIGNATION,
            "designation.in=" + UPDATED_DESIGNATION
        );
    }

    @Test
    @Transactional
    void getAllProduitsByDesignationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where designation is not null
        defaultProduitFiltering("designation.specified=true", "designation.specified=false");
    }

    @Test
    @Transactional
    void getAllProduitsByDesignationContainsSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where designation contains
        defaultProduitFiltering("designation.contains=" + DEFAULT_DESIGNATION, "designation.contains=" + UPDATED_DESIGNATION);
    }

    @Test
    @Transactional
    void getAllProduitsByDesignationNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where designation does not contain
        defaultProduitFiltering("designation.doesNotContain=" + UPDATED_DESIGNATION, "designation.doesNotContain=" + DEFAULT_DESIGNATION);
    }

    @Test
    @Transactional
    void getAllProduitsByTypePrixIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where typePrix equals to
        defaultProduitFiltering("typePrix.equals=" + DEFAULT_TYPE_PRIX, "typePrix.equals=" + UPDATED_TYPE_PRIX);
    }

    @Test
    @Transactional
    void getAllProduitsByTypePrixIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where typePrix in
        defaultProduitFiltering("typePrix.in=" + DEFAULT_TYPE_PRIX + "," + UPDATED_TYPE_PRIX, "typePrix.in=" + UPDATED_TYPE_PRIX);
    }

    @Test
    @Transactional
    void getAllProduitsByTypePrixIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where typePrix is not null
        defaultProduitFiltering("typePrix.specified=true", "typePrix.specified=false");
    }

    @Test
    @Transactional
    void getAllProduitsByPrixVenteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prixVente equals to
        defaultProduitFiltering("prixVente.equals=" + DEFAULT_PRIX_VENTE, "prixVente.equals=" + UPDATED_PRIX_VENTE);
    }

    @Test
    @Transactional
    void getAllProduitsByPrixVenteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prixVente in
        defaultProduitFiltering("prixVente.in=" + DEFAULT_PRIX_VENTE + "," + UPDATED_PRIX_VENTE, "prixVente.in=" + UPDATED_PRIX_VENTE);
    }

    @Test
    @Transactional
    void getAllProduitsByPrixVenteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prixVente is not null
        defaultProduitFiltering("prixVente.specified=true", "prixVente.specified=false");
    }

    @Test
    @Transactional
    void getAllProduitsByPrixVenteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prixVente is greater than or equal to
        defaultProduitFiltering("prixVente.greaterThanOrEqual=" + DEFAULT_PRIX_VENTE, "prixVente.greaterThanOrEqual=" + UPDATED_PRIX_VENTE);
    }

    @Test
    @Transactional
    void getAllProduitsByPrixVenteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prixVente is less than or equal to
        defaultProduitFiltering("prixVente.lessThanOrEqual=" + DEFAULT_PRIX_VENTE, "prixVente.lessThanOrEqual=" + SMALLER_PRIX_VENTE);
    }

    @Test
    @Transactional
    void getAllProduitsByPrixVenteIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prixVente is less than
        defaultProduitFiltering("prixVente.lessThan=" + UPDATED_PRIX_VENTE, "prixVente.lessThan=" + DEFAULT_PRIX_VENTE);
    }

    @Test
    @Transactional
    void getAllProduitsByPrixVenteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where prixVente is greater than
        defaultProduitFiltering("prixVente.greaterThan=" + SMALLER_PRIX_VENTE, "prixVente.greaterThan=" + DEFAULT_PRIX_VENTE);
    }

    @Test
    @Transactional
    void getAllProduitsByTauxRedevanceApplicableIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where tauxRedevanceApplicable equals to
        defaultProduitFiltering(
            "tauxRedevanceApplicable.equals=" + DEFAULT_TAUX_REDEVANCE_APPLICABLE,
            "tauxRedevanceApplicable.equals=" + UPDATED_TAUX_REDEVANCE_APPLICABLE
        );
    }

    @Test
    @Transactional
    void getAllProduitsByTauxRedevanceApplicableIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where tauxRedevanceApplicable in
        defaultProduitFiltering(
            "tauxRedevanceApplicable.in=" + DEFAULT_TAUX_REDEVANCE_APPLICABLE + "," + UPDATED_TAUX_REDEVANCE_APPLICABLE,
            "tauxRedevanceApplicable.in=" + UPDATED_TAUX_REDEVANCE_APPLICABLE
        );
    }

    @Test
    @Transactional
    void getAllProduitsByTauxRedevanceApplicableIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where tauxRedevanceApplicable is not null
        defaultProduitFiltering("tauxRedevanceApplicable.specified=true", "tauxRedevanceApplicable.specified=false");
    }

    @Test
    @Transactional
    void getAllProduitsByTauxRedevanceApplicableIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where tauxRedevanceApplicable is greater than or equal to
        defaultProduitFiltering(
            "tauxRedevanceApplicable.greaterThanOrEqual=" + DEFAULT_TAUX_REDEVANCE_APPLICABLE,
            "tauxRedevanceApplicable.greaterThanOrEqual=" + (DEFAULT_TAUX_REDEVANCE_APPLICABLE.add(BigDecimal.ONE))
        );
    }

    @Test
    @Transactional
    void getAllProduitsByTauxRedevanceApplicableIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where tauxRedevanceApplicable is less than or equal to
        defaultProduitFiltering(
            "tauxRedevanceApplicable.lessThanOrEqual=" + DEFAULT_TAUX_REDEVANCE_APPLICABLE,
            "tauxRedevanceApplicable.lessThanOrEqual=" + SMALLER_TAUX_REDEVANCE_APPLICABLE
        );
    }

    @Test
    @Transactional
    void getAllProduitsByTauxRedevanceApplicableIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where tauxRedevanceApplicable is less than
        defaultProduitFiltering(
            "tauxRedevanceApplicable.lessThan=" + (DEFAULT_TAUX_REDEVANCE_APPLICABLE.add(BigDecimal.ONE)),
            "tauxRedevanceApplicable.lessThan=" + DEFAULT_TAUX_REDEVANCE_APPLICABLE
        );
    }

    @Test
    @Transactional
    void getAllProduitsByTauxRedevanceApplicableIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where tauxRedevanceApplicable is greater than
        defaultProduitFiltering(
            "tauxRedevanceApplicable.greaterThan=" + SMALLER_TAUX_REDEVANCE_APPLICABLE,
            "tauxRedevanceApplicable.greaterThan=" + DEFAULT_TAUX_REDEVANCE_APPLICABLE
        );
    }

    @Test
    @Transactional
    void getAllProduitsByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where statut equals to
        defaultProduitFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllProduitsByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where statut in
        defaultProduitFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllProduitsByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where statut is not null
        defaultProduitFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllProduitsByDateCreationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where dateCreation equals to
        defaultProduitFiltering("dateCreation.equals=" + DEFAULT_DATE_CREATION, "dateCreation.equals=" + UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    void getAllProduitsByDateCreationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where dateCreation in
        defaultProduitFiltering(
            "dateCreation.in=" + DEFAULT_DATE_CREATION + "," + UPDATED_DATE_CREATION,
            "dateCreation.in=" + UPDATED_DATE_CREATION
        );
    }

    @Test
    @Transactional
    void getAllProduitsByDateCreationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        // Get all the produitList where dateCreation is not null
        defaultProduitFiltering("dateCreation.specified=true", "dateCreation.specified=false");
    }

    @Test
    @Transactional
    void getAllProduitsByBoutiqueIsEqualToSomething() throws Exception {
        Boutique boutique;
        if (TestUtil.findAll(em, Boutique.class).isEmpty()) {
            produitRepository.saveAndFlush(produit);
            boutique = BoutiqueResourceIT.createEntity();
        } else {
            boutique = TestUtil.findAll(em, Boutique.class).get(0);
        }
        em.persist(boutique);
        em.flush();
        produit.setBoutique(boutique);
        produitRepository.saveAndFlush(produit);
        Long boutiqueId = boutique.getId();
        // Get all the produitList where boutique equals to boutiqueId
        defaultProduitShouldBeFound("boutiqueId.equals=" + boutiqueId);

        // Get all the produitList where boutique equals to (boutiqueId + 1)
        defaultProduitShouldNotBeFound("boutiqueId.equals=" + (boutiqueId + 1));
    }

    @Test
    @Transactional
    void getAllProduitsByGroupeArticleIsEqualToSomething() throws Exception {
        GroupeArticle groupeArticle;
        if (TestUtil.findAll(em, GroupeArticle.class).isEmpty()) {
            produitRepository.saveAndFlush(produit);
            groupeArticle = GroupeArticleResourceIT.createEntity(em);
        } else {
            groupeArticle = TestUtil.findAll(em, GroupeArticle.class).get(0);
        }
        em.persist(groupeArticle);
        em.flush();
        produit.setGroupeArticle(groupeArticle);
        produitRepository.saveAndFlush(produit);
        Long groupeArticleId = groupeArticle.getId();
        // Get all the produitList where groupeArticle equals to groupeArticleId
        defaultProduitShouldBeFound("groupeArticleId.equals=" + groupeArticleId);

        // Get all the produitList where groupeArticle equals to (groupeArticleId + 1)
        defaultProduitShouldNotBeFound("groupeArticleId.equals=" + (groupeArticleId + 1));
    }

    @Test
    @Transactional
    void getAllProduitsByFamilleArticleIsEqualToSomething() throws Exception {
        FamilleArticle familleArticle;
        if (TestUtil.findAll(em, FamilleArticle.class).isEmpty()) {
            produitRepository.saveAndFlush(produit);
            familleArticle = FamilleArticleResourceIT.createEntity(em);
        } else {
            familleArticle = TestUtil.findAll(em, FamilleArticle.class).get(0);
        }
        em.persist(familleArticle);
        em.flush();
        produit.setFamilleArticle(familleArticle);
        produitRepository.saveAndFlush(produit);
        Long familleArticleId = familleArticle.getId();
        // Get all the produitList where familleArticle equals to familleArticleId
        defaultProduitShouldBeFound("familleArticleId.equals=" + familleArticleId);

        // Get all the produitList where familleArticle equals to (familleArticleId + 1)
        defaultProduitShouldNotBeFound("familleArticleId.equals=" + (familleArticleId + 1));
    }

    @Test
    @Transactional
    void getAllProduitsBySousFamilleArticleIsEqualToSomething() throws Exception {
        SousFamilleArticle sousFamilleArticle;
        if (TestUtil.findAll(em, SousFamilleArticle.class).isEmpty()) {
            produitRepository.saveAndFlush(produit);
            sousFamilleArticle = SousFamilleArticleResourceIT.createEntity(em);
        } else {
            sousFamilleArticle = TestUtil.findAll(em, SousFamilleArticle.class).get(0);
        }
        em.persist(sousFamilleArticle);
        em.flush();
        produit.setSousFamilleArticle(sousFamilleArticle);
        produitRepository.saveAndFlush(produit);
        Long sousFamilleArticleId = sousFamilleArticle.getId();
        // Get all the produitList where sousFamilleArticle equals to sousFamilleArticleId
        defaultProduitShouldBeFound("sousFamilleArticleId.equals=" + sousFamilleArticleId);

        // Get all the produitList where sousFamilleArticle equals to (sousFamilleArticleId + 1)
        defaultProduitShouldNotBeFound("sousFamilleArticleId.equals=" + (sousFamilleArticleId + 1));
    }

    @Test
    @Transactional
    void getAllProduitsByUniteMesureIsEqualToSomething() throws Exception {
        UniteMesure uniteMesure;
        if (TestUtil.findAll(em, UniteMesure.class).isEmpty()) {
            produitRepository.saveAndFlush(produit);
            uniteMesure = UniteMesureResourceIT.createEntity();
        } else {
            uniteMesure = TestUtil.findAll(em, UniteMesure.class).get(0);
        }
        em.persist(uniteMesure);
        em.flush();
        produit.setUniteMesure(uniteMesure);
        produitRepository.saveAndFlush(produit);
        Long uniteMesureId = uniteMesure.getId();
        // Get all the produitList where uniteMesure equals to uniteMesureId
        defaultProduitShouldBeFound("uniteMesureId.equals=" + uniteMesureId);

        // Get all the produitList where uniteMesure equals to (uniteMesureId + 1)
        defaultProduitShouldNotBeFound("uniteMesureId.equals=" + (uniteMesureId + 1));
    }

    private void defaultProduitFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProduitShouldBeFound(shouldBeFound);
        defaultProduitShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProduitShouldBeFound(String filter) throws Exception {
        restProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(produit.getId().intValue())))
            .andExpect(jsonPath("$.[*].codeInterne").value(hasItem(DEFAULT_CODE_INTERNE)))
            .andExpect(jsonPath("$.[*].designation").value(hasItem(DEFAULT_DESIGNATION)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].typePrix").value(hasItem(DEFAULT_TYPE_PRIX.toString())))
            .andExpect(jsonPath("$.[*].prixVente").value(hasItem(sameNumber(DEFAULT_PRIX_VENTE))))
            .andExpect(jsonPath("$.[*].tauxRedevanceApplicable").value(hasItem(sameNumber(DEFAULT_TAUX_REDEVANCE_APPLICABLE))))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())));

        // Check, that the count call also returns 1
        restProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProduitShouldNotBeFound(String filter) throws Exception {
        restProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProduit() throws Exception {
        // Get the produit
        restProduitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProduit() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the produit
        Produit updatedProduit = produitRepository.findById(produit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProduit are not directly saved in db
        em.detach(updatedProduit);
        updatedProduit
            .codeInterne(UPDATED_CODE_INTERNE)
            .designation(UPDATED_DESIGNATION)
            .description(UPDATED_DESCRIPTION)
            .typePrix(UPDATED_TYPE_PRIX)
            .prixVente(UPDATED_PRIX_VENTE)
            .tauxRedevanceApplicable(UPDATED_TAUX_REDEVANCE_APPLICABLE)
            .statut(UPDATED_STATUT)
            .dateCreation(UPDATED_DATE_CREATION);
        ProduitDTO produitDTO = produitMapper.toDto(updatedProduit);

        restProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, produitDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO))
            )
            .andExpect(status().isOk());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProduitToMatchAllProperties(updatedProduit);
    }

    @Test
    @Transactional
    void putNonExistingProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, produitDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(produitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProduitWithPatch() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the produit using partial update
        Produit partialUpdatedProduit = new Produit();
        partialUpdatedProduit.setId(produit.getId());

        partialUpdatedProduit
            .typePrix(UPDATED_TYPE_PRIX)
            .tauxRedevanceApplicable(UPDATED_TAUX_REDEVANCE_APPLICABLE)
            .statut(UPDATED_STATUT)
            .dateCreation(UPDATED_DATE_CREATION);

        restProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProduit))
            )
            .andExpect(status().isOk());

        // Validate the Produit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProduitUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProduit, produit), getPersistedProduit(produit));
    }

    @Test
    @Transactional
    void fullUpdateProduitWithPatch() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the produit using partial update
        Produit partialUpdatedProduit = new Produit();
        partialUpdatedProduit.setId(produit.getId());

        partialUpdatedProduit
            .codeInterne(UPDATED_CODE_INTERNE)
            .designation(UPDATED_DESIGNATION)
            .description(UPDATED_DESCRIPTION)
            .typePrix(UPDATED_TYPE_PRIX)
            .prixVente(UPDATED_PRIX_VENTE)
            .tauxRedevanceApplicable(UPDATED_TAUX_REDEVANCE_APPLICABLE)
            .statut(UPDATED_STATUT)
            .dateCreation(UPDATED_DATE_CREATION);

        restProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProduit))
            )
            .andExpect(status().isOk());

        // Validate the Produit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProduitUpdatableFieldsEquals(partialUpdatedProduit, getPersistedProduit(partialUpdatedProduit));
    }

    @Test
    @Transactional
    void patchNonExistingProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, produitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(produitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(produitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        produit.setId(longCount.incrementAndGet());

        // Create the Produit
        ProduitDTO produitDTO = produitMapper.toDto(produit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProduitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(produitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Produit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProduit() throws Exception {
        // Initialize the database
        insertedProduit = produitRepository.saveAndFlush(produit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the produit
        restProduitMockMvc
            .perform(delete(ENTITY_API_URL_ID, produit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return produitRepository.count();
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

    protected Produit getPersistedProduit(Produit produit) {
        return produitRepository.findById(produit.getId()).orElseThrow();
    }

    protected void assertPersistedProduitToMatchAllProperties(Produit expectedProduit) {
        assertProduitAllPropertiesEquals(expectedProduit, getPersistedProduit(expectedProduit));
    }

    protected void assertPersistedProduitToMatchUpdatableProperties(Produit expectedProduit) {
        assertProduitAllUpdatablePropertiesEquals(expectedProduit, getPersistedProduit(expectedProduit));
    }
}
