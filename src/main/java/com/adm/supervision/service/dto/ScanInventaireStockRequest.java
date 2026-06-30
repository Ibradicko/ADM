package com.adm.supervision.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class ScanInventaireStockRequest {

    private Long produitId;

    @Size(max = 80)
    private String codeBarres;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal quantiteComptee;

    @Size(max = 255)
    private String commentaire;

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public String getCodeBarres() {
        return codeBarres;
    }

    public void setCodeBarres(String codeBarres) {
        this.codeBarres = codeBarres;
    }

    public BigDecimal getQuantiteComptee() {
        return quantiteComptee;
    }

    public void setQuantiteComptee(BigDecimal quantiteComptee) {
        this.quantiteComptee = quantiteComptee;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
