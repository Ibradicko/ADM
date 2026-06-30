package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.LigneMouvementStock} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneMouvementStockDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal quantite;

    private BigDecimal stockAvant;

    private BigDecimal stockApres;

    @Size(max = 255)
    private String commentaire;

    @NotNull
    private MouvementStockDTO mouvement;

    @NotNull
    private ProduitDTO produit;

    @NotNull
    private DepotStockDTO depot;

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

    public BigDecimal getStockAvant() {
        return stockAvant;
    }

    public void setStockAvant(BigDecimal stockAvant) {
        this.stockAvant = stockAvant;
    }

    public BigDecimal getStockApres() {
        return stockApres;
    }

    public void setStockApres(BigDecimal stockApres) {
        this.stockApres = stockApres;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public MouvementStockDTO getMouvement() {
        return mouvement;
    }

    public void setMouvement(MouvementStockDTO mouvement) {
        this.mouvement = mouvement;
    }

    public ProduitDTO getProduit() {
        return produit;
    }

    public void setProduit(ProduitDTO produit) {
        this.produit = produit;
    }

    public DepotStockDTO getDepot() {
        return depot;
    }

    public void setDepot(DepotStockDTO depot) {
        this.depot = depot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LigneMouvementStockDTO)) {
            return false;
        }

        LigneMouvementStockDTO ligneMouvementStockDTO = (LigneMouvementStockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ligneMouvementStockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneMouvementStockDTO{" +
            "id=" + getId() +
            ", quantite=" + getQuantite() +
            ", stockAvant=" + getStockAvant() +
            ", stockApres=" + getStockApres() +
            ", commentaire='" + getCommentaire() + "'" +
            ", mouvement=" + getMouvement() +
            ", produit=" + getProduit() +
            ", depot=" + getDepot() +
            "}";
    }
}
