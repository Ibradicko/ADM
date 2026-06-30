package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.StatutPaiement;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.PaiementVente} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaiementVenteDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal montant;

    @NotNull
    private StatutPaiement statut;

    @Size(max = 100)
    private String referencePaiement;

    @NotNull
    private Instant datePaiement;

    @NotNull
    private VenteDTO vente;

    @NotNull
    private ModePaiementRefDTO modePaiement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public StatutPaiement getStatut() {
        return statut;
    }

    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }

    public String getReferencePaiement() {
        return referencePaiement;
    }

    public void setReferencePaiement(String referencePaiement) {
        this.referencePaiement = referencePaiement;
    }

    public Instant getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(Instant datePaiement) {
        this.datePaiement = datePaiement;
    }

    public VenteDTO getVente() {
        return vente;
    }

    public void setVente(VenteDTO vente) {
        this.vente = vente;
    }

    public ModePaiementRefDTO getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(ModePaiementRefDTO modePaiement) {
        this.modePaiement = modePaiement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaiementVenteDTO)) {
            return false;
        }

        PaiementVenteDTO paiementVenteDTO = (PaiementVenteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paiementVenteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaiementVenteDTO{" +
            "id=" + getId() +
            ", montant=" + getMontant() +
            ", statut='" + getStatut() + "'" +
            ", referencePaiement='" + getReferencePaiement() + "'" +
            ", datePaiement='" + getDatePaiement() + "'" +
            ", vente=" + getVente() +
            ", modePaiement=" + getModePaiement() +
            "}";
    }
}
