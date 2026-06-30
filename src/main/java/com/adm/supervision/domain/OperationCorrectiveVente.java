package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.TypeOperationCorrective;
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
 * A OperationCorrectiveVente.
 */
@Entity
@Table(name = "operation_corrective_vente")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OperationCorrectiveVente implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_operation", nullable = false)
    private TypeOperationCorrective typeOperation;

    @NotNull
    @Size(max = 255)
    @Column(name = "motif", length = 255, nullable = false)
    private String motif;

    @DecimalMin(value = "0")
    @Column(name = "montant_impact", precision = 21, scale = 2)
    private BigDecimal montantImpact;

    @NotNull
    @Column(name = "date_operation", nullable = false)
    private Instant dateOperation;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "boutique", "locataire", "vendeur" }, allowSetters = true)
    private Vente vente;

    @ManyToOne(optional = false)
    @NotNull
    private User utilisateur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OperationCorrectiveVente id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeOperationCorrective getTypeOperation() {
        return this.typeOperation;
    }

    public OperationCorrectiveVente typeOperation(TypeOperationCorrective typeOperation) {
        this.setTypeOperation(typeOperation);
        return this;
    }

    public void setTypeOperation(TypeOperationCorrective typeOperation) {
        this.typeOperation = typeOperation;
    }

    public String getMotif() {
        return this.motif;
    }

    public OperationCorrectiveVente motif(String motif) {
        this.setMotif(motif);
        return this;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public BigDecimal getMontantImpact() {
        return this.montantImpact;
    }

    public OperationCorrectiveVente montantImpact(BigDecimal montantImpact) {
        this.setMontantImpact(montantImpact);
        return this;
    }

    public void setMontantImpact(BigDecimal montantImpact) {
        this.montantImpact = montantImpact;
    }

    public Instant getDateOperation() {
        return this.dateOperation;
    }

    public OperationCorrectiveVente dateOperation(Instant dateOperation) {
        this.setDateOperation(dateOperation);
        return this;
    }

    public void setDateOperation(Instant dateOperation) {
        this.dateOperation = dateOperation;
    }

    public Vente getVente() {
        return this.vente;
    }

    public void setVente(Vente vente) {
        this.vente = vente;
    }

    public OperationCorrectiveVente vente(Vente vente) {
        this.setVente(vente);
        return this;
    }

    public User getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(User user) {
        this.utilisateur = user;
    }

    public OperationCorrectiveVente utilisateur(User user) {
        this.setUtilisateur(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OperationCorrectiveVente)) {
            return false;
        }
        return getId() != null && getId().equals(((OperationCorrectiveVente) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OperationCorrectiveVente{" +
            "id=" + getId() +
            ", typeOperation='" + getTypeOperation() + "'" +
            ", motif='" + getMotif() + "'" +
            ", montantImpact=" + getMontantImpact() +
            ", dateOperation='" + getDateOperation() + "'" +
            "}";
    }
}
