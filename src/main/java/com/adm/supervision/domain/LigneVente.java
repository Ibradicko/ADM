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
 * A LigneVente.
 */
@Entity
@Table(name = "ligne_vente")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneVente implements Serializable {

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

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "prix_unitaire", precision = 21, scale = 2, nullable = false)
    private BigDecimal prixUnitaire;

    @DecimalMin(value = "0")
    @Column(name = "remise", precision = 21, scale = 2)
    private BigDecimal remise;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "montant_ligne", precision = 21, scale = 2, nullable = false)
    private BigDecimal montantLigne;

    @Size(max = 80)
    @Column(name = "code_barres_scanne", length = 80)
    private String codeBarresScanne;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "boutique", "locataire", "vendeur" }, allowSetters = true)
    private Vente vente;

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

    public LigneVente id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantite() {
        return this.quantite;
    }

    public LigneVente quantite(BigDecimal quantite) {
        this.setQuantite(quantite);
        return this;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return this.prixUnitaire;
    }

    public LigneVente prixUnitaire(BigDecimal prixUnitaire) {
        this.setPrixUnitaire(prixUnitaire);
        return this;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getRemise() {
        return this.remise;
    }

    public LigneVente remise(BigDecimal remise) {
        this.setRemise(remise);
        return this;
    }

    public void setRemise(BigDecimal remise) {
        this.remise = remise;
    }

    public BigDecimal getMontantLigne() {
        return this.montantLigne;
    }

    public LigneVente montantLigne(BigDecimal montantLigne) {
        this.setMontantLigne(montantLigne);
        return this;
    }

    public void setMontantLigne(BigDecimal montantLigne) {
        this.montantLigne = montantLigne;
    }

    public String getCodeBarresScanne() {
        return this.codeBarresScanne;
    }

    public LigneVente codeBarresScanne(String codeBarresScanne) {
        this.setCodeBarresScanne(codeBarresScanne);
        return this;
    }

    public void setCodeBarresScanne(String codeBarresScanne) {
        this.codeBarresScanne = codeBarresScanne;
    }

    public Vente getVente() {
        return this.vente;
    }

    public void setVente(Vente vente) {
        this.vente = vente;
    }

    public LigneVente vente(Vente vente) {
        this.setVente(vente);
        return this;
    }

    public Produit getProduit() {
        return this.produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public LigneVente produit(Produit produit) {
        this.setProduit(produit);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LigneVente)) {
            return false;
        }
        return getId() != null && getId().equals(((LigneVente) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneVente{" +
            "id=" + getId() +
            ", quantite=" + getQuantite() +
            ", prixUnitaire=" + getPrixUnitaire() +
            ", remise=" + getRemise() +
            ", montantLigne=" + getMontantLigne() +
            ", codeBarresScanne='" + getCodeBarresScanne() + "'" +
            "}";
    }
}
