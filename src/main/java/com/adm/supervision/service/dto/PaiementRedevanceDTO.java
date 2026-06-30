package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.PaiementRedevance} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaiementRedevanceDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String reference;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal montant;

    @NotNull
    private LocalDate datePaiement;

    @Size(max = 80)
    private String modePaiement;

    @Size(max = 255)
    private String commentaire;

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

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
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
        if (!(o instanceof PaiementRedevanceDTO)) {
            return false;
        }

        PaiementRedevanceDTO paiementRedevanceDTO = (PaiementRedevanceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paiementRedevanceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaiementRedevanceDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", montant=" + getMontant() +
            ", datePaiement='" + getDatePaiement() + "'" +
            ", modePaiement='" + getModePaiement() + "'" +
            ", commentaire='" + getCommentaire() + "'" +
            ", calcul=" + getCalcul() +
            "}";
    }
}
