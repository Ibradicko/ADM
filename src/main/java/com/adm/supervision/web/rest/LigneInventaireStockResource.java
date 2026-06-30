package com.adm.supervision.web.rest;

import com.adm.supervision.repository.LigneInventaireStockRepository;
import com.adm.supervision.service.LigneInventaireStockQueryService;
import com.adm.supervision.service.LigneInventaireStockService;
import com.adm.supervision.service.criteria.LigneInventaireStockCriteria;
import com.adm.supervision.service.dto.LigneInventaireStockDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.LigneInventaireStock}.
 */
@RestController
@RequestMapping("/api/ligne-inventaire-stocks")
@PreAuthorize("@businessAuthorizationService.canReadStock()")
public class LigneInventaireStockResource {

    private static final Logger LOG = LoggerFactory.getLogger(LigneInventaireStockResource.class);

    private static final String ENTITY_NAME = "ligneInventaireStock";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final LigneInventaireStockService ligneInventaireStockService;

    private final LigneInventaireStockRepository ligneInventaireStockRepository;

    private final LigneInventaireStockQueryService ligneInventaireStockQueryService;

    public LigneInventaireStockResource(
        LigneInventaireStockService ligneInventaireStockService,
        LigneInventaireStockRepository ligneInventaireStockRepository,
        LigneInventaireStockQueryService ligneInventaireStockQueryService
    ) {
        this.ligneInventaireStockService = ligneInventaireStockService;
        this.ligneInventaireStockRepository = ligneInventaireStockRepository;
        this.ligneInventaireStockQueryService = ligneInventaireStockQueryService;
    }

    /**
     * {@code POST  /ligne-inventaire-stocks} : Create a new ligneInventaireStock.
     *
     * @param ligneInventaireStockDTO the ligneInventaireStockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ligneInventaireStockDTO, or with status {@code 400 (Bad Request)} if the ligneInventaireStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LigneInventaireStockDTO> createLigneInventaireStock(
        @Valid @RequestBody LigneInventaireStockDTO ligneInventaireStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save LigneInventaireStock : {}", ligneInventaireStockDTO);
        if (ligneInventaireStockDTO.getId() != null) {
            throw new BadRequestAlertException("A new ligneInventaireStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ligneInventaireStockDTO = ligneInventaireStockService.save(ligneInventaireStockDTO);
        return ResponseEntity.created(new URI("/api/ligne-inventaire-stocks/" + ligneInventaireStockDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ligneInventaireStockDTO.getId().toString()))
            .body(ligneInventaireStockDTO);
    }

    /**
     * {@code PUT  /ligne-inventaire-stocks/:id} : Updates an existing ligneInventaireStock.
     *
     * @param id the id of the ligneInventaireStockDTO to save.
     * @param ligneInventaireStockDTO the ligneInventaireStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneInventaireStockDTO,
     * or with status {@code 400 (Bad Request)} if the ligneInventaireStockDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ligneInventaireStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LigneInventaireStockDTO> updateLigneInventaireStock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LigneInventaireStockDTO ligneInventaireStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LigneInventaireStock : {}, {}", id, ligneInventaireStockDTO);
        if (ligneInventaireStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneInventaireStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneInventaireStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ligneInventaireStockDTO = ligneInventaireStockService.update(ligneInventaireStockDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneInventaireStockDTO.getId().toString()))
            .body(ligneInventaireStockDTO);
    }

    /**
     * {@code PATCH  /ligne-inventaire-stocks/:id} : Partial updates given fields of an existing ligneInventaireStock, field will ignore if it is null
     *
     * @param id the id of the ligneInventaireStockDTO to save.
     * @param ligneInventaireStockDTO the ligneInventaireStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneInventaireStockDTO,
     * or with status {@code 400 (Bad Request)} if the ligneInventaireStockDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ligneInventaireStockDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ligneInventaireStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LigneInventaireStockDTO> partialUpdateLigneInventaireStock(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LigneInventaireStockDTO ligneInventaireStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LigneInventaireStock partially : {}, {}", id, ligneInventaireStockDTO);
        if (ligneInventaireStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneInventaireStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneInventaireStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LigneInventaireStockDTO> result = ligneInventaireStockService.partialUpdate(ligneInventaireStockDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneInventaireStockDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ligne-inventaire-stocks} : get all the Ligne Inventaire Stocks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ligne Inventaire Stocks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LigneInventaireStockDTO>> getAllLigneInventaireStocks(LigneInventaireStockCriteria criteria) {
        LOG.debug("REST request to get LigneInventaireStocks by criteria: {}", criteria);

        List<LigneInventaireStockDTO> entityList = ligneInventaireStockQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /ligne-inventaire-stocks/count} : count all the ligneInventaireStocks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countLigneInventaireStocks(LigneInventaireStockCriteria criteria) {
        LOG.debug("REST request to count LigneInventaireStocks by criteria: {}", criteria);
        return ResponseEntity.ok().body(ligneInventaireStockQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ligne-inventaire-stocks/:id} : get the "id" ligneInventaireStock.
     *
     * @param id the id of the ligneInventaireStockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ligneInventaireStockDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LigneInventaireStockDTO> getLigneInventaireStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LigneInventaireStock : {}", id);
        Optional<LigneInventaireStockDTO> ligneInventaireStockDTO = ligneInventaireStockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ligneInventaireStockDTO);
    }

    /**
     * {@code DELETE  /ligne-inventaire-stocks/:id} : delete the "id" ligneInventaireStock.
     *
     * @param id the id of the ligneInventaireStockDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLigneInventaireStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LigneInventaireStock : {}", id);
        ligneInventaireStockService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
