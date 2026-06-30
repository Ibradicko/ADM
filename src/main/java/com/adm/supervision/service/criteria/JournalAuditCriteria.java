package com.adm.supervision.service.criteria;

import com.adm.supervision.domain.enumeration.TypeActionAudit;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.adm.supervision.domain.JournalAudit} entity. This class is used
 * in {@link com.adm.supervision.web.rest.JournalAuditResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /journal-audits?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class JournalAuditCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeActionAudit
     */
    public static class TypeActionAuditFilter extends Filter<TypeActionAudit> {

        public TypeActionAuditFilter() {}

        public TypeActionAuditFilter(TypeActionAuditFilter filter) {
            super(filter);
        }

        @Override
        public TypeActionAuditFilter copy() {
            return new TypeActionAuditFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private TypeActionAuditFilter typeAction;

    private StringFilter entiteConcernee;

    private StringFilter identifiantEntite;

    private StringFilter adresseIp;

    private InstantFilter dateAction;

    private LongFilter boutiqueId;

    private LongFilter utilisateurId;

    private Boolean distinct;

    public JournalAuditCriteria() {}

    public JournalAuditCriteria(JournalAuditCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.typeAction = other.optionalTypeAction().map(TypeActionAuditFilter::copy).orElse(null);
        this.entiteConcernee = other.optionalEntiteConcernee().map(StringFilter::copy).orElse(null);
        this.identifiantEntite = other.optionalIdentifiantEntite().map(StringFilter::copy).orElse(null);
        this.adresseIp = other.optionalAdresseIp().map(StringFilter::copy).orElse(null);
        this.dateAction = other.optionalDateAction().map(InstantFilter::copy).orElse(null);
        this.boutiqueId = other.optionalBoutiqueId().map(LongFilter::copy).orElse(null);
        this.utilisateurId = other.optionalUtilisateurId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public JournalAuditCriteria copy() {
        return new JournalAuditCriteria(this);
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

    public TypeActionAuditFilter getTypeAction() {
        return typeAction;
    }

    public Optional<TypeActionAuditFilter> optionalTypeAction() {
        return Optional.ofNullable(typeAction);
    }

    public TypeActionAuditFilter typeAction() {
        if (typeAction == null) {
            setTypeAction(new TypeActionAuditFilter());
        }
        return typeAction;
    }

    public void setTypeAction(TypeActionAuditFilter typeAction) {
        this.typeAction = typeAction;
    }

    public StringFilter getEntiteConcernee() {
        return entiteConcernee;
    }

    public Optional<StringFilter> optionalEntiteConcernee() {
        return Optional.ofNullable(entiteConcernee);
    }

    public StringFilter entiteConcernee() {
        if (entiteConcernee == null) {
            setEntiteConcernee(new StringFilter());
        }
        return entiteConcernee;
    }

    public void setEntiteConcernee(StringFilter entiteConcernee) {
        this.entiteConcernee = entiteConcernee;
    }

    public StringFilter getIdentifiantEntite() {
        return identifiantEntite;
    }

    public Optional<StringFilter> optionalIdentifiantEntite() {
        return Optional.ofNullable(identifiantEntite);
    }

    public StringFilter identifiantEntite() {
        if (identifiantEntite == null) {
            setIdentifiantEntite(new StringFilter());
        }
        return identifiantEntite;
    }

    public void setIdentifiantEntite(StringFilter identifiantEntite) {
        this.identifiantEntite = identifiantEntite;
    }

    public StringFilter getAdresseIp() {
        return adresseIp;
    }

    public Optional<StringFilter> optionalAdresseIp() {
        return Optional.ofNullable(adresseIp);
    }

    public StringFilter adresseIp() {
        if (adresseIp == null) {
            setAdresseIp(new StringFilter());
        }
        return adresseIp;
    }

    public void setAdresseIp(StringFilter adresseIp) {
        this.adresseIp = adresseIp;
    }

    public InstantFilter getDateAction() {
        return dateAction;
    }

    public Optional<InstantFilter> optionalDateAction() {
        return Optional.ofNullable(dateAction);
    }

    public InstantFilter dateAction() {
        if (dateAction == null) {
            setDateAction(new InstantFilter());
        }
        return dateAction;
    }

    public void setDateAction(InstantFilter dateAction) {
        this.dateAction = dateAction;
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
        final JournalAuditCriteria that = (JournalAuditCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(typeAction, that.typeAction) &&
            Objects.equals(entiteConcernee, that.entiteConcernee) &&
            Objects.equals(identifiantEntite, that.identifiantEntite) &&
            Objects.equals(adresseIp, that.adresseIp) &&
            Objects.equals(dateAction, that.dateAction) &&
            Objects.equals(boutiqueId, that.boutiqueId) &&
            Objects.equals(utilisateurId, that.utilisateurId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, typeAction, entiteConcernee, identifiantEntite, adresseIp, dateAction, boutiqueId, utilisateurId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JournalAuditCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTypeAction().map(f -> "typeAction=" + f + ", ").orElse("") +
            optionalEntiteConcernee().map(f -> "entiteConcernee=" + f + ", ").orElse("") +
            optionalIdentifiantEntite().map(f -> "identifiantEntite=" + f + ", ").orElse("") +
            optionalAdresseIp().map(f -> "adresseIp=" + f + ", ").orElse("") +
            optionalDateAction().map(f -> "dateAction=" + f + ", ").orElse("") +
            optionalBoutiqueId().map(f -> "boutiqueId=" + f + ", ").orElse("") +
            optionalUtilisateurId().map(f -> "utilisateurId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
