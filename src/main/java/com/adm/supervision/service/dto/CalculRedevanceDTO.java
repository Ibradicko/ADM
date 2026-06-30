package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.StatutRedevance;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.CalculRedevance} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CalculRedevanceDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String reference;

    @NotNull
    private LocalDate periodeDebut;

    @NotNull
    private LocalDate periodeFin;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal chiffreAffaires;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal montantRedevance;

    @NotNull
    private StatutRedevance statut;

    @NotNull
    private Instant dateCalcul;

    @NotNull
    private BoutiqueDTO boutique;

    @NotNull
    private LocataireDTO locataire;

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

    public BigDecimal getChiffreAffaires() {
        return chiffreAffaires;
    }

    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }

    public BigDecimal getMontantRedevance() {
        return montantRedevance;
    }

    public void setMontantRedevance(BigDecimal montantRedevance) {
        this.montantRedevance = montantRedevance;
    }

    public StatutRedevance getStatut() {
        return statut;
    }

    public void setStatut(StatutRedevance statut) {
        this.statut = statut;
    }

    public Instant getDateCalcul() {
        return dateCalcul;
    }

    public void setDateCalcul(Instant dateCalcul) {
        this.dateCalcul = dateCalcul;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CalculRedevanceDTO)) {
            return false;
        }

        CalculRedevanceDTO calculRedevanceDTO = (CalculRedevanceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, calculRedevanceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CalculRedevanceDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", periodeDebut='" + getPeriodeDebut() + "'" +
            ", periodeFin='" + getPeriodeFin() + "'" +
            ", chiffreAffaires=" + getChiffreAffaires() +
            ", montantRedevance=" + getMontantRedevance() +
            ", statut='" + getStatut() + "'" +
            ", dateCalcul='" + getDateCalcul() + "'" +
            ", boutique=" + getBoutique() +
            ", locataire=" + getLocataire() +
            "}";
    }
}
