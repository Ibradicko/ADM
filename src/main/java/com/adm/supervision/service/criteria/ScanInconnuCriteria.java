package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.ScanInconnu} entity. This class is used
 * in {@link com.adm.supervision.web.rest.ScanInconnuResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /scan-inconnus?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScanInconnuCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter codeScanne;

    private StringFilter ecranOrigine;

    private InstantFilter dateScan;

    private StringFilter commentaire;

    private BooleanFilter resolu;

    private LongFilter boutiqueId;

    private LongFilter produitAffecteId;

    private Boolean distinct;

    public ScanInconnuCriteria() {}

    public ScanInconnuCriteria(ScanInconnuCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.codeScanne = other.optionalCodeScanne().map(StringFilter::copy).orElse(null);
        this.ecranOrigine = other.optionalEcranOrigine().map(StringFilter::copy).orElse(null);
        this.dateScan = other.optionalDateScan().map(InstantFilter::copy).orElse(null);
        this.commentaire = other.optionalCommentaire().map(StringFilter::copy).orElse(null);
        this.resolu = other.optionalResolu().map(BooleanFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.produitAffecteId = other.optionalProduitAffecteId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ScanInconnuCriteria copy() {
        return new ScanInconnuCriteria(this);
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

    public StringFilter getCodeScanne() {
        return codeScanne;
    }

    public Optional<StringFilter> optionalCodeScanne() {
        return Optional.ofNullable(codeScanne);
    }

    public StringFilter codeScanne() {
        if (codeScanne == null) {
            setCodeScanne(new StringFilter());
        }
        return codeScanne;
    }

    public void setCodeScanne(StringFilter codeScanne) {
        this.codeScanne = codeScanne;
    }

    public StringFilter getEcranOrigine() {
        return ecranOrigine;
    }

    public Optional<StringFilter> optionalEcranOrigine() {
        return Optional.ofNullable(ecranOrigine);
    }

    public StringFilter ecranOrigine() {
        if (ecranOrigine == null) {
            setEcranOrigine(new StringFilter());
        }
        return ecranOrigine;
    }

    public void setEcranOrigine(StringFilter ecranOrigine) {
        this.ecranOrigine = ecranOrigine;
    }

    public InstantFilter getDateScan() {
        return dateScan;
    }

    public Optional<InstantFilter> optionalDateScan() {
        return Optional.ofNullable(dateScan);
    }

    public InstantFilter dateScan() {
        if (dateScan == null) {
            setDateScan(new InstantFilter());
        }
        return dateScan;
    }

    public void setDateScan(InstantFilter dateScan) {
        this.dateScan = dateScan;
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

    public BooleanFilter getResolu() {
        return resolu;
    }

    public Optional<BooleanFilter> optionalResolu() {
        return Optional.ofNullable(resolu);
    }

    public BooleanFilter resolu() {
        if (resolu == null) {
            setResolu(new BooleanFilter());
        }
        return resolu;
    }

    public void setResolu(BooleanFilter resolu) {
        this.resolu = resolu;
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

    public LongFilter getProduitAffecteId() {
        return produitAffecteId;
    }

    public Optional<LongFilter> optionalProduitAffecteId() {
        return Optional.ofNullable(produitAffecteId);
    }

    public LongFilter produitAffecteId() {
        if (produitAffecteId == null) {
            setProduitAffecteId(new LongFilter());
        }
        return produitAffecteId;
    }

    public void setProduitAffecteId(LongFilter produitAffecteId) {
        this.produitAffecteId = produitAffecteId;
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
        final ScanInconnuCriteria that = (ScanInconnuCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(codeScanne, that.codeScanne) &&
            Objects.equals(ecranOrigine, that.ecranOrigine) &&
            Objects.equals(dateScan, that.dateScan) &&
            Objects.equals(commentaire, that.commentaire) &&
            Objects.equals(resolu, that.resolu) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(produitAffecteId, that.produitAffecteId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, codeScanne, ecranOrigine, dateScan, commentaire, resolu, boutiqueId, produitAffecteId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScanInconnuCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCodeScanne().map(f -> "codeScanne=" + f + ", ").orElse("") +
            optionalEcranOrigine().map(f -> "ecranOrigine=" + f + ", ").orElse("") +
            optionalDateScan().map(f -> "dateScan=" + f + ", ").orElse("") +
            optionalCommentaire().map(f -> "commentaire=" + f + ", ").orElse("") +
            optionalResolu().map(f -> "resolu=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalProduitAffecteId().map(f -> "produitAffecteId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
