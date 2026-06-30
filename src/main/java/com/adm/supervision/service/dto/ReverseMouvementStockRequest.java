package com.adm.supervision.service.dto;

import jakarta.validation.constraints.Size;

public class ReverseMouvementStockRequest {

    @Size(max = 255)
    private String motif;

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }
}
