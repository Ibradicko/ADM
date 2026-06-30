package com.adm.supervision.web.rest;

import com.adm.supervision.repository.VenteRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.VenteQueryService;
import com.adm.supervision.service.VenteService;
import com.adm.supervision.service.criteria.VenteCriteria;
import com.adm.supervision.service.dto.CaisseVenteRequest;
import com.adm.supervision.service.dto.CaisseVenteResultDTO;
import com.adm.supervision.service.dto.VenteDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.Vente}.
 */
@RestController
@RequestMapping("/api/ventes")
public class VenteResource {

    private static final Logger LOG = LoggerFactory.getLogger(VenteResource.class);

    private static final String ENTITY_NAME = "vente";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final VenteService venteService;

    private final VenteRepository venteRepository;

    private final VenteQueryService venteQueryService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public VenteResource(
        VenteService venteService,
        VenteRepository venteRepository,
        VenteQueryService venteQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.venteService = venteService;
        this.venteRepository = venteRepository;
        this.venteQueryService = venteQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * {@code POST  /ventes} : Create a new vente.
     *
     * @param venteDTO the venteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new venteDTO, or with status {@code 400 (Bad Request)} if the vente has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize(
        "@businessAuthorizationService.canManageSales() and @businessAuthorizationService.canAccessBoutique(#venteDTO.boutique.id)"
    )
    public ResponseEntity<VenteDTO> createVente(@Valid @RequestBody VenteDTO venteDTO) throws URISyntaxException {
        LOG.debug("REST request to save Vente : {}", venteDTO);
        if (venteDTO.getId() != null) {
            throw new BadRequestAlertException("A new vente cannot already have an ID", ENTITY_NAME, "idexists");
        }
        venteDTO = venteService.save(venteDTO);
        return ResponseEntity.created(new URI("/api/ventes/" + venteDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, venteDTO.getId().toString()))
            .body(venteDTO);
    }

    /**
     * {@code POST  /ventes/checkout} : Create and validate a complete cash desk sale.
     *
     * @param request the checkout request.
     * @return the sale, lines, payments and generated ticket.
     */
    @PostMapping("/checkout")
    @PreAuthorize("@businessAuthorizationService.canManageSales() and @businessAuthorizationService.canAccessBoutique(#request.boutiqueId)")
    public ResponseEntity<CaisseVenteResultDTO> checkout(@Valid @RequestBody CaisseVenteRequest request) {
        LOG.debug("REST request to checkout Vente : {}", request);
        CaisseVenteResultDTO result = venteService.checkout(request);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code PUT  /ventes/:id} : Updates an existing vente.
     *
     * @param id the id of the venteDTO to save.
     * @param venteDTO the venteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated venteDTO,
     * or with status {@code 400 (Bad Request)} if the venteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the venteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize(
        "@businessAuthorizationService.canManageSales() and @resourceBoutiqueSecurityService.canAccessVente(#id) and @businessAuthorizationService.canAccessBoutique(#venteDTO.boutique.id)"
    )
    public ResponseEntity<VenteDTO> updateVente(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VenteDTO venteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Vente : {}, {}", id, venteDTO);
        if (venteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, venteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!venteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        venteDTO = venteService.update(venteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, venteDTO.getId().toString()))
            .body(venteDTO);
    }

    /**
     * {@code PATCH  /ventes/:id} : Partial updates given fields of an existing vente, field will ignore if it is null
     *
     * @param id the id of the venteDTO to save.
     * @param venteDTO the venteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated venteDTO,
     * or with status {@code 400 (Bad Request)} if the venteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the venteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the venteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize(
        "@businessAuthorizationService.canManageSales() and @resourceBoutiqueSecurityService.canAccessVente(#id) and (#venteDTO.boutique == null or @businessAuthorizationService.canAccessBoutique(#venteDTO.boutique.id))"
    )
    public ResponseEntity<VenteDTO> partialUpdateVente(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VenteDTO venteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Vente partially : {}, {}", id, venteDTO);
        if (venteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, venteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!venteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VenteDTO> result = venteService.partialUpdate(venteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, venteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ventes} : get all the Ventes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ventes in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canReadSales()")
    public ResponseEntity<List<VenteDTO>> getAllVentes(
        VenteCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Ventes by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux ventes demandees")
        );

        Page<VenteDTO> page = venteQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ventes/count} : count all the ventes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canReadSales()")
    public ResponseEntity<Long> countVentes(VenteCriteria criteria) {
        LOG.debug("REST request to count Ventes by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux ventes demandees")
        );
        return ResponseEntity.ok().body(venteQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ventes/:id} : get the "id" vente.
     *
     * @param id the id of the venteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the venteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canReadSales() and @resourceBoutiqueSecurityService.canAccessVente(#id)")
    public ResponseEntity<VenteDTO> getVente(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Vente : {}", id);
        Optional<VenteDTO> venteDTO = venteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(venteDTO);
    }

    /**
     * {@code DELETE  /ventes/:id} : delete the "id" vente.
     *
     * @param id the id of the venteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSales() and @resourceBoutiqueSecurityService.canAccessVente(#id)")
    public ResponseEntity<Void> deleteVente(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Vente : {}", id);
        venteService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
