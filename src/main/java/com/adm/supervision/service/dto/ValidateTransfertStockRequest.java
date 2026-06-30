package com.adm.supervision.service.dto;

import jakarta.validation.constraints.NotNull;

public class ValidateTransfertStockRequest {

    @NotNull
    private Long depotOrigineId;

    @NotNull
    private Long depotDestinationId;

    public Long getDepotOrigineId() {
        return depotOrigineId;
    }

    public void setDepotOrigineId(Long depotOrigineId) {
        this.depotOrigineId = depotOrigineId;
    }

    public Long getDepotDestinationId() {
        return depotDestinationId;
    }

    public void setDepotDestinationId(Long depotDestinationId) {
        this.depotDestinationId = depotDestinationId;
    }
}
