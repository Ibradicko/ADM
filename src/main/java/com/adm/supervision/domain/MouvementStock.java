package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.StatutMouvementStock;
import com.adm.supervision.domain.enumeration.TypeMouvementStock;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A MouvementStock.
 */
@Entity
@Table(name = "mouvement_stock")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MouvementStock implements Serializable {

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
    @Enumerated(EnumType.STRING)
    @Column(name = "type_mouvement", nullable = false)
    private TypeMouvementStock typeMouvement;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutMouvementStock statut;

    @NotNull
    @Column(name = "date_mouvement", nullable = false)
    private Instant dateMouvement;

    @Size(max = 255)
    @Column(name = "motif", length = 255)
    private String motif;

    @ManyToOne(optional = false)
    @NotNull
    private Boutique boutique;

    @ManyToOne(optional = false)
    @NotNull
    private User utilisateur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MouvementStock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public MouvementStock reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public TypeMouvementStock getTypeMouvement() {
        return this.typeMouvement;
    }

    public MouvementStock typeMouvement(TypeMouvementStock typeMouvement) {
        this.setTypeMouvement(typeMouvement);
        return this;
    }

    public void setTypeMouvement(TypeMouvementStock typeMouvement) {
        this.typeMouvement = typeMouvement;
    }

    public StatutMouvementStock getStatut() {
        return this.statut;
    }

    public MouvementStock statut(StatutMouvementStock statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutMouvementStock statut) {
        this.statut = statut;
    }

    public Instant getDateMouvement() {
        return this.dateMouvement;
    }

    public MouvementStock dateMouvement(Instant dateMouvement) {
        this.setDateMouvement(dateMouvement);
        return this;
    }

    public void setDateMouvement(Instant dateMouvement) {
        this.dateMouvement = dateMouvement;
    }

    public String getMotif() {
        return this.motif;
    }

    public MouvementStock motif(String motif) {
        this.setMotif(motif);
        return this;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Boutique getBoutique() {
        return this.boutique;
    }

    public void setBoutique(Boutique boutique) {
        this.boutique = boutique;
    }

    public MouvementStock boutique(Boutique boutique) {
        this.setBoutique(boutique);
        return this;
    }

    public User getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(User user) {
        this.utilisateur = user;
    }

    public MouvementStock utilisateur(User user) {
        this.setUtilisateur(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MouvementStock)) {
            return false;
        }
        return getId() != null && getId().equals(((MouvementStock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MouvementStock{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", typeMouvement='" + getTypeMouvement() + "'" +
            ", statut='" + getStatut() + "'" +
            ", dateMouvement='" + getDateMouvement() + "'" +
            ", motif='" + getMotif() + "'" +
            "}";
    }
}
