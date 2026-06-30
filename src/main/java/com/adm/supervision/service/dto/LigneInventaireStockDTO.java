package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.LigneInventaireStock} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneInventaireStockDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal quantiteTheorique;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal quantiteComptee;

    private BigDecimal ecart;

    @Size(max = 255)
    private String commentaire;

    @NotNull
    private InventaireStockDTO inventaire;

    @NotNull
    private ProduitDTO produit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantiteTheorique() {
        return quantiteTheorique;
    }

    public void setQuantiteTheorique(BigDecimal quantiteTheorique) {
        this.quantiteTheorique = quantiteTheorique;
    }

    public BigDecimal getQuantiteComptee() {
        return quantiteComptee;
    }

    public void setQuantiteComptee(BigDecimal quantiteComptee) {
        this.quantiteComptee = quantiteComptee;
    }

    public BigDecimal getEcart() {
        return ecart;
    }

    public void setEcart(BigDecimal ecart) {
        this.ecart = ecart;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public InventaireStockDTO getInventaire() {
        return inventaire;
    }

    public void setInventaire(InventaireStockDTO inventaire) {
        this.inventaire = inventaire;
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
        if (!(o instanceof LigneInventaireStockDTO)) {
            return false;
        }

        LigneInventaireStockDTO ligneInventaireStockDTO = (LigneInventaireStockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ligneInventaireStockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneInventaireStockDTO{" +
            "id=" + getId() +
            ", quantiteTheorique=" + getQuantiteTheorique() +
            ", quantiteComptee=" + getQuantiteComptee() +
            ", ecart=" + getEcart() +
            ", commentaire='" + getCommentaire() + "'" +
            ", inventaire=" + getInventaire() +
            ", produit=" + getProduit() +
            "}";
    }
}
