package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.TypeRegleRedevance;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.RegleRedevance} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RegleRedevanceDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String code;

    @NotNull
    private TypeRegleRedevance typeRegle;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal taux;

    @NotNull
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Min(value = 1)
    private Integer priorite;

    @NotNull
    private Boolean actif;

    private BoutiqueDTO boutique;

    private LocataireDTO locataire;

    private GroupeArticleDTO groupeArticle;

    private ProduitDTO produit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TypeRegleRedevance getTypeRegle() {
        return typeRegle;
    }

    public void setTypeRegle(TypeRegleRedevance typeRegle) {
        this.typeRegle = typeRegle;
    }

    public BigDecimal getTaux() {
        return taux;
    }

    public void setTaux(BigDecimal taux) {
        this.taux = taux;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Integer getPriorite() {
        return priorite;
    }

    public void setPriorite(Integer priorite) {
        this.priorite = priorite;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public BoutiqueDTO getBoutique() {
        return boutique;
    }

    public void setBoutique(BoutiqueDTO boutique) {
        this.boutique = boutique;
    }

    public LocataireDTO getLocataire() {
        return locataire;
    }

    public void setLocataire(LocataireDTO locataire) {
        this.locataire = locataire;
    }

    public GroupeArticleDTO getGroupeArticle() {
        return groupeArticle;
    }

    public void setGroupeArticle(GroupeArticleDTO groupeArticle) {
        this.groupeArticle = groupeArticle;
    }

    public ProduitDTO getProduit() {
        return produit;
    }

    public void setProduit(ProduitDTO produit) {
        this.produit = produit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegleRedevanceDTO)) {
            return false;
        }

        RegleRedevanceDTO regleRedevanceDTO = (RegleRedevanceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, regleRedevanceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RegleRedevanceDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", typeRegle='" + getTypeRegle() + "'" +
            ", taux=" + getTaux() +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            ", priorite=" + getPriorite() +
            ", actif='" + getActif() + "'" +
            ", boutique=" + getBoutique() +
            ", locataire=" + getLocataire() +
            ", groupeArticle=" + getGroupeArticle() +
            ", produit=" + getProduit() +
            "}";
    }
}
