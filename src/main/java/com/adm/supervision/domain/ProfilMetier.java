package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.StatutGeneral;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A ProfilMetier.
 */
@Entity
@Table(name = "profil_metier")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProfilMetier implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @NotNull
    @Size(max = 150)
    @Column(name = "libelle", length = 150, nullable = false)
    private String libelle;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutGeneral statut;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_profil_metier__permissions",
        joinColumns = @JoinColumn(name = "profil_metier_id"),
        inverseJoinColumns = @JoinColumn(name = "permissions_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "profilses" }, allowSetters = true)
    private Set<PermissionMetier> permissionses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProfilMetier id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public ProfilMetier code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public ProfilMetier libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return this.description;
    }

    public ProfilMetier description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatutGeneral getStatut() {
        return this.statut;
    }

    public ProfilMetier statut(StatutGeneral statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutGeneral statut) {
        this.statut = statut;
    }

    public Set<PermissionMetier> getPermissionses() {
        return this.permissionses;
    }

    public void setPermissionses(Set<PermissionMetier> permissionMetiers) {
        this.permissionses = permissionMetiers;
    }

    public ProfilMetier permissionses(Set<PermissionMetier> permissionMetiers) {
        this.setPermissionses(permissionMetiers);
        return this;
    }

    public ProfilMetier addPermissions(PermissionMetier permissionMetier) {
        this.permissionses.add(permissionMetier);
        return this;
    }

    public ProfilMetier removePermissions(PermissionMetier permissionMetier) {
        this.permissionses.remove(permissionMetier);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfilMetier)) {
            return false;
        }
        return getId() != null && getId().equals(((ProfilMetier) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfilMetier{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", libelle='" + getLibelle() + "'" +
            ", description='" + getDescription() + "'" +
            ", statut='" + getStatut() + "'" +
            "}";
    }
}
