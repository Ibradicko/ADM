package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.CodeBarresProduitAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.CodeBarresProduit;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.enumeration.TypeCodeBarres;
import com.adm.supervision.repository.CodeBarresProduitRepository;
import com.adm.supervision.service.CodeBarresProduitService;
import com.adm.supervision.service.dto.CodeBarresProduitDTO;
import com.adm.supervision.service.mapper.CodeBarresProduitMapper;
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
 * Integration tests for the {@link CodeBarresProduitResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CodeBarresProduitResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final TypeCodeBarres DEFAULT_TYPE = TypeCodeBarres.EAN13;
    private static final TypeCodeBarres UPDATED_TYPE = TypeCodeBarres.EAN8;

    private static final Boolean DEFAULT_PRINCIPAL = false;
    private static final Boolean UPDATED_PRINCIPAL = true;

    private static final Boolean DEFAULT_GENERE_PAR_SYSTEME = false;
    private static final Boolean UPDATED_GENERE_PAR_SYSTEME = true;

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final Instant DEFAULT_DATE_AFFECTATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_AFFECTATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/code-barres-produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CodeBarresProduitRepository codeBarresProduitRepository;

    @Mock
    private CodeBarresProduitRepository codeBarresProduitRepositoryMock;

    @Autowired
    private CodeBarresProduitMapper codeBarresProduitMapper;

    @Mock
    private CodeBarresProduitService codeBarresProduitServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCodeBarresProduitMockMvc;

    private CodeBarresProduit codeBarresProduit;

    private CodeBarresProduit insertedCodeBarresProduit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CodeBarresProduit createEntity(EntityManager em) {
        CodeBarresProduit codeBarresProduit = new CodeBarresProduit()
            .code(DEFAULT_CODE)
            .type(DEFAULT_TYPE)
            .principal(DEFAULT_PRINCIPAL)
            .genereParSysteme(DEFAULT_GENERE_PAR_SYSTEME)
            .actif(DEFAULT_ACTIF)
            .dateAffectation(DEFAULT_DATE_AFFECTATION);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        codeBarresProduit.setProduit(produit);
        return codeBarresProduit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CodeBarresProduit createUpdatedEntity(EntityManager em) {
        CodeBarresProduit updatedCodeBarresProduit = new CodeBarresProduit()
            .code(UPDATED_CODE)
            .type(UPDATED_TYPE)
            .principal(UPDATED_PRINCIPAL)
            .genereParSysteme(UPDATED_GENERE_PAR_SYSTEME)
            .actif(UPDATED_ACTIF)
            .dateAffectation(UPDATED_DATE_AFFECTATION);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createUpdatedEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        updatedCodeBarresProduit.setProduit(produit);
        return updatedCodeBarresProduit;
    }

    @BeforeEach
    void initTest() {
        codeBarresProduit = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCodeBarresProduit != null) {
            codeBarresProduitRepository.delete(insertedCodeBarresProduit);
            insertedCodeBarresProduit = null;
        }
    }

    @Test
    @Transactional
    void createCodeBarresProduit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CodeBarresProduit
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);
        var returnedCodeBarresProduitDTO = om.readValue(
            restCodeBarresProduitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(codeBarresProduitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CodeBarresProduitDTO.class
        );

        // Validate the CodeBarresProduit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCodeBarresProduit = codeBarresProduitMapper.toEntity(returnedCodeBarresProduitDTO);
        assertCodeBarresProduitUpdatableFieldsEquals(returnedCodeBarresProduit, getPersistedCodeBarresProduit(returnedCodeBarresProduit));

        insertedCodeBarresProduit = returnedCodeBarresProduit;
    }

    @Test
    @Transactional
    void createCodeBarresProduitWithExistingId() throws Exception {
        // Create the CodeBarresProduit with an existing ID
        codeBarresProduit.setId(1L);
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCodeBarresProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(codeBarresProduitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CodeBarresProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void createSecondActivePrimaryBarcodeForSameProductShouldBeRejected() throws Exception {
        CodeBarresProduit existingPrimary = createEntity(em).code("PRIMARY-EXISTING").principal(true).actif(true);
        codeBarresProduitRepository.saveAndFlush(existingPrimary);

        CodeBarresProduit secondPrimary = createEntity(em)
            .code("PRIMARY-SECOND")
            .principal(true)
            .actif(true)
            .produit(existingPrimary.getProduit());
        CodeBarresProduitDTO dto = codeBarresProduitMapper.toDto(secondPrimary);

        restCodeBarresProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void createDuplicateActiveBarcodeWithinSameShopShouldBeRejected() throws Exception {
        Produit firstProduct = codeBarresProduit.getProduit();
        CodeBarresProduit existingBarcode = createEntity(em).code("SHOP-DUPLICATE").principal(true).actif(true).produit(firstProduct);
        codeBarresProduitRepository.saveAndFlush(existingBarcode);

        Produit secondProduct = ProduitResourceIT.createEntity(em);
        secondProduct.setCodeInterne("SECOND-" + longCount.incrementAndGet());
        secondProduct.setBoutique(firstProduct.getBoutique());
        em.persist(secondProduct);
        em.flush();

        CodeBarresProduit duplicateBarcode = createEntity(em).code("SHOP-DUPLICATE").principal(true).actif(true).produit(secondProduct);

        restCodeBarresProduitMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(codeBarresProduitMapper.toDto(duplicateBarcode)))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        codeBarresProduit.setCode(null);

        // Create the CodeBarresProduit, which fails.
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);

        restCodeBarresProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(codeBarresProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        codeBarresProduit.setType(null);

        // Create the CodeBarresProduit, which fails.
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);

        restCodeBarresProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(codeBarresProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrincipalIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        codeBarresProduit.setPrincipal(null);

        // Create the CodeBarresProduit, which fails.
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);

        restCodeBarresProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(codeBarresProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGenereParSystemeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        codeBarresProduit.setGenereParSysteme(null);

        // Create the CodeBarresProduit, which fails.
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);

        restCodeBarresProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(codeBarresProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        codeBarresProduit.setActif(null);

        // Create the CodeBarresProduit, which fails.
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);

        restCodeBarresProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(codeBarresProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateAffectationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        codeBarresProduit.setDateAffectation(null);

        // Create the CodeBarresProduit, which fails.
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);

        restCodeBarresProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(codeBarresProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCodeBarresProduits() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList
        restCodeBarresProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(codeBarresProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].principal").value(hasItem(DEFAULT_PRINCIPAL)))
            .andExpect(jsonPath("$.[*].genereParSysteme").value(hasItem(DEFAULT_GENERE_PAR_SYSTEME)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)))
            .andExpect(jsonPath("$.[*].dateAffectation").value(hasItem(DEFAULT_DATE_AFFECTATION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCodeBarresProduitsWithEagerRelationshipsIsEnabled() throws Exception {
        when(codeBarresProduitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCodeBarresProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(codeBarresProduitServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCodeBarresProduitsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(codeBarresProduitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCodeBarresProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(codeBarresProduitRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCodeBarresProduit() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get the codeBarresProduit
        restCodeBarresProduitMockMvc
            .perform(get(ENTITY_API_URL_ID, codeBarresProduit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(codeBarresProduit.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.principal").value(DEFAULT_PRINCIPAL))
            .andExpect(jsonPath("$.genereParSysteme").value(DEFAULT_GENERE_PAR_SYSTEME))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF))
            .andExpect(jsonPath("$.dateAffectation").value(DEFAULT_DATE_AFFECTATION.toString()));
    }

    @Test
    @Transactional
    void getCodeBarresProduitsByIdFiltering() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        Long id = codeBarresProduit.getId();

        defaultCodeBarresProduitFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCodeBarresProduitFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCodeBarresProduitFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where code equals to
        defaultCodeBarresProduitFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where code in
        defaultCodeBarresProduitFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where code is not null
        defaultCodeBarresProduitFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where code contains
        defaultCodeBarresProduitFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where code does not contain
        defaultCodeBarresProduitFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where type equals to
        defaultCodeBarresProduitFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where type in
        defaultCodeBarresProduitFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where type is not null
        defaultCodeBarresProduitFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByPrincipalIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where principal equals to
        defaultCodeBarresProduitFiltering("principal.equals=" + DEFAULT_PRINCIPAL, "principal.equals=" + UPDATED_PRINCIPAL);
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByPrincipalIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where principal in
        defaultCodeBarresProduitFiltering(
            "principal.in=" + DEFAULT_PRINCIPAL + "," + UPDATED_PRINCIPAL,
            "principal.in=" + UPDATED_PRINCIPAL
        );
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByPrincipalIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where principal is not null
        defaultCodeBarresProduitFiltering("principal.specified=true", "principal.specified=false");
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByGenereParSystemeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where genereParSysteme equals to
        defaultCodeBarresProduitFiltering(
            "genereParSysteme.equals=" + DEFAULT_GENERE_PAR_SYSTEME,
            "genereParSysteme.equals=" + UPDATED_GENERE_PAR_SYSTEME
        );
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByGenereParSystemeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where genereParSysteme in
        defaultCodeBarresProduitFiltering(
            "genereParSysteme.in=" + DEFAULT_GENERE_PAR_SYSTEME + "," + UPDATED_GENERE_PAR_SYSTEME,
            "genereParSysteme.in=" + UPDATED_GENERE_PAR_SYSTEME
        );
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByGenereParSystemeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where genereParSysteme is not null
        defaultCodeBarresProduitFiltering("genereParSysteme.specified=true", "genereParSysteme.specified=false");
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByActifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where actif equals to
        defaultCodeBarresProduitFiltering("actif.equals=" + DEFAULT_ACTIF, "actif.equals=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByActifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where actif in
        defaultCodeBarresProduitFiltering("actif.in=" + DEFAULT_ACTIF + "," + UPDATED_ACTIF, "actif.in=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByActifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where actif is not null
        defaultCodeBarresProduitFiltering("actif.specified=true", "actif.specified=false");
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByDateAffectationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where dateAffectation equals to
        defaultCodeBarresProduitFiltering(
            "dateAffectation.equals=" + DEFAULT_DATE_AFFECTATION,
            "dateAffectation.equals=" + UPDATED_DATE_AFFECTATION
        );
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByDateAffectationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where dateAffectation in
        defaultCodeBarresProduitFiltering(
            "dateAffectation.in=" + DEFAULT_DATE_AFFECTATION + "," + UPDATED_DATE_AFFECTATION,
            "dateAffectation.in=" + UPDATED_DATE_AFFECTATION
        );
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByDateAffectationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        // Get all the codeBarresProduitList where dateAffectation is not null
        defaultCodeBarresProduitFiltering("dateAffectation.specified=true", "dateAffectation.specified=false");
    }

    @Test
    @Transactional
    void getAllCodeBarresProduitsByProduitIsEqualToSomething() throws Exception {
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            codeBarresProduitRepository.saveAndFlush(codeBarresProduit);
            produit = ProduitResourceIT.createEntity(em);
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        em.persist(produit);
        em.flush();
        codeBarresProduit.setProduit(produit);
        codeBarresProduitRepository.saveAndFlush(codeBarresProduit);
        Long produitId = produit.getId();
        // Get all the codeBarresProduitList where produit equals to produitId
        defaultCodeBarresProduitShouldBeFound("produitId.equals=" + produitId);

        // Get all the codeBarresProduitList where produit equals to (produitId + 1)
        defaultCodeBarresProduitShouldNotBeFound("produitId.equals=" + (produitId + 1));
    }

    private void defaultCodeBarresProduitFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCodeBarresProduitShouldBeFound(shouldBeFound);
        defaultCodeBarresProduitShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCodeBarresProduitShouldBeFound(String filter) throws Exception {
        restCodeBarresProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(codeBarresProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].principal").value(hasItem(DEFAULT_PRINCIPAL)))
            .andExpect(jsonPath("$.[*].genereParSysteme").value(hasItem(DEFAULT_GENERE_PAR_SYSTEME)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)))
            .andExpect(jsonPath("$.[*].dateAffectation").value(hasItem(DEFAULT_DATE_AFFECTATION.toString())));

        // Check, that the count call also returns 1
        restCodeBarresProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCodeBarresProduitShouldNotBeFound(String filter) throws Exception {
        restCodeBarresProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCodeBarresProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCodeBarresProduit() throws Exception {
        // Get the codeBarresProduit
        restCodeBarresProduitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCodeBarresProduit() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the codeBarresProduit
        CodeBarresProduit updatedCodeBarresProduit = codeBarresProduitRepository.findById(codeBarresProduit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCodeBarresProduit are not directly saved in db
        em.detach(updatedCodeBarresProduit);
        updatedCodeBarresProduit
            .code(UPDATED_CODE)
            .type(UPDATED_TYPE)
            .principal(UPDATED_PRINCIPAL)
            .genereParSysteme(UPDATED_GENERE_PAR_SYSTEME)
            .actif(UPDATED_ACTIF)
            .dateAffectation(UPDATED_DATE_AFFECTATION);
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(updatedCodeBarresProduit);

        restCodeBarresProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, codeBarresProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(codeBarresProduitDTO))
            )
            .andExpect(status().isOk());

        // Validate the CodeBarresProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCodeBarresProduitToMatchAllProperties(updatedCodeBarresProduit);
    }

    @Test
    @Transactional
    void putNonExistingCodeBarresProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        codeBarresProduit.setId(longCount.incrementAndGet());

        // Create the CodeBarresProduit
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCodeBarresProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, codeBarresProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(codeBarresProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CodeBarresProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCodeBarresProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        codeBarresProduit.setId(longCount.incrementAndGet());

        // Create the CodeBarresProduit
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCodeBarresProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(codeBarresProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CodeBarresProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCodeBarresProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        codeBarresProduit.setId(longCount.incrementAndGet());

        // Create the CodeBarresProduit
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCodeBarresProduitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(codeBarresProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CodeBarresProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCodeBarresProduitWithPatch() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the codeBarresProduit using partial update
        CodeBarresProduit partialUpdatedCodeBarresProduit = new CodeBarresProduit();
        partialUpdatedCodeBarresProduit.setId(codeBarresProduit.getId());

        partialUpdatedCodeBarresProduit
            .code(UPDATED_CODE)
            .principal(UPDATED_PRINCIPAL)
            .actif(UPDATED_ACTIF)
            .dateAffectation(UPDATED_DATE_AFFECTATION);

        restCodeBarresProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCodeBarresProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCodeBarresProduit))
            )
            .andExpect(status().isOk());

        // Validate the CodeBarresProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCodeBarresProduitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCodeBarresProduit, codeBarresProduit),
            getPersistedCodeBarresProduit(codeBarresProduit)
        );
    }

    @Test
    @Transactional
    void fullUpdateCodeBarresProduitWithPatch() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the codeBarresProduit using partial update
        CodeBarresProduit partialUpdatedCodeBarresProduit = new CodeBarresProduit();
        partialUpdatedCodeBarresProduit.setId(codeBarresProduit.getId());

        partialUpdatedCodeBarresProduit
            .code(UPDATED_CODE)
            .type(UPDATED_TYPE)
            .principal(UPDATED_PRINCIPAL)
            .genereParSysteme(UPDATED_GENERE_PAR_SYSTEME)
            .actif(UPDATED_ACTIF)
            .dateAffectation(UPDATED_DATE_AFFECTATION);

        restCodeBarresProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCodeBarresProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCodeBarresProduit))
            )
            .andExpect(status().isOk());

        // Validate the CodeBarresProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCodeBarresProduitUpdatableFieldsEquals(
            partialUpdatedCodeBarresProduit,
            getPersistedCodeBarresProduit(partialUpdatedCodeBarresProduit)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCodeBarresProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        codeBarresProduit.setId(longCount.incrementAndGet());

        // Create the CodeBarresProduit
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCodeBarresProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, codeBarresProduitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(codeBarresProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CodeBarresProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCodeBarresProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        codeBarresProduit.setId(longCount.incrementAndGet());

        // Create the CodeBarresProduit
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCodeBarresProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(codeBarresProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CodeBarresProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCodeBarresProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        codeBarresProduit.setId(longCount.incrementAndGet());

        // Create the CodeBarresProduit
        CodeBarresProduitDTO codeBarresProduitDTO = codeBarresProduitMapper.toDto(codeBarresProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCodeBarresProduitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(codeBarresProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CodeBarresProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCodeBarresProduit() throws Exception {
        // Initialize the database
        insertedCodeBarresProduit = codeBarresProduitRepository.saveAndFlush(codeBarresProduit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the codeBarresProduit
        restCodeBarresProduitMockMvc
            .perform(delete(ENTITY_API_URL_ID, codeBarresProduit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return codeBarresProduitRepository.count();
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

    protected CodeBarresProduit getPersistedCodeBarresProduit(CodeBarresProduit codeBarresProduit) {
        return codeBarresProduitRepository.findById(codeBarresProduit.getId()).orElseThrow();
    }

    protected void assertPersistedCodeBarresProduitToMatchAllProperties(CodeBarresProduit expectedCodeBarresProduit) {
        assertCodeBarresProduitAllPropertiesEquals(expectedCodeBarresProduit, getPersistedCodeBarresProduit(expectedCodeBarresProduit));
    }

    protected void assertPersistedCodeBarresProduitToMatchUpdatableProperties(CodeBarresProduit expectedCodeBarresProduit) {
        assertCodeBarresProduitAllUpdatablePropertiesEquals(
            expectedCodeBarresProduit,
            getPersistedCodeBarresProduit(expectedCodeBarresProduit)
        );
    }
}
