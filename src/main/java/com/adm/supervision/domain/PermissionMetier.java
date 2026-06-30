package com.adm.supervision.domain;

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
 * A PermissionMetier.
 */
@Entity
@Table(name = "permission_metier")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PermissionMetier implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "code", length = 80, nullable = false, unique = true)
    private String code;

    @NotNull
    @Size(max = 150)
    @Column(name = "libelle", length = 150, nullable = false)
    private String libelle;

    @NotNull
    @Size(max = 80)
    @Column(name = "module", length = 80, nullable = false)
    private String module;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissionses")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "permissionses" }, allowSetters = true)
    private Set<ProfilMetier> profilses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PermissionMetier id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public PermissionMetier code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public PermissionMetier libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getModule() {
        return this.module;
    }

    public PermissionMetier module(String module) {
        this.setModule(module);
        return this;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDescription() {
        return this.description;
    }

    public PermissionMetier description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ProfilMetier> getProfilses() {
        return this.profilses;
    }

    public void setProfilses(Set<ProfilMetier> profilMetiers) {
        if (this.profilses != null) {
            this.profilses.forEach(i -> i.removePermissions(this));
        }
        if (profilMetiers != null) {
            profilMetiers.forEach(i -> i.addPermissions(this));
        }
        this.profilses = profilMetiers;
    }

    public PermissionMetier profilses(Set<ProfilMetier> profilMetiers) {
        this.setProfilses(profilMetiers);
        return this;
    }

    public PermissionMetier addProfils(ProfilMetier profilMetier) {
        this.profilses.add(profilMetier);
        profilMetier.getPermissionses().add(this);
        return this;
    }

    public PermissionMetier removeProfils(ProfilMetier profilMetier) {
        this.profilses.remove(profilMetier);
        profilMetier.getPermissionses().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermissionMetier)) {
            return false;
        }
        return getId() != null && getId().equals(((PermissionMetier) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PermissionMetier{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", libelle='" + getLibelle() + "'" +
            ", module='" + getModule() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
