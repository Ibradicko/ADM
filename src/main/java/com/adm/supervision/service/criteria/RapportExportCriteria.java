package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.FormatExport;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.RapportExport} entity. This class is used
 * in {@link com.adm.supervision.web.rest.RapportExportResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /rapport-exports?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RapportExportCriteria implements Serializable, Criteria {

    /**
     * Class for filtering FormatExport
     */
    public static class FormatExportFilter extends Filter<FormatExport> {

        public FormatExportFilter() {}

        public FormatExportFilter(FormatExportFilter filter) {
            super(filter);
        }

        @Override
        public FormatExportFilter copy() {
            return new FormatExportFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private StringFilter typeRapport;

    private FormatExportFilter format;

    private LocalDateFilter periodeDebut;

    private LocalDateFilter periodeFin;

    private StringFilter cheminFichier;

    private InstantFilter dateGeneration;

    private LongFilter boutiqueId;

    private LongFilter locataireId;

    private LongFilter utilisateurId;

    private Boolean distinct;

    public RapportExportCriteria() {}

    public RapportExportCriteria(RapportExportCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.typeRapport = other.optionalTypeRapport().map(StringFilter::copy).orElse(null);
        this.format = other.optionalFormat().map(FormatExportFilter::copy).orElse(null);
        this.periodeDebut = other.optionalPeriodeDebut().map(LocalDateFilter::copy).orElse(null);
        this.periodeFin = other.optionalPeriodeFin().map(LocalDateFilter::copy).orElse(null);
        this.cheminFichier = other.optionalCheminFichier().map(StringFilter::copy).orElse(null);
        this.dateGeneration = other.optionalDateGeneration().map(InstantFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.locataireId = other.optionalLocataireId().map(LongFilter::copy).orElse(null);
        this.utilisateurId = other.optionalUtilisateurId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RapportExportCriteria copy() {
        return new RapportExportCriteria(this);
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

    public StringFilter getTypeRapport() {
        return typeRapport;
    }

    public Optional<StringFilter> optionalTypeRapport() {
        return Optional.ofNullable(typeRapport);
    }

    public StringFilter typeRapport() {
        if (typeRapport == null) {
            setTypeRapport(new StringFilter());
        }
        return typeRapport;
    }

    public void setTypeRapport(StringFilter typeRapport) {
        this.typeRapport = typeRapport;
    }

    public FormatExportFilter getFormat() {
        return format;
    }

    public Optional<FormatExportFilter> optionalFormat() {
        return Optional.ofNullable(format);
    }

    public FormatExportFilter format() {
        if (format == null) {
            setFormat(new FormatExportFilter());
        }
        return format;
    }

    public void setFormat(FormatExportFilter format) {
        this.format = format;
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

    public StringFilter getCheminFichier() {
        return cheminFichier;
    }

    public Optional<StringFilter> optionalCheminFichier() {
        return Optional.ofNullable(cheminFichier);
    }

    public StringFilter cheminFichier() {
        if (cheminFichier == null) {
            setCheminFichier(new StringFilter());
        }
        return cheminFichier;
    }

    public void setCheminFichier(StringFilter cheminFichier) {
        this.cheminFichier = cheminFichier;
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

    public LongFilter getUtilisateurId() {
        return utilisateurId;
    }

    public Optional<LongFilter> optionalUtilisateurId() {
        return Optional.ofNullable(utilisateurId);
    }

    public LongFilter utilisateurId() {
        if (utilisateurId == null) {
            setUtilisateurId(new LongFilter());
        }
        return utilisateurId;
    }

    public void setUtilisateurId(LongFilter utilisateurId) {
        this.utilisateurId = utilisateurId;
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
        final RapportExportCriteria that = (RapportExportCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(typeRapport, that.typeRapport) &&
            Objects.equals(format, that.format) &&
            Objects.equals(periodeDebut, that.periodeDebut) &&
            Objects.equals(periodeFin, that.periodeFin) &&
            Objects.equals(cheminFichier, that.cheminFichier) &&
            Objects.equals(dateGeneration, that.dateGeneration) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(locataireId, that.locataireId) &&
            Objects.equals(utilisateurId, that.utilisateurId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            reference,
            typeRapport,
            format,
            periodeDebut,
            periodeFin,
            cheminFichier,
            dateGeneration,
            boutiqueId,
            locataireId,
            utilisateurId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RapportExportCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalTypeRapport().map(f -> "typeRapport=" + f + ", ").orElse("") +
            optionalFormat().map(f -> "format=" + f + ", ").orElse("") +
            optionalPeriodeDebut().map(f -> "periodeDebut=" + f + ", ").orElse("") +
            optionalPeriodeFin().map(f -> "periodeFin=" + f + ", ").orElse("") +
            optionalCheminFichier().map(f -> "cheminFichier=" + f + ", ").orElse("") +
            optionalDateGeneration().map(f -> "dateGeneration=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalLocataireId().map(f -> "locataireId=" + f + ", ").orElse("") +
            optionalUtilisateurId().map(f -> "utilisateurId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
