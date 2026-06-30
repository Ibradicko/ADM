package com.adm.supervision.web.rest;

import com.adm.supervision.repository.MouvementStockRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.MouvementStockQueryService;
import com.adm.supervision.service.MouvementStockService;
import com.adm.supervision.service.criteria.MouvementStockCriteria;
import com.adm.supervision.service.dto.MouvementStockDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.MouvementStock}.
 */
@RestController
@RequestMapping("/api/mouvement-stocks")
public class MouvementStockResource {

    private static final Logger LOG = LoggerFactory.getLogger(MouvementStockResource.class);

    private static final String ENTITY_NAME = "mouvementStock";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final MouvementStockService mouvementStockService;

    private final MouvementStockRepository mouvementStockRepository;

    private final MouvementStockQueryService mouvementStockQueryService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public MouvementStockResource(
        MouvementStockService mouvementStockService,
        MouvementStockRepository mouvementStockRepository,
        MouvementStockQueryService mouvementStockQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.mouvementStockService = mouvementStockService;
        this.mouvementStockRepository = mouvementStockRepository;
        this.mouvementStockQueryService = mouvementStockQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * {@code POST  /mouvement-stocks} : Create a new mouvementStock.
     *
     * @param mouvementStockDTO the mouvementStockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mouvementStockDTO, or with status {@code 400 (Bad Request)} if the mouvementStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize(
        "@businessAuthorizationService.canManageStock() and @businessAuthorizationService.canAccessBoutique(#mouvementStockDTO.boutique.id)"
    )
    public ResponseEntity<MouvementStockDTO> createMouvementStock(@Valid @RequestBody MouvementStockDTO mouvementStockDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MouvementStock : {}", mouvementStockDTO);
        if (mouvementStockDTO.getId() != null) {
            throw new BadRequestAlertException("A new mouvementStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        mouvementStockDTO = mouvementStockService.save(mouvementStockDTO);
        return ResponseEntity.created(new URI("/api/mouvement-stocks/" + mouvementStockDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, mouvementStockDTO.getId().toString()))
            .body(mouvementStockDTO);
    }

    /**
     * {@code PUT  /mouvement-stocks/:id} : Updates an existing mouvementStock.
     *
     * @param id the id of the mouvementStockDTO to save.
     * @param mouvementStockDTO the mouvementStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mouvementStockDTO,
     * or with status {@code 400 (Bad Request)} if the mouvementStockDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mouvementStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize(
        "@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessMouvementStock(#id) and @businessAuthorizationService.canAccessBoutique(#mouvementStockDTO.boutique.id)"
    )
    public ResponseEntity<MouvementStockDTO> updateMouvementStock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MouvementStockDTO mouvementStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MouvementStock : {}, {}", id, mouvementStockDTO);
        if (mouvementStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mouvementStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mouvementStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        mouvementStockDTO = mouvementStockService.update(mouvementStockDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mouvementStockDTO.getId().toString()))
            .body(mouvementStockDTO);
    }

    /**
     * {@code PATCH  /mouvement-stocks/:id} : Partial updates given fields of an existing mouvementStock, field will ignore if it is null
     *
     * @param id the id of the mouvementStockDTO to save.
     * @param mouvementStockDTO the mouvementStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mouvementStockDTO,
     * or with status {@code 400 (Bad Request)} if the mouvementStockDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mouvementStockDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mouvementStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize(
        "@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessMouvementStock(#id) and (#mouvementStockDTO.boutique == null or @businessAuthorizationService.canAccessBoutique(#mouvementStockDTO.boutique.id))"
    )
    public ResponseEntity<MouvementStockDTO> partialUpdateMouvementStock(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MouvementStockDTO mouvementStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MouvementStock partially : {}, {}", id, mouvementStockDTO);
        if (mouvementStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mouvementStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mouvementStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MouvementStockDTO> result = mouvementStockService.partialUpdate(mouvementStockDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mouvementStockDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mouvement-stocks} : get all the Mouvement Stocks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mouvement Stocks in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canReadStock()")
    public ResponseEntity<List<MouvementStockDTO>> getAllMouvementStocks(
        MouvementStockCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get MouvementStocks by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux mouvements de stock demandes")
        );

        Page<MouvementStockDTO> page = mouvementStockQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /mouvement-stocks/count} : count all the mouvementStocks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canReadStock()")
    public ResponseEntity<Long> countMouvementStocks(MouvementStockCriteria criteria) {
        LOG.debug("REST request to count MouvementStocks by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux mouvements de stock demandes")
        );
        return ResponseEntity.ok().body(mouvementStockQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /mouvement-stocks/:id} : get the "id" mouvementStock.
     *
     * @param id the id of the mouvementStockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mouvementStockDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canReadStock() and @resourceBoutiqueSecurityService.canAccessMouvementStock(#id)")
    public ResponseEntity<MouvementStockDTO> getMouvementStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MouvementStock : {}", id);
        Optional<MouvementStockDTO> mouvementStockDTO = mouvementStockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mouvementStockDTO);
    }

    /**
     * {@code DELETE  /mouvement-stocks/:id} : delete the "id" mouvementStock.
     *
     * @param id the id of the mouvementStockDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessMouvementStock(#id)")
    public ResponseEntity<Void> deleteMouvementStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MouvementStock : {}", id);
        mouvementStockService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
