package com.adm.supervision.web.rest;

import com.adm.supervision.repository.TransfertStockRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.TransfertStockQueryService;
import com.adm.supervision.service.TransfertStockService;
import com.adm.supervision.service.criteria.TransfertStockCriteria;
import com.adm.supervision.service.dto.TransfertStockDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.TransfertStock}.
 */
@RestController
@RequestMapping("/api/transfert-stocks")
public class TransfertStockResource {

    private static final Logger LOG = LoggerFactory.getLogger(TransfertStockResource.class);

    private static final String ENTITY_NAME = "transfertStock";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final TransfertStockService transfertStockService;

    private final TransfertStockRepository transfertStockRepository;

    private final TransfertStockQueryService transfertStockQueryService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public TransfertStockResource(
        TransfertStockService transfertStockService,
        TransfertStockRepository transfertStockRepository,
        TransfertStockQueryService transfertStockQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.transfertStockService = transfertStockService;
        this.transfertStockRepository = transfertStockRepository;
        this.transfertStockQueryService = transfertStockQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * {@code POST  /transfert-stocks} : Create a new transfertStock.
     *
     * @param transfertStockDTO the transfertStockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transfertStockDTO, or with status {@code 400 (Bad Request)} if the transfertStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageStock()")
    public ResponseEntity<TransfertStockDTO> createTransfertStock(@Valid @RequestBody TransfertStockDTO transfertStockDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TransfertStock : {}", transfertStockDTO);
        if (transfertStockDTO.getId() != null) {
            throw new BadRequestAlertException("A new transfertStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        transfertStockDTO = transfertStockService.save(transfertStockDTO);
        return ResponseEntity.created(new URI("/api/transfert-stocks/" + transfertStockDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, transfertStockDTO.getId().toString()))
            .body(transfertStockDTO);
    }

    /**
     * {@code PUT  /transfert-stocks/:id} : Updates an existing transfertStock.
     *
     * @param id the id of the transfertStockDTO to save.
     * @param transfertStockDTO the transfertStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transfertStockDTO,
     * or with status {@code 400 (Bad Request)} if the transfertStockDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transfertStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessTransfertStock(#id)")
    public ResponseEntity<TransfertStockDTO> updateTransfertStock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransfertStockDTO transfertStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TransfertStock : {}, {}", id, transfertStockDTO);
        if (transfertStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transfertStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transfertStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        transfertStockDTO = transfertStockService.update(transfertStockDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transfertStockDTO.getId().toString()))
            .body(transfertStockDTO);
    }

    /**
     * {@code PATCH  /transfert-stocks/:id} : Partial updates given fields of an existing transfertStock, field will ignore if it is null
     *
     * @param id the id of the transfertStockDTO to save.
     * @param transfertStockDTO the transfertStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transfertStockDTO,
     * or with status {@code 400 (Bad Request)} if the transfertStockDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transfertStockDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transfertStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessTransfertStock(#id)")
    public ResponseEntity<TransfertStockDTO> partialUpdateTransfertStock(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransfertStockDTO transfertStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TransfertStock partially : {}, {}", id, transfertStockDTO);
        if (transfertStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transfertStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transfertStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransfertStockDTO> result = transfertStockService.partialUpdate(transfertStockDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transfertStockDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transfert-stocks} : get all the Transfert Stocks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Transfert Stocks in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canReadStock()")
    public ResponseEntity<List<TransfertStockDTO>> getAllTransfertStocks(
        TransfertStockCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TransfertStocks by criteria: {}", criteria);
        criteria.setBoutiqueOrigineId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueOrigineId(), "Acces refuse aux transferts demandes")
        );
        criteria.setBoutiqueDestinationId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueDestinationId(), "Acces refuse aux transferts demandes")
        );

        Page<TransfertStockDTO> page = transfertStockQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transfert-stocks/count} : count all the transfertStocks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canReadStock()")
    public ResponseEntity<Long> countTransfertStocks(TransfertStockCriteria criteria) {
        LOG.debug("REST request to count TransfertStocks by criteria: {}", criteria);
        criteria.setBoutiqueOrigineId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueOrigineId(), "Acces refuse aux transferts demandes")
        );
        criteria.setBoutiqueDestinationId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueDestinationId(), "Acces refuse aux transferts demandes")
        );
        return ResponseEntity.ok().body(transfertStockQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /transfert-stocks/:id} : get the "id" transfertStock.
     *
     * @param id the id of the transfertStockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transfertStockDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canReadStock() and @resourceBoutiqueSecurityService.canAccessTransfertStock(#id)")
    public ResponseEntity<TransfertStockDTO> getTransfertStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TransfertStock : {}", id);
        Optional<TransfertStockDTO> transfertStockDTO = transfertStockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transfertStockDTO);
    }

    /**
     * {@code DELETE  /transfert-stocks/:id} : delete the "id" transfertStock.
     *
     * @param id the id of the transfertStockDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessTransfertStock(#id)")
    public ResponseEntity<Void> deleteTransfertStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TransfertStock : {}", id);
        transfertStockService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
