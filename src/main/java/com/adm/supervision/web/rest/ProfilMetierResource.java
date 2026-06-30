package com.adm.supervision.web.rest;

import com.adm.supervision.repository.ProfilMetierRepository;
import com.adm.supervision.service.ProfilMetierQueryService;
import com.adm.supervision.service.ProfilMetierService;
import com.adm.supervision.service.criteria.ProfilMetierCriteria;
import com.adm.supervision.service.dto.ProfilMetierDTO;
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
 * REST controller for managing {@link com.adm.supervision.domain.ProfilMetier}.
 */
@RestController
@RequestMapping("/api/profil-metiers")
public class ProfilMetierResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProfilMetierResource.class);

    private static final String ENTITY_NAME = "profilMetier";

    @Value("${jhipster.clientApp.name:admSupervisionVentes}")
    private String applicationName;

    private final ProfilMetierService profilMetierService;

    private final ProfilMetierRepository profilMetierRepository;

    private final ProfilMetierQueryService profilMetierQueryService;

    public ProfilMetierResource(
        ProfilMetierService profilMetierService,
        ProfilMetierRepository profilMetierRepository,
        ProfilMetierQueryService profilMetierQueryService
    ) {
        this.profilMetierService = profilMetierService;
        this.profilMetierRepository = profilMetierRepository;
        this.profilMetierQueryService = profilMetierQueryService;
    }

    /**
     * {@code POST  /profil-metiers} : Create a new profilMetier.
     *
     * @param profilMetierDTO the profilMetierDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new profilMetierDTO, or with status {@code 400 (Bad Request)} if the profilMetier has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<ProfilMetierDTO> createProfilMetier(@Valid @RequestBody ProfilMetierDTO profilMetierDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProfilMetier : {}", profilMetierDTO);
        if (profilMetierDTO.getId() != null) {
            throw new BadRequestAlertException("A new profilMetier cannot already have an ID", ENTITY_NAME, "idexists");
        }
        profilMetierDTO = profilMetierService.save(profilMetierDTO);
        return ResponseEntity.created(new URI("/api/profil-metiers/" + profilMetierDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, profilMetierDTO.getId().toString()))
            .body(profilMetierDTO);
    }

    /**
     * {@code PUT  /profil-metiers/:id} : Updates an existing profilMetier.
     *
     * @param id the id of the profilMetierDTO to save.
     * @param profilMetierDTO the profilMetierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profilMetierDTO,
     * or with status {@code 400 (Bad Request)} if the profilMetierDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the profilMetierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<ProfilMetierDTO> updateProfilMetier(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProfilMetierDTO profilMetierDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProfilMetier : {}, {}", id, profilMetierDTO);
        if (profilMetierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profilMetierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profilMetierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        profilMetierDTO = profilMetierService.update(profilMetierDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, profilMetierDTO.getId().toString()))
            .body(profilMetierDTO);
    }

    /**
     * {@code PATCH  /profil-metiers/:id} : Partial updates given fields of an existing profilMetier, field will ignore if it is null
     *
     * @param id the id of the profilMetierDTO to save.
     * @param profilMetierDTO the profilMetierDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profilMetierDTO,
     * or with status {@code 400 (Bad Request)} if the profilMetierDTO is not valid,
     * or with status {@code 404 (Not Found)} if the profilMetierDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the profilMetierDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<ProfilMetierDTO> partialUpdateProfilMetier(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProfilMetierDTO profilMetierDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProfilMetier partially : {}, {}", id, profilMetierDTO);
        if (profilMetierDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profilMetierDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profilMetierRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProfilMetierDTO> result = profilMetierService.partialUpdate(profilMetierDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, profilMetierDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /profil-metiers} : get all the Profil Metiers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Profil Metiers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProfilMetierDTO>> getAllProfilMetiers(ProfilMetierCriteria criteria) {
        LOG.debug("REST request to get ProfilMetiers by criteria: {}", criteria);

        List<ProfilMetierDTO> entityList = profilMetierQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /profil-metiers/count} : count all the profilMetiers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countProfilMetiers(ProfilMetierCriteria criteria) {
        LOG.debug("REST request to count ProfilMetiers by criteria: {}", criteria);
        return ResponseEntity.ok().body(profilMetierQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /profil-metiers/:id} : get the "id" profilMetier.
     *
     * @param id the id of the profilMetierDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the profilMetierDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfilMetierDTO> getProfilMetier(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProfilMetier : {}", id);
        Optional<ProfilMetierDTO> profilMetierDTO = profilMetierService.findOne(id);
        return ResponseUtil.wrapOrNotFound(profilMetierDTO);
    }

    /**
     * {@code DELETE  /profil-metiers/:id} : delete the "id" profilMetier.
     *
     * @param id the id of the profilMetierDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@businessAuthorizationService.canManageSettings()")
    public ResponseEntity<Void> deleteProfilMetier(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProfilMetier : {}", id);
        profilMetierService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
