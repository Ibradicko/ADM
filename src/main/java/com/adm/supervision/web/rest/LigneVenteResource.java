package com.adm.supervision.web.rest;

import com.adm.supervision.repository.LigneVenteRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.LigneVenteQueryService;
import com.adm.supervision.service.LigneVenteService;
import com.adm.supervision.service.criteria.LigneVenteCriteria;
import com.adm.supervision.service.dto.LigneVenteDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.LigneVente}.
 */
@RestController
@RequestMapping("/api/ligne-ventes")
public class LigneVenteResource {

    private static final Logger LOG = LoggerFactory.getLogger(LigneVenteResource.class);

    private static final String ENTITY_NAME = "ligneVente";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final LigneVenteService ligneVenteService;

    private final LigneVenteRepository ligneVenteRepository;

    private final LigneVenteQueryService ligneVenteQueryService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public LigneVenteResource(
        LigneVenteService ligneVenteService,
        LigneVenteRepository ligneVenteRepository,
        LigneVenteQueryService ligneVenteQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.ligneVenteService = ligneVenteService;
        this.ligneVenteRepository = ligneVenteRepository;
        this.ligneVenteQueryService = ligneVenteQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * {@code POST  /ligne-ventes} : Create a new ligneVente.
     *
     * @param ligneVenteDTO the ligneVenteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ligneVenteDTO, or with status {@code 400 (Bad Request)} if the ligneVente has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageSales()")
    public ResponseEntity<LigneVenteDTO> createLigneVente(@Valid @RequestBody LigneVenteDTO ligneVenteDTO) throws URISyntaxException {
        LOG.debug("REST request to save LigneVente : {}", ligneVenteDTO);
        if (ligneVenteDTO.getId() != null) {
            throw new BadRequestAlertException("A new ligneVente cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ligneVenteDTO = ligneVenteService.save(ligneVenteDTO);
        return ResponseEntity.created(new URI("/api/ligne-ventes/" + ligneVenteDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ligneVenteDTO.getId().toString()))
            .body(ligneVenteDTO);
    }

    /**
     * {@code PUT  /ligne-ventes/:id} : Updates an existing ligneVente.
     *
     * @param id the id of the ligneVenteDTO to save.
     * @param ligneVenteDTO the ligneVenteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneVenteDTO,
     * or with status {@code 400 (Bad Request)} if the ligneVenteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ligneVenteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSales() and @resourceBoutiqueSecurityService.canAccessLigneVente(#id)")
    public ResponseEntity<LigneVenteDTO> updateLigneVente(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LigneVenteDTO ligneVenteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update LigneVente : {}, {}", id, ligneVenteDTO);
        if (ligneVenteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneVenteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneVenteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ligneVenteDTO = ligneVenteService.update(ligneVenteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneVenteDTO.getId().toString()))
            .body(ligneVenteDTO);
    }

    /**
     * {@code PATCH  /ligne-ventes/:id} : Partial updates given fields of an existing ligneVente, field will ignore if it is null
     *
     * @param id the id of the ligneVenteDTO to save.
     * @param ligneVenteDTO the ligneVenteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ligneVenteDTO,
     * or with status {@code 400 (Bad Request)} if the ligneVenteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ligneVenteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ligneVenteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageSales() and @resourceBoutiqueSecurityService.canAccessLigneVente(#id)")
    public ResponseEntity<LigneVenteDTO> partialUpdateLigneVente(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LigneVenteDTO ligneVenteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LigneVente partially : {}, {}", id, ligneVenteDTO);
        if (ligneVenteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ligneVenteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ligneVenteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LigneVenteDTO> result = ligneVenteService.partialUpdate(ligneVenteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ligneVenteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ligne-ventes} : get all the Ligne Ventes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ligne Ventes in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canReadSales()")
    public ResponseEntity<List<LigneVenteDTO>> getAllLigneVentes(
        LigneVenteCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get LigneVentes by criteria: {}", criteria);
        criteria.setVenteId(
            boutiqueCriteriaScopeService.scopeVenteFilter(criteria.getVenteId(), "Acces refuse aux lignes de vente demandees")
        );

        Page<LigneVenteDTO> page = ligneVenteQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ligne-ventes/count} : count all the ligneVentes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canReadSales()")
    public ResponseEntity<Long> countLigneVentes(LigneVenteCriteria criteria) {
        LOG.debug("REST request to count LigneVentes by criteria: {}", criteria);
        criteria.setVenteId(
            boutiqueCriteriaScopeService.scopeVenteFilter(criteria.getVenteId(), "Acces refuse aux lignes de vente demandees")
        );
        return ResponseEntity.ok().body(ligneVenteQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ligne-ventes/:id} : get the "id" ligneVente.
     *
     * @param id the id of the ligneVenteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ligneVenteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canReadSales() and @resourceBoutiqueSecurityService.canAccessLigneVente(#id)")
    public ResponseEntity<LigneVenteDTO> getLigneVente(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LigneVente : {}", id);
        Optional<LigneVenteDTO> ligneVenteDTO = ligneVenteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ligneVenteDTO);
    }

    /**
     * {@code DELETE  /ligne-ventes/:id} : delete the "id" ligneVente.
     *
     * @param id the id of the ligneVenteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSales() and @resourceBoutiqueSecurityService.canAccessLigneVente(#id)")
    public ResponseEntity<Void> deleteLigneVente(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LigneVente : {}", id);
        ligneVenteService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
