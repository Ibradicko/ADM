package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.ReceptionProduit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReceptionProduitDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String reference;

    @NotNull
    private Instant dateReception;

    @Size(max = 150)
    private String fournisseur;

    @Size(max = 255)
    private String commentaire;

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

    public Instant getDateReception() {
        return dateReception;
    }

    public void setDateReception(Instant dateReception) {
        this.dateReception = dateReception;
    }

    public String getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
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
        if (!(o instanceof ReceptionProduitDTO)) {
            return false;
        }

        ReceptionProduitDTO receptionProduitDTO = (ReceptionProduitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, receptionProduitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReceptionProduitDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", dateReception='" + getDateReception() + "'" +
            ", fournisseur='" + getFournisseur() + "'" +
            ", commentaire='" + getCommentaire() + "'" +
            ", boutique=" + getBoutique() +
            ", utilisateur=" + getUtilisateur() +
            "}";
    }
}
