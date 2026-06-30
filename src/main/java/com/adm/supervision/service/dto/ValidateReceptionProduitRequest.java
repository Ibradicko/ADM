package com.adm.supervision.service.dto;

import jakarta.validation.constraints.NotNull;

public class ValidateReceptionProduitRequest {

    @NotNull
    private Long depotId;

    public Long getDepotId() {
        return depotId;
    }

    public void setDepotId(Long depotId) {
        this.depotId = depotId;
    }
}
