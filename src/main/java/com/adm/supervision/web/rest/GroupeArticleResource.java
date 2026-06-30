package com.adm.supervision.web.rest;

import com.adm.supervision.repository.GroupeArticleRepository;
import com.adm.supervision.service.GroupeArticleQueryService;
import com.adm.supervision.service.GroupeArticleService;
import com.adm.supervision.service.criteria.GroupeArticleCriteria;
import com.adm.supervision.service.dto.GroupeArticleDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.GroupeArticle}.
 */
@RestController
@RequestMapping("/api/groupe-articles")
@PreAuthorize("isAuthenticated()")
public class GroupeArticleResource {

    private static final Logger LOG = LoggerFactory.getLogger(GroupeArticleResource.class);

    private static final String ENTITY_NAME = "groupeArticle";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final GroupeArticleService groupeArticleService;

    private final GroupeArticleRepository groupeArticleRepository;

    private final GroupeArticleQueryService groupeArticleQueryService;

    public GroupeArticleResource(
        GroupeArticleService groupeArticleService,
        GroupeArticleRepository groupeArticleRepository,
        GroupeArticleQueryService groupeArticleQueryService
    ) {
        this.groupeArticleService = groupeArticleService;
        this.groupeArticleRepository = groupeArticleRepository;
        this.groupeArticleQueryService = groupeArticleQueryService;
    }

    /**
     * {@code POST  /groupe-articles} : Create a new groupeArticle.
     *
     * @param groupeArticleDTO the groupeArticleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new groupeArticleDTO, or with status {@code 400 (Bad Request)} if the groupeArticle has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<GroupeArticleDTO> createGroupeArticle(@Valid @RequestBody GroupeArticleDTO groupeArticleDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save GroupeArticle : {}", groupeArticleDTO);
        if (groupeArticleDTO.getId() != null) {
            throw new BadRequestAlertException("A new groupeArticle cannot already have an ID", ENTITY_NAME, "idexists");
        }
        groupeArticleDTO = groupeArticleService.save(groupeArticleDTO);
        return ResponseEntity.created(new URI("/api/groupe-articles/" + groupeArticleDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, groupeArticleDTO.getId().toString()))
            .body(groupeArticleDTO);
    }

    /**
     * {@code PUT  /groupe-articles/:id} : Updates an existing groupeArticle.
     *
     * @param id the id of the groupeArticleDTO to save.
     * @param groupeArticleDTO the groupeArticleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated groupeArticleDTO,
     * or with status {@code 400 (Bad Request)} if the groupeArticleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the groupeArticleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<GroupeArticleDTO> updateGroupeArticle(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GroupeArticleDTO groupeArticleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update GroupeArticle : {}, {}", id, groupeArticleDTO);
        if (groupeArticleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, groupeArticleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!groupeArticleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        groupeArticleDTO = groupeArticleService.update(groupeArticleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, groupeArticleDTO.getId().toString()))
            .body(groupeArticleDTO);
    }

    /**
     * {@code PATCH  /groupe-articles/:id} : Partial updates given fields of an existing groupeArticle, field will ignore if it is null
     *
     * @param id the id of the groupeArticleDTO to save.
     * @param groupeArticleDTO the groupeArticleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated groupeArticleDTO,
     * or with status {@code 400 (Bad Request)} if the groupeArticleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the groupeArticleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the groupeArticleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<GroupeArticleDTO> partialUpdateGroupeArticle(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GroupeArticleDTO groupeArticleDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update GroupeArticle partially : {}, {}", id, groupeArticleDTO);
        if (groupeArticleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, groupeArticleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!groupeArticleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GroupeArticleDTO> result = groupeArticleService.partialUpdate(groupeArticleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, groupeArticleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /groupe-articles} : get all the Groupe Articles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Groupe Articles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<GroupeArticleDTO>> getAllGroupeArticles(GroupeArticleCriteria criteria) {
        LOG.debug("REST request to get GroupeArticles by criteria: {}", criteria);

        List<GroupeArticleDTO> entityList = groupeArticleQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /groupe-articles/count} : count all the groupeArticles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countGroupeArticles(GroupeArticleCriteria criteria) {
        LOG.debug("REST request to count GroupeArticles by criteria: {}", criteria);
        return ResponseEntity.ok().body(groupeArticleQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /groupe-articles/:id} : get the "id" groupeArticle.
     *
     * @param id the id of the groupeArticleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the groupeArticleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GroupeArticleDTO> getGroupeArticle(@PathVariable("id") Long id) {
        LOG.debug("REST request to get GroupeArticle : {}", id);
        Optional<GroupeArticleDTO> groupeArticleDTO = groupeArticleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(groupeArticleDTO);
    }

    /**
     * {@code DELETE  /groupe-articles/:id} : delete the "id" groupeArticle.
     *
     * @param id the id of the groupeArticleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<Void> deleteGroupeArticle(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete GroupeArticle : {}", id);
        groupeArticleService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
