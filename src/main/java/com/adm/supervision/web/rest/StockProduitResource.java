package com.adm.supervision.web.rest;

import com.adm.supervision.repository.StockProduitRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.StockProduitQueryService;
import com.adm.supervision.service.StockProduitService;
import com.adm.supervision.service.criteria.StockProduitCriteria;
import com.adm.supervision.service.dto.StockProduitDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.StockProduit}.
 */
@RestController
@RequestMapping("/api/stock-produits")
public class StockProduitResource {

    private static final Logger LOG = LoggerFactory.getLogger(StockProduitResource.class);

    private static final String ENTITY_NAME = "stockProduit";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final StockProduitService stockProduitService;

    private final StockProduitRepository stockProduitRepository;

    private final StockProduitQueryService stockProduitQueryService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public StockProduitResource(
        StockProduitService stockProduitService,
        StockProduitRepository stockProduitRepository,
        StockProduitQueryService stockProduitQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.stockProduitService = stockProduitService;
        this.stockProduitRepository = stockProduitRepository;
        this.stockProduitQueryService = stockProduitQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * {@code POST  /stock-produits} : Create a new stockProduit.
     *
     * @param stockProduitDTO the stockProduitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockProduitDTO, or with status {@code 400 (Bad Request)} if the stockProduit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageStock()")
    public ResponseEntity<StockProduitDTO> createStockProduit(@Valid @RequestBody StockProduitDTO stockProduitDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save StockProduit : {}", stockProduitDTO);
        if (stockProduitDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockProduit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stockProduitDTO = stockProduitService.save(stockProduitDTO);
        return ResponseEntity.created(new URI("/api/stock-produits/" + stockProduitDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, stockProduitDTO.getId().toString()))
            .body(stockProduitDTO);
    }

    /**
     * {@code PUT  /stock-produits/:id} : Updates an existing stockProduit.
     *
     * @param id the id of the stockProduitDTO to save.
     * @param stockProduitDTO the stockProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockProduitDTO,
     * or with status {@code 400 (Bad Request)} if the stockProduitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessStockProduit(#id)")
    public ResponseEntity<StockProduitDTO> updateStockProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockProduitDTO stockProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StockProduit : {}, {}", id, stockProduitDTO);
        if (stockProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stockProduitDTO = stockProduitService.update(stockProduitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockProduitDTO.getId().toString()))
            .body(stockProduitDTO);
    }

    /**
     * {@code PATCH  /stock-produits/:id} : Partial updates given fields of an existing stockProduit, field will ignore if it is null
     *
     * @param id the id of the stockProduitDTO to save.
     * @param stockProduitDTO the stockProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockProduitDTO,
     * or with status {@code 400 (Bad Request)} if the stockProduitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockProduitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessStockProduit(#id)")
    public ResponseEntity<StockProduitDTO> partialUpdateStockProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockProduitDTO stockProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StockProduit partially : {}, {}", id, stockProduitDTO);
        if (stockProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockProduitDTO> result = stockProduitService.partialUpdate(stockProduitDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockProduitDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-produits} : get all the Stock Produits.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Stock Produits in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canReadStock()")
    public ResponseEntity<List<StockProduitDTO>> getAllStockProduits(
        StockProduitCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get StockProduits by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux stocks demandes")
        );

        Page<StockProduitDTO> page = stockProduitQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stock-produits/count} : count all the stockProduits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canReadStock()")
    public ResponseEntity<Long> countStockProduits(StockProduitCriteria criteria) {
        LOG.debug("REST request to count StockProduits by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux stocks demandes")
        );
        return ResponseEntity.ok().body(stockProduitQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /stock-produits/:id} : get the "id" stockProduit.
     *
     * @param id the id of the stockProduitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockProduitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canReadStock() and @resourceBoutiqueSecurityService.canAccessStockProduit(#id)")
    public ResponseEntity<StockProduitDTO> getStockProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StockProduit : {}", id);
        Optional<StockProduitDTO> stockProduitDTO = stockProduitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockProduitDTO);
    }

    /**
     * {@code DELETE  /stock-produits/:id} : delete the "id" stockProduit.
     *
     * @param id the id of the stockProduitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessStockProduit(#id)")
    public ResponseEntity<Void> deleteStockProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StockProduit : {}", id);
        stockProduitService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
