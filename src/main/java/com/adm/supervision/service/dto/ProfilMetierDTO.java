package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.StatutGeneral;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.adm.supervision.domain.ProfilMetier} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProfilMetierDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String code;

    @NotNull
    @Size(max = 150)
    private String libelle;

    @Lob
    private String description;

    @NotNull
    private StatutGeneral statut;

    private Set<PermissionMetierDTO> permissionses = new HashSet<>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatutGeneral getStatut() {
        return statut;
    }

    public void setStatut(StatutGeneral statut) {
        this.statut = statut;
    }

    public Set<PermissionMetierDTO> getPermissionses() {
        return permissionses;
    }

    public void setPermissionses(Set<PermissionMetierDTO> permissionses) {
        this.permissionses = permissionses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfilMetierDTO)) {
            return false;
        }

        ProfilMetierDTO profilMetierDTO = (ProfilMetierDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, profilMetierDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfilMetierDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", libelle='" + getLibelle() + "'" +
            ", description='" + getDescription() + "'" +
            ", statut='" + getStatut() + "'" +
            ", permissionses=" + getPermissionses() +
            "}";
    }
}
