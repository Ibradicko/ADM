package com.adm.supervision.service.dto;

public class CloseInventaireStockRequest {

    private boolean applyAdjustments = true;

    public boolean isApplyAdjustments() {
        return applyAdjustments;
    }

    public void setApplyAdjustments(boolean applyAdjustments) {
        this.applyAdjustments = applyAdjustments;
    }
}
