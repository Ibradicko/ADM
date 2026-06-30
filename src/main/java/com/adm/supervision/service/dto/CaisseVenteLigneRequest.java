package com.adm.supervision.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

public class CaisseVenteLigneRequest implements Serializable {

    @NotNull
    private Long produitId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal quantite;

    @DecimalMin(value = "0")
    private BigDecimal remise;

    @Size(max = 80)
    private String codeBarresScanne;

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getRemise() {
        return remise;
    }

    public void setRemise(BigDecimal remise) {
        this.remise = remise;
    }

    public String getCodeBarresScanne() {
        return codeBarresScanne;
    }

    public void setCodeBarresScanne(String codeBarresScanne) {
        this.codeBarresScanne = codeBarresScanne;
    }
}
