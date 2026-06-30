package com.adm.supervision.web.rest;

import com.adm.supervision.repository.ReceptionProduitRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.ReceptionProduitQueryService;
import com.adm.supervision.service.ReceptionProduitService;
import com.adm.supervision.service.criteria.ReceptionProduitCriteria;
import com.adm.supervision.service.dto.ReceptionProduitDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.ReceptionProduit}.
 */
@RestController
@RequestMapping("/api/reception-produits")
public class ReceptionProduitResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReceptionProduitResource.class);

    private static final String ENTITY_NAME = "receptionProduit";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final ReceptionProduitService receptionProduitService;

    private final ReceptionProduitRepository receptionProduitRepository;

    private final ReceptionProduitQueryService receptionProduitQueryService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public ReceptionProduitResource(
        ReceptionProduitService receptionProduitService,
        ReceptionProduitRepository receptionProduitRepository,
        ReceptionProduitQueryService receptionProduitQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.receptionProduitService = receptionProduitService;
        this.receptionProduitRepository = receptionProduitRepository;
        this.receptionProduitQueryService = receptionProduitQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * {@code POST  /reception-produits} : Create a new receptionProduit.
     *
     * @param receptionProduitDTO the receptionProduitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new receptionProduitDTO, or with status {@code 400 (Bad Request)} if the receptionProduit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize(
        "@businessAuthorizationService.canManageStock() and @businessAuthorizationService.canAccessBoutique(#receptionProduitDTO.boutique.id)"
    )
    public ResponseEntity<ReceptionProduitDTO> createReceptionProduit(@Valid @RequestBody ReceptionProduitDTO receptionProduitDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ReceptionProduit : {}", receptionProduitDTO);
        if (receptionProduitDTO.getId() != null) {
            throw new BadRequestAlertException("A new receptionProduit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        receptionProduitDTO = receptionProduitService.save(receptionProduitDTO);
        return ResponseEntity.created(new URI("/api/reception-produits/" + receptionProduitDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, receptionProduitDTO.getId().toString()))
            .body(receptionProduitDTO);
    }

    /**
     * {@code PUT  /reception-produits/:id} : Updates an existing receptionProduit.
     *
     * @param id the id of the receptionProduitDTO to save.
     * @param receptionProduitDTO the receptionProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated receptionProduitDTO,
     * or with status {@code 400 (Bad Request)} if the receptionProduitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the receptionProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize(
        "@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessReceptionProduit(#id) and @businessAuthorizationService.canAccessBoutique(#receptionProduitDTO.boutique.id)"
    )
    public ResponseEntity<ReceptionProduitDTO> updateReceptionProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReceptionProduitDTO receptionProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReceptionProduit : {}, {}", id, receptionProduitDTO);
        if (receptionProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, receptionProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!receptionProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        receptionProduitDTO = receptionProduitService.update(receptionProduitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, receptionProduitDTO.getId().toString()))
            .body(receptionProduitDTO);
    }

    /**
     * {@code PATCH  /reception-produits/:id} : Partial updates given fields of an existing receptionProduit, field will ignore if it is null
     *
     * @param id the id of the receptionProduitDTO to save.
     * @param receptionProduitDTO the receptionProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated receptionProduitDTO,
     * or with status {@code 400 (Bad Request)} if the receptionProduitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the receptionProduitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the receptionProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize(
        "@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessReceptionProduit(#id) and (#receptionProduitDTO.boutique == null or @businessAuthorizationService.canAccessBoutique(#receptionProduitDTO.boutique.id))"
    )
    public ResponseEntity<ReceptionProduitDTO> partialUpdateReceptionProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReceptionProduitDTO receptionProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReceptionProduit partially : {}, {}", id, receptionProduitDTO);
        if (receptionProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, receptionProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!receptionProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReceptionProduitDTO> result = receptionProduitService.partialUpdate(receptionProduitDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, receptionProduitDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /reception-produits} : get all the Reception Produits.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Reception Produits in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canReadStock()")
    public ResponseEntity<List<ReceptionProduitDTO>> getAllReceptionProduits(
        ReceptionProduitCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ReceptionProduits by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux receptions demandees")
        );

        Page<ReceptionProduitDTO> page = receptionProduitQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /reception-produits/count} : count all the receptionProduits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canReadStock()")
    public ResponseEntity<Long> countReceptionProduits(ReceptionProduitCriteria criteria) {
        LOG.debug("REST request to count ReceptionProduits by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux receptions demandees")
        );
        return ResponseEntity.ok().body(receptionProduitQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /reception-produits/:id} : get the "id" receptionProduit.
     *
     * @param id the id of the receptionProduitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the receptionProduitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canReadStock() and @resourceBoutiqueSecurityService.canAccessReceptionProduit(#id)")
    public ResponseEntity<ReceptionProduitDTO> getReceptionProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReceptionProduit : {}", id);
        Optional<ReceptionProduitDTO> receptionProduitDTO = receptionProduitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(receptionProduitDTO);
    }

    /**
     * {@code DELETE  /reception-produits/:id} : delete the "id" receptionProduit.
     *
     * @param id the id of the receptionProduitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageStock() and @resourceBoutiqueSecurityService.canAccessReceptionProduit(#id)")
    public ResponseEntity<Void> deleteReceptionProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReceptionProduit : {}", id);
        receptionProduitService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
