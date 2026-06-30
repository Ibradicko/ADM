package com.adm.supervision.web.rest;

import com.adm.supervision.repository.LigneTransfertStockRepository;
import com.adm.supervision.service.LigneTransfertStockQueryService;
import com.adm.supervision.service.LigneTransfertStockService;
import com.adm.supervision.service.criteria.LigneTransfertStockCriteria;
import com.adm.supervision.service.dto.LigneTransfertStockDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.LigneTransfertStock}.
 */
@RestController
@RequestMapping("/api/ligne-transfert-stocks")
@PreAuthorize("@businessAuthorizationService.canReadStock()")
public class LigneTransfertStockResource {

    private static final Logger LOG = LoggerFactory.getLogger(LigneTransfertStockResource.class);

    private static final String ENTITY_NAME = "ligneTransfertStock";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final LigneTransfertStockService ligneTransfertStockService;

    private final LigneTransfertStockRepository ligneTransfertStockRepository;

    private final LigneTransfertStockQueryService ligneTransfertStockQueryService;

    public LigneTransfertStockResource(
        LigneTransfertStockService ligneTransfertStockService,
        LigneTransfertStockRepository ligneTransfertStockRepository,
        LigneTransfertStockQueryService ligneTransfertStockQueryService
    ) {
        this.ligneTransfertStockService = ligneTransfertStockService;
        this.ligneTransfertStockRepository = ligneTransfertStockRepository;
        this.ligneTransfertStockQueryService = ligneTransfertStockQueryService;
    }

    /**
     * {@code POST  /ligne-transfert-stocks} : Create a new ligneTransfertStock.
     *
     * @param ligneTransfertStockDTO the ligneTransfertStockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ligneTransfertStockDTO, or with status {@code 400 (Bad Request)} if the ligneTransfertStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LigneTransfertStockDTO> createLigneTransfertStock(
        @Valid @RequestBody LigneTransfertStockDTO ligneTransfertStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save LigneTransfertStock : {}", ligneTransfertStockDTO);
        if (ligneTransfertStockDTO.getId() != null) {
            throw new BadRequestAlertException("A new ligneTransfertStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ligneTransfertStockDTO = ligneTransfertStockService.save(ligneTransfertStockDTO);
        return ResponseEntity.created(new URI("/api/ligne-transfert-stocks/" + ligneTransfertStockDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ligneTransfertStockDTO.getId().toString()))
            .body(ligneTransfertStockDTO);
    }

    /**
     * {@code PUT  /ligne-transfert-stocks/:id} : Updates an existing ligneTransfertStock.
     *
     * @param id the id of the ligneTransfertStockDTO to save.
     * @param ligneTransfertStockDTO the ligneTransfertStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneTransfertStockDTO,
     * or with status {@code 400 (Bad Request)} if the ligneTransfertStockDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ligneTransfertStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LigneTransfertStockDTO> updateLigneTransfertStock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LigneTransfertStockDTO ligneTransfertStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LigneTransfertStock : {}, {}", id, ligneTransfertStockDTO);
        if (ligneTransfertStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneTransfertStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneTransfertStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ligneTransfertStockDTO = ligneTransfertStockService.update(ligneTransfertStockDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneTransfertStockDTO.getId().toString()))
            .body(ligneTransfertStockDTO);
    }

    /**
     * {@code PATCH  /ligne-transfert-stocks/:id} : Partial updates given fields of an existing ligneTransfertStock, field will ignore if it is null
     *
     * @param id the id of the ligneTransfertStockDTO to save.
     * @param ligneTransfertStockDTO the ligneTransfertStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneTransfertStockDTO,
     * or with status {@code 400 (Bad Request)} if the ligneTransfertStockDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ligneTransfertStockDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ligneTransfertStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LigneTransfertStockDTO> partialUpdateLigneTransfertStock(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LigneTransfertStockDTO ligneTransfertStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LigneTransfertStock partially : {}, {}", id, ligneTransfertStockDTO);
        if (ligneTransfertStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneTransfertStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneTransfertStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LigneTransfertStockDTO> result = ligneTransfertStockService.partialUpdate(ligneTransfertStockDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneTransfertStockDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ligne-transfert-stocks} : get all the Ligne Transfert Stocks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ligne Transfert Stocks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LigneTransfertStockDTO>> getAllLigneTransfertStocks(LigneTransfertStockCriteria criteria) {
        LOG.debug("REST request to get LigneTransfertStocks by criteria: {}", criteria);

        List<LigneTransfertStockDTO> entityList = ligneTransfertStockQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /ligne-transfert-stocks/count} : count all the ligneTransfertStocks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countLigneTransfertStocks(LigneTransfertStockCriteria criteria) {
        LOG.debug("REST request to count LigneTransfertStocks by criteria: {}", criteria);
        return ResponseEntity.ok().body(ligneTransfertStockQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ligne-transfert-stocks/:id} : get the "id" ligneTransfertStock.
     *
     * @param id the id of the ligneTransfertStockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ligneTransfertStockDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LigneTransfertStockDTO> getLigneTransfertStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LigneTransfertStock : {}", id);
        Optional<LigneTransfertStockDTO> ligneTransfertStockDTO = ligneTransfertStockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ligneTransfertStockDTO);
    }

    /**
     * {@code DELETE  /ligne-transfert-stocks/:id} : delete the "id" ligneTransfertStock.
     *
     * @param id the id of the ligneTransfertStockDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLigneTransfertStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LigneTransfertStock : {}", id);
        ligneTransfertStockService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
