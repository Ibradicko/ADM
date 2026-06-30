package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.AffectationUtilisateur} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AffectationUtilisateurDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @NotNull
    private Boolean actif;

    @NotNull
    private UserDTO user;

    @NotNull
    private BoutiqueDTO boutique;

    @NotNull
    private ProfilMetierDTO profil;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public BoutiqueDTO getBoutique() {
        return boutique;
    }

    public void setBoutique(BoutiqueDTO boutique) {
        this.boutique = boutique;
    }

    public ProfilMetierDTO getProfil() {
        return profil;
    }

    public void setProfil(ProfilMetierDTO profil) {
        this.profil = profil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AffectationUtilisateurDTO)) {
            return false;
        }

        AffectationUtilisateurDTO affectationUtilisateurDTO = (AffectationUtilisateurDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, affectationUtilisateurDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AffectationUtilisateurDTO{" +
            "id=" + getId() +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            ", actif='" + getActif() + "'" +
            ", user=" + getUser() +
            ", boutique=" + getBoutique() +
            ", profil=" + getProfil() +
            "}";
    }
}
