package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.TypeCodeBarres;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ParametreCodeBarres.
 */
@Entity
@Table(name = "parametre_code_barres")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ParametreCodeBarres implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "format_par_defaut", nullable = false)
    private TypeCodeBarres formatParDefaut;

    @Size(max = 20)
    @Column(name = "prefixe", length = 20)
    private String prefixe;

    @Min(value = 8)
    @Max(value = 30)
    @Column(name = "longueur")
    private Integer longueur;

    @NotNull
    @Column(name = "actif", nullable = false)
    private Boolean actif;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ParametreCodeBarres id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TypeCodeBarres getFormatParDefaut() {
        return this.formatParDefaut;
    }

    public ParametreCodeBarres formatParDefaut(TypeCodeBarres formatParDefaut) {
        this.setFormatParDefaut(formatParDefaut);
        return this;
    }

    public void setFormatParDefaut(TypeCodeBarres formatParDefaut) {
        this.formatParDefaut = formatParDefaut;
    }

    public String getPrefixe() {
        return this.prefixe;
    }

    public ParametreCodeBarres prefixe(String prefixe) {
        this.setPrefixe(prefixe);
        return this;
    }

    public void setPrefixe(String prefixe) {
        this.prefixe = prefixe;
    }

    public Integer getLongueur() {
        return this.longueur;
    }

    public ParametreCodeBarres longueur(Integer longueur) {
        this.setLongueur(longueur);
        return this;
    }

    public void setLongueur(Integer longueur) {
        this.longueur = longueur;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public ParametreCodeBarres actif(Boolean actif) {
        this.setActif(actif);
        return this;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParametreCodeBarres)) {
            return false;
        }
        return getId() != null && getId().equals(((ParametreCodeBarres) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParametreCodeBarres{" +
            "id=" + getId() +
            ", formatParDefaut='" + getFormatParDefaut() + "'" +
            ", prefixe='" + getPrefixe() + "'" +
            ", longueur=" + getLongueur() +
            ", actif='" + getActif() + "'" +
            "}";
    }
}
