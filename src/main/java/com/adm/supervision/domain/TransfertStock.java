package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.StatutMouvementStock;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TransfertStock.
 */
@Entity
@Table(name = "transfert_stock")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransfertStock implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "reference", length = 80, nullable = false, unique = true)
    private String reference;

    @NotNull
    @Column(name = "date_transfert", nullable = false)
    private Instant dateTransfert;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutMouvementStock statut;

    @Size(max = 255)
    @Column(name = "motif", length = 255)
    private String motif;

    @ManyToOne(optional = false)
    @NotNull
    private Boutique boutiqueOrigine;

    @ManyToOne(optional = false)
    @NotNull
    private Boutique boutiqueDestination;

    @ManyToOne(optional = false)
    @NotNull
    private User utilisateur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TransfertStock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public TransfertStock reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getDateTransfert() {
        return this.dateTransfert;
    }

    public TransfertStock dateTransfert(Instant dateTransfert) {
        this.setDateTransfert(dateTransfert);
        return this;
    }

    public void setDateTransfert(Instant dateTransfert) {
        this.dateTransfert = dateTransfert;
    }

    public StatutMouvementStock getStatut() {
        return this.statut;
    }

    public TransfertStock statut(StatutMouvementStock statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutMouvementStock statut) {
        this.statut = statut;
    }

    public String getMotif() {
        return this.motif;
    }

    public TransfertStock motif(String motif) {
        this.setMotif(motif);
        return this;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Boutique getBoutiqueOrigine() {
        return this.boutiqueOrigine;
    }

    public void setBoutiqueOrigine(Boutique boutique) {
        this.boutiqueOrigine = boutique;
    }

    public TransfertStock boutiqueOrigine(Boutique boutique) {
        this.setBoutiqueOrigine(boutique);
        return this;
    }

    public Boutique getBoutiqueDestination() {
        return this.boutiqueDestination;
    }

    public void setBoutiqueDestination(Boutique boutique) {
        this.boutiqueDestination = boutique;
    }

    public TransfertStock boutiqueDestination(Boutique boutique) {
        this.setBoutiqueDestination(boutique);
        return this;
    }

    public User getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(User user) {
        this.utilisateur = user;
    }

    public TransfertStock utilisateur(User user) {
        this.setUtilisateur(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransfertStock)) {
            return false;
        }
        return getId() != null && getId().equals(((TransfertStock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransfertStock{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", dateTransfert='" + getDateTransfert() + "'" +
            ", statut='" + getStatut() + "'" +
            ", motif='" + getMotif() + "'" +
            "}";
    }
}
