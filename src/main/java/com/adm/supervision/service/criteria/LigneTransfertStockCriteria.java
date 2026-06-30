package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.LigneTransfertStock} entity. This class is used
 * in {@link com.adm.supervision.web.rest.LigneTransfertStockResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ligne-transfert-stocks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneTransfertStockCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter quantite;

    private StringFilter commentaire;

    private LongFilter transfertId;

    private LongFilter produitId;

    private Boolean distinct;

    public LigneTransfertStockCriteria() {}

    public LigneTransfertStockCriteria(LigneTransfertStockCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quantite = other.optionalQuantite().map(BigDecimalFilter::copy).orElse(null);
        this.commentaire = other.optionalCommentaire().map(StringFilter::copy).orElse(null);
        this.transfertId = other.optionalTransfertId().map(LongFilter::copy).orElse(null);
        this.produitId = other.optionalProduitId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LigneTransfertStockCriteria copy() {
        return new LigneTransfertStockCriteria(this);
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

    public LongFilter getTransfertId() {
        return transfertId;
    }

    public Optional<LongFilter> optionalTransfertId() {
        return Optional.ofNullable(transfertId);
    }

    public LongFilter transfertId() {
        if (transfertId == null) {
            setTransfertId(new LongFilter());
        }
        return transfertId;
    }

    public void setTransfertId(LongFilter transfertId) {
        this.transfertId = transfertId;
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
        final LigneTransfertStockCriteria that = (LigneTransfertStockCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantite, that.quantite) &&
            Objects.equals(commentaire, that.commentaire) &&
            Objects.equals(transfertId, that.transfertId) &&
            Objects.equals(produitId, that.produitId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantite, commentaire, transfertId, produitId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneTransfertStockCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuantite().map(f -> "quantite=" + f + ", ").orElse("") +
            optionalCommentaire().map(f -> "commentaire=" + f + ", ").orElse("") +
            optionalTransfertId().map(f -> "transfertId=" + f + ", ").orElse("") +
            optionalProduitId().map(f -> "produitId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
