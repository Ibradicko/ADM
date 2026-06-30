package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.LigneCalculRedevanceAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static com.adm.supervision.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.LigneCalculRedevance;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.repository.LigneCalculRedevanceRepository;
import com.adm.supervision.service.LigneCalculRedevanceService;
import com.adm.supervision.service.dto.LigneCalculRedevanceDTO;
import com.adm.supervision.service.mapper.LigneCalculRedevanceMapper;
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
 * Integration tests for the {@link LigneCalculRedevanceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LigneCalculRedevanceResourceIT {

    private static final BigDecimal DEFAULT_BASE_CALCUL = new BigDecimal(0);
    private static final BigDecimal UPDATED_BASE_CALCUL = new BigDecimal(1);
    private static final BigDecimal SMALLER_BASE_CALCUL = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_TAUX_APPLIQUE = new BigDecimal(0);
    private static final BigDecimal UPDATED_TAUX_APPLIQUE = new BigDecimal(1);
    private static final BigDecimal SMALLER_TAUX_APPLIQUE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_MONTANT_REDEVANCE = new BigDecimal(0);
    private static final BigDecimal UPDATED_MONTANT_REDEVANCE = new BigDecimal(1);
    private static final BigDecimal SMALLER_MONTANT_REDEVANCE = new BigDecimal(0 - 1);

    private static final String ENTITY_API_URL = "/api/ligne-calcul-redevances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LigneCalculRedevanceRepository ligneCalculRedevanceRepository;

    @Mock
    private LigneCalculRedevanceRepository ligneCalculRedevanceRepositoryMock;

    @Autowired
    private LigneCalculRedevanceMapper ligneCalculRedevanceMapper;

    @Mock
    private LigneCalculRedevanceService ligneCalculRedevanceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLigneCalculRedevanceMockMvc;

    private LigneCalculRedevance ligneCalculRedevance;

    private LigneCalculRedevance insertedLigneCalculRedevance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneCalculRedevance createEntity(EntityManager em) {
        LigneCalculRedevance ligneCalculRedevance = new LigneCalculRedevance()
            .baseCalcul(DEFAULT_BASE_CALCUL)
            .tauxApplique(DEFAULT_TAUX_APPLIQUE)
            .montantRedevance(DEFAULT_MONTANT_REDEVANCE);
        // Add required entity
        CalculRedevance calculRedevance;
        if (TestUtil.findAll(em, CalculRedevance.class).isEmpty()) {
            calculRedevance = CalculRedevanceResourceIT.createEntity(em);
            em.persist(calculRedevance);
            em.flush();
        } else {
            calculRedevance = TestUtil.findAll(em, CalculRedevance.class).get(0);
        }
        ligneCalculRedevance.setCalcul(calculRedevance);
        return ligneCalculRedevance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LigneCalculRedevance createUpdatedEntity(EntityManager em) {
        LigneCalculRedevance updatedLigneCalculRedevance = new LigneCalculRedevance()
            .baseCalcul(UPDATED_BASE_CALCUL)
            .tauxApplique(UPDATED_TAUX_APPLIQUE)
            .montantRedevance(UPDATED_MONTANT_REDEVANCE);
        // Add required entity
        CalculRedevance calculRedevance;
        if (TestUtil.findAll(em, CalculRedevance.class).isEmpty()) {
            calculRedevance = CalculRedevanceResourceIT.createUpdatedEntity(em);
            em.persist(calculRedevance);
            em.flush();
        } else {
            calculRedevance = TestUtil.findAll(em, CalculRedevance.class).get(0);
        }
        updatedLigneCalculRedevance.setCalcul(calculRedevance);
        return updatedLigneCalculRedevance;
    }

    @BeforeEach
    void initTest() {
        ligneCalculRedevance = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLigneCalculRedevance != null) {
            ligneCalculRedevanceRepository.delete(insertedLigneCalculRedevance);
            insertedLigneCalculRedevance = null;
        }
    }

    @Test
    @Transactional
    void createLigneCalculRedevance() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LigneCalculRedevance
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO = ligneCalculRedevanceMapper.toDto(ligneCalculRedevance);
        var returnedLigneCalculRedevanceDTO = om.readValue(
            restLigneCalculRedevanceMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneCalculRedevanceDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LigneCalculRedevanceDTO.class
        );

        // Validate the LigneCalculRedevance in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLigneCalculRedevance = ligneCalculRedevanceMapper.toEntity(returnedLigneCalculRedevanceDTO);
        assertLigneCalculRedevanceUpdatableFieldsEquals(
            returnedLigneCalculRedevance,
            getPersistedLigneCalculRedevance(returnedLigneCalculRedevance)
        );

        insertedLigneCalculRedevance = returnedLigneCalculRedevance;
    }

    @Test
    @Transactional
    void createLigneCalculRedevanceWithExistingId() throws Exception {
        // Create the LigneCalculRedevance with an existing ID
        ligneCalculRedevance.setId(1L);
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO = ligneCalculRedevanceMapper.toDto(ligneCalculRedevance);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLigneCalculRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneCalculRedevanceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LigneCalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkBaseCalculIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ligneCalculRedevance.setBaseCalcul(null);

        // Create the LigneCalculRedevance, which fails.
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO = ligneCalculRedevanceMapper.toDto(ligneCalculRedevance);

        restLigneCalculRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneCalculRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTauxAppliqueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ligneCalculRedevance.setTauxApplique(null);

        // Create the LigneCalculRedevance, which fails.
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO = ligneCalculRedevanceMapper.toDto(ligneCalculRedevance);

        restLigneCalculRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneCalculRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMontantRedevanceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ligneCalculRedevance.setMontantRedevance(null);

        // Create the LigneCalculRedevance, which fails.
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO = ligneCalculRedevanceMapper.toDto(ligneCalculRedevance);

        restLigneCalculRedevanceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneCalculRedevanceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevances() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList
        restLigneCalculRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligneCalculRedevance.getId().intValue())))
            .andExpect(jsonPath("$.[*].baseCalcul").value(hasItem(sameNumber(DEFAULT_BASE_CALCUL))))
            .andExpect(jsonPath("$.[*].tauxApplique").value(hasItem(sameNumber(DEFAULT_TAUX_APPLIQUE))))
            .andExpect(jsonPath("$.[*].montantRedevance").value(hasItem(sameNumber(DEFAULT_MONTANT_REDEVANCE))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLigneCalculRedevancesWithEagerRelationshipsIsEnabled() throws Exception {
        when(ligneCalculRedevanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLigneCalculRedevanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ligneCalculRedevanceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLigneCalculRedevancesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ligneCalculRedevanceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLigneCalculRedevanceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ligneCalculRedevanceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLigneCalculRedevance() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get the ligneCalculRedevance
        restLigneCalculRedevanceMockMvc
            .perform(get(ENTITY_API_URL_ID, ligneCalculRedevance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ligneCalculRedevance.getId().intValue()))
            .andExpect(jsonPath("$.baseCalcul").value(sameNumber(DEFAULT_BASE_CALCUL)))
            .andExpect(jsonPath("$.tauxApplique").value(sameNumber(DEFAULT_TAUX_APPLIQUE)))
            .andExpect(jsonPath("$.montantRedevance").value(sameNumber(DEFAULT_MONTANT_REDEVANCE)));
    }

    @Test
    @Transactional
    void getLigneCalculRedevancesByIdFiltering() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        Long id = ligneCalculRedevance.getId();

        defaultLigneCalculRedevanceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLigneCalculRedevanceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLigneCalculRedevanceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByBaseCalculIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where baseCalcul equals to
        defaultLigneCalculRedevanceFiltering("baseCalcul.equals=" + DEFAULT_BASE_CALCUL, "baseCalcul.equals=" + UPDATED_BASE_CALCUL);
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByBaseCalculIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where baseCalcul in
        defaultLigneCalculRedevanceFiltering(
            "baseCalcul.in=" + DEFAULT_BASE_CALCUL + "," + UPDATED_BASE_CALCUL,
            "baseCalcul.in=" + UPDATED_BASE_CALCUL
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByBaseCalculIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where baseCalcul is not null
        defaultLigneCalculRedevanceFiltering("baseCalcul.specified=true", "baseCalcul.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByBaseCalculIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where baseCalcul is greater than or equal to
        defaultLigneCalculRedevanceFiltering(
            "baseCalcul.greaterThanOrEqual=" + DEFAULT_BASE_CALCUL,
            "baseCalcul.greaterThanOrEqual=" + UPDATED_BASE_CALCUL
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByBaseCalculIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where baseCalcul is less than or equal to
        defaultLigneCalculRedevanceFiltering(
            "baseCalcul.lessThanOrEqual=" + DEFAULT_BASE_CALCUL,
            "baseCalcul.lessThanOrEqual=" + SMALLER_BASE_CALCUL
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByBaseCalculIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where baseCalcul is less than
        defaultLigneCalculRedevanceFiltering("baseCalcul.lessThan=" + UPDATED_BASE_CALCUL, "baseCalcul.lessThan=" + DEFAULT_BASE_CALCUL);
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByBaseCalculIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where baseCalcul is greater than
        defaultLigneCalculRedevanceFiltering(
            "baseCalcul.greaterThan=" + SMALLER_BASE_CALCUL,
            "baseCalcul.greaterThan=" + DEFAULT_BASE_CALCUL
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByTauxAppliqueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where tauxApplique equals to
        defaultLigneCalculRedevanceFiltering(
            "tauxApplique.equals=" + DEFAULT_TAUX_APPLIQUE,
            "tauxApplique.equals=" + UPDATED_TAUX_APPLIQUE
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByTauxAppliqueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where tauxApplique in
        defaultLigneCalculRedevanceFiltering(
            "tauxApplique.in=" + DEFAULT_TAUX_APPLIQUE + "," + UPDATED_TAUX_APPLIQUE,
            "tauxApplique.in=" + UPDATED_TAUX_APPLIQUE
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByTauxAppliqueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where tauxApplique is not null
        defaultLigneCalculRedevanceFiltering("tauxApplique.specified=true", "tauxApplique.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByTauxAppliqueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where tauxApplique is greater than or equal to
        defaultLigneCalculRedevanceFiltering(
            "tauxApplique.greaterThanOrEqual=" + DEFAULT_TAUX_APPLIQUE,
            "tauxApplique.greaterThanOrEqual=" + (DEFAULT_TAUX_APPLIQUE.add(BigDecimal.ONE))
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByTauxAppliqueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where tauxApplique is less than or equal to
        defaultLigneCalculRedevanceFiltering(
            "tauxApplique.lessThanOrEqual=" + DEFAULT_TAUX_APPLIQUE,
            "tauxApplique.lessThanOrEqual=" + SMALLER_TAUX_APPLIQUE
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByTauxAppliqueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where tauxApplique is less than
        defaultLigneCalculRedevanceFiltering(
            "tauxApplique.lessThan=" + (DEFAULT_TAUX_APPLIQUE.add(BigDecimal.ONE)),
            "tauxApplique.lessThan=" + DEFAULT_TAUX_APPLIQUE
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByTauxAppliqueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where tauxApplique is greater than
        defaultLigneCalculRedevanceFiltering(
            "tauxApplique.greaterThan=" + SMALLER_TAUX_APPLIQUE,
            "tauxApplique.greaterThan=" + DEFAULT_TAUX_APPLIQUE
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByMontantRedevanceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where montantRedevance equals to
        defaultLigneCalculRedevanceFiltering(
            "montantRedevance.equals=" + DEFAULT_MONTANT_REDEVANCE,
            "montantRedevance.equals=" + UPDATED_MONTANT_REDEVANCE
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByMontantRedevanceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where montantRedevance in
        defaultLigneCalculRedevanceFiltering(
            "montantRedevance.in=" + DEFAULT_MONTANT_REDEVANCE + "," + UPDATED_MONTANT_REDEVANCE,
            "montantRedevance.in=" + UPDATED_MONTANT_REDEVANCE
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByMontantRedevanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where montantRedevance is not null
        defaultLigneCalculRedevanceFiltering("montantRedevance.specified=true", "montantRedevance.specified=false");
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByMontantRedevanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where montantRedevance is greater than or equal to
        defaultLigneCalculRedevanceFiltering(
            "montantRedevance.greaterThanOrEqual=" + DEFAULT_MONTANT_REDEVANCE,
            "montantRedevance.greaterThanOrEqual=" + UPDATED_MONTANT_REDEVANCE
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByMontantRedevanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where montantRedevance is less than or equal to
        defaultLigneCalculRedevanceFiltering(
            "montantRedevance.lessThanOrEqual=" + DEFAULT_MONTANT_REDEVANCE,
            "montantRedevance.lessThanOrEqual=" + SMALLER_MONTANT_REDEVANCE
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByMontantRedevanceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where montantRedevance is less than
        defaultLigneCalculRedevanceFiltering(
            "montantRedevance.lessThan=" + UPDATED_MONTANT_REDEVANCE,
            "montantRedevance.lessThan=" + DEFAULT_MONTANT_REDEVANCE
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByMontantRedevanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        // Get all the ligneCalculRedevanceList where montantRedevance is greater than
        defaultLigneCalculRedevanceFiltering(
            "montantRedevance.greaterThan=" + SMALLER_MONTANT_REDEVANCE,
            "montantRedevance.greaterThan=" + DEFAULT_MONTANT_REDEVANCE
        );
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByCalculIsEqualToSomething() throws Exception {
        CalculRedevance calcul;
        if (TestUtil.findAll(em, CalculRedevance.class).isEmpty()) {
            ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);
            calcul = CalculRedevanceResourceIT.createEntity(em);
        } else {
            calcul = TestUtil.findAll(em, CalculRedevance.class).get(0);
        }
        em.persist(calcul);
        em.flush();
        ligneCalculRedevance.setCalcul(calcul);
        ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);
        Long calculId = calcul.getId();
        // Get all the ligneCalculRedevanceList where calcul equals to calculId
        defaultLigneCalculRedevanceShouldBeFound("calculId.equals=" + calculId);

        // Get all the ligneCalculRedevanceList where calcul equals to (calculId + 1)
        defaultLigneCalculRedevanceShouldNotBeFound("calculId.equals=" + (calculId + 1));
    }

    @Test
    @Transactional
    void getAllLigneCalculRedevancesByVenteIsEqualToSomething() throws Exception {
        Vente vente;
        if (TestUtil.findAll(em, Vente.class).isEmpty()) {
            ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);
            vente = VenteResourceIT.createEntity(em);
        } else {
            vente = TestUtil.findAll(em, Vente.class).get(0);
        }
        em.persist(vente);
        em.flush();
        ligneCalculRedevance.setVente(vente);
        ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);
        Long venteId = vente.getId();
        // Get all the ligneCalculRedevanceList where vente equals to venteId
        defaultLigneCalculRedevanceShouldBeFound("venteId.equals=" + venteId);

        // Get all the ligneCalculRedevanceList where vente equals to (venteId + 1)
        defaultLigneCalculRedevanceShouldNotBeFound("venteId.equals=" + (venteId + 1));
    }

    private void defaultLigneCalculRedevanceFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLigneCalculRedevanceShouldBeFound(shouldBeFound);
        defaultLigneCalculRedevanceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLigneCalculRedevanceShouldBeFound(String filter) throws Exception {
        restLigneCalculRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ligneCalculRedevance.getId().intValue())))
            .andExpect(jsonPath("$.[*].baseCalcul").value(hasItem(sameNumber(DEFAULT_BASE_CALCUL))))
            .andExpect(jsonPath("$.[*].tauxApplique").value(hasItem(sameNumber(DEFAULT_TAUX_APPLIQUE))))
            .andExpect(jsonPath("$.[*].montantRedevance").value(hasItem(sameNumber(DEFAULT_MONTANT_REDEVANCE))));

        // Check, that the count call also returns 1
        restLigneCalculRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLigneCalculRedevanceShouldNotBeFound(String filter) throws Exception {
        restLigneCalculRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLigneCalculRedevanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLigneCalculRedevance() throws Exception {
        // Get the ligneCalculRedevance
        restLigneCalculRedevanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLigneCalculRedevance() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneCalculRedevance
        LigneCalculRedevance updatedLigneCalculRedevance = ligneCalculRedevanceRepository
            .findById(ligneCalculRedevance.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedLigneCalculRedevance are not directly saved in db
        em.detach(updatedLigneCalculRedevance);
        updatedLigneCalculRedevance
            .baseCalcul(UPDATED_BASE_CALCUL)
            .tauxApplique(UPDATED_TAUX_APPLIQUE)
            .montantRedevance(UPDATED_MONTANT_REDEVANCE);
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO = ligneCalculRedevanceMapper.toDto(updatedLigneCalculRedevance);

        restLigneCalculRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneCalculRedevanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneCalculRedevanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the LigneCalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLigneCalculRedevanceToMatchAllProperties(updatedLigneCalculRedevance);
    }

    @Test
    @Transactional
    void putNonExistingLigneCalculRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneCalculRedevance.setId(longCount.incrementAndGet());

        // Create the LigneCalculRedevance
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO = ligneCalculRedevanceMapper.toDto(ligneCalculRedevance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneCalculRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ligneCalculRedevanceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneCalculRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneCalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLigneCalculRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneCalculRedevance.setId(longCount.incrementAndGet());

        // Create the LigneCalculRedevance
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO = ligneCalculRedevanceMapper.toDto(ligneCalculRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneCalculRedevanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ligneCalculRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneCalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLigneCalculRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneCalculRedevance.setId(longCount.incrementAndGet());

        // Create the LigneCalculRedevance
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO = ligneCalculRedevanceMapper.toDto(ligneCalculRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneCalculRedevanceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ligneCalculRedevanceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneCalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLigneCalculRedevanceWithPatch() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneCalculRedevance using partial update
        LigneCalculRedevance partialUpdatedLigneCalculRedevance = new LigneCalculRedevance();
        partialUpdatedLigneCalculRedevance.setId(ligneCalculRedevance.getId());

        partialUpdatedLigneCalculRedevance
            .baseCalcul(UPDATED_BASE_CALCUL)
            .tauxApplique(UPDATED_TAUX_APPLIQUE)
            .montantRedevance(UPDATED_MONTANT_REDEVANCE);

        restLigneCalculRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneCalculRedevance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneCalculRedevance))
            )
            .andExpect(status().isOk());

        // Validate the LigneCalculRedevance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneCalculRedevanceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLigneCalculRedevance, ligneCalculRedevance),
            getPersistedLigneCalculRedevance(ligneCalculRedevance)
        );
    }

    @Test
    @Transactional
    void fullUpdateLigneCalculRedevanceWithPatch() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ligneCalculRedevance using partial update
        LigneCalculRedevance partialUpdatedLigneCalculRedevance = new LigneCalculRedevance();
        partialUpdatedLigneCalculRedevance.setId(ligneCalculRedevance.getId());

        partialUpdatedLigneCalculRedevance
            .baseCalcul(UPDATED_BASE_CALCUL)
            .tauxApplique(UPDATED_TAUX_APPLIQUE)
            .montantRedevance(UPDATED_MONTANT_REDEVANCE);

        restLigneCalculRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLigneCalculRedevance.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLigneCalculRedevance))
            )
            .andExpect(status().isOk());

        // Validate the LigneCalculRedevance in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLigneCalculRedevanceUpdatableFieldsEquals(
            partialUpdatedLigneCalculRedevance,
            getPersistedLigneCalculRedevance(partialUpdatedLigneCalculRedevance)
        );
    }

    @Test
    @Transactional
    void patchNonExistingLigneCalculRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneCalculRedevance.setId(longCount.incrementAndGet());

        // Create the LigneCalculRedevance
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO = ligneCalculRedevanceMapper.toDto(ligneCalculRedevance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLigneCalculRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ligneCalculRedevanceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneCalculRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneCalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLigneCalculRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneCalculRedevance.setId(longCount.incrementAndGet());

        // Create the LigneCalculRedevance
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO = ligneCalculRedevanceMapper.toDto(ligneCalculRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneCalculRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ligneCalculRedevanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the LigneCalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLigneCalculRedevance() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ligneCalculRedevance.setId(longCount.incrementAndGet());

        // Create the LigneCalculRedevance
        LigneCalculRedevanceDTO ligneCalculRedevanceDTO = ligneCalculRedevanceMapper.toDto(ligneCalculRedevance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLigneCalculRedevanceMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ligneCalculRedevanceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the LigneCalculRedevance in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLigneCalculRedevance() throws Exception {
        // Initialize the database
        insertedLigneCalculRedevance = ligneCalculRedevanceRepository.saveAndFlush(ligneCalculRedevance);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ligneCalculRedevance
        restLigneCalculRedevanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, ligneCalculRedevance.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ligneCalculRedevanceRepository.count();
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

    protected LigneCalculRedevance getPersistedLigneCalculRedevance(LigneCalculRedevance ligneCalculRedevance) {
        return ligneCalculRedevanceRepository.findById(ligneCalculRedevance.getId()).orElseThrow();
    }

    protected void assertPersistedLigneCalculRedevanceToMatchAllProperties(LigneCalculRedevance expectedLigneCalculRedevance) {
        assertLigneCalculRedevanceAllPropertiesEquals(
            expectedLigneCalculRedevance,
            getPersistedLigneCalculRedevance(expectedLigneCalculRedevance)
        );
    }

    protected void assertPersistedLigneCalculRedevanceToMatchUpdatableProperties(LigneCalculRedevance expectedLigneCalculRedevance) {
        assertLigneCalculRedevanceAllUpdatablePropertiesEquals(
            expectedLigneCalculRedevance,
            getPersistedLigneCalculRedevance(expectedLigneCalculRedevance)
        );
    }
}
