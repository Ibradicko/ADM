package com.adm.supervision.web.rest;

import com.adm.supervision.repository.AffectationUtilisateurRepository;
import com.adm.supervision.service.BoutiqueCriteriaScopeService;
import com.adm.supervision.service.JournalAuditQueryService;
import com.adm.supervision.service.JournalAuditService;
import com.adm.supervision.service.ModuleSecurityService;
import com.adm.supervision.service.criteria.JournalAuditCriteria;
import com.adm.supervision.service.dto.JournalAuditDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.adm.supervision.domain.JournalAudit}.
 * Lecture seule — le journal d'audit ne peut jamais être modifié via l'API.
 */
@RestController
@RequestMapping("/api/journal-audits")
@PreAuthorize("@businessAuthorizationService.canReadAudit()")
public class JournalAuditResource {

    private static final Logger LOG = LoggerFactory.getLogger(JournalAuditResource.class);

    private final JournalAuditService journalAuditService;
    private final JournalAuditQueryService journalAuditQueryService;
    private final BoutiqueCriteriaScopeService boutiqueCriteriaScopeService;
    private final ModuleSecurityService moduleSecurityService;
    private final AffectationUtilisateurRepository affectationUtilisateurRepository;

    public JournalAuditResource(
        JournalAuditService journalAuditService,
        JournalAuditQueryService journalAuditQueryService,
        BoutiqueCriteriaScopeService boutiqueCriteriaScopeService,
        ModuleSecurityService moduleSecurityService,
        AffectationUtilisateurRepository affectationUtilisateurRepository
    ) {
        this.journalAuditService = journalAuditService;
        this.journalAuditQueryService = journalAuditQueryService;
        this.boutiqueCriteriaScopeService = boutiqueCriteriaScopeService;
        this.moduleSecurityService = moduleSecurityService;
        this.affectationUtilisateurRepository = affectationUtilisateurRepository;
    }

    @GetMapping("")
    public ResponseEntity<List<JournalAuditDTO>> getAllJournalAudits(
        JournalAuditCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get JournalAudits by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux audits demandes")
        );
        scopeLocataireUtilisateurFilter(criteria);
        Page<JournalAuditDTO> page = journalAuditQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countJournalAudits(JournalAuditCriteria criteria) {
        LOG.debug("REST request to count JournalAudits by criteria: {}", criteria);
        criteria.setBoutiqueId(
            boutiqueCriteriaScopeService.scopeBoutiqueFilter(criteria.getBoutiqueId(), "Acces refuse aux audits demandes")
        );
        scopeLocataireUtilisateurFilter(criteria);
        return ResponseEntity.ok().body(journalAuditQueryService.countByCriteria(criteria));
    }

    @GetMapping("/{id}")
    @PreAuthorize("@resourceBoutiqueSecurityService.canAccessJournalAudit(#id)")
    public ResponseEntity<JournalAuditDTO> getJournalAudit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get JournalAudit : {}", id);
        Optional<JournalAuditDTO> journalAuditDTO = journalAuditService.findOne(id);
        return ResponseUtil.wrapOrNotFound(journalAuditDTO);
    }

    private void scopeLocataireUtilisateurFilter(JournalAuditCriteria criteria) {
        if (!moduleSecurityService.isCurrentUserLocataire()) {
            return;
        }

        Set<Long> accessibleBoutiqueIds = moduleSecurityService.getAccessibleBoutiqueIds();
        if (accessibleBoutiqueIds.isEmpty()) {
            throw new AccessDeniedException("Acces refuse aux audits demandes");
        }

        Set<Long> accessibleUserIds = affectationUtilisateurRepository.findActiveUserIdsByBoutiqueIds(
            accessibleBoutiqueIds,
            LocalDate.now()
        );

        LongFilter scopedFilter = criteria.getUtilisateurId() == null ? new LongFilter() : criteria.getUtilisateurId();
        if (accessibleUserIds.isEmpty()) {
            scopedFilter.setEquals(-1L);
            criteria.setUtilisateurId(scopedFilter);
            return;
        }

        if (scopedFilter.getEquals() != null && !accessibleUserIds.contains(scopedFilter.getEquals())) {
            throw new AccessDeniedException("Acces refuse aux audits demandes");
        }

        if (scopedFilter.getIn() != null && !scopedFilter.getIn().isEmpty()) {
            List<Long> scopedIds = scopedFilter.getIn().stream().filter(accessibleUserIds::contains).toList();
            if (scopedIds.isEmpty()) {
                throw new AccessDeniedException("Acces refuse aux audits demandes");
            }
            scopedFilter.setIn(scopedIds);
            criteria.setUtilisateurId(scopedFilter);
            return;
        }

        scopedFilter.setIn(new ArrayList<>(accessibleUserIds));
        criteria.setUtilisateurId(scopedFilter);
    }
}
