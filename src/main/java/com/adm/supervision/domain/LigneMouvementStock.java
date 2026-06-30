package com.adm.supervision.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LigneMouvementStock.
 */
@Entity
@Table(name = "ligne_mouvement_stock")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneMouvementStock implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "quantite", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantite;

    @Column(name = "stock_avant", precision = 21, scale = 2)
    private BigDecimal stockAvant;

    @Column(name = "stock_apres", precision = 21, scale = 2)
    private BigDecimal stockApres;

    @Size(max = 255)
    @Column(name = "commentaire", length = 255)
    private String commentaire;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "boutique", "utilisateur" }, allowSetters = true)
    private MouvementStock mouvement;

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

    public LigneMouvementStock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantite() {
        return this.quantite;
    }

    public LigneMouvementStock quantite(BigDecimal quantite) {
        this.setQuantite(quantite);
        return this;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getStockAvant() {
        return this.stockAvant;
    }

    public LigneMouvementStock stockAvant(BigDecimal stockAvant) {
        this.setStockAvant(stockAvant);
        return this;
    }

    public void setStockAvant(BigDecimal stockAvant) {
        this.stockAvant = stockAvant;
    }

    public BigDecimal getStockApres() {
        return this.stockApres;
    }

    public LigneMouvementStock stockApres(BigDecimal stockApres) {
        this.setStockApres(stockApres);
        return this;
    }

    public void setStockApres(BigDecimal stockApres) {
        this.stockApres = stockApres;
    }

    public String getCommentaire() {
        return this.commentaire;
    }

    public LigneMouvementStock commentaire(String commentaire) {
        this.setCommentaire(commentaire);
        return this;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public MouvementStock getMouvement() {
        return this.mouvement;
    }

    public void setMouvement(MouvementStock mouvementStock) {
        this.mouvement = mouvementStock;
    }

    public LigneMouvementStock mouvement(MouvementStock mouvementStock) {
        this.setMouvement(mouvementStock);
        return this;
    }

    public Produit getProduit() {
        return this.produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public LigneMouvementStock produit(Produit produit) {
        this.setProduit(produit);
        return this;
    }

    public DepotStock getDepot() {
        return this.depot;
    }

    public void setDepot(DepotStock depotStock) {
        this.depot = depotStock;
    }

    public LigneMouvementStock depot(DepotStock depotStock) {
        this.setDepot(depotStock);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LigneMouvementStock)) {
            return false;
        }
        return getId() != null && getId().equals(((LigneMouvementStock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneMouvementStock{" +
            "id=" + getId() +
            ", quantite=" + getQuantite() +
            ", stockAvant=" + getStockAvant() +
            ", stockApres=" + getStockApres() +
            ", commentaire='" + getCommentaire() + "'" +
            "}";
    }
}
