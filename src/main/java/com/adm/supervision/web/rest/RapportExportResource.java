package com.adm.supervision.web.rest;

import com.adm.supervision.repository.RapportExportRepository;
import com.adm.supervision.service.RapportExportQueryService;
import com.adm.supervision.service.RapportExportService;
import com.adm.supervision.service.criteria.RapportExportCriteria;
import com.adm.supervision.service.dto.RapportExportDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adm.supervision.domain.RapportExport}.
 */
@RestController
@RequestMapping("/api/rapport-exports")
public class RapportExportResource {

    private static final Logger LOG = LoggerFactory.getLogger(RapportExportResource.class);

    private static final String ENTITY_NAME = "rapportExport";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final RapportExportService rapportExportService;

    private final RapportExportRepository rapportExportRepository;

    private final RapportExportQueryService rapportExportQueryService;

    public RapportExportResource(
        RapportExportService rapportExportService,
        RapportExportRepository rapportExportRepository,
        RapportExportQueryService rapportExportQueryService
    ) {
        this.rapportExportService = rapportExportService;
        this.rapportExportRepository = rapportExportRepository;
        this.rapportExportQueryService = rapportExportQueryService;
    }

    /**
     * {@code POST  /rapport-exports} : Create a new rapportExport.
     *
     * @param rapportExportDTO the rapportExportDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rapportExportDTO, or with status {@code 400 (Bad Request)} if the rapportExport has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canExportReporting()")
    public ResponseEntity<RapportExportDTO> createRapportExport(@Valid @RequestBody RapportExportDTO rapportExportDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save RapportExport : {}", rapportExportDTO);
        if (rapportExportDTO.getId() != null) {
            throw new BadRequestAlertException("A new rapportExport cannot already have an ID", ENTITY_NAME, "idexists");
        }
        rapportExportDTO = rapportExportService.save(rapportExportDTO);
        return ResponseEntity.created(new URI("/api/rapport-exports/" + rapportExportDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, rapportExportDTO.getId().toString()))
            .body(rapportExportDTO);
    }

    /**
     * {@code PUT  /rapport-exports/:id} : Updates an existing rapportExport.
     *
     * @param id the id of the rapportExportDTO to save.
     * @param rapportExportDTO the rapportExportDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rapportExportDTO,
     * or with status {@code 400 (Bad Request)} if the rapportExportDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rapportExportDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canExportReporting() and @resourceBoutiqueSecurityService.canAccessRapportExport(#id)")
    public ResponseEntity<RapportExportDTO> updateRapportExport(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RapportExportDTO rapportExportDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RapportExport : {}, {}", id, rapportExportDTO);
        if (rapportExportDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rapportExportDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rapportExportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        rapportExportDTO = rapportExportService.update(rapportExportDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rapportExportDTO.getId().toString()))
            .body(rapportExportDTO);
    }

    /**
     * {@code PATCH  /rapport-exports/:id} : Partial updates given fields of an existing rapportExport, field will ignore if it is null
     *
     * @param id the id of the rapportExportDTO to save.
     * @param rapportExportDTO the rapportExportDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rapportExportDTO,
     * or with status {@code 400 (Bad Request)} if the rapportExportDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rapportExportDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rapportExportDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canExportReporting() and @resourceBoutiqueSecurityService.canAccessRapportExport(#id)")
    public ResponseEntity<RapportExportDTO> partialUpdateRapportExport(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RapportExportDTO rapportExportDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RapportExport partially : {}, {}", id, rapportExportDTO);
        if (rapportExportDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rapportExportDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!rapportExportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RapportExportDTO> result = rapportExportService.partialUpdate(rapportExportDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, rapportExportDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /rapport-exports} : get all the Rapport Exports.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Rapport Exports in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canAccessReportingExports()")
    public ResponseEntity<List<RapportExportDTO>> getAllRapportExports(
        RapportExportCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get RapportExports by criteria: {}", criteria);
        Page<RapportExportDTO> page = rapportExportQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /rapport-exports/count} : count all the rapportExports.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canAccessReportingExports()")
    public ResponseEntity<Long> countRapportExports(RapportExportCriteria criteria) {
        LOG.debug("REST request to count RapportExports by criteria: {}", criteria);
        return ResponseEntity.ok().body(rapportExportQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /rapport-exports/:id} : get the "id" rapportExport.
     *
     * @param id the id of the rapportExportDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rapportExportDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize(
        "@businessAuthorizationService.canAccessReportingExports() and @resourceBoutiqueSecurityService.canAccessRapportExport(#id)"
    )
    public ResponseEntity<RapportExportDTO> getRapportExport(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RapportExport : {}", id);
        Optional<RapportExportDTO> rapportExportDTO = rapportExportService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rapportExportDTO);
    }

    /**
     * {@code DELETE  /rapport-exports/:id} : delete the "id" rapportExport.
     *
     * @param id the id of the rapportExportDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canExportReporting() and @resourceBoutiqueSecurityService.canAccessRapportExport(#id)")
    public ResponseEntity<Void> deleteRapportExport(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RapportExport : {}", id);
        rapportExportService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
