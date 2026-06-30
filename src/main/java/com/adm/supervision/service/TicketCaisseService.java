package com.adm.supervision.service;

import com.adm.supervision.domain.TicketCaisse;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.TicketCaisseRepository;
import com.adm.supervision.repository.VenteRepository;
import com.adm.supervision.service.dto.TicketCaisseDTO;
import com.adm.supervision.service.mapper.TicketCaisseMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.TicketCaisse}.
 */
@Service
@Transactional
public class TicketCaisseService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketCaisseService.class);

    private final TicketCaisseRepository ticketCaisseRepository;

    private final TicketCaisseMapper ticketCaisseMapper;

    private final ModuleSecurityService moduleSecurityService;

    private final JournalAuditService journalAuditService;

    private final VenteRepository venteRepository;

    public TicketCaisseService(
        TicketCaisseRepository ticketCaisseRepository,
        TicketCaisseMapper ticketCaisseMapper,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService,
        VenteRepository venteRepository
    ) {
        this.ticketCaisseRepository = ticketCaisseRepository;
        this.ticketCaisseMapper = ticketCaisseMapper;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
        this.venteRepository = venteRepository;
    }

    /**
     * Save a ticketCaisse.
     *
     * @param ticketCaisseDTO the entity to save.
     * @return the persisted entity.
     */
    public TicketCaisseDTO save(TicketCaisseDTO ticketCaisseDTO) {
        LOG.debug("Request to save TicketCaisse : {}", ticketCaisseDTO);
        TicketCaisse ticketCaisse = ticketCaisseMapper.toEntity(ticketCaisseDTO);
        attachSaleAndAssertAccess(ticketCaisse, "Acces refuse au ticket de caisse a creer");
        ticketCaisse = ticketCaisseRepository.save(ticketCaisse);
        audit(TypeActionAudit.CREATION, ticketCaisse, "Creation ticket caisse numero=" + ticketCaisse.getNumero());
        return ticketCaisseMapper.toDto(ticketCaisse);
    }

    /**
     * Update a ticketCaisse.
     *
     * @param ticketCaisseDTO the entity to save.
     * @return the persisted entity.
     */
    public TicketCaisseDTO update(TicketCaisseDTO ticketCaisseDTO) {
        LOG.debug("Request to update TicketCaisse : {}", ticketCaisseDTO);
        TicketCaisse ticketCaisse = ticketCaisseMapper.toEntity(ticketCaisseDTO);
        attachSaleAndAssertAccess(ticketCaisse, "Acces refuse au ticket de caisse a modifier");
        ticketCaisse = ticketCaisseRepository.save(ticketCaisse);
        audit(TypeActionAudit.MODIFICATION, ticketCaisse, "Modification ticket caisse numero=" + ticketCaisse.getNumero());
        return ticketCaisseMapper.toDto(ticketCaisse);
    }

    /**
     * Partially update a ticketCaisse.
     *
     * @param ticketCaisseDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TicketCaisseDTO> partialUpdate(TicketCaisseDTO ticketCaisseDTO) {
        LOG.debug("Request to partially update TicketCaisse : {}", ticketCaisseDTO);

        return ticketCaisseRepository
            .findOneWithEagerRelationships(ticketCaisseDTO.getId())
            .map(existingTicketCaisse -> {
                ticketCaisseMapper.partialUpdate(existingTicketCaisse, ticketCaisseDTO);
                attachSaleAndAssertAccess(existingTicketCaisse, "Acces refuse au ticket de caisse a modifier");

                return existingTicketCaisse;
            })
            .map(ticketCaisseRepository::save)
            .map(ticketCaisse -> {
                audit(
                    TypeActionAudit.MODIFICATION,
                    ticketCaisse,
                    "Modification partielle ticket caisse numero=" + ticketCaisse.getNumero()
                );
                return ticketCaisse;
            })
            .map(ticketCaisseMapper::toDto);
    }

    /**
     * Get all the ticketCaisses with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TicketCaisseDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ticketCaisseRepository.findAllWithEagerRelationships(pageable).map(ticketCaisseMapper::toDto);
    }

    /**
     * Get one ticketCaisse by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TicketCaisseDTO> findOne(Long id) {
        LOG.debug("Request to get TicketCaisse : {}", id);
        return ticketCaisseRepository
            .findOneWithEagerRelationships(id)
            .map(ticketCaisse -> {
                attachSaleAndAssertAccess(ticketCaisse, "Acces refuse au ticket de caisse demande");
                return ticketCaisse;
            })
            .map(ticketCaisseMapper::toDto);
    }

    /**
     * Delete the ticketCaisse by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TicketCaisse : {}", id);
        TicketCaisse ticketCaisse = ticketCaisseRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("ticketCaisse", "notFound", "Ticket de caisse introuvable"));
        attachSaleAndAssertAccess(ticketCaisse, "Acces refuse au ticket de caisse a supprimer");
        audit(TypeActionAudit.DESACTIVATION, ticketCaisse, "Suppression ticket caisse numero=" + ticketCaisse.getNumero());
        ticketCaisseRepository.deleteById(id);
    }

    private void attachSaleAndAssertAccess(TicketCaisse ticketCaisse, String message) {
        Vente vente = resolveSale(ticketCaisse.getVente());
        moduleSecurityService.assertBoutiqueAccess(vente.getBoutique().getId(), message);
        ticketCaisse.setVente(vente);
    }

    private Vente resolveSale(Vente saleReference) {
        if (saleReference == null || saleReference.getId() == null) {
            throw new BusinessValidationException("ticketCaisse", "missingSale", "La vente est obligatoire");
        }
        return venteRepository
            .findOneWithEagerRelationships(saleReference.getId())
            .orElseThrow(() -> new BusinessValidationException("ticketCaisse", "saleNotFound", "Vente introuvable"));
    }

    private void audit(TypeActionAudit action, TicketCaisse ticketCaisse, String description) {
        journalAuditService.logAction(
            action,
            "TicketCaisse",
            ticketCaisse.getNumero(),
            description,
            ticketCaisse.getVente().getBoutique(),
            moduleSecurityService.getCurrentUser()
        );
    }
}
