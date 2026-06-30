package com.adm.supervision.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A EtiquetteProduit.
 */
@Entity
@Table(name = "etiquette_produit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EtiquetteProduit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantite", nullable = false)
    private Integer quantite;

    @NotNull
    @Column(name = "imprimee", nullable = false)
    private Boolean imprimee;

    @Column(name = "date_impression")
    private Instant dateImpression;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "boutique", "groupeArticle", "familleArticle", "sousFamilleArticle", "uniteMesure" },
        allowSetters = true
    )
    private Produit produit;

    @ManyToOne(optional = false)
    @NotNull
    private LotEtiquettes lot;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EtiquetteProduit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantite() {
        return this.quantite;
    }

    public EtiquetteProduit quantite(Integer quantite) {
        this.setQuantite(quantite);
        return this;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Boolean getImprimee() {
        return this.imprimee;
    }

    public EtiquetteProduit imprimee(Boolean imprimee) {
        this.setImprimee(imprimee);
        return this;
    }

    public void setImprimee(Boolean imprimee) {
        this.imprimee = imprimee;
    }

    public Instant getDateImpression() {
        return this.dateImpression;
    }

    public EtiquetteProduit dateImpression(Instant dateImpression) {
        this.setDateImpression(dateImpression);
        return this;
    }

    public void setDateImpression(Instant dateImpression) {
        this.dateImpression = dateImpression;
    }

    public Produit getProduit() {
        return this.produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public EtiquetteProduit produit(Produit produit) {
        this.setProduit(produit);
        return this;
    }

    public LotEtiquettes getLot() {
        return this.lot;
    }

    public void setLot(LotEtiquettes lotEtiquettes) {
        this.lot = lotEtiquettes;
    }

    public EtiquetteProduit lot(LotEtiquettes lotEtiquettes) {
        this.setLot(lotEtiquettes);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EtiquetteProduit)) {
            return false;
        }
        return getId() != null && getId().equals(((EtiquetteProduit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EtiquetteProduit{" +
            "id=" + getId() +
            ", quantite=" + getQuantite() +
            ", imprimee='" + getImprimee() + "'" +
            ", dateImpression='" + getDateImpression() + "'" +
            "}";
    }
}
