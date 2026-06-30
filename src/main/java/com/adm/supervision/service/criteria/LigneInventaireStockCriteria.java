package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.LigneInventaireStock} entity. This class is used
 * in {@link com.adm.supervision.web.rest.LigneInventaireStockResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ligne-inventaire-stocks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneInventaireStockCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter quantiteTheorique;

    private BigDecimalFilter quantiteComptee;

    private BigDecimalFilter ecart;

    private StringFilter commentaire;

    private LongFilter inventaireId;

    private LongFilter produitId;

    private Boolean distinct;

    public LigneInventaireStockCriteria() {}

    public LigneInventaireStockCriteria(LigneInventaireStockCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.quantiteTheorique = other.optionalQuantiteTheorique().map(BigDecimalFilter::copy).orElse(null);
        this.quantiteComptee = other.optionalQuantiteComptee().map(BigDecimalFilter::copy).orElse(null);
        this.ecart = other.optionalEcart().map(BigDecimalFilter::copy).orElse(null);
        this.commentaire = other.optionalCommentaire().map(StringFilter::copy).orElse(null);
        this.inventaireId = other.optionalInventaireId().map(LongFilter::copy).orElse(null);
        this.produitId = other.optionalProduitId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LigneInventaireStockCriteria copy() {
        return new LigneInventaireStockCriteria(this);
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

    public BigDecimalFilter getQuantiteComptee() {
        return quantiteComptee;
    }

    public Optional<BigDecimalFilter> optionalQuantiteComptee() {
        return Optional.ofNullable(quantiteComptee);
    }

    public BigDecimalFilter quantiteComptee() {
        if (quantiteComptee == null) {
            setQuantiteComptee(new BigDecimalFilter());
        }
        return quantiteComptee;
    }

    public void setQuantiteComptee(BigDecimalFilter quantiteComptee) {
        this.quantiteComptee = quantiteComptee;
    }

    public BigDecimalFilter getEcart() {
        return ecart;
    }

    public Optional<BigDecimalFilter> optionalEcart() {
        return Optional.ofNullable(ecart);
    }

    public BigDecimalFilter ecart() {
        if (ecart == null) {
            setEcart(new BigDecimalFilter());
        }
        return ecart;
    }

    public void setEcart(BigDecimalFilter ecart) {
        this.ecart = ecart;
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

    public LongFilter getInventaireId() {
        return inventaireId;
    }

    public Optional<LongFilter> optionalInventaireId() {
        return Optional.ofNullable(inventaireId);
    }

    public LongFilter inventaireId() {
        if (inventaireId == null) {
            setInventaireId(new LongFilter());
        }
        return inventaireId;
    }

    public void setInventaireId(LongFilter inventaireId) {
        this.inventaireId = inventaireId;
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
        final LigneInventaireStockCriteria that = (LigneInventaireStockCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(quantiteTheorique, that.quantiteTheorique) &&
            Objects.equals(quantiteComptee, that.quantiteComptee) &&
            Objects.equals(ecart, that.ecart) &&
            Objects.equals(commentaire, that.commentaire) &&
            Objects.equals(inventaireId, that.inventaireId) &&
            Objects.equals(produitId, that.produitId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantiteTheorique, quantiteComptee, ecart, commentaire, inventaireId, produitId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneInventaireStockCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalQuantiteTheorique().map(f -> "quantiteTheorique=" + f + ", ").orElse("") +
            optionalQuantiteComptee().map(f -> "quantiteComptee=" + f + ", ").orElse("") +
            optionalEcart().map(f -> "ecart=" + f + ", ").orElse("") +
            optionalCommentaire().map(f -> "commentaire=" + f + ", ").orElse("") +
            optionalInventaireId().map(f -> "inventaireId=" + f + ", ").orElse("") +
            optionalProduitId().map(f -> "produitId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
