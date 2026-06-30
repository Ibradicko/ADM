package com.adm.supervision.service.dto;

import java.math.BigDecimal;

public class DashboardStockAlertDTO {

    private Long stockProduitId;
    private Long produitId;
    private String produitDesignation;
    private Long depotId;
    private String depotCode;
    private Long boutiqueId;
    private String boutiqueNom;
    private BigDecimal quantiteTheorique;
    private BigDecimal stockAlerte;

    public Long getStockProduitId() {
        return stockProduitId;
    }

    public void setStockProduitId(Long stockProduitId) {
        this.stockProduitId = stockProduitId;
    }

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public String getProduitDesignation() {
        return produitDesignation;
    }

    public void setProduitDesignation(String produitDesignation) {
        this.produitDesignation = produitDesignation;
    }

    public Long getDepotId() {
        return depotId;
    }

    public void setDepotId(Long depotId) {
        this.depotId = depotId;
    }

    public String getDepotCode() {
        return depotCode;
    }

    public void setDepotCode(String depotCode) {
        this.depotCode = depotCode;
    }

    public Long getBoutiqueId() {
        return boutiqueId;
    }

    public void setBoutiqueId(Long boutiqueId) {
        this.boutiqueId = boutiqueId;
    }

    public String getBoutiqueNom() {
        return boutiqueNom;
    }

    public void setBoutiqueNom(String boutiqueNom) {
        this.boutiqueNom = boutiqueNom;
    }

    public BigDecimal getQuantiteTheorique() {
        return quantiteTheorique;
    }

    public void setQuantiteTheorique(BigDecimal quantiteTheorique) {
        this.quantiteTheorique = quantiteTheorique;
    }

    public BigDecimal getStockAlerte() {
        return stockAlerte;
    }

    public void setStockAlerte(BigDecimal stockAlerte) {
        this.stockAlerte = stockAlerte;
    }
}
