package com.adm.supervision.web.rest;

import com.adm.supervision.repository.HistoriqueCodeBarresRepository;
import com.adm.supervision.service.HistoriqueCodeBarresQueryService;
import com.adm.supervision.service.HistoriqueCodeBarresService;
import com.adm.supervision.service.criteria.HistoriqueCodeBarresCriteria;
import com.adm.supervision.service.dto.HistoriqueCodeBarresDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.HistoriqueCodeBarres}.
 */
@RestController
@RequestMapping("/api/historique-code-barres")
@PreAuthorize("@businessAuthorizationService.canReadStock()")
public class HistoriqueCodeBarresResource {

    private static final Logger LOG = LoggerFactory.getLogger(HistoriqueCodeBarresResource.class);

    private static final String ENTITY_NAME = "historiqueCodeBarres";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final HistoriqueCodeBarresService historiqueCodeBarresService;

    private final HistoriqueCodeBarresRepository historiqueCodeBarresRepository;

    private final HistoriqueCodeBarresQueryService historiqueCodeBarresQueryService;

    public HistoriqueCodeBarresResource(
        HistoriqueCodeBarresService historiqueCodeBarresService,
        HistoriqueCodeBarresRepository historiqueCodeBarresRepository,
        HistoriqueCodeBarresQueryService historiqueCodeBarresQueryService
    ) {
        this.historiqueCodeBarresService = historiqueCodeBarresService;
        this.historiqueCodeBarresRepository = historiqueCodeBarresRepository;
        this.historiqueCodeBarresQueryService = historiqueCodeBarresQueryService;
    }

    /**
     * {@code POST  /historique-code-barres} : Create a new historiqueCodeBarres.
     *
     * @param historiqueCodeBarresDTO the historiqueCodeBarresDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new historiqueCodeBarresDTO, or with status {@code 400 (Bad Request)} if the historiqueCodeBarres has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<HistoriqueCodeBarresDTO> createHistoriqueCodeBarres(
        @Valid @RequestBody HistoriqueCodeBarresDTO historiqueCodeBarresDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save HistoriqueCodeBarres : {}", historiqueCodeBarresDTO);
        if (historiqueCodeBarresDTO.getId() != null) {
            throw new BadRequestAlertException("A new historiqueCodeBarres cannot already have an ID", ENTITY_NAME, "idexists");
        }
        historiqueCodeBarresDTO = historiqueCodeBarresService.save(historiqueCodeBarresDTO);
        return ResponseEntity.created(new URI("/api/historique-code-barres/" + historiqueCodeBarresDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, historiqueCodeBarresDTO.getId().toString()))
            .body(historiqueCodeBarresDTO);
    }

    /**
     * {@code PUT  /historique-code-barres/:id} : Updates an existing historiqueCodeBarres.
     *
     * @param id the id of the historiqueCodeBarresDTO to save.
     * @param historiqueCodeBarresDTO the historiqueCodeBarresDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated historiqueCodeBarresDTO,
     * or with status {@code 400 (Bad Request)} if the historiqueCodeBarresDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the historiqueCodeBarresDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<HistoriqueCodeBarresDTO> updateHistoriqueCodeBarres(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody HistoriqueCodeBarresDTO historiqueCodeBarresDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update HistoriqueCodeBarres : {}, {}", id, historiqueCodeBarresDTO);
        if (historiqueCodeBarresDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, historiqueCodeBarresDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!historiqueCodeBarresRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        historiqueCodeBarresDTO = historiqueCodeBarresService.update(historiqueCodeBarresDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, historiqueCodeBarresDTO.getId().toString()))
            .body(historiqueCodeBarresDTO);
    }

    /**
     * {@code PATCH  /historique-code-barres/:id} : Partial updates given fields of an existing historiqueCodeBarres, field will ignore if it is null
     *
     * @param id the id of the historiqueCodeBarresDTO to save.
     * @param historiqueCodeBarresDTO the historiqueCodeBarresDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated historiqueCodeBarresDTO,
     * or with status {@code 400 (Bad Request)} if the historiqueCodeBarresDTO is not valid,
     * or with status {@code 404 (Not Found)} if the historiqueCodeBarresDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the historiqueCodeBarresDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<HistoriqueCodeBarresDTO> partialUpdateHistoriqueCodeBarres(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody HistoriqueCodeBarresDTO historiqueCodeBarresDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update HistoriqueCodeBarres partially : {}, {}", id, historiqueCodeBarresDTO);
        if (historiqueCodeBarresDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, historiqueCodeBarresDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!historiqueCodeBarresRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<HistoriqueCodeBarresDTO> result = historiqueCodeBarresService.partialUpdate(historiqueCodeBarresDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, historiqueCodeBarresDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /historique-code-barres} : get all the Historique Code Barres.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Historique Code Barres in body.
     */
    @GetMapping("")
    public ResponseEntity<List<HistoriqueCodeBarresDTO>> getAllHistoriqueCodeBarreses(HistoriqueCodeBarresCriteria criteria) {
        LOG.debug("REST request to get HistoriqueCodeBarreses by criteria: {}", criteria);

        List<HistoriqueCodeBarresDTO> entityList = historiqueCodeBarresQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /historique-code-barres/count} : count all the historiqueCodeBarreses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countHistoriqueCodeBarreses(HistoriqueCodeBarresCriteria criteria) {
        LOG.debug("REST request to count HistoriqueCodeBarreses by criteria: {}", criteria);
        return ResponseEntity.ok().body(historiqueCodeBarresQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /historique-code-barres/:id} : get the "id" historiqueCodeBarres.
     *
     * @param id the id of the historiqueCodeBarresDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the historiqueCodeBarresDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HistoriqueCodeBarresDTO> getHistoriqueCodeBarres(@PathVariable("id") Long id) {
        LOG.debug("REST request to get HistoriqueCodeBarres : {}", id);
        Optional<HistoriqueCodeBarresDTO> historiqueCodeBarresDTO = historiqueCodeBarresService.findOne(id);
        return ResponseUtil.wrapOrNotFound(historiqueCodeBarresDTO);
    }

    /**
     * {@code DELETE  /historique-code-barres/:id} : delete the "id" historiqueCodeBarres.
     *
     * @param id the id of the historiqueCodeBarresDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteHistoriqueCodeBarres(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete HistoriqueCodeBarres : {}", id);
        historiqueCodeBarresService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
