package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.StatutMouvementStock;
import com.adm.supervision.domain.enumeration.TypeMouvementStock;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.MouvementStock} entity. This class is used
 * in {@link com.adm.supervision.web.rest.MouvementStockResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /mouvement-stocks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MouvementStockCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeMouvementStock
     */
    public static class TypeMouvementStockFilter extends Filter<TypeMouvementStock> {

        public TypeMouvementStockFilter() {}

        public TypeMouvementStockFilter(TypeMouvementStockFilter filter) {
            super(filter);
        }

        @Override
        public TypeMouvementStockFilter copy() {
            return new TypeMouvementStockFilter(this);
        }
    }

    /**
     * Class for filtering StatutMouvementStock
     */
    public static class StatutMouvementStockFilter extends Filter<StatutMouvementStock> {

        public StatutMouvementStockFilter() {}

        public StatutMouvementStockFilter(StatutMouvementStockFilter filter) {
            super(filter);
        }

        @Override
        public StatutMouvementStockFilter copy() {
            return new StatutMouvementStockFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private TypeMouvementStockFilter typeMouvement;

    private StatutMouvementStockFilter statut;

    private InstantFilter dateMouvement;

    private StringFilter motif;

    private LongFilter boutiqueId;

    private LongFilter utilisateurId;

    private Boolean distinct;

    public MouvementStockCriteria() {}

    public MouvementStockCriteria(MouvementStockCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.typeMouvement = other.optionalTypeMouvement().map(TypeMouvementStockFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutMouvementStockFilter::copy).orElse(null);
        this.dateMouvement = other.optionalDateMouvement().map(InstantFilter::copy).orElse(null);
        this.motif = other.optionalMotif().map(StringFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.utilisateurId = other.optionalUtilisateurId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public MouvementStockCriteria copy() {
        return new MouvementStockCriteria(this);
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

    public TypeMouvementStockFilter getTypeMouvement() {
        return typeMouvement;
    }

    public Optional<TypeMouvementStockFilter> optionalTypeMouvement() {
        return Optional.ofNullable(typeMouvement);
    }

    public TypeMouvementStockFilter typeMouvement() {
        if (typeMouvement == null) {
            setTypeMouvement(new TypeMouvementStockFilter());
        }
        return typeMouvement;
    }

    public void setTypeMouvement(TypeMouvementStockFilter typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public StatutMouvementStockFilter getStatut() {
        return statut;
    }

    public Optional<StatutMouvementStockFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutMouvementStockFilter statut() {
        if (statut == null) {
            setStatut(new StatutMouvementStockFilter());
        }
        return statut;
    }

    public void setStatut(StatutMouvementStockFilter statut) {
        this.statut = statut;
    }

    public InstantFilter getDateMouvement() {
        return dateMouvement;
    }

    public Optional<InstantFilter> optionalDateMouvement() {
        return Optional.ofNullable(dateMouvement);
    }

    public InstantFilter dateMouvement() {
        if (dateMouvement == null) {
            setDateMouvement(new InstantFilter());
        }
        return dateMouvement;
    }

    public void setDateMouvement(InstantFilter dateMouvement) {
        this.dateMouvement = dateMouvement;
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
        final MouvementStockCriteria that = (MouvementStockCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(typeMouvement, that.typeMouvement) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(dateMouvement, that.dateMouvement) &&
            Objects.equals(motif, that.motif) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(utilisateurId, that.utilisateurId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, typeMouvement, statut, dateMouvement, motif, boutiqueId, utilisateurId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MouvementStockCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalTypeMouvement().map(f -> "typeMouvement=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalDateMouvement().map(f -> "dateMouvement=" + f + ", ").orElse("") +
            optionalMotif().map(f -> "motif=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalUtilisateurId().map(f -> "utilisateurId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
