package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.TicketCaisseAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.TicketCaisse;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.repository.TicketCaisseRepository;
import com.adm.supervision.service.TicketCaisseService;
import com.adm.supervision.service.dto.TicketCaisseDTO;
import com.adm.supervision.service.mapper.TicketCaisseMapper;
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
 * Integration tests for the {@link TicketCaisseResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TicketCaisseResourceIT {

    private static final String DEFAULT_NUMERO = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_EMISSION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_EMISSION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_NOMBRE_IMPRESSIONS = 1;
    private static final Integer UPDATED_NOMBRE_IMPRESSIONS = 2;
    private static final Integer SMALLER_NOMBRE_IMPRESSIONS = 1 - 1;

    private static final String DEFAULT_CONTENU = "AAAAAAAAAA";
    private static final String UPDATED_CONTENU = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/ticket-caisses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TicketCaisseRepository ticketCaisseRepository;

    @Mock
    private TicketCaisseRepository ticketCaisseRepositoryMock;

    @Autowired
    private TicketCaisseMapper ticketCaisseMapper;

    @Mock
    private TicketCaisseService ticketCaisseServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTicketCaisseMockMvc;

    private TicketCaisse ticketCaisse;

    private TicketCaisse insertedTicketCaisse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketCaisse createEntity(EntityManager em) {
        TicketCaisse ticketCaisse = new TicketCaisse()
            .numero(DEFAULT_NUMERO)
            .dateEmission(DEFAULT_DATE_EMISSION)
            .nombreImpressions(DEFAULT_NOMBRE_IMPRESSIONS)
            .contenu(DEFAULT_CONTENU);
        // Add required entity
        Vente vente;
        if (TestUtil.findAll(em, Vente.class).isEmpty()) {
            vente = VenteResourceIT.createEntity(em);
            em.persist(vente);
            em.flush();
        } else {
            vente = TestUtil.findAll(em, Vente.class).get(0);
        }
        ticketCaisse.setVente(vente);
        return ticketCaisse;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TicketCaisse createUpdatedEntity(EntityManager em) {
        TicketCaisse updatedTicketCaisse = new TicketCaisse()
            .numero(UPDATED_NUMERO)
            .dateEmission(UPDATED_DATE_EMISSION)
            .nombreImpressions(UPDATED_NOMBRE_IMPRESSIONS)
            .contenu(UPDATED_CONTENU);
        // Add required entity
        Vente vente;
        if (TestUtil.findAll(em, Vente.class).isEmpty()) {
            vente = VenteResourceIT.createUpdatedEntity(em);
            em.persist(vente);
            em.flush();
        } else {
            vente = TestUtil.findAll(em, Vente.class).get(0);
        }
        updatedTicketCaisse.setVente(vente);
        return updatedTicketCaisse;
    }

    @BeforeEach
    void initTest() {
        ticketCaisse = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTicketCaisse != null) {
            ticketCaisseRepository.delete(insertedTicketCaisse);
            insertedTicketCaisse = null;
        }
    }

    @Test
    @Transactional
    void createTicketCaisse() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TicketCaisse
        TicketCaisseDTO ticketCaisseDTO = ticketCaisseMapper.toDto(ticketCaisse);
        var returnedTicketCaisseDTO = om.readValue(
            restTicketCaisseMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCaisseDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TicketCaisseDTO.class
        );

        // Validate the TicketCaisse in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTicketCaisse = ticketCaisseMapper.toEntity(returnedTicketCaisseDTO);
        assertTicketCaisseUpdatableFieldsEquals(returnedTicketCaisse, getPersistedTicketCaisse(returnedTicketCaisse));

        insertedTicketCaisse = returnedTicketCaisse;
    }

    @Test
    @Transactional
    void createTicketCaisseWithExistingId() throws Exception {
        // Create the TicketCaisse with an existing ID
        ticketCaisse.setId(1L);
        TicketCaisseDTO ticketCaisseDTO = ticketCaisseMapper.toDto(ticketCaisse);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTicketCaisseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCaisseDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TicketCaisse in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNumeroIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticketCaisse.setNumero(null);

        // Create the TicketCaisse, which fails.
        TicketCaisseDTO ticketCaisseDTO = ticketCaisseMapper.toDto(ticketCaisse);

        restTicketCaisseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCaisseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateEmissionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticketCaisse.setDateEmission(null);

        // Create the TicketCaisse, which fails.
        TicketCaisseDTO ticketCaisseDTO = ticketCaisseMapper.toDto(ticketCaisse);

        restTicketCaisseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCaisseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNombreImpressionsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ticketCaisse.setNombreImpressions(null);

        // Create the TicketCaisse, which fails.
        TicketCaisseDTO ticketCaisseDTO = ticketCaisseMapper.toDto(ticketCaisse);

        restTicketCaisseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCaisseDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTicketCaisses() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList
        restTicketCaisseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketCaisse.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].dateEmission").value(hasItem(DEFAULT_DATE_EMISSION.toString())))
            .andExpect(jsonPath("$.[*].nombreImpressions").value(hasItem(DEFAULT_NOMBRE_IMPRESSIONS)))
            .andExpect(jsonPath("$.[*].contenu").value(hasItem(DEFAULT_CONTENU)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTicketCaissesWithEagerRelationshipsIsEnabled() throws Exception {
        when(ticketCaisseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTicketCaisseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(ticketCaisseServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTicketCaissesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(ticketCaisseServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTicketCaisseMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(ticketCaisseRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTicketCaisse() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get the ticketCaisse
        restTicketCaisseMockMvc
            .perform(get(ENTITY_API_URL_ID, ticketCaisse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ticketCaisse.getId().intValue()))
            .andExpect(jsonPath("$.numero").value(DEFAULT_NUMERO))
            .andExpect(jsonPath("$.dateEmission").value(DEFAULT_DATE_EMISSION.toString()))
            .andExpect(jsonPath("$.nombreImpressions").value(DEFAULT_NOMBRE_IMPRESSIONS))
            .andExpect(jsonPath("$.contenu").value(DEFAULT_CONTENU));
    }

    @Test
    @Transactional
    void getTicketCaissesByIdFiltering() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        Long id = ticketCaisse.getId();

        defaultTicketCaisseFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTicketCaisseFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTicketCaisseFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTicketCaissesByNumeroIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where numero equals to
        defaultTicketCaisseFiltering("numero.equals=" + DEFAULT_NUMERO, "numero.equals=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllTicketCaissesByNumeroIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where numero in
        defaultTicketCaisseFiltering("numero.in=" + DEFAULT_NUMERO + "," + UPDATED_NUMERO, "numero.in=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllTicketCaissesByNumeroIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where numero is not null
        defaultTicketCaisseFiltering("numero.specified=true", "numero.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketCaissesByNumeroContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where numero contains
        defaultTicketCaisseFiltering("numero.contains=" + DEFAULT_NUMERO, "numero.contains=" + UPDATED_NUMERO);
    }

    @Test
    @Transactional
    void getAllTicketCaissesByNumeroNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where numero does not contain
        defaultTicketCaisseFiltering("numero.doesNotContain=" + UPDATED_NUMERO, "numero.doesNotContain=" + DEFAULT_NUMERO);
    }

    @Test
    @Transactional
    void getAllTicketCaissesByDateEmissionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where dateEmission equals to
        defaultTicketCaisseFiltering("dateEmission.equals=" + DEFAULT_DATE_EMISSION, "dateEmission.equals=" + UPDATED_DATE_EMISSION);
    }

    @Test
    @Transactional
    void getAllTicketCaissesByDateEmissionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where dateEmission in
        defaultTicketCaisseFiltering(
            "dateEmission.in=" + DEFAULT_DATE_EMISSION + "," + UPDATED_DATE_EMISSION,
            "dateEmission.in=" + UPDATED_DATE_EMISSION
        );
    }

    @Test
    @Transactional
    void getAllTicketCaissesByDateEmissionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where dateEmission is not null
        defaultTicketCaisseFiltering("dateEmission.specified=true", "dateEmission.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketCaissesByNombreImpressionsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where nombreImpressions equals to
        defaultTicketCaisseFiltering(
            "nombreImpressions.equals=" + DEFAULT_NOMBRE_IMPRESSIONS,
            "nombreImpressions.equals=" + UPDATED_NOMBRE_IMPRESSIONS
        );
    }

    @Test
    @Transactional
    void getAllTicketCaissesByNombreImpressionsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where nombreImpressions in
        defaultTicketCaisseFiltering(
            "nombreImpressions.in=" + DEFAULT_NOMBRE_IMPRESSIONS + "," + UPDATED_NOMBRE_IMPRESSIONS,
            "nombreImpressions.in=" + UPDATED_NOMBRE_IMPRESSIONS
        );
    }

    @Test
    @Transactional
    void getAllTicketCaissesByNombreImpressionsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where nombreImpressions is not null
        defaultTicketCaisseFiltering("nombreImpressions.specified=true", "nombreImpressions.specified=false");
    }

    @Test
    @Transactional
    void getAllTicketCaissesByNombreImpressionsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where nombreImpressions is greater than or equal to
        defaultTicketCaisseFiltering(
            "nombreImpressions.greaterThanOrEqual=" + DEFAULT_NOMBRE_IMPRESSIONS,
            "nombreImpressions.greaterThanOrEqual=" + UPDATED_NOMBRE_IMPRESSIONS
        );
    }

    @Test
    @Transactional
    void getAllTicketCaissesByNombreImpressionsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where nombreImpressions is less than or equal to
        defaultTicketCaisseFiltering(
            "nombreImpressions.lessThanOrEqual=" + DEFAULT_NOMBRE_IMPRESSIONS,
            "nombreImpressions.lessThanOrEqual=" + SMALLER_NOMBRE_IMPRESSIONS
        );
    }

    @Test
    @Transactional
    void getAllTicketCaissesByNombreImpressionsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where nombreImpressions is less than
        defaultTicketCaisseFiltering(
            "nombreImpressions.lessThan=" + UPDATED_NOMBRE_IMPRESSIONS,
            "nombreImpressions.lessThan=" + DEFAULT_NOMBRE_IMPRESSIONS
        );
    }

    @Test
    @Transactional
    void getAllTicketCaissesByNombreImpressionsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        // Get all the ticketCaisseList where nombreImpressions is greater than
        defaultTicketCaisseFiltering(
            "nombreImpressions.greaterThan=" + SMALLER_NOMBRE_IMPRESSIONS,
            "nombreImpressions.greaterThan=" + DEFAULT_NOMBRE_IMPRESSIONS
        );
    }

    @Test
    @Transactional
    void getAllTicketCaissesByVenteIsEqualToSomething() throws Exception {
        Vente vente;
        if (TestUtil.findAll(em, Vente.class).isEmpty()) {
            ticketCaisseRepository.saveAndFlush(ticketCaisse);
            vente = VenteResourceIT.createEntity(em);
        } else {
            vente = TestUtil.findAll(em, Vente.class).get(0);
        }
        em.persist(vente);
        em.flush();
        ticketCaisse.setVente(vente);
        ticketCaisseRepository.saveAndFlush(ticketCaisse);
        Long venteId = vente.getId();
        // Get all the ticketCaisseList where vente equals to venteId
        defaultTicketCaisseShouldBeFound("venteId.equals=" + venteId);

        // Get all the ticketCaisseList where vente equals to (venteId + 1)
        defaultTicketCaisseShouldNotBeFound("venteId.equals=" + (venteId + 1));
    }

    private void defaultTicketCaisseFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTicketCaisseShouldBeFound(shouldBeFound);
        defaultTicketCaisseShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTicketCaisseShouldBeFound(String filter) throws Exception {
        restTicketCaisseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ticketCaisse.getId().intValue())))
            .andExpect(jsonPath("$.[*].numero").value(hasItem(DEFAULT_NUMERO)))
            .andExpect(jsonPath("$.[*].dateEmission").value(hasItem(DEFAULT_DATE_EMISSION.toString())))
            .andExpect(jsonPath("$.[*].nombreImpressions").value(hasItem(DEFAULT_NOMBRE_IMPRESSIONS)))
            .andExpect(jsonPath("$.[*].contenu").value(hasItem(DEFAULT_CONTENU)));

        // Check, that the count call also returns 1
        restTicketCaisseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTicketCaisseShouldNotBeFound(String filter) throws Exception {
        restTicketCaisseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTicketCaisseMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTicketCaisse() throws Exception {
        // Get the ticketCaisse
        restTicketCaisseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTicketCaisse() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketCaisse
        TicketCaisse updatedTicketCaisse = ticketCaisseRepository.findById(ticketCaisse.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTicketCaisse are not directly saved in db
        em.detach(updatedTicketCaisse);
        updatedTicketCaisse
            .numero(UPDATED_NUMERO)
            .dateEmission(UPDATED_DATE_EMISSION)
            .nombreImpressions(UPDATED_NOMBRE_IMPRESSIONS)
            .contenu(UPDATED_CONTENU);
        TicketCaisseDTO ticketCaisseDTO = ticketCaisseMapper.toDto(updatedTicketCaisse);

        restTicketCaisseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketCaisseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketCaisseDTO))
            )
            .andExpect(status().isOk());

        // Validate the TicketCaisse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTicketCaisseToMatchAllProperties(updatedTicketCaisse);
    }

    @Test
    @Transactional
    void putNonExistingTicketCaisse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketCaisse.setId(longCount.incrementAndGet());

        // Create the TicketCaisse
        TicketCaisseDTO ticketCaisseDTO = ticketCaisseMapper.toDto(ticketCaisse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketCaisseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ticketCaisseDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketCaisseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketCaisse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTicketCaisse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketCaisse.setId(longCount.incrementAndGet());

        // Create the TicketCaisse
        TicketCaisseDTO ticketCaisseDTO = ticketCaisseMapper.toDto(ticketCaisse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketCaisseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ticketCaisseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketCaisse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTicketCaisse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketCaisse.setId(longCount.incrementAndGet());

        // Create the TicketCaisse
        TicketCaisseDTO ticketCaisseDTO = ticketCaisseMapper.toDto(ticketCaisse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketCaisseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ticketCaisseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketCaisse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTicketCaisseWithPatch() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketCaisse using partial update
        TicketCaisse partialUpdatedTicketCaisse = new TicketCaisse();
        partialUpdatedTicketCaisse.setId(ticketCaisse.getId());

        partialUpdatedTicketCaisse.numero(UPDATED_NUMERO).dateEmission(UPDATED_DATE_EMISSION);

        restTicketCaisseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketCaisse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketCaisse))
            )
            .andExpect(status().isOk());

        // Validate the TicketCaisse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketCaisseUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTicketCaisse, ticketCaisse),
            getPersistedTicketCaisse(ticketCaisse)
        );
    }

    @Test
    @Transactional
    void fullUpdateTicketCaisseWithPatch() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ticketCaisse using partial update
        TicketCaisse partialUpdatedTicketCaisse = new TicketCaisse();
        partialUpdatedTicketCaisse.setId(ticketCaisse.getId());

        partialUpdatedTicketCaisse
            .numero(UPDATED_NUMERO)
            .dateEmission(UPDATED_DATE_EMISSION)
            .nombreImpressions(UPDATED_NOMBRE_IMPRESSIONS)
            .contenu(UPDATED_CONTENU);

        restTicketCaisseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTicketCaisse.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTicketCaisse))
            )
            .andExpect(status().isOk());

        // Validate the TicketCaisse in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTicketCaisseUpdatableFieldsEquals(partialUpdatedTicketCaisse, getPersistedTicketCaisse(partialUpdatedTicketCaisse));
    }

    @Test
    @Transactional
    void patchNonExistingTicketCaisse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketCaisse.setId(longCount.incrementAndGet());

        // Create the TicketCaisse
        TicketCaisseDTO ticketCaisseDTO = ticketCaisseMapper.toDto(ticketCaisse);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTicketCaisseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ticketCaisseDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketCaisseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketCaisse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTicketCaisse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketCaisse.setId(longCount.incrementAndGet());

        // Create the TicketCaisse
        TicketCaisseDTO ticketCaisseDTO = ticketCaisseMapper.toDto(ticketCaisse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketCaisseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ticketCaisseDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TicketCaisse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTicketCaisse() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ticketCaisse.setId(longCount.incrementAndGet());

        // Create the TicketCaisse
        TicketCaisseDTO ticketCaisseDTO = ticketCaisseMapper.toDto(ticketCaisse);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTicketCaisseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ticketCaisseDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TicketCaisse in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTicketCaisse() throws Exception {
        // Initialize the database
        insertedTicketCaisse = ticketCaisseRepository.saveAndFlush(ticketCaisse);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ticketCaisse
        restTicketCaisseMockMvc
            .perform(delete(ENTITY_API_URL_ID, ticketCaisse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ticketCaisseRepository.count();
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

    protected TicketCaisse getPersistedTicketCaisse(TicketCaisse ticketCaisse) {
        return ticketCaisseRepository.findById(ticketCaisse.getId()).orElseThrow();
    }

    protected void assertPersistedTicketCaisseToMatchAllProperties(TicketCaisse expectedTicketCaisse) {
        assertTicketCaisseAllPropertiesEquals(expectedTicketCaisse, getPersistedTicketCaisse(expectedTicketCaisse));
    }

    protected void assertPersistedTicketCaisseToMatchUpdatableProperties(TicketCaisse expectedTicketCaisse) {
        assertTicketCaisseAllUpdatablePropertiesEquals(expectedTicketCaisse, getPersistedTicketCaisse(expectedTicketCaisse));
    }
}
