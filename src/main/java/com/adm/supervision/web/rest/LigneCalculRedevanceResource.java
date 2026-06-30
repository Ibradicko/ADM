package com.adm.supervision.web.rest;

import com.adm.supervision.repository.LigneCalculRedevanceRepository;
import com.adm.supervision.service.LigneCalculRedevanceQueryService;
import com.adm.supervision.service.LigneCalculRedevanceService;
import com.adm.supervision.service.criteria.LigneCalculRedevanceCriteria;
import com.adm.supervision.service.dto.LigneCalculRedevanceDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.LigneCalculRedevance}.
 */
@RestController
@RequestMapping("/api/ligne-calcul-redevances")
@PreAuthorize("@businessAuthorizationService.canReadRoyalties()")
public class LigneCalculRedevanceResource {

    private static final Logger LOG = LoggerFactory.getLogger(LigneCalculRedevanceResource.class);

    private static final String ENTITY_NAME = "ligneCalculRedevance";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final LigneCalculRedevanceService ligneCalculRedevanceService;

    private final LigneCalculRedevanceRepository ligneCalculRedevanceRepository;

    private final LigneCalculRedevanceQueryService ligneCalculRedevanceQueryService;

    public LigneCalculRedevanceResource(
        LigneCalculRedevanceService ligneCalculRedevanceService,
        LigneCalculRedevanceRepository ligneCalculRedevanceRepository,
        LigneCalculRedevanceQueryService ligneCalculRedevanceQueryService
    ) {
        this.ligneCalculRedevanceService = ligneCalculRedevanceService;
        this.ligneCalculRedevanceRepository = ligneCalculRedevanceRepository;
        this.ligneCalculRedevanceQueryService = ligneCalculRedevanceQueryService;
    }

    /**
     * {@code POST  /ligne-calcul-redevances} : Create a new ligneCalculRedevance.
     *
     * @param ligneCalculRedevanceDTO the ligneCalculRedevanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ligneCalculRedevanceDTO, or with status {@code 400 (Bad Request)} if the ligneCalculRedevance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LigneCalculRedevanceDTO> createLigneCalculRedevance(
        @Valid @RequestBody LigneCalculRedevanceDTO ligneCalculRedevanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save LigneCalculRedevance : {}", ligneCalculRedevanceDTO);
        if (ligneCalculRedevanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new ligneCalculRedevance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ligneCalculRedevanceDTO = ligneCalculRedevanceService.save(ligneCalculRedevanceDTO);
        return ResponseEntity.created(new URI("/api/ligne-calcul-redevances/" + ligneCalculRedevanceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ligneCalculRedevanceDTO.getId().toString()))
            .body(ligneCalculRedevanceDTO);
    }

    /**
     * {@code PUT  /ligne-calcul-redevances/:id} : Updates an existing ligneCalculRedevance.
     *
     * @param id the id of the ligneCalculRedevanceDTO to save.
     * @param ligneCalculRedevanceDTO the ligneCalculRedevanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneCalculRedevanceDTO,
     * or with status {@code 400 (Bad Request)} if the ligneCalculRedevanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ligneCalculRedevanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LigneCalculRedevanceDTO> updateLigneCalculRedevance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LigneCalculRedevanceDTO ligneCalculRedevanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LigneCalculRedevance : {}, {}", id, ligneCalculRedevanceDTO);
        if (ligneCalculRedevanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneCalculRedevanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneCalculRedevanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ligneCalculRedevanceDTO = ligneCalculRedevanceService.update(ligneCalculRedevanceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneCalculRedevanceDTO.getId().toString()))
            .body(ligneCalculRedevanceDTO);
    }

    /**
     * {@code PATCH  /ligne-calcul-redevances/:id} : Partial updates given fields of an existing ligneCalculRedevance, field will ignore if it is null
     *
     * @param id the id of the ligneCalculRedevanceDTO to save.
     * @param ligneCalculRedevanceDTO the ligneCalculRedevanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneCalculRedevanceDTO,
     * or with status {@code 400 (Bad Request)} if the ligneCalculRedevanceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ligneCalculRedevanceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ligneCalculRedevanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LigneCalculRedevanceDTO> partialUpdateLigneCalculRedevance(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LigneCalculRedevanceDTO ligneCalculRedevanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LigneCalculRedevance partially : {}, {}", id, ligneCalculRedevanceDTO);
        if (ligneCalculRedevanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneCalculRedevanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneCalculRedevanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LigneCalculRedevanceDTO> result = ligneCalculRedevanceService.partialUpdate(ligneCalculRedevanceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneCalculRedevanceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ligne-calcul-redevances} : get all the Ligne Calcul Redevances.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ligne Calcul Redevances in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LigneCalculRedevanceDTO>> getAllLigneCalculRedevances(LigneCalculRedevanceCriteria criteria) {
        LOG.debug("REST request to get LigneCalculRedevances by criteria: {}", criteria);

        List<LigneCalculRedevanceDTO> entityList = ligneCalculRedevanceQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /ligne-calcul-redevances/count} : count all the ligneCalculRedevances.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countLigneCalculRedevances(LigneCalculRedevanceCriteria criteria) {
        LOG.debug("REST request to count LigneCalculRedevances by criteria: {}", criteria);
        return ResponseEntity.ok().body(ligneCalculRedevanceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ligne-calcul-redevances/:id} : get the "id" ligneCalculRedevance.
     *
     * @param id the id of the ligneCalculRedevanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ligneCalculRedevanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LigneCalculRedevanceDTO> getLigneCalculRedevance(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LigneCalculRedevance : {}", id);
        Optional<LigneCalculRedevanceDTO> ligneCalculRedevanceDTO = ligneCalculRedevanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ligneCalculRedevanceDTO);
    }

    /**
     * {@code DELETE  /ligne-calcul-redevances/:id} : delete the "id" ligneCalculRedevance.
     *
     * @param id the id of the ligneCalculRedevanceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLigneCalculRedevance(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LigneCalculRedevance : {}", id);
        ligneCalculRedevanceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
