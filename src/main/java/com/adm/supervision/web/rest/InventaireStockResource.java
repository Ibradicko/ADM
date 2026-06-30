package com.adm.supervision.web.rest;

import com.adm.supervision.repository.InventaireStockRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.InventaireStockQueryService;
import com.adm.supervision.service.InventaireStockService;
import com.adm.supervision.service.criteria.InventaireStockCriteria;
import com.adm.supervision.service.dto.InventaireStockDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.InventaireStock}.
 */
@RestController
@RequestMapping("/api/inventaire-stocks")
public class InventaireStockResource {

    private static final Logger LOG = LoggerFactory.getLogger(InventaireStockResource.class);

    private static final String ENTITY_NAME = "inventaireStock";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final InventaireStockService inventaireStockService;

    private final InventaireStockRepository inventaireStockRepository;

    private final InventaireStockQueryService inventaireStockQueryService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public InventaireStockResource(
        InventaireStockService inventaireStockService,
        InventaireStockRepository inventaireStockRepository,
        InventaireStockQueryService inventaireStockQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.inventaireStockService = inventaireStockService;
        this.inventaireStockRepository = inventaireStockRepository;
        this.inventaireStockQueryService = inventaireStockQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * {@code POST  /inventaire-stocks} : Create a new inventaireStock.
     *
     * @param inventaireStockDTO the inventaireStockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new inventaireStockDTO, or with status {@code 400 (Bad Request)} if the inventaireStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageStock()")
    public ResponseEntity<InventaireStockDTO> createInventaireStock(@Valid @RequestBody InventaireStockDTO inventaireStockDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save InventaireStock : {}", inventaireStockDTO);
        if (inventaireStockDTO.getId() != null) {
            throw new BadRequestAlertException("A new inventaireStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        inventaireStockDTO = inventaireStockService.save(inventaireStockDTO);
        return ResponseEntity.created(new URI("/api/inventaire-stocks/" + inventaireStockDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, inventaireStockDTO.getId().toString()))
            .body(inventaireStockDTO);
    }

    /**
     * {@code PUT  /inventaire-stocks/:id} : Updates an existing inventaireStock.
     *
     * @param id the id of the inventaireStockDTO to save.
     * @param inventaireStockDTO the inventaireStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inventaireStockDTO,
     * or with status {@code 400 (Bad Request)} if the inventaireStockDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the inventaireStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessInventaireStock(#id)")
    public ResponseEntity<InventaireStockDTO> updateInventaireStock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InventaireStockDTO inventaireStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update InventaireStock : {}, {}", id, inventaireStockDTO);
        if (inventaireStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inventaireStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inventaireStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        inventaireStockDTO = inventaireStockService.update(inventaireStockDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inventaireStockDTO.getId().toString()))
            .body(inventaireStockDTO);
    }

    /**
     * {@code PATCH  /inventaire-stocks/:id} : Partial updates given fields of an existing inventaireStock, field will ignore if it is null
     *
     * @param id the id of the inventaireStockDTO to save.
     * @param inventaireStockDTO the inventaireStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inventaireStockDTO,
     * or with status {@code 400 (Bad Request)} if the inventaireStockDTO is not valid,
     * or with status {@code 404 (Not Found)} if the inventaireStockDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the inventaireStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessInventaireStock(#id)")
    public ResponseEntity<InventaireStockDTO> partialUpdateInventaireStock(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InventaireStockDTO inventaireStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update InventaireStock partially : {}, {}", id, inventaireStockDTO);
        if (inventaireStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inventaireStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inventaireStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InventaireStockDTO> result = inventaireStockService.partialUpdate(inventaireStockDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inventaireStockDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /inventaire-stocks} : get all the Inventaire Stocks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Inventaire Stocks in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canReadStock()")
    public ResponseEntity<List<InventaireStockDTO>> getAllInventaireStocks(
        InventaireStockCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get InventaireStocks by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux inventaires demandes")
        );

        Page<InventaireStockDTO> page = inventaireStockQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /inventaire-stocks/count} : count all the inventaireStocks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canReadStock()")
    public ResponseEntity<Long> countInventaireStocks(InventaireStockCriteria criteria) {
        LOG.debug("REST request to count InventaireStocks by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux inventaires demandes")
        );
        return ResponseEntity.ok().body(inventaireStockQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /inventaire-stocks/:id} : get the "id" inventaireStock.
     *
     * @param id the id of the inventaireStockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the inventaireStockDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canReadStock() and @resourceBoutiqueSecurityService.canAccessInventaireStock(#id)")
    public ResponseEntity<InventaireStockDTO> getInventaireStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get InventaireStock : {}", id);
        Optional<InventaireStockDTO> inventaireStockDTO = inventaireStockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(inventaireStockDTO);
    }

    /**
     * {@code DELETE  /inventaire-stocks/:id} : delete the "id" inventaireStock.
     *
     * @param id the id of the inventaireStockDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessInventaireStock(#id)")
    public ResponseEntity<Void> deleteInventaireStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete InventaireStock : {}", id);
        inventaireStockService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
