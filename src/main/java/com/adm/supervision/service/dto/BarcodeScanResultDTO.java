package com.adm.supervision.service.dto;

public class BarcodeScanResultDTO {

    private boolean trouve;
    private String message;
    private boolean affectationAutorisee;
    private Long scanInconnuId;
    private ProduitDTO produit;

    public boolean isTrouve() {
        return trouve;
    }

    public void setTrouve(boolean trouve) {
        this.trouve = trouve;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAffectationAutorisee() {
        return affectationAutorisee;
    }

    public void setAffectationAutorisee(boolean affectationAutorisee) {
        this.affectationAutorisee = affectationAutorisee;
    }

    public Long getScanInconnuId() {
        return scanInconnuId;
    }

    public void setScanInconnuId(Long scanInconnuId) {
        this.scanInconnuId = scanInconnuId;
    }

    public ProduitDTO getProduit() {
        return produit;
    }

    public void setProduit(ProduitDTO produit) {
        this.produit = produit;
    }
}
