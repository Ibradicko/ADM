package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.TypeCodeBarres;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.ParametreCodeBarres} entity. This class is used
 * in {@link com.adm.supervision.web.rest.ParametreCodeBarresResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /parametre-code-barres?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParametreCodeBarresCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeCodeBarres
     */
    public static class TypeCodeBarresFilter extends Filter<TypeCodeBarres> {

        public TypeCodeBarresFilter() {}

        public TypeCodeBarresFilter(TypeCodeBarresFilter filter) {
            super(filter);
        }

        @Override
        public TypeCodeBarresFilter copy() {
            return new TypeCodeBarresFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private TypeCodeBarresFilter formatParDefaut;

    private StringFilter prefixe;

    private IntegerFilter longueur;

    private BooleanFilter actif;

    private Boolean distinct;

    public ParametreCodeBarresCriteria() {}

    public ParametreCodeBarresCriteria(ParametreCodeBarresCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.formatParDefaut = other.optionalFormatParDefaut().map(TypeCodeBarresFilter::copy).orElse(null);
        this.prefixe = other.optionalPrefixe().map(StringFilter::copy).orElse(null);
        this.longueur = other.optionalLongueur().map(IntegerFilter::copy).orElse(null);
        this.actif = other.optionalActif().map(BooleanFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ParametreCodeBarresCriteria copy() {
        return new ParametreCodeBarresCriteria(this);
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

    public TypeCodeBarresFilter getFormatParDefaut() {
        return formatParDefaut;
    }

    public Optional<TypeCodeBarresFilter> optionalFormatParDefaut() {
        return Optional.ofNullable(formatParDefaut);
    }

    public TypeCodeBarresFilter formatParDefaut() {
        if (formatParDefaut == null) {
            setFormatParDefaut(new TypeCodeBarresFilter());
        }
        return formatParDefaut;
    }

    public void setFormatParDefaut(TypeCodeBarresFilter formatParDefaut) {
        this.formatParDefaut = formatParDefaut;
    }

    public StringFilter getPrefixe() {
        return prefixe;
    }

    public Optional<StringFilter> optionalPrefixe() {
        return Optional.ofNullable(prefixe);
    }

    public StringFilter prefixe() {
        if (prefixe == null) {
            setPrefixe(new StringFilter());
        }
        return prefixe;
    }

    public void setPrefixe(StringFilter prefixe) {
        this.prefixe = prefixe;
    }

    public IntegerFilter getLongueur() {
        return longueur;
    }

    public Optional<IntegerFilter> optionalLongueur() {
        return Optional.ofNullable(longueur);
    }

    public IntegerFilter longueur() {
        if (longueur == null) {
            setLongueur(new IntegerFilter());
        }
        return longueur;
    }

    public void setLongueur(IntegerFilter longueur) {
        this.longueur = longueur;
    }

    public BooleanFilter getActif() {
        return actif;
    }

    public Optional<BooleanFilter> optionalActif() {
        return Optional.ofNullable(actif);
    }

    public BooleanFilter actif() {
        if (actif == null) {
            setActif(new BooleanFilter());
        }
        return actif;
    }

    public void setActif(BooleanFilter actif) {
        this.actif = actif;
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
        final ParametreCodeBarresCriteria that = (ParametreCodeBarresCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(formatParDefaut, that.formatParDefaut) &&
            Objects.equals(prefixe, that.prefixe) &&
            Objects.equals(longueur, that.longueur) &&
            Objects.equals(actif, that.actif) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, formatParDefaut, prefixe, longueur, actif, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParametreCodeBarresCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFormatParDefaut().map(f -> "formatParDefaut=" + f + ", ").orElse("") +
            optionalPrefixe().map(f -> "prefixe=" + f + ", ").orElse("") +
            optionalLongueur().map(f -> "longueur=" + f + ", ").orElse("") +
            optionalActif().map(f -> "actif=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
