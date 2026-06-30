package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.TypeOperationCorrective;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.OperationCorrectiveVente} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OperationCorrectiveVenteDTO implements Serializable {

    private Long id;

    @NotNull
    private TypeOperationCorrective typeOperation;

    @NotNull
    @Size(max = 255)
    private String motif;

    @DecimalMin(value = "0")
    private BigDecimal montantImpact;

    @NotNull
    private Instant dateOperation;

    @NotNull
    private VenteDTO vente;

    @NotNull
    private UserDTO utilisateur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeOperationCorrective getTypeOperation() {
        return typeOperation;
    }

    public void setTypeOperation(TypeOperationCorrective typeOperation) {
        this.typeOperation = typeOperation;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public BigDecimal getMontantImpact() {
        return montantImpact;
    }

    public void setMontantImpact(BigDecimal montantImpact) {
        this.montantImpact = montantImpact;
    }

    public Instant getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(Instant dateOperation) {
        this.dateOperation = dateOperation;
    }

    public VenteDTO getVente() {
        return vente;
    }

    public void setVente(VenteDTO vente) {
        this.vente = vente;
    }

    public UserDTO getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(UserDTO utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OperationCorrectiveVenteDTO)) {
            return false;
        }

        OperationCorrectiveVenteDTO operationCorrectiveVenteDTO = (OperationCorrectiveVenteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, operationCorrectiveVenteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OperationCorrectiveVenteDTO{" +
            "id=" + getId() +
            ", typeOperation='" + getTypeOperation() + "'" +
            ", motif='" + getMotif() + "'" +
            ", montantImpact=" + getMontantImpact() +
            ", dateOperation='" + getDateOperation() + "'" +
            ", vente=" + getVente() +
            ", utilisateur=" + getUtilisateur() +
            "}";
    }
}
