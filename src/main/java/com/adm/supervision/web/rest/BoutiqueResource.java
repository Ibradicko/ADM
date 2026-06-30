package com.adm.supervision.web.rest;

import com.adm.supervision.repository.BoutiqueRepository;
import com.adm.supervision.security.BusinessAuthorizationService;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.BoutiqueQueryService;
import com.adm.supervision.service.BoutiqueService;
import com.adm.supervision.service.criteria.BoutiqueCriteria;
import com.adm.supervision.service.dto.BoutiqueDTO;
import com.adm.supervision.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adm.supervision.domain.Boutique}.
 */
@RestController
@RequestMapping("/api/boutiques")
public class BoutiqueResource {

    private static final Logger LOG = LoggerFactory.getLogger(BoutiqueResource.class);

    private static final String ENTITY_NAME = "boutique";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final BoutiqueService boutiqueService;

    private final BoutiqueRepository boutiqueRepository;

    private final BoutiqueQueryService boutiqueQueryService;

    private final BusinessAuthorizationService businessAuthorizationService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public BoutiqueResource(
        BoutiqueService boutiqueService,
        BoutiqueRepository boutiqueRepository,
        BoutiqueQueryService boutiqueQueryService,
        BusinessAuthorizationService businessAuthorizationService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.boutiqueService = boutiqueService;
        this.boutiqueRepository = boutiqueRepository;
        this.boutiqueQueryService = boutiqueQueryService;
        this.businessAuthorizationService = businessAuthorizationService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * {@code POST  /boutiques} : Create a new boutique.
     *
     * @param boutiqueDTO the boutiqueDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new boutiqueDTO, or with status {@code 400 (Bad Request)} if the boutique has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<BoutiqueDTO> createBoutique(@Valid @RequestBody BoutiqueDTO boutiqueDTO) throws URISyntaxException {
        LOG.debug("REST request to save Boutique : {}", boutiqueDTO);
        if (boutiqueDTO.getId() != null) {
            throw new BadRequestAlertException("A new boutique cannot already have an ID", ENTITY_NAME, "idexists");
        }
        boutiqueDTO.setCode(resolveCode(boutiqueDTO.getCode(), boutiqueDTO.getNom()));
        boutiqueDTO = boutiqueService.save(boutiqueDTO);
        return ResponseEntity.created(new URI("/api/boutiques/" + boutiqueDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, boutiqueDTO.getId().toString()))
            .body(boutiqueDTO);
    }

    /**
     * {@code PUT  /boutiques/:id} : Updates an existing boutique.
     *
     * @param id the id of the boutiqueDTO to save.
     * @param boutiqueDTO the boutiqueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boutiqueDTO,
     * or with status {@code 400 (Bad Request)} if the boutiqueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the boutiqueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<BoutiqueDTO> updateBoutique(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BoutiqueDTO boutiqueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Boutique : {}, {}", id, boutiqueDTO);
        if (boutiqueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boutiqueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boutiqueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        boutiqueDTO.setCode(resolveCode(boutiqueDTO.getCode(), boutiqueDTO.getNom()));
        boutiqueDTO = boutiqueService.update(boutiqueDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, boutiqueDTO.getId().toString()))
            .body(boutiqueDTO);
    }

    /**
     * {@code PATCH  /boutiques/:id} : Partial updates given fields of an existing boutique, field will ignore if it is null
     *
     * @param id the id of the boutiqueDTO to save.
     * @param boutiqueDTO the boutiqueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boutiqueDTO,
     * or with status {@code 400 (Bad Request)} if the boutiqueDTO is not valid,
     * or with status {@code 404 (Not Found)} if the boutiqueDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the boutiqueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<BoutiqueDTO> partialUpdateBoutique(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BoutiqueDTO boutiqueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Boutique partially : {}, {}", id, boutiqueDTO);
        if (boutiqueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boutiqueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boutiqueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BoutiqueDTO> result = boutiqueService.partialUpdate(boutiqueDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, boutiqueDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /boutiques} : get all the Boutiques.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Boutiques in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BoutiqueDTO>> getAllBoutiques(
        BoutiqueCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Boutiques by criteria: {}", criteria);
        criteria.setId(boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getId(), "Acces refuse aux boutiques demandees"));

        Page<BoutiqueDTO> page = boutiqueQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /boutiques/count} : count all the boutiques.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBoutiques(BoutiqueCriteria criteria) {
        LOG.debug("REST request to count Boutiques by criteria: {}", criteria);
        criteria.setId(boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getId(), "Acces refuse aux boutiques demandees"));
        return ResponseEntity.ok().body(boutiqueQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /boutiques/:id} : get the "id" boutique.
     *
     * @param id the id of the boutiqueDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the boutiqueDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoutiqueDTO> getBoutique(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Boutique : {}", id);
        if (!businessAuthorizationService.canAccessBoutique(id)) {
            throw new AccessDeniedException("Vous n'etes pas autorise a consulter cette boutique.");
        }
        Optional<BoutiqueDTO> boutiqueDTO = boutiqueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(boutiqueDTO);
    }

    /**
     * {@code DELETE  /boutiques/:id} : delete the "id" boutique.
     *
     * @param id the id of the boutiqueDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<Void> deleteBoutique(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Boutique : {}", id);
        boutiqueService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    private String resolveCode(String code, String nom) {
        if (code != null && !code.isBlank()) {
            return code;
        }

        String source = nom == null || nom.isBlank() ? "BOUTIQUE_" + Instant.now().toEpochMilli() : nom;
        String base = Normalizer.normalize(source, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toUpperCase(Locale.ROOT)
            .replaceAll("[^A-Z0-9]+", "_")
            .replaceAll("^_+|_+$", "");
        if (base.isBlank()) {
            base = "BOUTIQUE_" + Instant.now().toEpochMilli();
        }
        return ("BTQ_" + base).substring(0, Math.min(30, ("BTQ_" + base).length()));
    }
}
