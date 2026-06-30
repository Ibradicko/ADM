package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.TypeRegleRedevance;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.RegleRedevance} entity. This class is used
 * in {@link com.adm.supervision.web.rest.RegleRedevanceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /regle-redevances?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RegleRedevanceCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeRegleRedevance
     */
    public static class TypeRegleRedevanceFilter extends Filter<TypeRegleRedevance> {

        public TypeRegleRedevanceFilter() {}

        public TypeRegleRedevanceFilter(TypeRegleRedevanceFilter filter) {
            super(filter);
        }

        @Override
        public TypeRegleRedevanceFilter copy() {
            return new TypeRegleRedevanceFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private TypeRegleRedevanceFilter typeRegle;

    private BigDecimalFilter taux;

    private LocalDateFilter dateDebut;

    private LocalDateFilter dateFin;

    private IntegerFilter priorite;

    private BooleanFilter actif;

    private LongFilter boutiqueId;

    private LongFilter locataireId;

    private LongFilter groupeArticleId;

    private LongFilter produitId;

    private Boolean distinct;

    public RegleRedevanceCriteria() {}

    public RegleRedevanceCriteria(RegleRedevanceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.typeRegle = other.optionalTypeRegle().map(TypeRegleRedevanceFilter::copy).orElse(null);
        this.taux = other.optionalTaux().map(BigDecimalFilter::copy).orElse(null);
        this.dateDebut = other.optionalDateDebut().map(LocalDateFilter::copy).orElse(null);
        this.dateFin = other.optionalDateFin().map(LocalDateFilter::copy).orElse(null);
        this.priorite = other.optionalPriorite().map(IntegerFilter::copy).orElse(null);
        this.actif = other.optionalActif().map(BooleanFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.locataireId = other.optionalLocataireId().map(LongFilter::copy).orElse(null);
        this.groupeArticleId = other.optionalGroupeArticleId().map(LongFilter::copy).orElse(null);
        this.produitId = other.optionalProduitId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RegleRedevanceCriteria copy() {
        return new RegleRedevanceCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public TypeRegleRedevanceFilter getTypeRegle() {
        return typeRegle;
    }

    public Optional<TypeRegleRedevanceFilter> optionalTypeRegle() {
        return Optional.ofNullable(typeRegle);
    }

    public TypeRegleRedevanceFilter typeRegle() {
        if (typeRegle == null) {
            setTypeRegle(new TypeRegleRedevanceFilter());
        }
        return typeRegle;
    }

    public void setTypeRegle(TypeRegleRedevanceFilter typeRegle) {
        this.typeRegle = typeRegle;
    }

    public BigDecimalFilter getTaux() {
        return taux;
    }

    public Optional<BigDecimalFilter> optionalTaux() {
        return Optional.ofNullable(taux);
    }

    public BigDecimalFilter taux() {
        if (taux == null) {
            setTaux(new BigDecimalFilter());
        }
        return taux;
    }

    public void setTaux(BigDecimalFilter taux) {
        this.taux = taux;
    }

    public LocalDateFilter getDateDebut() {
        return dateDebut;
    }

    public Optional<LocalDateFilter> optionalDateDebut() {
        return Optional.ofNullable(dateDebut);
    }

    public LocalDateFilter dateDebut() {
        if (dateDebut == null) {
            setDateDebut(new LocalDateFilter());
        }
        return dateDebut;
    }

    public void setDateDebut(LocalDateFilter dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateFilter getDateFin() {
        return dateFin;
    }

    public Optional<LocalDateFilter> optionalDateFin() {
        return Optional.ofNullable(dateFin);
    }

    public LocalDateFilter dateFin() {
        if (dateFin == null) {
            setDateFin(new LocalDateFilter());
        }
        return dateFin;
    }

    public void setDateFin(LocalDateFilter dateFin) {
        this.dateFin = dateFin;
    }

    public IntegerFilter getPriorite() {
        return priorite;
    }

    public Optional<IntegerFilter> optionalPriorite() {
        return Optional.ofNullable(priorite);
    }

    public IntegerFilter priorite() {
        if (priorite == null) {
            setPriorite(new IntegerFilter());
        }
        return priorite;
    }

    public void setPriorite(IntegerFilter priorite) {
        this.priorite = priorite;
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

    public LongFilter getGroupeArticleId() {
        return groupeArticleId;
    }

    public Optional<LongFilter> optionalGroupeArticleId() {
        return Optional.ofNullable(groupeArticleId);
    }

    public LongFilter groupeArticleId() {
        if (groupeArticleId == null) {
            setGroupeArticleId(new LongFilter());
        }
        return groupeArticleId;
    }

    public void setGroupeArticleId(LongFilter groupeArticleId) {
        this.groupeArticleId = groupeArticleId;
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
        final RegleRedevanceCriteria that = (RegleRedevanceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(typeRegle, that.typeRegle) &&
            Objects.equals(taux, that.taux) &&
            Objects.equals(dateDebut, that.dateDebut) &&
            Objects.equals(dateFin, that.dateFin) &&
            Objects.equals(priorite, that.priorite) &&
            Objects.equals(actif, that.actif) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(locataireId, that.locataireId) &&
            Objects.equals(groupeArticleId, that.groupeArticleId) &&
            Objects.equals(produitId, that.produitId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            code,
            typeRegle,
            taux,
            dateDebut,
            dateFin,
            priorite,
            actif,
            boutiqueId,
            locataireId,
            groupeArticleId,
            produitId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RegleRedevanceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalTypeRegle().map(f -> "typeRegle=" + f + ", ").orElse("") +
            optionalTaux().map(f -> "taux=" + f + ", ").orElse("") +
            optionalDateDebut().map(f -> "dateDebut=" + f + ", ").orElse("") +
            optionalDateFin().map(f -> "dateFin=" + f + ", ").orElse("") +
            optionalPriorite().map(f -> "priorite=" + f + ", ").orElse("") +
            optionalActif().map(f -> "actif=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalLocataireId().map(f -> "locataireId=" + f + ", ").orElse("") +
            optionalGroupeArticleId().map(f -> "groupeArticleId=" + f + ", ").orElse("") +
            optionalProduitId().map(f -> "produitId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
