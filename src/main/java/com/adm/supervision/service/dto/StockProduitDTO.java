package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.StockProduit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockProduitDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal quantiteTheorique;

    @DecimalMin(value = "0")
    private BigDecimal stockAlerte;

    private Instant dateDernierMouvement;

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

    public BigDecimal getQuantiteTheorique() {
        return quantiteTheorique;
    }

    public void setQuantiteTheorique(BigDecimal quantiteTheorique) {
        this.quantiteTheorique = quantiteTheorique;
    }

    public BigDecimal getStockAlerte() {
        return stockAlerte;
    }

    public void setStockAlerte(BigDecimal stockAlerte) {
        this.stockAlerte = stockAlerte;
    }

    public Instant getDateDernierMouvement() {
        return dateDernierMouvement;
    }

    public void setDateDernierMouvement(Instant dateDernierMouvement) {
        this.dateDernierMouvement = dateDernierMouvement;
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
        if (!(o instanceof StockProduitDTO)) {
            return false;
        }

        StockProduitDTO stockProduitDTO = (StockProduitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockProduitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockProduitDTO{" +
            "id=" + getId() +
            ", quantiteTheorique=" + getQuantiteTheorique() +
            ", stockAlerte=" + getStockAlerte() +
            ", dateDernierMouvement='" + getDateDernierMouvement() + "'" +
            ", produit=" + getProduit() +
            ", depot=" + getDepot() +
            "}";
    }
}
