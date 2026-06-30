package com.adm.supervision.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.adm.supervision.domain.PermissionMetier} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PermissionMetierDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String code;

    @NotNull
    @Size(max = 150)
    private String libelle;

    @NotNull
    @Size(max = 80)
    private String module;

    @Lob
    private String description;

    private Set<ProfilMetierDTO> profilses = new HashSet<>();

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

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ProfilMetierDTO> getProfilses() {
        return profilses;
    }

    public void setProfilses(Set<ProfilMetierDTO> profilses) {
        this.profilses = profilses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermissionMetierDTO)) {
            return false;
        }

        PermissionMetierDTO permissionMetierDTO = (PermissionMetierDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, permissionMetierDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PermissionMetierDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", libelle='" + getLibelle() + "'" +
            ", module='" + getModule() + "'" +
            ", description='" + getDescription() + "'" +
            ", profilses=" + getProfilses() +
            "}";
    }
}
