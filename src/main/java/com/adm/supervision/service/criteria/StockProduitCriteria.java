package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.StockProduit} entity. This class is used
 * in {@link com.adm.supervision.web.rest.StockProduitResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stock-produits?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockProduitCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter quantiteTheorique;

    private BigDecimalFilter stockAlerte;

    private InstantFilter dateDernierMouvement;

    private LongFilter produitId;

    private LongFilter depotId;

    private LongFilter boutiqueId;

    private Boolean distinct;

    public StockProduitCriteria() {}

    public StockProduitCriteria(StockProduitCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quantiteTheorique = other.optionalQuantiteTheorique().map(BigDecimalFilter::copy).orElse(null);
        this.stockAlerte = other.optionalStockAlerte().map(BigDecimalFilter::copy).orElse(null);
        this.dateDernierMouvement = other.optionalDateDernierMouvement().map(InstantFilter::copy).orElse(null);
        this.produitId = other.optionalProduitId().map(LongFilter::copy).orElse(null);
        this.depotId = other.optionalDepotId().map(LongFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public StockProduitCriteria copy() {
        return new StockProduitCriteria(this);
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

    public BigDecimalFilter getQuantiteTheorique() {
        return quantiteTheorique;
    }

    public Optional<BigDecimalFilter> optionalQuantiteTheorique() {
        return Optional.ofNullable(quantiteTheorique);
    }

    public BigDecimalFilter quantiteTheorique() {
        if (quantiteTheorique == null) {
            setQuantiteTheorique(new BigDecimalFilter());
        }
        return quantiteTheorique;
    }

    public void setQuantiteTheorique(BigDecimalFilter quantiteTheorique) {
        this.quantiteTheorique = quantiteTheorique;
    }

    public BigDecimalFilter getStockAlerte() {
        return stockAlerte;
    }

    public Optional<BigDecimalFilter> optionalStockAlerte() {
        return Optional.ofNullable(stockAlerte);
    }

    public BigDecimalFilter stockAlerte() {
        if (stockAlerte == null) {
            setStockAlerte(new BigDecimalFilter());
        }
        return stockAlerte;
    }

    public void setStockAlerte(BigDecimalFilter stockAlerte) {
        this.stockAlerte = stockAlerte;
    }

    public InstantFilter getDateDernierMouvement() {
        return dateDernierMouvement;
    }

    public Optional<InstantFilter> optionalDateDernierMouvement() {
        return Optional.ofNullable(dateDernierMouvement);
    }

    public InstantFilter dateDernierMouvement() {
        if (dateDernierMouvement == null) {
            setDateDernierMouvement(new InstantFilter());
        }
        return dateDernierMouvement;
    }

    public void setDateDernierMouvement(InstantFilter dateDernierMouvement) {
        this.dateDernierMouvement = dateDernierMouvement;
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
        final StockProduitCriteria that = (StockProduitCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantiteTheorique, that.quantiteTheorique) &&
            Objects.equals(stockAlerte, that.stockAlerte) &&
            Objects.equals(dateDernierMouvement, that.dateDernierMouvement) &&
            Objects.equals(produitId, that.produitId) &&
            Objects.equals(depotId, that.depotId) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantiteTheorique, stockAlerte, dateDernierMouvement, produitId, depotId, boutiqueId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockProduitCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuantiteTheorique().map(f -> "quantiteTheorique=" + f + ", ").orElse("") +
            optionalStockAlerte().map(f -> "stockAlerte=" + f + ", ").orElse("") +
            optionalDateDernierMouvement().map(f -> "dateDernierMouvement=" + f + ", ").orElse("") +
            optionalProduitId().map(f -> "produitId=" + f + ", ").orElse("") +
            optionalDepotId().map(f -> "depotId=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
