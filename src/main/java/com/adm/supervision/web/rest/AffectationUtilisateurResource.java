package com.adm.supervision.web.rest;

import com.adm.supervision.repository.AffectationUtilisateurRepository;
import com.adm.supervision.security.BusinessAuthorizationService;
import com.adm.supervision.service.AffectationUtilisateurQueryService;
import com.adm.supervision.service.AffectationUtilisateurService;
import com.adm.supervision.service.criteria.AffectationUtilisateurCriteria;
import com.adm.supervision.service.dto.AffectationUtilisateurDTO;
import com.adm.supervision.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adm.supervision.domain.AffectationUtilisateur}.
 */
@RestController
@RequestMapping("/api/affectation-utilisateurs")
public class AffectationUtilisateurResource {

    private static final Logger LOG = LoggerFactory.getLogger(AffectationUtilisateurResource.class);

    private static final String ENTITY_NAME = "affectationUtilisateur";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final AffectationUtilisateurService affectationUtilisateurService;

    private final AffectationUtilisateurRepository affectationUtilisateurRepository;

    private final AffectationUtilisateurQueryService affectationUtilisateurQueryService;

    private final BusinessAuthorizationService businessAuthorizationService;

    public AffectationUtilisateurResource(
        AffectationUtilisateurService affectationUtilisateurService,
        AffectationUtilisateurRepository affectationUtilisateurRepository,
        AffectationUtilisateurQueryService affectationUtilisateurQueryService,
        BusinessAuthorizationService businessAuthorizationService
    ) {
        this.affectationUtilisateurService = affectationUtilisateurService;
        this.affectationUtilisateurRepository = affectationUtilisateurRepository;
        this.affectationUtilisateurQueryService = affectationUtilisateurQueryService;
        this.businessAuthorizationService = businessAuthorizationService;
    }

