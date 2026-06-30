package com.adm.supervision.web.rest;

import com.adm.supervision.repository.CodeBarresProduitRepository;
import com.adm.supervision.service.CodeBarresProduitQueryService;
import com.adm.supervision.service.CodeBarresProduitService;
import com.adm.supervision.service.criteria.CodeBarresProduitCriteria;
import com.adm.supervision.service.dto.CodeBarresProduitDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.CodeBarresProduit}.
 */
@RestController
@RequestMapping("/api/code-barres-produits")
public class CodeBarresProduitResource {

    private static final Logger LOG = LoggerFactory.getLogger(CodeBarresProduitResource.class);

    private static final String ENTITY_NAME = "codeBarresProduit";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final CodeBarresProduitService codeBarresProduitService;

    private final CodeBarresProduitRepository codeBarresProduitRepository;

    private final CodeBarresProduitQueryService codeBarresProduitQueryService;

    public CodeBarresProduitResource(
        CodeBarresProduitService codeBarresProduitService,
        CodeBarresProduitRepository codeBarresProduitRepository,
        CodeBarresProduitQueryService codeBarresProduitQueryService
    ) {
        this.codeBarresProduitService = codeBarresProduitService;
        this.codeBarresProduitRepository = codeBarresProduitRepository;
        this.codeBarresProduitQueryService = codeBarresProduitQueryService;
    }

    /**
     * {@code POST  /code-barres-produits} : Create a new codeBarresProduit.
     *
     * @param codeBarresProduitDTO the codeBarresProduitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new codeBarresProduitDTO, or with status {@code 400 (Bad Request)} if the codeBarresProduit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CodeBarresProduitDTO> createCodeBarresProduit(@Valid @RequestBody CodeBarresProduitDTO codeBarresProduitDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CodeBarresProduit : {}", codeBarresProduitDTO);
        if (codeBarresProduitDTO.getId() != null) {
            throw new BadRequestAlertException("A new codeBarresProduit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        codeBarresProduitDTO = codeBarresProduitService.save(codeBarresProduitDTO);
        return ResponseEntity.created(new URI("/api/code-barres-produits/" + codeBarresProduitDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, codeBarresProduitDTO.getId().toString()))
            .body(codeBarresProduitDTO);
    }

    @PostMapping("/generate/{produitId}")
    @PreAuthorize("@businessAuthorizationService.canAssignBarcode()")
    public ResponseEntity<CodeBarresProduitDTO> generateCodeBarresProduit(@PathVariable Long produitId) {
        return ResponseEntity.ok(codeBarresProduitService.generate(produitId));
    }

    /**
     * {@code PUT  /code-barres-produits/:id} : Updates an existing codeBarresProduit.
     *
     * @param id the id of the codeBarresProduitDTO to save.
     * @param codeBarresProduitDTO the codeBarresProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated codeBarresProduitDTO,
     * or with status {@code 400 (Bad Request)} if the codeBarresProduitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the codeBarresProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CodeBarresProduitDTO> updateCodeBarresProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CodeBarresProduitDTO codeBarresProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CodeBarresProduit : {}, {}", id, codeBarresProduitDTO);
        if (codeBarresProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, codeBarresProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!codeBarresProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        codeBarresProduitDTO = codeBarresProduitService.update(codeBarresProduitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, codeBarresProduitDTO.getId().toString()))
            .body(codeBarresProduitDTO);
    }

    /**
     * {@code PATCH  /code-barres-produits/:id} : Partial updates given fields of an existing codeBarresProduit, field will ignore if it is null
     *
     * @param id the id of the codeBarresProduitDTO to save.
     * @param codeBarresProduitDTO the codeBarresProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated codeBarresProduitDTO,
     * or with status {@code 400 (Bad Request)} if the codeBarresProduitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the codeBarresProduitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the codeBarresProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CodeBarresProduitDTO> partialUpdateCodeBarresProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CodeBarresProduitDTO codeBarresProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CodeBarresProduit partially : {}, {}", id, codeBarresProduitDTO);
        if (codeBarresProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, codeBarresProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!codeBarresProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CodeBarresProduitDTO> result = codeBarresProduitService.partialUpdate(codeBarresProduitDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, codeBarresProduitDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /code-barres-produits} : get all the Code Barres Produits.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Code Barres Produits in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CodeBarresProduitDTO>> getAllCodeBarresProduits(
        CodeBarresProduitCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CodeBarresProduits by criteria: {}", criteria);

        Page<CodeBarresProduitDTO> page = codeBarresProduitQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /code-barres-produits/count} : count all the codeBarresProduits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countCodeBarresProduits(CodeBarresProduitCriteria criteria) {
        LOG.debug("REST request to count CodeBarresProduits by criteria: {}", criteria);
        return ResponseEntity.ok().body(codeBarresProduitQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /code-barres-produits/:id} : get the "id" codeBarresProduit.
     *
     * @param id the id of the codeBarresProduitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the codeBarresProduitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CodeBarresProduitDTO> getCodeBarresProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CodeBarresProduit : {}", id);
        Optional<CodeBarresProduitDTO> codeBarresProduitDTO = codeBarresProduitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(codeBarresProduitDTO);
    }

    /**
     * {@code DELETE  /code-barres-produits/:id} : delete the "id" codeBarresProduit.
     *
     * @param id the id of the codeBarresProduitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCodeBarresProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CodeBarresProduit : {}", id);
        codeBarresProduitService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
