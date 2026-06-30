package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.adm.supervision.domain.enumeration.TypeBoutique;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Boutique.
 */
@Entity
@Table(name = "boutique")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Boutique implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 30)
    @Column(name = "code", length = 30, nullable = false, unique = true)
    private String code;

    @NotNull
    @Size(max = 150)
    @Column(name = "nom", length = 150, nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TypeBoutique type;

    @Size(max = 255)
    @Column(name = "emplacement", length = 255)
    private String emplacement;

    @Size(max = 30)
    @Column(name = "telephone", length = 30)
    private String telephone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutGeneral statut;

    @NotNull
    @Column(name = "date_creation", nullable = false)
    private Instant dateCreation;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Boutique id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Boutique code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return this.nom;
    }

    public Boutique nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public TypeBoutique getType() {
        return this.type;
    }

    public Boutique type(TypeBoutique type) {
        this.setType(type);
        return this;
    }

    public void setType(TypeBoutique type) {
        this.type = type;
    }

    public String getEmplacement() {
        return this.emplacement;
    }

    public Boutique emplacement(String emplacement) {
        this.setEmplacement(emplacement);
        return this;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public Boutique telephone(String telephone) {
        this.setTelephone(telephone);
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public StatutGeneral getStatut() {
        return this.statut;
    }

    public Boutique statut(StatutGeneral statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutGeneral statut) {
        this.statut = statut;
    }

    public Instant getDateCreation() {
        return this.dateCreation;
    }

    public Boutique dateCreation(Instant dateCreation) {
        this.setDateCreation(dateCreation);
        return this;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Boutique)) {
            return false;
        }
        return getId() != null && getId().equals(((Boutique) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Boutique{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", nom='" + getNom() + "'" +
            ", type='" + getType() + "'" +
            ", emplacement='" + getEmplacement() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", statut='" + getStatut() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            "}";
    }
}
