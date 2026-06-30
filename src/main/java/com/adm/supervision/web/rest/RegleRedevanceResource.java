package com.adm.supervision.web.rest;

import com.adm.supervision.repository.RegleRedevanceRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.RegleRedevanceQueryService;
import com.adm.supervision.service.RegleRedevanceService;
import com.adm.supervision.service.ResourceBoutiqueSecurityService;
import com.adm.supervision.service.criteria.RegleRedevanceCriteria;
import com.adm.supervision.service.dto.RegleRedevanceDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.RegleRedevance}.
 */
@RestController
@RequestMapping("/api/regle-redevances")
public class RegleRedevanceResource {

    private static final Logger LOG = LoggerFactory.getLogger(RegleRedevanceResource.class);

    private static final String ENTITY_NAME = "regleRedevance";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final RegleRedevanceService regleRedevanceService;

    private final RegleRedevanceRepository regleRedevanceRepository;

    private final RegleRedevanceQueryService regleRedevanceQueryService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    private final ResourceBoutiqueSecurityService resourceBoutiqueSecurityService;

    public RegleRedevanceResource(
        RegleRedevanceService regleRedevanceService,
        RegleRedevanceRepository regleRedevanceRepository,
        RegleRedevanceQueryService regleRedevanceQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService,
        ResourceBoutiqueSecurityService resourceBoutiqueSecurityService
    ) {
        this.regleRedevanceService = regleRedevanceService;
        this.regleRedevanceRepository = regleRedevanceRepository;
        this.regleRedevanceQueryService = regleRedevanceQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
        this.resourceBoutiqueSecurityService = resourceBoutiqueSecurityService;
    }

    /**
     * {@code POST  /regle-redevances} : Create a new regleRedevance.
     *
     * @param regleRedevanceDTO the regleRedevanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new regleRedevanceDTO, or with status {@code 400 (Bad Request)} if the regleRedevance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageRoyalties()")
    public ResponseEntity<RegleRedevanceDTO> createRegleRedevance(@Valid @RequestBody RegleRedevanceDTO regleRedevanceDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save RegleRedevance : {}", regleRedevanceDTO);
        if (regleRedevanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new regleRedevance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        regleRedevanceDTO = regleRedevanceService.save(regleRedevanceDTO);
        return ResponseEntity.created(new URI("/api/regle-redevances/" + regleRedevanceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, regleRedevanceDTO.getId().toString()))
            .body(regleRedevanceDTO);
    }

    /**
     * {@code PUT  /regle-redevances/:id} : Updates an existing regleRedevance.
     *
     * @param id the id of the regleRedevanceDTO to save.
     * @param regleRedevanceDTO the regleRedevanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated regleRedevanceDTO,
     * or with status {@code 400 (Bad Request)} if the regleRedevanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the regleRedevanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageRoyalties()")
    public ResponseEntity<RegleRedevanceDTO> updateRegleRedevance(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RegleRedevanceDTO regleRedevanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RegleRedevance : {}, {}", id, regleRedevanceDTO);
        if (regleRedevanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, regleRedevanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!regleRedevanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        regleRedevanceDTO = regleRedevanceService.update(regleRedevanceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, regleRedevanceDTO.getId().toString()))
            .body(regleRedevanceDTO);
    }

    /**
     * {@code PATCH  /regle-redevances/:id} : Partial updates given fields of an existing regleRedevance, field will ignore if it is null
     *
     * @param id the id of the regleRedevanceDTO to save.
     * @param regleRedevanceDTO the regleRedevanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated regleRedevanceDTO,
     * or with status {@code 400 (Bad Request)} if the regleRedevanceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the regleRedevanceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the regleRedevanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageRoyalties()")
    public ResponseEntity<RegleRedevanceDTO> partialUpdateRegleRedevance(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RegleRedevanceDTO regleRedevanceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RegleRedevance partially : {}, {}", id, regleRedevanceDTO);
        if (regleRedevanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, regleRedevanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!regleRedevanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RegleRedevanceDTO> result = regleRedevanceService.partialUpdate(regleRedevanceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, regleRedevanceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /regle-redevances} : get all the Regle Redevances.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Regle Redevances in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canReadRoyalties()")
    public ResponseEntity<List<RegleRedevanceDTO>> getAllRegleRedevances(
        RegleRedevanceCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get RegleRedevances by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux regles de redevance demandees")
        );

        Page<RegleRedevanceDTO> page = regleRedevanceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /regle-redevances/count} : count all the regleRedevances.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canReadRoyalties()")
    public ResponseEntity<Long> countRegleRedevances(RegleRedevanceCriteria criteria) {
        LOG.debug("REST request to count RegleRedevances by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux regles de redevance demandees")
        );
        return ResponseEntity.ok().body(regleRedevanceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /regle-redevances/:id} : get the "id" regleRedevance.
     *
     * @param id the id of the regleRedevanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the regleRedevanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canReadRoyalties()")
    public ResponseEntity<RegleRedevanceDTO> getRegleRedevance(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RegleRedevance : {}", id);
        resourceBoutiqueSecurityService.assertRegleRedevance(id);
        Optional<RegleRedevanceDTO> regleRedevanceDTO = regleRedevanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(regleRedevanceDTO);
    }

    /**
     * {@code DELETE  /regle-redevances/:id} : delete the "id" regleRedevance.
     *
     * @param id the id of the regleRedevanceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageRoyalties()")
    public ResponseEntity<Void> deleteRegleRedevance(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RegleRedevance : {}", id);
        regleRedevanceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
