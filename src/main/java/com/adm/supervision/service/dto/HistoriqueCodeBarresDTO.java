package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.HistoriqueCodeBarres} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HistoriqueCodeBarresDTO implements Serializable {

    private Long id;

    @Size(max = 80)
    private String ancienCode;

    @NotNull
    @Size(max = 80)
    private String nouveauCode;

    @Size(max = 255)
    private String motif;

    @NotNull
    private Instant dateChangement;

    @NotNull
    private ProduitDTO produit;

    private UserDTO utilisateur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAncienCode() {
        return ancienCode;
    }

    public void setAncienCode(String ancienCode) {
        this.ancienCode = ancienCode;
    }

    public String getNouveauCode() {
        return nouveauCode;
    }

    public void setNouveauCode(String nouveauCode) {
        this.nouveauCode = nouveauCode;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Instant getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(Instant dateChangement) {
        this.dateChangement = dateChangement;
    }

    public ProduitDTO getProduit() {
        return produit;
    }

    public void setProduit(ProduitDTO produit) {
        this.produit = produit;
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
        if (!(o instanceof HistoriqueCodeBarresDTO)) {
            return false;
        }

        HistoriqueCodeBarresDTO historiqueCodeBarresDTO = (HistoriqueCodeBarresDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, historiqueCodeBarresDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistoriqueCodeBarresDTO{" +
            "id=" + getId() +
            ", ancienCode='" + getAncienCode() + "'" +
            ", nouveauCode='" + getNouveauCode() + "'" +
            ", motif='" + getMotif() + "'" +
            ", dateChangement='" + getDateChangement() + "'" +
            ", produit=" + getProduit() +
            ", utilisateur=" + getUtilisateur() +
            "}";
    }
}
