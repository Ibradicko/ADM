package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.StatutPaiement;
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
 * A PaiementVente.
 */
@Entity
@Table(name = "paiement_vente")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PaiementVente implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "montant", precision = 21, scale = 2, nullable = false)
    private BigDecimal montant;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutPaiement statut;

    @Size(max = 100)
    @Column(name = "reference_paiement", length = 100)
    private String referencePaiement;

    @NotNull
    @Column(name = "date_paiement", nullable = false)
    private Instant datePaiement;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "boutique", "locataire", "vendeur" }, allowSetters = true)
    private Vente vente;

    @ManyToOne(optional = false)
    @NotNull
    private ModePaiementRef modePaiement;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PaiementVente id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return this.montant;
    }

    public PaiementVente montant(BigDecimal montant) {
        this.setMontant(montant);
        return this;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public StatutPaiement getStatut() {
        return this.statut;
    }

    public PaiementVente statut(StatutPaiement statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutPaiement statut) {
        this.statut = statut;
    }

    public String getReferencePaiement() {
        return this.referencePaiement;
    }

    public PaiementVente referencePaiement(String referencePaiement) {
        this.setReferencePaiement(referencePaiement);
        return this;
    }

    public void setReferencePaiement(String referencePaiement) {
        this.referencePaiement = referencePaiement;
    }

    public Instant getDatePaiement() {
        return this.datePaiement;
    }

    public PaiementVente datePaiement(Instant datePaiement) {
        this.setDatePaiement(datePaiement);
        return this;
    }

    public void setDatePaiement(Instant datePaiement) {
        this.datePaiement = datePaiement;
    }

    public Vente getVente() {
        return this.vente;
    }

    public void setVente(Vente vente) {
        this.vente = vente;
    }

    public PaiementVente vente(Vente vente) {
        this.setVente(vente);
        return this;
    }

    public ModePaiementRef getModePaiement() {
        return this.modePaiement;
    }

    public void setModePaiement(ModePaiementRef modePaiementRef) {
        this.modePaiement = modePaiementRef;
    }

    public PaiementVente modePaiement(ModePaiementRef modePaiementRef) {
        this.setModePaiement(modePaiementRef);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaiementVente)) {
            return false;
        }
        return getId() != null && getId().equals(((PaiementVente) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaiementVente{" +
            "id=" + getId() +
            ", montant=" + getMontant() +
            ", statut='" + getStatut() + "'" +
            ", referencePaiement='" + getReferencePaiement() + "'" +
            ", datePaiement='" + getDatePaiement() + "'" +
            "}";
    }
}
