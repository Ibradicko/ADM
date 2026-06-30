package com.adm.supervision.service;

import com.adm.supervision.domain.OperationCorrectiveVente;
import com.adm.supervision.domain.PaiementVente;
import com.adm.supervision.domain.Vente;
import com.adm.supervision.domain.enumeration.StatutPaiement;
import com.adm.supervision.domain.enumeration.StatutVente;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.domain.enumeration.TypeOperationCorrective;
import com.adm.supervision.repository.OperationCorrectiveVenteRepository;
import com.adm.supervision.repository.PaiementVenteRepository;
import com.adm.supervision.repository.VenteRepository;
import com.adm.supervision.service.dto.OperationCorrectiveVenteDTO;
import com.adm.supervision.service.mapper.OperationCorrectiveVenteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.OperationCorrectiveVente}.
 */
@Service
@Transactional
public class OperationCorrectiveVenteService {

    private static final Logger LOG = LoggerFactory.getLogger(OperationCorrectiveVenteService.class);

    private final OperationCorrectiveVenteRepository operationCorrectiveVenteRepository;

    private final OperationCorrectiveVenteMapper operationCorrectiveVenteMapper;

    private final VenteRepository venteRepository;

    private final PaiementVenteRepository paiementVenteRepository;

    private final ModuleSecurityService moduleSecurityService;

    private final JournalAuditService journalAuditService;

    public OperationCorrectiveVenteService(
        OperationCorrectiveVenteRepository operationCorrectiveVenteRepository,
        OperationCorrectiveVenteMapper operationCorrectiveVenteMapper,
        VenteRepository venteRepository,
        PaiementVenteRepository paiementVenteRepository,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService
    ) {
        this.operationCorrectiveVenteRepository = operationCorrectiveVenteRepository;
        this.operationCorrectiveVenteMapper = operationCorrectiveVenteMapper;
        this.venteRepository = venteRepository;
        this.paiementVenteRepository = paiementVenteRepository;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
    }

    /**
     * Save a operationCorrectiveVente.
     *
     * @param operationCorrectiveVenteDTO the entity to save.
     * @return the persisted entity.
     */
    public OperationCorrectiveVenteDTO save(OperationCorrectiveVenteDTO operationCorrectiveVenteDTO) {
        LOG.debug("Request to save OperationCorrectiveVente : {}", operationCorrectiveVenteDTO);
        OperationCorrectiveVente operationCorrectiveVente = operationCorrectiveVenteMapper.toEntity(operationCorrectiveVenteDTO);
        Vente vente = loadValidatedSale(operationCorrectiveVente);
        TypeOperationCorrective typeOperation = operationCorrectiveVente.getTypeOperation();
        StatutVente targetStatus = targetStatus(typeOperation);

        operationCorrectiveVente.setVente(vente);
        operationCorrectiveVente.setUtilisateur(moduleSecurityService.getCurrentUser());
        operationCorrectiveVente.setDateOperation(java.time.Instant.now());
        operationCorrectiveVente = operationCorrectiveVenteRepository.save(operationCorrectiveVente);

        vente.setStatut(targetStatus);
        venteRepository.save(vente);
        if (typeOperation == TypeOperationCorrective.ANNULATION || typeOperation == TypeOperationCorrective.RETOUR) {
            for (PaiementVente paiement : paiementVenteRepository.findAllByVenteId(vente.getId())) {
                paiement.setStatut(StatutPaiement.ANNULE);
            }
        }
        journalAuditService.logAction(
            auditAction(typeOperation),
            "Vente",
            vente.getNumeroTicket(),
            "Operation corrective " + typeOperation + " motif=" + operationCorrectiveVente.getMotif(),
            vente.getBoutique(),
            operationCorrectiveVente.getUtilisateur()
        );
        return operationCorrectiveVenteMapper.toDto(operationCorrectiveVente);
    }

    /**
     * Update a operationCorrectiveVente.
     *
     * @param operationCorrectiveVenteDTO the entity to save.
     * @return the persisted entity.
     */
    public OperationCorrectiveVenteDTO update(OperationCorrectiveVenteDTO operationCorrectiveVenteDTO) {
        throw immutableCorrection();
    }

    /**
     * Partially update a operationCorrectiveVente.
     *
     * @param operationCorrectiveVenteDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OperationCorrectiveVenteDTO> partialUpdate(OperationCorrectiveVenteDTO operationCorrectiveVenteDTO) {
        throw immutableCorrection();
    }

    /**
     * Get all the operationCorrectiveVentes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<OperationCorrectiveVenteDTO> findAllWithEagerRelationships(Pageable pageable) {
        return operationCorrectiveVenteRepository.findAllWithEagerRelationships(pageable).map(operationCorrectiveVenteMapper::toDto);
    }

    /**
     * Get one operationCorrectiveVente by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OperationCorrectiveVenteDTO> findOne(Long id) {
        LOG.debug("Request to get OperationCorrectiveVente : {}", id);
        return operationCorrectiveVenteRepository
            .findOneWithEagerRelationships(id)
            .map(operation -> {
                moduleSecurityService.assertBoutiqueAccess(
                    operation.getVente().getBoutique().getId(),
                    "Acces refuse a l'operation corrective demandee"
                );
                return operation;
            })
            .map(operationCorrectiveVenteMapper::toDto);
    }

    /**
     * Delete the operationCorrectiveVente by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        throw immutableCorrection();
    }

    private Vente loadValidatedSale(OperationCorrectiveVente operation) {
        if (operation.getVente() == null || operation.getVente().getId() == null) {
            throw new BusinessValidationException("operationCorrectiveVente", "missingSale", "La vente a corriger est obligatoire");
        }
        Vente vente = venteRepository
            .findOneWithEagerRelationships(operation.getVente().getId())
            .orElseThrow(() -> new BusinessValidationException("operationCorrectiveVente", "saleNotFound", "Vente introuvable"));
        moduleSecurityService.assertBoutiqueAccess(vente.getBoutique().getId(), "Acces refuse a la vente a corriger");
        if (vente.getStatut() != StatutVente.VALIDEE) {
            throw new BusinessValidationException(
                "operationCorrectiveVente",
                "invalidSaleStatus",
                "Seule une vente validee peut faire l'objet d'une operation corrective"
            );
        }
        return vente;
    }

    private StatutVente targetStatus(TypeOperationCorrective typeOperation) {
        return switch (typeOperation) {
            case ANNULATION -> StatutVente.ANNULEE;
            case RETOUR -> StatutVente.RETOURNEE;
            case AJUSTEMENT -> StatutVente.AJUSTEE;
        };
    }

    private TypeActionAudit auditAction(TypeOperationCorrective typeOperation) {
        return switch (typeOperation) {
            case ANNULATION -> TypeActionAudit.VENTE_ANNULEE;
            case RETOUR -> TypeActionAudit.RETOUR_VENTE;
            case AJUSTEMENT -> TypeActionAudit.MODIFICATION;
        };
    }

    private BusinessValidationException immutableCorrection() {
        return new BusinessValidationException(
            "operationCorrectiveVente",
            "immutableCorrection",
            "Une operation corrective tracee ne peut etre modifiee ni supprimee"
        );
    }
}
