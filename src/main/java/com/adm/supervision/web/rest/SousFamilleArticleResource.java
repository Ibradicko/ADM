package com.adm.supervision.web.rest;

import com.adm.supervision.repository.SousFamilleArticleRepository;
import com.adm.supervision.service.SousFamilleArticleQueryService;
import com.adm.supervision.service.SousFamilleArticleService;
import com.adm.supervision.service.criteria.SousFamilleArticleCriteria;
import com.adm.supervision.service.dto.SousFamilleArticleDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.SousFamilleArticle}.
 */
@RestController
@RequestMapping("/api/sous-famille-articles")
@PreAuthorize("isAuthenticated()")
public class SousFamilleArticleResource {

    private static final Logger LOG = LoggerFactory.getLogger(SousFamilleArticleResource.class);

    private static final String ENTITY_NAME = "sousFamilleArticle";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final SousFamilleArticleService sousFamilleArticleService;

    private final SousFamilleArticleRepository sousFamilleArticleRepository;

    private final SousFamilleArticleQueryService sousFamilleArticleQueryService;

    public SousFamilleArticleResource(
        SousFamilleArticleService sousFamilleArticleService,
        SousFamilleArticleRepository sousFamilleArticleRepository,
        SousFamilleArticleQueryService sousFamilleArticleQueryService
    ) {
        this.sousFamilleArticleService = sousFamilleArticleService;
        this.sousFamilleArticleRepository = sousFamilleArticleRepository;
        this.sousFamilleArticleQueryService = sousFamilleArticleQueryService;
    }

    /**
     * {@code POST  /sous-famille-articles} : Create a new sousFamilleArticle.
     *
     * @param sousFamilleArticleDTO the sousFamilleArticleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sousFamilleArticleDTO, or with status {@code 400 (Bad Request)} if the sousFamilleArticle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<SousFamilleArticleDTO> createSousFamilleArticle(@Valid @RequestBody SousFamilleArticleDTO sousFamilleArticleDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save SousFamilleArticle : {}", sousFamilleArticleDTO);
        if (sousFamilleArticleDTO.getId() != null) {
            throw new BadRequestAlertException("A new sousFamilleArticle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        sousFamilleArticleDTO = sousFamilleArticleService.save(sousFamilleArticleDTO);
        return ResponseEntity.created(new URI("/api/sous-famille-articles/" + sousFamilleArticleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, sousFamilleArticleDTO.getId().toString()))
            .body(sousFamilleArticleDTO);
    }

    /**
     * {@code PUT  /sous-famille-articles/:id} : Updates an existing sousFamilleArticle.
     *
     * @param id the id of the sousFamilleArticleDTO to save.
     * @param sousFamilleArticleDTO the sousFamilleArticleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sousFamilleArticleDTO,
     * or with status {@code 400 (Bad Request)} if the sousFamilleArticleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sousFamilleArticleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<SousFamilleArticleDTO> updateSousFamilleArticle(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SousFamilleArticleDTO sousFamilleArticleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SousFamilleArticle : {}, {}", id, sousFamilleArticleDTO);
        if (sousFamilleArticleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sousFamilleArticleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sousFamilleArticleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        sousFamilleArticleDTO = sousFamilleArticleService.update(sousFamilleArticleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sousFamilleArticleDTO.getId().toString()))
            .body(sousFamilleArticleDTO);
    }

    /**
     * {@code PATCH  /sous-famille-articles/:id} : Partial updates given fields of an existing sousFamilleArticle, field will ignore if it is null
     *
     * @param id the id of the sousFamilleArticleDTO to save.
     * @param sousFamilleArticleDTO the sousFamilleArticleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sousFamilleArticleDTO,
     * or with status {@code 400 (Bad Request)} if the sousFamilleArticleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the sousFamilleArticleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the sousFamilleArticleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<SousFamilleArticleDTO> partialUpdateSousFamilleArticle(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SousFamilleArticleDTO sousFamilleArticleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SousFamilleArticle partially : {}, {}", id, sousFamilleArticleDTO);
        if (sousFamilleArticleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sousFamilleArticleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sousFamilleArticleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SousFamilleArticleDTO> result = sousFamilleArticleService.partialUpdate(sousFamilleArticleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sousFamilleArticleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sous-famille-articles} : get all the Sous Famille Articles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Sous Famille Articles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SousFamilleArticleDTO>> getAllSousFamilleArticles(SousFamilleArticleCriteria criteria) {
        LOG.debug("REST request to get SousFamilleArticles by criteria: {}", criteria);

        List<SousFamilleArticleDTO> entityList = sousFamilleArticleQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /sous-famille-articles/count} : count all the sousFamilleArticles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countSousFamilleArticles(SousFamilleArticleCriteria criteria) {
        LOG.debug("REST request to count SousFamilleArticles by criteria: {}", criteria);
        return ResponseEntity.ok().body(sousFamilleArticleQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sous-famille-articles/:id} : get the "id" sousFamilleArticle.
     *
     * @param id the id of the sousFamilleArticleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sousFamilleArticleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SousFamilleArticleDTO> getSousFamilleArticle(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SousFamilleArticle : {}", id);
        Optional<SousFamilleArticleDTO> sousFamilleArticleDTO = sousFamilleArticleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sousFamilleArticleDTO);
    }

    /**
     * {@code DELETE  /sous-famille-articles/:id} : delete the "id" sousFamilleArticle.
     *
     * @param id the id of the sousFamilleArticleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<Void> deleteSousFamilleArticle(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SousFamilleArticle : {}", id);
        sousFamilleArticleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
