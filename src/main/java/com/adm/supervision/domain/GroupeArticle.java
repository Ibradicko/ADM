package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.StatutGeneral;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A GroupeArticle.
 */
@Entity
@Table(name = "groupe_article")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class GroupeArticle implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 30)
    @Column(name = "code", length = 30, nullable = false)
    private String code;

    @NotNull
    @Size(max = 150)
    @Column(name = "libelle", length = 150, nullable = false)
    private String libelle;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutGeneral statut;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "taux_redevance", precision = 21, scale = 2, nullable = false)
    private BigDecimal tauxRedevance;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public GroupeArticle id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public GroupeArticle code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public GroupeArticle libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public StatutGeneral getStatut() {
        return this.statut;
    }

    public GroupeArticle statut(StatutGeneral statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutGeneral statut) {
        this.statut = statut;
    }

    public BigDecimal getTauxRedevance() {
        return this.tauxRedevance;
    }

    public GroupeArticle tauxRedevance(BigDecimal tauxRedevance) {
        this.setTauxRedevance(tauxRedevance);
        return this;
    }

    public void setTauxRedevance(BigDecimal tauxRedevance) {
        this.tauxRedevance = tauxRedevance;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupeArticle)) {
            return false;
        }
        return getId() != null && getId().equals(((GroupeArticle) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GroupeArticle{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", libelle='" + getLibelle() + "'" +
            ", statut='" + getStatut() + "'" +
            ", tauxRedevance=" + getTauxRedevance() +
            '}';
    }
}
