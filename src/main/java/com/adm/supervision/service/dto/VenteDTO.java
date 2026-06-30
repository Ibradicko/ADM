package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.StatutVente;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.Vente} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VenteDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String numeroTicket;

    @NotNull
    private Instant dateHeure;

    @NotNull
    private StatutVente statut;

    @Size(max = 80)
    private String referencePassager;

    @Size(max = 80)
    private String referenceCarteEmbarquement;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal montantBrut;

    @DecimalMin(value = "0")
    private BigDecimal montantRemise;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal montantNet;

    @Size(max = 255)
    private String commentaire;

    @NotNull
    private BoutiqueDTO boutique;

    @NotNull
    private LocataireDTO locataire;

    @NotNull
    private UserDTO vendeur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroTicket() {
        return numeroTicket;
    }

    public void setNumeroTicket(String numeroTicket) {
        this.numeroTicket = numeroTicket;
    }

    public Instant getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(Instant dateHeure) {
        this.dateHeure = dateHeure;
    }

    public StatutVente getStatut() {
        return statut;
    }

    public void setStatut(StatutVente statut) {
        this.statut = statut;
    }

    public String getReferencePassager() {
        return referencePassager;
    }

    public void setReferencePassager(String referencePassager) {
        this.referencePassager = referencePassager;
    }

    public String getReferenceCarteEmbarquement() {
        return referenceCarteEmbarquement;
    }

    public void setReferenceCarteEmbarquement(String referenceCarteEmbarquement) {
        this.referenceCarteEmbarquement = referenceCarteEmbarquement;
    }

    public BigDecimal getMontantBrut() {
        return montantBrut;
    }

    public void setMontantBrut(BigDecimal montantBrut) {
        this.montantBrut = montantBrut;
    }

    public BigDecimal getMontantRemise() {
        return montantRemise;
    }

    public void setMontantRemise(BigDecimal montantRemise) {
        this.montantRemise = montantRemise;
    }

    public BigDecimal getMontantNet() {
        return montantNet;
    }

    public void setMontantNet(BigDecimal montantNet) {
        this.montantNet = montantNet;
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

    public LocataireDTO getLocataire() {
        return locataire;
    }

    public void setLocataire(LocataireDTO locataire) {
        this.locataire = locataire;
    }

    public UserDTO getVendeur() {
        return vendeur;
    }

    public void setVendeur(UserDTO vendeur) {
        this.vendeur = vendeur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VenteDTO)) {
            return false;
        }

        VenteDTO venteDTO = (VenteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, venteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VenteDTO{" +
            "id=" + getId() +
            ", numeroTicket='" + getNumeroTicket() + "'" +
            ", dateHeure='" + getDateHeure() + "'" +
            ", statut='" + getStatut() + "'" +
            ", referencePassager='" + getReferencePassager() + "'" +
            ", referenceCarteEmbarquement='" + getReferenceCarteEmbarquement() + "'" +
            ", montantBrut=" + getMontantBrut() +
            ", montantRemise=" + getMontantRemise() +
            ", montantNet=" + getMontantNet() +
            ", commentaire='" + getCommentaire() + "'" +
            ", boutique=" + getBoutique() +
            ", locataire=" + getLocataire() +
            ", vendeur=" + getVendeur() +
            "}";
    }
}
