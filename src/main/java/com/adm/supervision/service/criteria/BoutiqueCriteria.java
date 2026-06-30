package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.TypeBoutique;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.Boutique} entity. This class is used
 * in {@link com.adm.supervision.web.rest.BoutiqueResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /boutiques?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BoutiqueCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeBoutique
     */
    public static class TypeBoutiqueFilter extends Filter<TypeBoutique> {

        public TypeBoutiqueFilter() {}

        public TypeBoutiqueFilter(TypeBoutiqueFilter filter) {
            super(filter);
        }

        @Override
        public TypeBoutiqueFilter copy() {
            return new TypeBoutiqueFilter(this);
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

    private StringFilter code;

    private StringFilter nom;

    private TypeBoutiqueFilter type;

    private StringFilter emplacement;

    private StringFilter telephone;

    private StatutGeneralFilter statut;

    private InstantFilter dateCreation;

    private Boolean distinct;

    public BoutiqueCriteria() {}

    public BoutiqueCriteria(BoutiqueCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.nom = other.optionalNom().map(StringFilter::copy).orElse(null);
        this.type = other.optionalType().map(TypeBoutiqueFilter::copy).orElse(null);
        this.emplacement = other.optionalEmplacement().map(StringFilter::copy).orElse(null);
        this.telephone = other.optionalTelephone().map(StringFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutGeneralFilter::copy).orElse(null);
        this.dateCreation = other.optionalDateCreation().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BoutiqueCriteria copy() {
        return new BoutiqueCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public StringFilter getNom() {
        return nom;
    }

    public Optional<StringFilter> optionalNom() {
        return Optional.ofNullable(nom);
    }

    public StringFilter nom() {
        if (nom == null) {
            setNom(new StringFilter());
        }
        return nom;
    }

    public void setNom(StringFilter nom) {
        this.nom = nom;
    }

    public TypeBoutiqueFilter getType() {
        return type;
    }

    public Optional<TypeBoutiqueFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public TypeBoutiqueFilter type() {
        if (type == null) {
            setType(new TypeBoutiqueFilter());
        }
        return type;
    }

    public void setType(TypeBoutiqueFilter type) {
        this.type = type;
    }

    public StringFilter getEmplacement() {
        return emplacement;
    }

    public Optional<StringFilter> optionalEmplacement() {
        return Optional.ofNullable(emplacement);
    }

    public StringFilter emplacement() {
        if (emplacement == null) {
            setEmplacement(new StringFilter());
        }
        return emplacement;
    }

    public void setEmplacement(StringFilter emplacement) {
        this.emplacement = emplacement;
    }

    public StringFilter getTelephone() {
        return telephone;
    }

    public Optional<StringFilter> optionalTelephone() {
        return Optional.ofNullable(telephone);
    }

    public StringFilter telephone() {
        if (telephone == null) {
            setTelephone(new StringFilter());
        }
        return telephone;
    }

    public void setTelephone(StringFilter telephone) {
        this.telephone = telephone;
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
        final BoutiqueCriteria that = (BoutiqueCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(type, that.type) &&
            Objects.equals(emplacement, that.emplacement) &&
            Objects.equals(telephone, that.telephone) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(dateCreation, that.dateCreation) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, nom, type, emplacement, telephone, statut, dateCreation, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BoutiqueCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalNom().map(f -> "nom=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalEmplacement().map(f -> "emplacement=" + f + ", ").orElse("") +
            optionalTelephone().map(f -> "telephone=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalDateCreation().map(f -> "dateCreation=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
