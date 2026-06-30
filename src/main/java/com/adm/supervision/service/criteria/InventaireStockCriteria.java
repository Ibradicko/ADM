package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.StatutInventaire;
import com.adm.supervision.domain.enumeration.TypeInventaire;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.InventaireStock} entity. This class is used
 * in {@link com.adm.supervision.web.rest.InventaireStockResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /inventaire-stocks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventaireStockCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeInventaire
     */
    public static class TypeInventaireFilter extends Filter<TypeInventaire> {

        public TypeInventaireFilter() {}

        public TypeInventaireFilter(TypeInventaireFilter filter) {
            super(filter);
        }

        @Override
        public TypeInventaireFilter copy() {
            return new TypeInventaireFilter(this);
        }
    }

    /**
     * Class for filtering StatutInventaire
     */
    public static class StatutInventaireFilter extends Filter<StatutInventaire> {

        public StatutInventaireFilter() {}

        public StatutInventaireFilter(StatutInventaireFilter filter) {
            super(filter);
        }

        @Override
        public StatutInventaireFilter copy() {
            return new StatutInventaireFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private TypeInventaireFilter typeInventaire;

    private StatutInventaireFilter statut;

    private InstantFilter dateDebut;

    private InstantFilter dateFin;

    private LongFilter boutiqueId;

    private LongFilter depotId;

    private LongFilter utilisateurId;

    private Boolean distinct;

    public InventaireStockCriteria() {}

    public InventaireStockCriteria(InventaireStockCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.typeInventaire = other.optionalTypeInventaire().map(TypeInventaireFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutInventaireFilter::copy).orElse(null);
        this.dateDebut = other.optionalDateDebut().map(InstantFilter::copy).orElse(null);
        this.dateFin = other.optionalDateFin().map(InstantFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.depotId = other.optionalDepotId().map(LongFilter::copy).orElse(null);
        this.utilisateurId = other.optionalUtilisateurId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public InventaireStockCriteria copy() {
        return new InventaireStockCriteria(this);
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

    public TypeInventaireFilter getTypeInventaire() {
        return typeInventaire;
    }

    public Optional<TypeInventaireFilter> optionalTypeInventaire() {
        return Optional.ofNullable(typeInventaire);
    }

    public TypeInventaireFilter typeInventaire() {
        if (typeInventaire == null) {
            setTypeInventaire(new TypeInventaireFilter());
        }
        return typeInventaire;
    }

    public void setTypeInventaire(TypeInventaireFilter typeInventaire) {
        this.typeInventaire = typeInventaire;
    }

    public StatutInventaireFilter getStatut() {
        return statut;
    }

    public Optional<StatutInventaireFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutInventaireFilter statut() {
        if (statut == null) {
            setStatut(new StatutInventaireFilter());
        }
        return statut;
    }

    public void setStatut(StatutInventaireFilter statut) {
        this.statut = statut;
    }

    public InstantFilter getDateDebut() {
        return dateDebut;
    }

    public Optional<InstantFilter> optionalDateDebut() {
        return Optional.ofNullable(dateDebut);
    }

    public InstantFilter dateDebut() {
        if (dateDebut == null) {
            setDateDebut(new InstantFilter());
        }
        return dateDebut;
    }

    public void setDateDebut(InstantFilter dateDebut) {
        this.dateDebut = dateDebut;
    }

    public InstantFilter getDateFin() {
        return dateFin;
    }

    public Optional<InstantFilter> optionalDateFin() {
        return Optional.ofNullable(dateFin);
    }

    public InstantFilter dateFin() {
        if (dateFin == null) {
            setDateFin(new InstantFilter());
        }
        return dateFin;
    }

    public void setDateFin(InstantFilter dateFin) {
        this.dateFin = dateFin;
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

    public LongFilter getDepotId() {
        return depotId;
    }

    public Optional<LongFilter> optionalDepotId() {
        return Optional.ofNullable(depotId);
    }

    public LongFilter depotId() {
        if (depotId == null) {
            setDepotId(new LongFilter());
        }
        return depotId;
    }

    public void setDepotId(LongFilter depotId) {
        this.depotId = depotId;
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
        final InventaireStockCriteria that = (InventaireStockCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(typeInventaire, that.typeInventaire) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(dateDebut, that.dateDebut) &&
            Objects.equals(dateFin, that.dateFin) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(depotId, that.depotId) &&
            Objects.equals(utilisateurId, that.utilisateurId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, typeInventaire, statut, dateDebut, dateFin, boutiqueId, depotId, utilisateurId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventaireStockCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalTypeInventaire().map(f -> "typeInventaire=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalDateDebut().map(f -> "dateDebut=" + f + ", ").orElse("") +
            optionalDateFin().map(f -> "dateFin=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalDepotId().map(f -> "depotId=" + f + ", ").orElse("") +
            optionalUtilisateurId().map(f -> "utilisateurId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
