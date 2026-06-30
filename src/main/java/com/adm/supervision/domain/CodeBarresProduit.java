package com.adm.supervision.domain;

import com.adm.supervision.domain.enumeration.TypeCodeBarres;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CodeBarresProduit.
 */
@Entity
@Table(name = "code_barres_produit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CodeBarresProduit implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "code", length = 80, nullable = false)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeCodeBarres type;

    @NotNull
    @Column(name = "principal", nullable = false)
    private Boolean principal;

    @NotNull
    @Column(name = "genere_par_systeme", nullable = false)
    private Boolean genereParSysteme;

    @NotNull
    @Column(name = "actif", nullable = false)
    private Boolean actif;

    @NotNull
    @Column(name = "date_affectation", nullable = false)
    private Instant dateAffectation;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "boutique", "groupeArticle", "familleArticle", "sousFamilleArticle", "uniteMesure" },
        allowSetters = true
    )
    private Produit produit;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CodeBarresProduit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public CodeBarresProduit code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public TypeCodeBarres getType() {
        return this.type;
    }

    public CodeBarresProduit type(TypeCodeBarres type) {
        this.setType(type);
        return this;
    }

    public void setType(TypeCodeBarres type) {
        this.type = type;
    }

    public Boolean getPrincipal() {
        return this.principal;
    }

    public CodeBarresProduit principal(Boolean principal) {
        this.setPrincipal(principal);
        return this;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public Boolean getGenereParSysteme() {
        return this.genereParSysteme;
    }

    public CodeBarresProduit genereParSysteme(Boolean genereParSysteme) {
        this.setGenereParSysteme(genereParSysteme);
        return this;
    }

    public void setGenereParSysteme(Boolean genereParSysteme) {
        this.genereParSysteme = genereParSysteme;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public CodeBarresProduit actif(Boolean actif) {
        this.setActif(actif);
        return this;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Instant getDateAffectation() {
        return this.dateAffectation;
    }

    public CodeBarresProduit dateAffectation(Instant dateAffectation) {
        this.setDateAffectation(dateAffectation);
        return this;
    }

    public void setDateAffectation(Instant dateAffectation) {
        this.dateAffectation = dateAffectation;
    }

    public Produit getProduit() {
        return this.produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public CodeBarresProduit produit(Produit produit) {
        this.setProduit(produit);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CodeBarresProduit)) {
            return false;
        }
        return getId() != null && getId().equals(((CodeBarresProduit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CodeBarresProduit{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", type='" + getType() + "'" +
            ", principal='" + getPrincipal() + "'" +
            ", genereParSysteme='" + getGenereParSysteme() + "'" +
            ", actif='" + getActif() + "'" +
            ", dateAffectation='" + getDateAffectation() + "'" +
            "}";
    }
}
