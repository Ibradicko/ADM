package com.adm.supervision.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A LigneCalculRedevance.
 */
@Entity
@Table(name = "ligne_calcul_redevance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LigneCalculRedevance implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "base_calcul", precision = 21, scale = 2, nullable = false)
    private BigDecimal baseCalcul;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "taux_applique", precision = 21, scale = 2, nullable = false)
    private BigDecimal tauxApplique;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "montant_redevance", precision = 21, scale = 2, nullable = false)
    private BigDecimal montantRedevance;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "boutique", "locataire" }, allowSetters = true)
    private CalculRedevance calcul;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "boutique", "locataire", "vendeur" }, allowSetters = true)
    private Vente vente;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public LigneCalculRedevance id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBaseCalcul() {
        return this.baseCalcul;
    }

    public LigneCalculRedevance baseCalcul(BigDecimal baseCalcul) {
        this.setBaseCalcul(baseCalcul);
        return this;
    }

    public void setBaseCalcul(BigDecimal baseCalcul) {
        this.baseCalcul = baseCalcul;
    }

    public BigDecimal getTauxApplique() {
        return this.tauxApplique;
    }

    public LigneCalculRedevance tauxApplique(BigDecimal tauxApplique) {
        this.setTauxApplique(tauxApplique);
        return this;
    }

    public void setTauxApplique(BigDecimal tauxApplique) {
        this.tauxApplique = tauxApplique;
    }

    public BigDecimal getMontantRedevance() {
        return this.montantRedevance;
    }

    public LigneCalculRedevance montantRedevance(BigDecimal montantRedevance) {
        this.setMontantRedevance(montantRedevance);
        return this;
    }

    public void setMontantRedevance(BigDecimal montantRedevance) {
        this.montantRedevance = montantRedevance;
    }

    public CalculRedevance getCalcul() {
        return this.calcul;
    }

    public void setCalcul(CalculRedevance calculRedevance) {
        this.calcul = calculRedevance;
    }

    public LigneCalculRedevance calcul(CalculRedevance calculRedevance) {
        this.setCalcul(calculRedevance);
        return this;
    }

    public Vente getVente() {
        return this.vente;
    }

    public void setVente(Vente vente) {
        this.vente = vente;
    }

    public LigneCalculRedevance vente(Vente vente) {
        this.setVente(vente);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LigneCalculRedevance)) {
            return false;
        }
        return getId() != null && getId().equals(((LigneCalculRedevance) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "LigneCalculRedevance{" +
            "id=" + getId() +
            ", baseCalcul=" + getBaseCalcul() +
            ", tauxApplique=" + getTauxApplique() +
            ", montantRedevance=" + getMontantRedevance() +
            "}";
    }
}
