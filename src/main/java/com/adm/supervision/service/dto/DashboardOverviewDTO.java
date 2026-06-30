package com.adm.supervision.service.dto;

import java.math.BigDecimal;

public class DashboardOverviewDTO {

    private BigDecimal grossSales;
    private BigDecimal netSales;
    private long validatedSalesCount;
    private long pendingSalesCount;
    private long stockAlertCount;
    private long unresolvedUnknownScans;
    private BigDecimal royaltyOutstandingAmount;

    public BigDecimal getGrossSales() {
        return grossSales;
    }

    public void setGrossSales(BigDecimal grossSales) {
        this.grossSales = grossSales;
    }

    public BigDecimal getNetSales() {
        return netSales;
    }

    public void setNetSales(BigDecimal netSales) {
        this.netSales = netSales;
    }

    public long getValidatedSalesCount() {
        return validatedSalesCount;
    }

    public void setValidatedSalesCount(long validatedSalesCount) {
        this.validatedSalesCount = validatedSalesCount;
    }

    public long getPendingSalesCount() {
        return pendingSalesCount;
    }

    public void setPendingSalesCount(long pendingSalesCount) {
        this.pendingSalesCount = pendingSalesCount;
    }

    public long getStockAlertCount() {
        return stockAlertCount;
    }

    public void setStockAlertCount(long stockAlertCount) {
        this.stockAlertCount = stockAlertCount;
    }

    public long getUnresolvedUnknownScans() {
        return unresolvedUnknownScans;
    }

    public void setUnresolvedUnknownScans(long unresolvedUnknownScans) {
        this.unresolvedUnknownScans = unresolvedUnknownScans;
    }

    public BigDecimal getRoyaltyOutstandingAmount() {
        return royaltyOutstandingAmount;
    }

    public void setRoyaltyOutstandingAmount(BigDecimal royaltyOutstandingAmount) {
        this.royaltyOutstandingAmount = royaltyOutstandingAmount;
    }
}
