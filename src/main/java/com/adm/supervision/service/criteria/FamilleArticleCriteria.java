package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.StatutGeneral;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.FamilleArticle} entity. This class is used
 * in {@link com.adm.supervision.web.rest.FamilleArticleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /famille-articles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FamilleArticleCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatutGeneral
     */
    public static class StatutGeneralFilter extends Filter<StatutGeneral> {

        public StatutGeneralFilter() {}

        public StatutGeneralFilter(StatutGeneralFilter filter) {
            super(filter);
        }

        @Override
        public StatutGeneralFilter copy() {
            return new StatutGeneralFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter libelle;

    private StatutGeneralFilter statut;

    private LongFilter groupeArticleId;

    private Boolean distinct;

    public FamilleArticleCriteria() {}

    public FamilleArticleCriteria(FamilleArticleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.libelle = other.optionalLibelle().map(StringFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutGeneralFilter::copy).orElse(null);
        this.groupeArticleId = other.optionalGroupeArticleId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public FamilleArticleCriteria copy() {
        return new FamilleArticleCriteria(this);
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

    public StringFilter getLibelle() {
        return libelle;
    }

    public Optional<StringFilter> optionalLibelle() {
        return Optional.ofNullable(libelle);
    }

    public StringFilter libelle() {
        if (libelle == null) {
            setLibelle(new StringFilter());
        }
        return libelle;
    }

    public void setLibelle(StringFilter libelle) {
        this.libelle = libelle;
    }

    public StatutGeneralFilter getStatut() {
        return statut;
    }

    public Optional<StatutGeneralFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutGeneralFilter statut() {
        if (statut == null) {
            setStatut(new StatutGeneralFilter());
        }
        return statut;
    }

    public void setStatut(StatutGeneralFilter statut) {
        this.statut = statut;
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
        final FamilleArticleCriteria that = (FamilleArticleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(libelle, that.libelle) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(groupeArticleId, that.groupeArticleId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, libelle, statut, groupeArticleId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FamilleArticleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalLibelle().map(f -> "libelle=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalGroupeArticleId().map(f -> "groupeArticleId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
