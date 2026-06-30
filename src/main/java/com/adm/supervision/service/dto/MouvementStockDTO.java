package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.StatutMouvementStock;
import com.adm.supervision.domain.enumeration.TypeMouvementStock;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.MouvementStock} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MouvementStockDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String reference;

    @NotNull
    private TypeMouvementStock typeMouvement;

    @NotNull
    private StatutMouvementStock statut;

    @NotNull
    private Instant dateMouvement;

    @Size(max = 255)
    private String motif;

    @NotNull
    private BoutiqueDTO boutique;

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

    public TypeMouvementStock getTypeMouvement() {
        return typeMouvement;
    }

    public void setTypeMouvement(TypeMouvementStock typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public StatutMouvementStock getStatut() {
        return statut;
    }

    public void setStatut(StatutMouvementStock statut) {
        this.statut = statut;
    }

    public Instant getDateMouvement() {
        return dateMouvement;
    }

    public void setDateMouvement(Instant dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public BoutiqueDTO getBoutique() {
        return boutique;
    }

    public void setBoutique(BoutiqueDTO boutique) {
        this.boutique = boutique;
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
        if (!(o instanceof MouvementStockDTO)) {
            return false;
        }

        MouvementStockDTO mouvementStockDTO = (MouvementStockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mouvementStockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MouvementStockDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", typeMouvement='" + getTypeMouvement() + "'" +
            ", statut='" + getStatut() + "'" +
            ", dateMouvement='" + getDateMouvement() + "'" +
            ", motif='" + getMotif() + "'" +
            ", boutique=" + getBoutique() +
            ", utilisateur=" + getUtilisateur() +
            "}";
    }
}
