package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.StatutRedevance;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.CalculRedevance} entity. This class is used
 * in {@link com.adm.supervision.web.rest.CalculRedevanceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /calcul-redevances?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CalculRedevanceCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatutRedevance
     */
    public static class StatutRedevanceFilter extends Filter<StatutRedevance> {

        public StatutRedevanceFilter() {}

        public StatutRedevanceFilter(StatutRedevanceFilter filter) {
            super(filter);
        }

        @Override
        public StatutRedevanceFilter copy() {
            return new StatutRedevanceFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private LocalDateFilter periodeDebut;

    private LocalDateFilter periodeFin;

    private BigDecimalFilter chiffreAffaires;

    private BigDecimalFilter montantRedevance;

    private StatutRedevanceFilter statut;

    private InstantFilter dateCalcul;

    private LongFilter boutiqueId;

    private LongFilter locataireId;

    private Boolean distinct;

    public CalculRedevanceCriteria() {}

    public CalculRedevanceCriteria(CalculRedevanceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.periodeDebut = other.optionalPeriodeDebut().map(LocalDateFilter::copy).orElse(null);
        this.periodeFin = other.optionalPeriodeFin().map(LocalDateFilter::copy).orElse(null);
        this.chiffreAffaires = other.optionalChiffreAffaires().map(BigDecimalFilter::copy).orElse(null);
        this.montantRedevance = other.optionalMontantRedevance().map(BigDecimalFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutRedevanceFilter::copy).orElse(null);
        this.dateCalcul = other.optionalDateCalcul().map(InstantFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.locataireId = other.optionalLocataireId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CalculRedevanceCriteria copy() {
        return new CalculRedevanceCriteria(this);
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

    public LocalDateFilter getPeriodeDebut() {
        return periodeDebut;
    }

    public Optional<LocalDateFilter> optionalPeriodeDebut() {
        return Optional.ofNullable(periodeDebut);
    }

    public LocalDateFilter periodeDebut() {
        if (periodeDebut == null) {
            setPeriodeDebut(new LocalDateFilter());
        }
        return periodeDebut;
    }

    public void setPeriodeDebut(LocalDateFilter periodeDebut) {
        this.periodeDebut = periodeDebut;
    }

    public LocalDateFilter getPeriodeFin() {
        return periodeFin;
    }

    public Optional<LocalDateFilter> optionalPeriodeFin() {
        return Optional.ofNullable(periodeFin);
    }

    public LocalDateFilter periodeFin() {
        if (periodeFin == null) {
            setPeriodeFin(new LocalDateFilter());
        }
        return periodeFin;
    }

    public void setPeriodeFin(LocalDateFilter periodeFin) {
        this.periodeFin = periodeFin;
    }

    public BigDecimalFilter getChiffreAffaires() {
        return chiffreAffaires;
    }

    public Optional<BigDecimalFilter> optionalChiffreAffaires() {
        return Optional.ofNullable(chiffreAffaires);
    }

    public BigDecimalFilter chiffreAffaires() {
        if (chiffreAffaires == null) {
            setChiffreAffaires(new BigDecimalFilter());
        }
        return chiffreAffaires;
    }

    public void setChiffreAffaires(BigDecimalFilter chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
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

    public StatutRedevanceFilter getStatut() {
        return statut;
    }

    public Optional<StatutRedevanceFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutRedevanceFilter statut() {
        if (statut == null) {
            setStatut(new StatutRedevanceFilter());
        }
        return statut;
    }

    public void setStatut(StatutRedevanceFilter statut) {
        this.statut = statut;
    }

    public InstantFilter getDateCalcul() {
        return dateCalcul;
    }

    public Optional<InstantFilter> optionalDateCalcul() {
        return Optional.ofNullable(dateCalcul);
    }

    public InstantFilter dateCalcul() {
        if (dateCalcul == null) {
            setDateCalcul(new InstantFilter());
        }
        return dateCalcul;
    }

    public void setDateCalcul(InstantFilter dateCalcul) {
        this.dateCalcul = dateCalcul;
    }

    public LongFilter getBoutiqueId() {
        return boutiqueId;
    }

    public Optional<LongFilter> optionalBoutiqueId() {
        return Optional.ofNullable(boutiqueId);
    }

    public LongFilter boutiqueId() {
        if (boutiqueId == null) {
            setBoutiqueId(new LongFilter());
        }
        return boutiqueId;
    }

    public void setBoutiqueId(LongFilter boutiqueId) {
        this.boutiqueId = boutiqueId;
    }

    public LongFilter getLocataireId() {
        return locataireId;
    }

    public Optional<LongFilter> optionalLocataireId() {
        return Optional.ofNullable(locataireId);
    }

    public LongFilter locataireId() {
        if (locataireId == null) {
            setLocataireId(new LongFilter());
        }
        return locataireId;
    }

    public void setLocataireId(LongFilter locataireId) {
        this.locataireId = locataireId;
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
        final CalculRedevanceCriteria that = (CalculRedevanceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(periodeDebut, that.periodeDebut) &&
            Objects.equals(periodeFin, that.periodeFin) &&
            Objects.equals(chiffreAffaires, that.chiffreAffaires) &&
            Objects.equals(montantRedevance, that.montantRedevance) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(dateCalcul, that.dateCalcul) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(locataireId, that.locataireId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            reference,
            periodeDebut,
            periodeFin,
            chiffreAffaires,
            montantRedevance,
            statut,
            dateCalcul,
            boutiqueId,
            locataireId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CalculRedevanceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalPeriodeDebut().map(f -> "periodeDebut=" + f + ", ").orElse("") +
            optionalPeriodeFin().map(f -> "periodeFin=" + f + ", ").orElse("") +
            optionalChiffreAffaires().map(f -> "chiffreAffaires=" + f + ", ").orElse("") +
            optionalMontantRedevance().map(f -> "montantRedevance=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalDateCalcul().map(f -> "dateCalcul=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalLocataireId().map(f -> "locataireId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
