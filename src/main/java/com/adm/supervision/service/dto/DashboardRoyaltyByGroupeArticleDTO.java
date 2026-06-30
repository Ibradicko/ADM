package com.adm.supervision.service.dto;

import java.math.BigDecimal;

public class DashboardRoyaltyByGroupeArticleDTO {

    private Long groupeArticleId;
    private String groupeArticleCode;
    private String groupeArticleLibelle;
    private BigDecimal chiffreAffaires;
    private BigDecimal montantRedevance;
    private BigDecimal tauxEffectif;
    private long nombreArticlesVendus;

    public Long getGroupeArticleId() {
        return groupeArticleId;
    }

    public void setGroupeArticleId(Long groupeArticleId) {
        this.groupeArticleId = groupeArticleId;
    }

    public String getGroupeArticleCode() {
        return groupeArticleCode;
    }

    public void setGroupeArticleCode(String groupeArticleCode) {
        this.groupeArticleCode = groupeArticleCode;
    }

    public String getGroupeArticleLibelle() {
        return groupeArticleLibelle;
    }

    public void setGroupeArticleLibelle(String groupeArticleLibelle) {
        this.groupeArticleLibelle = groupeArticleLibelle;
    }

    public BigDecimal getChiffreAffaires() {
        return chiffreAffaires;
    }

    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }

    public BigDecimal getMontantRedevance() {
        return montantRedevance;
    }

    public void setMontantRedevance(BigDecimal montantRedevance) {
        this.montantRedevance = montantRedevance;
    }

    public BigDecimal getTauxEffectif() {
        return tauxEffectif;
    }

    public void setTauxEffectif(BigDecimal tauxEffectif) {
        this.tauxEffectif = tauxEffectif;
    }

    public long getNombreArticlesVendus() {
        return nombreArticlesVendus;
    }

    public void setNombreArticlesVendus(long nombreArticlesVendus) {
        this.nombreArticlesVendus = nombreArticlesVendus;
    }
}
