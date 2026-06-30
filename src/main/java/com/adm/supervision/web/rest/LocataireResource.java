package com.adm.supervision.web.rest;

import com.adm.supervision.config.Constants;
import com.adm.supervision.repository.LocataireRepository;
import com.adm.supervision.service.LocataireQueryService;
import com.adm.supervision.service.LocataireService;
import com.adm.supervision.service.criteria.LocataireCriteria;
import com.adm.supervision.service.dto.LocataireDTO;
import com.adm.supervision.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
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
 * REST controller for managing {@link com.adm.supervision.domain.Locataire}.
 */
@RestController
@RequestMapping("/api/locataires")
public class LocataireResource {

    private static final Logger LOG = LoggerFactory.getLogger(LocataireResource.class);

    private static final String ENTITY_NAME = "locataire";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final LocataireService locataireService;

    private final LocataireRepository locataireRepository;

    private final LocataireQueryService locataireQueryService;

    public LocataireResource(
        LocataireService locataireService,
        LocataireRepository locataireRepository,
        LocataireQueryService locataireQueryService
    ) {
        this.locataireService = locataireService;
        this.locataireRepository = locataireRepository;
        this.locataireQueryService = locataireQueryService;
    }

    /**
     * {@code POST  /locataires} : Create a new locataire.
     *
     * @param locataireDTO the locataireDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new locataireDTO, or with status {@code 400 (Bad Request)} if the locataire has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<LocataireDTO> createLocataire(@Valid @RequestBody LocataireDTO locataireDTO) throws URISyntaxException {
        LOG.debug("REST request to save Locataire : {}", locataireDTO);
        if (locataireDTO.getId() != null) {
            throw new BadRequestAlertException("A new locataire cannot already have an ID", ENTITY_NAME, "idexists");
        }
        locataireDTO = locataireService.save(locataireDTO);
        return ResponseEntity.created(new URI("/api/locataires/" + locataireDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, locataireDTO.getId().toString()))
            .body(locataireDTO);
    }

    /**
     * {@code PUT  /locataires/:id} : Updates an existing locataire.
     *
     * @param id the id of the locataireDTO to save.
     * @param locataireDTO the locataireDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated locataireDTO,
     * or with status {@code 400 (Bad Request)} if the locataireDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the locataireDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<LocataireDTO> updateLocataire(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LocataireDTO locataireDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Locataire : {}, {}", id, locataireDTO);
        if (locataireDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, locataireDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!locataireRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        locataireDTO = locataireService.update(locataireDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, locataireDTO.getId().toString()))
            .body(locataireDTO);
    }

    /**
     * {@code PATCH  /locataires/:id} : Partial updates given fields of an existing locataire, field will ignore if it is null
     *
     * @param id the id of the locataireDTO to save.
     * @param locataireDTO the locataireDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated locataireDTO,
     * or with status {@code 400 (Bad Request)} if the locataireDTO is not valid,
     * or with status {@code 404 (Not Found)} if the locataireDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the locataireDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<LocataireDTO> partialUpdateLocataire(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LocataireDTO locataireDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Locataire partially : {}, {}", id, locataireDTO);
        if (locataireDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, locataireDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!locataireRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LocataireDTO> result = locataireService.partialUpdate(locataireDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, locataireDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /locataires} : get all the Locataires.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Locataires in body.
     */
    @GetMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<List<LocataireDTO>> getAllLocataires(
        LocataireCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Locataires by criteria: {}", criteria);

        Page<LocataireDTO> page = locataireQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /locataires/count} : count all the locataires.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<Long> countLocataires(LocataireCriteria criteria) {
        LOG.debug("REST request to count Locataires by criteria: {}", criteria);
        return ResponseEntity.ok().body(locataireQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /locataires/:id} : get the "id" locataire.
     *
     * @param id the id of the locataireDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the locataireDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques() || @businessAuthorizationService.isCurrentUserLocataire()")
    public ResponseEntity<LocataireDTO> getLocataire(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Locataire : {}", id);
        Optional<LocataireDTO> locataireDTO = locataireService.findOne(id);
        return ResponseUtil.wrapOrNotFound(locataireDTO);
    }

    /**
     * {@code DELETE  /locataires/:id} : delete the "id" locataire.
     *
     * @param id the id of the locataireDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PostMapping("/{id}/reinitialiser-mot-de-passe")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<Map<String, String>> reinitialiserMotDePasseLocataire(@PathVariable("id") Long id) {
        LOG.debug("REST request to reset password for Locataire : {}", id);
        String login = locataireService.reinitialiserMotDePasse(id);
        return ResponseEntity.ok(Map.of("login", login, "temporaryPassword", Constants.MOT_DE_PASSE_PAR_DEFAUT));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageBoutiques()")
    public ResponseEntity<Void> deleteLocataire(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Locataire : {}", id);
        locataireService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
