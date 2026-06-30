package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.DepotStock} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DepotStockDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 30)
    private String code;

    @NotNull
    @Size(max = 150)
    private String libelle;

    @Size(max = 255)
    private String emplacement;

    @NotNull
    private Boolean actif;

    @NotNull
    private BoutiqueDTO boutique;

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

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DepotStockDTO)) {
            return false;
        }

        DepotStockDTO depotStockDTO = (DepotStockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, depotStockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DepotStockDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", libelle='" + getLibelle() + "'" +
            ", emplacement='" + getEmplacement() + "'" +
            ", actif='" + getActif() + "'" +
            ", boutique=" + getBoutique() +
            "}";
    }
}
