package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.TypeLocataire;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.Locataire} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LocataireDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 30)
    private String code;

    @NotNull
    @Size(max = 150)
    private String nom;

    @NotNull
    private TypeLocataire typeLocataire;

    @Size(max = 80)
    private String numeroIdentification;

    @Size(max = 30)
    private String telephone;

    @NotBlank
    @Email
    @Size(max = 120)
    private String email;

    @Size(max = 255)
    private String adresse;

    @NotNull
    private StatutGeneral statut;

    @NotNull
    private Instant dateCreation;

    private String loginGenere;

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

    public TypeLocataire getTypeLocataire() {
        return typeLocataire;
    }

    public void setTypeLocataire(TypeLocataire typeLocataire) {
        this.typeLocataire = typeLocataire;
    }

    public String getNumeroIdentification() {
        return numeroIdentification;
    }

    public void setNumeroIdentification(String numeroIdentification) {
        this.numeroIdentification = numeroIdentification;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
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

    public String getLoginGenere() {
        return loginGenere;
    }

    public void setLoginGenere(String loginGenere) {
        this.loginGenere = loginGenere;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LocataireDTO)) {
            return false;
        }

        LocataireDTO locataireDTO = (LocataireDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, locataireDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocataireDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", nom='" + getNom() + "'" +
            ", typeLocataire='" + getTypeLocataire() + "'" +
            ", numeroIdentification='" + getNumeroIdentification() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", email='" + getEmail() + "'" +
            ", adresse='" + getAdresse() + "'" +
            ", statut='" + getStatut() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            ", loginGenere='" + getLoginGenere() + "'" +
            "}";
    }
}