    /**
     * {@code POST  /affectation-utilisateurs} : Create a new affectationUtilisateur.
     *
     * @param affectationUtilisateurDTO the affectationUtilisateurDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new affectationUtilisateurDTO, or with status {@code 400 (Bad Request)} if the affectationUtilisateur has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageAffectationUtilisateur(#affectationUtilisateurDTO)")
    public ResponseEntity<AffectationUtilisateurDTO> createAffectationUtilisateur(
        @Valid @RequestBody AffectationUtilisateurDTO affectationUtilisateurDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save AffectationUtilisateur : {}", affectationUtilisateurDTO);
        if (affectationUtilisateurDTO.getId() != null) {
            throw new BadRequestAlertException("A new affectationUtilisateur cannot already have an ID", ENTITY_NAME, "idexists");
        }
        affectationUtilisateurDTO = affectationUtilisateurService.save(affectationUtilisateurDTO);
        return ResponseEntity.created(new URI("/api/affectation-utilisateurs/" + affectationUtilisateurDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, affectationUtilisateurDTO.getId().toString()))
            .body(affectationUtilisateurDTO);
    }

    /**
     * {@code PUT  /affectation-utilisateurs/:id} : Updates an existing affectationUtilisateur.
     *
     * @param id the id of the affectationUtilisateurDTO to save.
     * @param affectationUtilisateurDTO the affectationUtilisateurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated affectationUtilisateurDTO,
     * or with status {@code 400 (Bad Request)} if the affectationUtilisateurDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the affectationUtilisateurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canUpdateUsers() || @businessAuthorizationService.isCurrentUserLocataire()")
    public ResponseEntity<AffectationUtilisateurDTO> updateAffectationUtilisateur(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AffectationUtilisateurDTO affectationUtilisateurDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AffectationUtilisateur : {}, {}", id, affectationUtilisateurDTO);
        if (affectationUtilisateurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, affectationUtilisateurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!affectationUtilisateurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        assertAffectationManageable(affectationUtilisateurDTO);

        affectationUtilisateurDTO = affectationUtilisateurService.update(affectationUtilisateurDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, affectationUtilisateurDTO.getId().toString()))
            .body(affectationUtilisateurDTO);
    }

    /**
     * {@code PATCH  /affectation-utilisateurs/:id} : Partial updates given fields of an existing affectationUtilisateur, field will ignore if it is null
     *
     * @param id the id of the affectationUtilisateurDTO to save.
     * @param affectationUtilisateurDTO the affectationUtilisateurDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated affectationUtilisateurDTO,
     * or with status {@code 400 (Bad Request)} if the affectationUtilisateurDTO is not valid,
     * or with status {@code 404 (Not Found)} if the affectationUtilisateurDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the affectationUtilisateurDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canUpdateUsers() || @businessAuthorizationService.isCurrentUserLocataire()")
    public ResponseEntity<AffectationUtilisateurDTO> partialUpdateAffectationUtilisateur(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AffectationUtilisateurDTO affectationUtilisateurDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AffectationUtilisateur partially : {}, {}", id, affectationUtilisateurDTO);
        if (affectationUtilisateurDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, affectationUtilisateurDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!affectationUtilisateurRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        assertAffectationManageable(resolveAffectationForAuthorization(id, affectationUtilisateurDTO));

        Optional<AffectationUtilisateurDTO> result = affectationUtilisateurService.partialUpdate(affectationUtilisateurDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, affectationUtilisateurDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /affectation-utilisateurs} : get all the Affectation Utilisateurs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Affectation Utilisateurs in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canReadUsers() || @businessAuthorizationService.canReadOwnAssignments(#criteria)")
    public ResponseEntity<List<AffectationUtilisateurDTO>> getAllAffectationUtilisateurs(AffectationUtilisateurCriteria criteria) {
        LOG.debug("REST request to get AffectationUtilisateurs by criteria: {}", criteria);

        scopeCriteriaToManagedBoutiques(criteria);
        List<AffectationUtilisateurDTO> entityList = affectationUtilisateurQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /affectation-utilisateurs/count} : count all the affectationUtilisateurs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canReadUsers() || @businessAuthorizationService.canReadOwnAssignments(#criteria)")
    public ResponseEntity<Long> countAffectationUtilisateurs(AffectationUtilisateurCriteria criteria) {
        LOG.debug("REST request to count AffectationUtilisateurs by criteria: {}", criteria);
        scopeCriteriaToManagedBoutiques(criteria);
        return ResponseEntity.ok().body(affectationUtilisateurQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /affectation-utilisateurs/:id} : get the "id" affectationUtilisateur.
     *
     * @param id the id of the affectationUtilisateurDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the affectationUtilisateurDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canReadUsers()")
    public ResponseEntity<AffectationUtilisateurDTO> getAffectationUtilisateur(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AffectationUtilisateur : {}", id);
        Optional<AffectationUtilisateurDTO> affectationUtilisateurDTO = affectationUtilisateurService.findOne(id);
        affectationUtilisateurDTO.ifPresent(this::assertAffectationReadable);
        return ResponseUtil.wrapOrNotFound(affectationUtilisateurDTO);
    }

    /**
     * {@code DELETE  /affectation-utilisateurs/:id} : delete the "id" affectationUtilisateur.
     *
     * @param id the id of the affectationUtilisateurDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canDeactivateUsers()")
    public ResponseEntity<Void> deleteAffectationUtilisateur(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AffectationUtilisateur : {}", id);
        affectationUtilisateurService.findOne(id).ifPresent(this::assertAffectationManageable);
        affectationUtilisateurService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    private void scopeCriteriaToManagedBoutiques(AffectationUtilisateurCriteria criteria) {
        if (businessAuthorizationService.isAdmin()) {
            return;
        }

        List<Long> boutiqueIds = new ArrayList<>(businessAuthorizationService.getAccessibleBoutiqueIds());
        if (boutiqueIds.isEmpty()) {
            boutiqueIds.add(-1L);
        }
        LongFilter boutiqueFilter = new LongFilter();
        boutiqueFilter.setIn(boutiqueIds);
        criteria.setBoutiqueId(boutiqueFilter);
    }

    private AffectationUtilisateurDTO resolveAffectationForAuthorization(Long id, AffectationUtilisateurDTO incoming) {
        if (incoming.getBoutique() != null && incoming.getProfil() != null) {
            return incoming;
        }
        return affectationUtilisateurService.findOne(id).orElse(incoming);
    }

    private void assertAffectationReadable(AffectationUtilisateurDTO affectationUtilisateurDTO) {
        if (
            !businessAuthorizationService.isAdmin() &&
            (affectationUtilisateurDTO.getBoutique() == null ||
                !businessAuthorizationService.canAccessBoutique(affectationUtilisateurDTO.getBoutique().getId()))
        ) {
            throw new AccessDeniedException("Access denied to requested assignment");
        }
    }

    private void assertAffectationManageable(AffectationUtilisateurDTO affectationUtilisateurDTO) {
        if (!businessAuthorizationService.canManageAffectationUtilisateur(affectationUtilisateurDTO)) {
            throw new AccessDeniedException("Access denied to requested assignment");
        }
    }
}
