package com.adm.supervision.web.rest;

import com.adm.supervision.repository.PermissionMetierRepository;
import com.adm.supervision.service.PermissionMetierQueryService;
import com.adm.supervision.service.PermissionMetierService;
import com.adm.supervision.service.criteria.PermissionMetierCriteria;
import com.adm.supervision.service.dto.PermissionMetierDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.PermissionMetier}.
 */
@RestController
@RequestMapping("/api/permission-metiers")
public class PermissionMetierResource {

    private static final Logger LOG = LoggerFactory.getLogger(PermissionMetierResource.class);

    private static final String ENTITY_NAME = "permissionMetier";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final PermissionMetierService permissionMetierService;

    private final PermissionMetierRepository permissionMetierRepository;

    private final PermissionMetierQueryService permissionMetierQueryService;

    public PermissionMetierResource(
        PermissionMetierService permissionMetierService,
        PermissionMetierRepository permissionMetierRepository,
        PermissionMetierQueryService permissionMetierQueryService
    ) {
        this.permissionMetierService = permissionMetierService;
        this.permissionMetierRepository = permissionMetierRepository;
        this.permissionMetierQueryService = permissionMetierQueryService;
    }

    /**
     * {@code POST  /permission-metiers} : Create a new permissionMetier.
     *
     * @param permissionMetierDTO the permissionMetierDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new permissionMetierDTO, or with status {@code 400 (Bad Request)} if the permissionMetier has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<PermissionMetierDTO> createPermissionMetier(@Valid @RequestBody PermissionMetierDTO permissionMetierDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PermissionMetier : {}", permissionMetierDTO);
        if (permissionMetierDTO.getId() != null) {
            throw new BadRequestAlertException("A new permissionMetier cannot already have an ID", ENTITY_NAME, "idexists");
        }
        permissionMetierDTO = permissionMetierService.save(permissionMetierDTO);
        return ResponseEntity.created(new URI("/api/permission-metiers/" + permissionMetierDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, permissionMetierDTO.getId().toString()))
            .body(permissionMetierDTO);
    }

    /**
     * {@code PUT  /permission-metiers/:id} : Updates an existing permissionMetier.
     *
     * @param id the id of the permissionMetierDTO to save.
     * @param permissionMetierDTO the permissionMetierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated permissionMetierDTO,
     * or with status {@code 400 (Bad Request)} if the permissionMetierDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the permissionMetierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<PermissionMetierDTO> updatePermissionMetier(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PermissionMetierDTO permissionMetierDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PermissionMetier : {}, {}", id, permissionMetierDTO);
        if (permissionMetierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, permissionMetierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!permissionMetierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        permissionMetierDTO = permissionMetierService.update(permissionMetierDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, permissionMetierDTO.getId().toString()))
            .body(permissionMetierDTO);
    }

    /**
     * {@code PATCH  /permission-metiers/:id} : Partial updates given fields of an existing permissionMetier, field will ignore if it is null
     *
     * @param id the id of the permissionMetierDTO to save.
     * @param permissionMetierDTO the permissionMetierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated permissionMetierDTO,
     * or with status {@code 400 (Bad Request)} if the permissionMetierDTO is not valid,
     * or with status {@code 404 (Not Found)} if the permissionMetierDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the permissionMetierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<PermissionMetierDTO> partialUpdatePermissionMetier(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PermissionMetierDTO permissionMetierDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PermissionMetier partially : {}, {}", id, permissionMetierDTO);
        if (permissionMetierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, permissionMetierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!permissionMetierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PermissionMetierDTO> result = permissionMetierService.partialUpdate(permissionMetierDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, permissionMetierDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /permission-metiers} : get all the Permission Metiers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Permission Metiers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PermissionMetierDTO>> getAllPermissionMetiers(PermissionMetierCriteria criteria) {
        LOG.debug("REST request to get PermissionMetiers by criteria: {}", criteria);

        List<PermissionMetierDTO> entityList = permissionMetierQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /permission-metiers/count} : count all the permissionMetiers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPermissionMetiers(PermissionMetierCriteria criteria) {
        LOG.debug("REST request to count PermissionMetiers by criteria: {}", criteria);
        return ResponseEntity.ok().body(permissionMetierQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /permission-metiers/:id} : get the "id" permissionMetier.
     *
     * @param id the id of the permissionMetierDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the permissionMetierDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PermissionMetierDTO> getPermissionMetier(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PermissionMetier : {}", id);
        Optional<PermissionMetierDTO> permissionMetierDTO = permissionMetierService.findOne(id);
        return ResponseUtil.wrapOrNotFound(permissionMetierDTO);
    }

    /**
     * {@code DELETE  /permission-metiers/:id} : delete the "id" permissionMetier.
     *
     * @param id the id of the permissionMetierDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<Void> deletePermissionMetier(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PermissionMetier : {}", id);
        permissionMetierService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
