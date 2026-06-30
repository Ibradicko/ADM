package com.adm.supervision.web.rest;

import com.adm.supervision.repository.UniteMesureRepository;
import com.adm.supervision.service.UniteMesureQueryService;
import com.adm.supervision.service.UniteMesureService;
import com.adm.supervision.service.criteria.UniteMesureCriteria;
import com.adm.supervision.service.dto.UniteMesureDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.UniteMesure}.
 */
@RestController
@RequestMapping("/api/unite-mesures")
@PreAuthorize("isAuthenticated()")
public class UniteMesureResource {

    private static final Logger LOG = LoggerFactory.getLogger(UniteMesureResource.class);

    private static final String ENTITY_NAME = "uniteMesure";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final UniteMesureService uniteMesureService;

    private final UniteMesureRepository uniteMesureRepository;

    private final UniteMesureQueryService uniteMesureQueryService;

    public UniteMesureResource(
        UniteMesureService uniteMesureService,
        UniteMesureRepository uniteMesureRepository,
        UniteMesureQueryService uniteMesureQueryService
    ) {
        this.uniteMesureService = uniteMesureService;
        this.uniteMesureRepository = uniteMesureRepository;
        this.uniteMesureQueryService = uniteMesureQueryService;
    }

    /**
     * {@code POST  /unite-mesures} : Create a new uniteMesure.
     *
     * @param uniteMesureDTO the uniteMesureDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new uniteMesureDTO, or with status {@code 400 (Bad Request)} if the uniteMesure has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<UniteMesureDTO> createUniteMesure(@Valid @RequestBody UniteMesureDTO uniteMesureDTO) throws URISyntaxException {
        LOG.debug("REST request to save UniteMesure : {}", uniteMesureDTO);
        if (uniteMesureDTO.getId() != null) {
            throw new BadRequestAlertException("A new uniteMesure cannot already have an ID", ENTITY_NAME, "idexists");
        }
        uniteMesureDTO = uniteMesureService.save(uniteMesureDTO);
        return ResponseEntity.created(new URI("/api/unite-mesures/" + uniteMesureDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, uniteMesureDTO.getId().toString()))
            .body(uniteMesureDTO);
    }

    /**
     * {@code PUT  /unite-mesures/:id} : Updates an existing uniteMesure.
     *
     * @param id the id of the uniteMesureDTO to save.
     * @param uniteMesureDTO the uniteMesureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uniteMesureDTO,
     * or with status {@code 400 (Bad Request)} if the uniteMesureDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the uniteMesureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<UniteMesureDTO> updateUniteMesure(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UniteMesureDTO uniteMesureDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update UniteMesure : {}, {}", id, uniteMesureDTO);
        if (uniteMesureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uniteMesureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uniteMesureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        uniteMesureDTO = uniteMesureService.update(uniteMesureDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uniteMesureDTO.getId().toString()))
            .body(uniteMesureDTO);
    }

    /**
     * {@code PATCH  /unite-mesures/:id} : Partial updates given fields of an existing uniteMesure, field will ignore if it is null
     *
     * @param id the id of the uniteMesureDTO to save.
     * @param uniteMesureDTO the uniteMesureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated uniteMesureDTO,
     * or with status {@code 400 (Bad Request)} if the uniteMesureDTO is not valid,
     * or with status {@code 404 (Not Found)} if the uniteMesureDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the uniteMesureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<UniteMesureDTO> partialUpdateUniteMesure(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UniteMesureDTO uniteMesureDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update UniteMesure partially : {}, {}", id, uniteMesureDTO);
        if (uniteMesureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, uniteMesureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!uniteMesureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<UniteMesureDTO> result = uniteMesureService.partialUpdate(uniteMesureDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, uniteMesureDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /unite-mesures} : get all the Unite Mesures.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Unite Mesures in body.
     */
    @GetMapping("")
    public ResponseEntity<List<UniteMesureDTO>> getAllUniteMesures(UniteMesureCriteria criteria) {
        LOG.debug("REST request to get UniteMesures by criteria: {}", criteria);

        List<UniteMesureDTO> entityList = uniteMesureQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /unite-mesures/count} : count all the uniteMesures.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countUniteMesures(UniteMesureCriteria criteria) {
        LOG.debug("REST request to count UniteMesures by criteria: {}", criteria);
        return ResponseEntity.ok().body(uniteMesureQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /unite-mesures/:id} : get the "id" uniteMesure.
     *
     * @param id the id of the uniteMesureDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the uniteMesureDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UniteMesureDTO> getUniteMesure(@PathVariable("id") Long id) {
        LOG.debug("REST request to get UniteMesure : {}", id);
        Optional<UniteMesureDTO> uniteMesureDTO = uniteMesureService.findOne(id);
        return ResponseUtil.wrapOrNotFound(uniteMesureDTO);
    }

    /**
     * {@code DELETE  /unite-mesures/:id} : delete the "id" uniteMesure.
     *
     * @param id the id of the uniteMesureDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<Void> deleteUniteMesure(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete UniteMesure : {}", id);
        uniteMesureService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
