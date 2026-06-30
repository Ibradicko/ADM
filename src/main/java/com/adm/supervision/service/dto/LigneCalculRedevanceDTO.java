package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.LigneCalculRedevance} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneCalculRedevanceDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal baseCalcul;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal tauxApplique;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal montantRedevance;

    @NotNull
    private CalculRedevanceDTO calcul;

    private VenteDTO vente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBaseCalcul() {
        return baseCalcul;
    }

    public void setBaseCalcul(BigDecimal baseCalcul) {
        this.baseCalcul = baseCalcul;
    }

    public BigDecimal getTauxApplique() {
        return tauxApplique;
    }

    public void setTauxApplique(BigDecimal tauxApplique) {
        this.tauxApplique = tauxApplique;
    }

    public BigDecimal getMontantRedevance() {
        return montantRedevance;
    }

    public void setMontantRedevance(BigDecimal montantRedevance) {
        this.montantRedevance = montantRedevance;
    }

    public CalculRedevanceDTO getCalcul() {
        return calcul;
    }

    public void setCalcul(CalculRedevanceDTO calcul) {
        this.calcul = calcul;
    }

    public VenteDTO getVente() {
        return vente;
    }

    public void setVente(VenteDTO vente) {
        this.vente = vente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LigneCalculRedevanceDTO)) {
            return false;
        }

        LigneCalculRedevanceDTO ligneCalculRedevanceDTO = (LigneCalculRedevanceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ligneCalculRedevanceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneCalculRedevanceDTO{" +
            "id=" + getId() +
            ", baseCalcul=" + getBaseCalcul() +
            ", tauxApplique=" + getTauxApplique() +
            ", montantRedevance=" + getMontantRedevance() +
            ", calcul=" + getCalcul() +
            ", vente=" + getVente() +
            "}";
    }
}
