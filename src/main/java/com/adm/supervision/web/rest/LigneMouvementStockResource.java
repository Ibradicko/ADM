package com.adm.supervision.web.rest;

import com.adm.supervision.repository.LigneMouvementStockRepository;
import com.adm.supervision.service.LigneMouvementStockQueryService;
import com.adm.supervision.service.LigneMouvementStockService;
import com.adm.supervision.service.criteria.LigneMouvementStockCriteria;
import com.adm.supervision.service.dto.LigneMouvementStockDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.LigneMouvementStock}.
 */
@RestController
@RequestMapping("/api/ligne-mouvement-stocks")
@PreAuthorize("@businessAuthorizationService.canReadStock()")
public class LigneMouvementStockResource {

    private static final Logger LOG = LoggerFactory.getLogger(LigneMouvementStockResource.class);

    private static final String ENTITY_NAME = "ligneMouvementStock";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final LigneMouvementStockService ligneMouvementStockService;

    private final LigneMouvementStockRepository ligneMouvementStockRepository;

    private final LigneMouvementStockQueryService ligneMouvementStockQueryService;

    public LigneMouvementStockResource(
        LigneMouvementStockService ligneMouvementStockService,
        LigneMouvementStockRepository ligneMouvementStockRepository,
        LigneMouvementStockQueryService ligneMouvementStockQueryService
    ) {
        this.ligneMouvementStockService = ligneMouvementStockService;
        this.ligneMouvementStockRepository = ligneMouvementStockRepository;
        this.ligneMouvementStockQueryService = ligneMouvementStockQueryService;
    }

    /**
     * {@code POST  /ligne-mouvement-stocks} : Create a new ligneMouvementStock.
     *
     * @param ligneMouvementStockDTO the ligneMouvementStockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ligneMouvementStockDTO, or with status {@code 400 (Bad Request)} if the ligneMouvementStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LigneMouvementStockDTO> createLigneMouvementStock(
        @Valid @RequestBody LigneMouvementStockDTO ligneMouvementStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save LigneMouvementStock : {}", ligneMouvementStockDTO);
        if (ligneMouvementStockDTO.getId() != null) {
            throw new BadRequestAlertException("A new ligneMouvementStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ligneMouvementStockDTO = ligneMouvementStockService.save(ligneMouvementStockDTO);
        return ResponseEntity.created(new URI("/api/ligne-mouvement-stocks/" + ligneMouvementStockDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ligneMouvementStockDTO.getId().toString()))
            .body(ligneMouvementStockDTO);
    }

    /**
     * {@code PUT  /ligne-mouvement-stocks/:id} : Updates an existing ligneMouvementStock.
     *
     * @param id the id of the ligneMouvementStockDTO to save.
     * @param ligneMouvementStockDTO the ligneMouvementStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneMouvementStockDTO,
     * or with status {@code 400 (Bad Request)} if the ligneMouvementStockDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ligneMouvementStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LigneMouvementStockDTO> updateLigneMouvementStock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LigneMouvementStockDTO ligneMouvementStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LigneMouvementStock : {}, {}", id, ligneMouvementStockDTO);
        if (ligneMouvementStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneMouvementStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneMouvementStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ligneMouvementStockDTO = ligneMouvementStockService.update(ligneMouvementStockDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneMouvementStockDTO.getId().toString()))
            .body(ligneMouvementStockDTO);
    }

    /**
     * {@code PATCH  /ligne-mouvement-stocks/:id} : Partial updates given fields of an existing ligneMouvementStock, field will ignore if it is null
     *
     * @param id the id of the ligneMouvementStockDTO to save.
     * @param ligneMouvementStockDTO the ligneMouvementStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneMouvementStockDTO,
     * or with status {@code 400 (Bad Request)} if the ligneMouvementStockDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ligneMouvementStockDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ligneMouvementStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LigneMouvementStockDTO> partialUpdateLigneMouvementStock(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LigneMouvementStockDTO ligneMouvementStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LigneMouvementStock partially : {}, {}", id, ligneMouvementStockDTO);
        if (ligneMouvementStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneMouvementStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneMouvementStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LigneMouvementStockDTO> result = ligneMouvementStockService.partialUpdate(ligneMouvementStockDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneMouvementStockDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ligne-mouvement-stocks} : get all the Ligne Mouvement Stocks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ligne Mouvement Stocks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LigneMouvementStockDTO>> getAllLigneMouvementStocks(
        LigneMouvementStockCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get LigneMouvementStocks by criteria: {}", criteria);

        Page<LigneMouvementStockDTO> page = ligneMouvementStockQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ligne-mouvement-stocks/count} : count all the ligneMouvementStocks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countLigneMouvementStocks(LigneMouvementStockCriteria criteria) {
        LOG.debug("REST request to count LigneMouvementStocks by criteria: {}", criteria);
        return ResponseEntity.ok().body(ligneMouvementStockQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ligne-mouvement-stocks/:id} : get the "id" ligneMouvementStock.
     *
     * @param id the id of the ligneMouvementStockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ligneMouvementStockDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LigneMouvementStockDTO> getLigneMouvementStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LigneMouvementStock : {}", id);
        Optional<LigneMouvementStockDTO> ligneMouvementStockDTO = ligneMouvementStockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ligneMouvementStockDTO);
    }

    /**
     * {@code DELETE  /ligne-mouvement-stocks/:id} : delete the "id" ligneMouvementStock.
     *
     * @param id the id of the ligneMouvementStockDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLigneMouvementStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LigneMouvementStock : {}", id);
        ligneMouvementStockService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
