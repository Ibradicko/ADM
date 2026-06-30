package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.ScanInconnu} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScanInconnuDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String codeScanne;

    @Size(max = 80)
    private String ecranOrigine;

    @NotNull
    private Instant dateScan;

    @Size(max = 255)
    private String commentaire;

    @NotNull
    private Boolean resolu;

    @NotNull
    private BoutiqueDTO boutique;

    private ProduitDTO produitAffecte;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeScanne() {
        return codeScanne;
    }

    public void setCodeScanne(String codeScanne) {
        this.codeScanne = codeScanne;
    }

    public String getEcranOrigine() {
        return ecranOrigine;
    }

    public void setEcranOrigine(String ecranOrigine) {
        this.ecranOrigine = ecranOrigine;
    }

    public Instant getDateScan() {
        return dateScan;
    }

    public void setDateScan(Instant dateScan) {
        this.dateScan = dateScan;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Boolean getResolu() {
        return resolu;
    }

    public void setResolu(Boolean resolu) {
        this.resolu = resolu;
    }

    public BoutiqueDTO getBoutique() {
        return boutique;
    }

    public void setBoutique(BoutiqueDTO boutique) {
        this.boutique = boutique;
    }

    public ProduitDTO getProduitAffecte() {
        return produitAffecte;
    }

    public void setProduitAffecte(ProduitDTO produitAffecte) {
        this.produitAffecte = produitAffecte;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScanInconnuDTO)) {
            return false;
        }

        ScanInconnuDTO scanInconnuDTO = (ScanInconnuDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, scanInconnuDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScanInconnuDTO{" +
            "id=" + getId() +
            ", codeScanne='" + getCodeScanne() + "'" +
            ", ecranOrigine='" + getEcranOrigine() + "'" +
            ", dateScan='" + getDateScan() + "'" +
            ", commentaire='" + getCommentaire() + "'" +
            ", resolu='" + getResolu() + "'" +
            ", boutique=" + getBoutique() +
            ", produitAffecte=" + getProduitAffecte() +
            "}";
    }
}
