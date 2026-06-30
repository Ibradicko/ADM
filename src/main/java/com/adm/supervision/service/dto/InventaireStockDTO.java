package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.StatutInventaire;
import com.adm.supervision.domain.enumeration.TypeInventaire;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.InventaireStock} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventaireStockDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String reference;

    @NotNull
    private TypeInventaire typeInventaire;

    @NotNull
    private StatutInventaire statut;

    @NotNull
    private Instant dateDebut;

    private Instant dateFin;

    @NotNull
    private BoutiqueDTO boutique;

    private DepotStockDTO depot;

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

    public TypeInventaire getTypeInventaire() {
        return typeInventaire;
    }

    public void setTypeInventaire(TypeInventaire typeInventaire) {
        this.typeInventaire = typeInventaire;
    }

    public StatutInventaire getStatut() {
        return statut;
    }

    public void setStatut(StatutInventaire statut) {
        this.statut = statut;
    }

    public Instant getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Instant dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Instant getDateFin() {
        return dateFin;
    }

    public void setDateFin(Instant dateFin) {
        this.dateFin = dateFin;
    }

    public BoutiqueDTO getBoutique() {
        return boutique;
    }

    public void setBoutique(BoutiqueDTO boutique) {
        this.boutique = boutique;
    }

    public DepotStockDTO getDepot() {
        return depot;
    }

    public void setDepot(DepotStockDTO depot) {
        this.depot = depot;
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
        if (!(o instanceof InventaireStockDTO)) {
            return false;
        }

        InventaireStockDTO inventaireStockDTO = (InventaireStockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inventaireStockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventaireStockDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", typeInventaire='" + getTypeInventaire() + "'" +
            ", statut='" + getStatut() + "'" +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            ", boutique=" + getBoutique() +
            ", depot=" + getDepot() +
            ", utilisateur=" + getUtilisateur() +
            "}";
    }
}
