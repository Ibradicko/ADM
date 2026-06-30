package com.adm.supervision.web.rest;

import com.adm.supervision.repository.LigneReceptionProduitRepository;
import com.adm.supervision.service.LigneReceptionProduitQueryService;
import com.adm.supervision.service.LigneReceptionProduitService;
import com.adm.supervision.service.criteria.LigneReceptionProduitCriteria;
import com.adm.supervision.service.dto.LigneReceptionProduitDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.LigneReceptionProduit}.
 */
@RestController
@RequestMapping("/api/ligne-reception-produits")
@PreAuthorize("@businessAuthorizationService.canReadStock()")
public class LigneReceptionProduitResource {

    private static final Logger LOG = LoggerFactory.getLogger(LigneReceptionProduitResource.class);

    private static final String ENTITY_NAME = "ligneReceptionProduit";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final LigneReceptionProduitService ligneReceptionProduitService;

    private final LigneReceptionProduitRepository ligneReceptionProduitRepository;

    private final LigneReceptionProduitQueryService ligneReceptionProduitQueryService;

    public LigneReceptionProduitResource(
        LigneReceptionProduitService ligneReceptionProduitService,
        LigneReceptionProduitRepository ligneReceptionProduitRepository,
        LigneReceptionProduitQueryService ligneReceptionProduitQueryService
    ) {
        this.ligneReceptionProduitService = ligneReceptionProduitService;
        this.ligneReceptionProduitRepository = ligneReceptionProduitRepository;
        this.ligneReceptionProduitQueryService = ligneReceptionProduitQueryService;
    }

    /**
     * {@code POST  /ligne-reception-produits} : Create a new ligneReceptionProduit.
     *
     * @param ligneReceptionProduitDTO the ligneReceptionProduitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ligneReceptionProduitDTO, or with status {@code 400 (Bad Request)} if the ligneReceptionProduit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LigneReceptionProduitDTO> createLigneReceptionProduit(
        @Valid @RequestBody LigneReceptionProduitDTO ligneReceptionProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save LigneReceptionProduit : {}", ligneReceptionProduitDTO);
        if (ligneReceptionProduitDTO.getId() != null) {
            throw new BadRequestAlertException("A new ligneReceptionProduit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ligneReceptionProduitDTO = ligneReceptionProduitService.save(ligneReceptionProduitDTO);
        return ResponseEntity.created(new URI("/api/ligne-reception-produits/" + ligneReceptionProduitDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ligneReceptionProduitDTO.getId().toString()))
            .body(ligneReceptionProduitDTO);
    }

    /**
     * {@code PUT  /ligne-reception-produits/:id} : Updates an existing ligneReceptionProduit.
     *
     * @param id the id of the ligneReceptionProduitDTO to save.
     * @param ligneReceptionProduitDTO the ligneReceptionProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneReceptionProduitDTO,
     * or with status {@code 400 (Bad Request)} if the ligneReceptionProduitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ligneReceptionProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LigneReceptionProduitDTO> updateLigneReceptionProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LigneReceptionProduitDTO ligneReceptionProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LigneReceptionProduit : {}, {}", id, ligneReceptionProduitDTO);
        if (ligneReceptionProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneReceptionProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneReceptionProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ligneReceptionProduitDTO = ligneReceptionProduitService.update(ligneReceptionProduitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneReceptionProduitDTO.getId().toString()))
            .body(ligneReceptionProduitDTO);
    }

    /**
     * {@code PATCH  /ligne-reception-produits/:id} : Partial updates given fields of an existing ligneReceptionProduit, field will ignore if it is null
     *
     * @param id the id of the ligneReceptionProduitDTO to save.
     * @param ligneReceptionProduitDTO the ligneReceptionProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneReceptionProduitDTO,
     * or with status {@code 400 (Bad Request)} if the ligneReceptionProduitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ligneReceptionProduitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ligneReceptionProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LigneReceptionProduitDTO> partialUpdateLigneReceptionProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LigneReceptionProduitDTO ligneReceptionProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LigneReceptionProduit partially : {}, {}", id, ligneReceptionProduitDTO);
        if (ligneReceptionProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneReceptionProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneReceptionProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LigneReceptionProduitDTO> result = ligneReceptionProduitService.partialUpdate(ligneReceptionProduitDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneReceptionProduitDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ligne-reception-produits} : get all the Ligne Reception Produits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ligne Reception Produits in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LigneReceptionProduitDTO>> getAllLigneReceptionProduits(LigneReceptionProduitCriteria criteria) {
        LOG.debug("REST request to get LigneReceptionProduits by criteria: {}", criteria);

        List<LigneReceptionProduitDTO> entityList = ligneReceptionProduitQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /ligne-reception-produits/count} : count all the ligneReceptionProduits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countLigneReceptionProduits(LigneReceptionProduitCriteria criteria) {
        LOG.debug("REST request to count LigneReceptionProduits by criteria: {}", criteria);
        return ResponseEntity.ok().body(ligneReceptionProduitQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ligne-reception-produits/:id} : get the "id" ligneReceptionProduit.
     *
     * @param id the id of the ligneReceptionProduitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ligneReceptionProduitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LigneReceptionProduitDTO> getLigneReceptionProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LigneReceptionProduit : {}", id);
        Optional<LigneReceptionProduitDTO> ligneReceptionProduitDTO = ligneReceptionProduitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ligneReceptionProduitDTO);
    }

    /**
     * {@code DELETE  /ligne-reception-produits/:id} : delete the "id" ligneReceptionProduit.
     *
     * @param id the id of the ligneReceptionProduitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLigneReceptionProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LigneReceptionProduit : {}", id);
        ligneReceptionProduitService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
