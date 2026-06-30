package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.TypeOperationCorrective;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.OperationCorrectiveVente} entity. This class is used
 * in {@link com.adm.supervision.web.rest.OperationCorrectiveVenteResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /operation-corrective-ventes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OperationCorrectiveVenteCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeOperationCorrective
     */
    public static class TypeOperationCorrectiveFilter extends Filter<TypeOperationCorrective> {

        public TypeOperationCorrectiveFilter() {}

        public TypeOperationCorrectiveFilter(TypeOperationCorrectiveFilter filter) {
            super(filter);
        }

        @Override
        public TypeOperationCorrectiveFilter copy() {
            return new TypeOperationCorrectiveFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private TypeOperationCorrectiveFilter typeOperation;

    private StringFilter motif;

    private BigDecimalFilter montantImpact;

    private InstantFilter dateOperation;

    private LongFilter venteId;

    private LongFilter utilisateurId;

    private Boolean distinct;

    public OperationCorrectiveVenteCriteria() {}

    public OperationCorrectiveVenteCriteria(OperationCorrectiveVenteCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.typeOperation = other.optionalTypeOperation().map(TypeOperationCorrectiveFilter::copy).orElse(null);
        this.motif = other.optionalMotif().map(StringFilter::copy).orElse(null);
        this.montantImpact = other.optionalMontantImpact().map(BigDecimalFilter::copy).orElse(null);
        this.dateOperation = other.optionalDateOperation().map(InstantFilter::copy).orElse(null);
        this.venteId = other.optionalVenteId().map(LongFilter::copy).orElse(null);
        this.utilisateurId = other.optionalUtilisateurId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public OperationCorrectiveVenteCriteria copy() {
        return new OperationCorrectiveVenteCriteria(this);
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

    public TypeOperationCorrectiveFilter getTypeOperation() {
        return typeOperation;
    }

    public Optional<TypeOperationCorrectiveFilter> optionalTypeOperation() {
        return Optional.ofNullable(typeOperation);
    }

    public TypeOperationCorrectiveFilter typeOperation() {
        if (typeOperation == null) {
            setTypeOperation(new TypeOperationCorrectiveFilter());
        }
        return typeOperation;
    }

    public void setTypeOperation(TypeOperationCorrectiveFilter typeOperation) {
        this.typeOperation = typeOperation;
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

    public BigDecimalFilter getMontantImpact() {
        return montantImpact;
    }

    public Optional<BigDecimalFilter> optionalMontantImpact() {
        return Optional.ofNullable(montantImpact);
    }

    public BigDecimalFilter montantImpact() {
        if (montantImpact == null) {
            setMontantImpact(new BigDecimalFilter());
        }
        return montantImpact;
    }

    public void setMontantImpact(BigDecimalFilter montantImpact) {
        this.montantImpact = montantImpact;
    }

    public InstantFilter getDateOperation() {
        return dateOperation;
    }

    public Optional<InstantFilter> optionalDateOperation() {
        return Optional.ofNullable(dateOperation);
    }

    public InstantFilter dateOperation() {
        if (dateOperation == null) {
            setDateOperation(new InstantFilter());
        }
        return dateOperation;
    }

    public void setDateOperation(InstantFilter dateOperation) {
        this.dateOperation = dateOperation;
    }

    public LongFilter getVenteId() {
        return venteId;
    }

    public Optional<LongFilter> optionalVenteId() {
        return Optional.ofNullable(venteId);
    }

    public LongFilter venteId() {
        if (venteId == null) {
            setVenteId(new LongFilter());
        }
        return venteId;
    }

    public void setVenteId(LongFilter venteId) {
        this.venteId = venteId;
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
        final OperationCorrectiveVenteCriteria that = (OperationCorrectiveVenteCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(typeOperation, that.typeOperation) &&
            Objects.equals(motif, that.motif) &&
            Objects.equals(montantImpact, that.montantImpact) &&
            Objects.equals(dateOperation, that.dateOperation) &&
            Objects.equals(venteId, that.venteId) &&
            Objects.equals(utilisateurId, that.utilisateurId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, typeOperation, motif, montantImpact, dateOperation, venteId, utilisateurId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OperationCorrectiveVenteCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTypeOperation().map(f -> "typeOperation=" + f + ", ").orElse("") +
            optionalMotif().map(f -> "motif=" + f + ", ").orElse("") +
            optionalMontantImpact().map(f -> "montantImpact=" + f + ", ").orElse("") +
            optionalDateOperation().map(f -> "dateOperation=" + f + ", ").orElse("") +
            optionalVenteId().map(f -> "venteId=" + f + ", ").orElse("") +
            optionalUtilisateurId().map(f -> "utilisateurId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
