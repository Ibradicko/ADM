package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.TypeCodeBarres;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.ParametreCodeBarres} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParametreCodeBarresDTO implements Serializable {

    private Long id;

    @NotNull
    private TypeCodeBarres formatParDefaut;

    @Size(max = 20)
    private String prefixe;

    @Min(value = 8)
    @Max(value = 30)
    private Integer longueur;

    @NotNull
    private Boolean actif;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeCodeBarres getFormatParDefaut() {
        return formatParDefaut;
    }

    public void setFormatParDefaut(TypeCodeBarres formatParDefaut) {
        this.formatParDefaut = formatParDefaut;
    }

    public String getPrefixe() {
        return prefixe;
    }

    public void setPrefixe(String prefixe) {
        this.prefixe = prefixe;
    }

    public Integer getLongueur() {
        return longueur;
    }

    public void setLongueur(Integer longueur) {
        this.longueur = longueur;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParametreCodeBarresDTO)) {
            return false;
        }

        ParametreCodeBarresDTO parametreCodeBarresDTO = (ParametreCodeBarresDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, parametreCodeBarresDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParametreCodeBarresDTO{" +
            "id=" + getId() +
            ", formatParDefaut='" + getFormatParDefaut() + "'" +
            ", prefixe='" + getPrefixe() + "'" +
            ", longueur=" + getLongueur() +
            ", actif='" + getActif() + "'" +
            "}";
    }
}
