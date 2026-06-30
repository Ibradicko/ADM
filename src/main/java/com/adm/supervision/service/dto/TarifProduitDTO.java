package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.TypePrix;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.TarifProduit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TarifProduitDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal montant;

    @NotNull
    private TypePrix typePrix;

    @NotNull
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @NotNull
    private Boolean actif;

    @NotNull
    private ProduitDTO produit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public TypePrix getTypePrix() {
        return typePrix;
    }

    public void setTypePrix(TypePrix typePrix) {
        this.typePrix = typePrix;
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

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
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
        if (!(o instanceof TarifProduitDTO)) {
            return false;
        }

        TarifProduitDTO tarifProduitDTO = (TarifProduitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tarifProduitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TarifProduitDTO{" +
            "id=" + getId() +
            ", montant=" + getMontant() +
            ", typePrix='" + getTypePrix() + "'" +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            ", actif='" + getActif() + "'" +
            ", produit=" + getProduit() +
            "}";
    }
}
