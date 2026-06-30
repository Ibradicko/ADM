package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.LigneReceptionProduitAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.LigneReceptionProduit;
import com.adm.supervision.domain.Produit;
import com.adm.supervision.domain.ReceptionProduit;
import com.adm.supervision.repository.LigneReceptionProduitRepository;
import com.adm.supervision.service.LigneReceptionProduitService;
import com.adm.supervision.service.dto.LigneReceptionProduitDTO;
import com.adm.supervision.service.mapper.LigneReceptionProduitMapper;
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
 * Integration tests for the {@link LigneReceptionProduitResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LigneReceptionProduitResourceIT {

    private static final BigDecimal DEFAULT_QUANTITE_ATTENDUE = new BigDecimal(0);
    private static final BigDecimal UPDATED_QUANTITE_ATTENDUE = new BigDecimal(1);
    private static final BigDecimal SMALLER_QUANTITE_ATTENDUE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_QUANTITE_RECUE = new BigDecimal(0);
    private static final BigDecimal UPDATED_QUANTITE_RECUE = new BigDecimal(1);
    private static final BigDecimal SMALLER_QUANTITE_RECUE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_ECART = new BigDecimal(1);
    private static final BigDecimal UPDATED_ECART = new BigDecimal(2);
    private static final BigDecimal SMALLER_ECART = new BigDecimal(1 - 1);

    private static final String DEFAULT_CODE_BARRES_SCANNE = "AAAAAAAAAA";
    private static final String UPDATED_CODE_BARRES_SCANNE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ligne-reception-produits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LigneReceptionProduitRepository ligneReceptionProduitRepository;

    @Mock
    private LigneReceptionProduitRepository ligneReceptionProduitRepositoryMock;

    @Autowired
    private LigneReceptionProduitMapper ligneReceptionProduitMapper;

    @Mock
    private LigneReceptionProduitService ligneReceptionProduitServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLigneReceptionProduitMockMvc;

    private LigneReceptionProduit ligneReceptionProduit;

    private LigneReceptionProduit insertedLigneReceptionProduit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneReceptionProduit createEntity(EntityManager em) {
        LigneReceptionProduit ligneReceptionProduit = new LigneReceptionProduit()
            .quantiteAttendue(DEFAULT_QUANTITE_ATTENDUE)
            .quantiteRecue(DEFAULT_QUANTITE_RECUE)
            .ecart(DEFAULT_ECART)
            .codeBarresScanne(DEFAULT_CODE_BARRES_SCANNE);
        // Add required entity
        ReceptionProduit receptionProduit;
        if (TestUtil.findAll(em, ReceptionProduit.class).isEmpty()) {
            receptionProduit = ReceptionProduitResourceIT.createEntity(em);
            em.persist(receptionProduit);
            em.flush();
        } else {
            receptionProduit = TestUtil.findAll(em, ReceptionProduit.class).get(0);
        }
        ligneReceptionProduit.setReception(receptionProduit);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        ligneReceptionProduit.setProduit(produit);
        return ligneReceptionProduit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneReceptionProduit createUpdatedEntity(EntityManager em) {
        LigneReceptionProduit updatedLigneReceptionProduit = new LigneReceptionProduit()
            .quantiteAttendue(UPDATED_QUANTITE_ATTENDUE)
            .quantiteRecue(UPDATED_QUANTITE_RECUE)
            .ecart(UPDATED_ECART)
            .codeBarresScanne(UPDATED_CODE_BARRES_SCANNE);
        // Add required entity
        ReceptionProduit receptionProduit;
        if (TestUtil.findAll(em, ReceptionProduit.class).isEmpty()) {
            receptionProduit = ReceptionProduitResourceIT.createUpdatedEntity(em);
            em.persist(receptionProduit);
            em.flush();
        } else {
            receptionProduit = TestUtil.findAll(em, ReceptionProduit.class).get(0);
        }
        updatedLigneReceptionProduit.setReception(receptionProduit);
        // Add required entity
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            produit = ProduitResourceIT.createUpdatedEntity(em);
            em.persist(produit);
            em.flush();
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        updatedLigneReceptionProduit.setProduit(produit);
        return updatedLigneReceptionProduit;
    }

    @BeforeEach
    void initTest() {
        ligneReceptionProduit = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLigneReceptionProduit != null) {
            ligneReceptionProduitRepository.delete(insertedLigneReceptionProduit);
            insertedLigneReceptionProduit = null;
        }
    }

    @Test
    @Transactional
    void createLigneReceptionProduit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LigneReceptionProduit
        LigneReceptionProduitDTO ligneReceptionProduitDTO = ligneReceptionProduitMapper.toDto(ligneReceptionProduit);
        var returnedLigneReceptionProduitDTO = om.readValue(
            restLigneReceptionProduitMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneReceptionProduitDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LigneReceptionProduitDTO.class
        );

        // Validate the LigneReceptionProduit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLigneReceptionProduit = ligneReceptionProduitMapper.toEntity(returnedLigneReceptionProduitDTO);
        assertLigneReceptionProduitUpdatableFieldsEquals(
            returnedLigneReceptionProduit,
            getPersistedLigneReceptionProduit(returnedLigneReceptionProduit)
        );

        insertedLigneReceptionProduit = returnedLigneReceptionProduit;
    }

    @Test
    @Transactional
    void createLigneReceptionProduitWithExistingId() throws Exception {
        // Create the LigneReceptionProduit with an existing ID
        ligneReceptionProduit.setId(1L);
        LigneReceptionProduitDTO ligneReceptionProduitDTO = ligneReceptionProduitMapper.toDto(ligneReceptionProduit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLigneReceptionProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneReceptionProduitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LigneReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantiteRecueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ligneReceptionProduit.setQuantiteRecue(null);

        // Create the LigneReceptionProduit, which fails.
        LigneReceptionProduitDTO ligneReceptionProduitDTO = ligneReceptionProduitMapper.toDto(ligneReceptionProduit);

        restLigneReceptionProduitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneReceptionProduitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduits() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList
        restLigneReceptionProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligneReceptionProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantiteAttendue").value(hasItem(sameNumber(DEFAULT_QUANTITE_ATTENDUE))))
            .andExpect(jsonPath("$.[*].quantiteRecue").value(hasItem(sameNumber(DEFAULT_QUANTITE_RECUE))))
            .andExpect(jsonPath("$.[*].ecart").value(hasItem(sameNumber(DEFAULT_ECART))))
            .andExpect(jsonPath("$.[*].codeBarresScanne").value(hasItem(DEFAULT_CODE_BARRES_SCANNE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLigneReceptionProduitsWithEagerRelationshipsIsEnabled() throws Exception {
        when(ligneReceptionProduitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLigneReceptionProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ligneReceptionProduitServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLigneReceptionProduitsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ligneReceptionProduitServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLigneReceptionProduitMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ligneReceptionProduitRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLigneReceptionProduit() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get the ligneReceptionProduit
        restLigneReceptionProduitMockMvc
            .perform(get(ENTITY_API_URL_ID, ligneReceptionProduit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ligneReceptionProduit.getId().intValue()))
            .andExpect(jsonPath("$.quantiteAttendue").value(sameNumber(DEFAULT_QUANTITE_ATTENDUE)))
            .andExpect(jsonPath("$.quantiteRecue").value(sameNumber(DEFAULT_QUANTITE_RECUE)))
            .andExpect(jsonPath("$.ecart").value(sameNumber(DEFAULT_ECART)))
            .andExpect(jsonPath("$.codeBarresScanne").value(DEFAULT_CODE_BARRES_SCANNE));
    }

    @Test
    @Transactional
    void getLigneReceptionProduitsByIdFiltering() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        Long id = ligneReceptionProduit.getId();

        defaultLigneReceptionProduitFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLigneReceptionProduitFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLigneReceptionProduitFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteAttendueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteAttendue equals to
        defaultLigneReceptionProduitFiltering(
            "quantiteAttendue.equals=" + DEFAULT_QUANTITE_ATTENDUE,
            "quantiteAttendue.equals=" + UPDATED_QUANTITE_ATTENDUE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteAttendueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteAttendue in
        defaultLigneReceptionProduitFiltering(
            "quantiteAttendue.in=" + DEFAULT_QUANTITE_ATTENDUE + "," + UPDATED_QUANTITE_ATTENDUE,
            "quantiteAttendue.in=" + UPDATED_QUANTITE_ATTENDUE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteAttendueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteAttendue is not null
        defaultLigneReceptionProduitFiltering("quantiteAttendue.specified=true", "quantiteAttendue.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteAttendueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteAttendue is greater than or equal to
        defaultLigneReceptionProduitFiltering(
            "quantiteAttendue.greaterThanOrEqual=" + DEFAULT_QUANTITE_ATTENDUE,
            "quantiteAttendue.greaterThanOrEqual=" + UPDATED_QUANTITE_ATTENDUE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteAttendueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteAttendue is less than or equal to
        defaultLigneReceptionProduitFiltering(
            "quantiteAttendue.lessThanOrEqual=" + DEFAULT_QUANTITE_ATTENDUE,
            "quantiteAttendue.lessThanOrEqual=" + SMALLER_QUANTITE_ATTENDUE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteAttendueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteAttendue is less than
        defaultLigneReceptionProduitFiltering(
            "quantiteAttendue.lessThan=" + UPDATED_QUANTITE_ATTENDUE,
            "quantiteAttendue.lessThan=" + DEFAULT_QUANTITE_ATTENDUE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteAttendueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteAttendue is greater than
        defaultLigneReceptionProduitFiltering(
            "quantiteAttendue.greaterThan=" + SMALLER_QUANTITE_ATTENDUE,
            "quantiteAttendue.greaterThan=" + DEFAULT_QUANTITE_ATTENDUE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteRecueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteRecue equals to
        defaultLigneReceptionProduitFiltering(
            "quantiteRecue.equals=" + DEFAULT_QUANTITE_RECUE,
            "quantiteRecue.equals=" + UPDATED_QUANTITE_RECUE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteRecueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteRecue in
        defaultLigneReceptionProduitFiltering(
            "quantiteRecue.in=" + DEFAULT_QUANTITE_RECUE + "," + UPDATED_QUANTITE_RECUE,
            "quantiteRecue.in=" + UPDATED_QUANTITE_RECUE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteRecueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteRecue is not null
        defaultLigneReceptionProduitFiltering("quantiteRecue.specified=true", "quantiteRecue.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteRecueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteRecue is greater than or equal to
        defaultLigneReceptionProduitFiltering(
            "quantiteRecue.greaterThanOrEqual=" + DEFAULT_QUANTITE_RECUE,
            "quantiteRecue.greaterThanOrEqual=" + UPDATED_QUANTITE_RECUE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteRecueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteRecue is less than or equal to
        defaultLigneReceptionProduitFiltering(
            "quantiteRecue.lessThanOrEqual=" + DEFAULT_QUANTITE_RECUE,
            "quantiteRecue.lessThanOrEqual=" + SMALLER_QUANTITE_RECUE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteRecueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteRecue is less than
        defaultLigneReceptionProduitFiltering(
            "quantiteRecue.lessThan=" + UPDATED_QUANTITE_RECUE,
            "quantiteRecue.lessThan=" + DEFAULT_QUANTITE_RECUE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByQuantiteRecueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where quantiteRecue is greater than
        defaultLigneReceptionProduitFiltering(
            "quantiteRecue.greaterThan=" + SMALLER_QUANTITE_RECUE,
            "quantiteRecue.greaterThan=" + DEFAULT_QUANTITE_RECUE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByEcartIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where ecart equals to
        defaultLigneReceptionProduitFiltering("ecart.equals=" + DEFAULT_ECART, "ecart.equals=" + UPDATED_ECART);
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByEcartIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where ecart in
        defaultLigneReceptionProduitFiltering("ecart.in=" + DEFAULT_ECART + "," + UPDATED_ECART, "ecart.in=" + UPDATED_ECART);
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByEcartIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where ecart is not null
        defaultLigneReceptionProduitFiltering("ecart.specified=true", "ecart.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByEcartIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where ecart is greater than or equal to
        defaultLigneReceptionProduitFiltering("ecart.greaterThanOrEqual=" + DEFAULT_ECART, "ecart.greaterThanOrEqual=" + UPDATED_ECART);
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByEcartIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where ecart is less than or equal to
        defaultLigneReceptionProduitFiltering("ecart.lessThanOrEqual=" + DEFAULT_ECART, "ecart.lessThanOrEqual=" + SMALLER_ECART);
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByEcartIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where ecart is less than
        defaultLigneReceptionProduitFiltering("ecart.lessThan=" + UPDATED_ECART, "ecart.lessThan=" + DEFAULT_ECART);
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByEcartIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where ecart is greater than
        defaultLigneReceptionProduitFiltering("ecart.greaterThan=" + SMALLER_ECART, "ecart.greaterThan=" + DEFAULT_ECART);
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByCodeBarresScanneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where codeBarresScanne equals to
        defaultLigneReceptionProduitFiltering(
            "codeBarresScanne.equals=" + DEFAULT_CODE_BARRES_SCANNE,
            "codeBarresScanne.equals=" + UPDATED_CODE_BARRES_SCANNE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByCodeBarresScanneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where codeBarresScanne in
        defaultLigneReceptionProduitFiltering(
            "codeBarresScanne.in=" + DEFAULT_CODE_BARRES_SCANNE + "," + UPDATED_CODE_BARRES_SCANNE,
            "codeBarresScanne.in=" + UPDATED_CODE_BARRES_SCANNE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByCodeBarresScanneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where codeBarresScanne is not null
        defaultLigneReceptionProduitFiltering("codeBarresScanne.specified=true", "codeBarresScanne.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByCodeBarresScanneContainsSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where codeBarresScanne contains
        defaultLigneReceptionProduitFiltering(
            "codeBarresScanne.contains=" + DEFAULT_CODE_BARRES_SCANNE,
            "codeBarresScanne.contains=" + UPDATED_CODE_BARRES_SCANNE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByCodeBarresScanneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        // Get all the ligneReceptionProduitList where codeBarresScanne does not contain
        defaultLigneReceptionProduitFiltering(
            "codeBarresScanne.doesNotContain=" + UPDATED_CODE_BARRES_SCANNE,
            "codeBarresScanne.doesNotContain=" + DEFAULT_CODE_BARRES_SCANNE
        );
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByReceptionIsEqualToSomething() throws Exception {
        ReceptionProduit reception;
        if (TestUtil.findAll(em, ReceptionProduit.class).isEmpty()) {
            ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);
            reception = ReceptionProduitResourceIT.createEntity(em);
        } else {
            reception = TestUtil.findAll(em, ReceptionProduit.class).get(0);
        }
        em.persist(reception);
        em.flush();
        ligneReceptionProduit.setReception(reception);
        ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);
        Long receptionId = reception.getId();
        // Get all the ligneReceptionProduitList where reception equals to receptionId
        defaultLigneReceptionProduitShouldBeFound("receptionId.equals=" + receptionId);

        // Get all the ligneReceptionProduitList where reception equals to (receptionId + 1)
        defaultLigneReceptionProduitShouldNotBeFound("receptionId.equals=" + (receptionId + 1));
    }

    @Test
    @Transactional
    void getAllLigneReceptionProduitsByProduitIsEqualToSomething() throws Exception {
        Produit produit;
        if (TestUtil.findAll(em, Produit.class).isEmpty()) {
            ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);
            produit = ProduitResourceIT.createEntity(em);
        } else {
            produit = TestUtil.findAll(em, Produit.class).get(0);
        }
        em.persist(produit);
        em.flush();
        ligneReceptionProduit.setProduit(produit);
        ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);
        Long produitId = produit.getId();
        // Get all the ligneReceptionProduitList where produit equals to produitId
        defaultLigneReceptionProduitShouldBeFound("produitId.equals=" + produitId);

        // Get all the ligneReceptionProduitList where produit equals to (produitId + 1)
        defaultLigneReceptionProduitShouldNotBeFound("produitId.equals=" + (produitId + 1));
    }

    private void defaultLigneReceptionProduitFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLigneReceptionProduitShouldBeFound(shouldBeFound);
        defaultLigneReceptionProduitShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLigneReceptionProduitShouldBeFound(String filter) throws Exception {
        restLigneReceptionProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligneReceptionProduit.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantiteAttendue").value(hasItem(sameNumber(DEFAULT_QUANTITE_ATTENDUE))))
            .andExpect(jsonPath("$.[*].quantiteRecue").value(hasItem(sameNumber(DEFAULT_QUANTITE_RECUE))))
            .andExpect(jsonPath("$.[*].ecart").value(hasItem(sameNumber(DEFAULT_ECART))))
            .andExpect(jsonPath("$.[*].codeBarresScanne").value(hasItem(DEFAULT_CODE_BARRES_SCANNE)));

        // Check, that the count call also returns 1
        restLigneReceptionProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLigneReceptionProduitShouldNotBeFound(String filter) throws Exception {
        restLigneReceptionProduitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLigneReceptionProduitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLigneReceptionProduit() throws Exception {
        // Get the ligneReceptionProduit
        restLigneReceptionProduitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLigneReceptionProduit() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneReceptionProduit
        LigneReceptionProduit updatedLigneReceptionProduit = ligneReceptionProduitRepository
            .findById(ligneReceptionProduit.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedLigneReceptionProduit are not directly saved in db
        em.detach(updatedLigneReceptionProduit);
        updatedLigneReceptionProduit
            .quantiteAttendue(UPDATED_QUANTITE_ATTENDUE)
            .quantiteRecue(UPDATED_QUANTITE_RECUE)
            .ecart(UPDATED_ECART)
            .codeBarresScanne(UPDATED_CODE_BARRES_SCANNE);
        LigneReceptionProduitDTO ligneReceptionProduitDTO = ligneReceptionProduitMapper.toDto(updatedLigneReceptionProduit);

        restLigneReceptionProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneReceptionProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneReceptionProduitDTO))
            )
            .andExpect(status().isOk());

        // Validate the LigneReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLigneReceptionProduitToMatchAllProperties(updatedLigneReceptionProduit);
    }

    @Test
    @Transactional
    void putNonExistingLigneReceptionProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneReceptionProduit.setId(longCount.incrementAndGet());

        // Create the LigneReceptionProduit
        LigneReceptionProduitDTO ligneReceptionProduitDTO = ligneReceptionProduitMapper.toDto(ligneReceptionProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneReceptionProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneReceptionProduitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneReceptionProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLigneReceptionProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneReceptionProduit.setId(longCount.incrementAndGet());

        // Create the LigneReceptionProduit
        LigneReceptionProduitDTO ligneReceptionProduitDTO = ligneReceptionProduitMapper.toDto(ligneReceptionProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneReceptionProduitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneReceptionProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLigneReceptionProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneReceptionProduit.setId(longCount.incrementAndGet());

        // Create the LigneReceptionProduit
        LigneReceptionProduitDTO ligneReceptionProduitDTO = ligneReceptionProduitMapper.toDto(ligneReceptionProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneReceptionProduitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneReceptionProduitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLigneReceptionProduitWithPatch() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneReceptionProduit using partial update
        LigneReceptionProduit partialUpdatedLigneReceptionProduit = new LigneReceptionProduit();
        partialUpdatedLigneReceptionProduit.setId(ligneReceptionProduit.getId());

        partialUpdatedLigneReceptionProduit.quantiteRecue(UPDATED_QUANTITE_RECUE).ecart(UPDATED_ECART);

        restLigneReceptionProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneReceptionProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneReceptionProduit))
            )
            .andExpect(status().isOk());

        // Validate the LigneReceptionProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneReceptionProduitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLigneReceptionProduit, ligneReceptionProduit),
            getPersistedLigneReceptionProduit(ligneReceptionProduit)
        );
    }

    @Test
    @Transactional
    void fullUpdateLigneReceptionProduitWithPatch() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneReceptionProduit using partial update
        LigneReceptionProduit partialUpdatedLigneReceptionProduit = new LigneReceptionProduit();
        partialUpdatedLigneReceptionProduit.setId(ligneReceptionProduit.getId());

        partialUpdatedLigneReceptionProduit
            .quantiteAttendue(UPDATED_QUANTITE_ATTENDUE)
            .quantiteRecue(UPDATED_QUANTITE_RECUE)
            .ecart(UPDATED_ECART)
            .codeBarresScanne(UPDATED_CODE_BARRES_SCANNE);

        restLigneReceptionProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneReceptionProduit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneReceptionProduit))
            )
            .andExpect(status().isOk());

        // Validate the LigneReceptionProduit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneReceptionProduitUpdatableFieldsEquals(
            partialUpdatedLigneReceptionProduit,
            getPersistedLigneReceptionProduit(partialUpdatedLigneReceptionProduit)
        );
    }

    @Test
    @Transactional
    void patchNonExistingLigneReceptionProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneReceptionProduit.setId(longCount.incrementAndGet());

        // Create the LigneReceptionProduit
        LigneReceptionProduitDTO ligneReceptionProduitDTO = ligneReceptionProduitMapper.toDto(ligneReceptionProduit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneReceptionProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ligneReceptionProduitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneReceptionProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLigneReceptionProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneReceptionProduit.setId(longCount.incrementAndGet());

        // Create the LigneReceptionProduit
        LigneReceptionProduitDTO ligneReceptionProduitDTO = ligneReceptionProduitMapper.toDto(ligneReceptionProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneReceptionProduitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneReceptionProduitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLigneReceptionProduit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneReceptionProduit.setId(longCount.incrementAndGet());

        // Create the LigneReceptionProduit
        LigneReceptionProduitDTO ligneReceptionProduitDTO = ligneReceptionProduitMapper.toDto(ligneReceptionProduit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneReceptionProduitMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ligneReceptionProduitDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneReceptionProduit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLigneReceptionProduit() throws Exception {
        // Initialize the database
        insertedLigneReceptionProduit = ligneReceptionProduitRepository.saveAndFlush(ligneReceptionProduit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ligneReceptionProduit
        restLigneReceptionProduitMockMvc
            .perform(delete(ENTITY_API_URL_ID, ligneReceptionProduit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ligneReceptionProduitRepository.count();
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

    protected LigneReceptionProduit getPersistedLigneReceptionProduit(LigneReceptionProduit ligneReceptionProduit) {
        return ligneReceptionProduitRepository.findById(ligneReceptionProduit.getId()).orElseThrow();
    }

    protected void assertPersistedLigneReceptionProduitToMatchAllProperties(LigneReceptionProduit expectedLigneReceptionProduit) {
        assertLigneReceptionProduitAllPropertiesEquals(
            expectedLigneReceptionProduit,
            getPersistedLigneReceptionProduit(expectedLigneReceptionProduit)
        );
    }

    protected void assertPersistedLigneReceptionProduitToMatchUpdatableProperties(LigneReceptionProduit expectedLigneReceptionProduit) {
        assertLigneReceptionProduitAllUpdatablePropertiesEquals(
            expectedLigneReceptionProduit,
            getPersistedLigneReceptionProduit(expectedLigneReceptionProduit)
        );
    }
}
