package com.adm.supervision.web.rest;

import static com.adm.supervision.domain.ProfilMetierAsserts.*;
import static com.adm.supervision.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.adm.supervision.IntegrationTest;
import com.adm.supervision.domain.PermissionMetier;
import com.adm.supervision.domain.ProfilMetier;
import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.repository.ProfilMetierRepository;
import com.adm.supervision.service.ProfilMetierService;
import com.adm.supervision.service.dto.ProfilMetierDTO;
import com.adm.supervision.service.mapper.ProfilMetierMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link ProfilMetierResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProfilMetierResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final StatutGeneral DEFAULT_STATUT = StatutGeneral.ACTIF;
    private static final StatutGeneral UPDATED_STATUT = StatutGeneral.INACTIF;

    private static final String ENTITY_API_URL = "/api/profil-metiers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProfilMetierRepository profilMetierRepository;

    @Mock
    private ProfilMetierRepository profilMetierRepositoryMock;

    @Autowired
    private ProfilMetierMapper profilMetierMapper;

    @Mock
    private ProfilMetierService profilMetierServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProfilMetierMockMvc;

    private ProfilMetier profilMetier;

    private ProfilMetier insertedProfilMetier;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfilMetier createEntity() {
        return new ProfilMetier().code(DEFAULT_CODE).libelle(DEFAULT_LIBELLE).description(DEFAULT_DESCRIPTION).statut(DEFAULT_STATUT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfilMetier createUpdatedEntity() {
        return new ProfilMetier().code(UPDATED_CODE).libelle(UPDATED_LIBELLE).description(UPDATED_DESCRIPTION).statut(UPDATED_STATUT);
    }

    @BeforeEach
    void initTest() {
        profilMetier = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProfilMetier != null) {
            profilMetierRepository.delete(insertedProfilMetier);
            insertedProfilMetier = null;
        }
    }

    @Test
    @Transactional
    void createProfilMetier() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProfilMetier
        ProfilMetierDTO profilMetierDTO = profilMetierMapper.toDto(profilMetier);
        var returnedProfilMetierDTO = om.readValue(
            restProfilMetierMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profilMetierDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProfilMetierDTO.class
        );

        // Validate the ProfilMetier in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProfilMetier = profilMetierMapper.toEntity(returnedProfilMetierDTO);
        assertProfilMetierUpdatableFieldsEquals(returnedProfilMetier, getPersistedProfilMetier(returnedProfilMetier));

        insertedProfilMetier = returnedProfilMetier;
    }

    @Test
    @Transactional
    void createProfilMetierWithExistingId() throws Exception {
        // Create the ProfilMetier with an existing ID
        profilMetier.setId(1L);
        ProfilMetierDTO profilMetierDTO = profilMetierMapper.toDto(profilMetier);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfilMetierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profilMetierDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProfilMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        profilMetier.setCode(null);

        // Create the ProfilMetier, which fails.
        ProfilMetierDTO profilMetierDTO = profilMetierMapper.toDto(profilMetier);

        restProfilMetierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profilMetierDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        profilMetier.setLibelle(null);

        // Create the ProfilMetier, which fails.
        ProfilMetierDTO profilMetierDTO = profilMetierMapper.toDto(profilMetier);

        restProfilMetierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profilMetierDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        profilMetier.setStatut(null);

        // Create the ProfilMetier, which fails.
        ProfilMetierDTO profilMetierDTO = profilMetierMapper.toDto(profilMetier);

        restProfilMetierMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profilMetierDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProfilMetiers() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList
        restProfilMetierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profilMetier.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProfilMetiersWithEagerRelationshipsIsEnabled() throws Exception {
        when(profilMetierServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProfilMetierMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(profilMetierServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProfilMetiersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(profilMetierServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProfilMetierMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(profilMetierRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProfilMetier() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get the profilMetier
        restProfilMetierMockMvc
            .perform(get(ENTITY_API_URL_ID, profilMetier.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(profilMetier.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()));
    }

    @Test
    @Transactional
    void getProfilMetiersByIdFiltering() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        Long id = profilMetier.getId();

        defaultProfilMetierFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProfilMetierFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProfilMetierFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProfilMetiersByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList where code equals to
        defaultProfilMetierFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllProfilMetiersByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList where code in
        defaultProfilMetierFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllProfilMetiersByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList where code is not null
        defaultProfilMetierFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilMetiersByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList where code contains
        defaultProfilMetierFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllProfilMetiersByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList where code does not contain
        defaultProfilMetierFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllProfilMetiersByLibelleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList where libelle equals to
        defaultProfilMetierFiltering("libelle.equals=" + DEFAULT_LIBELLE, "libelle.equals=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllProfilMetiersByLibelleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList where libelle in
        defaultProfilMetierFiltering("libelle.in=" + DEFAULT_LIBELLE + "," + UPDATED_LIBELLE, "libelle.in=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllProfilMetiersByLibelleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList where libelle is not null
        defaultProfilMetierFiltering("libelle.specified=true", "libelle.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilMetiersByLibelleContainsSomething() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList where libelle contains
        defaultProfilMetierFiltering("libelle.contains=" + DEFAULT_LIBELLE, "libelle.contains=" + UPDATED_LIBELLE);
    }

    @Test
    @Transactional
    void getAllProfilMetiersByLibelleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList where libelle does not contain
        defaultProfilMetierFiltering("libelle.doesNotContain=" + UPDATED_LIBELLE, "libelle.doesNotContain=" + DEFAULT_LIBELLE);
    }

    @Test
    @Transactional
    void getAllProfilMetiersByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList where statut equals to
        defaultProfilMetierFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllProfilMetiersByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList where statut in
        defaultProfilMetierFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllProfilMetiersByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        // Get all the profilMetierList where statut is not null
        defaultProfilMetierFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllProfilMetiersByPermissionsIsEqualToSomething() throws Exception {
        PermissionMetier permissions;
        if (TestUtil.findAll(em, PermissionMetier.class).isEmpty()) {
            profilMetierRepository.saveAndFlush(profilMetier);
            permissions = PermissionMetierResourceIT.createEntity();
        } else {
            permissions = TestUtil.findAll(em, PermissionMetier.class).get(0);
        }
        em.persist(permissions);
        em.flush();
        profilMetier.addPermissions(permissions);
        profilMetierRepository.saveAndFlush(profilMetier);
        Long permissionsId = permissions.getId();
        // Get all the profilMetierList where permissions equals to permissionsId
        defaultProfilMetierShouldBeFound("permissionsId.equals=" + permissionsId);

        // Get all the profilMetierList where permissions equals to (permissionsId + 1)
        defaultProfilMetierShouldNotBeFound("permissionsId.equals=" + (permissionsId + 1));
    }

    private void defaultProfilMetierFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProfilMetierShouldBeFound(shouldBeFound);
        defaultProfilMetierShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProfilMetierShouldBeFound(String filter) throws Exception {
        restProfilMetierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profilMetier.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())));

        // Check, that the count call also returns 1
        restProfilMetierMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProfilMetierShouldNotBeFound(String filter) throws Exception {
        restProfilMetierMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProfilMetierMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProfilMetier() throws Exception {
        // Get the profilMetier
        restProfilMetierMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProfilMetier() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profilMetier
        ProfilMetier updatedProfilMetier = profilMetierRepository.findById(profilMetier.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProfilMetier are not directly saved in db
        em.detach(updatedProfilMetier);
        updatedProfilMetier.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).description(UPDATED_DESCRIPTION).statut(UPDATED_STATUT);
        ProfilMetierDTO profilMetierDTO = profilMetierMapper.toDto(updatedProfilMetier);

        restProfilMetierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profilMetierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profilMetierDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProfilMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProfilMetierToMatchAllProperties(updatedProfilMetier);
    }

    @Test
    @Transactional
    void putNonExistingProfilMetier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profilMetier.setId(longCount.incrementAndGet());

        // Create the ProfilMetier
        ProfilMetierDTO profilMetierDTO = profilMetierMapper.toDto(profilMetier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfilMetierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profilMetierDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profilMetierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfilMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProfilMetier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profilMetier.setId(longCount.incrementAndGet());

        // Create the ProfilMetier
        ProfilMetierDTO profilMetierDTO = profilMetierMapper.toDto(profilMetier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfilMetierMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profilMetierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfilMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProfilMetier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profilMetier.setId(longCount.incrementAndGet());

        // Create the ProfilMetier
        ProfilMetierDTO profilMetierDTO = profilMetierMapper.toDto(profilMetier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfilMetierMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profilMetierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfilMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProfilMetierWithPatch() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profilMetier using partial update
        ProfilMetier partialUpdatedProfilMetier = new ProfilMetier();
        partialUpdatedProfilMetier.setId(profilMetier.getId());

        partialUpdatedProfilMetier.code(UPDATED_CODE).description(UPDATED_DESCRIPTION).statut(UPDATED_STATUT);

        restProfilMetierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfilMetier.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfilMetier))
            )
            .andExpect(status().isOk());

        // Validate the ProfilMetier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfilMetierUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProfilMetier, profilMetier),
            getPersistedProfilMetier(profilMetier)
        );
    }

    @Test
    @Transactional
    void fullUpdateProfilMetierWithPatch() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profilMetier using partial update
        ProfilMetier partialUpdatedProfilMetier = new ProfilMetier();
        partialUpdatedProfilMetier.setId(profilMetier.getId());

        partialUpdatedProfilMetier.code(UPDATED_CODE).libelle(UPDATED_LIBELLE).description(UPDATED_DESCRIPTION).statut(UPDATED_STATUT);

        restProfilMetierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfilMetier.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfilMetier))
            )
            .andExpect(status().isOk());

        // Validate the ProfilMetier in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfilMetierUpdatableFieldsEquals(partialUpdatedProfilMetier, getPersistedProfilMetier(partialUpdatedProfilMetier));
    }

    @Test
    @Transactional
    void patchNonExistingProfilMetier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profilMetier.setId(longCount.incrementAndGet());

        // Create the ProfilMetier
        ProfilMetierDTO profilMetierDTO = profilMetierMapper.toDto(profilMetier);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfilMetierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, profilMetierDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profilMetierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfilMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProfilMetier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profilMetier.setId(longCount.incrementAndGet());

        // Create the ProfilMetier
        ProfilMetierDTO profilMetierDTO = profilMetierMapper.toDto(profilMetier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfilMetierMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profilMetierDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfilMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProfilMetier() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profilMetier.setId(longCount.incrementAndGet());

        // Create the ProfilMetier
        ProfilMetierDTO profilMetierDTO = profilMetierMapper.toDto(profilMetier);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfilMetierMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(profilMetierDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfilMetier in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProfilMetier() throws Exception {
        // Initialize the database
        insertedProfilMetier = profilMetierRepository.saveAndFlush(profilMetier);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the profilMetier
        restProfilMetierMockMvc
            .perform(delete(ENTITY_API_URL_ID, profilMetier.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return profilMetierRepository.count();
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

    protected ProfilMetier getPersistedProfilMetier(ProfilMetier profilMetier) {
        return profilMetierRepository.findById(profilMetier.getId()).orElseThrow();
    }

    protected void assertPersistedProfilMetierToMatchAllProperties(ProfilMetier expectedProfilMetier) {
        assertProfilMetierAllPropertiesEquals(expectedProfilMetier, getPersistedProfilMetier(expectedProfilMetier));
    }

    protected void assertPersistedProfilMetierToMatchUpdatableProperties(ProfilMetier expectedProfilMetier) {
        assertProfilMetierAllUpdatablePropertiesEquals(expectedProfilMetier, getPersistedProfilMetier(expectedProfilMetier));
    }
}
