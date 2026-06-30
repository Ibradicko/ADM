package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.StatutMouvementStock;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.TransfertStock} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransfertStockDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String reference;

    @NotNull
    private Instant dateTransfert;

    @NotNull
    private StatutMouvementStock statut;

    @Size(max = 255)
    private String motif;

    @NotNull
    private BoutiqueDTO boutiqueOrigine;

    @NotNull
    private BoutiqueDTO boutiqueDestination;

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

    public Instant getDateTransfert() {
        return dateTransfert;
    }

    public void setDateTransfert(Instant dateTransfert) {
        this.dateTransfert = dateTransfert;
    }

    public StatutMouvementStock getStatut() {
        return statut;
    }

    public void setStatut(StatutMouvementStock statut) {
        this.statut = statut;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public BoutiqueDTO getBoutiqueOrigine() {
        return boutiqueOrigine;
    }

    public void setBoutiqueOrigine(BoutiqueDTO boutiqueOrigine) {
        this.boutiqueOrigine = boutiqueOrigine;
    }

    public BoutiqueDTO getBoutiqueDestination() {
        return boutiqueDestination;
    }

    public void setBoutiqueDestination(BoutiqueDTO boutiqueDestination) {
        this.boutiqueDestination = boutiqueDestination;
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
        if (!(o instanceof TransfertStockDTO)) {
            return false;
        }

        TransfertStockDTO transfertStockDTO = (TransfertStockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transfertStockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransfertStockDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", dateTransfert='" + getDateTransfert() + "'" +
            ", statut='" + getStatut() + "'" +
            ", motif='" + getMotif() + "'" +
            ", boutiqueOrigine=" + getBoutiqueOrigine() +
            ", boutiqueDestination=" + getBoutiqueDestination() +
            ", utilisateur=" + getUtilisateur() +
            "}";
    }
}
