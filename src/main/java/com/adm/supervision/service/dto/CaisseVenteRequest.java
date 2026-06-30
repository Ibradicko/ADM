package com.adm.supervision.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CaisseVenteRequest implements Serializable {

    @NotNull
    private Long boutiqueId;

    @NotNull
    private Long locataireId;

    @Size(max = 80)
    private String referencePassager;

    @Size(max = 80)
    private String referenceCarteEmbarquement;

    @Size(max = 255)
    private String commentaire;

    @NotEmpty
    @Valid
    private List<CaisseVenteLigneRequest> lignes = new ArrayList<>();

    @NotEmpty
    @Valid
    private List<CaisseVentePaiementRequest> paiements = new ArrayList<>();

    public Long getBoutiqueId() {
        return boutiqueId;
    }

    public void setBoutiqueId(Long boutiqueId) {
        this.boutiqueId = boutiqueId;
    }

    public Long getLocataireId() {
        return locataireId;
    }

    public void setLocataireId(Long locataireId) {
        this.locataireId = locataireId;
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

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public List<CaisseVenteLigneRequest> getLignes() {
        return lignes;
    }

    public void setLignes(List<CaisseVenteLigneRequest> lignes) {
        this.lignes = lignes;
    }

    public List<CaisseVentePaiementRequest> getPaiements() {
        return paiements;
    }

    public void setPaiements(List<CaisseVentePaiementRequest> paiements) {
        this.paiements = paiements;
    }
}
