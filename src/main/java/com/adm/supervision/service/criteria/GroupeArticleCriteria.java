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
 * Criteria class for the {@link com.adm.supervision.domain.GroupeArticle} entity.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GroupeArticleCriteria implements Serializable, Criteria {

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

    private Boolean distinct;

    public GroupeArticleCriteria() {}

    public GroupeArticleCriteria(GroupeArticleCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.libelle = other.optionalLibelle().map(StringFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutGeneralFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public GroupeArticleCriteria copy() {
        return new GroupeArticleCriteria(this);
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
        final GroupeArticleCriteria that = (GroupeArticleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(libelle, that.libelle) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, libelle, statut, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GroupeArticleCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalLibelle().map(f -> "libelle=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
