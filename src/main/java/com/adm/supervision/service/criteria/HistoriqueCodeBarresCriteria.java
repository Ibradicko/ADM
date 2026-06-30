package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.HistoriqueCodeBarres} entity. This class is used
 * in {@link com.adm.supervision.web.rest.HistoriqueCodeBarresResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /historique-code-barres?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HistoriqueCodeBarresCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter ancienCode;

    private StringFilter nouveauCode;

    private StringFilter motif;

    private InstantFilter dateChangement;

    private LongFilter produitId;

    private LongFilter utilisateurId;

    private Boolean distinct;

    public HistoriqueCodeBarresCriteria() {}

    public HistoriqueCodeBarresCriteria(HistoriqueCodeBarresCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.ancienCode = other.optionalAncienCode().map(StringFilter::copy).orElse(null);
        this.nouveauCode = other.optionalNouveauCode().map(StringFilter::copy).orElse(null);
        this.motif = other.optionalMotif().map(StringFilter::copy).orElse(null);
        this.dateChangement = other.optionalDateChangement().map(InstantFilter::copy).orElse(null);
        this.produitId = other.optionalProduitId().map(LongFilter::copy).orElse(null);
        this.utilisateurId = other.optionalUtilisateurId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public HistoriqueCodeBarresCriteria copy() {
        return new HistoriqueCodeBarresCriteria(this);
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

    public StringFilter getAncienCode() {
        return ancienCode;
    }

    public Optional<StringFilter> optionalAncienCode() {
        return Optional.ofNullable(ancienCode);
    }

    public StringFilter ancienCode() {
        if (ancienCode == null) {
            setAncienCode(new StringFilter());
        }
        return ancienCode;
    }

    public void setAncienCode(StringFilter ancienCode) {
        this.ancienCode = ancienCode;
    }

    public StringFilter getNouveauCode() {
        return nouveauCode;
    }

    public Optional<StringFilter> optionalNouveauCode() {
        return Optional.ofNullable(nouveauCode);
    }

    public StringFilter nouveauCode() {
        if (nouveauCode == null) {
            setNouveauCode(new StringFilter());
        }
        return nouveauCode;
    }

    public void setNouveauCode(StringFilter nouveauCode) {
        this.nouveauCode = nouveauCode;
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

    public InstantFilter getDateChangement() {
        return dateChangement;
    }

    public Optional<InstantFilter> optionalDateChangement() {
        return Optional.ofNullable(dateChangement);
    }

    public InstantFilter dateChangement() {
        if (dateChangement == null) {
            setDateChangement(new InstantFilter());
        }
        return dateChangement;
    }

    public void setDateChangement(InstantFilter dateChangement) {
        this.dateChangement = dateChangement;
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
        final HistoriqueCodeBarresCriteria that = (HistoriqueCodeBarresCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(ancienCode, that.ancienCode) &&
            Objects.equals(nouveauCode, that.nouveauCode) &&
            Objects.equals(motif, that.motif) &&
            Objects.equals(dateChangement, that.dateChangement) &&
            Objects.equals(produitId, that.produitId) &&
            Objects.equals(utilisateurId, that.utilisateurId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ancienCode, nouveauCode, motif, dateChangement, produitId, utilisateurId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistoriqueCodeBarresCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAncienCode().map(f -> "ancienCode=" + f + ", ").orElse("") +
            optionalNouveauCode().map(f -> "nouveauCode=" + f + ", ").orElse("") +
            optionalMotif().map(f -> "motif=" + f + ", ").orElse("") +
            optionalDateChangement().map(f -> "dateChangement=" + f + ", ").orElse("") +
            optionalProduitId().map(f -> "produitId=" + f + ", ").orElse("") +
            optionalUtilisateurId().map(f -> "utilisateurId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
