package com.adm.supervision.web.rest;

import com.adm.supervision.repository.TicketCaisseRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.TicketCaisseQueryService;
import com.adm.supervision.service.TicketCaisseService;
import com.adm.supervision.service.criteria.TicketCaisseCriteria;
import com.adm.supervision.service.dto.TicketCaisseDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.TicketCaisse}.
 */
@RestController
@RequestMapping("/api/ticket-caisses")
public class TicketCaisseResource {

    private static final Logger LOG = LoggerFactory.getLogger(TicketCaisseResource.class);

    private static final String ENTITY_NAME = "ticketCaisse";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final TicketCaisseService ticketCaisseService;

    private final TicketCaisseRepository ticketCaisseRepository;

    private final TicketCaisseQueryService ticketCaisseQueryService;

    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;

    public TicketCaisseResource(
        TicketCaisseService ticketCaisseService,
        TicketCaisseRepository ticketCaisseRepository,
        TicketCaisseQueryService ticketCaisseQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService
    ) {
        this.ticketCaisseService = ticketCaisseService;
        this.ticketCaisseRepository = ticketCaisseRepository;
        this.ticketCaisseQueryService = ticketCaisseQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
    }

    /**
     * {@code POST  /ticket-caisses} : Create a new ticketCaisse.
     *
     * @param ticketCaisseDTO the ticketCaisseDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ticketCaisseDTO, or with status {@code 400 (Bad Request)} if the ticketCaisse has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageSales()")
    public ResponseEntity<TicketCaisseDTO> createTicketCaisse(@Valid @RequestBody TicketCaisseDTO ticketCaisseDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TicketCaisse : {}", ticketCaisseDTO);
        if (ticketCaisseDTO.getId() != null) {
            throw new BadRequestAlertException("A new ticketCaisse cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ticketCaisseDTO = ticketCaisseService.save(ticketCaisseDTO);
        return ResponseEntity.created(new URI("/api/ticket-caisses/" + ticketCaisseDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ticketCaisseDTO.getId().toString()))
            .body(ticketCaisseDTO);
    }

    /**
     * {@code PUT  /ticket-caisses/:id} : Updates an existing ticketCaisse.
     *
     * @param id the id of the ticketCaisseDTO to save.
     * @param ticketCaisseDTO the ticketCaisseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketCaisseDTO,
     * or with status {@code 400 (Bad Request)} if the ticketCaisseDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ticketCaisseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSales()")
    public ResponseEntity<TicketCaisseDTO> updateTicketCaisse(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TicketCaisseDTO ticketCaisseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TicketCaisse : {}, {}", id, ticketCaisseDTO);
        if (ticketCaisseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketCaisseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketCaisseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ticketCaisseDTO = ticketCaisseService.update(ticketCaisseDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketCaisseDTO.getId().toString()))
            .body(ticketCaisseDTO);
    }

    /**
     * {@code PATCH  /ticket-caisses/:id} : Partial updates given fields of an existing ticketCaisse, field will ignore if it is null
     *
     * @param id the id of the ticketCaisseDTO to save.
     * @param ticketCaisseDTO the ticketCaisseDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ticketCaisseDTO,
     * or with status {@code 400 (Bad Request)} if the ticketCaisseDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ticketCaisseDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ticketCaisseDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageSales()")
    public ResponseEntity<TicketCaisseDTO> partialUpdateTicketCaisse(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TicketCaisseDTO ticketCaisseDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TicketCaisse partially : {}, {}", id, ticketCaisseDTO);
        if (ticketCaisseDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ticketCaisseDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ticketCaisseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TicketCaisseDTO> result = ticketCaisseService.partialUpdate(ticketCaisseDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ticketCaisseDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ticket-caisses} : get all the Ticket Caisses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ticket Caisses in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canReadSales()")
    public ResponseEntity<List<TicketCaisseDTO>> getAllTicketCaisses(TicketCaisseCriteria criteria) {
        LOG.debug("REST request to get TicketCaisses by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux tickets de caisse demandes")
        );

        List<TicketCaisseDTO> entityList = ticketCaisseQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /ticket-caisses/count} : count all the ticketCaisses.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canReadSales()")
    public ResponseEntity<Long> countTicketCaisses(TicketCaisseCriteria criteria) {
        LOG.debug("REST request to count TicketCaisses by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux tickets de caisse demandes")
        );
        return ResponseEntity.ok().body(ticketCaisseQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ticket-caisses/:id} : get the "id" ticketCaisse.
     *
     * @param id the id of the ticketCaisseDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ticketCaisseDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canReadSales()")
    public ResponseEntity<TicketCaisseDTO> getTicketCaisse(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TicketCaisse : {}", id);
        Optional<TicketCaisseDTO> ticketCaisseDTO = ticketCaisseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ticketCaisseDTO);
    }

    /**
     * {@code DELETE  /ticket-caisses/:id} : delete the "id" ticketCaisse.
     *
     * @param id the id of the ticketCaisseDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSales()")
    public ResponseEntity<Void> deleteTicketCaisse(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TicketCaisse : {}", id);
        ticketCaisseService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
