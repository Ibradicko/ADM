package com.adm.supervision.web.rest;

import com.adm.supervision.repository.PaiementVenteRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.PaiementVenteQueryService;
import com.adm.supervision.service.PaiementVenteService;
import com.adm.supervision.service.criteria.PaiementVenteCriteria;
import com.adm.supervision.service.dto.PaiementVenteDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.PaiementVente}.
 */
@RestController
@RequestMapping("/api/paiement-ventes")
public class PaiementVenteResource {

    private static final Logger LOG = LoggerFactory.getLogger(PaiementVenteResource.class);

    private static final String ENTITY_NAME = "paiementVente";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final PaiementVenteService paiementVenteService;

    private final PaiementVenteRepository paiementVenteRepository;

    private final PaiementVenteQueryService paiementVenteQueryService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public PaiementVenteResource(
        PaiementVenteService paiementVenteService,
        PaiementVenteRepository paiementVenteRepository,
        PaiementVenteQueryService paiementVenteQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.paiementVenteService = paiementVenteService;
        this.paiementVenteRepository = paiementVenteRepository;
        this.paiementVenteQueryService = paiementVenteQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * {@code POST  /paiement-ventes} : Create a new paiementVente.
     *
     * @param paiementVenteDTO the paiementVenteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paiementVenteDTO, or with status {@code 400 (Bad Request)} if the paiementVente has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageSales()")
    public ResponseEntity<PaiementVenteDTO> createPaiementVente(@Valid @RequestBody PaiementVenteDTO paiementVenteDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PaiementVente : {}", paiementVenteDTO);
        if (paiementVenteDTO.getId() != null) {
            throw new BadRequestAlertException("A new paiementVente cannot already have an ID", ENTITY_NAME, "idexists");
        }
        paiementVenteDTO = paiementVenteService.save(paiementVenteDTO);
        return ResponseEntity.created(new URI("/api/paiement-ventes/" + paiementVenteDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, paiementVenteDTO.getId().toString()))
            .body(paiementVenteDTO);
    }

    /**
     * {@code PUT  /paiement-ventes/:id} : Updates an existing paiementVente.
     *
     * @param id the id of the paiementVenteDTO to save.
     * @param paiementVenteDTO the paiementVenteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paiementVenteDTO,
     * or with status {@code 400 (Bad Request)} if the paiementVenteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paiementVenteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSales()")
    public ResponseEntity<PaiementVenteDTO> updatePaiementVente(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PaiementVenteDTO paiementVenteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PaiementVente : {}, {}", id, paiementVenteDTO);
        if (paiementVenteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paiementVenteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paiementVenteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        paiementVenteDTO = paiementVenteService.update(paiementVenteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paiementVenteDTO.getId().toString()))
            .body(paiementVenteDTO);
    }

    /**
     * {@code PATCH  /paiement-ventes/:id} : Partial updates given fields of an existing paiementVente, field will ignore if it is null
     *
     * @param id the id of the paiementVenteDTO to save.
     * @param paiementVenteDTO the paiementVenteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paiementVenteDTO,
     * or with status {@code 400 (Bad Request)} if the paiementVenteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the paiementVenteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the paiementVenteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageSales()")
    public ResponseEntity<PaiementVenteDTO> partialUpdatePaiementVente(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PaiementVenteDTO paiementVenteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PaiementVente partially : {}, {}", id, paiementVenteDTO);
        if (paiementVenteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paiementVenteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paiementVenteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PaiementVenteDTO> result = paiementVenteService.partialUpdate(paiementVenteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, paiementVenteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /paiement-ventes} : get all the Paiement Ventes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Paiement Ventes in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canReadSales()")
    public ResponseEntity<List<PaiementVenteDTO>> getAllPaiementVentes(
        PaiementVenteCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get PaiementVentes by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux paiements de vente demandes")
        );

        Page<PaiementVenteDTO> page = paiementVenteQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /paiement-ventes/count} : count all the paiementVentes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canReadSales()")
    public ResponseEntity<Long> countPaiementVentes(PaiementVenteCriteria criteria) {
        LOG.debug("REST request to count PaiementVentes by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux paiements de vente demandes")
        );
        return ResponseEntity.ok().body(paiementVenteQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /paiement-ventes/:id} : get the "id" paiementVente.
     *
     * @param id the id of the paiementVenteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paiementVenteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canReadSales()")
    public ResponseEntity<PaiementVenteDTO> getPaiementVente(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PaiementVente : {}", id);
        Optional<PaiementVenteDTO> paiementVenteDTO = paiementVenteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paiementVenteDTO);
    }

    /**
     * {@code DELETE  /paiement-ventes/:id} : delete the "id" paiementVente.
     *
     * @param id the id of the paiementVenteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSales()")
    public ResponseEntity<Void> deletePaiementVente(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PaiementVente : {}", id);
        paiementVenteService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
