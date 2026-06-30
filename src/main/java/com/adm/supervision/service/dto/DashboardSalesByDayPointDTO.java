package com.adm.supervision.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DashboardSalesByDayPointDTO {

    private LocalDate day;
    private long validatedSalesCount;
    private BigDecimal grossAmount;
    private BigDecimal netAmount;

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public long getValidatedSalesCount() {
        return validatedSalesCount;
    }

    public void setValidatedSalesCount(long validatedSalesCount) {
        this.validatedSalesCount = validatedSalesCount;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }
}
