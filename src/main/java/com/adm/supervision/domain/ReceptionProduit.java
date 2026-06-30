package com.adm.supervision.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ReceptionProduit.
 */
@Entity
@Table(name = "reception_produit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReceptionProduit implements Serializable {

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
    @Column(name = "date_reception", nullable = false)
    private Instant dateReception;

    @Size(max = 150)
    @Column(name = "fournisseur", length = 150)
    private String fournisseur;

    @Size(max = 255)
    @Column(name = "commentaire", length = 255)
    private String commentaire;

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

    public ReceptionProduit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public ReceptionProduit reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getDateReception() {
        return this.dateReception;
    }

    public ReceptionProduit dateReception(Instant dateReception) {
        this.setDateReception(dateReception);
        return this;
    }

    public void setDateReception(Instant dateReception) {
        this.dateReception = dateReception;
    }

    public String getFournisseur() {
        return this.fournisseur;
    }

    public ReceptionProduit fournisseur(String fournisseur) {
        this.setFournisseur(fournisseur);
        return this;
    }

    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }

    public String getCommentaire() {
        return this.commentaire;
    }

    public ReceptionProduit commentaire(String commentaire) {
        this.setCommentaire(commentaire);
        return this;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Boutique getBoutique() {
        return this.boutique;
    }

    public void setBoutique(Boutique boutique) {
        this.boutique = boutique;
    }

    public ReceptionProduit boutique(Boutique boutique) {
        this.setBoutique(boutique);
        return this;
    }

    public User getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(User user) {
        this.utilisateur = user;
    }

    public ReceptionProduit utilisateur(User user) {
        this.setUtilisateur(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReceptionProduit)) {
            return false;
        }
        return getId() != null && getId().equals(((ReceptionProduit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReceptionProduit{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", dateReception='" + getDateReception() + "'" +
            ", fournisseur='" + getFournisseur() + "'" +
            ", commentaire='" + getCommentaire() + "'" +
            "}";
    }
}
