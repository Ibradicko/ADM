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
 * A LigneReceptionProduit.
 */
@Entity
@Table(name = "ligne_reception_produit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneReceptionProduit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @DecimalMin(value = "0")
    @Column(name = "quantite_attendue", precision = 21, scale = 2)
    private BigDecimal quantiteAttendue;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "quantite_recue", precision = 21, scale = 2, nullable = false)
    private BigDecimal quantiteRecue;

    @Column(name = "ecart", precision = 21, scale = 2)
    private BigDecimal ecart;

    @Size(max = 80)
    @Column(name = "code_barres_scanne", length = 80)
    private String codeBarresScanne;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "boutique", "utilisateur" }, allowSetters = true)
    private ReceptionProduit reception;

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

    public LigneReceptionProduit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantiteAttendue() {
        return this.quantiteAttendue;
    }

    public LigneReceptionProduit quantiteAttendue(BigDecimal quantiteAttendue) {
        this.setQuantiteAttendue(quantiteAttendue);
        return this;
    }

    public void setQuantiteAttendue(BigDecimal quantiteAttendue) {
        this.quantiteAttendue = quantiteAttendue;
    }

    public BigDecimal getQuantiteRecue() {
        return this.quantiteRecue;
    }

    public LigneReceptionProduit quantiteRecue(BigDecimal quantiteRecue) {
        this.setQuantiteRecue(quantiteRecue);
        return this;
    }

    public void setQuantiteRecue(BigDecimal quantiteRecue) {
        this.quantiteRecue = quantiteRecue;
    }

    public BigDecimal getEcart() {
        return this.ecart;
    }

    public LigneReceptionProduit ecart(BigDecimal ecart) {
        this.setEcart(ecart);
        return this;
    }

    public void setEcart(BigDecimal ecart) {
        this.ecart = ecart;
    }

    public String getCodeBarresScanne() {
        return this.codeBarresScanne;
    }

    public LigneReceptionProduit codeBarresScanne(String codeBarresScanne) {
        this.setCodeBarresScanne(codeBarresScanne);
        return this;
    }

    public void setCodeBarresScanne(String codeBarresScanne) {
        this.codeBarresScanne = codeBarresScanne;
    }

    public ReceptionProduit getReception() {
        return this.reception;
    }

    public void setReception(ReceptionProduit receptionProduit) {
        this.reception = receptionProduit;
    }

    public LigneReceptionProduit reception(ReceptionProduit receptionProduit) {
        this.setReception(receptionProduit);
        return this;
    }

    public Produit getProduit() {
        return this.produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public LigneReceptionProduit produit(Produit produit) {
        this.setProduit(produit);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LigneReceptionProduit)) {
            return false;
        }
        return getId() != null && getId().equals(((LigneReceptionProduit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneReceptionProduit{" +
            "id=" + getId() +
            ", quantiteAttendue=" + getQuantiteAttendue() +
            ", quantiteRecue=" + getQuantiteRecue() +
            ", ecart=" + getEcart() +
            ", codeBarresScanne='" + getCodeBarresScanne() + "'" +
            "}";
    }
}
