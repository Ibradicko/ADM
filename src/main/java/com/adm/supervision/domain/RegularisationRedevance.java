package com.adm.supervision.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RegularisationRedevance.
 */
@Entity
@Table(name = "regularisation_redevance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RegularisationRedevance implements Serializable {

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
    @Column(name = "montant", precision = 21, scale = 2, nullable = false)
    private BigDecimal montant;

    @NotNull
    @Size(max = 255)
    @Column(name = "motif", length = 255, nullable = false)
    private String motif;

    @NotNull
    @Column(name = "date_regularisation", nullable = false)
    private Instant dateRegularisation;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "boutique", "locataire" }, allowSetters = true)
    private CalculRedevance calcul;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RegularisationRedevance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public RegularisationRedevance reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getMontant() {
        return this.montant;
    }

    public RegularisationRedevance montant(BigDecimal montant) {
        this.setMontant(montant);
        return this;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getMotif() {
        return this.motif;
    }

    public RegularisationRedevance motif(String motif) {
        this.setMotif(motif);
        return this;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Instant getDateRegularisation() {
        return this.dateRegularisation;
    }

    public RegularisationRedevance dateRegularisation(Instant dateRegularisation) {
        this.setDateRegularisation(dateRegularisation);
        return this;
    }

    public void setDateRegularisation(Instant dateRegularisation) {
        this.dateRegularisation = dateRegularisation;
    }

    public CalculRedevance getCalcul() {
        return this.calcul;
    }

    public void setCalcul(CalculRedevance calculRedevance) {
        this.calcul = calculRedevance;
    }

    public RegularisationRedevance calcul(CalculRedevance calculRedevance) {
        this.setCalcul(calculRedevance);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegularisationRedevance)) {
            return false;
        }
        return getId() != null && getId().equals(((RegularisationRedevance) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RegularisationRedevance{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", montant=" + getMontant() +
            ", motif='" + getMotif() + "'" +
            ", dateRegularisation='" + getDateRegularisation() + "'" +
            "}";
    }
}
