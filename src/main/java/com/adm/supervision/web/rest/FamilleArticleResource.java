package com.adm.supervision.web.rest;

import com.adm.supervision.repository.FamilleArticleRepository;
import com.adm.supervision.service.FamilleArticleQueryService;
import com.adm.supervision.service.FamilleArticleService;
import com.adm.supervision.service.criteria.FamilleArticleCriteria;
import com.adm.supervision.service.dto.FamilleArticleDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.FamilleArticle}.
 */
@RestController
@RequestMapping("/api/famille-articles")
@PreAuthorize("isAuthenticated()")
public class FamilleArticleResource {

    private static final Logger LOG = LoggerFactory.getLogger(FamilleArticleResource.class);

    private static final String ENTITY_NAME = "familleArticle";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final FamilleArticleService familleArticleService;

    private final FamilleArticleRepository familleArticleRepository;

    private final FamilleArticleQueryService familleArticleQueryService;

    public FamilleArticleResource(
        FamilleArticleService familleArticleService,
        FamilleArticleRepository familleArticleRepository,
        FamilleArticleQueryService familleArticleQueryService
    ) {
        this.familleArticleService = familleArticleService;
        this.familleArticleRepository = familleArticleRepository;
        this.familleArticleQueryService = familleArticleQueryService;
    }

    /**
     * {@code POST  /famille-articles} : Create a new familleArticle.
     *
     * @param familleArticleDTO the familleArticleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new familleArticleDTO, or with status {@code 400 (Bad Request)} if the familleArticle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<FamilleArticleDTO> createFamilleArticle(@Valid @RequestBody FamilleArticleDTO familleArticleDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save FamilleArticle : {}", familleArticleDTO);
        if (familleArticleDTO.getId() != null) {
            throw new BadRequestAlertException("A new familleArticle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        familleArticleDTO = familleArticleService.save(familleArticleDTO);
        return ResponseEntity.created(new URI("/api/famille-articles/" + familleArticleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, familleArticleDTO.getId().toString()))
            .body(familleArticleDTO);
    }

    /**
     * {@code PUT  /famille-articles/:id} : Updates an existing familleArticle.
     *
     * @param id the id of the familleArticleDTO to save.
     * @param familleArticleDTO the familleArticleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated familleArticleDTO,
     * or with status {@code 400 (Bad Request)} if the familleArticleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the familleArticleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<FamilleArticleDTO> updateFamilleArticle(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FamilleArticleDTO familleArticleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FamilleArticle : {}, {}", id, familleArticleDTO);
        if (familleArticleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, familleArticleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!familleArticleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        familleArticleDTO = familleArticleService.update(familleArticleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, familleArticleDTO.getId().toString()))
            .body(familleArticleDTO);
    }

    /**
     * {@code PATCH  /famille-articles/:id} : Partial updates given fields of an existing familleArticle, field will ignore if it is null
     *
     * @param id the id of the familleArticleDTO to save.
     * @param familleArticleDTO the familleArticleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated familleArticleDTO,
     * or with status {@code 400 (Bad Request)} if the familleArticleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the familleArticleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the familleArticleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<FamilleArticleDTO> partialUpdateFamilleArticle(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FamilleArticleDTO familleArticleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FamilleArticle partially : {}, {}", id, familleArticleDTO);
        if (familleArticleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, familleArticleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!familleArticleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FamilleArticleDTO> result = familleArticleService.partialUpdate(familleArticleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, familleArticleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /famille-articles} : get all the Famille Articles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Famille Articles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FamilleArticleDTO>> getAllFamilleArticles(FamilleArticleCriteria criteria) {
        LOG.debug("REST request to get FamilleArticles by criteria: {}", criteria);

        List<FamilleArticleDTO> entityList = familleArticleQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /famille-articles/count} : count all the familleArticles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countFamilleArticles(FamilleArticleCriteria criteria) {
        LOG.debug("REST request to count FamilleArticles by criteria: {}", criteria);
        return ResponseEntity.ok().body(familleArticleQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /famille-articles/:id} : get the "id" familleArticle.
     *
     * @param id the id of the familleArticleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the familleArticleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FamilleArticleDTO> getFamilleArticle(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FamilleArticle : {}", id);
        Optional<FamilleArticleDTO> familleArticleDTO = familleArticleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(familleArticleDTO);
    }

    /**
     * {@code DELETE  /famille-articles/:id} : delete the "id" familleArticle.
     *
     * @param id the id of the familleArticleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<Void> deleteFamilleArticle(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FamilleArticle : {}", id);
        familleArticleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
