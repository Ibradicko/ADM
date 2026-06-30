package com.adm.supervision.web.rest;

import com.adm.supervision.repository.DepotStockRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.DepotStockQueryService;
import com.adm.supervision.service.DepotStockService;
import com.adm.supervision.service.criteria.DepotStockCriteria;
import com.adm.supervision.service.dto.DepotStockDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.DepotStock}.
 */
@RestController
@RequestMapping("/api/depot-stocks")
@PreAuthorize("@businessAuthorizationService.canReadStock()")
public class DepotStockResource {

    private static final Logger LOG = LoggerFactory.getLogger(DepotStockResource.class);

    private static final String ENTITY_NAME = "depotStock";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final DepotStockService depotStockService;

    private final DepotStockRepository depotStockRepository;

    private final DepotStockQueryService depotStockQueryService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public DepotStockResource(
        DepotStockService depotStockService,
        DepotStockRepository depotStockRepository,
        DepotStockQueryService depotStockQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.depotStockService = depotStockService;
        this.depotStockRepository = depotStockRepository;
        this.depotStockQueryService = depotStockQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * {@code POST  /depot-stocks} : Create a new depotStock.
     *
     * @param depotStockDTO the depotStockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new depotStockDTO, or with status {@code 400 (Bad Request)} if the depotStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DepotStockDTO> createDepotStock(@Valid @RequestBody DepotStockDTO depotStockDTO) throws URISyntaxException {
        LOG.debug("REST request to save DepotStock : {}", depotStockDTO);
        if (depotStockDTO.getId() != null) {
            throw new BadRequestAlertException("A new depotStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        depotStockDTO = depotStockService.save(depotStockDTO);
        return ResponseEntity.created(new URI("/api/depot-stocks/" + depotStockDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, depotStockDTO.getId().toString()))
            .body(depotStockDTO);
    }

    /**
     * {@code PUT  /depot-stocks/:id} : Updates an existing depotStock.
     *
     * @param id the id of the depotStockDTO to save.
     * @param depotStockDTO the depotStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated depotStockDTO,
     * or with status {@code 400 (Bad Request)} if the depotStockDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the depotStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DepotStockDTO> updateDepotStock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DepotStockDTO depotStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DepotStock : {}, {}", id, depotStockDTO);
        if (depotStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, depotStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!depotStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        depotStockDTO = depotStockService.update(depotStockDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, depotStockDTO.getId().toString()))
            .body(depotStockDTO);
    }

    /**
     * {@code PATCH  /depot-stocks/:id} : Partial updates given fields of an existing depotStock, field will ignore if it is null
     *
     * @param id the id of the depotStockDTO to save.
     * @param depotStockDTO the depotStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated depotStockDTO,
     * or with status {@code 400 (Bad Request)} if the depotStockDTO is not valid,
     * or with status {@code 404 (Not Found)} if the depotStockDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the depotStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DepotStockDTO> partialUpdateDepotStock(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DepotStockDTO depotStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DepotStock partially : {}, {}", id, depotStockDTO);
        if (depotStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, depotStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!depotStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DepotStockDTO> result = depotStockService.partialUpdate(depotStockDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, depotStockDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /depot-stocks} : get all the Depot Stocks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Depot Stocks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DepotStockDTO>> getAllDepotStocks(DepotStockCriteria criteria) {
        LOG.debug("REST request to get DepotStocks by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux depots demandes")
        );
        List<DepotStockDTO> entityList = depotStockQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /depot-stocks/count} : count all the depotStocks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDepotStocks(DepotStockCriteria criteria) {
        LOG.debug("REST request to count DepotStocks by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux depots demandes")
        );
        return ResponseEntity.ok().body(depotStockQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /depot-stocks/:id} : get the "id" depotStock.
     *
     * @param id the id of the depotStockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the depotStockDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepotStockDTO> getDepotStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DepotStock : {}", id);
        Optional<DepotStockDTO> depotStockDTO = depotStockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(depotStockDTO);
    }

    /**
     * {@code DELETE  /depot-stocks/:id} : delete the "id" depotStock.
     *
     * @param id the id of the depotStockDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepotStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DepotStock : {}", id);
        depotStockService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
