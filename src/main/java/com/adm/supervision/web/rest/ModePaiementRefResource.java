package com.adm.supervision.web.rest;

import com.adm.supervision.repository.ModePaiementRefRepository;
import com.adm.supervision.service.ModePaiementRefQueryService;
import com.adm.supervision.service.ModePaiementRefService;
import com.adm.supervision.service.criteria.ModePaiementRefCriteria;
import com.adm.supervision.service.dto.ModePaiementRefDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.ModePaiementRef}.
 */
@RestController
@RequestMapping("/api/mode-paiement-refs")
@PreAuthorize("@businessAuthorizationService.canReadPaymentSettings()")
public class ModePaiementRefResource {

    private static final Logger LOG = LoggerFactory.getLogger(ModePaiementRefResource.class);

    private static final String ENTITY_NAME = "modePaiementRef";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final ModePaiementRefService modePaiementRefService;

    private final ModePaiementRefRepository modePaiementRefRepository;

    private final ModePaiementRefQueryService modePaiementRefQueryService;

    public ModePaiementRefResource(
        ModePaiementRefService modePaiementRefService,
        ModePaiementRefRepository modePaiementRefRepository,
        ModePaiementRefQueryService modePaiementRefQueryService
    ) {
        this.modePaiementRefService = modePaiementRefService;
        this.modePaiementRefRepository = modePaiementRefRepository;
        this.modePaiementRefQueryService = modePaiementRefQueryService;
    }

    /**
     * {@code POST  /mode-paiement-refs} : Create a new modePaiementRef.
     *
     * @param modePaiementRefDTO the modePaiementRefDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new modePaiementRefDTO, or with status {@code 400 (Bad Request)} if the modePaiementRef has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<ModePaiementRefDTO> createModePaiementRef(@Valid @RequestBody ModePaiementRefDTO modePaiementRefDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ModePaiementRef : {}", modePaiementRefDTO);
        if (modePaiementRefDTO.getId() != null) {
            throw new BadRequestAlertException("A new modePaiementRef cannot already have an ID", ENTITY_NAME, "idexists");
        }
        modePaiementRefDTO = modePaiementRefService.save(modePaiementRefDTO);
        return ResponseEntity.created(new URI("/api/mode-paiement-refs/" + modePaiementRefDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, modePaiementRefDTO.getId().toString()))
            .body(modePaiementRefDTO);
    }

    /**
     * {@code PUT  /mode-paiement-refs/:id} : Updates an existing modePaiementRef.
     *
     * @param id the id of the modePaiementRefDTO to save.
     * @param modePaiementRefDTO the modePaiementRefDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated modePaiementRefDTO,
     * or with status {@code 400 (Bad Request)} if the modePaiementRefDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the modePaiementRefDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<ModePaiementRefDTO> updateModePaiementRef(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ModePaiementRefDTO modePaiementRefDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ModePaiementRef : {}, {}", id, modePaiementRefDTO);
        if (modePaiementRefDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, modePaiementRefDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!modePaiementRefRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        modePaiementRefDTO = modePaiementRefService.update(modePaiementRefDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, modePaiementRefDTO.getId().toString()))
            .body(modePaiementRefDTO);
    }

    /**
     * {@code PATCH  /mode-paiement-refs/:id} : Partial updates given fields of an existing modePaiementRef, field will ignore if it is null
     *
     * @param id the id of the modePaiementRefDTO to save.
     * @param modePaiementRefDTO the modePaiementRefDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated modePaiementRefDTO,
     * or with status {@code 400 (Bad Request)} if the modePaiementRefDTO is not valid,
     * or with status {@code 404 (Not Found)} if the modePaiementRefDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the modePaiementRefDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<ModePaiementRefDTO> partialUpdateModePaiementRef(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ModePaiementRefDTO modePaiementRefDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ModePaiementRef partially : {}, {}", id, modePaiementRefDTO);
        if (modePaiementRefDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, modePaiementRefDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!modePaiementRefRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ModePaiementRefDTO> result = modePaiementRefService.partialUpdate(modePaiementRefDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, modePaiementRefDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mode-paiement-refs} : get all the Mode Paiement Refs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mode Paiement Refs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ModePaiementRefDTO>> getAllModePaiementRefs(ModePaiementRefCriteria criteria) {
        LOG.debug("REST request to get ModePaiementRefs by criteria: {}", criteria);

        List<ModePaiementRefDTO> entityList = modePaiementRefQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /mode-paiement-refs/count} : count all the modePaiementRefs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countModePaiementRefs(ModePaiementRefCriteria criteria) {
        LOG.debug("REST request to count ModePaiementRefs by criteria: {}", criteria);
        return ResponseEntity.ok().body(modePaiementRefQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /mode-paiement-refs/:id} : get the "id" modePaiementRef.
     *
     * @param id the id of the modePaiementRefDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the modePaiementRefDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ModePaiementRefDTO> getModePaiementRef(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ModePaiementRef : {}", id);
        Optional<ModePaiementRefDTO> modePaiementRefDTO = modePaiementRefService.findOne(id);
        return ResponseUtil.wrapOrNotFound(modePaiementRefDTO);
    }

    /**
     * {@code DELETE  /mode-paiement-refs/:id} : delete the "id" modePaiementRef.
     *
     * @param id the id of the modePaiementRefDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<Void> deleteModePaiementRef(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ModePaiementRef : {}", id);
        modePaiementRefService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
