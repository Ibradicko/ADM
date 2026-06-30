package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.RegularisationRedevance} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RegularisationRedevanceDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String reference;

    @NotNull
    private BigDecimal montant;

    @NotNull
    @Size(max = 255)
    private String motif;

    @NotNull
    private Instant dateRegularisation;

    @NotNull
    private CalculRedevanceDTO calcul;

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

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Instant getDateRegularisation() {
        return dateRegularisation;
    }

    public void setDateRegularisation(Instant dateRegularisation) {
        this.dateRegularisation = dateRegularisation;
    }

    public CalculRedevanceDTO getCalcul() {
        return calcul;
    }

    public void setCalcul(CalculRedevanceDTO calcul) {
        this.calcul = calcul;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegularisationRedevanceDTO)) {
            return false;
        }

        RegularisationRedevanceDTO regularisationRedevanceDTO = (RegularisationRedevanceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, regularisationRedevanceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RegularisationRedevanceDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", montant=" + getMontant() +
            ", motif='" + getMotif() + "'" +
            ", dateRegularisation='" + getDateRegularisation() + "'" +
            ", calcul=" + getCalcul() +
            "}";
    }
}
