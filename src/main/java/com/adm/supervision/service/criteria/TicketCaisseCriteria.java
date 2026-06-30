package com.adm.supervision.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.TicketCaisse} entity. This class is used
 * in {@link com.adm.supervision.web.rest.TicketCaisseResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ticket-caisses?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TicketCaisseCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter numero;

    private InstantFilter dateEmission;

    private IntegerFilter nombreImpressions;

    private LongFilter venteId;

    private LongFilter boutiqueId;

    private Boolean distinct;

    public TicketCaisseCriteria() {}

    public TicketCaisseCriteria(TicketCaisseCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.numero = other.optionalNumero().map(StringFilter::copy).orElse(null);
        this.dateEmission = other.optionalDateEmission().map(InstantFilter::copy).orElse(null);
        this.nombreImpressions = other.optionalNombreImpressions().map(IntegerFilter::copy).orElse(null);
        this.venteId = other.optionalVenteId().map(LongFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TicketCaisseCriteria copy() {
        return new TicketCaisseCriteria(this);
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

    public StringFilter getNumero() {
        return numero;
    }

    public Optional<StringFilter> optionalNumero() {
        return Optional.ofNullable(numero);
    }

    public StringFilter numero() {
        if (numero == null) {
            setNumero(new StringFilter());
        }
        return numero;
    }

    public void setNumero(StringFilter numero) {
        this.numero = numero;
    }

    public InstantFilter getDateEmission() {
        return dateEmission;
    }

    public Optional<InstantFilter> optionalDateEmission() {
        return Optional.ofNullable(dateEmission);
    }

    public InstantFilter dateEmission() {
        if (dateEmission == null) {
            setDateEmission(new InstantFilter());
        }
        return dateEmission;
    }

    public void setDateEmission(InstantFilter dateEmission) {
        this.dateEmission = dateEmission;
    }

    public IntegerFilter getNombreImpressions() {
        return nombreImpressions;
    }

    public Optional<IntegerFilter> optionalNombreImpressions() {
        return Optional.ofNullable(nombreImpressions);
    }

    public IntegerFilter nombreImpressions() {
        if (nombreImpressions == null) {
            setNombreImpressions(new IntegerFilter());
        }
        return nombreImpressions;
    }

    public void setNombreImpressions(IntegerFilter nombreImpressions) {
        this.nombreImpressions = nombreImpressions;
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
        final TicketCaisseCriteria that = (TicketCaisseCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(numero, that.numero) &&
            Objects.equals(dateEmission, that.dateEmission) &&
            Objects.equals(nombreImpressions, that.nombreImpressions) &&
            Objects.equals(venteId, that.venteId) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numero, dateEmission, nombreImpressions, venteId, boutiqueId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TicketCaisseCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNumero().map(f -> "numero=" + f + ", ").orElse("") +
            optionalDateEmission().map(f -> "dateEmission=" + f + ", ").orElse("") +
            optionalNombreImpressions().map(f -> "nombreImpressions=" + f + ", ").orElse("") +
            optionalVenteId().map(f -> "venteId=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
