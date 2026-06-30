package com.adm.supervision.web.rest;

import com.adm.supervision.repository.EtiquetteProduitRepository;
import com.adm.supervision.service.EtiquetteProduitQueryService;
import com.adm.supervision.service.EtiquetteProduitService;
import com.adm.supervision.service.criteria.EtiquetteProduitCriteria;
import com.adm.supervision.service.dto.EtiquetteProduitDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.EtiquetteProduit}.
 */
@RestController
@RequestMapping("/api/etiquette-produits")
@PreAuthorize("@businessAuthorizationService.canManageCatalogue()")
public class EtiquetteProduitResource {

    private static final Logger LOG = LoggerFactory.getLogger(EtiquetteProduitResource.class);

    private static final String ENTITY_NAME = "etiquetteProduit";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final EtiquetteProduitService etiquetteProduitService;

    private final EtiquetteProduitRepository etiquetteProduitRepository;

    private final EtiquetteProduitQueryService etiquetteProduitQueryService;

    public EtiquetteProduitResource(
        EtiquetteProduitService etiquetteProduitService,
        EtiquetteProduitRepository etiquetteProduitRepository,
        EtiquetteProduitQueryService etiquetteProduitQueryService
    ) {
        this.etiquetteProduitService = etiquetteProduitService;
        this.etiquetteProduitRepository = etiquetteProduitRepository;
        this.etiquetteProduitQueryService = etiquetteProduitQueryService;
    }

    /**
     * {@code POST  /etiquette-produits} : Create a new etiquetteProduit.
     *
     * @param etiquetteProduitDTO the etiquetteProduitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new etiquetteProduitDTO, or with status {@code 400 (Bad Request)} if the etiquetteProduit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EtiquetteProduitDTO> createEtiquetteProduit(@Valid @RequestBody EtiquetteProduitDTO etiquetteProduitDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save EtiquetteProduit : {}", etiquetteProduitDTO);
        if (etiquetteProduitDTO.getId() != null) {
            throw new BadRequestAlertException("A new etiquetteProduit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        etiquetteProduitDTO = etiquetteProduitService.save(etiquetteProduitDTO);
        return ResponseEntity.created(new URI("/api/etiquette-produits/" + etiquetteProduitDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, etiquetteProduitDTO.getId().toString()))
            .body(etiquetteProduitDTO);
    }

    /**
     * {@code PUT  /etiquette-produits/:id} : Updates an existing etiquetteProduit.
     *
     * @param id the id of the etiquetteProduitDTO to save.
     * @param etiquetteProduitDTO the etiquetteProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etiquetteProduitDTO,
     * or with status {@code 400 (Bad Request)} if the etiquetteProduitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the etiquetteProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EtiquetteProduitDTO> updateEtiquetteProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EtiquetteProduitDTO etiquetteProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EtiquetteProduit : {}, {}", id, etiquetteProduitDTO);
        if (etiquetteProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etiquetteProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!etiquetteProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        etiquetteProduitDTO = etiquetteProduitService.update(etiquetteProduitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, etiquetteProduitDTO.getId().toString()))
            .body(etiquetteProduitDTO);
    }

    /**
     * {@code PATCH  /etiquette-produits/:id} : Partial updates given fields of an existing etiquetteProduit, field will ignore if it is null
     *
     * @param id the id of the etiquetteProduitDTO to save.
     * @param etiquetteProduitDTO the etiquetteProduitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etiquetteProduitDTO,
     * or with status {@code 400 (Bad Request)} if the etiquetteProduitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the etiquetteProduitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the etiquetteProduitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EtiquetteProduitDTO> partialUpdateEtiquetteProduit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EtiquetteProduitDTO etiquetteProduitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EtiquetteProduit partially : {}, {}", id, etiquetteProduitDTO);
        if (etiquetteProduitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etiquetteProduitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!etiquetteProduitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EtiquetteProduitDTO> result = etiquetteProduitService.partialUpdate(etiquetteProduitDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, etiquetteProduitDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /etiquette-produits} : get all the Etiquette Produits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Etiquette Produits in body.
     */
    @GetMapping("")
    public ResponseEntity<List<EtiquetteProduitDTO>> getAllEtiquetteProduits(EtiquetteProduitCriteria criteria) {
        LOG.debug("REST request to get EtiquetteProduits by criteria: {}", criteria);

        List<EtiquetteProduitDTO> entityList = etiquetteProduitQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /etiquette-produits/count} : count all the etiquetteProduits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countEtiquetteProduits(EtiquetteProduitCriteria criteria) {
        LOG.debug("REST request to count EtiquetteProduits by criteria: {}", criteria);
        return ResponseEntity.ok().body(etiquetteProduitQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /etiquette-produits/:id} : get the "id" etiquetteProduit.
     *
     * @param id the id of the etiquetteProduitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the etiquetteProduitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EtiquetteProduitDTO> getEtiquetteProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EtiquetteProduit : {}", id);
        Optional<EtiquetteProduitDTO> etiquetteProduitDTO = etiquetteProduitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(etiquetteProduitDTO);
    }

    /**
     * {@code DELETE  /etiquette-produits/:id} : delete the "id" etiquetteProduit.
     *
     * @param id the id of the etiquetteProduitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtiquetteProduit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EtiquetteProduit : {}", id);
        etiquetteProduitService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
