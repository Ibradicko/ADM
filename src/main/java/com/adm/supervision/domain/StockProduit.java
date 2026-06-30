package com.adm.supervision.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A StockProduit.
 */
@Entity
@Table(
    name = "stock_produit",
    uniqueConstraints = { @UniqueConstraint(name = "ux_stock_produit_produit_depot", columnNames = { "produit_id", "depot_id" }) }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockProduit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "quantite_theorique", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantiteTheorique;

    @DecimalMin(value = "0")
    @Column(name = "stock_alerte", precision = 21, scale = 2)
    private BigDecimal stockAlerte;

    @Column(name = "date_dernier_mouvement")
    private Instant dateDernierMouvement;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "boutique", "groupeArticle", "familleArticle", "sousFamilleArticle", "uniteMesure" },
        allowSetters = true
    )
    private Produit produit;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "boutique" }, allowSetters = true)
    private DepotStock depot;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockProduit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantiteTheorique() {
        return this.quantiteTheorique;
    }

    public StockProduit quantiteTheorique(BigDecimal quantiteTheorique) {
        this.setQuantiteTheorique(quantiteTheorique);
        return this;
    }

    public void setQuantiteTheorique(BigDecimal quantiteTheorique) {
        this.quantiteTheorique = quantiteTheorique;
    }

    public BigDecimal getStockAlerte() {
        return this.stockAlerte;
    }

    public StockProduit stockAlerte(BigDecimal stockAlerte) {
        this.setStockAlerte(stockAlerte);
        return this;
    }

    public void setStockAlerte(BigDecimal stockAlerte) {
        this.stockAlerte = stockAlerte;
    }

    public Instant getDateDernierMouvement() {
        return this.dateDernierMouvement;
    }

    public StockProduit dateDernierMouvement(Instant dateDernierMouvement) {
        this.setDateDernierMouvement(dateDernierMouvement);
        return this;
    }

    public void setDateDernierMouvement(Instant dateDernierMouvement) {
        this.dateDernierMouvement = dateDernierMouvement;
    }

    public Produit getProduit() {
        return this.produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public StockProduit produit(Produit produit) {
        this.setProduit(produit);
        return this;
    }

    public DepotStock getDepot() {
        return this.depot;
    }

    public void setDepot(DepotStock depotStock) {
        this.depot = depotStock;
    }

    public StockProduit depot(DepotStock depotStock) {
        this.setDepot(depotStock);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockProduit)) {
            return false;
        }
        return getId() != null && getId().equals(((StockProduit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockProduit{" +
            "id=" + getId() +
            ", quantiteTheorique=" + getQuantiteTheorique() +
            ", stockAlerte=" + getStockAlerte() +
            ", dateDernierMouvement='" + getDateDernierMouvement() + "'" +
            "}";
    }
}
