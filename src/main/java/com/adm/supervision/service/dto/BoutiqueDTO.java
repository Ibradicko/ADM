package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.TypeBoutique;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.Boutique} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BoutiqueDTO implements Serializable {

    private Long id;

    @Size(max = 30)
    private String code;

    @NotNull
    @Size(max = 150)
    private String nom;

    private TypeBoutique type;

    @Size(max = 255)
    private String emplacement;

    @Pattern(regexp = "^$|\\d{8}$", message = "Le telephone doit contenir exactement 8 chiffres")
    private String telephone;

    @NotNull
    private StatutGeneral statut;

    @NotNull
    private Instant dateCreation;

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

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public TypeBoutique getType() {
        return type;
    }

    public void setType(TypeBoutique type) {
        this.type = type;
    }

    public String getEmplacement() {
        return emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public StatutGeneral getStatut() {
        return statut;
    }

    public void setStatut(StatutGeneral statut) {
        this.statut = statut;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoutiqueDTO)) {
            return false;
        }

        BoutiqueDTO boutiqueDTO = (BoutiqueDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, boutiqueDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BoutiqueDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", nom='" + getNom() + "'" +
            ", type='" + getType() + "'" +
            ", emplacement='" + getEmplacement() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", statut='" + getStatut() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            "}";
    }
}
