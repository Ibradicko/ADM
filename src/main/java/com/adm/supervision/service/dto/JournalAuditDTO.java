package com.adm.supervision.service.dto;

import com.adm.supervision.domain.enumeration.TypeActionAudit;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.adm.supervision.domain.JournalAudit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class JournalAuditDTO implements Serializable {

    private Long id;

    @NotNull
    private TypeActionAudit typeAction;

    @Size(max = 100)
    private String entiteConcernee;

    @Size(max = 100)
    private String identifiantEntite;

    @Lob
    private String description;

    @Size(max = 80)
    private String adresseIp;

    @NotNull
    private Instant dateAction;

    private BoutiqueDTO boutique;

    private UserDTO utilisateur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeActionAudit getTypeAction() {
        return typeAction;
    }

    public void setTypeAction(TypeActionAudit typeAction) {
        this.typeAction = typeAction;
    }

    public String getEntiteConcernee() {
        return entiteConcernee;
    }

    public void setEntiteConcernee(String entiteConcernee) {
        this.entiteConcernee = entiteConcernee;
    }

    public String getIdentifiantEntite() {
        return identifiantEntite;
    }

    public void setIdentifiantEntite(String identifiantEntite) {
        this.identifiantEntite = identifiantEntite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdresseIp() {
        return adresseIp;
    }

    public void setAdresseIp(String adresseIp) {
        this.adresseIp = adresseIp;
    }

    public Instant getDateAction() {
        return dateAction;
    }

    public void setDateAction(Instant dateAction) {
        this.dateAction = dateAction;
    }

    public BoutiqueDTO getBoutique() {
        return boutique;
    }

    public void setBoutique(BoutiqueDTO boutique) {
        this.boutique = boutique;
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
        if (!(o instanceof JournalAuditDTO)) {
            return false;
        }

        JournalAuditDTO journalAuditDTO = (JournalAuditDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, journalAuditDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "JournalAuditDTO{" +
            "id=" + getId() +
            ", typeAction='" + getTypeAction() + "'" +
            ", entiteConcernee='" + getEntiteConcernee() + "'" +
            ", identifiantEntite='" + getIdentifiantEntite() + "'" +
            ", description='" + getDescription() + "'" +
            ", adresseIp='" + getAdresseIp() + "'" +
            ", dateAction='" + getDateAction() + "'" +
            ", boutique=" + getBoutique() +
            ", utilisateur=" + getUtilisateur() +
            "}";
    }
}
