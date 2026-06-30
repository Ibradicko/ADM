package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.ReceptionProduit} entity. This class is used
 * in {@link com.adm.supervision.web.rest.ReceptionProduitResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /reception-produits?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReceptionProduitCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private InstantFilter dateReception;

    private StringFilter fournisseur;

    private StringFilter commentaire;

    private LongFilter boutiqueId;

    private LongFilter utilisateurId;

    private Boolean distinct;

    public ReceptionProduitCriteria() {}

    public ReceptionProduitCriteria(ReceptionProduitCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.dateReception = other.optionalDateReception().map(InstantFilter::copy).orElse(null);
        this.fournisseur = other.optionalFournisseur().map(StringFilter::copy).orElse(null);
        this.commentaire = other.optionalCommentaire().map(StringFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.utilisateurId = other.optionalUtilisateurId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ReceptionProduitCriteria copy() {
        return new ReceptionProduitCriteria(this);
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

    public InstantFilter getDateReception() {
        return dateReception;
    }

    public Optional<InstantFilter> optionalDateReception() {
        return Optional.ofNullable(dateReception);
    }

    public InstantFilter dateReception() {
        if (dateReception == null) {
            setDateReception(new InstantFilter());
        }
        return dateReception;
    }

    public void setDateReception(InstantFilter dateReception) {
        this.dateReception = dateReception;
    }

    public StringFilter getFournisseur() {
        return fournisseur;
    }

    public Optional<StringFilter> optionalFournisseur() {
        return Optional.ofNullable(fournisseur);
    }

    public StringFilter fournisseur() {
        if (fournisseur == null) {
            setFournisseur(new StringFilter());
        }
        return fournisseur;
    }

    public void setFournisseur(StringFilter fournisseur) {
        this.fournisseur = fournisseur;
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
        final ReceptionProduitCriteria that = (ReceptionProduitCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(dateReception, that.dateReception) &&
            Objects.equals(fournisseur, that.fournisseur) &&
            Objects.equals(commentaire, that.commentaire) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(utilisateurId, that.utilisateurId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, dateReception, fournisseur, commentaire, boutiqueId, utilisateurId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReceptionProduitCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalDateReception().map(f -> "dateReception=" + f + ", ").orElse("") +
            optionalFournisseur().map(f -> "fournisseur=" + f + ", ").orElse("") +
            optionalCommentaire().map(f -> "commentaire=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalUtilisateurId().map(f -> "utilisateurId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
