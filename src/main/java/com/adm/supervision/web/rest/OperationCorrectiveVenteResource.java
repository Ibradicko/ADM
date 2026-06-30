package com.adm.supervision.web.rest;

import com.adm.supervision.repository.OperationCorrectiveVenteRepository;
import com.adm.supervision.service.OperationCorrectiveVenteQueryService;
import com.adm.supervision.service.OperationCorrectiveVenteService;
import com.adm.supervision.service.criteria.OperationCorrectiveVenteCriteria;
import com.adm.supervision.service.dto.OperationCorrectiveVenteDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.OperationCorrectiveVente}.
 */
@RestController
@RequestMapping("/api/operation-corrective-ventes")
@PreAuthorize("@businessAuthorizationService.canReadSales()")
public class OperationCorrectiveVenteResource {

    private static final Logger LOG = LoggerFactory.getLogger(OperationCorrectiveVenteResource.class);

    private static final String ENTITY_NAME = "operationCorrectiveVente";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final OperationCorrectiveVenteService operationCorrectiveVenteService;

    private final OperationCorrectiveVenteRepository operationCorrectiveVenteRepository;

    private final OperationCorrectiveVenteQueryService operationCorrectiveVenteQueryService;

    public OperationCorrectiveVenteResource(
        OperationCorrectiveVenteService operationCorrectiveVenteService,
        OperationCorrectiveVenteRepository operationCorrectiveVenteRepository,
        OperationCorrectiveVenteQueryService operationCorrectiveVenteQueryService
    ) {
        this.operationCorrectiveVenteService = operationCorrectiveVenteService;
        this.operationCorrectiveVenteRepository = operationCorrectiveVenteRepository;
        this.operationCorrectiveVenteQueryService = operationCorrectiveVenteQueryService;
    }

    /**
     * {@code POST  /operation-corrective-ventes} : Create a new operationCorrectiveVente.
     *
     * @param operationCorrectiveVenteDTO the operationCorrectiveVenteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new operationCorrectiveVenteDTO, or with status {@code 400 (Bad Request)} if the operationCorrectiveVente has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageSales()")
    public ResponseEntity<OperationCorrectiveVenteDTO> createOperationCorrectiveVente(
        @Valid @RequestBody OperationCorrectiveVenteDTO operationCorrectiveVenteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save OperationCorrectiveVente : {}", operationCorrectiveVenteDTO);
        if (operationCorrectiveVenteDTO.getId() != null) {
            throw new BadRequestAlertException("A new operationCorrectiveVente cannot already have an ID", ENTITY_NAME, "idexists");
        }
        operationCorrectiveVenteDTO = operationCorrectiveVenteService.save(operationCorrectiveVenteDTO);
        return ResponseEntity.created(new URI("/api/operation-corrective-ventes/" + operationCorrectiveVenteDTO.getId()))
            .headers(
                HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, operationCorrectiveVenteDTO.getId().toString())
            )
            .body(operationCorrectiveVenteDTO);
    }

    /**
     * {@code PUT  /operation-corrective-ventes/:id} : Updates an existing operationCorrectiveVente.
     *
     * @param id the id of the operationCorrectiveVenteDTO to save.
     * @param operationCorrectiveVenteDTO the operationCorrectiveVenteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated operationCorrectiveVenteDTO,
     * or with status {@code 400 (Bad Request)} if the operationCorrectiveVenteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the operationCorrectiveVenteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("denyAll()")
    public ResponseEntity<OperationCorrectiveVenteDTO> updateOperationCorrectiveVente(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OperationCorrectiveVenteDTO operationCorrectiveVenteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update OperationCorrectiveVente : {}, {}", id, operationCorrectiveVenteDTO);
        if (operationCorrectiveVenteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, operationCorrectiveVenteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!operationCorrectiveVenteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        operationCorrectiveVenteDTO = operationCorrectiveVenteService.update(operationCorrectiveVenteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, operationCorrectiveVenteDTO.getId().toString()))
            .body(operationCorrectiveVenteDTO);
    }

    /**
     * {@code PATCH  /operation-corrective-ventes/:id} : Partial updates given fields of an existing operationCorrectiveVente, field will ignore if it is null
     *
     * @param id the id of the operationCorrectiveVenteDTO to save.
     * @param operationCorrectiveVenteDTO the operationCorrectiveVenteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated operationCorrectiveVenteDTO,
     * or with status {@code 400 (Bad Request)} if the operationCorrectiveVenteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the operationCorrectiveVenteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the operationCorrectiveVenteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("denyAll()")
    public ResponseEntity<OperationCorrectiveVenteDTO> partialUpdateOperationCorrectiveVente(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OperationCorrectiveVenteDTO operationCorrectiveVenteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OperationCorrectiveVente partially : {}, {}", id, operationCorrectiveVenteDTO);
        if (operationCorrectiveVenteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, operationCorrectiveVenteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!operationCorrectiveVenteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OperationCorrectiveVenteDTO> result = operationCorrectiveVenteService.partialUpdate(operationCorrectiveVenteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, operationCorrectiveVenteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /operation-corrective-ventes} : get all the Operation Corrective Ventes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Operation Corrective Ventes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<OperationCorrectiveVenteDTO>> getAllOperationCorrectiveVentes(OperationCorrectiveVenteCriteria criteria) {
        LOG.debug("REST request to get OperationCorrectiveVentes by criteria: {}", criteria);

        List<OperationCorrectiveVenteDTO> entityList = operationCorrectiveVenteQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /operation-corrective-ventes/count} : count all the operationCorrectiveVentes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countOperationCorrectiveVentes(OperationCorrectiveVenteCriteria criteria) {
        LOG.debug("REST request to count OperationCorrectiveVentes by criteria: {}", criteria);
        return ResponseEntity.ok().body(operationCorrectiveVenteQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /operation-corrective-ventes/:id} : get the "id" operationCorrectiveVente.
     *
     * @param id the id of the operationCorrectiveVenteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the operationCorrectiveVenteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OperationCorrectiveVenteDTO> getOperationCorrectiveVente(@PathVariable("id") Long id) {
        LOG.debug("REST request to get OperationCorrectiveVente : {}", id);
        Optional<OperationCorrectiveVenteDTO> operationCorrectiveVenteDTO = operationCorrectiveVenteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(operationCorrectiveVenteDTO);
    }

    /**
     * {@code DELETE  /operation-corrective-ventes/:id} : delete the "id" operationCorrectiveVente.
     *
     * @param id the id of the operationCorrectiveVenteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("denyAll()")
    public ResponseEntity<Void> deleteOperationCorrectiveVente(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OperationCorrectiveVente : {}", id);
        operationCorrectiveVenteService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
