package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.LigneTransfertStock} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneTransfertStockDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal quantite;

    @Size(max = 255)
    private String commentaire;

    @NotNull
    private TransfertStockDTO transfert;

    @NotNull
    private ProduitDTO produit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public TransfertStockDTO getTransfert() {
        return transfert;
    }

    public void setTransfert(TransfertStockDTO transfert) {
        this.transfert = transfert;
    }

    public ProduitDTO getProduit() {
        return produit;
    }

    public void setProduit(ProduitDTO produit) {
        this.produit = produit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LigneTransfertStockDTO)) {
            return false;
        }

        LigneTransfertStockDTO ligneTransfertStockDTO = (LigneTransfertStockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ligneTransfertStockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneTransfertStockDTO{" +
            "id=" + getId() +
            ", quantite=" + getQuantite() +
            ", commentaire='" + getCommentaire() + "'" +
            ", transfert=" + getTransfert() +
            ", produit=" + getProduit() +
            "}";
    }
}
