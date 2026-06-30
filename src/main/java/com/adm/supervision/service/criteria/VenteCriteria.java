package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.StatutVente;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.Vente} entity. This class is used
 * in {@link com.adm.supervision.web.rest.VenteResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /ventes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VenteCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatutVente
     */
    public static class StatutVenteFilter extends Filter<StatutVente> {

        public StatutVenteFilter() {}

        public StatutVenteFilter(StatutVenteFilter filter) {
            super(filter);
        }

        @Override
        public StatutVenteFilter copy() {
            return new StatutVenteFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter numeroTicket;

    private InstantFilter dateHeure;

    private StatutVenteFilter statut;

    private StringFilter referencePassager;

    private StringFilter referenceCarteEmbarquement;

    private BigDecimalFilter montantBrut;

    private BigDecimalFilter montantRemise;

    private BigDecimalFilter montantNet;

    private StringFilter commentaire;

    private LongFilter boutiqueId;

    private LongFilter locataireId;

    private LongFilter vendeurId;

    private Boolean distinct;

    public VenteCriteria() {}

    public VenteCriteria(VenteCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.numeroTicket = other.optionalNumeroTicket().map(StringFilter::copy).orElse(null);
        this.dateHeure = other.optionalDateHeure().map(InstantFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutVenteFilter::copy).orElse(null);
        this.referencePassager = other.optionalReferencePassager().map(StringFilter::copy).orElse(null);
        this.referenceCarteEmbarquement = other.optionalReferenceCarteEmbarquement().map(StringFilter::copy).orElse(null);
        this.montantBrut = other.optionalMontantBrut().map(BigDecimalFilter::copy).orElse(null);
        this.montantRemise = other.optionalMontantRemise().map(BigDecimalFilter::copy).orElse(null);
        this.montantNet = other.optionalMontantNet().map(BigDecimalFilter::copy).orElse(null);
        this.commentaire = other.optionalCommentaire().map(StringFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.locataireId = other.optionalLocataireId().map(LongFilter::copy).orElse(null);
        this.vendeurId = other.optionalVendeurId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public VenteCriteria copy() {
        return new VenteCriteria(this);
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

    public StringFilter getNumeroTicket() {
        return numeroTicket;
    }

    public Optional<StringFilter> optionalNumeroTicket() {
        return Optional.ofNullable(numeroTicket);
    }

    public StringFilter numeroTicket() {
        if (numeroTicket == null) {
            setNumeroTicket(new StringFilter());
        }
        return numeroTicket;
    }

    public void setNumeroTicket(StringFilter numeroTicket) {
        this.numeroTicket = numeroTicket;
    }

    public InstantFilter getDateHeure() {
        return dateHeure;
    }

    public Optional<InstantFilter> optionalDateHeure() {
        return Optional.ofNullable(dateHeure);
    }

    public InstantFilter dateHeure() {
        if (dateHeure == null) {
            setDateHeure(new InstantFilter());
        }
        return dateHeure;
    }

    public void setDateHeure(InstantFilter dateHeure) {
        this.dateHeure = dateHeure;
    }

    public StatutVenteFilter getStatut() {
        return statut;
    }

    public Optional<StatutVenteFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutVenteFilter statut() {
        if (statut == null) {
            setStatut(new StatutVenteFilter());
        }
        return statut;
    }

    public void setStatut(StatutVenteFilter statut) {
        this.statut = statut;
    }

    public StringFilter getReferencePassager() {
        return referencePassager;
    }

    public Optional<StringFilter> optionalReferencePassager() {
        return Optional.ofNullable(referencePassager);
    }

    public StringFilter referencePassager() {
        if (referencePassager == null) {
            setReferencePassager(new StringFilter());
        }
        return referencePassager;
    }

    public void setReferencePassager(StringFilter referencePassager) {
        this.referencePassager = referencePassager;
    }

    public StringFilter getReferenceCarteEmbarquement() {
        return referenceCarteEmbarquement;
    }

    public Optional<StringFilter> optionalReferenceCarteEmbarquement() {
        return Optional.ofNullable(referenceCarteEmbarquement);
    }

    public StringFilter referenceCarteEmbarquement() {
        if (referenceCarteEmbarquement == null) {
            setReferenceCarteEmbarquement(new StringFilter());
        }
        return referenceCarteEmbarquement;
    }

    public void setReferenceCarteEmbarquement(StringFilter referenceCarteEmbarquement) {
        this.referenceCarteEmbarquement = referenceCarteEmbarquement;
    }

    public BigDecimalFilter getMontantBrut() {
        return montantBrut;
    }

    public Optional<BigDecimalFilter> optionalMontantBrut() {
        return Optional.ofNullable(montantBrut);
    }

    public BigDecimalFilter montantBrut() {
        if (montantBrut == null) {
            setMontantBrut(new BigDecimalFilter());
        }
        return montantBrut;
    }

    public void setMontantBrut(BigDecimalFilter montantBrut) {
        this.montantBrut = montantBrut;
    }

    public BigDecimalFilter getMontantRemise() {
        return montantRemise;
    }

    public Optional<BigDecimalFilter> optionalMontantRemise() {
        return Optional.ofNullable(montantRemise);
    }

    public BigDecimalFilter montantRemise() {
        if (montantRemise == null) {
            setMontantRemise(new BigDecimalFilter());
        }
        return montantRemise;
    }

    public void setMontantRemise(BigDecimalFilter montantRemise) {
        this.montantRemise = montantRemise;
    }

    public BigDecimalFilter getMontantNet() {
        return montantNet;
    }

    public Optional<BigDecimalFilter> optionalMontantNet() {
        return Optional.ofNullable(montantNet);
    }

    public BigDecimalFilter montantNet() {
        if (montantNet == null) {
            setMontantNet(new BigDecimalFilter());
        }
        return montantNet;
    }

    public void setMontantNet(BigDecimalFilter montantNet) {
        this.montantNet = montantNet;
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

    public LongFilter getLocataireId() {
        return locataireId;
    }

    public Optional<LongFilter> optionalLocataireId() {
        return Optional.ofNullable(locataireId);
    }

    public LongFilter locataireId() {
        if (locataireId == null) {
            setLocataireId(new LongFilter());
        }
        return locataireId;
    }

    public void setLocataireId(LongFilter locataireId) {
        this.locataireId = locataireId;
    }

    public LongFilter getVendeurId() {
        return vendeurId;
    }

    public Optional<LongFilter> optionalVendeurId() {
        return Optional.ofNullable(vendeurId);
    }

    public LongFilter vendeurId() {
        if (vendeurId == null) {
            setVendeurId(new LongFilter());
        }
        return vendeurId;
    }

    public void setVendeurId(LongFilter vendeurId) {
        this.vendeurId = vendeurId;
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
        final VenteCriteria that = (VenteCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(numeroTicket, that.numeroTicket) &&
            Objects.equals(dateHeure, that.dateHeure) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(referencePassager, that.referencePassager) &&
            Objects.equals(referenceCarteEmbarquement, that.referenceCarteEmbarquement) &&
            Objects.equals(montantBrut, that.montantBrut) &&
            Objects.equals(montantRemise, that.montantRemise) &&
            Objects.equals(montantNet, that.montantNet) &&
            Objects.equals(commentaire, that.commentaire) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(locataireId, that.locataireId) &&
            Objects.equals(vendeurId, that.vendeurId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            numeroTicket,
            dateHeure,
            statut,
            referencePassager,
            referenceCarteEmbarquement,
            montantBrut,
            montantRemise,
            montantNet,
            commentaire,
            boutiqueId,
            locataireId,
            vendeurId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VenteCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNumeroTicket().map(f -> "numeroTicket=" + f + ", ").orElse("") +
            optionalDateHeure().map(f -> "dateHeure=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalReferencePassager().map(f -> "referencePassager=" + f + ", ").orElse("") +
            optionalReferenceCarteEmbarquement().map(f -> "referenceCarteEmbarquement=" + f + ", ").orElse("") +
            optionalMontantBrut().map(f -> "montantBrut=" + f + ", ").orElse("") +
            optionalMontantRemise().map(f -> "montantRemise=" + f + ", ").orElse("") +
            optionalMontantNet().map(f -> "montantNet=" + f + ", ").orElse("") +
            optionalCommentaire().map(f -> "commentaire=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalLocataireId().map(f -> "locataireId=" + f + ", ").orElse("") +
            optionalVendeurId().map(f -> "vendeurId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
