package com.adm.supervision.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

public class CaisseVentePaiementRequest implements Serializable {

    @NotNull
    private Long modePaiementId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal montant;

    @Size(max = 100)
    private String referencePaiement;

    public Long getModePaiementId() {
        return modePaiementId;
    }

    public void setModePaiementId(Long modePaiementId) {
        this.modePaiementId = modePaiementId;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getReferencePaiement() {
        return referencePaiement;
    }

    public void setReferencePaiement(String referencePaiement) {
        this.referencePaiement = referencePaiement;
    }
}
