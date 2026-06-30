package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.LigneReceptionProduit} entity. This class is used
 * in {@link com.adm.supervision.web.rest.LigneReceptionProduitResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ligne-reception-produits?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneReceptionProduitCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter quantiteAttendue;

    private BigDecimalFilter quantiteRecue;

    private BigDecimalFilter ecart;

    private StringFilter codeBarresScanne;

    private LongFilter receptionId;

    private LongFilter produitId;

    private Boolean distinct;

    public LigneReceptionProduitCriteria() {}

    public LigneReceptionProduitCriteria(LigneReceptionProduitCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quantiteAttendue = other.optionalQuantiteAttendue().map(BigDecimalFilter::copy).orElse(null);
        this.quantiteRecue = other.optionalQuantiteRecue().map(BigDecimalFilter::copy).orElse(null);
        this.ecart = other.optionalEcart().map(BigDecimalFilter::copy).orElse(null);
        this.codeBarresScanne = other.optionalCodeBarresScanne().map(StringFilter::copy).orElse(null);
        this.receptionId = other.optionalReceptionId().map(LongFilter::copy).orElse(null);
        this.produitId = other.optionalProduitId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LigneReceptionProduitCriteria copy() {
        return new LigneReceptionProduitCriteria(this);
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

    public BigDecimalFilter getQuantiteAttendue() {
        return quantiteAttendue;
    }

    public Optional<BigDecimalFilter> optionalQuantiteAttendue() {
        return Optional.ofNullable(quantiteAttendue);
    }

    public BigDecimalFilter quantiteAttendue() {
        if (quantiteAttendue == null) {
            setQuantiteAttendue(new BigDecimalFilter());
        }
        return quantiteAttendue;
    }

    public void setQuantiteAttendue(BigDecimalFilter quantiteAttendue) {
        this.quantiteAttendue = quantiteAttendue;
    }

    public BigDecimalFilter getQuantiteRecue() {
        return quantiteRecue;
    }

    public Optional<BigDecimalFilter> optionalQuantiteRecue() {
        return Optional.ofNullable(quantiteRecue);
    }

    public BigDecimalFilter quantiteRecue() {
        if (quantiteRecue == null) {
            setQuantiteRecue(new BigDecimalFilter());
        }
        return quantiteRecue;
    }

    public void setQuantiteRecue(BigDecimalFilter quantiteRecue) {
        this.quantiteRecue = quantiteRecue;
    }

    public BigDecimalFilter getEcart() {
        return ecart;
    }

    public Optional<BigDecimalFilter> optionalEcart() {
        return Optional.ofNullable(ecart);
    }

    public BigDecimalFilter ecart() {
        if (ecart == null) {
            setEcart(new BigDecimalFilter());
        }
        return ecart;
    }

    public void setEcart(BigDecimalFilter ecart) {
        this.ecart = ecart;
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

    public LongFilter getReceptionId() {
        return receptionId;
    }

    public Optional<LongFilter> optionalReceptionId() {
        return Optional.ofNullable(receptionId);
    }

    public LongFilter receptionId() {
        if (receptionId == null) {
            setReceptionId(new LongFilter());
        }
        return receptionId;
    }

    public void setReceptionId(LongFilter receptionId) {
        this.receptionId = receptionId;
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
        final LigneReceptionProduitCriteria that = (LigneReceptionProduitCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantiteAttendue, that.quantiteAttendue) &&
            Objects.equals(quantiteRecue, that.quantiteRecue) &&
            Objects.equals(ecart, that.ecart) &&
            Objects.equals(codeBarresScanne, that.codeBarresScanne) &&
            Objects.equals(receptionId, that.receptionId) &&
            Objects.equals(produitId, that.produitId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantiteAttendue, quantiteRecue, ecart, codeBarresScanne, receptionId, produitId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneReceptionProduitCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuantiteAttendue().map(f -> "quantiteAttendue=" + f + ", ").orElse("") +
            optionalQuantiteRecue().map(f -> "quantiteRecue=" + f + ", ").orElse("") +
            optionalEcart().map(f -> "ecart=" + f + ", ").orElse("") +
            optionalCodeBarresScanne().map(f -> "codeBarresScanne=" + f + ", ").orElse("") +
            optionalReceptionId().map(f -> "receptionId=" + f + ", ").orElse("") +
            optionalProduitId().map(f -> "produitId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
