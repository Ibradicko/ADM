package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.PaiementRedevance} entity. This class is used
 * in {@link com.adm.supervision.web.rest.PaiementRedevanceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /paiement-redevances?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaiementRedevanceCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private BigDecimalFilter montant;

    private LocalDateFilter datePaiement;

    private StringFilter modePaiement;

    private StringFilter commentaire;

    private LongFilter calculId;

    private LongFilter boutiqueId;

    private Boolean distinct;

    public PaiementRedevanceCriteria() {}

    public PaiementRedevanceCriteria(PaiementRedevanceCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.montant = other.optionalMontant().map(BigDecimalFilter::copy).orElse(null);
        this.datePaiement = other.optionalDatePaiement().map(LocalDateFilter::copy).orElse(null);
        this.modePaiement = other.optionalModePaiement().map(StringFilter::copy).orElse(null);
        this.commentaire = other.optionalCommentaire().map(StringFilter::copy).orElse(null);
        this.calculId = other.optionalCalculId().map(LongFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PaiementRedevanceCriteria copy() {
        return new PaiementRedevanceCriteria(this);
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

    public BigDecimalFilter getMontant() {
        return montant;
    }

    public Optional<BigDecimalFilter> optionalMontant() {
        return Optional.ofNullable(montant);
    }

    public BigDecimalFilter montant() {
        if (montant == null) {
            setMontant(new BigDecimalFilter());
        }
        return montant;
    }

    public void setMontant(BigDecimalFilter montant) {
        this.montant = montant;
    }

    public LocalDateFilter getDatePaiement() {
        return datePaiement;
    }

    public Optional<LocalDateFilter> optionalDatePaiement() {
        return Optional.ofNullable(datePaiement);
    }

    public LocalDateFilter datePaiement() {
        if (datePaiement == null) {
            setDatePaiement(new LocalDateFilter());
        }
        return datePaiement;
    }

    public void setDatePaiement(LocalDateFilter datePaiement) {
        this.datePaiement = datePaiement;
    }

    public StringFilter getModePaiement() {
        return modePaiement;
    }

    public Optional<StringFilter> optionalModePaiement() {
        return Optional.ofNullable(modePaiement);
    }

    public StringFilter modePaiement() {
        if (modePaiement == null) {
            setModePaiement(new StringFilter());
        }
        return modePaiement;
    }

    public void setModePaiement(StringFilter modePaiement) {
        this.modePaiement = modePaiement;
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

    public LongFilter getCalculId() {
        return calculId;
    }

    public Optional<LongFilter> optionalCalculId() {
        return Optional.ofNullable(calculId);
    }

    public LongFilter calculId() {
        if (calculId == null) {
            setCalculId(new LongFilter());
        }
        return calculId;
    }

    public void setCalculId(LongFilter calculId) {
        this.calculId = calculId;
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
        final PaiementRedevanceCriteria that = (PaiementRedevanceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(montant, that.montant) &&
            Objects.equals(datePaiement, that.datePaiement) &&
            Objects.equals(modePaiement, that.modePaiement) &&
            Objects.equals(commentaire, that.commentaire) &&
            Objects.equals(calculId, that.calculId) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, montant, datePaiement, modePaiement, commentaire, calculId, boutiqueId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaiementRedevanceCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalMontant().map(f -> "montant=" + f + ", ").orElse("") +
            optionalDatePaiement().map(f -> "datePaiement=" + f + ", ").orElse("") +
            optionalModePaiement().map(f -> "modePaiement=" + f + ", ").orElse("") +
            optionalCommentaire().map(f -> "commentaire=" + f + ", ").orElse("") +
            optionalCalculId().map(f -> "calculId=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
