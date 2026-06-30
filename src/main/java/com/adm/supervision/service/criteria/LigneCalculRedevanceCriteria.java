package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.LigneCalculRedevance} entity. This class is used
 * in {@link com.adm.supervision.web.rest.LigneCalculRedevanceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ligne-calcul-redevances?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneCalculRedevanceCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter baseCalcul;

    private BigDecimalFilter tauxApplique;

    private BigDecimalFilter montantRedevance;

    private LongFilter calculId;

    private LongFilter venteId;

    private Boolean distinct;

    public LigneCalculRedevanceCriteria() {}

    public LigneCalculRedevanceCriteria(LigneCalculRedevanceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.baseCalcul = other.optionalBaseCalcul().map(BigDecimalFilter::copy).orElse(null);
        this.tauxApplique = other.optionalTauxApplique().map(BigDecimalFilter::copy).orElse(null);
        this.montantRedevance = other.optionalMontantRedevance().map(BigDecimalFilter::copy).orElse(null);
        this.calculId = other.optionalCalculId().map(LongFilter::copy).orElse(null);
        this.venteId = other.optionalVenteId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LigneCalculRedevanceCriteria copy() {
        return new LigneCalculRedevanceCriteria(this);
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

    public BigDecimalFilter getBaseCalcul() {
        return baseCalcul;
    }

    public Optional<BigDecimalFilter> optionalBaseCalcul() {
        return Optional.ofNullable(baseCalcul);
    }

    public BigDecimalFilter baseCalcul() {
        if (baseCalcul == null) {
            setBaseCalcul(new BigDecimalFilter());
        }
        return baseCalcul;
    }

    public void setBaseCalcul(BigDecimalFilter baseCalcul) {
        this.baseCalcul = baseCalcul;
    }

    public BigDecimalFilter getTauxApplique() {
        return tauxApplique;
    }

    public Optional<BigDecimalFilter> optionalTauxApplique() {
        return Optional.ofNullable(tauxApplique);
    }

    public BigDecimalFilter tauxApplique() {
        if (tauxApplique == null) {
            setTauxApplique(new BigDecimalFilter());
        }
        return tauxApplique;
    }

    public void setTauxApplique(BigDecimalFilter tauxApplique) {
        this.tauxApplique = tauxApplique;
    }

    public BigDecimalFilter getMontantRedevance() {
        return montantRedevance;
    }

    public Optional<BigDecimalFilter> optionalMontantRedevance() {
        return Optional.ofNullable(montantRedevance);
    }

    public BigDecimalFilter montantRedevance() {
        if (montantRedevance == null) {
            setMontantRedevance(new BigDecimalFilter());
        }
        return montantRedevance;
    }

    public void setMontantRedevance(BigDecimalFilter montantRedevance) {
        this.montantRedevance = montantRedevance;
    }

    public LongFilter getCalculId() {
        return calculId;
    }

    public Optional<LongFilter> optionalCalculId() {
        return Optional.ofNullable(calculId);
    }

    public LongFilter calculId() {
        if (calculId == null) {
            setCalculId(new LongFilter());
        }
        return calculId;
    }

    public void setCalculId(LongFilter calculId) {
        this.calculId = calculId;
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
        final LigneCalculRedevanceCriteria that = (LigneCalculRedevanceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(baseCalcul, that.baseCalcul) &&
            Objects.equals(tauxApplique, that.tauxApplique) &&
            Objects.equals(montantRedevance, that.montantRedevance) &&
            Objects.equals(calculId, that.calculId) &&
            Objects.equals(venteId, that.venteId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, baseCalcul, tauxApplique, montantRedevance, calculId, venteId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneCalculRedevanceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalBaseCalcul().map(f -> "baseCalcul=" + f + ", ").orElse("") +
            optionalTauxApplique().map(f -> "tauxApplique=" + f + ", ").orElse("") +
            optionalMontantRedevance().map(f -> "montantRedevance=" + f + ", ").orElse("") +
            optionalCalculId().map(f -> "calculId=" + f + ", ").orElse("") +
            optionalVenteId().map(f -> "venteId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
