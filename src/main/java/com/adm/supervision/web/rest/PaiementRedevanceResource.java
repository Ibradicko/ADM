package com.adm.supervision.web.rest;

import com.adm.supervision.repository.PaiementRedevanceRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.PaiementRedevanceQueryService;
import com.adm.supervision.service.PaiementRedevanceService;
import com.adm.supervision.service.criteria.PaiementRedevanceCriteria;
import com.adm.supervision.service.dto.PaiementRedevanceDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.PaiementRedevance}.
 */
@RestController
@RequestMapping("/api/paiement-redevances")
public class PaiementRedevanceResource {

    private static final Logger LOG = LoggerFactory.getLogger(PaiementRedevanceResource.class);

    private static final String ENTITY_NAME = "paiementRedevance";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final PaiementRedevanceService paiementRedevanceService;

    private final PaiementRedevanceRepository paiementRedevanceRepository;

    private final PaiementRedevanceQueryService paiementRedevanceQueryService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public PaiementRedevanceResource(
        PaiementRedevanceService paiementRedevanceService,
        PaiementRedevanceRepository paiementRedevanceRepository,
        PaiementRedevanceQueryService paiementRedevanceQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.paiementRedevanceService = paiementRedevanceService;
        this.paiementRedevanceRepository = paiementRedevanceRepository;
        this.paiementRedevanceQueryService = paiementRedevanceQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * {@code POST  /paiement-redevances} : Create a new paiementRedevance.
     *
     * @param paiementRedevanceDTO the paiementRedevanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paiementRedevanceDTO, or with status {@code 400 (Bad Request)} if the paiementRedevance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageRoyalties()")
    public ResponseEntity<PaiementRedevanceDTO> createPaiementRedevance(@Valid @RequestBody PaiementRedevanceDTO paiementRedevanceDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PaiementRedevance : {}", paiementRedevanceDTO);
        if (paiementRedevanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new paiementRedevance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        paiementRedevanceDTO = paiementRedevanceService.save(paiementRedevanceDTO);
        return ResponseEntity.created(new URI("/api/paiement-redevances/" + paiementRedevanceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, paiementRedevanceDTO.getId().toString()))
            .body(paiementRedevanceDTO);
    }

    /**
     * {@code PUT  /paiement-redevances/:id} : Updates an existing paiementRedevance.
     *
     * @param id the id of the paiementRedevanceDTO to save.
     * @param paiementRedevanceDTO the paiementRedevanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paiementRedevanceDTO,
     * or with status {@code 400 (Bad Request)} if the paiementRedevanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paiementRedevanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageRoyalties()")
    public ResponseEntity<PaiementRedevanceDTO> updatePaiementRedevance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PaiementRedevanceDTO paiementRedevanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PaiementRedevance : {}, {}", id, paiementRedevanceDTO);
        if (paiementRedevanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paiementRedevanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paiementRedevanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        paiementRedevanceDTO = paiementRedevanceService.update(paiementRedevanceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paiementRedevanceDTO.getId().toString()))
            .body(paiementRedevanceDTO);
    }

    /**
     * {@code PATCH  /paiement-redevances/:id} : Partial updates given fields of an existing paiementRedevance, field will ignore if it is null
     *
     * @param id the id of the paiementRedevanceDTO to save.
     * @param paiementRedevanceDTO the paiementRedevanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paiementRedevanceDTO,
     * or with status {@code 400 (Bad Request)} if the paiementRedevanceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the paiementRedevanceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the paiementRedevanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageRoyalties()")
    public ResponseEntity<PaiementRedevanceDTO> partialUpdatePaiementRedevance(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PaiementRedevanceDTO paiementRedevanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PaiementRedevance partially : {}, {}", id, paiementRedevanceDTO);
        if (paiementRedevanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paiementRedevanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paiementRedevanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PaiementRedevanceDTO> result = paiementRedevanceService.partialUpdate(paiementRedevanceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paiementRedevanceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /paiement-redevances} : get all the Paiement Redevances.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Paiement Redevances in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canReadRoyalties()")
    public ResponseEntity<List<PaiementRedevanceDTO>> getAllPaiementRedevances(PaiementRedevanceCriteria criteria) {
        LOG.debug("REST request to get PaiementRedevances by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux paiements de redevance demandes")
        );

        List<PaiementRedevanceDTO> entityList = paiementRedevanceQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /paiement-redevances/count} : count all the paiementRedevances.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canReadRoyalties()")
    public ResponseEntity<Long> countPaiementRedevances(PaiementRedevanceCriteria criteria) {
        LOG.debug("REST request to count PaiementRedevances by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux paiements de redevance demandes")
        );
        return ResponseEntity.ok().body(paiementRedevanceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /paiement-redevances/:id} : get the "id" paiementRedevance.
     *
     * @param id the id of the paiementRedevanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paiementRedevanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canReadRoyalties()")
    public ResponseEntity<PaiementRedevanceDTO> getPaiementRedevance(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PaiementRedevance : {}", id);
        Optional<PaiementRedevanceDTO> paiementRedevanceDTO = paiementRedevanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paiementRedevanceDTO);
    }

    /**
     * {@code DELETE  /paiement-redevances/:id} : delete the "id" paiementRedevance.
     *
     * @param id the id of the paiementRedevanceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageRoyalties()")
    public ResponseEntity<Void> deletePaiementRedevance(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PaiementRedevance : {}", id);
        paiementRedevanceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
