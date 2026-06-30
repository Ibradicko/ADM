package com.adm.supervision.web.rest;

import com.adm.supervision.repository.RegularisationRedevanceRepository;
import com.adm.supervision.service.RegularisationRedevanceQueryService;
import com.adm.supervision.service.RegularisationRedevanceService;
import com.adm.supervision.service.criteria.RegularisationRedevanceCriteria;
import com.adm.supervision.service.dto.RegularisationRedevanceDTO;
import com.adm.supervision.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adm.supervision.domain.RegularisationRedevance}.
 */
@RestController
@RequestMapping("/api/regularisation-redevances")
public class RegularisationRedevanceResource {

    private static final Logger LOG = LoggerFactory.getLogger(RegularisationRedevanceResource.class);

    private static final String ENTITY_NAME = "regularisationRedevance";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final RegularisationRedevanceService regularisationRedevanceService;

    private final RegularisationRedevanceRepository regularisationRedevanceRepository;

    private final RegularisationRedevanceQueryService regularisationRedevanceQueryService;

    public RegularisationRedevanceResource(
        RegularisationRedevanceService regularisationRedevanceService,
        RegularisationRedevanceRepository regularisationRedevanceRepository,
        RegularisationRedevanceQueryService regularisationRedevanceQueryService
    ) {
        this.regularisationRedevanceService = regularisationRedevanceService;
        this.regularisationRedevanceRepository = regularisationRedevanceRepository;
        this.regularisationRedevanceQueryService = regularisationRedevanceQueryService;
    }

    /**
     * {@code POST  /regularisation-redevances} : Create a new regularisationRedevance.
     *
     * @param regularisationRedevanceDTO the regularisationRedevanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new regularisationRedevanceDTO, or with status {@code 400 (Bad Request)} if the regularisationRedevance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageRoyalties()")
    public ResponseEntity<RegularisationRedevanceDTO> createRegularisationRedevance(
        @Valid @RequestBody RegularisationRedevanceDTO regularisationRedevanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save RegularisationRedevance : {}", regularisationRedevanceDTO);
        if (regularisationRedevanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new regularisationRedevance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        regularisationRedevanceDTO = regularisationRedevanceService.save(regularisationRedevanceDTO);
        return ResponseEntity.created(new URI("/api/regularisation-redevances/" + regularisationRedevanceDTO.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, regularisationRedevanceDTO.getId().toString())
            )
            .body(regularisationRedevanceDTO);
    }

    /**
     * {@code PUT  /regularisation-redevances/:id} : Updates an existing regularisationRedevance.
     *
     * @param id the id of the regularisationRedevanceDTO to save.
     * @param regularisationRedevanceDTO the regularisationRedevanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated regularisationRedevanceDTO,
     * or with status {@code 400 (Bad Request)} if the regularisationRedevanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the regularisationRedevanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageRoyalties()")
    public ResponseEntity<RegularisationRedevanceDTO> updateRegularisationRedevance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RegularisationRedevanceDTO regularisationRedevanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RegularisationRedevance : {}, {}", id, regularisationRedevanceDTO);
        if (regularisationRedevanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, regularisationRedevanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!regularisationRedevanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        regularisationRedevanceDTO = regularisationRedevanceService.update(regularisationRedevanceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, regularisationRedevanceDTO.getId().toString()))
            .body(regularisationRedevanceDTO);
    }

    /**
     * {@code PATCH  /regularisation-redevances/:id} : Partial updates given fields of an existing regularisationRedevance, field will ignore if it is null
     *
     * @param id the id of the regularisationRedevanceDTO to save.
     * @param regularisationRedevanceDTO the regularisationRedevanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated regularisationRedevanceDTO,
     * or with status {@code 400 (Bad Request)} if the regularisationRedevanceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the regularisationRedevanceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the regularisationRedevanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageRoyalties()")
    public ResponseEntity<RegularisationRedevanceDTO> partialUpdateRegularisationRedevance(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RegularisationRedevanceDTO regularisationRedevanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RegularisationRedevance partially : {}, {}", id, regularisationRedevanceDTO);
        if (regularisationRedevanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, regularisationRedevanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!regularisationRedevanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RegularisationRedevanceDTO> result = regularisationRedevanceService.partialUpdate(regularisationRedevanceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, regularisationRedevanceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /regularisation-redevances} : get all the Regularisation Redevances.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Regularisation Redevances in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canReadRoyalties()")
    public ResponseEntity<List<RegularisationRedevanceDTO>> getAllRegularisationRedevances(RegularisationRedevanceCriteria criteria) {
        LOG.debug("REST request to get RegularisationRedevances by criteria: {}", criteria);

        List<RegularisationRedevanceDTO> entityList = regularisationRedevanceQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /regularisation-redevances/count} : count all the regularisationRedevances.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canReadRoyalties()")
    public ResponseEntity<Long> countRegularisationRedevances(RegularisationRedevanceCriteria criteria) {
        LOG.debug("REST request to count RegularisationRedevances by criteria: {}", criteria);
        return ResponseEntity.ok().body(regularisationRedevanceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /regularisation-redevances/:id} : get the "id" regularisationRedevance.
     *
     * @param id the id of the regularisationRedevanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the regularisationRedevanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canReadRoyalties()")
    public ResponseEntity<RegularisationRedevanceDTO> getRegularisationRedevance(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RegularisationRedevance : {}", id);
        Optional<RegularisationRedevanceDTO> regularisationRedevanceDTO = regularisationRedevanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(regularisationRedevanceDTO);
    }

    /**
     * {@code DELETE  /regularisation-redevances/:id} : delete the "id" regularisationRedevance.
     *
     * @param id the id of the regularisationRedevanceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageRoyalties()")
    public ResponseEntity<Void> deleteRegularisationRedevance(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RegularisationRedevance : {}", id);
        regularisationRedevanceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
