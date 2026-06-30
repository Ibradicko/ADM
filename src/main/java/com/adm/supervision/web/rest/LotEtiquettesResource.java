package com.adm.supervision.web.rest;

import com.adm.supervision.repository.LotEtiquettesRepository;
import com.adm.supervision.service.LotEtiquettesQueryService;
import com.adm.supervision.service.LotEtiquettesService;
import com.adm.supervision.service.criteria.LotEtiquettesCriteria;
import com.adm.supervision.service.dto.LotEtiquettesDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.LotEtiquettes}.
 */
@RestController
@RequestMapping("/api/lot-etiquettes")
@PreAuthorize("@businessAuthorizationService.canManageCatalogue()")
public class LotEtiquettesResource {

    private static final Logger LOG = LoggerFactory.getLogger(LotEtiquettesResource.class);

    private static final String ENTITY_NAME = "lotEtiquettes";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final LotEtiquettesService lotEtiquettesService;

    private final LotEtiquettesRepository lotEtiquettesRepository;

    private final LotEtiquettesQueryService lotEtiquettesQueryService;

    public LotEtiquettesResource(
        LotEtiquettesService lotEtiquettesService,
        LotEtiquettesRepository lotEtiquettesRepository,
        LotEtiquettesQueryService lotEtiquettesQueryService
    ) {
        this.lotEtiquettesService = lotEtiquettesService;
        this.lotEtiquettesRepository = lotEtiquettesRepository;
        this.lotEtiquettesQueryService = lotEtiquettesQueryService;
    }

    /**
     * {@code POST  /lot-etiquettes} : Create a new lotEtiquettes.
     *
     * @param lotEtiquettesDTO the lotEtiquettesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new lotEtiquettesDTO, or with status {@code 400 (Bad Request)} if the lotEtiquettes has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LotEtiquettesDTO> createLotEtiquettes(@Valid @RequestBody LotEtiquettesDTO lotEtiquettesDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save LotEtiquettes : {}", lotEtiquettesDTO);
        if (lotEtiquettesDTO.getId() != null) {
            throw new BadRequestAlertException("A new lotEtiquettes cannot already have an ID", ENTITY_NAME, "idexists");
        }
        lotEtiquettesDTO = lotEtiquettesService.save(lotEtiquettesDTO);
        return ResponseEntity.created(new URI("/api/lot-etiquettes/" + lotEtiquettesDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, lotEtiquettesDTO.getId().toString()))
            .body(lotEtiquettesDTO);
    }

    /**
     * {@code PUT  /lot-etiquettes/:id} : Updates an existing lotEtiquettes.
     *
     * @param id the id of the lotEtiquettesDTO to save.
     * @param lotEtiquettesDTO the lotEtiquettesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lotEtiquettesDTO,
     * or with status {@code 400 (Bad Request)} if the lotEtiquettesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the lotEtiquettesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LotEtiquettesDTO> updateLotEtiquettes(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LotEtiquettesDTO lotEtiquettesDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LotEtiquettes : {}, {}", id, lotEtiquettesDTO);
        if (lotEtiquettesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lotEtiquettesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lotEtiquettesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        lotEtiquettesDTO = lotEtiquettesService.update(lotEtiquettesDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lotEtiquettesDTO.getId().toString()))
            .body(lotEtiquettesDTO);
    }

    /**
     * {@code PATCH  /lot-etiquettes/:id} : Partial updates given fields of an existing lotEtiquettes, field will ignore if it is null
     *
     * @param id the id of the lotEtiquettesDTO to save.
     * @param lotEtiquettesDTO the lotEtiquettesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated lotEtiquettesDTO,
     * or with status {@code 400 (Bad Request)} if the lotEtiquettesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the lotEtiquettesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the lotEtiquettesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LotEtiquettesDTO> partialUpdateLotEtiquettes(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LotEtiquettesDTO lotEtiquettesDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LotEtiquettes partially : {}, {}", id, lotEtiquettesDTO);
        if (lotEtiquettesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, lotEtiquettesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!lotEtiquettesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LotEtiquettesDTO> result = lotEtiquettesService.partialUpdate(lotEtiquettesDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, lotEtiquettesDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /lot-etiquettes} : get all the Lot Etiquettes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Lot Etiquettes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LotEtiquettesDTO>> getAllLotEtiquetteses(LotEtiquettesCriteria criteria) {
        LOG.debug("REST request to get LotEtiquetteses by criteria: {}", criteria);

        List<LotEtiquettesDTO> entityList = lotEtiquettesQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /lot-etiquettes/count} : count all the lotEtiquetteses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countLotEtiquetteses(LotEtiquettesCriteria criteria) {
        LOG.debug("REST request to count LotEtiquetteses by criteria: {}", criteria);
        return ResponseEntity.ok().body(lotEtiquettesQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /lot-etiquettes/:id} : get the "id" lotEtiquettes.
     *
     * @param id the id of the lotEtiquettesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the lotEtiquettesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LotEtiquettesDTO> getLotEtiquettes(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LotEtiquettes : {}", id);
        Optional<LotEtiquettesDTO> lotEtiquettesDTO = lotEtiquettesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(lotEtiquettesDTO);
    }

    /**
     * {@code DELETE  /lot-etiquettes/:id} : delete the "id" lotEtiquettes.
     *
     * @param id the id of the lotEtiquettesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLotEtiquettes(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LotEtiquettes : {}", id);
        lotEtiquettesService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
