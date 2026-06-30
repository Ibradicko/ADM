package com.adm.supervision.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.LigneReceptionProduit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneReceptionProduitDTO implements Serializable {

    private Long id;

    @DecimalMin(value = "0")
    private BigDecimal quantiteAttendue;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal quantiteRecue;

    private BigDecimal ecart;

    @Size(max = 80)
    private String codeBarresScanne;

    @NotNull
    private ReceptionProduitDTO reception;

    @NotNull
    private ProduitDTO produit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantiteAttendue() {
        return quantiteAttendue;
    }

    public void setQuantiteAttendue(BigDecimal quantiteAttendue) {
        this.quantiteAttendue = quantiteAttendue;
    }

    public BigDecimal getQuantiteRecue() {
        return quantiteRecue;
    }

    public void setQuantiteRecue(BigDecimal quantiteRecue) {
        this.quantiteRecue = quantiteRecue;
    }

    public BigDecimal getEcart() {
        return ecart;
    }

    public void setEcart(BigDecimal ecart) {
        this.ecart = ecart;
    }

    public String getCodeBarresScanne() {
        return codeBarresScanne;
    }

    public void setCodeBarresScanne(String codeBarresScanne) {
        this.codeBarresScanne = codeBarresScanne;
    }

    public ReceptionProduitDTO getReception() {
        return reception;
    }

    public void setReception(ReceptionProduitDTO reception) {
        this.reception = reception;
    }

    public ProduitDTO getProduit() {
        return produit;
    }

    public void setProduit(ProduitDTO produit) {
        this.produit = produit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LigneReceptionProduitDTO)) {
            return false;
        }

        LigneReceptionProduitDTO ligneReceptionProduitDTO = (LigneReceptionProduitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ligneReceptionProduitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneReceptionProduitDTO{" +
            "id=" + getId() +
            ", quantiteAttendue=" + getQuantiteAttendue() +
            ", quantiteRecue=" + getQuantiteRecue() +
            ", ecart=" + getEcart() +
            ", codeBarresScanne='" + getCodeBarresScanne() + "'" +
            ", reception=" + getReception() +
            ", produit=" + getProduit() +
            "}";
    }
}
