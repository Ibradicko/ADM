package com.adm.supervision.service;

import com.adm.supervision.domain.PaiementVente;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.StatutVente;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.PaiementVenteRepository;
import com.adm.supervision.repository.VenteRepository;
import com.adm.supervision.service.dto.PaiementVenteDTO;
import com.adm.supervision.service.mapper.PaiementVenteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.PaiementVente}.
 */
@Service
@Transactional
public class PaiementVenteService {

    private static final Logger LOG = LoggerFactory.getLogger(PaiementVenteService.class);

    private final PaiementVenteRepository paiementVenteRepository;

    private final PaiementVenteMapper paiementVenteMapper;

    private final ModuleSecurityService moduleSecurityService;

    private final JournalAuditService journalAuditService;

    private final VenteRepository venteRepository;

    public PaiementVenteService(
        PaiementVenteRepository paiementVenteRepository,
        PaiementVenteMapper paiementVenteMapper,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService,
        VenteRepository venteRepository
    ) {
        this.paiementVenteRepository = paiementVenteRepository;
        this.paiementVenteMapper = paiementVenteMapper;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
        this.venteRepository = venteRepository;
    }

    /**
     * Save a paiementVente.
     *
     * @param paiementVenteDTO the entity to save.
     * @return the persisted entity.
     */
    public PaiementVenteDTO save(PaiementVenteDTO paiementVenteDTO) {
        LOG.debug("Request to save PaiementVente : {}", paiementVenteDTO);
        PaiementVente paiementVente = paiementVenteMapper.toEntity(paiementVenteDTO);
        Vente vente = loadEditableSale(paiementVente.getVente());
        assertAccessible(vente, "Acces refuse au paiement de vente a creer");
        paiementVente.setVente(vente);
        paiementVente = paiementVenteRepository.save(paiementVente);
        audit(TypeActionAudit.CREATION, paiementVente, "Creation paiement vente reference=" + paiementVente.getReferencePaiement());
        return paiementVenteMapper.toDto(paiementVente);
    }

    /**
     * Update a paiementVente.
     *
     * @param paiementVenteDTO the entity to save.
     * @return the persisted entity.
     */
    public PaiementVenteDTO update(PaiementVenteDTO paiementVenteDTO) {
        LOG.debug("Request to update PaiementVente : {}", paiementVenteDTO);
        loadEditableSale(getExisting(paiementVenteDTO.getId()).getVente());
        PaiementVente paiementVente = paiementVenteMapper.toEntity(paiementVenteDTO);
        Vente vente = loadEditableSale(paiementVente.getVente());
        assertAccessible(vente, "Acces refuse au paiement de vente a modifier");
        paiementVente.setVente(vente);
        paiementVente = paiementVenteRepository.save(paiementVente);
        audit(TypeActionAudit.MODIFICATION, paiementVente, "Modification paiement vente reference=" + paiementVente.getReferencePaiement());
        return paiementVenteMapper.toDto(paiementVente);
    }

    /**
     * Partially update a paiementVente.
     *
     * @param paiementVenteDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PaiementVenteDTO> partialUpdate(PaiementVenteDTO paiementVenteDTO) {
        LOG.debug("Request to partially update PaiementVente : {}", paiementVenteDTO);

        return paiementVenteRepository
            .findById(paiementVenteDTO.getId())
            .map(existingPaiementVente -> {
                loadEditableSale(existingPaiementVente.getVente());
                paiementVenteMapper.partialUpdate(existingPaiementVente, paiementVenteDTO);
                Vente vente = loadEditableSale(existingPaiementVente.getVente());
                assertAccessible(vente, "Acces refuse au paiement de vente a modifier");
                existingPaiementVente.setVente(vente);

                return existingPaiementVente;
            })
            .map(paiementVenteRepository::save)
            .map(paiementVente -> {
                audit(
                    TypeActionAudit.MODIFICATION,
                    paiementVente,
                    "Modification partielle paiement vente reference=" + paiementVente.getReferencePaiement()
                );
                return paiementVente;
            })
            .map(paiementVenteMapper::toDto);
    }

    /**
     * Get all the paiementVentes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PaiementVenteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return paiementVenteRepository.findAllWithEagerRelationships(pageable).map(paiementVenteMapper::toDto);
    }

    /**
     * Get one paiementVente by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PaiementVenteDTO> findOne(Long id) {
        LOG.debug("Request to get PaiementVente : {}", id);
        return paiementVenteRepository
            .findOneWithEagerRelationships(id)
            .map(paiementVente -> {
                Vente vente = loadSale(paiementVente.getVente());
                assertAccessible(vente, "Acces refuse au paiement de vente demande");
                paiementVente.setVente(vente);
                return paiementVente;
            })
            .map(paiementVenteMapper::toDto);
    }

    /**
     * Delete the paiementVente by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PaiementVente : {}", id);
        PaiementVente paiementVente = paiementVenteRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("paiementVente", "notFound", "Paiement vente introuvable"));
        Vente vente = loadEditableSale(paiementVente.getVente());
        assertAccessible(vente, "Acces refuse au paiement de vente a supprimer");
        paiementVente.setVente(vente);
        audit(TypeActionAudit.DESACTIVATION, paiementVente, "Suppression paiement vente reference=" + paiementVente.getReferencePaiement());
        paiementVenteRepository.deleteById(id);
    }

    private void assertAccessible(Vente vente, String message) {
        moduleSecurityService.assertBoutiqueAccess(vente.getBoutique().getId(), message);
    }

    private void audit(TypeActionAudit action, PaiementVente paiementVente, String description) {
        journalAuditService.logAction(
            action,
            "PaiementVente",
            paiementVente.getReferencePaiement(),
            description,
            paiementVente.getVente().getBoutique(),
            moduleSecurityService.getCurrentUser()
        );
    }

    private PaiementVente getExisting(Long id) {
        return paiementVenteRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("paiementVente", "notFound", "Paiement vente introuvable"));
    }

    private Vente loadSale(Vente saleReference) {
        if (saleReference == null || saleReference.getId() == null) {
            throw new BusinessValidationException("paiementVente", "missingSale", "La vente est obligatoire");
        }
        return venteRepository
            .findOneWithEagerRelationships(saleReference.getId())
            .orElseThrow(() -> new BusinessValidationException("paiementVente", "saleNotFound", "Vente introuvable"));
    }

    private Vente loadEditableSale(Vente saleReference) {
        Vente sale = loadSale(saleReference);
        if (sale.getStatut() != StatutVente.BROUILLON) {
            throw new BusinessValidationException(
                "paiementVente",
                "validatedSaleImmutable",
                "Les paiements d'une vente validee ne peuvent pas etre modifies directement"
            );
        }
        return sale;
    }
}
