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
 * A LigneInventaireStock.
 */
@Entity
@Table(name = "ligne_inventaire_stock")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneInventaireStock implements Serializable {

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

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "quantite_comptee", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantiteComptee;

    @Column(name = "ecart", precision = 21, scale = 2)
    private BigDecimal ecart;

    @Size(max = 255)
    @Column(name = "commentaire", length = 255)
    private String commentaire;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "boutique", "depot", "utilisateur" }, allowSetters = true)
    private InventaireStock inventaire;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "boutique", "groupeArticle", "familleArticle", "sousFamilleArticle", "uniteMesure" },
        allowSetters = true
    )
    private Produit produit;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LigneInventaireStock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantiteTheorique() {
        return this.quantiteTheorique;
    }

    public LigneInventaireStock quantiteTheorique(BigDecimal quantiteTheorique) {
        this.setQuantiteTheorique(quantiteTheorique);
        return this;
    }

    public void setQuantiteTheorique(BigDecimal quantiteTheorique) {
        this.quantiteTheorique = quantiteTheorique;
    }

    public BigDecimal getQuantiteComptee() {
        return this.quantiteComptee;
    }

    public LigneInventaireStock quantiteComptee(BigDecimal quantiteComptee) {
        this.setQuantiteComptee(quantiteComptee);
        return this;
    }

    public void setQuantiteComptee(BigDecimal quantiteComptee) {
        this.quantiteComptee = quantiteComptee;
    }

    public BigDecimal getEcart() {
        return this.ecart;
    }

    public LigneInventaireStock ecart(BigDecimal ecart) {
        this.setEcart(ecart);
        return this;
    }

    public void setEcart(BigDecimal ecart) {
        this.ecart = ecart;
    }

    public String getCommentaire() {
        return this.commentaire;
    }

    public LigneInventaireStock commentaire(String commentaire) {
        this.setCommentaire(commentaire);
        return this;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public InventaireStock getInventaire() {
        return this.inventaire;
    }

    public void setInventaire(InventaireStock inventaireStock) {
        this.inventaire = inventaireStock;
    }

    public LigneInventaireStock inventaire(InventaireStock inventaireStock) {
        this.setInventaire(inventaireStock);
        return this;
    }

    public Produit getProduit() {
        return this.produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public LigneInventaireStock produit(Produit produit) {
        this.setProduit(produit);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LigneInventaireStock)) {
            return false;
        }
        return getId() != null && getId().equals(((LigneInventaireStock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneInventaireStock{" +
            "id=" + getId() +
            ", quantiteTheorique=" + getQuantiteTheorique() +
            ", quantiteComptee=" + getQuantiteComptee() +
            ", ecart=" + getEcart() +
            ", commentaire='" + getCommentaire() + "'" +
            "}";
    }
}
