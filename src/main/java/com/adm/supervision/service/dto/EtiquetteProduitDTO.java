package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.EtiquetteProduit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EtiquetteProduitDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer quantite;

    @NotNull
    private Boolean imprimee;

    private Instant dateImpression;

    @NotNull
    private ProduitDTO produit;

    @NotNull
    private LotEtiquettesDTO lot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Boolean getImprimee() {
        return imprimee;
    }

    public void setImprimee(Boolean imprimee) {
        this.imprimee = imprimee;
    }

    public Instant getDateImpression() {
        return dateImpression;
    }

    public void setDateImpression(Instant dateImpression) {
        this.dateImpression = dateImpression;
    }

    public ProduitDTO getProduit() {
        return produit;
    }

    public void setProduit(ProduitDTO produit) {
        this.produit = produit;
    }

    public LotEtiquettesDTO getLot() {
        return lot;
    }

    public void setLot(LotEtiquettesDTO lot) {
        this.lot = lot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EtiquetteProduitDTO)) {
            return false;
        }

        EtiquetteProduitDTO etiquetteProduitDTO = (EtiquetteProduitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, etiquetteProduitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EtiquetteProduitDTO{" +
            "id=" + getId() +
            ", quantite=" + getQuantite() +
            ", imprimee='" + getImprimee() + "'" +
            ", dateImpression='" + getDateImpression() + "'" +
            ", produit=" + getProduit() +
            ", lot=" + getLot() +
            "}";
    }
}
