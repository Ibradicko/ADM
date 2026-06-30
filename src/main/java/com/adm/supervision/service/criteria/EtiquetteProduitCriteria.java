package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.EtiquetteProduit} entity. This class is used
 * in {@link com.adm.supervision.web.rest.EtiquetteProduitResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /etiquette-produits?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EtiquetteProduitCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter quantite;

    private BooleanFilter imprimee;

    private InstantFilter dateImpression;

    private LongFilter produitId;

    private LongFilter lotId;

    private Boolean distinct;

    public EtiquetteProduitCriteria() {}

    public EtiquetteProduitCriteria(EtiquetteProduitCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quantite = other.optionalQuantite().map(IntegerFilter::copy).orElse(null);
        this.imprimee = other.optionalImprimee().map(BooleanFilter::copy).orElse(null);
        this.dateImpression = other.optionalDateImpression().map(InstantFilter::copy).orElse(null);
        this.produitId = other.optionalProduitId().map(LongFilter::copy).orElse(null);
        this.lotId = other.optionalLotId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EtiquetteProduitCriteria copy() {
        return new EtiquetteProduitCriteria(this);
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

    public IntegerFilter getQuantite() {
        return quantite;
    }

    public Optional<IntegerFilter> optionalQuantite() {
        return Optional.ofNullable(quantite);
    }

    public IntegerFilter quantite() {
        if (quantite == null) {
            setQuantite(new IntegerFilter());
        }
        return quantite;
    }

    public void setQuantite(IntegerFilter quantite) {
        this.quantite = quantite;
    }

    public BooleanFilter getImprimee() {
        return imprimee;
    }

    public Optional<BooleanFilter> optionalImprimee() {
        return Optional.ofNullable(imprimee);
    }

    public BooleanFilter imprimee() {
        if (imprimee == null) {
            setImprimee(new BooleanFilter());
        }
        return imprimee;
    }

    public void setImprimee(BooleanFilter imprimee) {
        this.imprimee = imprimee;
    }

    public InstantFilter getDateImpression() {
        return dateImpression;
    }

    public Optional<InstantFilter> optionalDateImpression() {
        return Optional.ofNullable(dateImpression);
    }

    public InstantFilter dateImpression() {
        if (dateImpression == null) {
            setDateImpression(new InstantFilter());
        }
        return dateImpression;
    }

    public void setDateImpression(InstantFilter dateImpression) {
        this.dateImpression = dateImpression;
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

    public LongFilter getLotId() {
        return lotId;
    }

    public Optional<LongFilter> optionalLotId() {
        return Optional.ofNullable(lotId);
    }

    public LongFilter lotId() {
        if (lotId == null) {
            setLotId(new LongFilter());
        }
        return lotId;
    }

    public void setLotId(LongFilter lotId) {
        this.lotId = lotId;
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
        final EtiquetteProduitCriteria that = (EtiquetteProduitCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantite, that.quantite) &&
            Objects.equals(imprimee, that.imprimee) &&
            Objects.equals(dateImpression, that.dateImpression) &&
            Objects.equals(produitId, that.produitId) &&
            Objects.equals(lotId, that.lotId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantite, imprimee, dateImpression, produitId, lotId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EtiquetteProduitCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuantite().map(f -> "quantite=" + f + ", ").orElse("") +
            optionalImprimee().map(f -> "imprimee=" + f + ", ").orElse("") +
            optionalDateImpression().map(f -> "dateImpression=" + f + ", ").orElse("") +
            optionalProduitId().map(f -> "produitId=" + f + ", ").orElse("") +
            optionalLotId().map(f -> "lotId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
