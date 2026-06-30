package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.LigneMouvementStock} entity. This class is used
 * in {@link com.adm.supervision.web.rest.LigneMouvementStockResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ligne-mouvement-stocks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneMouvementStockCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter quantite;

    private BigDecimalFilter stockAvant;

    private BigDecimalFilter stockApres;

    private StringFilter commentaire;

    private LongFilter mouvementId;

    private LongFilter produitId;

    private LongFilter depotId;

    private Boolean distinct;

    public LigneMouvementStockCriteria() {}

    public LigneMouvementStockCriteria(LigneMouvementStockCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quantite = other.optionalQuantite().map(BigDecimalFilter::copy).orElse(null);
        this.stockAvant = other.optionalStockAvant().map(BigDecimalFilter::copy).orElse(null);
        this.stockApres = other.optionalStockApres().map(BigDecimalFilter::copy).orElse(null);
        this.commentaire = other.optionalCommentaire().map(StringFilter::copy).orElse(null);
        this.mouvementId = other.optionalMouvementId().map(LongFilter::copy).orElse(null);
        this.produitId = other.optionalProduitId().map(LongFilter::copy).orElse(null);
        this.depotId = other.optionalDepotId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LigneMouvementStockCriteria copy() {
        return new LigneMouvementStockCriteria(this);
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

    public BigDecimalFilter getQuantite() {
        return quantite;
    }

    public Optional<BigDecimalFilter> optionalQuantite() {
        return Optional.ofNullable(quantite);
    }

    public BigDecimalFilter quantite() {
        if (quantite == null) {
            setQuantite(new BigDecimalFilter());
        }
        return quantite;
    }

    public void setQuantite(BigDecimalFilter quantite) {
        this.quantite = quantite;
    }

    public BigDecimalFilter getStockAvant() {
        return stockAvant;
    }

    public Optional<BigDecimalFilter> optionalStockAvant() {
        return Optional.ofNullable(stockAvant);
    }

    public BigDecimalFilter stockAvant() {
        if (stockAvant == null) {
            setStockAvant(new BigDecimalFilter());
        }
        return stockAvant;
    }

    public void setStockAvant(BigDecimalFilter stockAvant) {
        this.stockAvant = stockAvant;
    }

    public BigDecimalFilter getStockApres() {
        return stockApres;
    }

    public Optional<BigDecimalFilter> optionalStockApres() {
        return Optional.ofNullable(stockApres);
    }

    public BigDecimalFilter stockApres() {
        if (stockApres == null) {
            setStockApres(new BigDecimalFilter());
        }
        return stockApres;
    }

    public void setStockApres(BigDecimalFilter stockApres) {
        this.stockApres = stockApres;
    }

    public StringFilter getCommentaire() {
        return commentaire;
    }

    public Optional<StringFilter> optionalCommentaire() {
        return Optional.ofNullable(commentaire);
    }

    public StringFilter commentaire() {
        if (commentaire == null) {
            setCommentaire(new StringFilter());
        }
        return commentaire;
    }

    public void setCommentaire(StringFilter commentaire) {
        this.commentaire = commentaire;
    }

    public LongFilter getMouvementId() {
        return mouvementId;
    }

    public Optional<LongFilter> optionalMouvementId() {
        return Optional.ofNullable(mouvementId);
    }

    public LongFilter mouvementId() {
        if (mouvementId == null) {
            setMouvementId(new LongFilter());
        }
        return mouvementId;
    }

    public void setMouvementId(LongFilter mouvementId) {
        this.mouvementId = mouvementId;
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

    public LongFilter getDepotId() {
        return depotId;
    }

    public Optional<LongFilter> optionalDepotId() {
        return Optional.ofNullable(depotId);
    }

    public LongFilter depotId() {
        if (depotId == null) {
            setDepotId(new LongFilter());
        }
        return depotId;
    }

    public void setDepotId(LongFilter depotId) {
        this.depotId = depotId;
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
        final LigneMouvementStockCriteria that = (LigneMouvementStockCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantite, that.quantite) &&
            Objects.equals(stockAvant, that.stockAvant) &&
            Objects.equals(stockApres, that.stockApres) &&
            Objects.equals(commentaire, that.commentaire) &&
            Objects.equals(mouvementId, that.mouvementId) &&
            Objects.equals(produitId, that.produitId) &&
            Objects.equals(depotId, that.depotId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantite, stockAvant, stockApres, commentaire, mouvementId, produitId, depotId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneMouvementStockCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuantite().map(f -> "quantite=" + f + ", ").orElse("") +
            optionalStockAvant().map(f -> "stockAvant=" + f + ", ").orElse("") +
            optionalStockApres().map(f -> "stockApres=" + f + ", ").orElse("") +
            optionalCommentaire().map(f -> "commentaire=" + f + ", ").orElse("") +
            optionalMouvementId().map(f -> "mouvementId=" + f + ", ").orElse("") +
            optionalProduitId().map(f -> "produitId=" + f + ", ").orElse("") +
            optionalDepotId().map(f -> "depotId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
