package com.adm.supervision.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A HistoriqueCodeBarres.
 */
@Entity
@Table(name = "historique_code_barres")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HistoriqueCodeBarres implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 80)
    @Column(name = "ancien_code", length = 80)
    private String ancienCode;

    @NotNull
    @Size(max = 80)
    @Column(name = "nouveau_code", length = 80, nullable = false)
    private String nouveauCode;

    @Size(max = 255)
    @Column(name = "motif", length = 255)
    private String motif;

    @NotNull
    @Column(name = "date_changement", nullable = false)
    private Instant dateChangement;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "boutique", "groupeArticle", "familleArticle", "sousFamilleArticle", "uniteMesure" },
        allowSetters = true
    )
    private Produit produit;

    @ManyToOne(fetch = FetchType.LAZY)
    private User utilisateur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HistoriqueCodeBarres id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAncienCode() {
        return this.ancienCode;
    }

    public HistoriqueCodeBarres ancienCode(String ancienCode) {
        this.setAncienCode(ancienCode);
        return this;
    }

    public void setAncienCode(String ancienCode) {
        this.ancienCode = ancienCode;
    }

    public String getNouveauCode() {
        return this.nouveauCode;
    }

    public HistoriqueCodeBarres nouveauCode(String nouveauCode) {
        this.setNouveauCode(nouveauCode);
        return this;
    }

    public void setNouveauCode(String nouveauCode) {
        this.nouveauCode = nouveauCode;
    }

    public String getMotif() {
        return this.motif;
    }

    public HistoriqueCodeBarres motif(String motif) {
        this.setMotif(motif);
        return this;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Instant getDateChangement() {
        return this.dateChangement;
    }

    public HistoriqueCodeBarres dateChangement(Instant dateChangement) {
        this.setDateChangement(dateChangement);
        return this;
    }

    public void setDateChangement(Instant dateChangement) {
        this.dateChangement = dateChangement;
    }

    public Produit getProduit() {
        return this.produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public HistoriqueCodeBarres produit(Produit produit) {
        this.setProduit(produit);
        return this;
    }

    public User getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(User user) {
        this.utilisateur = user;
    }

    public HistoriqueCodeBarres utilisateur(User user) {
        this.setUtilisateur(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistoriqueCodeBarres)) {
            return false;
        }
        return getId() != null && getId().equals(((HistoriqueCodeBarres) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistoriqueCodeBarres{" +
            "id=" + getId() +
            ", ancienCode='" + getAncienCode() + "'" +
            ", nouveauCode='" + getNouveauCode() + "'" +
            ", motif='" + getMotif() + "'" +
            ", dateChangement='" + getDateChangement() + "'" +
            "}";
    }
}
