package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.FormatExport;
import com.adm.supervision.domain.enumeration.StatutVente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GenerateRapportExportRequest {

    @NotBlank
    private String typeRapport;

    @NotNull
    private FormatExport format;

    private LocalDate periodeDebut;

    private LocalDate periodeFin;

    private Long boutiqueId;

    private Long locataireId;

    private Long depotId;

    private Long produitId;

    private StatutVente statutVente;

    private BigDecimal minMontantNet;

    public String getTypeRapport() {
        return typeRapport;
    }

    public void setTypeRapport(String typeRapport) {
        this.typeRapport = typeRapport;
    }

    public FormatExport getFormat() {
        return format;
    }

    public void setFormat(FormatExport format) {
        this.format = format;
    }

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

    public Long getDepotId() {
        return depotId;
    }

    public void setDepotId(Long depotId) {
        this.depotId = depotId;
    }

    public Long getProduitId() {
        return produitId;
    }

    public void setProduitId(Long produitId) {
        this.produitId = produitId;
    }

    public StatutVente getStatutVente() {
        return statutVente;
    }

    public void setStatutVente(StatutVente statutVente) {
        this.statutVente = statutVente;
    }

    public BigDecimal getMinMontantNet() {
        return minMontantNet;
    }

    public void setMinMontantNet(BigDecimal minMontantNet) {
        this.minMontantNet = minMontantNet;
    }
}
