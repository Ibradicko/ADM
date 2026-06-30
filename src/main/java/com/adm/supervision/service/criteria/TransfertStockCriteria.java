package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.StatutMouvementStock;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.TransfertStock} entity. This class is used
 * in {@link com.adm.supervision.web.rest.TransfertStockResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transfert-stocks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransfertStockCriteria implements Serializable, Criteria {

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

    private InstantFilter dateTransfert;

    private StatutMouvementStockFilter statut;

    private StringFilter motif;

    private LongFilter boutiqueOrigineId;

    private LongFilter boutiqueDestinationId;

    private LongFilter utilisateurId;

    private Boolean distinct;

    public TransfertStockCriteria() {}

    public TransfertStockCriteria(TransfertStockCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.dateTransfert = other.optionalDateTransfert().map(InstantFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutMouvementStockFilter::copy).orElse(null);
        this.motif = other.optionalMotif().map(StringFilter::copy).orElse(null);
        this.boutiqueOrigineId = other.optionalBoutiqueOrigineId().map(LongFilter::copy).orElse(null);
        this.boutiqueDestinationId = other.optionalBoutiqueDestinationId().map(LongFilter::copy).orElse(null);
        this.utilisateurId = other.optionalUtilisateurId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TransfertStockCriteria copy() {
        return new TransfertStockCriteria(this);
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

    public InstantFilter getDateTransfert() {
        return dateTransfert;
    }

    public Optional<InstantFilter> optionalDateTransfert() {
        return Optional.ofNullable(dateTransfert);
    }

    public InstantFilter dateTransfert() {
        if (dateTransfert == null) {
            setDateTransfert(new InstantFilter());
        }
        return dateTransfert;
    }

    public void setDateTransfert(InstantFilter dateTransfert) {
        this.dateTransfert = dateTransfert;
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

    public LongFilter getBoutiqueOrigineId() {
        return boutiqueOrigineId;
    }

    public Optional<LongFilter> optionalBoutiqueOrigineId() {
        return Optional.ofNullable(boutiqueOrigineId);
    }

    public LongFilter boutiqueOrigineId() {
        if (boutiqueOrigineId == null) {
            setBoutiqueOrigineId(new LongFilter());
        }
        return boutiqueOrigineId;
    }

    public void setBoutiqueOrigineId(LongFilter boutiqueOrigineId) {
        this.boutiqueOrigineId = boutiqueOrigineId;
    }

    public LongFilter getBoutiqueDestinationId() {
        return boutiqueDestinationId;
    }

    public Optional<LongFilter> optionalBoutiqueDestinationId() {
        return Optional.ofNullable(boutiqueDestinationId);
    }

    public LongFilter boutiqueDestinationId() {
        if (boutiqueDestinationId == null) {
            setBoutiqueDestinationId(new LongFilter());
        }
        return boutiqueDestinationId;
    }

    public void setBoutiqueDestinationId(LongFilter boutiqueDestinationId) {
        this.boutiqueDestinationId = boutiqueDestinationId;
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
        final TransfertStockCriteria that = (TransfertStockCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(dateTransfert, that.dateTransfert) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(motif, that.motif) &&
            Objects.equals(boutiqueOrigineId, that.boutiqueOrigineId) &&
            Objects.equals(boutiqueDestinationId, that.boutiqueDestinationId) &&
            Objects.equals(utilisateurId, that.utilisateurId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, dateTransfert, statut, motif, boutiqueOrigineId, boutiqueDestinationId, utilisateurId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransfertStockCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalDateTransfert().map(f -> "dateTransfert=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalMotif().map(f -> "motif=" + f + ", ").orElse("") +
            optionalBoutiqueOrigineId().map(f -> "boutiqueOrigineId=" + f + ", ").orElse("") +
            optionalBoutiqueDestinationId().map(f -> "boutiqueDestinationId=" + f + ", ").orElse("") +
            optionalUtilisateurId().map(f -> "utilisateurId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
