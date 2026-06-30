package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.TypeActionAudit;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A JournalAudit.
 */
@Entity
@Table(name = "journal_audit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class JournalAudit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_action", nullable = false)
    private TypeActionAudit typeAction;

    @Size(max = 100)
    @Column(name = "entite_concernee", length = 100)
    private String entiteConcernee;

    @Size(max = 100)
    @Column(name = "identifiant_entite", length = 100)
    private String identifiantEntite;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    private String description;

    @Size(max = 80)
    @Column(name = "adresse_ip", length = 80)
    private String adresseIp;

    @NotNull
    @Column(name = "date_action", nullable = false)
    private Instant dateAction;

    @ManyToOne(fetch = FetchType.LAZY)
    private Boutique boutique;

    @ManyToOne(fetch = FetchType.LAZY)
    private User utilisateur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public JournalAudit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeActionAudit getTypeAction() {
        return this.typeAction;
    }

    public JournalAudit typeAction(TypeActionAudit typeAction) {
        this.setTypeAction(typeAction);
        return this;
    }

    public void setTypeAction(TypeActionAudit typeAction) {
        this.typeAction = typeAction;
    }

    public String getEntiteConcernee() {
        return this.entiteConcernee;
    }

    public JournalAudit entiteConcernee(String entiteConcernee) {
        this.setEntiteConcernee(entiteConcernee);
        return this;
    }

    public void setEntiteConcernee(String entiteConcernee) {
        this.entiteConcernee = entiteConcernee;
    }

    public String getIdentifiantEntite() {
        return this.identifiantEntite;
    }

    public JournalAudit identifiantEntite(String identifiantEntite) {
        this.setIdentifiantEntite(identifiantEntite);
        return this;
    }

    public void setIdentifiantEntite(String identifiantEntite) {
        this.identifiantEntite = identifiantEntite;
    }

    public String getDescription() {
        return this.description;
    }

    public JournalAudit description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdresseIp() {
        return this.adresseIp;
    }

    public JournalAudit adresseIp(String adresseIp) {
        this.setAdresseIp(adresseIp);
        return this;
    }

    public void setAdresseIp(String adresseIp) {
        this.adresseIp = adresseIp;
    }

    public Instant getDateAction() {
        return this.dateAction;
    }

    public JournalAudit dateAction(Instant dateAction) {
        this.setDateAction(dateAction);
        return this;
    }

    public void setDateAction(Instant dateAction) {
        this.dateAction = dateAction;
    }

    public Boutique getBoutique() {
        return this.boutique;
    }

    public void setBoutique(Boutique boutique) {
        this.boutique = boutique;
    }

    public JournalAudit boutique(Boutique boutique) {
        this.setBoutique(boutique);
        return this;
    }

    public User getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(User user) {
        this.utilisateur = user;
    }

    public JournalAudit utilisateur(User user) {
        this.setUtilisateur(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JournalAudit)) {
            return false;
        }
        return getId() != null && getId().equals(((JournalAudit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JournalAudit{" +
            "id=" + getId() +
            ", typeAction='" + getTypeAction() + "'" +
            ", entiteConcernee='" + getEntiteConcernee() + "'" +
            ", identifiantEntite='" + getIdentifiantEntite() + "'" +
            ", description='" + getDescription() + "'" +
            ", adresseIp='" + getAdresseIp() + "'" +
            ", dateAction='" + getDateAction() + "'" +
            "}";
    }
}
