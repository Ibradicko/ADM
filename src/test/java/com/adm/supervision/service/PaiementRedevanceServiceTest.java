package com.adm.supervision.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.adm.supervision.domain.Boutique;
import com.adm.supervision.domain.CalculRedevance;
import com.adm.supervision.domain.PaiementRedevance;
import com.adm.supervision.domain.enumeration.StatutRedevance;
import com.adm.supervision.repository.CalculRedevanceRepository;
import com.adm.supervision.repository.PaiementRedevanceRepository;
import com.adm.supervision.service.dto.PaiementRedevanceDTO;
import com.adm.supervision.service.mapper.PaiementRedevanceMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaiementRedevanceServiceTest {

    @Mock
    private PaiementRedevanceRepository paiementRepository;

    @Mock
    private PaiementRedevanceMapper paiementMapper;

    @Mock
    private ModuleSecurityService moduleSecurityService;

    @Mock
    private JournalAuditService journalAuditService;

    @Mock
    private CalculRedevanceRepository calculRepository;

    private PaiementRedevanceService service;
    private CalculRedevance calculation;

    @BeforeEach
    void setUp() {
        service = new PaiementRedevanceService(
            paiementRepository,
            paiementMapper,
            moduleSecurityService,
            journalAuditService,
            calculRepository
        );
        calculation = new CalculRedevance()
            .id(10L)
            .reference("RED-10")
            .montantRedevance(new BigDecimal("100"))
            .statut(StatutRedevance.VALIDEE)
            .boutique(new Boutique().id(1L));
    }

    @Test
    void marksCalculationAsPartiallyPaidThenPaid() {
        PaiementRedevance payment = payment("40");
        when(paiementMapper.toEntity(any(PaiementRedevanceDTO.class))).thenReturn(payment);
        when(calculRepository.findById(10L)).thenReturn(Optional.of(calculation));
        when(paiementRepository.findAllByCalcul_Id(10L)).thenReturn(List.of());
        when(paiementRepository.save(payment)).thenReturn(payment);
        when(paiementRepository.sumMontantByCalculId(10L)).thenReturn(new BigDecimal("40"));

        service.save(new PaiementRedevanceDTO());

        assertThat(calculation.getStatut()).isEqualTo(StatutRedevance.PARTIELLEMENT_PAYEE);

        when(paiementRepository.sumMontantByCalculId(10L)).thenReturn(new BigDecimal("100"));
        service.save(new PaiementRedevanceDTO());

        assertThat(calculation.getStatut()).isEqualTo(StatutRedevance.PAYEE);
    }

    @Test
    void rejectsOverpayment() {
        PaiementRedevance payment = payment("60");
        PaiementRedevance previous = new PaiementRedevance().id(20L).montant(new BigDecimal("50")).calcul(calculation);
        when(paiementMapper.toEntity(any(PaiementRedevanceDTO.class))).thenReturn(payment);
        when(calculRepository.findById(10L)).thenReturn(Optional.of(calculation));
        when(paiementRepository.findAllByCalcul_Id(10L)).thenReturn(List.of(previous));

        assertThatThrownBy(() -> service.save(new PaiementRedevanceDTO()))
            .isInstanceOf(BusinessValidationException.class)
            .hasMessageContaining("depasser");
    }

    private PaiementRedevance payment(String amount) {
        return new PaiementRedevance().reference("PAY-1").montant(new BigDecimal(amount)).calcul(new CalculRedevance().id(10L));
    }
}
