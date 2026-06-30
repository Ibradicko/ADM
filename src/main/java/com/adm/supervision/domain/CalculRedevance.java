package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.StatutRedevance;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CalculRedevance.
 */
@Entity
@Table(name = "calcul_redevance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CalculRedevance implements Serializable {

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
    @Column(name = "periode_debut", nullable = false)
    private LocalDate periodeDebut;

    @NotNull
    @Column(name = "periode_fin", nullable = false)
    private LocalDate periodeFin;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "chiffre_affaires", precision = 21, scale = 2, nullable = false)
    private BigDecimal chiffreAffaires;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "montant_redevance", precision = 21, scale = 2, nullable = false)
    private BigDecimal montantRedevance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutRedevance statut;

    @NotNull
    @Column(name = "date_calcul", nullable = false)
    private Instant dateCalcul;

    @ManyToOne(optional = false)
    @NotNull
    private Boutique boutique;

    @ManyToOne(optional = false)
    @NotNull
    private Locataire locataire;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CalculRedevance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public CalculRedevance reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getPeriodeDebut() {
        return this.periodeDebut;
    }

    public CalculRedevance periodeDebut(LocalDate periodeDebut) {
        this.setPeriodeDebut(periodeDebut);
        return this;
    }

    public void setPeriodeDebut(LocalDate periodeDebut) {
        this.periodeDebut = periodeDebut;
    }

    public LocalDate getPeriodeFin() {
        return this.periodeFin;
    }

    public CalculRedevance periodeFin(LocalDate periodeFin) {
        this.setPeriodeFin(periodeFin);
        return this;
    }

    public void setPeriodeFin(LocalDate periodeFin) {
        this.periodeFin = periodeFin;
    }

    public BigDecimal getChiffreAffaires() {
        return this.chiffreAffaires;
    }

    public CalculRedevance chiffreAffaires(BigDecimal chiffreAffaires) {
        this.setChiffreAffaires(chiffreAffaires);
        return this;
    }

    public void setChiffreAffaires(BigDecimal chiffreAffaires) {
        this.chiffreAffaires = chiffreAffaires;
    }

    public BigDecimal getMontantRedevance() {
        return this.montantRedevance;
    }

    public CalculRedevance montantRedevance(BigDecimal montantRedevance) {
        this.setMontantRedevance(montantRedevance);
        return this;
    }

    public void setMontantRedevance(BigDecimal montantRedevance) {
        this.montantRedevance = montantRedevance;
    }

    public StatutRedevance getStatut() {
        return this.statut;
    }

    public CalculRedevance statut(StatutRedevance statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutRedevance statut) {
        this.statut = statut;
    }

    public Instant getDateCalcul() {
        return this.dateCalcul;
    }

    public CalculRedevance dateCalcul(Instant dateCalcul) {
        this.setDateCalcul(dateCalcul);
        return this;
    }

    public void setDateCalcul(Instant dateCalcul) {
        this.dateCalcul = dateCalcul;
    }

    public Boutique getBoutique() {
        return this.boutique;
    }

    public void setBoutique(Boutique boutique) {
        this.boutique = boutique;
    }

    public CalculRedevance boutique(Boutique boutique) {
        this.setBoutique(boutique);
        return this;
    }

    public Locataire getLocataire() {
        return this.locataire;
    }

    public void setLocataire(Locataire locataire) {
        this.locataire = locataire;
    }

    public CalculRedevance locataire(Locataire locataire) {
        this.setLocataire(locataire);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CalculRedevance)) {
            return false;
        }
        return getId() != null && getId().equals(((CalculRedevance) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CalculRedevance{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", periodeDebut='" + getPeriodeDebut() + "'" +
            ", periodeFin='" + getPeriodeFin() + "'" +
            ", chiffreAffaires=" + getChiffreAffaires() +
            ", montantRedevance=" + getMontantRedevance() +
            ", statut='" + getStatut() + "'" +
            ", dateCalcul='" + getDateCalcul() + "'" +
            "}";
    }
}
