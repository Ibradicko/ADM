package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.LotEtiquettes} entity. This class is used
 * in {@link com.adm.supervision.web.rest.LotEtiquettesResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /lot-etiquettes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LotEtiquettesCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private InstantFilter dateGeneration;

    private StringFilter formatImpression;

    private IntegerFilter nombreEtiquettes;

    private Boolean distinct;

    public LotEtiquettesCriteria() {}

    public LotEtiquettesCriteria(LotEtiquettesCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.dateGeneration = other.optionalDateGeneration().map(InstantFilter::copy).orElse(null);
        this.formatImpression = other.optionalFormatImpression().map(StringFilter::copy).orElse(null);
        this.nombreEtiquettes = other.optionalNombreEtiquettes().map(IntegerFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LotEtiquettesCriteria copy() {
        return new LotEtiquettesCriteria(this);
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

    public InstantFilter getDateGeneration() {
        return dateGeneration;
    }

    public Optional<InstantFilter> optionalDateGeneration() {
        return Optional.ofNullable(dateGeneration);
    }

    public InstantFilter dateGeneration() {
        if (dateGeneration == null) {
            setDateGeneration(new InstantFilter());
        }
        return dateGeneration;
    }

    public void setDateGeneration(InstantFilter dateGeneration) {
        this.dateGeneration = dateGeneration;
    }

    public StringFilter getFormatImpression() {
        return formatImpression;
    }

    public Optional<StringFilter> optionalFormatImpression() {
        return Optional.ofNullable(formatImpression);
    }

    public StringFilter formatImpression() {
        if (formatImpression == null) {
            setFormatImpression(new StringFilter());
        }
        return formatImpression;
    }

    public void setFormatImpression(StringFilter formatImpression) {
        this.formatImpression = formatImpression;
    }

    public IntegerFilter getNombreEtiquettes() {
        return nombreEtiquettes;
    }

    public Optional<IntegerFilter> optionalNombreEtiquettes() {
        return Optional.ofNullable(nombreEtiquettes);
    }

    public IntegerFilter nombreEtiquettes() {
        if (nombreEtiquettes == null) {
            setNombreEtiquettes(new IntegerFilter());
        }
        return nombreEtiquettes;
    }

    public void setNombreEtiquettes(IntegerFilter nombreEtiquettes) {
        this.nombreEtiquettes = nombreEtiquettes;
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
        final LotEtiquettesCriteria that = (LotEtiquettesCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(dateGeneration, that.dateGeneration) &&
            Objects.equals(formatImpression, that.formatImpression) &&
            Objects.equals(nombreEtiquettes, that.nombreEtiquettes) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, dateGeneration, formatImpression, nombreEtiquettes, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LotEtiquettesCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalDateGeneration().map(f -> "dateGeneration=" + f + ", ").orElse("") +
            optionalFormatImpression().map(f -> "formatImpression=" + f + ", ").orElse("") +
            optionalNombreEtiquettes().map(f -> "nombreEtiquettes=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
