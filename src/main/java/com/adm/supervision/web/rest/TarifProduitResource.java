package com.adm.supervision.web.rest;

import com.adm.supervision.repository.TarifProduitRepository;
import com.adm.supervision.service.TarifProduitQueryService;
import com.adm.supervision.service.TarifProduitService;
import com.adm.supervision.service.criteria.TarifProduitCriteria;
import com.adm.supervision.service.dto.TarifProduitDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.TarifProduit}.
 */
@RestController
@RequestMapping("/api/tarif-produits")
@PreAuthorize("@businessAuthorizationService.canManageCatalogue()")
public class TarifProduitResource {

    private static final Logger LOG = LoggerFactory.getLogger(TarifProduitResource.class);

    private static final String ENTITY_NAME = "tarifProduit";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final TarifProduitService tarifProduitService;

    private final TarifProduitRepository tarifProduitRepository;

    private final TarifProduitQueryService tarifProduitQueryService;

    public TarifProduitResource(
        TarifProduitService tarifProduitService,
        TarifProduitRepository tarifProduitRepository,
        TarifProduitQueryService tarifProduitQueryService
    ) {
        this.tarifProduitService = tarifProduitService;
        this.tarifProduitRepository = tarifProduitRepository;
        this.tarifProduitQueryService = tarifProduitQueryService;
    }

    /**
     * {@code POST  /tarif-produits} : Create a new tarifProduit.
     *
     * @param tarifProduitDTO the tarifProduitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tarifProduitDTO, or with status {@code 400 (Bad Request)} if the tarifProduit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TarifProduitDTO> createTarifProduit(@Valid @RequestBody TarifProduitDTO tarifProduitDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TarifProduit : {}", tarifProduitDTO);
        if (tarifProduitDTO.getId() != null) {
            throw new BadRequestAlertException("A new tarifProduit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tarifProduitDTO = tarifProduitService.save(tarifProduitDTO);
        return ResponseEntity.created(new URI("/api/tarif-produits/" + tarifProduitDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, tarifProduitDTO.getId().toString()))
            .body(tarifProduitDTO);
    }

    /**
     * {@code PUT  /tarif-produits/:id} : Updates an existing tarifProduit.
     *
     * @param id the id of the tarifProduitDTO to save.
     * @param tarifProduitDTO the tarifProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tarifProduitDTO,
     * or with status {@code 400 (Bad Request)} if the tarifProduitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tarifProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TarifProduitDTO> updateTarifProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TarifProduitDTO tarifProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TarifProduit : {}, {}", id, tarifProduitDTO);
        if (tarifProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tarifProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tarifProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tarifProduitDTO = tarifProduitService.update(tarifProduitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tarifProduitDTO.getId().toString()))
            .body(tarifProduitDTO);
    }

    /**
     * {@code PATCH  /tarif-produits/:id} : Partial updates given fields of an existing tarifProduit, field will ignore if it is null
     *
     * @param id the id of the tarifProduitDTO to save.
     * @param tarifProduitDTO the tarifProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tarifProduitDTO,
     * or with status {@code 400 (Bad Request)} if the tarifProduitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tarifProduitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tarifProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TarifProduitDTO> partialUpdateTarifProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TarifProduitDTO tarifProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TarifProduit partially : {}, {}", id, tarifProduitDTO);
        if (tarifProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tarifProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tarifProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TarifProduitDTO> result = tarifProduitService.partialUpdate(tarifProduitDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tarifProduitDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tarif-produits} : get all the Tarif Produits.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Tarif Produits in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TarifProduitDTO>> getAllTarifProduits(
        TarifProduitCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TarifProduits by criteria: {}", criteria);

        Page<TarifProduitDTO> page = tarifProduitQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tarif-produits/count} : count all the tarifProduits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTarifProduits(TarifProduitCriteria criteria) {
        LOG.debug("REST request to count TarifProduits by criteria: {}", criteria);
        return ResponseEntity.ok().body(tarifProduitQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tarif-produits/:id} : get the "id" tarifProduit.
     *
     * @param id the id of the tarifProduitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tarifProduitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TarifProduitDTO> getTarifProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TarifProduit : {}", id);
        Optional<TarifProduitDTO> tarifProduitDTO = tarifProduitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tarifProduitDTO);
    }

    /**
     * {@code DELETE  /tarif-produits/:id} : delete the "id" tarifProduit.
     *
     * @param id the id of the tarifProduitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTarifProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TarifProduit : {}", id);
        tarifProduitService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
