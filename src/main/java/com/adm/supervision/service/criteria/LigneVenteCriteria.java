package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.LigneVente} entity. This class is used
 * in {@link com.adm.supervision.web.rest.LigneVenteResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ligne-ventes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneVenteCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter quantite;

    private BigDecimalFilter prixUnitaire;

    private BigDecimalFilter remise;

    private BigDecimalFilter montantLigne;

    private StringFilter codeBarresScanne;

    private LongFilter venteId;

    private LongFilter produitId;

    private Boolean distinct;

    public LigneVenteCriteria() {}

    public LigneVenteCriteria(LigneVenteCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quantite = other.optionalQuantite().map(BigDecimalFilter::copy).orElse(null);
        this.prixUnitaire = other.optionalPrixUnitaire().map(BigDecimalFilter::copy).orElse(null);
        this.remise = other.optionalRemise().map(BigDecimalFilter::copy).orElse(null);
        this.montantLigne = other.optionalMontantLigne().map(BigDecimalFilter::copy).orElse(null);
        this.codeBarresScanne = other.optionalCodeBarresScanne().map(StringFilter::copy).orElse(null);
        this.venteId = other.optionalVenteId().map(LongFilter::copy).orElse(null);
        this.produitId = other.optionalProduitId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LigneVenteCriteria copy() {
        return new LigneVenteCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public BigDecimalFilter getQuantite() {
        return quantite;
    }

    public Optional<BigDecimalFilter> optionalQuantite() {
        return Optional.ofNullable(quantite);
    }

    public BigDecimalFilter quantite() {
        if (quantite == null) {
            setQuantite(new BigDecimalFilter());
        }
        return quantite;
    }

    public void setQuantite(BigDecimalFilter quantite) {
        this.quantite = quantite;
    }

    public BigDecimalFilter getPrixUnitaire() {
        return prixUnitaire;
    }

    public Optional<BigDecimalFilter> optionalPrixUnitaire() {
        return Optional.ofNullable(prixUnitaire);
    }

    public BigDecimalFilter prixUnitaire() {
        if (prixUnitaire == null) {
            setPrixUnitaire(new BigDecimalFilter());
        }
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimalFilter prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimalFilter getRemise() {
        return remise;
    }

    public Optional<BigDecimalFilter> optionalRemise() {
        return Optional.ofNullable(remise);
    }

    public BigDecimalFilter remise() {
        if (remise == null) {
            setRemise(new BigDecimalFilter());
        }
        return remise;
    }

    public void setRemise(BigDecimalFilter remise) {
        this.remise = remise;
    }

    public BigDecimalFilter getMontantLigne() {
        return montantLigne;
    }

    public Optional<BigDecimalFilter> optionalMontantLigne() {
        return Optional.ofNullable(montantLigne);
    }

    public BigDecimalFilter montantLigne() {
        if (montantLigne == null) {
            setMontantLigne(new BigDecimalFilter());
        }
        return montantLigne;
    }

    public void setMontantLigne(BigDecimalFilter montantLigne) {
        this.montantLigne = montantLigne;
    }

    public StringFilter getCodeBarresScanne() {
        return codeBarresScanne;
    }

    public Optional<StringFilter> optionalCodeBarresScanne() {
        return Optional.ofNullable(codeBarresScanne);
    }

    public StringFilter codeBarresScanne() {
        if (codeBarresScanne == null) {
            setCodeBarresScanne(new StringFilter());
        }
        return codeBarresScanne;
    }

    public void setCodeBarresScanne(StringFilter codeBarresScanne) {
        this.codeBarresScanne = codeBarresScanne;
    }

    public LongFilter getVenteId() {
        return venteId;
    }

    public Optional<LongFilter> optionalVenteId() {
        return Optional.ofNullable(venteId);
    }

    public LongFilter venteId() {
        if (venteId == null) {
            setVenteId(new LongFilter());
        }
        return venteId;
    }

    public void setVenteId(LongFilter venteId) {
        this.venteId = venteId;
    }

    public LongFilter getProduitId() {
        return produitId;
    }

    public Optional<LongFilter> optionalProduitId() {
        return Optional.ofNullable(produitId);
    }

    public LongFilter produitId() {
        if (produitId == null) {
            setProduitId(new LongFilter());
        }
        return produitId;
    }

    public void setProduitId(LongFilter produitId) {
        this.produitId = produitId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LigneVenteCriteria that = (LigneVenteCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantite, that.quantite) &&
            Objects.equals(prixUnitaire, that.prixUnitaire) &&
            Objects.equals(remise, that.remise) &&
            Objects.equals(montantLigne, that.montantLigne) &&
            Objects.equals(codeBarresScanne, that.codeBarresScanne) &&
            Objects.equals(venteId, that.venteId) &&
            Objects.equals(produitId, that.produitId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantite, prixUnitaire, remise, montantLigne, codeBarresScanne, venteId, produitId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneVenteCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuantite().map(f -> "quantite=" + f + ", ").orElse("") +
            optionalPrixUnitaire().map(f -> "prixUnitaire=" + f + ", ").orElse("") +
            optionalRemise().map(f -> "remise=" + f + ", ").orElse("") +
            optionalMontantLigne().map(f -> "montantLigne=" + f + ", ").orElse("") +
            optionalCodeBarresScanne().map(f -> "codeBarresScanne=" + f + ", ").orElse("") +
            optionalVenteId().map(f -> "venteId=" + f + ", ").orElse("") +
            optionalProduitId().map(f -> "produitId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
