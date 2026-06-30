package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.RegularisationRedevance} entity. This class is used
 * in {@link com.adm.supervision.web.rest.RegularisationRedevanceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /regularisation-redevances?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RegularisationRedevanceCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private BigDecimalFilter montant;

    private StringFilter motif;

    private InstantFilter dateRegularisation;

    private LongFilter calculId;

    private Boolean distinct;

    public RegularisationRedevanceCriteria() {}

    public RegularisationRedevanceCriteria(RegularisationRedevanceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.montant = other.optionalMontant().map(BigDecimalFilter::copy).orElse(null);
        this.motif = other.optionalMotif().map(StringFilter::copy).orElse(null);
        this.dateRegularisation = other.optionalDateRegularisation().map(InstantFilter::copy).orElse(null);
        this.calculId = other.optionalCalculId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RegularisationRedevanceCriteria copy() {
        return new RegularisationRedevanceCriteria(this);
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

    public StringFilter getReference() {
        return reference;
    }

    public Optional<StringFilter> optionalReference() {
        return Optional.ofNullable(reference);
    }

    public StringFilter reference() {
        if (reference == null) {
            setReference(new StringFilter());
        }
        return reference;
    }

    public void setReference(StringFilter reference) {
        this.reference = reference;
    }

    public BigDecimalFilter getMontant() {
        return montant;
    }

    public Optional<BigDecimalFilter> optionalMontant() {
        return Optional.ofNullable(montant);
    }

    public BigDecimalFilter montant() {
        if (montant == null) {
            setMontant(new BigDecimalFilter());
        }
        return montant;
    }

    public void setMontant(BigDecimalFilter montant) {
        this.montant = montant;
    }

    public StringFilter getMotif() {
        return motif;
    }

    public Optional<StringFilter> optionalMotif() {
        return Optional.ofNullable(motif);
    }

    public StringFilter motif() {
        if (motif == null) {
            setMotif(new StringFilter());
        }
        return motif;
    }

    public void setMotif(StringFilter motif) {
        this.motif = motif;
    }

    public InstantFilter getDateRegularisation() {
        return dateRegularisation;
    }

    public Optional<InstantFilter> optionalDateRegularisation() {
        return Optional.ofNullable(dateRegularisation);
    }

    public InstantFilter dateRegularisation() {
        if (dateRegularisation == null) {
            setDateRegularisation(new InstantFilter());
        }
        return dateRegularisation;
    }

    public void setDateRegularisation(InstantFilter dateRegularisation) {
        this.dateRegularisation = dateRegularisation;
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
        final RegularisationRedevanceCriteria that = (RegularisationRedevanceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(montant, that.montant) &&
            Objects.equals(motif, that.motif) &&
            Objects.equals(dateRegularisation, that.dateRegularisation) &&
            Objects.equals(calculId, that.calculId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, montant, motif, dateRegularisation, calculId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RegularisationRedevanceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalMontant().map(f -> "montant=" + f + ", ").orElse("") +
            optionalMotif().map(f -> "motif=" + f + ", ").orElse("") +
            optionalDateRegularisation().map(f -> "dateRegularisation=" + f + ", ").orElse("") +
            optionalCalculId().map(f -> "calculId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
