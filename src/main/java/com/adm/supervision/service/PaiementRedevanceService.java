package com.adm.supervision.service;

import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.PaiementRedevance;
import com.adm.supervision.domain.enumeration.StatutRedevance;
import com.adm.supervision.domain.enumeration.TypeActionAudit;
import com.adm.supervision.repository.CalculRedevanceRepository;
import com.adm.supervision.repository.PaiementRedevanceRepository;
import com.adm.supervision.service.dto.PaiementRedevanceDTO;
import com.adm.supervision.service.mapper.PaiementRedevanceMapper;
import java.math.BigDecimal;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.adm.supervision.domain.PaiementRedevance}.
 */
@Service
@Transactional
public class PaiementRedevanceService {

    private static final Logger LOG = LoggerFactory.getLogger(PaiementRedevanceService.class);

    private final PaiementRedevanceRepository paiementRedevanceRepository;

    private final PaiementRedevanceMapper paiementRedevanceMapper;

    private final ModuleSecurityService moduleSecurityService;

    private final JournalAuditService journalAuditService;

    private final CalculRedevanceRepository calculRedevanceRepository;

    public PaiementRedevanceService(
        PaiementRedevanceRepository paiementRedevanceRepository,
        PaiementRedevanceMapper paiementRedevanceMapper,
        ModuleSecurityService moduleSecurityService,
        JournalAuditService journalAuditService,
        CalculRedevanceRepository calculRedevanceRepository
    ) {
        this.paiementRedevanceRepository = paiementRedevanceRepository;
        this.paiementRedevanceMapper = paiementRedevanceMapper;
        this.moduleSecurityService = moduleSecurityService;
        this.journalAuditService = journalAuditService;
        this.calculRedevanceRepository = calculRedevanceRepository;
    }

    /**
     * Save a paiementRedevance.
     *
     * @param paiementRedevanceDTO the entity to save.
     * @return the persisted entity.
     */
    public PaiementRedevanceDTO save(PaiementRedevanceDTO paiementRedevanceDTO) {
        LOG.debug("Request to save PaiementRedevance : {}", paiementRedevanceDTO);
        PaiementRedevance paiementRedevance = paiementRedevanceMapper.toEntity(paiementRedevanceDTO);
        validatePayment(paiementRedevance, null);
        assertAccessible(paiementRedevance, "Acces refuse au paiement de redevance a creer");
        paiementRedevance = paiementRedevanceRepository.save(paiementRedevance);
        refreshCalculationStatus(paiementRedevance.getCalcul());
        audit(TypeActionAudit.CREATION, paiementRedevance, "Creation paiement redevance reference=" + paiementRedevance.getReference());
        return paiementRedevanceMapper.toDto(paiementRedevance);
    }

    /**
     * Update a paiementRedevance.
     *
     * @param paiementRedevanceDTO the entity to save.
     * @return the persisted entity.
     */
    public PaiementRedevanceDTO update(PaiementRedevanceDTO paiementRedevanceDTO) {
        LOG.debug("Request to update PaiementRedevance : {}", paiementRedevanceDTO);
        PaiementRedevance paiementRedevance = paiementRedevanceMapper.toEntity(paiementRedevanceDTO);
        validatePayment(paiementRedevance, paiementRedevance.getId());
        assertAccessible(paiementRedevance, "Acces refuse au paiement de redevance a modifier");
        paiementRedevance = paiementRedevanceRepository.save(paiementRedevance);
        refreshCalculationStatus(paiementRedevance.getCalcul());
        audit(
            TypeActionAudit.MODIFICATION,
            paiementRedevance,
            "Modification paiement redevance reference=" + paiementRedevance.getReference()
        );
        return paiementRedevanceMapper.toDto(paiementRedevance);
    }

