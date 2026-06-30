package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.FormatExport;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.RapportExport} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RapportExportDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String reference;

    @NotNull
    @Size(max = 100)
    private String typeRapport;

    @NotNull
    private FormatExport format;

    private LocalDate periodeDebut;

    private LocalDate periodeFin;

    @Size(max = 255)
    private String cheminFichier;

    @NotNull
    private Instant dateGeneration;

    private BoutiqueDTO boutique;

    private LocataireDTO locataire;

    @NotNull
    private UserDTO utilisateur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTypeRapport() {
        return typeRapport;
    }

    public void setTypeRapport(String typeRapport) {
        this.typeRapport = typeRapport;
    }

    public FormatExport getFormat() {
        return format;
    }

    public void setFormat(FormatExport format) {
        this.format = format;
    }

    public LocalDate getPeriodeDebut() {
        return periodeDebut;
    }

    public void setPeriodeDebut(LocalDate periodeDebut) {
        this.periodeDebut = periodeDebut;
    }

    public LocalDate getPeriodeFin() {
        return periodeFin;
    }

    public void setPeriodeFin(LocalDate periodeFin) {
        this.periodeFin = periodeFin;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public Instant getDateGeneration() {
        return dateGeneration;
    }

    public void setDateGeneration(Instant dateGeneration) {
        this.dateGeneration = dateGeneration;
    }

    public BoutiqueDTO getBoutique() {
        return boutique;
    }

    public void setBoutique(BoutiqueDTO boutique) {
        this.boutique = boutique;
    }

    public LocataireDTO getLocataire() {
        return locataire;
    }

    public void setLocataire(LocataireDTO locataire) {
        this.locataire = locataire;
    }

    public UserDTO getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(UserDTO utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RapportExportDTO)) {
            return false;
        }

        RapportExportDTO rapportExportDTO = (RapportExportDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, rapportExportDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RapportExportDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", typeRapport='" + getTypeRapport() + "'" +
            ", format='" + getFormat() + "'" +
            ", periodeDebut='" + getPeriodeDebut() + "'" +
            ", periodeFin='" + getPeriodeFin() + "'" +
            ", cheminFichier='" + getCheminFichier() + "'" +
            ", dateGeneration='" + getDateGeneration() + "'" +
            ", boutique=" + getBoutique() +
            ", locataire=" + getLocataire() +
            ", utilisateur=" + getUtilisateur() +
            "}";
    }
}
