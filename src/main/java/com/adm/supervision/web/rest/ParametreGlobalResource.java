package com.adm.supervision.web.rest;

import com.adm.supervision.repository.ParametreGlobalRepository;
import com.adm.supervision.service.ParametreGlobalQueryService;
import com.adm.supervision.service.ParametreGlobalService;
import com.adm.supervision.service.criteria.ParametreGlobalCriteria;
import com.adm.supervision.service.dto.ParametreGlobalDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.ParametreGlobal}.
 */
@RestController
@RequestMapping("/api/parametre-globals")
@PreAuthorize("@businessAuthorizationService.canReadSettings()")
public class ParametreGlobalResource {

    private static final Logger LOG = LoggerFactory.getLogger(ParametreGlobalResource.class);

    private static final String ENTITY_NAME = "parametreGlobal";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final ParametreGlobalService parametreGlobalService;

    private final ParametreGlobalRepository parametreGlobalRepository;

    private final ParametreGlobalQueryService parametreGlobalQueryService;

    public ParametreGlobalResource(
        ParametreGlobalService parametreGlobalService,
        ParametreGlobalRepository parametreGlobalRepository,
        ParametreGlobalQueryService parametreGlobalQueryService
    ) {
        this.parametreGlobalService = parametreGlobalService;
        this.parametreGlobalRepository = parametreGlobalRepository;
        this.parametreGlobalQueryService = parametreGlobalQueryService;
    }

    /**
     * {@code POST  /parametre-globals} : Create a new parametreGlobal.
     *
     * @param parametreGlobalDTO the parametreGlobalDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new parametreGlobalDTO, or with status {@code 400 (Bad Request)} if the parametreGlobal has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageParametreGlobal(#parametreGlobalDTO)")
    public ResponseEntity<ParametreGlobalDTO> createParametreGlobal(@Valid @RequestBody ParametreGlobalDTO parametreGlobalDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ParametreGlobal : {}", parametreGlobalDTO);
        if (parametreGlobalDTO.getId() != null) {
            throw new BadRequestAlertException("A new parametreGlobal cannot already have an ID", ENTITY_NAME, "idexists");
        }
        parametreGlobalDTO = parametreGlobalService.save(parametreGlobalDTO);
        return ResponseEntity.created(new URI("/api/parametre-globals/" + parametreGlobalDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, parametreGlobalDTO.getId().toString()))
            .body(parametreGlobalDTO);
    }

    /**
     * {@code PUT  /parametre-globals/:id} : Updates an existing parametreGlobal.
     *
     * @param id the id of the parametreGlobalDTO to save.
     * @param parametreGlobalDTO the parametreGlobalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parametreGlobalDTO,
     * or with status {@code 400 (Bad Request)} if the parametreGlobalDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the parametreGlobalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageParametreGlobal(#parametreGlobalDTO)")
    public ResponseEntity<ParametreGlobalDTO> updateParametreGlobal(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ParametreGlobalDTO parametreGlobalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ParametreGlobal : {}, {}", id, parametreGlobalDTO);
        if (parametreGlobalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parametreGlobalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!parametreGlobalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        parametreGlobalDTO = parametreGlobalService.update(parametreGlobalDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, parametreGlobalDTO.getId().toString()))
            .body(parametreGlobalDTO);
    }

    /**
     * {@code PATCH  /parametre-globals/:id} : Partial updates given fields of an existing parametreGlobal, field will ignore if it is null
     *
     * @param id the id of the parametreGlobalDTO to save.
     * @param parametreGlobalDTO the parametreGlobalDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parametreGlobalDTO,
     * or with status {@code 400 (Bad Request)} if the parametreGlobalDTO is not valid,
     * or with status {@code 404 (Not Found)} if the parametreGlobalDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the parametreGlobalDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageParametreGlobal(#parametreGlobalDTO)")
    public ResponseEntity<ParametreGlobalDTO> partialUpdateParametreGlobal(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ParametreGlobalDTO parametreGlobalDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ParametreGlobal partially : {}, {}", id, parametreGlobalDTO);
        if (parametreGlobalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parametreGlobalDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!parametreGlobalRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParametreGlobalDTO> result = parametreGlobalService.partialUpdate(parametreGlobalDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, parametreGlobalDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /parametre-globals} : get all the Parametre Globals.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Parametre Globals in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ParametreGlobalDTO>> getAllParametreGlobals(ParametreGlobalCriteria criteria) {
        LOG.debug("REST request to get ParametreGlobals by criteria: {}", criteria);

        List<ParametreGlobalDTO> entityList = parametreGlobalQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /parametre-globals/count} : count all the parametreGlobals.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countParametreGlobals(ParametreGlobalCriteria criteria) {
        LOG.debug("REST request to count ParametreGlobals by criteria: {}", criteria);
        return ResponseEntity.ok().body(parametreGlobalQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /parametre-globals/:id} : get the "id" parametreGlobal.
     *
     * @param id the id of the parametreGlobalDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the parametreGlobalDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParametreGlobalDTO> getParametreGlobal(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ParametreGlobal : {}", id);
        Optional<ParametreGlobalDTO> parametreGlobalDTO = parametreGlobalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(parametreGlobalDTO);
    }

    /**
     * {@code DELETE  /parametre-globals/:id} : delete the "id" parametreGlobal.
     *
     * @param id the id of the parametreGlobalDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<Void> deleteParametreGlobal(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ParametreGlobal : {}", id);
        parametreGlobalService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