    /**
     * Partially update a paiementRedevance.
     *
     * @param paiementRedevanceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PaiementRedevanceDTO> partialUpdate(PaiementRedevanceDTO paiementRedevanceDTO) {
        LOG.debug("Request to partially update PaiementRedevance : {}", paiementRedevanceDTO);

        return paiementRedevanceRepository
            .findById(paiementRedevanceDTO.getId())
            .map(existingPaiementRedevance -> {
                paiementRedevanceMapper.partialUpdate(existingPaiementRedevance, paiementRedevanceDTO);
                assertAccessible(existingPaiementRedevance, "Acces refuse au paiement de redevance a modifier");
                validatePayment(existingPaiementRedevance, existingPaiementRedevance.getId());

                return existingPaiementRedevance;
            })
            .map(paiementRedevanceRepository::save)
            .map(paiementRedevance -> {
                refreshCalculationStatus(paiementRedevance.getCalcul());
                audit(
                    TypeActionAudit.MODIFICATION,
                    paiementRedevance,
                    "Modification partielle paiement redevance reference=" + paiementRedevance.getReference()
                );
                return paiementRedevance;
            })
            .map(paiementRedevanceMapper::toDto);
    }

    /**
     * Get all the paiementRedevances with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PaiementRedevanceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return paiementRedevanceRepository.findAllWithEagerRelationships(pageable).map(paiementRedevanceMapper::toDto);
    }

    /**
     * Get one paiementRedevance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PaiementRedevanceDTO> findOne(Long id) {
        LOG.debug("Request to get PaiementRedevance : {}", id);
        return paiementRedevanceRepository
            .findOneWithEagerRelationships(id)
            .map(paiementRedevance -> {
                assertAccessible(paiementRedevance, "Acces refuse au paiement de redevance demande");
                return paiementRedevance;
            })
            .map(paiementRedevanceMapper::toDto);
    }

    /**
     * Delete the paiementRedevance by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PaiementRedevance : {}", id);
        PaiementRedevance paiementRedevance = paiementRedevanceRepository
            .findOneWithEagerRelationships(id)
            .orElseThrow(() -> new BusinessValidationException("paiementRedevance", "notFound", "Paiement redevance introuvable"));
        assertAccessible(paiementRedevance, "Acces refuse au paiement de redevance a supprimer");
        audit(
            TypeActionAudit.DESACTIVATION,
            paiementRedevance,
            "Suppression paiement redevance reference=" + paiementRedevance.getReference()
        );
        paiementRedevanceRepository.deleteById(id);
        paiementRedevanceRepository.flush();
        refreshCalculationStatus(paiementRedevance.getCalcul());
    }

    private void validatePayment(PaiementRedevance payment, Long excludedPaymentId) {
        if (payment.getMontant() == null || payment.getMontant().signum() <= 0) {
            throw new BusinessValidationException(
                "paiementRedevance",
                "invalidAmount",
                "Le montant du paiement doit etre strictement positif"
            );
        }
        CalculRedevance calcul = calculRedevanceRepository
            .findById(payment.getCalcul().getId())
            .orElseThrow(() ->
                new BusinessValidationException("paiementRedevance", "calculationNotFound", "Calcul de redevance introuvable")
            );
        payment.setCalcul(calcul);
        if (calcul.getStatut() == StatutRedevance.ANNULEE) {
            throw new BusinessValidationException("paiementRedevance", "cancelledCalculation", "Un calcul annule ne peut pas etre paye");
        }
        BigDecimal alreadyPaid = paiementRedevanceRepository
            .findAllByCalcul_Id(calcul.getId())
            .stream()
            .filter(existing -> excludedPaymentId == null || !excludedPaymentId.equals(existing.getId()))
            .map(PaiementRedevance::getMontant)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (alreadyPaid.add(payment.getMontant()).compareTo(calcul.getMontantRedevance()) > 0) {
            throw new BusinessValidationException(
                "paiementRedevance",
                "overpayment",
                "Le total des paiements ne peut pas depasser le montant de la redevance"
            );
        }
    }

    private void refreshCalculationStatus(CalculRedevance calcul) {
        BigDecimal paid = paiementRedevanceRepository.sumMontantByCalculId(calcul.getId());
        if (paid == null || paid.signum() == 0) {
            if (calcul.getStatut() == StatutRedevance.PAYEE || calcul.getStatut() == StatutRedevance.PARTIELLEMENT_PAYEE) {
                calcul.setStatut(StatutRedevance.VALIDEE);
            }
        } else if (paid.compareTo(calcul.getMontantRedevance()) >= 0) {
            calcul.setStatut(StatutRedevance.PAYEE);
        } else {
            calcul.setStatut(StatutRedevance.PARTIELLEMENT_PAYEE);
        }
        calculRedevanceRepository.save(calcul);
    }

    private void assertAccessible(PaiementRedevance paiementRedevance, String message) {
        moduleSecurityService.assertBoutiqueAccess(paiementRedevance.getCalcul().getBoutique().getId(), message);
    }

    private void audit(TypeActionAudit action, PaiementRedevance paiementRedevance, String description) {
        journalAuditService.logAction(
            action,
            "PaiementRedevance",
            paiementRedevance.getReference(),
            description,
            paiementRedevance.getCalcul().getBoutique(),
            moduleSecurityService.getCurrentUser()
        );
    }
}
