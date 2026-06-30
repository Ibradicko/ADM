package com.adm.supervision.service.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class GenerateCalculRedevanceRequest {

    @NotNull
    private LocalDate periodeDebut;

    @NotNull
    private LocalDate periodeFin;

    @NotNull
    private Long boutiqueId;

    @NotNull
    private Long locataireId;

    public LocalDate getPeriodeDebut() {
        return periodeDebut;
    }

    public void setPeriodeDebut(LocalDate periodeDebut) {
        this.periodeDebut = periodeDebut;
    }

    public LocalDate getPeriodeFin() {
        return periodeFin;
    }

    public void setPeriodeFin(LocalDate periodeFin) {
        this.periodeFin = periodeFin;
    }

    public Long getBoutiqueId() {
        return boutiqueId;
    }

    public void setBoutiqueId(Long boutiqueId) {
        this.boutiqueId = boutiqueId;
    }

    public Long getLocataireId() {
        return locataireId;
    }

    public void setLocataireId(Long locataireId) {
        this.locataireId = locataireId;
    }
}
