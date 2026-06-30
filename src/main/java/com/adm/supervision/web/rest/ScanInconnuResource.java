package com.adm.supervision.web.rest;

import com.adm.supervision.repository.ScanInconnuRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.ScanInconnuQueryService;
import com.adm.supervision.service.ScanInconnuService;
import com.adm.supervision.service.criteria.ScanInconnuCriteria;
import com.adm.supervision.service.dto.ScanInconnuDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.ScanInconnu}.
 */
@RestController
@RequestMapping("/api/scan-inconnus")
@PreAuthorize("@businessAuthorizationService.canManageStock()")
public class ScanInconnuResource {

    private static final Logger LOG = LoggerFactory.getLogger(ScanInconnuResource.class);

    private static final String ENTITY_NAME = "scanInconnu";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final ScanInconnuService scanInconnuService;

    private final ScanInconnuRepository scanInconnuRepository;

    private final ScanInconnuQueryService scanInconnuQueryService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public ScanInconnuResource(
        ScanInconnuService scanInconnuService,
        ScanInconnuRepository scanInconnuRepository,
        ScanInconnuQueryService scanInconnuQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.scanInconnuService = scanInconnuService;
        this.scanInconnuRepository = scanInconnuRepository;
        this.scanInconnuQueryService = scanInconnuQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * {@code POST  /scan-inconnus} : Create a new scanInconnu.
     *
     * @param scanInconnuDTO the scanInconnuDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scanInconnuDTO, or with status {@code 400 (Bad Request)} if the scanInconnu has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ScanInconnuDTO> createScanInconnu(@Valid @RequestBody ScanInconnuDTO scanInconnuDTO) throws URISyntaxException {
        LOG.debug("REST request to save ScanInconnu : {}", scanInconnuDTO);
        if (scanInconnuDTO.getId() != null) {
            throw new BadRequestAlertException("A new scanInconnu cannot already have an ID", ENTITY_NAME, "idexists");
        }
        scanInconnuDTO = scanInconnuService.save(scanInconnuDTO);
        return ResponseEntity.created(new URI("/api/scan-inconnus/" + scanInconnuDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, scanInconnuDTO.getId().toString()))
            .body(scanInconnuDTO);
    }

    /**
     * {@code PUT  /scan-inconnus/:id} : Updates an existing scanInconnu.
     *
     * @param id the id of the scanInconnuDTO to save.
     * @param scanInconnuDTO the scanInconnuDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scanInconnuDTO,
     * or with status {@code 400 (Bad Request)} if the scanInconnuDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scanInconnuDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScanInconnuDTO> updateScanInconnu(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ScanInconnuDTO scanInconnuDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ScanInconnu : {}, {}", id, scanInconnuDTO);
        if (scanInconnuDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scanInconnuDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scanInconnuRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        scanInconnuDTO = scanInconnuService.update(scanInconnuDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scanInconnuDTO.getId().toString()))
            .body(scanInconnuDTO);
    }

    /**
     * {@code PATCH  /scan-inconnus/:id} : Partial updates given fields of an existing scanInconnu, field will ignore if it is null
     *
     * @param id the id of the scanInconnuDTO to save.
     * @param scanInconnuDTO the scanInconnuDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scanInconnuDTO,
     * or with status {@code 400 (Bad Request)} if the scanInconnuDTO is not valid,
     * or with status {@code 404 (Not Found)} if the scanInconnuDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the scanInconnuDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ScanInconnuDTO> partialUpdateScanInconnu(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ScanInconnuDTO scanInconnuDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ScanInconnu partially : {}, {}", id, scanInconnuDTO);
        if (scanInconnuDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scanInconnuDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!scanInconnuRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ScanInconnuDTO> result = scanInconnuService.partialUpdate(scanInconnuDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, scanInconnuDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /scan-inconnus} : get all the Scan Inconnus.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Scan Inconnus in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ScanInconnuDTO>> getAllScanInconnus(ScanInconnuCriteria criteria) {
        LOG.debug("REST request to get ScanInconnus by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux scans demandes")
        );
        List<ScanInconnuDTO> entityList = scanInconnuQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /scan-inconnus/count} : count all the scanInconnus.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countScanInconnus(ScanInconnuCriteria criteria) {
        LOG.debug("REST request to count ScanInconnus by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux scans demandes")
        );
        return ResponseEntity.ok().body(scanInconnuQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /scan-inconnus/:id} : get the "id" scanInconnu.
     *
     * @param id the id of the scanInconnuDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scanInconnuDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScanInconnuDTO> getScanInconnu(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ScanInconnu : {}", id);
        Optional<ScanInconnuDTO> scanInconnuDTO = scanInconnuService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scanInconnuDTO);
    }

    /**
     * {@code DELETE  /scan-inconnus/:id} : delete the "id" scanInconnu.
     *
     * @param id the id of the scanInconnuDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScanInconnu(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ScanInconnu : {}", id);
        scanInconnuService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
