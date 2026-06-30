package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.StatutInventaire;
import com.adm.supervision.domain.enumeration.TypeInventaire;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A InventaireStock.
 */
@Entity
@Table(name = "inventaire_stock")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventaireStock implements Serializable {

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
    @Column(name = "type_inventaire", nullable = false)
    private TypeInventaire typeInventaire;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutInventaire statut;

    @NotNull
    @Column(name = "date_debut", nullable = false)
    private Instant dateDebut;

    @Column(name = "date_fin")
    private Instant dateFin;

    @ManyToOne(optional = false)
    @NotNull
    private Boutique boutique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "boutique" }, allowSetters = true)
    private DepotStock depot;

    @ManyToOne(optional = false)
    @NotNull
    private User utilisateur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InventaireStock id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public InventaireStock reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public TypeInventaire getTypeInventaire() {
        return this.typeInventaire;
    }

    public InventaireStock typeInventaire(TypeInventaire typeInventaire) {
        this.setTypeInventaire(typeInventaire);
        return this;
    }

    public void setTypeInventaire(TypeInventaire typeInventaire) {
        this.typeInventaire = typeInventaire;
    }

    public StatutInventaire getStatut() {
        return this.statut;
    }

    public InventaireStock statut(StatutInventaire statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutInventaire statut) {
        this.statut = statut;
    }

    public Instant getDateDebut() {
        return this.dateDebut;
    }

    public InventaireStock dateDebut(Instant dateDebut) {
        this.setDateDebut(dateDebut);
        return this;
    }

    public void setDateDebut(Instant dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Instant getDateFin() {
        return this.dateFin;
    }

    public InventaireStock dateFin(Instant dateFin) {
        this.setDateFin(dateFin);
        return this;
    }

    public void setDateFin(Instant dateFin) {
        this.dateFin = dateFin;
    }

    public Boutique getBoutique() {
        return this.boutique;
    }

    public void setBoutique(Boutique boutique) {
        this.boutique = boutique;
    }

    public InventaireStock boutique(Boutique boutique) {
        this.setBoutique(boutique);
        return this;
    }

    public DepotStock getDepot() {
        return this.depot;
    }

    public void setDepot(DepotStock depotStock) {
        this.depot = depotStock;
    }

    public InventaireStock depot(DepotStock depotStock) {
        this.setDepot(depotStock);
        return this;
    }

    public User getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(User user) {
        this.utilisateur = user;
    }

    public InventaireStock utilisateur(User user) {
        this.setUtilisateur(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventaireStock)) {
            return false;
        }
        return getId() != null && getId().equals(((InventaireStock) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventaireStock{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", typeInventaire='" + getTypeInventaire() + "'" +
            ", statut='" + getStatut() + "'" +
            ", dateDebut='" + getDateDebut() + "'" +
            ", dateFin='" + getDateFin() + "'" +
            "}";
    }
}
