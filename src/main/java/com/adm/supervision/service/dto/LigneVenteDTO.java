package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.LigneVente} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneVenteDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal quantite;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal prixUnitaire;

    @DecimalMin(value = "0")
    private BigDecimal remise;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal montantLigne;

    @Size(max = 80)
    private String codeBarresScanne;

    @NotNull
    private VenteDTO vente;

    @NotNull
    private ProduitDTO produit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantite() {
        return quantite;
    }

    public void setQuantite(BigDecimal quantite) {
        this.quantite = quantite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getRemise() {
        return remise;
    }

    public void setRemise(BigDecimal remise) {
        this.remise = remise;
    }

    public BigDecimal getMontantLigne() {
        return montantLigne;
    }

    public void setMontantLigne(BigDecimal montantLigne) {
        this.montantLigne = montantLigne;
    }

    public String getCodeBarresScanne() {
        return codeBarresScanne;
    }

    public void setCodeBarresScanne(String codeBarresScanne) {
        this.codeBarresScanne = codeBarresScanne;
    }

    public VenteDTO getVente() {
        return vente;
    }

    public void setVente(VenteDTO vente) {
        this.vente = vente;
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
        if (!(o instanceof LigneVenteDTO)) {
            return false;
        }

        LigneVenteDTO ligneVenteDTO = (LigneVenteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ligneVenteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneVenteDTO{" +
            "id=" + getId() +
            ", quantite=" + getQuantite() +
            ", prixUnitaire=" + getPrixUnitaire() +
            ", remise=" + getRemise() +
            ", montantLigne=" + getMontantLigne() +
            ", codeBarresScanne='" + getCodeBarresScanne() + "'" +
            ", vente=" + getVente() +
            ", produit=" + getProduit() +
            "}";
    }
}
