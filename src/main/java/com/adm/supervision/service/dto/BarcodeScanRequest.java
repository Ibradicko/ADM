package com.adm.supervision.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BarcodeScanRequest {

    @NotBlank
    @Size(max = 80)
    private String code;

    @NotNull
    private Long boutiqueId;

    @Size(max = 80)
    private String ecranOrigine;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getBoutiqueId() {
        return boutiqueId;
    }

    public void setBoutiqueId(Long boutiqueId) {
        this.boutiqueId = boutiqueId;
    }

    public String getEcranOrigine() {
        return ecranOrigine;
    }

    public void setEcranOrigine(String ecranOrigine) {
        this.ecranOrigine = ecranOrigine;
    }
}
