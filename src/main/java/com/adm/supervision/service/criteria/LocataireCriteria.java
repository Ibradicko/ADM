package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.TypeLocataire;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.Locataire} entity. This class is used
 * in {@link com.adm.supervision.web.rest.LocataireResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /locataires?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LocataireCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeLocataire
     */
    public static class TypeLocataireFilter extends Filter<TypeLocataire> {

        public TypeLocataireFilter() {}

        public TypeLocataireFilter(TypeLocataireFilter filter) {
            super(filter);
        }

        @Override
        public TypeLocataireFilter copy() {
            return new TypeLocataireFilter(this);
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

    private TypeLocataireFilter typeLocataire;

    private StringFilter numeroIdentification;

    private StringFilter telephone;

    private StringFilter email;

    private StringFilter adresse;

    private StatutGeneralFilter statut;

    private InstantFilter dateCreation;

    private Boolean distinct;

    public LocataireCriteria() {}

    public LocataireCriteria(LocataireCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.nom = other.optionalNom().map(StringFilter::copy).orElse(null);
        this.typeLocataire = other.optionalTypeLocataire().map(TypeLocataireFilter::copy).orElse(null);
        this.numeroIdentification = other.optionalNumeroIdentification().map(StringFilter::copy).orElse(null);
        this.telephone = other.optionalTelephone().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.adresse = other.optionalAdresse().map(StringFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutGeneralFilter::copy).orElse(null);
        this.dateCreation = other.optionalDateCreation().map(InstantFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public LocataireCriteria copy() {
        return new LocataireCriteria(this);
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

    public TypeLocataireFilter getTypeLocataire() {
        return typeLocataire;
    }

    public Optional<TypeLocataireFilter> optionalTypeLocataire() {
        return Optional.ofNullable(typeLocataire);
    }

    public TypeLocataireFilter typeLocataire() {
        if (typeLocataire == null) {
            setTypeLocataire(new TypeLocataireFilter());
        }
        return typeLocataire;
    }

    public void setTypeLocataire(TypeLocataireFilter typeLocataire) {
        this.typeLocataire = typeLocataire;
    }

    public StringFilter getNumeroIdentification() {
        return numeroIdentification;
    }

    public Optional<StringFilter> optionalNumeroIdentification() {
        return Optional.ofNullable(numeroIdentification);
    }

    public StringFilter numeroIdentification() {
        if (numeroIdentification == null) {
            setNumeroIdentification(new StringFilter());
        }
        return numeroIdentification;
    }

    public void setNumeroIdentification(StringFilter numeroIdentification) {
        this.numeroIdentification = numeroIdentification;
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

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getAdresse() {
        return adresse;
    }

    public Optional<StringFilter> optionalAdresse() {
        return Optional.ofNullable(adresse);
    }

    public StringFilter adresse() {
        if (adresse == null) {
            setAdresse(new StringFilter());
        }
        return adresse;
    }

    public void setAdresse(StringFilter adresse) {
        this.adresse = adresse;
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
        final LocataireCriteria that = (LocataireCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(typeLocataire, that.typeLocataire) &&
            Objects.equals(numeroIdentification, that.numeroIdentification) &&
            Objects.equals(telephone, that.telephone) &&
            Objects.equals(email, that.email) &&
            Objects.equals(adresse, that.adresse) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(dateCreation, that.dateCreation) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, nom, typeLocataire, numeroIdentification, telephone, email, adresse, statut, dateCreation, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LocataireCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalNom().map(f -> "nom=" + f + ", ").orElse("") +
            optionalTypeLocataire().map(f -> "typeLocataire=" + f + ", ").orElse("") +
            optionalNumeroIdentification().map(f -> "numeroIdentification=" + f + ", ").orElse("") +
            optionalTelephone().map(f -> "telephone=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalAdresse().map(f -> "adresse=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalDateCreation().map(f -> "dateCreation=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
