package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.TypePrix;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A Produit.
 */
@Entity
@Table(name = "produit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Produit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "code_interne", length = 50, nullable = false)
    private String codeInterne;

    @NotNull
    @Size(max = 200)
    @Column(name = "designation", length = 200, nullable = false)
    private String designation;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_prix", nullable = false)
    private TypePrix typePrix;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "prix_vente", precision = 21, scale = 2, nullable = false)
    private BigDecimal prixVente;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "taux_redevance_applicable", precision = 21, scale = 2)
    private BigDecimal tauxRedevanceApplicable;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutGeneral statut;

    @NotNull
    @Column(name = "date_creation", nullable = false)
    private Instant dateCreation;

    @ManyToOne(optional = false)
    @NotNull
    private Boutique boutique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "boutique" }, allowSetters = true)
    private GroupeArticle groupeArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "groupeArticle" }, allowSetters = true)
    private FamilleArticle familleArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "familleArticle" }, allowSetters = true)
    private SousFamilleArticle sousFamilleArticle;

    @ManyToOne(optional = false)
    @NotNull
    private UniteMesure uniteMesure;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Produit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeInterne() {
        return this.codeInterne;
    }

    public Produit codeInterne(String codeInterne) {
        this.setCodeInterne(codeInterne);
        return this;
    }

    public void setCodeInterne(String codeInterne) {
        this.codeInterne = codeInterne;
    }

    public String getDesignation() {
        return this.designation;
    }

    public Produit designation(String designation) {
        this.setDesignation(designation);
        return this;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDescription() {
        return this.description;
    }

    public Produit description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypePrix getTypePrix() {
        return this.typePrix;
    }

    public Produit typePrix(TypePrix typePrix) {
        this.setTypePrix(typePrix);
        return this;
    }

    public void setTypePrix(TypePrix typePrix) {
        this.typePrix = typePrix;
    }

    public BigDecimal getPrixVente() {
        return this.prixVente;
    }

    public Produit prixVente(BigDecimal prixVente) {
        this.setPrixVente(prixVente);
        return this;
    }

    public void setPrixVente(BigDecimal prixVente) {
        this.prixVente = prixVente;
    }

    public BigDecimal getTauxRedevanceApplicable() {
        return this.tauxRedevanceApplicable;
    }

    public Produit tauxRedevanceApplicable(BigDecimal tauxRedevanceApplicable) {
        this.setTauxRedevanceApplicable(tauxRedevanceApplicable);
        return this;
    }

    public void setTauxRedevanceApplicable(BigDecimal tauxRedevanceApplicable) {
        this.tauxRedevanceApplicable = tauxRedevanceApplicable;
    }

    public StatutGeneral getStatut() {
        return this.statut;
    }

    public Produit statut(StatutGeneral statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutGeneral statut) {
        this.statut = statut;
    }

    public Instant getDateCreation() {
        return this.dateCreation;
    }

    public Produit dateCreation(Instant dateCreation) {
        this.setDateCreation(dateCreation);
        return this;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Boutique getBoutique() {
        return this.boutique;
    }

    public void setBoutique(Boutique boutique) {
        this.boutique = boutique;
    }

    public Produit boutique(Boutique boutique) {
        this.setBoutique(boutique);
        return this;
    }

    public GroupeArticle getGroupeArticle() {
        return this.groupeArticle;
    }

    public void setGroupeArticle(GroupeArticle groupeArticle) {
        this.groupeArticle = groupeArticle;
    }

    public Produit groupeArticle(GroupeArticle groupeArticle) {
        this.setGroupeArticle(groupeArticle);
        return this;
    }

    public FamilleArticle getFamilleArticle() {
        return this.familleArticle;
    }

    public void setFamilleArticle(FamilleArticle familleArticle) {
        this.familleArticle = familleArticle;
    }

    public Produit familleArticle(FamilleArticle familleArticle) {
        this.setFamilleArticle(familleArticle);
        return this;
    }

    public SousFamilleArticle getSousFamilleArticle() {
        return this.sousFamilleArticle;
    }

    public void setSousFamilleArticle(SousFamilleArticle sousFamilleArticle) {
        this.sousFamilleArticle = sousFamilleArticle;
    }

    public Produit sousFamilleArticle(SousFamilleArticle sousFamilleArticle) {
        this.setSousFamilleArticle(sousFamilleArticle);
        return this;
    }

    public UniteMesure getUniteMesure() {
        return this.uniteMesure;
    }

    public void setUniteMesure(UniteMesure uniteMesure) {
        this.uniteMesure = uniteMesure;
    }

    public Produit uniteMesure(UniteMesure uniteMesure) {
        this.setUniteMesure(uniteMesure);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Produit)) {
            return false;
        }
        return getId() != null && getId().equals(((Produit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Produit{" +
            "id=" + getId() +
            ", codeInterne='" + getCodeInterne() + "'" +
            ", designation='" + getDesignation() + "'" +
            ", description='" + getDescription() + "'" +
            ", typePrix='" + getTypePrix() + "'" +
            ", prixVente=" + getPrixVente() +
            ", tauxRedevanceApplicable=" + getTauxRedevanceApplicable() +
            ", statut='" + getStatut() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            "}";
    }
}
