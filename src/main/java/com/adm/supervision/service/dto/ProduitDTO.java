package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.TypePrix;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.Produit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProduitDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String codeInterne;

    @NotNull
    @Size(max = 200)
    private String designation;

    @Lob
    private String description;

    @NotNull
    private TypePrix typePrix;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal prixVente;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal tauxRedevanceApplicable;

    @NotNull
    private StatutGeneral statut;

    @NotNull
    private Instant dateCreation;

    @NotNull
    private BoutiqueDTO boutique;

    private GroupeArticleDTO groupeArticle;

    private FamilleArticleDTO familleArticle;

    private SousFamilleArticleDTO sousFamilleArticle;

    @NotNull
    private UniteMesureDTO uniteMesure;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeInterne() {
        return codeInterne;
    }

    public void setCodeInterne(String codeInterne) {
        this.codeInterne = codeInterne;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypePrix getTypePrix() {
        return typePrix;
    }

    public void setTypePrix(TypePrix typePrix) {
        this.typePrix = typePrix;
    }

    public BigDecimal getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(BigDecimal prixVente) {
        this.prixVente = prixVente;
    }

    public BigDecimal getTauxRedevanceApplicable() {
        return tauxRedevanceApplicable;
    }

    public void setTauxRedevanceApplicable(BigDecimal tauxRedevanceApplicable) {
        this.tauxRedevanceApplicable = tauxRedevanceApplicable;
    }

    public StatutGeneral getStatut() {
        return statut;
    }

    public void setStatut(StatutGeneral statut) {
        this.statut = statut;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public BoutiqueDTO getBoutique() {
        return boutique;
    }

    public void setBoutique(BoutiqueDTO boutique) {
        this.boutique = boutique;
    }

    public GroupeArticleDTO getGroupeArticle() {
        return groupeArticle;
    }

    public void setGroupeArticle(GroupeArticleDTO groupeArticle) {
        this.groupeArticle = groupeArticle;
    }

    public FamilleArticleDTO getFamilleArticle() {
        return familleArticle;
    }

    public void setFamilleArticle(FamilleArticleDTO familleArticle) {
        this.familleArticle = familleArticle;
    }

    public SousFamilleArticleDTO getSousFamilleArticle() {
        return sousFamilleArticle;
    }

    public void setSousFamilleArticle(SousFamilleArticleDTO sousFamilleArticle) {
        this.sousFamilleArticle = sousFamilleArticle;
    }

    public UniteMesureDTO getUniteMesure() {
        return uniteMesure;
    }

    public void setUniteMesure(UniteMesureDTO uniteMesure) {
        this.uniteMesure = uniteMesure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProduitDTO)) {
            return false;
        }

        ProduitDTO produitDTO = (ProduitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, produitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProduitDTO{" +
            "id=" + getId() +
            ", codeInterne='" + getCodeInterne() + "'" +
            ", designation='" + getDesignation() + "'" +
            ", description='" + getDescription() + "'" +
            ", typePrix='" + getTypePrix() + "'" +
            ", prixVente=" + getPrixVente() +
            ", tauxRedevanceApplicable=" + getTauxRedevanceApplicable() +
            ", statut='" + getStatut() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            ", boutique=" + getBoutique() +
            ", groupeArticle=" + getGroupeArticle() +
            ", familleArticle=" + getFamilleArticle() +
            ", sousFamilleArticle=" + getSousFamilleArticle() +
            ", uniteMesure=" + getUniteMesure() +
            "}";
    }
}
