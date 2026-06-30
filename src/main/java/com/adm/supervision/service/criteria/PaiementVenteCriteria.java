package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.StatutPaiement;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.PaiementVente} entity. This class is used
 * in {@link com.adm.supervision.web.rest.PaiementVenteResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /paiement-ventes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaiementVenteCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatutPaiement
     */
    public static class StatutPaiementFilter extends Filter<StatutPaiement> {

        public StatutPaiementFilter() {}

        public StatutPaiementFilter(StatutPaiementFilter filter) {
            super(filter);
        }

        @Override
        public StatutPaiementFilter copy() {
            return new StatutPaiementFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter montant;

    private StatutPaiementFilter statut;

    private StringFilter referencePaiement;

    private InstantFilter datePaiement;

    private LongFilter venteId;

    private LongFilter modePaiementId;

    private LongFilter boutiqueId;

    private Boolean distinct;

    public PaiementVenteCriteria() {}

    public PaiementVenteCriteria(PaiementVenteCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.montant = other.optionalMontant().map(BigDecimalFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutPaiementFilter::copy).orElse(null);
        this.referencePaiement = other.optionalReferencePaiement().map(StringFilter::copy).orElse(null);
        this.datePaiement = other.optionalDatePaiement().map(InstantFilter::copy).orElse(null);
        this.venteId = other.optionalVenteId().map(LongFilter::copy).orElse(null);
        this.modePaiementId = other.optionalModePaiementId().map(LongFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PaiementVenteCriteria copy() {
        return new PaiementVenteCriteria(this);
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

    public StatutPaiementFilter getStatut() {
        return statut;
    }

    public Optional<StatutPaiementFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutPaiementFilter statut() {
        if (statut == null) {
            setStatut(new StatutPaiementFilter());
        }
        return statut;
    }

    public void setStatut(StatutPaiementFilter statut) {
        this.statut = statut;
    }

    public StringFilter getReferencePaiement() {
        return referencePaiement;
    }

    public Optional<StringFilter> optionalReferencePaiement() {
        return Optional.ofNullable(referencePaiement);
    }

    public StringFilter referencePaiement() {
        if (referencePaiement == null) {
            setReferencePaiement(new StringFilter());
        }
        return referencePaiement;
    }

    public void setReferencePaiement(StringFilter referencePaiement) {
        this.referencePaiement = referencePaiement;
    }

    public InstantFilter getDatePaiement() {
        return datePaiement;
    }

    public Optional<InstantFilter> optionalDatePaiement() {
        return Optional.ofNullable(datePaiement);
    }

    public InstantFilter datePaiement() {
        if (datePaiement == null) {
            setDatePaiement(new InstantFilter());
        }
        return datePaiement;
    }

    public void setDatePaiement(InstantFilter datePaiement) {
        this.datePaiement = datePaiement;
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

    public LongFilter getModePaiementId() {
        return modePaiementId;
    }

    public Optional<LongFilter> optionalModePaiementId() {
        return Optional.ofNullable(modePaiementId);
    }

    public LongFilter modePaiementId() {
        if (modePaiementId == null) {
            setModePaiementId(new LongFilter());
        }
        return modePaiementId;
    }

    public void setModePaiementId(LongFilter modePaiementId) {
        this.modePaiementId = modePaiementId;
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
        final PaiementVenteCriteria that = (PaiementVenteCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(montant, that.montant) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(referencePaiement, that.referencePaiement) &&
            Objects.equals(datePaiement, that.datePaiement) &&
            Objects.equals(venteId, that.venteId) &&
            Objects.equals(modePaiementId, that.modePaiementId) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, montant, statut, referencePaiement, datePaiement, venteId, modePaiementId, boutiqueId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaiementVenteCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalMontant().map(f -> "montant=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalReferencePaiement().map(f -> "referencePaiement=" + f + ", ").orElse("") +
            optionalDatePaiement().map(f -> "datePaiement=" + f + ", ").orElse("") +
            optionalVenteId().map(f -> "venteId=" + f + ", ").orElse("") +
            optionalModePaiementId().map(f -> "modePaiementId=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
