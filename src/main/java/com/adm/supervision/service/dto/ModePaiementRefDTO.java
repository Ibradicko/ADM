package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.ModePaiementRef} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ModePaiementRefDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 40)
    private String code;

    @NotNull
    @Size(max = 100)
    private String libelle;

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

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
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
        if (!(o instanceof ModePaiementRefDTO)) {
            return false;
        }

        ModePaiementRefDTO modePaiementRefDTO = (ModePaiementRefDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, modePaiementRefDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ModePaiementRefDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", libelle='" + getLibelle() + "'" +
            ", actif='" + getActif() + "'" +
            "}";
    }
}
