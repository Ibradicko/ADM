package com.adm.supervision.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PaiementRedevance.
 */
@Entity
@Table(name = "paiement_redevance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaiementRedevance implements Serializable {

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
    @DecimalMin(value = "0", inclusive = false)
    @Column(name = "montant", precision = 21, scale = 2, nullable = false)
    private BigDecimal montant;

    @NotNull
    @Column(name = "date_paiement", nullable = false)
    private LocalDate datePaiement;

    @Size(max = 80)
    @Column(name = "mode_paiement", length = 80)
    private String modePaiement;

    @Size(max = 255)
    @Column(name = "commentaire", length = 255)
    private String commentaire;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "boutique", "locataire" }, allowSetters = true)
    private CalculRedevance calcul;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PaiementRedevance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public PaiementRedevance reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getMontant() {
        return this.montant;
    }

    public PaiementRedevance montant(BigDecimal montant) {
        this.setMontant(montant);
        return this;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDatePaiement() {
        return this.datePaiement;
    }

    public PaiementRedevance datePaiement(LocalDate datePaiement) {
        this.setDatePaiement(datePaiement);
        return this;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getModePaiement() {
        return this.modePaiement;
    }

    public PaiementRedevance modePaiement(String modePaiement) {
        this.setModePaiement(modePaiement);
        return this;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getCommentaire() {
        return this.commentaire;
    }

    public PaiementRedevance commentaire(String commentaire) {
        this.setCommentaire(commentaire);
        return this;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public CalculRedevance getCalcul() {
        return this.calcul;
    }

    public void setCalcul(CalculRedevance calculRedevance) {
        this.calcul = calculRedevance;
    }

    public PaiementRedevance calcul(CalculRedevance calculRedevance) {
        this.setCalcul(calculRedevance);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaiementRedevance)) {
            return false;
        }
        return getId() != null && getId().equals(((PaiementRedevance) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaiementRedevance{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", montant=" + getMontant() +
            ", datePaiement='" + getDatePaiement() + "'" +
            ", modePaiement='" + getModePaiement() + "'" +
            ", commentaire='" + getCommentaire() + "'" +
            "}";
    }
}
