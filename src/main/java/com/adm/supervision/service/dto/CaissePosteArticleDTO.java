package com.adm.supervision.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Article vendable au poste de caisse : fusion du produit actif de la boutique et de son stock
 * disponible agrege sur tous les depots actifs de cette boutique.
 */
public class CaissePosteArticleDTO implements Serializable {

    private Long produitId;

    private String codeInterne;

    private String designation;

    private String description;

    private byte[] image;

    private String imageContentType;

    private BigDecimal prixVente;

    private Map<String, BigDecimal> tarifsParType = new HashMap<>();

    private Long groupeArticleId;

    private String groupeArticleLibelle;

    private BigDecimal stockDisponible;

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public String getCodeInterne() {
        return codeInterne;
    }

    public void setCodeInterne(String codeInterne) {
        this.codeInterne = codeInterne;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public BigDecimal getPrixVente() {
        return prixVente;
    }

    public void setPrixVente(BigDecimal prixVente) {
        this.prixVente = prixVente;
    }

    public Map<String, BigDecimal> getTarifsParType() {
        return tarifsParType;
    }

    public void setTarifsParType(Map<String, BigDecimal> tarifsParType) {
        this.tarifsParType = tarifsParType;
    }

    public Long getGroupeArticleId() {
        return groupeArticleId;
    }

    public void setGroupeArticleId(Long groupeArticleId) {
        this.groupeArticleId = groupeArticleId;
    }

    public String getGroupeArticleLibelle() {
        return groupeArticleLibelle;
    }

    public void setGroupeArticleLibelle(String groupeArticleLibelle) {
        this.groupeArticleLibelle = groupeArticleLibelle;
    }

    public BigDecimal getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(BigDecimal stockDisponible) {
        this.stockDisponible = stockDisponible;
    }
}
