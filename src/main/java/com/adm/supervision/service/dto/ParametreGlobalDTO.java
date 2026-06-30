package com.adm.supervision.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.ParametreGlobal} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParametreGlobalDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String code;

    @NotNull
    @Size(max = 255)
    private String valeur;

    @Lob
    private String description;

    @NotNull
    private Boolean actif;

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

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(o instanceof ParametreGlobalDTO)) {
            return false;
        }

        ParametreGlobalDTO parametreGlobalDTO = (ParametreGlobalDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, parametreGlobalDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParametreGlobalDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", valeur='" + getValeur() + "'" +
            ", description='" + getDescription() + "'" +
            ", actif='" + getActif() + "'" +
            "}";
    }
}
