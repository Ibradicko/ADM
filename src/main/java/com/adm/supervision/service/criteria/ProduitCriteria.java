package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.TypePrix;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.Produit} entity. This class is used
 * in {@link com.adm.supervision.web.rest.ProduitResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /produits?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProduitCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypePrix
     */
    public static class TypePrixFilter extends Filter<TypePrix> {

        public TypePrixFilter() {}

        public TypePrixFilter(TypePrixFilter filter) {
            super(filter);
        }

        @Override
        public TypePrixFilter copy() {
            return new TypePrixFilter(this);
        }
    }

    /**
     * Class for filtering StatutGeneral
     */
    public static class StatutGeneralFilter extends Filter<StatutGeneral> {

        public StatutGeneralFilter() {}

        public StatutGeneralFilter(StatutGeneralFilter filter) {
            super(filter);
        }

        @Override
        public StatutGeneralFilter copy() {
            return new StatutGeneralFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter codeInterne;

    private StringFilter designation;

    private TypePrixFilter typePrix;

    private BigDecimalFilter prixVente;

    private BigDecimalFilter tauxRedevanceApplicable;

    private StatutGeneralFilter statut;

    private InstantFilter dateCreation;

    private LongFilter boutiqueId;

    private LongFilter groupeArticleId;

    private LongFilter familleArticleId;

    private LongFilter sousFamilleArticleId;

    private LongFilter uniteMesureId;

    private Boolean distinct;

    public ProduitCriteria() {}

    public ProduitCriteria(ProduitCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.codeInterne = other.optionalCodeInterne().map(StringFilter::copy).orElse(null);
        this.designation = other.optionalDesignation().map(StringFilter::copy).orElse(null);
        this.typePrix = other.optionalTypePrix().map(TypePrixFilter::copy).orElse(null);
        this.prixVente = other.optionalPrixVente().map(BigDecimalFilter::copy).orElse(null);
        this.tauxRedevanceApplicable = other.optionalTauxRedevanceApplicable().map(BigDecimalFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutGeneralFilter::copy).orElse(null);
        this.dateCreation = other.optionalDateCreation().map(InstantFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.groupeArticleId = other.optionalGroupeArticleId().map(LongFilter::copy).orElse(null);
        this.familleArticleId = other.optionalFamilleArticleId().map(LongFilter::copy).orElse(null);
        this.sousFamilleArticleId = other.optionalSousFamilleArticleId().map(LongFilter::copy).orElse(null);
        this.uniteMesureId = other.optionalUniteMesureId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProduitCriteria copy() {
        return new ProduitCriteria(this);
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

    public StringFilter getCodeInterne() {
        return codeInterne;
    }

    public Optional<StringFilter> optionalCodeInterne() {
        return Optional.ofNullable(codeInterne);
    }

    public StringFilter codeInterne() {
        if (codeInterne == null) {
            setCodeInterne(new StringFilter());
        }
        return codeInterne;
    }

    public void setCodeInterne(StringFilter codeInterne) {
        this.codeInterne = codeInterne;
    }

    public StringFilter getDesignation() {
        return designation;
    }

    public Optional<StringFilter> optionalDesignation() {
        return Optional.ofNullable(designation);
    }

    public StringFilter designation() {
        if (designation == null) {
            setDesignation(new StringFilter());
        }
        return designation;
    }

    public void setDesignation(StringFilter designation) {
        this.designation = designation;
    }

    public TypePrixFilter getTypePrix() {
        return typePrix;
    }

    public Optional<TypePrixFilter> optionalTypePrix() {
        return Optional.ofNullable(typePrix);
    }

    public TypePrixFilter typePrix() {
        if (typePrix == null) {
            setTypePrix(new TypePrixFilter());
        }
        return typePrix;
    }

    public void setTypePrix(TypePrixFilter typePrix) {
        this.typePrix = typePrix;
    }

    public BigDecimalFilter getPrixVente() {
        return prixVente;
    }

    public Optional<BigDecimalFilter> optionalPrixVente() {
        return Optional.ofNullable(prixVente);
    }

    public BigDecimalFilter prixVente() {
        if (prixVente == null) {
            setPrixVente(new BigDecimalFilter());
        }
        return prixVente;
    }

    public void setPrixVente(BigDecimalFilter prixVente) {
        this.prixVente = prixVente;
    }

    public BigDecimalFilter getTauxRedevanceApplicable() {
        return tauxRedevanceApplicable;
    }

    public Optional<BigDecimalFilter> optionalTauxRedevanceApplicable() {
        return Optional.ofNullable(tauxRedevanceApplicable);
    }

    public BigDecimalFilter tauxRedevanceApplicable() {
        if (tauxRedevanceApplicable == null) {
            setTauxRedevanceApplicable(new BigDecimalFilter());
        }
        return tauxRedevanceApplicable;
    }

    public void setTauxRedevanceApplicable(BigDecimalFilter tauxRedevanceApplicable) {
        this.tauxRedevanceApplicable = tauxRedevanceApplicable;
    }

    public StatutGeneralFilter getStatut() {
        return statut;
    }

    public Optional<StatutGeneralFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutGeneralFilter statut() {
        if (statut == null) {
            setStatut(new StatutGeneralFilter());
        }
        return statut;
    }

    public void setStatut(StatutGeneralFilter statut) {
        this.statut = statut;
    }

    public InstantFilter getDateCreation() {
        return dateCreation;
    }

    public Optional<InstantFilter> optionalDateCreation() {
        return Optional.ofNullable(dateCreation);
    }

    public InstantFilter dateCreation() {
        if (dateCreation == null) {
            setDateCreation(new InstantFilter());
        }
        return dateCreation;
    }

    public void setDateCreation(InstantFilter dateCreation) {
        this.dateCreation = dateCreation;
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

    public LongFilter getGroupeArticleId() {
        return groupeArticleId;
    }

    public Optional<LongFilter> optionalGroupeArticleId() {
        return Optional.ofNullable(groupeArticleId);
    }

    public LongFilter groupeArticleId() {
        if (groupeArticleId == null) {
            setGroupeArticleId(new LongFilter());
        }
        return groupeArticleId;
    }

    public void setGroupeArticleId(LongFilter groupeArticleId) {
        this.groupeArticleId = groupeArticleId;
    }

    public LongFilter getFamilleArticleId() {
        return familleArticleId;
    }

    public Optional<LongFilter> optionalFamilleArticleId() {
        return Optional.ofNullable(familleArticleId);
    }

    public LongFilter familleArticleId() {
        if (familleArticleId == null) {
            setFamilleArticleId(new LongFilter());
        }
        return familleArticleId;
    }

    public void setFamilleArticleId(LongFilter familleArticleId) {
        this.familleArticleId = familleArticleId;
    }

    public LongFilter getSousFamilleArticleId() {
        return sousFamilleArticleId;
    }

    public Optional<LongFilter> optionalSousFamilleArticleId() {
        return Optional.ofNullable(sousFamilleArticleId);
    }

    public LongFilter sousFamilleArticleId() {
        if (sousFamilleArticleId == null) {
            setSousFamilleArticleId(new LongFilter());
        }
        return sousFamilleArticleId;
    }

    public void setSousFamilleArticleId(LongFilter sousFamilleArticleId) {
        this.sousFamilleArticleId = sousFamilleArticleId;
    }

    public LongFilter getUniteMesureId() {
        return uniteMesureId;
    }

    public Optional<LongFilter> optionalUniteMesureId() {
        return Optional.ofNullable(uniteMesureId);
    }

    public LongFilter uniteMesureId() {
        if (uniteMesureId == null) {
            setUniteMesureId(new LongFilter());
        }
        return uniteMesureId;
    }

    public void setUniteMesureId(LongFilter uniteMesureId) {
        this.uniteMesureId = uniteMesureId;
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
        final ProduitCriteria that = (ProduitCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(codeInterne, that.codeInterne) &&
            Objects.equals(designation, that.designation) &&
            Objects.equals(typePrix, that.typePrix) &&
            Objects.equals(prixVente, that.prixVente) &&
            Objects.equals(tauxRedevanceApplicable, that.tauxRedevanceApplicable) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(dateCreation, that.dateCreation) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(groupeArticleId, that.groupeArticleId) &&
            Objects.equals(familleArticleId, that.familleArticleId) &&
            Objects.equals(sousFamilleArticleId, that.sousFamilleArticleId) &&
            Objects.equals(uniteMesureId, that.uniteMesureId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            codeInterne,
            designation,
            typePrix,
            prixVente,
            tauxRedevanceApplicable,
            statut,
            dateCreation,
            boutiqueId,
            groupeArticleId,
            familleArticleId,
            sousFamilleArticleId,
            uniteMesureId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProduitCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCodeInterne().map(f -> "codeInterne=" + f + ", ").orElse("") +
            optionalDesignation().map(f -> "designation=" + f + ", ").orElse("") +
            optionalTypePrix().map(f -> "typePrix=" + f + ", ").orElse("") +
            optionalPrixVente().map(f -> "prixVente=" + f + ", ").orElse("") +
            optionalTauxRedevanceApplicable().map(f -> "tauxRedevanceApplicable=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalDateCreation().map(f -> "dateCreation=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalGroupeArticleId().map(f -> "groupeArticleId=" + f + ", ").orElse("") +
            optionalFamilleArticleId().map(f -> "familleArticleId=" + f + ", ").orElse("") +
            optionalSousFamilleArticleId().map(f -> "sousFamilleArticleId=" + f + ", ").orElse("") +
            optionalUniteMesureId().map(f -> "uniteMesureId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
