package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.StatutVente;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Vente.
 */
@Entity
@Table(name = "vente")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Vente implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "numero_ticket", length = 80, nullable = false, unique = true)
    private String numeroTicket;

    @NotNull
    @Column(name = "date_heure", nullable = false)
    private Instant dateHeure;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutVente statut;

    @Size(max = 80)
    @Column(name = "reference_passager", length = 80)
    private String referencePassager;

    @Size(max = 80)
    @Column(name = "reference_carte_embarquement", length = 80)
    private String referenceCarteEmbarquement;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "montant_brut", precision = 21, scale = 2, nullable = false)
    private BigDecimal montantBrut;

    @DecimalMin(value = "0")
    @Column(name = "montant_remise", precision = 21, scale = 2)
    private BigDecimal montantRemise;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "montant_net", precision = 21, scale = 2, nullable = false)
    private BigDecimal montantNet;

    @Size(max = 255)
    @Column(name = "commentaire", length = 255)
    private String commentaire;

    @ManyToOne(optional = false)
    @NotNull
    private Boutique boutique;

    @ManyToOne(optional = false)
    @NotNull
    private Locataire locataire;

    @ManyToOne(optional = false)
    @NotNull
    private User vendeur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Vente id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroTicket() {
        return this.numeroTicket;
    }

    public Vente numeroTicket(String numeroTicket) {
        this.setNumeroTicket(numeroTicket);
        return this;
    }

    public void setNumeroTicket(String numeroTicket) {
        this.numeroTicket = numeroTicket;
    }

    public Instant getDateHeure() {
        return this.dateHeure;
    }

    public Vente dateHeure(Instant dateHeure) {
        this.setDateHeure(dateHeure);
        return this;
    }

    public void setDateHeure(Instant dateHeure) {
        this.dateHeure = dateHeure;
    }

    public StatutVente getStatut() {
        return this.statut;
    }

    public Vente statut(StatutVente statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutVente statut) {
        this.statut = statut;
    }

    public String getReferencePassager() {
        return this.referencePassager;
    }

    public Vente referencePassager(String referencePassager) {
        this.setReferencePassager(referencePassager);
        return this;
    }

    public void setReferencePassager(String referencePassager) {
        this.referencePassager = referencePassager;
    }

    public String getReferenceCarteEmbarquement() {
        return this.referenceCarteEmbarquement;
    }

    public Vente referenceCarteEmbarquement(String referenceCarteEmbarquement) {
        this.setReferenceCarteEmbarquement(referenceCarteEmbarquement);
        return this;
    }

    public void setReferenceCarteEmbarquement(String referenceCarteEmbarquement) {
        this.referenceCarteEmbarquement = referenceCarteEmbarquement;
    }

    public BigDecimal getMontantBrut() {
        return this.montantBrut;
    }

    public Vente montantBrut(BigDecimal montantBrut) {
        this.setMontantBrut(montantBrut);
        return this;
    }

    public void setMontantBrut(BigDecimal montantBrut) {
        this.montantBrut = montantBrut;
    }

    public BigDecimal getMontantRemise() {
        return this.montantRemise;
    }

    public Vente montantRemise(BigDecimal montantRemise) {
        this.setMontantRemise(montantRemise);
        return this;
    }

    public void setMontantRemise(BigDecimal montantRemise) {
        this.montantRemise = montantRemise;
    }

    public BigDecimal getMontantNet() {
        return this.montantNet;
    }

    public Vente montantNet(BigDecimal montantNet) {
        this.setMontantNet(montantNet);
        return this;
    }

    public void setMontantNet(BigDecimal montantNet) {
        this.montantNet = montantNet;
    }

    public String getCommentaire() {
        return this.commentaire;
    }

    public Vente commentaire(String commentaire) {
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

    public Vente boutique(Boutique boutique) {
        this.setBoutique(boutique);
        return this;
    }

    public Locataire getLocataire() {
        return this.locataire;
    }

    public void setLocataire(Locataire locataire) {
        this.locataire = locataire;
    }

    public Vente locataire(Locataire locataire) {
        this.setLocataire(locataire);
        return this;
    }

    public User getVendeur() {
        return this.vendeur;
    }

    public void setVendeur(User user) {
        this.vendeur = user;
    }

    public Vente vendeur(User user) {
        this.setVendeur(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vente)) {
            return false;
        }
        return getId() != null && getId().equals(((Vente) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Vente{" +
            "id=" + getId() +
            ", numeroTicket='" + getNumeroTicket() + "'" +
            ", dateHeure='" + getDateHeure() + "'" +
            ", statut='" + getStatut() + "'" +
            ", referencePassager='" + getReferencePassager() + "'" +
            ", referenceCarteEmbarquement='" + getReferenceCarteEmbarquement() + "'" +
            ", montantBrut=" + getMontantBrut() +
            ", montantRemise=" + getMontantRemise() +
            ", montantNet=" + getMontantNet() +
            ", commentaire='" + getCommentaire() + "'" +
            "}";
    }
}
