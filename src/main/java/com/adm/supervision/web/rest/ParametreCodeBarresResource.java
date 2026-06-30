package com.adm.supervision.web.rest;

import com.adm.supervision.repository.ParametreCodeBarresRepository;
import com.adm.supervision.service.ParametreCodeBarresQueryService;
import com.adm.supervision.service.ParametreCodeBarresService;
import com.adm.supervision.service.criteria.ParametreCodeBarresCriteria;
import com.adm.supervision.service.dto.ParametreCodeBarresDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.ParametreCodeBarres}.
 */
@RestController
@RequestMapping("/api/parametre-code-barres")
@PreAuthorize("@businessAuthorizationService.canReadSettings()")
public class ParametreCodeBarresResource {

    private static final Logger LOG = LoggerFactory.getLogger(ParametreCodeBarresResource.class);

    private static final String ENTITY_NAME = "parametreCodeBarres";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final ParametreCodeBarresService parametreCodeBarresService;

    private final ParametreCodeBarresRepository parametreCodeBarresRepository;

    private final ParametreCodeBarresQueryService parametreCodeBarresQueryService;

    public ParametreCodeBarresResource(
        ParametreCodeBarresService parametreCodeBarresService,
        ParametreCodeBarresRepository parametreCodeBarresRepository,
        ParametreCodeBarresQueryService parametreCodeBarresQueryService
    ) {
        this.parametreCodeBarresService = parametreCodeBarresService;
        this.parametreCodeBarresRepository = parametreCodeBarresRepository;
        this.parametreCodeBarresQueryService = parametreCodeBarresQueryService;
    }

    /**
     * {@code POST  /parametre-code-barres} : Create a new parametreCodeBarres.
     *
     * @param parametreCodeBarresDTO the parametreCodeBarresDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new parametreCodeBarresDTO, or with status {@code 400 (Bad Request)} if the parametreCodeBarres has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<ParametreCodeBarresDTO> createParametreCodeBarres(
        @Valid @RequestBody ParametreCodeBarresDTO parametreCodeBarresDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save ParametreCodeBarres : {}", parametreCodeBarresDTO);
        if (parametreCodeBarresDTO.getId() != null) {
            throw new BadRequestAlertException("A new parametreCodeBarres cannot already have an ID", ENTITY_NAME, "idexists");
        }
        parametreCodeBarresDTO = parametreCodeBarresService.save(parametreCodeBarresDTO);
        return ResponseEntity.created(new URI("/api/parametre-code-barres/" + parametreCodeBarresDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, parametreCodeBarresDTO.getId().toString()))
            .body(parametreCodeBarresDTO);
    }

    /**
     * {@code PUT  /parametre-code-barres/:id} : Updates an existing parametreCodeBarres.
     *
     * @param id the id of the parametreCodeBarresDTO to save.
     * @param parametreCodeBarresDTO the parametreCodeBarresDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parametreCodeBarresDTO,
     * or with status {@code 400 (Bad Request)} if the parametreCodeBarresDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the parametreCodeBarresDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<ParametreCodeBarresDTO> updateParametreCodeBarres(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ParametreCodeBarresDTO parametreCodeBarresDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ParametreCodeBarres : {}, {}", id, parametreCodeBarresDTO);
        if (parametreCodeBarresDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parametreCodeBarresDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!parametreCodeBarresRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        parametreCodeBarresDTO = parametreCodeBarresService.update(parametreCodeBarresDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, parametreCodeBarresDTO.getId().toString()))
            .body(parametreCodeBarresDTO);
    }

    /**
     * {@code PATCH  /parametre-code-barres/:id} : Partial updates given fields of an existing parametreCodeBarres, field will ignore if it is null
     *
     * @param id the id of the parametreCodeBarresDTO to save.
     * @param parametreCodeBarresDTO the parametreCodeBarresDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated parametreCodeBarresDTO,
     * or with status {@code 400 (Bad Request)} if the parametreCodeBarresDTO is not valid,
     * or with status {@code 404 (Not Found)} if the parametreCodeBarresDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the parametreCodeBarresDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<ParametreCodeBarresDTO> partialUpdateParametreCodeBarres(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ParametreCodeBarresDTO parametreCodeBarresDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ParametreCodeBarres partially : {}, {}", id, parametreCodeBarresDTO);
        if (parametreCodeBarresDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, parametreCodeBarresDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!parametreCodeBarresRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParametreCodeBarresDTO> result = parametreCodeBarresService.partialUpdate(parametreCodeBarresDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, parametreCodeBarresDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /parametre-code-barres} : get all the Parametre Code Barres.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Parametre Code Barres in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ParametreCodeBarresDTO>> getAllParametreCodeBarreses(ParametreCodeBarresCriteria criteria) {
        LOG.debug("REST request to get ParametreCodeBarreses by criteria: {}", criteria);

        List<ParametreCodeBarresDTO> entityList = parametreCodeBarresQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /parametre-code-barres/count} : count all the parametreCodeBarreses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countParametreCodeBarreses(ParametreCodeBarresCriteria criteria) {
        LOG.debug("REST request to count ParametreCodeBarreses by criteria: {}", criteria);
        return ResponseEntity.ok().body(parametreCodeBarresQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /parametre-code-barres/:id} : get the "id" parametreCodeBarres.
     *
     * @param id the id of the parametreCodeBarresDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the parametreCodeBarresDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ParametreCodeBarresDTO> getParametreCodeBarres(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ParametreCodeBarres : {}", id);
        Optional<ParametreCodeBarresDTO> parametreCodeBarresDTO = parametreCodeBarresService.findOne(id);
        return ResponseUtil.wrapOrNotFound(parametreCodeBarresDTO);
    }

    /**
     * {@code DELETE  /parametre-code-barres/:id} : delete the "id" parametreCodeBarres.
     *
     * @param id the id of the parametreCodeBarresDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<Void> deleteParametreCodeBarres(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ParametreCodeBarres : {}", id);
        parametreCodeBarresService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
